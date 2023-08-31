package com.caspar.xl.network.websocket

import android.annotation.SuppressLint
import android.app.Application
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.app.lifecycleScope
import com.caspar.xl.network.util.isJson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds


/**
 * 长连接工具类,
 */
class WebSocketManager @Inject constructor(private val application: Application) {
    /**
     * 连接状态，用于判断长连接断连以后的重连操作
     */
    enum class ConnectState(val code:Int) {
        CONNECTED(4800),
        CONNECTING(4801),
        DISCONNECTING(4802),
        DISCONNECT(4803),
    }

    val coroutineError = CoroutineExceptionHandler { _, error ->
        application.lifecycleScope.launch {
            errorMessageFlow.emit("数据解析异常,请联系管理员处理")
            LogUtil.e(error)
        }
    }
    //错误信息反馈
    val errorMessageFlow = MutableSharedFlow<String>()
    //长连接返回json数据
    val contentCarsFlow = MutableSharedFlow<String>(1)

    //最后一次发送数据的时间戳
    private var requestPingTime: Long = 0

    //最后一次收到数据的时间戳
    private var responsePongTime: Long = 0

    //发送数据两秒收不到数据则超时
    private var sendTimeOut: Long = 5000

    //发送数据超时次数
    private var senTimeOutCount: Int = 0

    //最大数据超时三次
    private val maxCount = 4

    //记录所有长连接状态相关信息
    private val webUri: ConcurrentHashMap<String, ConnectState> = ConcurrentHashMap()
    //记录所有长连接对象相关信息
    private val webSocketUri: ConcurrentHashMap<String, WebSocket> = ConcurrentHashMap()

    //心跳检测机制,超出时间不返回则断连
    private val heartbeatFlow = flow<String> {
        while (true) {
            //五秒发送一次ping
            delay(5.seconds)
            val find = webUri.filterValues { it == ConnectState.CONNECTED }
            //确定连接成功状态的数据
            if (find.isNotEmpty()) {
                val sendWebSocket =
                    webSocketUri.filter { find.keys.firstOrNull() == it.key && it.key.contains("padNozzleRecordWebSocket") }
                        .firstNotNullOfOrNull { it.value }
                requestPingTime = System.currentTimeMillis()
                val send = sendWebSocket?.send("ping")
                LogUtil.d("WebSocket心跳:${send}")
                //发送和接受到的数据时间间隔超出规定时间没有收到记录一次
                if (abs(requestPingTime - responsePongTime) > sendTimeOut) {
                    senTimeOutCount++
                    LogUtil.d("WebSocket心跳超时,超时次数${senTimeOutCount}")
                } else {
                    //如果五秒内值正常响应,则重置超时次数
                    senTimeOutCount = 0
                    LogUtil.d("WebSocket心跳正常")
                }
                if (senTimeOutCount >= maxCount) {
                    webSocketUri.forEach { web ->
                        val webSocket = web.value
                        val closeResult = webSocket.close(ConnectState.DISCONNECT.code, "长连接请求异常, 无法收到回调, 全部关闭")
                        LogUtil.d("因WebSocket长时间未正常响应,关闭长连接:->${closeResult}")
                    }
                }
            } else {
                LogUtil.d("暂无已连接需要进行心跳的WebSocket")
            }
        }
    }.flowOn(Dispatchers.IO)

    //断连重试机制
    private val retryFlow = flow<String> {
        while (true) {
            val find = webUri.filterValues { it == ConnectState.DISCONNECT }.keys
            find.apply {
                withContext(Dispatchers.Main) {
                    if (find.isNotEmpty()) {
                        connectSocket(this@apply.first())
                    }
                }
            }
            delay(1.seconds)
        }
    }.flowOn(Dispatchers.IO)

    init {
        application.lifecycleScope.launch(Dispatchers.IO) {
            //重试机制
            launch {
                retryFlow.collect()
            }
            //心跳机制
            launch {
                heartbeatFlow.collect()
            }
        }
    }

    private val trustManager: X509TrustManager = object : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

