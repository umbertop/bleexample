package me.palazzini.bleexample.presentation.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import me.palazzini.bleexample.domain.model.BleDeviceScanResult
import me.palazzini.bleexample.presentation.MainState

@ExperimentalPermissionsApi
@SuppressLint("MissingPermission")
@Composable
fun BleScanDeviceView(
    modifier: Modifier,
    state: MainState,
    scanResult: BleDeviceScanResult,
    onConnectClick: (device: BluetoothDevice) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = scanResult.name)
            Text(text = scanResult.address)
        }

        Button(onClick = { onConnectClick(scanResult.device) }) {
            Text(text = if (state.isConnectedToDevice) "Disconnect" else "Connect")
        }
    }
}