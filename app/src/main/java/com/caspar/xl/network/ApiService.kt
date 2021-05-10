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
     * @param range 下载的范围，一般只有流文件才会生效，其他类型的下载需要做文件过滤 格式为:bytes = 全部[0-],范围[0-100]
     * @param url 下载的连接地址
     */
    @Streaming
    @GET
    suspend fun download(@Header("Range") range: String, @Url url: String): ResponseBody

    /**
     * config设置
     */
    @GET(ApiConfig.getCity)
    suspend fun getCity(): City

    /**
     * 翻译
     */
    @GET(ApiConfig.Translate)
    suspend fun translate(@Query("doctype") doctype: String = "json", @Query("type") type: String = "AUTO", @Query("i") text: String): TranslateBean

    /**
     * config设置
     */
    @GET(ApiConfig.getCity)
    fun getSyncCity(): Call<City>
}
