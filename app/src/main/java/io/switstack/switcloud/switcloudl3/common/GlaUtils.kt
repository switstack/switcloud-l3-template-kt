package io.switstack.switcloud.switcloudl3.common

import io.switstack.switcloud.switcloudl2.IGla
import io.switstack.switcloud.switcloudl2.exception.SwitcloudL2NotFoundException
import timber.log.Timber

object GlaUtils {

    fun getTagGlaSafe(gla: IGla, tag: ByteArray, tagName: String): ByteArray? {
        @Suppress("SwallowedException")
        try {
            return gla.getTag(tag)?.also {
                Timber.d("$tagName > ${it.toHexString()}")
            }
        } catch (e: SwitcloudL2NotFoundException) {
            Timber.w("$tagName tag not found")
            return null
        }
    }
}