package com.example.mqtt.data

object Constants {
    const val MQTT_SERVER_URI = "tcp://wqtt.ru"
    const val MQTT_SERVER_PORT = "5031"
    const val MQTT_CLIENT_ID = "xdtcgfvgyjuhijokcfghjkj"

    const val MQTT_USER_NAME = "123"
    const val MQTT_USER_PASSWORD = "123"

    private const val MQTT_TOPIC_MAIN = "airrefresher"

    const val MQTT_TOPIC_POWER = "$MQTT_TOPIC_MAIN/power"

    const val MQTT_TOPIC_COLOR = "$MQTT_TOPIC_MAIN/led/color"
    const val MQTT_TOPIC_BRIGHTNESS = "$MQTT_TOPIC_MAIN/led/brig"

    const val MQTT_TOPIC_TEMPERATURE = "$MQTT_TOPIC_MAIN/sensor_t_h/temp"
    const val MQTT_TOPIC_HUMIDITY = "$MQTT_TOPIC_MAIN/sensor_t_h/hum"

    val MQTT_TOPIC_LIST = listOf(MQTT_TOPIC_POWER, MQTT_TOPIC_COLOR, MQTT_TOPIC_BRIGHTNESS, MQTT_TOPIC_TEMPERATURE, MQTT_TOPIC_HUMIDITY)
}