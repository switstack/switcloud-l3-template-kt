package io.switstack.switcloud.switcloudl3.data

sealed class UserInfo {
    data class LedLevel(val level: Int) : UserInfo()
    data class UserMessage(val status: String?, val message: String, val ops: String? = null) : UserInfo()
}
