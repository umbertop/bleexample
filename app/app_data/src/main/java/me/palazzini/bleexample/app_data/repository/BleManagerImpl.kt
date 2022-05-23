package me.palazzini.bleexample.app_data.repository

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import me.palazzini.bleexample.app_domain.model.Command
import me.palazzini.bleexample.app_domain.repository.BleManager
import no.nordicsemi.android.ble.callback.DataSentCallback
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data
import timber.log.Timber
import java.util.*

class BleManagerImpl(
    private val ctx: Context
) : BleManager(ctx) {

    private var characteristic: BluetoothGattCharacteristic? = null

    private val _commandState = MutableSharedFlow<Command?>(extraBufferCapacity = 64)
    override val commandState: SharedFlow<Command?> = _commandState

    override fun getMinLogPriority(): Int {
        return Log.VERBOSE
    }

    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return GattCallbackImpl()
    }

    private inner class GattCallbackImpl : BleManagerGattCallback() {
        override fun initialize() {
            // TODO: Replace with asValidResponseFlow with data adapter.
            setNotificationCallback(characteristic).with(commandCallback)

            requestMtu(500).enqueue()
            enableNotifications(characteristic).enqueue()
        }


        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            gatt.getService(SERVICE_UUID)?.run {
                characteristic = getCharacteristic(CHARACTERISTIC_UUID)
            }

            return characteristic != null
        }

        override fun onServicesInvalidated() {
            characteristic = null
        }
    }

    // region Callbacks

    private val commandCallback = object : ProfileDataCallback, DataSentCallback {
        override fun onDataReceived(device: BluetoothDevice, data: Data) {
            // TODO: If invalid data is received
            if(false) {
                onInvalidDataReceived(device, data)
                return
            }

            log(Log.DEBUG, "Data received")

            val command = Command.parse(data.getStringValue(0) ?: "")
            _commandState.tryEmit(command)
        }



        override fun onDataSent(device: BluetoothDevice, data: Data) {

        }

        override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
            log(Log.WARN, "Invalid data received: $data")
        }
    }

    // endregion

    // region BleManager

    override suspend fun sendCommand(command: Command) {
        if (characteristic == null) {
            log(Log.WARN, "Characteristic is null")
            return
        }

        writeCharacteristic(
            characteristic,
            Command.send(command),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).with(commandCallback).enqueue()

        repeat(10) {
            readCharacteristic(
                characteristic,
            ).with(commandCallback).enqueue()
            // delay(500)
        }
    }

    // endregion

    companion object{
        private val SERVICE_UUID = UUID.fromString("000000FF-0000-1000-8000-00805f9b34fb")
        private val CHARACTERISTIC_UUID = UUID.fromString("0000FF01-0000-1000-8000-00805f9b34fb")
    }
}