package me.palazzini.bleexample.app_data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.palazzini.bleexample.app_domain.model.BleDeviceScanResult
import me.palazzini.bleexample.app_domain.repository.BleScanner
import no.nordicsemi.android.support.v18.scanner.*

class BleScannerImpl(
    bluetoothManager: BluetoothManager
) : BleScanner {

    private val scanner: BluetoothLeScannerCompat = BluetoothLeScannerCompat.getScanner()
    private val adapter: BluetoothAdapter = bluetoothManager.adapter

    @SuppressLint("MissingPermission")
    override fun enableBle() {
        adapter.enable()
    }

    override fun isBleEnabled(): Boolean = adapter.isEnabled

    override fun observeDevices(): Flow<BleDeviceScanResult> = callbackFlow {
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val scanResult = BleDeviceScanResult(
                    name = result.scanRecord?.deviceName ?: "",
                    address = result.device.address,
                    device = result.device
                )

                trySend(scanResult).onFailure {
                    it?.printStackTrace()
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { result ->
                    val scanResult = BleDeviceScanResult(
                        name = result.scanRecord?.deviceName ?: "",
                        address = result.device.address,
                        device = result.device
                    )

                    trySend(scanResult).onFailure {
                        it?.printStackTrace()
                    }
                }
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