package com.caspar.xl.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HeaderOkHttpClient
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BodyOkHttpClient