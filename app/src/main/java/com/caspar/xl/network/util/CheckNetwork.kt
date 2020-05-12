package com.caspar.xl.network.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * 用于判断是不是联网状态
 *
 * @author Dzy
 */
object CheckNetwork {
    /**
     * 判断网络是否连通
     */
    fun isNetworkConnected(context: Context?): Boolean {
        return if (context != null) {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            info != null && info.isConnected
        } else {
            /**如果context为空，就返回false，表示网络未连接 */
            false
        }
    }

    fun isWifiConnected(context: Context?): Boolean {
        return if (context != null) {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            info != null && info.type == ConnectivityManager.TYPE_WIFI
        } else {
            /**如果context为null就表示为未连接 */
            false
        }
    }
}