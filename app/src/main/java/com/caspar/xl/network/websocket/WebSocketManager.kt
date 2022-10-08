package com.caspar.xl.network.websocket

import android.annotation.SuppressLint
import android.text.TextUtils
import com.caspar.base.utils.log.LogUtil
import com.caspar.base.utils.log.LogUtil.e
import com.caspar.base.utils.log.LogUtil.i
import com.caspar.base.utils.log.LogUtil.json
import com.caspar.xl.config.ApiConfig
import okhttp3.*
import okio.ByteString
import java.security.*
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * 基于服务端的WebSocket技术,客户端勿用
 */
class WebSocketManager private constructor() {
    private var client: OkHttpClient? = null  //请求辅助类
    private var request: Request? = null //请求
    private var mWebSocket: WebSocket? = null //WebSocket主要类
    private var isConnect = false //是否连接
    private var connectNum = 0 //当前连接次数
    private var sendMessage: String? = null //断开连接时发送的命令
    private var isStart = false //是否正在连接
    private var url: String? = null

    fun init(url: String, message: IReceiveMessage?) {
        try {
            this.url = url
            val trustManager: X509TrustManager =
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                        /*if(chain.isNullOrEmpty()){
                           e("checkServerTrusted x509Certificates is null ");
                        }
                        for (cert in chain) {
                            cert.checkValidity()
                            try {
//                                cert.verify(serverCertificate.getPublicKey())
                                LogUtil.e("ToString:${cert}")//签名证书的数据
                                LogUtil.e("subjectDN:${cert.subjectDN.name}")//签名证书的Mac地址或是ip地址
                                LogUtil.e("subjectX500Principal:${cert.subjectX500Principal}")//签名证书的Mac地址或是ip地址
                                LogUtil.e("publicKeyString:${cert.publicKey.toString()}")//签名证书的publicKey
                                val publicKeyString = cert.publicKey.toString()
                                val modulus =publicKeyString.substring(
                                    publicKeyString.indexOf("modulus") + 8,
                                    publicKeyString.indexOf(",")
                                )
                                val exponent = publicKeyString.substring(
                                    publicKeyString.indexOf("publicExponent")+15,
                                    publicKeyString.indexOf("}")
                                )
                                val key=Utils.getPublicKey(modulus,exponent)//签名证书的publicKey
                                LogUtil.e("publicKey:${key.toString()}")
                                LogUtil.e("signature:${Utils.bytesToHexString(cert.signature)}")
                            } catch (e: NoSuchAlgorithmException) {
                                e.printStackTrace()
                                LogUtil.e(e)
                            } catch (e: InvalidKeyException) {
                                e.printStackTrace()
                                LogUtil.e(e)
                            } catch (e: NoSuchProviderException) {
                                e.printStackTrace()
                                LogUtil.e(e)
                            } catch (e: SignatureException) {
                                e.printStackTrace()
                                LogUtil.e(e)
                            }
                        }*/
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                }
//            val sslContext = SSLContext.getInstance("SSL")   //目前SSL貌似会导致偶发性的连接失败
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(
                null,
                arrayOf<TrustManager>(trustManager),
                SecureRandom()
            )
            val sslSocketFactory = sslContext.socketFactory
            client = OkHttpClient.Builder()
                .writeTimeout(10, TimeUnit.SECONDS) //写入超时
                .readTimeout(10, TimeUnit.SECONDS) //读取超时
                .connectTimeout(10, TimeUnit.SECONDS) //连接超时规定时间
                .retryOnConnectionFailure(true) //重连操作，但是感觉没啥用。
                .sslSocketFactory(sslSocketFactory, trustManager) //SSL协议证书，同意所有协议网络请求，否则可能不支持http请求
                .hostnameVerifier { _, _ -> true }
                .build()
            request = Request.Builder().url(url)
                .addHeader("Sec-WebSocket-Key", "1234") //WebSocket必须要的请求头
                .addHeader("Authorization", "请自行使用对应的验证")
                .build()
            receiveMessage = message
            connect()
        } catch (b: Exception) {
            e(b.toString() + "失败了")
            b.printStackTrace()
        }
    }

    /**
     * 连接
     */
    private fun connect() {
        if (isConnect()) {
            //连接中就没必要重连了
            return
        }
        if (!isStart) {
            e("Web Socket开始连接")
            isStart = true
            client?.newWebSocket(request!!, createListener())
        } else {
            e("已经有一个正在重连")
        }
    }

    /**
     * 重连
     */
    private fun reconnect() {
        if (connectNum <= MAX_NUM) {
            try {
                Thread.sleep(MILLIS.toLong())
                isStart = false //这里重置一下状态，避免多次点击数据不对
                connect()
                connectNum++
            } catch (e: InterruptedException) {
                e(e.message)
                e.printStackTrace()
            }
        } else {
            isStart = false
            connectNum = 0
            e("通过 " + MAX_NUM + "次重连后失败,请检查url或网络")
            receiveMessage?.onConnectFailed("max")
        }
    }

    /**
     * 是否连接
     */
    fun isConnect(): Boolean {
        return mWebSocket != null && isConnect
    }

    /**
     * 发送消息
     *
     * @param text 字符串
     * @return boolean
     */
    fun sendMessage(text: String): Boolean {
        sendMessage = text
        if (!isConnect()) {
            url?.apply {
                init(ApiConfig.BaseUrl, receiveMessage)
            }
            LogUtil.e("发送失败，可能是因为未连接,url:${url}")
            return false
        }
        json(text)
        return mWebSocket!!.send(text)
    }

    /**
     * 发送消息
     *
     * @param byteString 字符集
     * @return boolean
     */
    fun sendMessage(byteString: ByteString): Boolean {
        return if (!isConnect()) false else mWebSocket!!.send(byteString)
    }

    /**
     * 关闭连接
     */
    fun close() {
        if (isConnect()) {
            e("客户端主动关闭连接")
            mWebSocket?.cancel()
            mWebSocket?.close(1000, "客户端主动关闭连接")
        }
        receiveMessage = null//接口回调置空，否则会内存溢出
    }

    private fun createListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                e("Open WebSocket:$response")
                mWebSocket = webSocket
                isConnect = response.code == 101 //101证明连接成功
                if (!isConnect) {
                    reconnect()
                } else {
                    connectNum = 0
                    isStart = false //这里重置一下状态，避免切换网络重新连接其它失败
                    e("Connect Success")
                    receiveMessage?.onConnectSuccess()
                    sendMessage?.apply {
                        sendMessage(this)
                    }
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                if (!TextUtils.isEmpty(text) && text.contains("{") && text.contains("}")) {
                    sendMessage = null

                } else {
                    i("服务器返回非json数据:$text")
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                e("长连接返回的数据:$bytes")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                e("onClosing：远程不再传入消息，连接断开")
                mWebSocket = null
                isConnect = false
                receiveMessage?.onClose()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                e("onClosed：连接已成功释放,两端都不再发送消息")
                mWebSocket = null
                isConnect = false
                receiveMessage?.onClose()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                e("Socket连接失败,错误原因:")
                e(t)
                isConnect = false
                mWebSocket = null //出现了异常，就关闭掉本次Socket，根据情况可能会重连
                if ("Socket closed" == t.message) { //Socket手动关闭后极大概率走这个回调，所以在手动关闭时就不进行重连，否则可能会出现莫名的bug
                    receiveMessage?.onClose()
                } else {
                    receiveMessage?.onConnectFailed(t.message)
                    reconnect()
                }
            }
        }
    }

    companion object {
        private const val MAX_NUM = 5 // 最大重连数
        private const val MILLIS = 3000 // 重连间隔时间，毫秒
        var receiveMessage: IReceiveMessage? = null

        val manager: WebSocketManager by lazy {
            WebSocketManager()
        }
    }
}