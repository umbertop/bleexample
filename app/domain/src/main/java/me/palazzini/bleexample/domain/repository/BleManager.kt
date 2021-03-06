package me.palazzini.bleexample.domain.repository

import android.content.Context
import kotlinx.coroutines.flow.SharedFlow
import me.palazzini.bleexample.domain.model.Command
import no.nordicsemi.android.ble.BleManager as NordicBleManager

abstract class BleManager(
    context: Context
) : NordicBleManager(context) {
    abstract val commandState: SharedFlow<Command?>

    abstract suspend fun sendCommand(command: Command)
}