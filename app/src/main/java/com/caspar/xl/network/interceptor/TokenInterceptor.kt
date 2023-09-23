package com.caspar.xl.network.interceptor

import com.caspar.base.utils.log.dLog
import com.caspar.xl.helper.MMKVUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 自动刷新token的拦截器
 */
class TokenInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response: Response = chain.proceed(request)
        "response.code=${response.code}".dLog()
        //根据和服务端的约定判断token过期
        if (isTokenExpired(request,response)) {
            val refreshToken = MMKVUtil.decodeString("refreshToken")
            "自动刷新Token,然后重新请求数据".dLog()
            //同步请求方式，获取最新的Token
            val newToken = getNewToken(refreshToken)
            if (newToken == null){
                "token过期并且刷新失败".dLog()
                //这里跳转到登陆界面给用户
                return response
            } else {
                "存入新的token和刷新token用的refreshToken".dLog()
                //使用新的Token，创建新的请求
                val newRequest: Request = chain.request()
                    .newBuilder()
                    .header("Authorization", newToken)
                    .build()
                //重新请求,这里要把旧的response给close掉，否则会崩溃
                response.close()
                return chain.proceed(newRequest)
            }
        }
        return response
    }

    /**
     * 根据Response，判断Token是否失效
     *
     * @param response
     * @return
     */
    private fun isTokenExpired(request: Request,response: Response): Boolean {
        return response.code == 401
    }

    private fun getNewToken(refreshToken:String): String {
        return ""
    }
}