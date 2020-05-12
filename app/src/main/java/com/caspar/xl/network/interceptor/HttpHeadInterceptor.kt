package com.caspar.xl.network.interceptor

import com.caspar.xl.app.BaseApplication
import com.caspar.xl.network.util.CheckNetwork
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HttpHeadInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Accept", "application/json;versions=1")
        if (CheckNetwork.isNetworkConnected(BaseApplication.context)) {
            val maxAge = 60
            builder.addHeader("Cache-Control", "public, max-age=$maxAge")
        } else {
            val maxStale = 60 * 60 * 24 * 28
            builder.addHeader("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
        }
        return chain.proceed(builder.build())
    }
}