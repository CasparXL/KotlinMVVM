package com.caspar.xl.network.util

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * Post请求Json数据转换
 */
object PostJson {
    fun toRequestBody(json: String?): RequestBody {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json!!)
    }

    fun toJsonString(json: Any?): String {
        return Gson().toJson(json)
    }
}