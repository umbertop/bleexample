package me.palazzini.bleexample.core.util

sealed class UiEvent {
    object Success : UiEvent()
    object NavigateUp : UiEvent()
    data class ShowSnackbar(val message: UiText) : UiEvent()

    object RequestEnableBluetooth: UiEvent()
}
