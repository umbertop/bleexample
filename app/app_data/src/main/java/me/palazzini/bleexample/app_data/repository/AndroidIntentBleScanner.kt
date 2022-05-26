package me.palazzini.bleexample.app_data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.palazzini.bleexample.app_domain.model.BleDeviceScanResult
import me.palazzini.bleexample.app_domain.repository.BleScanner
import timber.log.Timber

class AndroidIntentBleScanner(
    private val context: Context,
    private val bluetoothManager: BluetoothManager
) : BleScanner {

    private val adapter: BluetoothAdapter = bluetoothManager.adapter

    @SuppressLint("MissingPermission")
    override fun enableBle() {
        adapter.enable()
    }

    override fun isBleEnabled(): Boolean = adapter.isEnabled

    @SuppressLint("MissingPermission")
    override fun observeDevices(): Flow<BleDeviceScanResult> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                                ?: return

                        if(device.name != BleScanner.BLE_DEVICE_NAME) return

                        Timber.d("Device (%s) - %s", device.name ?: "No Name", device.address)

                        val scanResult = BleDeviceScanResult(
                            name = device.name ?: "",
                            address = device.address,
                            device = device
                        )

                        trySend(scanResult).onFailure { it?.printStackTrace() }
                    }
                }
            }
        }

        context.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        adapter.startDiscovery()

        awaitClose {
            adapter.cancelDiscovery()

            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}