package me.palazzini.bleexample.app_presentation

import android.bluetooth.BluetoothDevice
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import no.nordicsemi.android.support.v18.scanner.ScanResult

@ExperimentalPermissionsApi
sealed class MainEvent {
    data class OnBluetoothEnableChanged(val isEnabled: Boolean): MainEvent()
    data class OnLocationPermissionStateChanged(val state: MultiplePermissionsState): MainEvent()
    data class OnBluetoothScanPermissionStateChanged(val state: PermissionState): MainEvent()

    data class OnConnectDeviceClicked(val device: BluetoothDevice): MainEvent()
    data class OnDisconnectToDeviceClicked(val device: BluetoothDevice): MainEvent()

    object OnRequestEnableBluetooth: MainEvent()
}
