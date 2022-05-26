package me.palazzini.bleexample.app_data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.palazzini.bleexample.app_domain.model.BleDeviceScanResult
import me.palazzini.bleexample.app_domain.repository.BleScanner
import timber.log.Timber

class AndroidBleScanner(
    bluetoothManager: BluetoothManager
) : BleScanner {

    private val adapter: BluetoothAdapter = bluetoothManager.adapter
    private val scanner: BluetoothLeScanner? = adapter.bluetoothLeScanner

    @SuppressLint("MissingPermission")
    override fun enableBle() {
        adapter.enable()
    }

    override fun isBleEnabled(): Boolean = adapter.isEnabled

    @SuppressLint("MissingPermission")
    override fun observeDevices(): Flow<BleDeviceScanResult> = callbackFlow {
        val scanCallback = object : ScanCallback() {
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
        }

        val filters = listOf(
            ScanFilter.Builder().setDeviceName(BleScanner.BLE_DEVICE_NAME).build()
        )

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(0)
            .build()

        scanner?.startScan(filters, settings, scanCallback)

        awaitClose {
            scanner?.stopScan(scanCallback)
        }
    }
}