package io.switstack.switcloud.switcloudl3.common

data class AndroidVersion(
    val sdkInt: Int,
    val release: String
) {
    companion object {
        fun fromSdkInt(sdkInt: Int): AndroidVersion {
            val release = when (sdkInt) {
                36 -> "16"
                35 -> "15"
                34 -> "14"
                33 -> "13"
                32 -> "12L" // Special case for large screen features
                31 -> "12"
                30 -> "11"
                29 -> "10"
                28 -> "9"
                else -> "API $sdkInt"
            }
            return AndroidVersion(sdkInt, release)
        }
    }
}