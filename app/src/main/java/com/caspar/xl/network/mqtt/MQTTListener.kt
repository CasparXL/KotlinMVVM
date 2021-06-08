package com.caspar.xl.network.mqtt
/*

import com.caspar.base.helper.LogUtil
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken

object MQTTListener {
    //连接所用的listener
    val mqttConnectionListener: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) { LogUtil.d("mqttConnectionListener success") }
        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) { exception?.apply { LogUtil.e(this) } }
    }
    //订阅所用的listener
    val mqttSubscribeListener: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            LogUtil.d("mqttSubscribeListener success")
        }
        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            exception?.apply { LogUtil.e(this) }
        }
    }
    //取消订阅所用的listener
    val mqttUnsubscribeListener: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) { LogUtil.d("mqttUnsubscribeListener success") }
        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) { exception?.apply { LogUtil.e(this) } }
    }
    //发送数据所用的listener
    val mqttPublishListener: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) { LogUtil.d("mqttPublishListener success") }
        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) { exception?.apply { LogUtil.e(this) } }
    }
    //断开连接所用的listener
    val mqttDisconnectListener: IMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) { LogUtil.d("mqttDisconnectListener success") }
        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) { exception?.apply { LogUtil.e(this) } }
    }
}*/
