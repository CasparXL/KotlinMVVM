package com.caspar.xl.network.interceptor

import com.caspar.xl.app.BaseApplication
import com.caspar.xl.network.util.CheckNetwork
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class AddCacheInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val cacheBuilder = CacheControl.Builder()
        cacheBuilder.maxAge(0, TimeUnit.SECONDS)
        cacheBuilder.maxStale(365, TimeUnit.DAYS)
        val cacheControl = cacheBuilder.build()
        var request = chain.request()
        if (!CheckNetwork.isNetworkConnected(BaseApplication.context)) {
            request = request.newBuilder()
                .cacheControl(cacheControl)
                .build()
        }
        val originalResponse = chain.proceed(request)
        return if (CheckNetwork.isNetworkConnected(BaseApplication.context)) {
            // read from cache
            val maxAge = 0
            originalResponse.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public ,max-age=$maxAge")
                .build()
        } else {
            // tolerate 4-weeks stale
            val maxStale = 60 * 60 * 24 * 28
            originalResponse.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
    }
}