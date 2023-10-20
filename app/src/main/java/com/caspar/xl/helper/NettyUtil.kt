package com.caspar.xl.helper

import android.content.Context
import com.caspar.base.utils.log.LOG_FILE_NAME
import com.caspar.base.utils.log.getLogFile
import com.caspar.base.utils.log.zipFolder
import io.ktor.http.HttpStatusCode
import io.ktor.http.hostIsIp
import io.ktor.serialization.gson.gson
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.io.File
import java.io.IOException
import java.net.ServerSocket

/**
 * 创建Netty服务器
 */
fun Context.createNetty(port: Int = 8080): NettyApplicationEngine {
    return embeddedServer(factory = Netty, host = "", port = port) {
        install(ContentNegotiation) {
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
                val logFile = File(getLogFile(), LOG_FILE_NAME)
                if (logFile.exists() && logFile.length() > 0) {
                    call.respondFile(logFile)
                } else {
                    call.respond("未查找到文件")
                }
            }
            get("/allLog") {
                val logFile = getLogFile()
                logFile.apply {
                    list()?.forEach {
                        if (it.contains("zip")) {
                            File(it).delete()
                        }
                    }
                }
                val path = zipFolder(
                    zipFileString = logFile.path ?: "",
                    srcFileString = logFile.path ?: ""
                )
                if (File(path).exists()) {
                    call.respondFile(File(path))
                } else {
                    call.respond("未查找到文件")
                }
            }
        }
    }
}

/**
 * 判断是否初始化过
 * @return true 代表初始化过，false代表未初始化过
 */
fun NettyApplicationEngine.isInit(): Boolean {
    return this.application.pluginOrNull(ContentNegotiation) != null && this.application.pluginOrNull(
        StatusPages
    ) != null && this.application.pluginOrNull(Routing) != null
}

/**
 * @return 端口号是否占用判断 true代表可用，false代表已被占用
 */
fun Int.isPortAvailable(): Boolean {
    var serverSocket: ServerSocket? = null
    return try {
        serverSocket = ServerSocket(this)
        true
    } catch (e: IOException) {
        false
    } finally {
        serverSocket?.close()
    }
}