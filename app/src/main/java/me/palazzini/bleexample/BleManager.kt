package me.palazzini.bleexample

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import java.util.*

/*
class AppBleManager(
    private val ctx: Context,
    private val scope: CoroutineScope
) : BleManager(ctx) {

    private var writeChar: BluetoothGattCharacteristic? = null

    override fun getMinLogPriority(): Int {
        return Log.VERBOSE
    }

    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return AppGattCallback()
    }

    private inner class AppGattCallback : BleManagerGattCallback() {

        override fun initialize() {
            requestMtu(500).enqueue()


        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            // TODO: Get service UUID
            gatt.getService(UUID.randomUUID())?.run {
                // TODO: Get characteristic UUID
                writeChar = getCharacteristic(UUID.randomUUID())
            }

            return writeChar != null
        }

        override fun onServicesInvalidated() {
            writeChar = null
        }
    }
}

*/