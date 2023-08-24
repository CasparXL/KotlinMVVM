package com.caspar.xl.network.websocket

import android.annotation.SuppressLint
import android.app.Application
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.app.scope
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
import okhttp3.WebSocketListener
import okio.ByteString
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.time.Duration.Companion.seconds


/**
 * 长连接工具类,搭配Hilt进行注入，方便使用
 */
class WebSocketManager(val application: Application) {
    /**
     * 连接状态，用于判断长连接断连以后的重连操作
     */
    enum class ConnectState {
        CONNECTED,
        CONNECTING,
        DISCONNECT
    }

    val contentFlow = MutableSharedFlow<String>(1)
    val content2Flow = MutableSharedFlow<String>(1)

    private val webUri: ConcurrentHashMap<String, ConnectState> = ConcurrentHashMap()

    private val retryFlow = flow<String> {
        while (true) {
            delay(1.seconds)
            val find = webUri.filterValues { it == ConnectState.DISCONNECT }.keys
            find.apply {
                withContext(Dispatchers.Main) {
                    if (find.isNotEmpty()) {
                        connectSocket(this@apply.first())
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    init {
        application.scope.launch {
            retryFlow.collect()
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

    /**
     * 加入要连接的长连接,init方法块会自动去连接设备
     */
    fun addConnectUrl(list: List<String>) {
        list.forEach {
            webUri[it] = ConnectState.DISCONNECT
        }
    }

    private fun connectSocket(url: String) {
        webUri[url] = ConnectState.CONNECTING
        LogUtil.d("准备连接${url}")
        val request = Request.Builder().url(url)
            .addHeader("Connection", "Upgrade")
            .addHeader("Upgrade", "websocket")
            .build()
        client.newWebSocket(request, object : WebSocketListener() {
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                LogUtil.e("长连接${webSocket.request().url}关闭,状态为${code},原因${reason}")
                application.scope.launch(Dispatchers.Main) {
                    webUri[url] = ConnectState.DISCONNECT

                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                LogUtil.e("长连接${webSocket.request().url}关闭,状态为${code},原因${reason}")
                application.scope.launch(Dispatchers.Main) {
                    webUri[url] = ConnectState.DISCONNECT
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                LogUtil.e("长连接${webSocket.request().url}出现错误,状态为${t.message},原因${response?.message}")
                application.scope.launch(Dispatchers.Main) {
                    webUri[url] = ConnectState.DISCONNECT
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                val requestUrl = webSocket.request().url.encodedPathSegments.last()
                application.scope.launch(Dispatchers.IO) {
                    if (requestUrl.contains("xxx")) {
                        contentFlow.emit("GsonUtils.fromJson(text, XXX1::class.java)")
                    } else if (requestUrl.contains("xxx")) {
                        contentFlow.emit("GsonUtils.fromJson(text, XXX2::class.java)")
                    }
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                LogUtil.e("接收到数据ByteString${bytes}")
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                LogUtil.e("打开长连接${webSocket.request().url}")
                application.scope.launch(Dispatchers.Main) {
                    webUri[url] = ConnectState.CONNECTED
                }
            }
        })
    }
}