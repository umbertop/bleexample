package me.palazzini.bleexample.app_domain.model

import android.bluetooth.BluetoothDevice

data class BleDeviceScanResult(
    val name: String,
    val address: String,
    val device: BluetoothDevice
)