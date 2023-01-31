package com.caspar.xl.network

import android.annotation.SuppressLint
import com.caspar.xl.BuildConfig
import com.caspar.xl.config.ApiConfig
import com.caspar.xl.network.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.*
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * "CasparXL" 创建 2019/7/1.
 * 界面名称以及功能:网络请求Retrofit2+Coroutine(协程)
 */
object Api {
    private const val DEFAULT_TIMEOUT: Long = 30

    // Install the all-trusting trust manager TLS
    val okhttpHeader: OkHttpClient
        get() = try {
            // Install the all-trusting trust manager TLS
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val okBuilder = OkHttpClient.Builder()
                .apply {
                    sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    addInterceptor(AuthInterceptor())
                    addInterceptor(
                        if (BuildConfig.LOG_ENABLE) HttpLoggingInterceptor().setLevel(
                            HttpLoggingInterceptor.Level.HEADERS
                        ) else HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                    )
                    hostnameVerifier { _, _ -> true }
                }
            okBuilder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    // Install the all-trusting trust manager TLS
    val okhttpLogBody: OkHttpClient
        get() = try {
            // Install the all-trusting trust manager TLS
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val okBuilder = OkHttpClient.Builder()
                .apply {
                    sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    addInterceptor(AuthInterceptor())
                    addInterceptor(
                        if (BuildConfig.LOG_ENABLE) HttpLoggingInterceptor().setLevel(
                            HttpLoggingInterceptor.Level.BODY
                        ) else HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                    )
                    hostnameVerifier { _, _ -> true }
                }
            okBuilder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            /*for (cert in chain) {
                cert.checkValidity()
                try {
                    LogUtil.e("ToString:${cert}")//签名证书的数据
                    LogUtil.e("subjectDN:${cert.subjectDN.name}")//签名证书的Mac地址或是ip地址
                    LogUtil.e("subjectX500Principal:${cert.subjectX500Principal}")//签名证书的Mac地址或是ip地址
                    LogUtil.e("publicKeyString:${cert.publicKey.toString()}")//签名证书的publicKey
                    val publicKeyString = cert.publicKey.toString()
                    val modulus = publicKeyString.substring(
                        publicKeyString.indexOf("modulus") + 8,
                        publicKeyString.indexOf(",")
                    )
                    val exponent = publicKeyString.substring(
                        publicKeyString.indexOf("publicExponent") + 15,
                        publicKeyString.indexOf("}")
                    )
                    val key = Utils.getPublicKey(modulus, exponent)//签名证书的publicKey
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

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    })

}
