package me.palazzini.bleexample.core.util

import android.content.Context

sealed class UiText {
    data class DynamicString(val text: String) : UiText()

    class StringResource(
        val resId: Int,
        vararg val args: Any
    ) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> text
            is StringResource -> context.getString(resId, *args)
        }
    }
}
