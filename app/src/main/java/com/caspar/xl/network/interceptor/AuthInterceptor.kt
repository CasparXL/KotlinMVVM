package com.caspar.xl.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation


@Target(AnnotationTarget.FUNCTION)
annotation class Authenticated

/**
 * 请求头加入用户Token
 * 搭配[Authenticated]使用才会生效,加入可该注解，才会将请求头写入,否则不会传入请求头
 */
class AuthInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val invocation = chain.request().tag(Invocation::class.java)
            ?: return chain.proceed(chain.request())

        val shouldAttachAuthHeader = invocation
            .method()
            .annotations
            .any { it.annotationClass == Authenticated::class }

        return if(shouldAttachAuthHeader) {
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "my token")
                    .build()
            )
        } else chain.proceed(chain.request())
    }
}