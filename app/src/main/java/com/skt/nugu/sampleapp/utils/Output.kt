package com.skt.nugu.sampleapp.utils


import com.google.gson.annotations.SerializedName

data class Output(
    @SerializedName("appExec")
    val appExec: String,
    @SerializedName("appName")
    val appName: String,
    @SerializedName("searchKeyword")
    val searchKeyword: String
)