package com.caspar.xl.network.interceptor

import com.caspar.xl.helper.MMKVUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AddCookiesInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val cookie: String? = MMKVUtil.decodeString("cookie")
        if (cookie != null)
            builder.addHeader("Cookie", cookie)
        return chain.proceed(builder.build())
    }
}