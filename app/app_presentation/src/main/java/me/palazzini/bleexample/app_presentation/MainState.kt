package me.palazzini.bleexample.app_presentation

import android.bluetooth.BluetoothDevice
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import me.palazzini.bleexample.app_domain.model.BleDeviceScanResult
import me.palazzini.bleexample.app_domain.model.Command
import no.nordicsemi.android.support.v18.scanner.ScanResult

@ExperimentalPermissionsApi
data class MainState(
    val isConnectedToDevice: Boolean = false,
    val isBluetoothEnabled: Boolean = false,
    val devices: List<BleDeviceScanResult> = emptyList(),
    val locationPermissionState: MultiplePermissionsState? = null,
    val commands: List<Command?> = emptyList()
)