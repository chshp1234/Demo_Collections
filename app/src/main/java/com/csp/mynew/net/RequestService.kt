package com.csp.mynew.net

import com.csp.mynew.net.bean.QA
import com.csp.network.data.Result
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * created by dongdaqing 9/14/21 10:48 AM
 */
interface RequestService {

    @GET("wenda/list/1/json")
    suspend fun getQA(): Base<QA>

    @GET("wenda/list/1/json")
    suspend fun getQAResult(): Result<QA>

    @GET("wenda/list/1/json")
    fun getQACall(): Call<Base<QA>>
}