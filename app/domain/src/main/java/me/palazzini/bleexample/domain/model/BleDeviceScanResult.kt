package me.palazzini.bleexample.domain.model

import android.bluetooth.BluetoothDevice

data class BleDeviceScanResult(
    val name: String,
    val address: String,
    val device: BluetoothDevice
)