package me.palazzini.bleexample.app_presentation.components

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
import no.nordicsemi.android.support.v18.scanner.ScanResult

@SuppressLint("MissingPermission")
@Composable
fun BleScanDeviceView(
    modifier: Modifier,
    scanResult: ScanResult,
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
            Text(text = scanResult.device.name ?: "")
            Text(text = scanResult.device.address ?: "")
        }

        Button(onClick = { onConnectClick(scanResult.device) }) {
            Text(text = "Connect")
        }
    }
}