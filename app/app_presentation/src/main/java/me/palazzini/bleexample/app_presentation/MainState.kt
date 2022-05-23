package me.palazzini.bleexample.app_presentation

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import me.palazzini.bleexample.app_domain.model.Command
import no.nordicsemi.android.support.v18.scanner.ScanResult

@ExperimentalPermissionsApi
data class MainState(
    val isBluetoothEnabled: Boolean = false,
    val devices: List<ScanResult> = emptyList(),
    val locationPermissionState: PermissionState? = null,
    val commands: List<Command?> = emptyList()
)