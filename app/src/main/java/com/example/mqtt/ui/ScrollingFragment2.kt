package com.example.mqtt.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.mqtt.MqttViewModel
import com.example.mqtt.R
import com.example.mqtt.databinding.FragmentScrolling2Binding
import com.skydoves.colorpickerview.listeners.ColorListener


class ScrollingFragment2 : Fragment() {
    private lateinit var binding: FragmentScrolling2Binding
    private val viewModelClient: MqttViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScrolling2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelClient.powerOn.observe(viewLifecycleOwner) {
            binding.connectionImage.isEnabled = true
            viewModelClient.updateConnectionText(power = it, connectionText = binding.connectionStatusText)
        }

        binding.connectionImage.setOnClickListener{
            it.isEnabled = false
            viewModelClient.mqttConnect()
        }

        binding.colorWheel.attachAlphaSlider(binding.brigSlideBar)
        binding.colorWheel.setColorListener(object : ColorListener {
            override fun onColorSelected(color: Int, fromUser: Boolean) {
                Log.d("APP_DEBUGGER", "Color hex = ${String.format("#%06X", 0xFFFFFF and binding.colorWheel.pureColor)}")
                Log.d("APP_DEBUGGER", "Color full = ${String.format("#%06X", 0xFFFFFFFF and binding.colorWheel.color.toLong())}")
                viewModelClient.setColorValues(String.format("#%06X", 0xFFFFFFFF and binding.colorWheel.color.toLong()))
            }
        })

        binding.colorSendButton.setOnClickListener {
            viewModelClient.mqttSendColor()
        }

        binding.arrowBackwards.setOnClickListener {
            requireView().findNavController()
                .navigate(R.id.action_scrollingFragment2_to_scrollingFragment)
        }
    }
}