    private val client by lazy {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(
            null,
            arrayOf<TrustManager>(trustManager),
            SecureRandom()
        )
        val sslSocketFactory = sslContext.socketFactory
        OkHttpClient.Builder()
            .writeTimeout(10, TimeUnit.SECONDS) //写入超时
            .readTimeout(10, TimeUnit.SECONDS) //读取超时
            .connectTimeout(10, TimeUnit.SECONDS) //连接超时规定时间
            .retryOnConnectionFailure(true) //重连操作，但是感觉没啥用。
            .sslSocketFactory(sslSocketFactory, trustManager) //SSL协议证书，同意所有协议网络请求，否则可能不支持http请求
            .hostnameVerifier { _, _ -> true }
            .build()
    }


    fun addConnectUrl(list: List<String>) {
        application.lifecycleScope.launch(Dispatchers.Main) {
            //清除所有旧数据
            client.dispatcher.cancelAll()
            webSocketUri.forEach {
                it.value.close(ConnectState.DISCONNECTING.code,"关闭长连接, 销毁资源")
            }
            webUri.clear()
            webSocketUri.clear()
            list.forEach {
                webUri[it] = ConnectState.DISCONNECT
            }
        }
    }

    private fun connectSocket(url: String) {
        senTimeOutCount = 0
        webUri[url] = ConnectState.CONNECTING
        LogUtil.d("准备连接${url}")
        val request = Request.Builder().url(url)
            .addHeader("Connection", "Upgrade")
            .addHeader("Upgrade", "websocket")
            .build()
        client.newWebSocket(request, WebSocketListener(url))
    }
    inner class WebSocketListener(private val url: String) : okhttp3.WebSocketListener(){
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            LogUtil.e("长连接${webSocket.request().url}关闭,状态为${code},原因${reason}")
            application.lifecycleScope.launch(Dispatchers.Main) {
                if (webUri.containsKey(url)){
                    webUri[url] = ConnectState.DISCONNECT
                }
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            application.lifecycleScope.launch(Dispatchers.Main) {
                LogUtil.d("长连接数量:[${webSocketUri.size},${webUri.size}]")
                LogUtil.e("长连接${webSocket.request().url}关闭中,状态为${code},原因${reason}")
                if (webUri.containsKey(url)){
                    webUri[url] = ConnectState.DISCONNECTING
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            application.lifecycleScope.launch(Dispatchers.Main) {
                LogUtil.d("长连接数量:[${webSocketUri.size},${webUri.size}]")
                LogUtil.e("长连接${webSocket.request().url}出现错误,状态为${t.message},原因${response?.message}")
                if (webUri.containsKey(url)){
                    webUri[url] = ConnectState.DISCONNECT
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            val requestUrl = webSocket.request().url.encodedPathSegments.last()
            application.lifecycleScope.launch(coroutineError) {
                withContext(Dispatchers.IO) {
                    if (text == "pong") {
                        LogUtil.d("收到心跳pong->${text}")
                        responsePongTime = System.currentTimeMillis()
                    } else if (requestUrl.contains("xxx")) {
                        if (text.isJson()) {
                            //发送flow信息到activity
                            //contentCarsFlow.emit(bean)
                        }
                    } else if (requestUrl.contains("xxx")) {
                        if (text.isJson()) {
                            //发送flow信息到activity
                            //contentCarsFlow.emit(bean)
                        }
                    }
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            LogUtil.e("接收到数据ByteString${bytes}")
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            application.lifecycleScope.launch(Dispatchers.Main) {
                LogUtil.e("打开长连接${webSocket.request().url}")
                webUri[url] = ConnectState.CONNECTED
                webSocketUri[url] = webSocket
            }
        }
    }
    /**
     * 清除所有长连接
     */
    fun onDestroy() {
        webUri.forEach {
            webUri[it.key] = ConnectState.DISCONNECTING
        }
        client.dispatcher.cancelAll()
        webSocketUri.forEach {
            it.value.close(ConnectState.DISCONNECTING.code,"关闭长连接, 销毁资源")
        }
        webUri.clear()
        webSocketUri.clear()
    }
}