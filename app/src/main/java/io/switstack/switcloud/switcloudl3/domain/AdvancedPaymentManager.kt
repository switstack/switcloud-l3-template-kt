package io.switstack.switcloud.switcloudl3.domain

import com.payneteasy.tlv.HexUtil
import io.switstack.switcloud.switcloudclt.common.Codec
import io.switstack.switcloud.switcloudclt.common.Codec.isIssuerScriptPresent
import io.switstack.switcloud.switcloudclt.common.Codec.isTransactionAcceptedByIssuer
import io.switstack.switcloud.switcloudclt.common.Codec.makePaymentData
import io.switstack.switcloud.switcloudclt.common.DeDs.deDsPerformOperatorLogic
import io.switstack.switcloud.switcloudclt.common.SwitcloudClientException
import io.switstack.switcloud.switcloudclt.common.Utils
import io.switstack.switcloud.switcloudclt.common.Utils.removeDuplicateIntermediateSignalsPresentInSignals
import io.switstack.switcloud.switcloudclt.common.toByteArray
import io.switstack.switcloud.switcloudclt.data.InitiationData
import io.switstack.switcloud.switcloudclt.data.OutcomeParameterSet
import io.switstack.switcloud.switcloudclt.data.OutcomeParameterSet.CVM
import io.switstack.switcloud.switcloudclt.data.OutcomeParameterSet.Start
import io.switstack.switcloud.switcloudclt.data.OutcomeParameterSet.Status
import io.switstack.switcloud.switcloudclt.data.PaymentData
import io.switstack.switcloud.switcloudclt.data.SessionData
import io.switstack.switcloud.switcloudclt.data.TlvTag.TAG_DEK
import io.switstack.switcloud.switcloudclt.data.TlvTag.TAG_WRITE_DATA_STORAGE_TEMPLATE
import io.switstack.switcloud.switcloudclt.domain.InitiateResponse
import io.switstack.switcloud.switcloudclt.domain.PaymentStepData
import io.switstack.switcloud.switcloudclt.domain.SwitcloudTestClient
import io.switstack.switcloud.switcloudclt.internal.SwitcloudClientHelper.activateKernel
import io.switstack.switcloud.switcloudclt.internal.SwitcloudClientHelper.performSelection
import io.switstack.switcloud.switcloudl2.exception.SwitcloudL2Exception
import io.switstack.switcloud.switcloudl3.common.Conf.readerParams
import kotlinx.coroutines.channels.Channel
import timber.log.Timber
import java.util.UUID

