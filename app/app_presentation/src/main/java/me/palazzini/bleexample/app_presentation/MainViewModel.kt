package me.palazzini.bleexample.app_presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.palazzini.bleexample.app_domain.model.Command
import me.palazzini.bleexample.app_domain.repository.BleManager
import me.palazzini.bleexample.app_domain.repository.BleScanner
import me.palazzini.bleexample.core.util.UiEvent
import no.nordicsemi.android.ble.ConnectRequest
import timber.log.Timber
import javax.inject.Inject

@ExperimentalPermissionsApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val bleManager: BleManager
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var connectRequest: ConnectRequest? = null

    init {
        state = state.copy(
            isBluetoothEnabled = bleScanner.isBleEnabled()
        )

        if (!state.isBluetoothEnabled) {
            onEvent(MainEvent.OnRequestEnableBluetooth)
        } else {
            bleScanner.enableBle()
            observeBleDevices()
        }
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnBluetoothEnableChanged -> {
                state = state.copy(isBluetoothEnabled = event.isEnabled)

                state.locationPermissionState?.let {
                    if (it.allPermissionsGranted && state.isBluetoothEnabled) {
                        observeBleDevices()
                    }
                }
            }
            is MainEvent.OnLocationPermissionStateChanged -> {
                state = state.copy(locationPermissionState = event.state)

                if (event.state.allPermissionsGranted && state.isBluetoothEnabled) {
                    observeBleDevices()
                }
            }
            is MainEvent.OnDisconnectToDeviceClicked -> {
                viewModelScope.launch {
                    bleManager.disconnect()
                }

                state = state.copy(isConnectedToDevice = false)
            }
            is MainEvent.OnConnectDeviceClicked -> {
                viewModelScope.launch {
                    connectRequest?.cancelPendingConnection()
                    connectRequest = bleManager.connect(event.device)
                        .retry(3, 500)
                        .timeout(15_000)
                        .useAutoConnect(true)
                        .fail { device, status ->
                            Timber.w("Cannot connect to device with address: %s", device.address)
                        }
                        .then {
                            Timber.d(it.address)
                            initDeviceDataTransfer()
                        }

                    connectRequest?.enqueue()
                }
            }
            is MainEvent.OnRequestEnableBluetooth -> {
                bleScanner.enableBle()
                onEvent(MainEvent.OnBluetoothEnableChanged(bleScanner.isBleEnabled()))
            }
        }
    }

    private fun observeBleDevices() {
        bleScanner.observeDevices()
            .onEach { result ->
                val temp = state.devices.toMutableList().run {
                    add(result)
                    distinctBy { it.address }
                }

                state = state.copy(devices = temp)
            }.launchIn(viewModelScope)
    }

    private fun initDeviceDataTransfer() {
        state = state.copy(isConnectedToDevice = true)

        bleManager.commandState.onEach {
            val temp = state.commands.toMutableList().run {
                add(it)
                toList()
            }

            state = state.copy(commands = temp)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            bleManager.sendCommand(Command.Start)
        }
    }
}