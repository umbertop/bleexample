package me.palazzini.bleexample.domain.repository

import kotlinx.coroutines.flow.Flow
import me.palazzini.bleexample.domain.model.BleDeviceScanResult

interface BleScanner {
    fun enableBle()
    fun isBleEnabled(): Boolean
    fun observeDevices(): Flow<BleDeviceScanResult>

    companion object {
        const val BLE_DEVICE_NAME = "ILLUSIO_GATTS"
    }
}