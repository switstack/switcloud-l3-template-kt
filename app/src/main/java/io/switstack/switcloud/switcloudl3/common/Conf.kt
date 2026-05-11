package io.switstack.switcloud.switcloudl3.common

import io.switstack.switcloud.switcloudl3.BuildConfig
import java.util.UUID

/**
 * In order to use the configure your app
 * Switstack provides your custom values to replace in you local properties.
 * The local.properties file located in you root project folder
 * have to be filled with the following fields :
 *
 * SWITSTACK_CLIENT_ATTESTATION_SECRET="TO_REPLACE"
 *
 * LOCAL_SWITCLOUD_URL="TO_REPLACE"
 * LOCAL_SWITCLOUD_CLIENT_ID="TO_REPLACE"
 * LOCAL_SWITCLOUD_CLIENT_SECRET="TO_REPLACE"
 * LOCAL_POI_ID="TO_REPLACE"
 * LOCAL_POI_CONFIG_ID="TO_REPLACE"
 * */
object Conf {
    const val SWITCLOUD_URL = BuildConfig.SWITCLOUD_URL
    const val SWITCLOUD_CLIENT_ID = BuildConfig.SWITCLOUD_CLIENT_ID
    const val SWITCLOUD_CLIENT_SECRET = BuildConfig.SWITCLOUD_CLIENT_SECRET

    val POI_ID: UUID = UUID.fromString(BuildConfig.POI_ID)
    val POI_CONFIG_ID: UUID = UUID.fromString(BuildConfig.POI_CONFIG_ID)
    const val TRD = "9F02060000000010009F03060000000000009A032006029C01005F2A020840"
}