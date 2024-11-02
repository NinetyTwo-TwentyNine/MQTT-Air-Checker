package com.example.mqtt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.mqtt.MqttViewModel
import com.example.mqtt.R
import com.example.mqtt.data.Constants.MQTT_TOPIC_POWER
import com.example.mqtt.databinding.FragmentScrollingBinding


class ScrollingFragment : Fragment() {

    private lateinit var binding: FragmentScrollingBinding
    private val viewModelClient: MqttViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScrollingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelClient.connectionOn.observe(viewLifecycleOwner) {
            viewModelClient.updateConnectionText(connection = it, connectionText = binding.connectionStatusText)
        }

        viewModelClient.tempText.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.temperatureText.text = it
                viewModelClient.updateConnectionText(connectionText = binding.connectionStatusText)
            }
        }
        viewModelClient.humidText.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.humidityText.text = it
                viewModelClient.updateConnectionText(connectionText = binding.connectionStatusText)
            }
        }

        viewModelClient.steamOn.observe(viewLifecycleOwner) {
            binding.powerImage.isEnabled = true
        }
        binding.powerImage.setOnClickListener{
            it.isEnabled = false
            viewModelClient.mqttPublish(MQTT_TOPIC_POWER, "${!viewModelClient.steamOn.value!!}")
        }

        binding.arrowForward.setOnClickListener {
            requireView().findNavController()
                .navigate(R.id.action_scrollingFragment_to_scrollingFragment2)
        }
    }
}