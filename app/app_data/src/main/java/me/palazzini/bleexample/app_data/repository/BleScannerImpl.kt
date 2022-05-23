package me.palazzini.bleexample.app_data.repository

import android.bluetooth.BluetoothManager
import android.content.Context
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.palazzini.bleexample.app_domain.repository.BleScanner
import no.nordicsemi.android.support.v18.scanner.*

class BleScannerImpl(
    private val context: Context,
    private val bluetoothManager: BluetoothManager
) : BleScanner {

    private val scanner: BluetoothLeScannerCompat = BluetoothLeScannerCompat.getScanner()

    override fun isBleEnabled(): Boolean = bluetoothManager.adapter.isEnabled

    override fun observeDevices(): Flow<ScanResult> = callbackFlow {
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                trySend(result).onFailure {}
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { trySend(it) }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
            }
        }

        val filters = listOf(
            ScanFilter.Builder().setDeviceName(BLE_DEVICE_NAME).build()
        )

        val settings = ScanSettings.Builder()
            .setLegacy(false)
            .setReportDelay(5000)
            .setUseHardwareBatchingIfSupported(true)
            .setUseHardwareFilteringIfSupported(true)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner.startScan(filters, settings, scanCallback)

        awaitClose {
            scanner.stopScan(scanCallback)
        }
    }

    companion object {
        const val BLE_DEVICE_NAME = "ILLUSIO_GATTS"
    }
}