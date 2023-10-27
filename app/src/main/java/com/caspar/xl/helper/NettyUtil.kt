package com.caspar.xl.helper

import android.content.Context
import com.caspar.base.utils.log.LOG_FILE_NAME
import com.caspar.base.utils.log.getLogFile
import com.caspar.base.utils.log.zipFolder
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.readAllParts
import io.ktor.http.content.streamProvider
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
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.io.File

/**
 * 创建Netty服务器
 */
fun Context.createNetty(port: Int = 8080): NettyApplicationEngine {
    return embeddedServer(factory = Netty, port = port) {
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
                    call.respond("暂无日志")
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
                    call.respond("暂无日志")
                }
            }
            //上传文件
            post("/upload") {
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        val fileName = part.originalFileName ?: ""
                        val files = File(externalCacheDir, "AppFiles").apply {
                            if (!exists()) {
                                mkdir()
                            }
                        }
                        val file = File(files, fileName)
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                    part.dispose()
                }
                call.respond("上传成功:\n${multipart.readAllParts().joinToString { (it.name ?: "") }}")
            }
            //下载文件
            get("/download/{fileName}") {
                val fileName = call.parameters["fileName"]
                val files = File(externalCacheDir, "AppFiles").apply {
                    if (!exists()) {
                        mkdir()
                    }
                }
                val file = File(files, fileName ?: "")
                if (file.exists()) {
                    call.respondFile(file)
                } else {
                    call.respondText("未找到该文件")
                }
            }
            //获取文件列表
            get("/fileList") {
                val file = File(externalCacheDir, "AppFiles").apply {
                    if (!exists()) {
                        mkdir()
                    }
                }
                if (file.exists() && file.isDirectory) {
                    val files = file.listFiles()
                    if (files != null && files.isNotEmpty()) {
                        val fileNames = buildString {
                            files.forEach { file ->
                                append("<li><a href=\"/download/${file.name}\">${file.name}</a></li>")
                            }
                        }
                        call.respondText(
                            """
                            <html>
                                <head>
                                    <title>文件列表</title>
                                </head>
                                <body>
                                    <h1>文件列表</h1>
                                    <ul>
                                        $fileNames
                                    </ul>
                                </body>
                            </html>
                            """.trimIndent(), ContentType.Text.Html
                        )
                    } else {
                        call.respondText("文件夹为空")
                    }
                } else {
                    call.respondText("无效的文件夹路径", status = HttpStatusCode.BadRequest)
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