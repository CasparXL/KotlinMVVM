package com.caspar.xl.network.websocket

/**
 * Socket通信接口
 */
interface IReceiveMessage {
    fun onConnectSuccess() // 连接成功
    fun onConnectFailed(max: String?) // 连接失败
    fun onClose() // 关闭
}