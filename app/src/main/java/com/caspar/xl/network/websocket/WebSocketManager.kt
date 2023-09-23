package com.caspar.xl.network.websocket

import android.annotation.SuppressLint
import android.app.Application
import com.caspar.base.utils.log.dLog
import com.caspar.base.utils.log.eLog
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


class WebSocketManager @Inject constructor(private val application: Application) {
    /**
     * 连接状态，用于判断长连接断连以后的重连操作
     */
    enum class ConnectState(val code: Int) {
        CONNECTED(4800),
        CONNECTING(4801),
        DISCONNECTING(4802),
        DISCONNECT(4803),
    }

    val coroutineError = CoroutineExceptionHandler { _, error ->
        application.lifecycleScope.launch {
            errorMessageFlow.emit("数据解析异常,请联系管理员处理")
            error.eLog()
        }
    }
    val errorMessageFlow = MutableSharedFlow<String>()
    val contentOilsFlow = MutableSharedFlow<String>(1)

    //最后一次发送数据的时间戳 默认0
    private var requestPingTime: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    //最后一次收到数据的时间戳 默认0
    private var responsePongTime: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    //发送数据两秒收不到数据则超时 默认5000
    private val sendTimeOut: Long = 5000

    //发送数据超时次数 默认0
    private var sendTimeOutCount: ConcurrentHashMap<String, Int> = ConcurrentHashMap()

    //最大数据超时三次 默认4
    private val maxCount: Int = 4

    //记录所有长连接相关信息
    private val webUri: ConcurrentHashMap<String, ConnectState> = ConcurrentHashMap()

    //记录所有长连接对象信息
    private val webSocketUri: ConcurrentHashMap<String, WebSocket> = ConcurrentHashMap()

    //心跳检测机制,超出时间不返回则断连
    private val heartbeatFlow = flow<String> {
        while (true) {
            //五秒发送一次ping
            delay(5.seconds)
            //确定连接成功状态的数据
            webSocketUri.forEach { (t, u) ->
                val socket = u.request().url.toString()
                val currentUriRequestTime = requestPingTime[socket] ?: 0
                val currentUriResponseTime = responsePongTime[socket] ?: 0
                val sendTimeOutCountValue = sendTimeOutCount[socket] ?: 0
                if (webUri.containsKey(t)) {
                    if (webUri[t] == ConnectState.CONNECTED) {
                        requestPingTime[socket] = System.currentTimeMillis()
                        "WebSocket[${u.request().url.encodedPathSegments.last()}]心跳}".dLog()
                        u.send("ping")
                        //发送和接受到的数据时间间隔超出规定时间没有收到记录一次
                        if (abs(currentUriRequestTime - currentUriResponseTime) > sendTimeOut) {
                            sendTimeOutCount[socket] = sendTimeOutCountValue + 1
                            "WebSocket心跳超时,超时次数${sendTimeOutCountValue + 1}".dLog()
                        } else {
                            //如果五秒内值正常响应,则重置超时次数
                            sendTimeOutCount[socket] = 0
                            "WebSocket心跳正常".dLog()
                        }
                        if ((sendTimeOutCount[socket] ?: 0) >= maxCount) {
                            sendTimeOutCount[socket] = 0
                            requestPingTime[socket] = 0
                            responsePongTime[socket] = 0
                            u.close(ConnectState.DISCONNECT.code, "长连接请求异常,无法收到回调,关闭当前超时的长连接")
                        }
                    } else {
                        "暂无需要心跳的长连接".dLog()
                    }
                } else {
                    "暂无需要心跳的长连接".dLog()
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    //断连重试机制
    private val retryFlow = flow<String> {
        while (true) {
            val find = webUri.filterValues { it == ConnectState.DISCONNECT }.keys
            find.apply {
                if (find.isNotEmpty()) {
                    connectSocket(this@apply.first())
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
                it.value.close(ConnectState.DISCONNECTING.code, "关闭长连接, 销毁资源")
            }
            requestPingTime.clear()
            responsePongTime.clear()
            sendTimeOutCount.clear()
            webUri.clear()
            webSocketUri.clear()
            list.forEach {
                webUri[it] = ConnectState.DISCONNECT
            }
        }
    }

    private fun connectSocket(url: String) {
        application.lifecycleScope.launch(Dispatchers.Main) {
            webUri[url] = ConnectState.CONNECTING
            "准备连接${url}".dLog()
            val request = Request.Builder().url(url)
                .addHeader("Connection", "Upgrade")
                .addHeader("Upgrade", "websocket")
                .build()
            client.newWebSocket(request, WebSocketListener(url))
        }
    }

    inner class WebSocketListener(private val url: String) : okhttp3.WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            "长连接${webSocket.request().url}关闭,状态为${code},原因${reason}".eLog()
            application.lifecycleScope.launch(Dispatchers.Main) {
                if (webUri.containsKey(url)) {
                    webUri[url] = ConnectState.DISCONNECT
                }
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            application.lifecycleScope.launch(Dispatchers.Main) {
                "长连接${webSocket.request().url}关闭中,状态为${code},原因${reason}".eLog()
                if (webUri.containsKey(url)) {
                    webUri[url] = ConnectState.DISCONNECT
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            application.lifecycleScope.launch(Dispatchers.Main) {
                "长连接${webSocket.request().url}出现错误,状态为${t.message},原因${response?.message}".eLog()
                if (webUri.containsKey(url)) {
                    webUri[url] = ConnectState.DISCONNECT
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            val requestUrl = webSocket.request().url.encodedPathSegments.last()
            application.lifecycleScope.launch(coroutineError) {
                withContext(Dispatchers.Main) {
                    if (text == "pong") {
                        "收到[${requestUrl}]心跳ping->${text}".dLog()
                        responsePongTime[webSocket.request().url.toString()] = System.currentTimeMillis()
                    }
                    if (requestUrl.contains("xxx")) {
                        if (text.isJson()) {
                            contentOilsFlow.emit("bean")
                        }
                    }
                    if (requestUrl.contains("xxx")) {
                        if (text.isJson()) { }
                    }
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            "接收到数据ByteString${bytes}".eLog()
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            application.lifecycleScope.launch(Dispatchers.Main) {
                "打开长连接${webSocket.request().url}".dLog()
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
            it.value.close(ConnectState.DISCONNECTING.code, "关闭长连接, 销毁资源")
        }
        requestPingTime.clear()
        responsePongTime.clear()
        sendTimeOutCount.clear()
        webUri.clear()
        webSocketUri.clear()
    }
}