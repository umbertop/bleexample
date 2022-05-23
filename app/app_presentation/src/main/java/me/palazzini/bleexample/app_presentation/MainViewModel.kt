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
import no.nordicsemi.android.ble.ktx.suspend
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

    init {
        state = state.copy(
            isBluetoothEnabled = bleScanner.isBleEnabled()
        )

        if (!state.isBluetoothEnabled) {
            onEvent(MainEvent.OnRequestEnableBluetooth)
        } else {
            observeBleDevices()
        }
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnBluetoothEnableChanged -> {
                state = state.copy(isBluetoothEnabled = event.isEnabled)

                state.locationPermissionState?.let {
                    if (it.hasPermission && state.isBluetoothEnabled) {
                        observeBleDevices()
                    }
                }
            }
            is MainEvent.OnLocationPermissionStateChanged -> {
                state = state.copy(locationPermissionState = event.state)

                if (event.state.hasPermission && state.isBluetoothEnabled) {
                    observeBleDevices()
                }
            }
            is MainEvent.OnConnectDeviceClicked -> {
                viewModelScope.launch {
                    bleManager.connect(event.device)
                        .retry(3, 100)
                        .timeout(15_000)
                        .useAutoConnect(true)
                        /*
                        .done {
                            Timber.d("Connected to device")
                            initDeviceDataTransfer()
                        }
                        .fail { device, status ->
                            Timber.d(status.toString())
                        }
                        */
                        .then {
                            Timber.d(it.address)
                            initDeviceDataTransfer()
                        }
                        .suspend()
                }
            }
            is MainEvent.OnRequestEnableBluetooth -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.RequestEnableBluetooth)
                }
            }
        }
    }

    private fun observeBleDevices() {
        bleScanner.observeDevices()
            .onEach { result ->
                val temp = state.devices.toMutableList().run {
                    add(result)
                    distinctBy { it.device.address }
                }

                state = state.copy(devices = temp)
            }.launchIn(viewModelScope)
    }

    private fun initDeviceDataTransfer() {
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