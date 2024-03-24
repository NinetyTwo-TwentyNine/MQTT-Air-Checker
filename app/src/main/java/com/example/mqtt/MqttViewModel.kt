package com.example.mqtt

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mqtt.data.Constants.MQTT_TOPIC_BRIGHTNESS
import com.example.mqtt.data.Constants.MQTT_TOPIC_COLOR
import com.example.mqtt.data.Constants.MQTT_TOPIC_HUMIDITY
import com.example.mqtt.data.Constants.MQTT_TOPIC_LIST
import com.example.mqtt.data.Constants.MQTT_TOPIC_TEMPERATURE
import com.example.mqtt.data.Constants.MQTT_USER_NAME
import com.example.mqtt.data.Constants.MQTT_USER_PASSWORD
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.IllegalStateException
import java.lang.Long.parseLong

class MqttViewModel(): ViewModel() {
    lateinit var mqttServer: MqttRepository
    var tempText: MutableLiveData<String> = MutableLiveData()
    var humidText: MutableLiveData<String> = MutableLiveData()
    var powerOn: MutableLiveData<Boolean> = MutableLiveData(false)
    var colorVal: String = "#FFFFFF"
    var brigVal: Int = 255

    fun mqttConnect() {
        mqttServer.connect(MQTT_USER_NAME, MQTT_USER_PASSWORD,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    MQTT_TOPIC_LIST.forEach {
                        mqttSubscribe(it)
                    }
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                }
            },
            object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    if(topic == MQTT_TOPIC_TEMPERATURE) {
                        powerOn.value = true
                        updateLiveData(tempText, "$message°C")
                    }
                    if(topic == MQTT_TOPIC_HUMIDITY) {
                        powerOn.value = true
                        updateLiveData(humidText, "$message%")
                    }
                }

                override fun connectionLost(cause: Throwable?) {}
                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })
    }

    private fun <T> updateLiveData(liveData: MutableLiveData<T>, message: T) {
        liveData.value = message
        liveData.postValue(message)
    }


    fun mqttSubscribe(topic: String) {
        if (mqttServer.isConnected()) {
            mqttServer.subscribe(topic,
                1,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {}
                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {}
                })
        }
    }

    fun mqttPublish(topic: String, message: String) {
        if (mqttServer.isConnected()) {
            mqttServer.publish(topic,
                message,
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {}
                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {}
                })
        }
    }

    fun mqttSendColor(hexColor: String = colorVal) {
        mqttServer.publish(MQTT_TOPIC_COLOR, hexColor)
        mqttServer.publish(MQTT_TOPIC_BRIGHTNESS, brigVal.toString())
    }

    fun mqttUnsubscribe(topic: String) {
        if (mqttServer.isConnected()) {
            mqttServer.unsubscribe( topic,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {}
                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {}
                })
        }
    }

    fun mqttDisconnect() {
        if (mqttServer.isConnected()) {
            mqttServer.disconnect(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {}
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {}
            })
        }
    }

    fun updateConnectionText(power: Boolean = powerOn.value!!, connectionText: TextView) {
        if (power) {
            connectionText.text = "Подключено"
        } else {
            connectionText.text = "Отключено"
        }
    }

    fun setColorValues(hexColor: String) {
        when (hexColor.length) {
            7 -> {
                brigVal = 0
                colorVal = hexColor
            }
            8,9 -> {
                brigVal = parseLong(hexColor.substring(1, hexColor.length-6), 16).toInt()
                colorVal = "#${hexColor.substring(hexColor.length-6, hexColor.length)}"
            }
            else -> {
                throw(IllegalStateException("Wrong hex color format: $hexColor."))
            }
        }
        Log.d("APP_DEBUGGER", "Color determination: brigVal = $brigVal, colorVal = $colorVal")
    }
}