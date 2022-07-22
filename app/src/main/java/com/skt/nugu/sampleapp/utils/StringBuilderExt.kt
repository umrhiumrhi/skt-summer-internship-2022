package com.skt.nugu.sampleapp.utils

/**
 * @since 2021-10-14 created by hyunho.mo.
 */

class StringBuilderExt

fun StringBuilder.appendNewLine() = apply { append(String.NEWLINE) }
fun StringBuilder.appendSpace() = apply { append(String.SPACE) }
