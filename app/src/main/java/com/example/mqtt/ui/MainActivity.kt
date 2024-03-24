package com.example.mqtt.ui


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mqtt.MqttRepository
import com.example.mqtt.MqttViewModel
import com.example.mqtt.data.Constants.MQTT_CLIENT_ID
import com.example.mqtt.data.Constants.MQTT_SERVER_PORT
import com.example.mqtt.data.Constants.MQTT_SERVER_URI
import com.example.mqtt.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val viewModelClient: MqttViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelClient.mqttServer = MqttRepository(this, "$MQTT_SERVER_URI:$MQTT_SERVER_PORT", MQTT_CLIENT_ID)
        viewModelClient.mqttConnect()
    }
}
