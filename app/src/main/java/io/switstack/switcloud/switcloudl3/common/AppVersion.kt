package io.switstack.switcloud.switcloudl3.common

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import timber.log.Timber

data class AppVersion(
    val versionCode: Long,
    val versionName: String
) {
    companion object {
        fun fromContext(context: Context): AppVersion? {
            try {
                val packageManager = context.packageManager
                val packageName = context.packageName

                val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(packageName, 0)
                }

                val versionCode: Long = packageInfo.longVersionCode
                val versionName: String = packageInfo.versionName ?: "Unknown"

                return AppVersion(versionCode, versionName)
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.e(e, "AppVersionInfo: Error getting app version info")
                return null
            }
        }
    }
}