package io.switstack.switcloud.switcloudl3.common

import com.payneteasy.tlv.BerTag
import com.payneteasy.tlv.BerTlvParser
import io.switstack.switcloud.switcloudclt.data.UserInterfaceRequestData
import io.switstack.switcloud.switcloudl3.data.UserInfo
import timber.log.Timber

object TlvUtils {

    private val tagUserInterfaceRequestData = BerTag(0xDF, 0x81, 0x16)
    private val tagLed = BerTag(0xDF, 0xA0, 0x1B)
    private val parser = BerTlvParser()

    fun parseUirdTlv(tlv: ByteArray): List<UserInfo> {
        val tlvs = parser.parse(tlv)
            ?: throw SwitcloudL3Exception("ill-formed TLV")

        val userInfoList = mutableListOf<UserInfo>()

        tlvs.list.forEach {
            when (it.tag) {
                tagLed -> {
                    val byteAsInt = it.bytesValue.single().toInt()
                    val isOn = byteAsInt and 0x80 == 0x80
                    val level = byteAsInt and 0x0F
                    Timber.d("TLV level $level / $isOn")
                    if (isOn) {
                        userInfoList.add(UserInfo.LedLevel(level))
                    }
                }

                tagUserInterfaceRequestData -> {
                    val message = UserInterfaceRequestData.MessageIdentifier.from(it.bytesValue[0])?.let { "${it.name} (${it.value.toHexString()})" } ?: ""
                    val status = UserInterfaceRequestData.Status.from(it.bytesValue[1])?.let { "${it.name} (${it.value.toHexString()})" } ?: ""
                    val userMessage = UserInfo.UserMessage(status, message)
                    Timber.d("TLV userMessage $userMessage")
                    userInfoList.add(userMessage)
                }
            }
        }
        return userInfoList
    }
}