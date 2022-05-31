package me.palazzini.bleexample.presentation

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
import me.palazzini.bleexample.core.util.UiEvent
import me.palazzini.bleexample.domain.di.NordicBleScanner
import me.palazzini.bleexample.domain.model.Command
import me.palazzini.bleexample.domain.repository.BleManager
import me.palazzini.bleexample.domain.repository.BleScanner
import me.palazzini.bleexample.domain.repository.ReportRepository
import no.nordicsemi.android.ble.ConnectRequest
import timber.log.Timber
import javax.inject.Inject

@ExperimentalPermissionsApi
@HiltViewModel
class MainViewModel @Inject constructor(
    @NordicBleScanner private val bleScanner: BleScanner,
    private val bleManager: BleManager,
    private val reportRepository: ReportRepository
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
        }
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnBluetoothEnableChanged -> {
                state = state.copy(isBluetoothEnabled = event.isEnabled)

                state.permissions?.allPermissionsGranted?.let {
                    if (event.isEnabled) observeBleDevices()
                }
            }
            is MainEvent.OnPermissionsChanged -> {
                state = state.copy(permissions = event.state)

                if (event.state.allPermissionsGranted) {
                    observeBleDevices()
                }
            }
            is MainEvent.OnDisconnectToDeviceClicked -> {
                viewModelScope.launch {
                    bleManager.sendCommand(Command.Stop)
                    bleManager.disconnect().enqueue()
                    reportRepository.flush()
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
                        .fail { device, _ ->
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

        bleManager.commandState.onEach { command ->
            command?.data?.let {
                reportRepository.add(it)
            }

            val temp = state.commands.toMutableList().run {
                add(command)
                toList()
            }

            state = state.copy(commands = temp)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            bleManager.sendCommand(Command.Start)
        }
    }
}