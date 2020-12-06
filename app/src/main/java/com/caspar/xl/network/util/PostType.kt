package com.caspar.xl.network.util

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

/**
 * Retrofit的Post请求工具类
 */
object PostType {
    /**
     * @param value
     * @return
     */
    fun toStringRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    /**
     * 上传图片类型
     *
     * @param value
     * @return
     */
    fun toFileRequestBody(value: File): RequestBody? {
        return value.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    /**
     * @param requestDataMap 用于Retrofit的Post请求form表单数据过多可以使用该请求
     * @return
     */
    fun toMapRequestBody(requestDataMap: Map<String, String>): Map<String, RequestBody> {
        val requestBodyMap: MutableMap<String, RequestBody> = HashMap()
        for (key in requestDataMap.keys) {
            val requestBody = (requestDataMap[key] ?: "").toRequestBody("multipart/form-data".toMediaTypeOrNull())
            requestBodyMap[key] = requestBody
        }
        return requestBodyMap
    }

    /**
     * @param Files 用于Retrofit的Post请求form表单数据过多可以使用该请求
     * @return
     */
    fun toMapRequestBody(Files: List<File>): Map<String, RequestBody> {
        val map = HashMap<String, RequestBody>()
        for (file in Files) {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            map["pictures\";filename=\"" + file.name] = requestFile
        }
        return map
    }
}