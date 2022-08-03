package com.skt.eaa.assistant.capability.client

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.skt.eaa.assistant.capability.directive.extension.ExtensionActionData
import com.skt.eaa.assistant.utils.JsonHelper
import com.skt.eaa.debug.capability.ExternalAppControl
import com.skt.nugu.sdk.agent.extension.ExtensionAgentInterface

/**
 * Extension interface의 Client 구현부
 *
 * Intent <-> URI 변환
 *  - intent to uri: Intent(Settings.ACTION_SOUND_SETTINGS).toUri(Intent.URI_INTENT_SCHEME)
 *  - uri to intent: Intent.parseUri(uri, Intent.URI_INTENT_SCHEME)
 *
 * @since 11/06/2020 created by siwon.jeong
 */
class ExtensionClient(
    val context: Context
): ExtensionAgentInterface.Client {
    companion object {

    }
    private val TAG = "ExtensionClient"

    val FOREGROUND_CANDIDATE_DEFAULT_ACTION = "open"
    val FOREGROUND_CANDIDATE_DEFAULT_TARGET = "default"
    val ACTION_OPEN_INTENT = "Open.Intent"

    override fun action(data: String, playServiceId: String, dialogRequestId: String): Boolean {
        Log.d(TAG, "[ExtensionClient] action() : data = <$data>, playServiceId = <$playServiceId>, dialogRequestId = <$dialogRequestId>")

        runCatching {
            JsonHelper.fromJsonOrNull(data, ExtensionActionData::class.java)?.run {
                runActionOpen(this)
                return true
            }
        }.onFailure {
            Log.w(TAG, "[ExtensionClient] action() : $it")
        }
        return false
    }


    var externalAppControl = ExternalAppControl(context)

    override fun getData(): String? {
        var data = externalAppControl.getUpdatedContextIfNeed()
        //var data = null
        Log.d(TAG, "[ExtensionClient] getData() : $data")
        return data
    }

    fun runActionOpen(data : ExtensionActionData) {
        when (data.action) {
            ACTION_OPEN_INTENT -> openIntent(data.url)
        }
    }

    private fun openIntent(uri: String?) {
        Log.d(TAG, "openIntent() : uri = <$uri>")

        runCatching {
            Intent.parseUri(uri, Intent.URI_INTENT_SCHEME).let { intent ->
                Log.d(TAG, "openIntent() : intent($intent)")

                resolveActivity(context, intent)?.let {
                    Log.d(TAG, "openIntent() : resolveActivity / resolveInfo : $it")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    context.startActivity(intent)
                    return
                }
                Log.w(TAG, "openIntent() : no activity resolved")

                resolveService(context, intent)?.let {
                    Log.d(TAG, "openIntent() : resolveActivity / resolveService : $it")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    context.startService(intent)
                    return
                }
                Log.w(TAG, "openIntent() : no service resolved")

                context.sendBroadcast(intent)
            }
        }.onFailure {
            Log.w(TAG, "[ExtensionClient] openIntent() : $it")
        }
    }

    private fun showToast(actionData: ExtensionActionData) {
        Log.d(TAG, "showToast(): extensionData: $actionData")

        val text = actionData.text
        val duration = actionData.duration
        Log.d(TAG, "showToast(): text: $text, duration: $duration")
    }

    fun resolveActivity(
        context: Context,
        intent: Intent
    ) = context.packageManager.resolveActivity(intent, PackageManager.MATCH_ALL)

    fun resolveService(
        context: Context,
        intent: Intent
    ) = context.packageManager.resolveService(intent, PackageManager.MATCH_ALL)

}