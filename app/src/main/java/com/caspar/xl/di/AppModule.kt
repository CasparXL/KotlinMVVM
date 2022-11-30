package com.caspar.xl.di

import android.app.Application
import com.caspar.xl.config.ApiConfig
import com.caspar.xl.network.Api
import com.caspar.xl.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideWeatherApi(): ApiService {
        return Retrofit.Builder().baseUrl(ApiConfig.BaseUrl)
            .addConverterFactory(ScalarsConverterFactory.create()) //添加ScalarsConverterFactory支持
            .addConverterFactory(GsonConverterFactory.create())//可以接收自定义的Gson，当然也可以不传
            .client(Api.unsafeOkHttpClient)
            .build()
            .create()
    }

}