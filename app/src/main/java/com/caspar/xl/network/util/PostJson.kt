package com.caspar.xl.network.util

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Post请求Json数据转换
 */
object PostJson {
    fun toRequestBody(json: String): RequestBody {
        return json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    fun toJsonString(json: Any?): String {
        return Gson().toJson(json)
    }
}