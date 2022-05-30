package me.palazzini.bleexample.presentation

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import me.palazzini.bleexample.domain.model.BleDeviceScanResult
import me.palazzini.bleexample.domain.model.Command

@ExperimentalPermissionsApi
data class MainState(
    val isConnectedToDevice: Boolean = false,
    val isBluetoothEnabled: Boolean = false,
    val devices: List<BleDeviceScanResult> = emptyList(),
    val locationPermissionsState: MultiplePermissionsState? = null,
    val bluetoothPermissionState: PermissionState? = null,
    val commands: List<Command?> = emptyList()
)