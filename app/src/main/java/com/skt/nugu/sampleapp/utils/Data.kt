package com.skt.nugu.sampleapp.utils


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("action")
    val action: String,
    @SerializedName("url")
    val url: String
)