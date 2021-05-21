package com.caspar.xl.helper

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.DiscoveryListener
import android.net.nsd.NsdManager.ResolveListener
import android.net.nsd.NsdServiceInfo
import android.os.SystemClock
import com.caspar.base.helper.LogUtil
import com.caspar.base.helper.LogUtil.e
import com.caspar.base.helper.LogUtil.i
import com.caspar.xl.app.BaseApplication
import com.caspar.xl.network.util.GsonUtils
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * 获取gateway网关列表
 */
class NsdHelper {
    //网关的管理工具
    private var mNsdManager: NsdManager? = null

    //扫描网关的数据
    private var mDiscoveryListener: DiscoveryListener? = null

    //错误解析的次数，三次以后就解析失败。
    private var errorTimes = 0
    private val SERVICE_TYPE = "_http._tcp."
    val map: Map<String, String> = mapOf()

    /**
     * 启动nsd扫描
     */
    fun startNsdClient() {
        unregisterNsdService()
        initDiscoveryListener()
        mNsdManager = BaseApplication.context.getSystemService(Context.NSD_SERVICE) as NsdManager
        mNsdManager?.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener)

    }

    private fun initDiscoveryListener() {
        mDiscoveryListener = object : DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                mNsdManager!!.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                mNsdManager!!.stopServiceDiscovery(this)
            }

            override fun onDiscoveryStarted(serviceType: String) {
                e("开始扫描设备" + Thread.currentThread())
            }

            override fun onDiscoveryStopped(serviceType: String) {
                i("Discovery stopped: $serviceType")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                BaseApplication.job.launch(Dispatchers.Main) {
                    if (SERVICE_TYPE == serviceInfo.serviceType) {
                        e("onServiceFound:" + serviceInfo.serviceName)
                        mNsdManager?.resolveService(serviceInfo, resolveListener())
                        SystemClock.sleep(300)
                    }
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                e("service lost：$serviceInfo")
            }
        }

    }

    private fun resolveListener(): ResolveListener {
        return object : ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                i("解析失败设备" + Thread.currentThread())
                e("onResolveFailed:$errorCode")
                errorTimes++
                if (errorTimes > 3) return  //解析失败超过3次不再解析
                // 解析
                mNsdManager!!.resolveService(serviceInfo, this)
                when (errorCode) {
                    NsdManager.FAILURE_ALREADY_ACTIVE -> e("FAILURE ALREADY ACTIVE")
                    NsdManager.FAILURE_INTERNAL_ERROR -> e("FAILURE_INTERNAL_ERROR")
                    NsdManager.FAILURE_MAX_LIMIT -> e("FAILURE_MAX_LIMIT")
                }
                //                post(RxGateWay(false, null))
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                val s: String? = if (serviceInfo.attributes["mac"] != null) {
                    serviceInfo.attributes["mac"]?.let { String(it) }
                } else {
                    getClientList(serviceInfo.host.hostAddress)
                }
                i("扫描到了设备" + serviceInfo.serviceName + "[" + serviceInfo.serviceType + "]网关地址:https://" + serviceInfo.host.hostAddress + ":" + serviceInfo.port + "  Mac地址:" + s)
                errorTimes = 0 //解析成功后复位计数
                /*val gateWayBean = GateWayBean(
                    s,
                    serviceInfo.serviceName.toUpperCase(),
                    serviceInfo.serviceType,
                    "https://" + serviceInfo.host.hostAddress,
                    serviceInfo.port.toString() + "/",
                    "https://" + serviceInfo.host.hostAddress + ":" + serviceInfo.port + "/",
                    false
                )
                post(
                    RxGateWay(
                        true,
                        gateWayBean
                    )
                ) */
                //这里可能需要延时发送，所以会导致解析不完整的情况，因此，工具类提供了next方法，确保解析完整
            }
        }
    }

    //无法通过手动获取Mac地址，则通过模拟的虚拟机来获取对应的Mac地址
    //https://blog.csdn.net/Elsa_Rong/article/details/47136725
    fun getClientList(ip: String): String? {
        var br: BufferedReader? = null
        var result: String? = null
        try {
            br = BufferedReader(FileReader("/proc/net/arp"))
            var line: String
            while (!br.readLine().also { line = if (it.isNullOrEmpty()) "" else it.trim() }.isNullOrEmpty()) {
                val splitted: List<String> = line.split(" ").filter { it.isNotEmpty() }
                e("系统api获取不到，手动查找数据:" + GsonUtils.toJson(splitted))
                splitted.apply {
                    if (this.size >= 4) {
                        // Basic sanity check
                        val mac = this[3]
                        if (mac.matches(Regex("..:..:..:..:..:..")) && ip == this[0]) {
                            result = mac.toUpperCase(Locale.ROOT).replace(":", "")
                            return result
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e(e)
        } finally {
            try {
                br?.close()
                e("关闭了流")
            } catch (e: IOException) {
                e(e)
            }
        }
        return result
    }

    //取消注册
    private fun unregisterNsdService() {
        mNsdManager?.let {
            mNsdManager?.stopServiceDiscovery(mDiscoveryListener)
            mNsdManager = null
            mDiscoveryListener = null
            errorTimes = 0
        }
    }

    fun stopJob() {
        unregisterNsdService()
    }

}