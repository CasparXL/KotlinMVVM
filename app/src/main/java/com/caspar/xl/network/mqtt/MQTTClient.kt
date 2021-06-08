package com.caspar.xl.network.mqtt
/**
 * Mqtt请求相关的工具类
 * build.gradle
 * maven {
 *  url "https://repo.eclipse.org/content/repositories/paho-snapshots/"
 * }
 * 依赖导入
 * implementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0'
 * implementation 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
 * AndroidManifest.xml
 * 权限：
 * <uses-permission android:name="android.permission.WAKE_LOCK" />
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * 服务：
 * <service android:name="org.eclipse.paho.android.service.MqttService" />
 *
 */
/*
import com.caspar.base.helper.LogUtil
import com.caspar.xl.app.BaseApplication
import kotlinx.coroutines.flow.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MQTTClient {
    companion object {
        val flow :MutableStateFlow<MQTTMessage?> = MutableStateFlow(null)
        @Volatile
        private var INSTANCE: MQTTClient? = null

        fun getInstance(): MQTTClient {
            return INSTANCE ?: synchronized(this) {
                val instance = MQTTClient()
                INSTANCE = instance
                instance
            }
        }
    }

    private var mqttClient = MqttAndroidClient(BaseApplication.context, "tcp://xxxx:xxx", "xxxxx")

    //mqtt收发数据的回调
    private val mqttResponse:MqttCallback = object :MqttCallback{
        override fun connectionLost(cause: Throwable?) {
            LogUtil.e("connectionLost")
            cause?.apply {
                LogUtil.e(this)
            }
        }
        //正常连接成功后，订阅成功，发数据将会从此处回调
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            flow.value = MQTTMessage(message = message!!,topic = topic!!) //通过flow发送出去，订阅过flow的界面将会收到mqtt的数据
        }
        //正常连接成功后，未订阅，发数据将会从此处接收到
        override fun deliveryComplete(token: IMqttDeliveryToken?) {

        }
    }


    fun connect(username: String = "", password: String = "") {
        mqttClient.setCallback(mqttResponse)
        val options = MqttConnectOptions()
        options.userName = username
        options.password = password.toCharArray()

        try {
            mqttClient.connect(options, null, MQTTListener.mqttConnectionListener)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String, qos: Int = 1) {
        try {
            mqttClient.subscribe(topic, qos, null, MQTTListener.mqttSubscribeListener)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String) {
        try {
            mqttClient.unsubscribe(topic, null, MQTTListener.mqttUnsubscribeListener)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, MQTTListener.mqttPublishListener)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect(null, MQTTListener.mqttDisconnectListener)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    inner class MQTTMessage(val message:MqttMessage,val topic: String)
}*/
