package com.skt.nugu.sampleapp.utils

import java.util.*

/**
 * @since 2020-02-06 created by hyunho.mo.
 */

class BooleanExt

/** Returns [positive] value if [this] is true, otherwise [negative] value. */
fun <T> Boolean.take(positive: T, negative: T) = if (this) positive else negative

/** Calls [positive] function if [this] is true, otherwise [negative] function. */
inline fun <T> Boolean.runTake(positive: () -> T, negative: () -> T) = run { if (this) positive() else negative() }

/** Calls [positive] function if [this] is true, otherwise [negative] function. */
inline fun Boolean.applyTake(positive: () -> Unit, negative: () -> Unit) = apply { if (this) positive() else negative() }

/** Calls the specified function [block] only if [this] is true */
inline fun Boolean.runTrue(block: () -> Unit) = apply { if (this) { block() } }

/** Calls the specified function [block] only if [this] is false */
inline fun Boolean.runFalse(block: () -> Unit) = apply { not().runTrue(block) }

/** Returns a string representation of the object as uppercase. */
fun Boolean?.toStringUpperCase() = if (this != null) toString().toUpperCase(Locale.getDefault()) else "NULL"


