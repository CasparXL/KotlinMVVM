package com.caspar.xl.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.caspar.base.utils.log.LOG_FILE_NAME
import com.caspar.base.utils.log.getLogFile
import com.caspar.base.utils.log.zipFolder
import com.caspar.xl.bean.db.TeacherDao
import com.caspar.xl.bean.db.UserDao
import com.caspar.xl.config.ApiConfig
import com.caspar.xl.db.RoomManager
import com.caspar.xl.network.*
import com.caspar.xl.network.websocket.WebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * 开启单独服务器
     */
    @Provides
    @Singleton
    fun ktorService(@ApplicationContext context: Context): NettyApplicationEngine {
        return embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation){
                gson {
                    setPrettyPrinting()
                }
            }
            install(StatusPages) {
                exception { call: ApplicationCall, cause: Throwable ->
                    call.respond(HttpStatusCode.InternalServerError, cause.localizedMessage)
                }
            }
            routing {
                get("/") {
                    val logFile = File(context.getLogFile(), LOG_FILE_NAME)
                    if (logFile.exists() && logFile.length() > 0){
                        call.respondFile(logFile)
                    } else {
                        call.respond("未查找到文件")
                    }
                }
                get("/allLog") {
                    val logFile = context.getLogFile()
                    logFile.apply {
                        list()?.forEach {
                            if (it.contains("zip")){
                                File(it).delete()
                            }
                        }
                    }
                    val path = zipFolder(zipFileString = logFile.path?:"", srcFileString = logFile.path?:"")
                    if (File(path).exists()){
                        call.respondFile(File(path))
                    } else {
                        call.respond("未查找到文件")
                    }
                }
            }
        }
    }

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

    @Provides
    fun provideWebSocket(@ApplicationContext context: Context): WebSocketManager {
        return WebSocketManager(context as Application)
    }
}