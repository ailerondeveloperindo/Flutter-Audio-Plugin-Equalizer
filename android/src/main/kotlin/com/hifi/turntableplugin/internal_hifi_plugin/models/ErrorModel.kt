package com.hifi.turntableplugin.internal_hifi_plugin.models

import kotlinx.serialization.Serializable

@Serializable
class ErrorModel (    val message: String? = null,
                      val type: Int? = null,
                      val errorCode: Int? = null,
                      val rendererIndex: Int? = null,
                      val cause: String? = null,
                      val isRecoverable: Boolean? = null,
                      val timestamp: Long = System.currentTimeMillis()){
}