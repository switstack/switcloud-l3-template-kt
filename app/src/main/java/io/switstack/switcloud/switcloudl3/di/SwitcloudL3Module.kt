package io.switstack.switcloud.switcloudl3.di

import io.switstack.switcloud.switcloudclt.di.switcloudClientModule
import io.switstack.switcloud.switcloudclt.domain.SwitcloudTestClient
import io.switstack.switcloud.switcloudclt.internal.SwitcloudTestClientImpl
import io.switstack.switcloud.switcloudl2.ISwitcloudL2
import io.switstack.switcloud.switcloudl2.SwitcloudL2
import io.switstack.switcloud.switcloudl3.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val switcloudL3Module = module {

    single<ISwitcloudL2> { SwitcloudL2.getInstance() }

    includes(switcloudClientModule)
    factory<SwitcloudTestClient> { (serverUrl: String) ->
        SwitcloudTestClientImpl(serverUrl)
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .also {
                if (BuildConfig.DEBUG) {
                    it.addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.HEADERS
                        }
                    )
                }
            }
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .build()
    }
}