package me.palazzini.bleexample.app_domain.repository

import kotlinx.coroutines.flow.Flow
import me.palazzini.bleexample.app_domain.model.BleDeviceScanResult
import no.nordicsemi.android.support.v18.scanner.ScanResult

interface BleScanner {
    fun enableBle()
    fun isBleEnabled(): Boolean
    fun observeDevices(): Flow<BleDeviceScanResult>
}