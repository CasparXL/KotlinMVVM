package com.caspar.xl.helper


import com.caspar.xl.network.ApiResponse

/**
 * 扩展函数
 */

/**
 * 数据仓库中的异步请求回调
 */
suspend fun <T> Any.call(job: suspend () -> T): ApiResponse<T> {
    return try {
        ApiResponse(job())
    } catch (e: java.lang.Exception) {
        ApiResponse(e)
    }
}
