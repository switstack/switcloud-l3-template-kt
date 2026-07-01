package io.switstack.switcloud.switcloudl3api

import io.switstack.switcloud.switcloudl3api.api.AuthApi
import io.switstack.switcloud.switcloudl3api.api.PaymentApi
import io.switstack.switcloud.switcloudl3api.model.TokenSchema
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.openapitools.client.infrastructure.Serializer

class SwitcloudL3Api(baseUrl: String) {

    /**
     * User access token.
     */
    private var accessToken: String = ""
    private var clientAttestationSecret: String? = null

    /**
     * Request interceptor.
     *
     * Used to manipulate HTTP requests header and authorization.
     */
    private var requestInterceptor = RequestInterceptor(::getAccessToken, ::getClientAttestationSecret)

    private val loggerInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(requestInterceptor)
        .addInterceptor(loggerInterceptor)
        .build()


    /**
     * OAuth API wrapper.
     */
    var auth = AuthApi(baseUrl, okHttpClient)

    /**
     * Payment API wrapper.
     */
    var payment = PaymentApi(baseUrl, okHttpClient)

    init {
        try {
            Serializer.kotlinxSerializationJsonConfiguration = {
                explicitNulls = false
                encodeDefaults = false
            }
        } catch (e: IllegalStateException) {
            println("SwitcloudAPI: IllegalStateException")
            println(e)
        }
    }

    private fun getAccessToken() = accessToken
    private fun getClientAttestationSecret() = clientAttestationSecret

    fun updateToken(token: TokenSchema, secret: String?) {
        accessToken = token.accessToken
        clientAttestationSecret = secret
    }

    /**
     * Request interceptor class.
     *
     * Used to management authorization tokens.
     */
    private class RequestInterceptor(val getAccessToken: () -> String, val getClientAttestationSecret: () -> String?) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val accessToken= getAccessToken()
            val request: Request = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer $accessToken")
                .apply {
                    getClientAttestationSecret()?.let {
                        addHeader("x-switstack-client-attestation", it)
                    }
                }
                .method(original.method, original.body)
                .build()

            return chain.proceed(request)
        }
    }
}