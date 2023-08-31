package com.caspar.xl.network.util

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Post请求Json数据转换
 */
fun Any.toRequestBody(): RequestBody {
    return if (this is String) {
        this.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    } else {
        Gson().toJson(this).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }
}

/**
 * 判断字符串是否是json
 */
fun String.isJson(): Boolean {
    val pattern = """^[\{\[].*[\}\]]$"""
    return this.trim().matches(pattern.toRegex())
}