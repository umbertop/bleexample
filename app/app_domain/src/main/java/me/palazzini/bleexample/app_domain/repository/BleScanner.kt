package me.palazzini.bleexample.app_domain.repository

import kotlinx.coroutines.flow.Flow
import no.nordicsemi.android.support.v18.scanner.ScanResult

interface BleScanner {
    fun isBleEnabled(): Boolean
    fun observeDevices(): Flow<ScanResult>
}