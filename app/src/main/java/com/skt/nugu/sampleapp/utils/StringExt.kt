package com.skt.nugu.sampleapp.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Base64

class StringExt

val String.Companion.EMPTY by lazy { "" }
val String.Companion.NULL by lazy { "null" }
val String.Companion.NEWLINE by lazy { "\n" }
val String.Companion.NEWLINE_UNICODE by lazy { "\u000d" }
val String.Companion.SPACE by lazy { " " }

fun String.lastSubstring(lastLength: Int)
        = if (withinRange(lastLength)) substring(length - lastLength, length) else this

/** Return true if index is within range, otherwise false. */
fun String.withinRange(index: Int) = index in 0..lastIndex

/** Returns a string with all ch characters removed. */
fun String.remove(ch: Char) = if (isEmpty()) this else replace(ch.toString(), String.EMPTY)

/** Returns a string which is removed all characters in 'chArray'. */
fun String.remove(chars: CharArray) = takeIf { isNotEmpty() && chars.isNotEmpty() }?.run {
    StringBuilder().also { sb ->
        var startIndex = 0
        while (true) {
            val foundIndex = indexOfAny(chars, startIndex)
            if (foundIndex != -1) {
                sb.append(this, startIndex, foundIndex)
                startIndex = foundIndex + 1
            } else {
                sb.append(this, startIndex, length)
                break
            }
        }
    }.toString()
} ?: this

/** Returns spannable CharSequence. */
fun String?.toSpannable(
    color: Int,
    style: Int = Typeface.NORMAL
) = SpannableString(this ?: "").apply {
    setSpan(ForegroundColorSpan(color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    setSpan(StyleSpan(style), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun String?.toNullOrEmptyString() = if (this == null) "NULL" else if (isEmpty()) "EMPTY" else this

fun String.Companion.newlineIndent(enableNewline: Boolean = true, indentCount: Int = 1) = StringBuilder().apply {
    if (enableNewline) {
        append(NEWLINE)

        repeat(indentCount) {
            append("    ")
        }
    }
}.toString()

fun String.equalsIgnoreCase(str: String) = equals(str, true)

fun String.equalsAny(
    vararg str: String,
    ignoreCase: Boolean = false
) = str.find { equals(it, ignoreCase) }?.run { true } ?: false

fun String?.containsMutually(str: String?, ignoreCase: Boolean = false): Boolean {
    if (!this.isNullOrEmpty() && !str.isNullOrEmpty()) {
        val longer = if (length > str.length) this else str
        val shorter = if (this.length > str.length) str else this
        return longer.contains(shorter, ignoreCase)
    }
    return false
}

fun String.fillFront(maxLength: Int, char: Char): String {
    var fillPrefix = ""
    repeat(maxLength - length) {
        fillPrefix = "$char$fillPrefix"
    }
    return fillPrefix + this
}

fun String.getBase64encodeToString(flags: Int = Base64.NO_WRAP): String {
    return Base64.encodeToString(this.toByteArray(), flags)
}

fun String.getBase64decode(flags: Int = Base64.NO_WRAP): String {
    return String(Base64.decode(this, flags))
}

fun String.markEllipsis(
    maxLength: Int,
    ellipsisMark: String = "..."
) = if ((length >= maxLength) && (maxLength > ellipsisMark.length)) {
    substring(0, maxLength - ellipsisMark.length) + ellipsisMark
} else {
    this
}