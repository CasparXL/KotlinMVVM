package com.caspar.xl.network.util

import com.caspar.base.R
import com.caspar.xl.app.BaseApplication

/**
 * "CasparXL" 创建 2019/10/23.
 * 界面名称以及功能:
 */
object NetException {
    //网络连接失败,请检查网络
    @JvmField
    val CONNECT_ERROR: String =
        BaseApplication.context.getString(R.string.connect_error)

    //连接超时,请稍后再试
    @JvmField
    val CONNECT_TIMEOUT: String =
        BaseApplication.context.getString(R.string.connect_timeout)

    //服务器异常
    @JvmField
    val BAD_NETWORK: String = BaseApplication.context.getString(R.string.bad_network)

    //解析服务器响应数据失败
    @JvmField
    val PARSE_ERROR: String = BaseApplication.context.getString(R.string.parse_error)

    //未知错误
    @JvmField
    val UNKNOWN_ERROR: String =
        BaseApplication.context.getString(R.string.unknown_error)

    //服务器返回数据失败
    val RESPONSE_RETURN_ERROR: String =
        BaseApplication.context.getString(R.string.response_return_error)
}