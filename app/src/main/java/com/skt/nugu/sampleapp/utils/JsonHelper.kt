package com.skt.eaa.assistant.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.internal.Streams
import com.skt.nugu.sdk.agent.AbstractDirectiveHandler.DirectiveInfo
import java.io.StringWriter

/**
 * @since 2020-05-11 created by hyunho.mo.
 */
object JsonHelper {
    private const val TAG = "JsonHelper"

    val gson: Gson by lazy { GsonBuilder().disableHtmlEscaping().create() }

    /**
     * Json string이나 object를 line-break와 indent가 적용된 pretty json 형태의 string 변환
     */
    fun toPrettyPrinting(obj: Any?) = obj?.run {
        runCatching {
            when {
                obj is String && isJson(obj) -> JsonParser.parseString(obj).toString()
                obj is String -> obj
                else -> gson.toJsonTree(obj).toString()
            }.let { jsonStr ->
                if (jsonStr.isEmpty()) {
                    return jsonStr
                }

                StringWriter().also { stringWriter ->
                    gson.toJson(
                        JsonParser.parseString(jsonStr),
                        gson.newJsonWriter(Streams.writerForAppendable(stringWriter)).apply {
                            setIndent("  ")
                        }
                    )
                }.toString()
            }
        }.onFailure {
            Log.w(TAG, it)
        }.getOrDefault(obj.toString())
    } ?: "null"

    /**
     * Pair array 정보를 pretty json 형태의 string 변환
     */
    fun toPrettyPrinting(vararg pairs: Pair<String, Any?>) = toPrettyPrinting(toJson(pairs.toMap()))

    fun toJson(map: Map<String, Any?>) = JsonObject().apply {
        map.forEach { (key, value) ->
            when {
                value is String && isJson(value) -> add(key, JsonParser.parseString(value))
                value is String -> addProperty(key, value)
                else -> add(key, gson.toJsonTree(value))
            }
        }
    }

    fun isJson(json: String?) = runCatching {
        gson.fromJson(json, Any::class.java).javaClass != String::class.java
    }.getOrDefault(false)

    fun <T> fromJsonOrNull(
        json: String,
        classOfT: Class<T>
    ) = runCatching {
        gson.fromJson(json, classOfT)
    }.onFailure {
        Log.w(TAG, it)
    }.getOrNull()

    fun directiveInfoToJsonObj(directiveInfo: DirectiveInfo) = JsonObject().apply {
        runCatching {
            add(
                "header",
                fromJsonOrNull(
                    gson.toJson(directiveInfo.directive.header),
                    JsonObject::class.java
                )
            )
            add(
                "payload",
                fromJsonOrNull(
                    directiveInfo.directive.payload,
                    JsonObject::class.java
                )
            )
        }.onFailure {
            Log.d(TAG, it.toString())
        }
    }

    fun loadJsonStringFromAsset(
        context: Context,
        fileName: String
    ) = runCatching {
        context.assets.open(fileName).use { inputStream ->
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            String(buffer)
        }
    }.onFailure {
        Log.w(TAG, it)
    }.getOrDefault("{}")

    fun toJsonObject(src: String) = runCatching {
        JsonParser.parseString(src).asJsonObject
    }.onFailure {
        Log.w(TAG, it)
    }.getOrNull()
}