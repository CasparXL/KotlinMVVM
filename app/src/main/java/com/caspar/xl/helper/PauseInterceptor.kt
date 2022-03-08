package com.caspar.xl.helper

import coil.intercept.Interceptor
import coil.request.ImageResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

class PauseInterceptor : Interceptor {
    companion object {
        //用于判断是否加载图片，当该值为false的时候，可以加载，反之，加载默认图片
        val isPaused = MutableStateFlow(false)
    }

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        //Flow.first方法内部如果是true，则执行下面的方法，反之则不执行下面的方法
        isPaused.first { !it }
        return chain.proceed(chain.request)
    }
}