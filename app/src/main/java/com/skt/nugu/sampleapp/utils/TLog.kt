/**
 * Copyright (c) 2019 SK Telecom Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skt.nugu.sampleapp.utils

import android.util.Log
import com.skt.nugu.sampleapp.BuildConfig

/**
 * @since 2019-06-20 created by hyunho.mo.
 */
object TLog {
    private const val TAG = "TLog"

    private var TAG_PREFIX = ""

    fun initialize(tagPrefix: String) {
        TAG_PREFIX = tagPrefix
    }

    fun d(
        tag: String?,
        msg: String?,
        insertToDb: Boolean = true
    ) = "$TAG_PREFIX$tag".let { modifiedTag ->
        runIfEnabled {
            Log.d(modifiedTag, msg.toNullOrEmptyString())
        }.apply {
            insertToDb.runTrue {
                insertToLogBuffer(Log.DEBUG, modifiedTag, msg)
            }
        }
    }

    fun i(
        tag: String?,
        msg: String?,
        insertToDb: Boolean = true
    ) = "$TAG_PREFIX$tag".let { modifiedTag ->
        runIfEnabled {
            Log.i(modifiedTag, msg.toNullOrEmptyString())
        }.apply {
            insertToDb.runTrue {
                insertToLogBuffer(Log.INFO, modifiedTag, msg)
            }
        }
    }

    fun w(
        tag: String?,
        msg: String?,
        insertDatabase: Boolean = true
    ) = "$TAG_PREFIX$tag".let { modifiedTag ->
        runIfEnabled {
            Log.w(modifiedTag, msg.toNullOrEmptyString())
        }.apply {
            insertDatabase.runTrue {
                insertToLogBuffer(Log.WARN, modifiedTag, msg)
            }
        }
    }

    fun e(
        tag: String?,
        msg: String?,
        insertToDb: Boolean = true
    ) = "$TAG_PREFIX$tag".let { modifiedTag ->
        runIfEnabled {
            Log.e(modifiedTag, msg.toNullOrEmptyString())
        }.apply {
            insertToDb.runTrue {
                insertToLogBuffer(Log.ERROR, modifiedTag, msg)
            }
        }
    }

    private fun runIfEnabled(block: () -> Int) = BuildConfig.DEBUG.runTrue { block() } ?: 0

    fun d(
        tag: String?,
        throwable: Throwable,
        insertToDb: Boolean = true
    ) = runCatching {
        stackTraceToString(throwable)
    }.getOrDefault(throwable.message).let {
        d(tag, "[Exception] $it", insertToDb)
    }

    fun i(
        tag: String?,
        throwable: Throwable,
        insertToDb: Boolean = true
    ) = runCatching {
        stackTraceToString(throwable)
    }.getOrDefault(throwable.message).let {
        i(tag, "[Exception] $it", insertToDb)
    }

    fun w(
        tag: String?,
        throwable: Throwable,
        insertToDb: Boolean = true
    ) = runCatching {
        stackTraceToString(throwable)
    }.getOrDefault(throwable.message).let {
        w(tag, "[Exception] $it", insertToDb)
    }

    fun e(
        tag: String?,
        throwable: Throwable,
        insertToDb: Boolean = true
    ) = runCatching {
        stackTraceToString(throwable)
    }.getOrDefault(throwable.message).let {
        e(tag, "[Exception] $it", insertToDb)
    }

    private fun stackTraceToString(throwable: Throwable) = StringBuilder().apply {
        append(throwable.toString()).appendNewLine()

        throwable.stackTrace.forEach {
            append("\tat ").append(it.toString()).appendNewLine()
        }
    }.toString()

    fun insertToLogBuffer(
        priority: Int,
        tag: String?,
        msg: String?
    ) {
        runCatching {
        }.onFailure {
            e(TAG, it, false)
        }
    }
}
