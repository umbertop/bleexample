package me.palazzini.bleexample.presentation

import android.bluetooth.BluetoothDevice
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState

@ExperimentalPermissionsApi
sealed class MainEvent {
    data class OnBluetoothEnableChanged(val isEnabled: Boolean): MainEvent()
    data class OnPermissionsChanged(val state: MultiplePermissionsState): MainEvent()

    data class OnConnectDeviceClicked(val device: BluetoothDevice): MainEvent()
    data class OnDisconnectToDeviceClicked(val device: BluetoothDevice): MainEvent()

    object OnRequestEnableBluetooth: MainEvent()
}
