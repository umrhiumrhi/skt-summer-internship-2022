package com.skt.nugu.sampleapp.utils


import com.google.gson.annotations.SerializedName

data class TestingDirective(
    @SerializedName("directives")
    val directives: List<Directive>,
    @SerializedName("output")
    val output: Output,
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("version")
    val version: String
)