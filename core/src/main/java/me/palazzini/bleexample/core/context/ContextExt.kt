package me.palazzini.bleexample.core.context

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.showAppSettingsDialog() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }

    startActivity(intent)
}