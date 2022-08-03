package com.skt.nugu.sampleapp.utils


import com.google.gson.annotations.SerializedName

data class Directive(
    @SerializedName("data")
    val data: Data,
    @SerializedName("type")
    val type: String,
    @SerializedName("version")
    val version: String
)