class AdvancedPaymentManager(
    val client: SwitcloudTestClient,
    val pinInput: Channel<String?>
) {
    private lateinit var sessionData: SessionData

    fun initialize() = client.initialize(readerParams, pinInput)

    fun configure(paymentId: UUID, fallbackTrd: String?) {
        sessionData = client.configure(paymentId, fallbackTrd)
    }

    fun initiate(): InitiateResponse {
        var finalSignals = byteArrayOf()
        var step: PaymentStepData
        var pinBlock: ByteArray? = null

        client.run {

            preInitiate(sessionData)

            try {
                // Perform pre-processing
                step = gla.preProcessing(HexUtil.parseHex(sessionData.trd))

                if (step.second) {
                    var outcome = InitiationData()

                    var performSelection = true
                    while (performSelection) {
                        // Activate protocol
                        gla.protocolActivation(null)

                        // Select combination
                        step = gla.combinationSelection()
                        if (!step.second) {
                            outcome = Codec.makeInitiationData(step.first)

                            checkNotNull(outcome.outcomeParameterSet) {
                                "failed to perform combination selection: " +
                                        "failed to extract OPS"
                            }

                            if (outcome.outcomeParameterSet!!.status ==
                                Status.TRY_AGAIN &&
                                outcome.outcomeParameterSet!!.start ==
                                Start.B
                            ) {
                                // loop back to protocol activation
                                continue
                            } else {
                                // save signals
                                finalSignals += step.first

                                // leave
                                break
                            }
                        }

                        var activationData = byteArrayOf()

                        var startD = true
                        while (startD) {

                            // activate kernel (e.g. actual card processing)
                            step = step.copy(first = gla.kernelActivation(activationData))

                            // DEK / DET
                            Utils.tlvFromByteArray(step.first)?.find(TAG_DEK)
                                ?.toByteArray()
                                ?.takeIf { it.isNotEmpty() }
                                ?.let { dek ->
                                    val amount = Utils.getTransactionAmount(sessionData.trd.hexToByteArray())
                                    activationData = deDsPerformOperatorLogic(
                                        amount,
                                        dek,
                                        byteArrayOf()
                                    )

                                    // restart kernel activation if DEK present
                                    continue
                                }

                            // Parse outcome data
                            outcome = Codec.makeInitiationData(step.first!!)

                            checkNotNull(outcome.outcomeParameterSet) {
                                "failed to perform combination selection: " +
                                        "failed to extract OPS"
                            }

                            if (outcome.outcomeParameterSet?.cvm == CVM.ONLINE_PIN) {
                                pinBlock = requestPinEntry()
                            }

                            when (outcome.outcomeParameterSet!!.status) {
                                Status.APPROVED,
                                Status.DECLINED -> {
                                    startD = false
                                }

                                Status.ONLINE_REQUEST -> {
                                    activationData = Codec.performOnlineManagement(null)
                                }

                                else -> {}
                            }

                            when (outcome.outcomeParameterSet!!.start) {
                                Start.B -> {
                                    startD = false
                                }

                                Start.D -> {
                                }

                                else -> {
                                    startD = false
                                    performSelection = false

                                    // backup step.first in signals
                                    finalSignals = step.first!!
                                }
                            }
                        }
                    }
                } else {
                    // Pre-processing failed, get signals
                    finalSignals += step.first!!
                }
            } catch (e: IllegalStateException) {
                throw SwitcloudClientException(
                    "failed to initiate transaction: " +
                            e.message,
                    e
                )
            } catch (e: SwitcloudL2Exception) {
                throw SwitcloudClientException(
                    "failed to initiate transaction" +
                            e.message,
                    e
                )
            }

            val paymentData = makePaymentData(
                finalSignals,
                fetchIntermediateSignals(),
                pinBlock
            ) ?: throw SwitcloudClientException("failed to prepare payment data")

            return postInitiate(sessionData, paymentData)
        }
    }

    fun complete(initiateResponse: InitiateResponse, authorizationResponse: String): InitiateResponse {
        client.run {
            preComplete(sessionData, initiateResponse)

            initiateResponse.outcomeParameterSet ?: return initiateResponse

            // complete with a clean OPS
            val ops = OutcomeParameterSet()

            try {
                val lastStart = initiateResponse.outcomeParameterSet!!.start
                var performPostCompletion = false
                val authorizationResponseByteArray = authorizationResponse.hexToByteArray()

                if (lastStart != Start.D) {

                    ops.status = if (isTransactionAcceptedByIssuer(authorizationResponseByteArray)) {
                        Status.APPROVED
                    } else {
                        Status.DECLINED
                    }

                    // Second presentment (supported by moka only for Discover)
                    if (isIssuerScriptPresent(authorizationResponseByteArray) ||
                        Utils.isTagPresent(TAG_WRITE_DATA_STORAGE_TEMPLATE, authorizationResponseByteArray)
                    ) {
                        try {
                            performSelection(authorizationResponseByteArray)
                            performPostCompletion = true
                        } catch (e: SwitcloudClientException) {
                            Timber.w("complete : second presentment: ${e.message}")
                            // acceptable ?
                        }
                    }
                } else {
                    performPostCompletion = true
                }

                if (performPostCompletion) {
                    val result = activateKernel(authorizationResponseByteArray)

                    val data = PaymentData()
                    // Manage DEK (only once)
                    Utils.tlvFromByteArray(result)?.find(TAG_DEK)?.let { dekTlv ->
                        val bf11 = Utils.tlvFromByteArray(authorizationResponseByteArray)
                            ?.find(TAG_WRITE_DATA_STORAGE_TEMPLATE)
                            ?.toByteArray()

                        val activationData = deDsPerformOperatorLogic(
                            0,
                            dekTlv.toByteArray(),
                            bf11 ?: byteArrayOf()
                        )
                        data.signals = activateKernel(activationData)

                        fetchIntermediateSignals()?.let {
                            data.intermediateSignals += it
                        }

                        // Remove initiate step signals
                        data.intermediateSignals =
                            removeDuplicateIntermediateSignalsPresentInSignals(data.signals, data.intermediateSignals)
                    }

                    val pinBlock = Codec.makeInitiationData(result)
                        .outcomeParameterSet
                        ?.takeIf { it.cvm == CVM.ONLINE_PIN }.let {
                            requestPinEntry()
                        }

                    makePaymentData(
                        data.signals,
                        data.intermediateSignals,
                        pinBlock
                    )?.let { paymentData ->
                        return postComplete(sessionData, paymentData)
                    }
                }

                return postComplete(initiateResponse.apply { outcomeParameterSet = ops })
            } catch (e: SwitcloudL2Exception) {
                throw SwitcloudClientException(
                    "failed to complete transaction " +
                            e.message,
                    e
                )
            } catch (e: SwitcloudClientException) {
                throw SwitcloudClientException(
                    "failed to complete transaction " +
                            e.message,
                    e
                )
            }
        }
    }

    fun emitReceipt(finalResponse: InitiateResponse) = client.emitReceipt(finalResponse)
}