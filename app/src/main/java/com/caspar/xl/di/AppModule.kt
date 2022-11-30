package com.caspar.xl.di

import android.content.Context
import androidx.room.Room
import com.caspar.xl.bean.db.TeacherDao
import com.caspar.xl.bean.db.UserDao
import com.caspar.xl.config.ApiConfig
import com.caspar.xl.db.RoomManager
import com.caspar.xl.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @BodyOkHttpClient
    @Provides
    fun provideBodyApi(): ApiService {
        return Retrofit.Builder().baseUrl(ApiConfig.BaseUrl)
            .addConverterFactory(ScalarsConverterFactory.create()) //添加ScalarsConverterFactory支持
            .addConverterFactory(GsonConverterFactory.create())//可以接收自定义的Gson，当然也可以不传
            .client(Api.okhttpLogBody)
            .build()
            .create()
    }

    @HeaderOkHttpClient
    @Provides
    fun provideHeaderApi(): ApiService {
        return Retrofit.Builder().baseUrl(ApiConfig.BaseUrl)
            .addConverterFactory(ScalarsConverterFactory.create()) //添加ScalarsConverterFactory支持
            .addConverterFactory(GsonConverterFactory.create())//可以接收自定义的Gson，当然也可以不传
            .client(Api.okhttpHeader)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideRoomManager(@ApplicationContext context: Context): RoomManager {
        return Room.databaseBuilder(context, RoomManager::class.java, "test_database").build()
    }

    @Singleton
    @Provides
    fun provideTeacherDao(database: RoomManager): TeacherDao {
        return database.getTeacherDao()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: RoomManager): UserDao {
        return database.getUserDao()
    }
}