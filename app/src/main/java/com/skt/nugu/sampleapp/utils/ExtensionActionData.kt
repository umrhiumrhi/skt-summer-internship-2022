package com.skt.eaa.assistant.capability.directive.extension

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 *
 *
 * @author siwon.jeong
 *
 * @since 11/06/2020 created by siwon.jeong
 */

@Keep data class ExtensionActionData(
    @SerializedName("action")
    val action: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("text")
    val text: String = "",
    @SerializedName("duration")
    val duration: Int = 0,
    @SerializedName("appControl")
    val appControl: AppControl? = null,
    @SerializedName("foregroundCandidate")
    val foregroundCandidate: ForegroundCandidate? = null
)

@Keep data class AppControl(
    @SerializedName("appName")
    val appName: String,
    @SerializedName("action")
    val action: String,
    @SerializedName("target")
    val target: String = ""
)

@Keep data class ForegroundCandidate(
    @SerializedName("playServiceId")
    val playServiceId: String = ""
)

