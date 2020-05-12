package com.caspar.xl.network.util

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    fun toStringRequestBody(value: String?): RequestBody? {
        return if (value != null) RequestBody.create(MediaType.parse("text/plain"), value) else null
    }

    /**
     * 上传图片类型
     *
     * @param value
     * @return
     */
    fun toFileRequestBody(value: File?): RequestBody? {
        return if (value != null) RequestBody.create(
            MediaType.parse("multipart/form-data"),
            value
        ) else null
    }

    /**
     * @param requestDataMap 用于Retrofit的Post请求form表单数据过多可以使用该请求
     * @return
     */
    fun toMapRequestBody(requestDataMap: Map<String, String>): Map<String, RequestBody> {
        val requestBodyMap: MutableMap<String, RequestBody> = HashMap()
        for (key in requestDataMap.keys) {
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), requestDataMap[key])
            requestBodyMap[key] = requestBody
        }
        return requestBodyMap
    }

    /**
     * @param Files 用于Retrofit的Post请求form表单数据过多可以使用该请求
     * @return
     */
    fun toMapRequestBody(Files: List<File>): Map<String, RequestBody> {
        val gson = Gson()
        val info = RequestBody.create(MultipartBody.FORM, gson.toJson(Files))
        val map =
            HashMap<String, RequestBody>()
        for (file in Files) {
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
            map["pictures\";filename=\"" + file.name] = requestFile
        }
        return map
    }
}