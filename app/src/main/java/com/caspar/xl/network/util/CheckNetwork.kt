package com.caspar.xl.network.util

import com.caspar.base.utils.log.eLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import kotlin.time.Duration.Companion.seconds

/**
 * 异步线程中检测网络状态(根据情况选择要ping的网络，最佳地址是你要请求的服务器ip地址,可以检测网络延迟程度，同时方便检测接口请求的大概情况)
 */
val netWorkFlow = flow {
    while (true) {
        var networkDelay = ""
        var netStatus = 1
        try {
            val p = Runtime.getRuntime().exec("ping -c 1 -w 10 www.baidu.com")
            netStatus = p.waitFor()
            if (netStatus == 0) {
                val buf = BufferedReader(InputStreamReader(p.inputStream))
                var str: String? = null
                while (true) {
                    try {
                        if (buf.readLine().also { str = it } == null) break
                    } catch (e: IOException) {
                        e.eLog()
                    }
                    if (str?.contains("avg") == true) {
                        val i = str?.indexOf("/", 20) ?: 0
                        val j = str?.indexOf(".", i) ?: 0
                        networkDelay = str?.substring(i + 1, j) ?: ""
                    }
                }
            }
        } catch (e: Exception) {
            e.eLog()
        }
        /*emit(
            if (netStatus == 0 && networkDelay.isNotEmpty()) {
                val delayNumber = networkDelay.toIntOrNull() ?: 101
                val pj: String = if (delayNumber < 30) {
                    "优"
                } else if (delayNumber < 50) {
                    "良"
                } else if (delayNumber < 100) {
                    "差"
                } else {
                    "极差"
                }
                "网络延迟：" + networkDelay + "ms " + " 等级：" + pj
            } else {
                "网络未连接"
            }
        )*/
        emit(
            if (netStatus == 0 && networkDelay.isNotEmpty()) {
                networkDelay.toIntOrNull() ?: 101
            } else {
                101
            }
        )
        delay(3.seconds)
    }
}.flowOn(Dispatchers.IO)

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