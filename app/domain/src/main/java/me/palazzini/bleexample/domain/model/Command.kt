package me.palazzini.bleexample.domain.model

import no.nordicsemi.android.ble.data.Data

sealed class Command(val data: String) {
    object Start : Command("\$CMD,51")
    object Stop : Command("\$CMD,54")

    data class Other(val value: String): Command(value)

    companion object {
        fun send(command: Command): Data {
            return Data.from(command.data)
        }

        fun parse(value: String): Command {
            return when (value) {
                "\$CMD,51" -> Start
                "\$CMD,54" -> Stop
                else -> Other(value)
            }
        }
    }
}