package me.palazzini.bleexample.data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.palazzini.bleexample.domain.model.BleDeviceScanResult
import me.palazzini.bleexample.domain.repository.BleScanner
import no.nordicsemi.android.support.v18.scanner.*
import timber.log.Timber

class NordicBleScanner(
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
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                Timber.d(
                    "Device (%s) - %s",
                    result.scanRecord?.deviceName ?: result.device.name ?: "No Name",
                    result.device.address
                )

                val scanResult = BleDeviceScanResult(
                    name = result.scanRecord?.deviceName ?: result.device.name ?: "",
                    address = result.device.address,
                    device = result.device
                )

                trySend(scanResult).onFailure {
                    it?.printStackTrace()
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                Timber.d("Found %d devices", results.size)

                results.forEach { result ->
                    Timber.d(
                        "Device (%s) - %s",
                        result.scanRecord?.deviceName ?: "No Name",
                        result.device.address
                    )

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
            ScanFilter.Builder().setDeviceName(BleScanner.BLE_DEVICE_NAME).build()
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
}