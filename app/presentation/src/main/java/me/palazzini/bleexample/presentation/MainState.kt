package me.palazzini.bleexample.presentation

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import me.palazzini.bleexample.domain.model.BleDeviceScanResult
import me.palazzini.bleexample.domain.model.Command

@ExperimentalPermissionsApi
data class MainState(
    val isBluetoothEnabled: Boolean = false,
    val isConnectedToDevice: Boolean = false,
    val permissions: MultiplePermissionsState? = null,
    val devices: List<BleDeviceScanResult> = emptyList(),
    val commands: List<Command?> = emptyList()
)