package com.caspar.xl.network


import com.caspar.xl.bean.response.City
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.config.ApiConfig
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * "CasparXL" 创建 2019/7/1.
 * 界面名称以及功能:Http的接口
 */
interface ApiService {

    /**
     * 下载文件
     */
    @Streaming
    @GET("v1/download")
    fun download(@Query("filename") fileName: String?): Call<ResponseBody>?


    /**
     * config设置
     */
    @GET(ApiConfig.getCity)
    suspend fun getCity(): City

    /**
     * 翻译
     */
    @GET(ApiConfig.Translate)
    suspend fun Translate(
        @Query("doctype") doctype: String = "json",
        @Query("type") type: String = "AUTO",
        @Query("i") text: String
    ): TranslateBean


}