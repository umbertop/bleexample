package me.palazzini.bleexample.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import me.palazzini.bleexample.presentation.components.BleScanDeviceView
import me.palazzini.bleexample.core.permissions.allPermanentlyDenied
import me.palazzini.bleexample.core.permissions.isPermanentlyDenied
import me.palazzini.bleexample.core.util.UiEvent
import me.palazzini.bleexample.core_ui.LocalSpacing
import androidx.compose.material3.ExperimentalMaterial3Api as ExpM3Api


@ExperimentalPermissionsApi
@ExpM3Api
@Composable
fun MainView(
    scaffoldState: ScaffoldState,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val state = viewModel.state

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.onEvent(
            MainEvent.OnBluetoothEnableChanged(it.resultCode == Activity.RESULT_OK)
        )
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val bluetoothScanPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        rememberPermissionState(Manifest.permission.BLUETOOTH_SCAN)
    } else {
        null
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                }
                is UiEvent.RequestEnableBluetooth -> {
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    launcher.launch(intent)
                }
                else -> Unit
            }
        }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (!state.isBluetoothEnabled) {
                    viewModel.onEvent(MainEvent.OnRequestEnableBluetooth)
                    return@LifecycleEventObserver
                }

                locationPermissionsState.launchMultiplePermissionRequest()
                bluetoothScanPermissionState?.launchPermissionRequest()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {
        locationPermissionsState.allPermissionsGranted
                && bluetoothScanPermissionState?.hasPermission ?: true -> {
            viewModel.onEvent(MainEvent.OnLocationPermissionStateChanged(locationPermissionsState))
            DevicesView(viewModel)
        }
        locationPermissionsState.shouldShowRationale -> {
            viewModel.onEvent(MainEvent.OnLocationPermissionStateChanged(locationPermissionsState))
            LocationDeniedView()
        }
        locationPermissionsState.allPermanentlyDenied -> {
            viewModel.onEvent(MainEvent.OnLocationPermissionStateChanged(locationPermissionsState))
            LocationPermanentlyDeniedView()
        }
        bluetoothScanPermissionState?.shouldShowRationale ?: false -> {
            viewModel.onEvent(MainEvent.OnBluetoothScanPermissionStateChanged(bluetoothScanPermissionState!!))
            BluetoothScanDeniedView()
        }
        bluetoothScanPermissionState?.isPermanentlyDenied ?: false -> {
            viewModel.onEvent(MainEvent.OnBluetoothScanPermissionStateChanged(bluetoothScanPermissionState!!))
            BluetoothScanPermanentlyDeniedView()
        }
    }
}

@Composable
fun LocationDeniedView() {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Location permission is needed " +
                    "to scan for BLE devices"
        )
        // TODO: Show button to requesting permission
    }
}

@Composable
fun LocationPermanentlyDeniedView() {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Location permission is permanently denied. " +
                    "You can enable it in the app settings."
        )
        // TODO: Show button to go to app settings
    }
}

@Composable
fun BluetoothScanDeniedView() {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bluetooth scan permission is needed " +
                    "to scan for BLE devices"
        )
        // TODO: Show button to requesting permission
    }
}

@Composable
fun BluetoothScanPermanentlyDeniedView() {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceMedium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bluetooth scan permission is permanently denied. " +
                    "You can enable it in the app settings."
        )
        // TODO: Show button to go to app settings
    }
}

@ExperimentalPermissionsApi
@SuppressLint("MissingPermission")
@Composable
fun DevicesView(
    viewModel: MainViewModel
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, Color.Red)
        ) {
            items(state.devices) { device ->
                BleScanDeviceView(
                    modifier = Modifier.padding(spacing.spaceMedium),
                    scanResult = device,
                    state = state,
                    onConnectClick = {
                        if (state.isConnectedToDevice) {
                            viewModel.onEvent(MainEvent.OnDisconnectToDeviceClicked(it))
                        } else {
                            viewModel.onEvent(MainEvent.OnConnectDeviceClicked(it))
                        }
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, Color.Blue)
        ) {
            items(viewModel.state.commands) {
                if (it == null) {
                    Text(text = "Empty command")
                } else {
                    Text(text = it.data)
                }
            }
        }
    }
}