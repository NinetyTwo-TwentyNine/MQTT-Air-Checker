package com.example.mqtt

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mqtt.data.Constants.MQTT_TOPIC_BRIGHTNESS
import com.example.mqtt.data.Constants.MQTT_TOPIC_COLOR
import com.example.mqtt.data.Constants.MQTT_TOPIC_HUMIDITY
import com.example.mqtt.data.Constants.MQTT_TOPIC_LIST
import com.example.mqtt.data.Constants.MQTT_TOPIC_POWER
import com.example.mqtt.data.Constants.MQTT_TOPIC_TEMPERATURE
import com.example.mqtt.data.Constants.MQTT_USER_NAME
import com.example.mqtt.data.Constants.MQTT_USER_PASSWORD
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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
    var connectionOn: MutableLiveData<Boolean> = MutableLiveData(false)
    var steamOn: MutableLiveData<Boolean> = MutableLiveData(false)
    private var colorVal: String = "#FFFFFF"
    private var brigVal: Int = 255

    fun mqttConnect() {
        mqttServer.connect(MQTT_USER_NAME, MQTT_USER_PASSWORD,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    MQTT_TOPIC_LIST.forEach {
                        mqttSubscribe(it)
                    }
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    updateLiveData(connectionOn, false)
                    Log.d("MQTT_DEBUGGER", "MQTT connection was failed.")
                    exception?.printStackTrace()

                    try {
                        mqttConnect()
                    } catch (e: Exception) {
                        Log.d("MQTT_DEBUGGER", "MQTT connection function call failed.")
                        e.printStackTrace()
                    }
                }
            },
            object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    when (topic) {
                        MQTT_TOPIC_POWER -> {
                            updateLiveData(steamOn, message.toString().toBoolean())
                        }
                        MQTT_TOPIC_TEMPERATURE -> {
                            updateLiveData(connectionOn, true, launchCallback = false)
                            updateLiveData(tempText, "$message°C")
                        }
                        MQTT_TOPIC_HUMIDITY -> {
                            updateLiveData(connectionOn, true, launchCallback = false)
                            updateLiveData(humidText, "$message%")
                        }
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d("MQTT_DEBUGGER", "MQTT connection was lost.")
                    cause?.printStackTrace()
                    try {
                        mqttConnect()
                    } catch (e: Exception) {
                        Log.d("MQTT_DEBUGGER", "MQTT connection function call failed.")
                        e.printStackTrace()
                    }
                }
                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })
    }

    private fun <T> updateLiveData(liveData: MutableLiveData<T>, message: T, launchCallback: Boolean = true) {
        MainScope().launch {
            liveData.value = message
            if (launchCallback) {
                liveData.postValue(message)
            }
        }
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

    fun updateConnectionText(connection: Boolean = connectionOn.value!!, connectionText: TextView) {
        if (connection) {
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