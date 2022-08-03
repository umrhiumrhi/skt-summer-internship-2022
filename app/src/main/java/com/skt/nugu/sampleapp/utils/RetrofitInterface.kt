package com.skt.nugu.sampleapp.utils

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitInterface {
    @POST(".")
    fun postData(@Body param : HashMap<String, Any>) : Call<TestingDirective>
}