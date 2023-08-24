package com.caspar.xl.di

import javax.inject.Qualifier

/**
 * 仅打印请求头和状态的http，适用于上传文件下载文件等
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HeaderOkHttpClient
/**
 *打印完整信息的retrofit
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BodyOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WaitDialogInject