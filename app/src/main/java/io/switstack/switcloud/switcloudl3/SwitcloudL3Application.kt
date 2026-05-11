package io.switstack.switcloud.switcloudl3

import android.app.Application
import io.switstack.switcloud.switcloudl3.common.TimberInfoTree
import io.switstack.switcloud.switcloudl3.di.switcloudL3Module
import org.koin.core.context.startKoin
import timber.log.Timber

class SwitcloudL3Application : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(switcloudL3Module)
        }

        Timber.plant(
            if (BuildConfig.DEBUG) {
                Timber.DebugTree()
            } else {
                TimberInfoTree()
            }
        )
    }
}