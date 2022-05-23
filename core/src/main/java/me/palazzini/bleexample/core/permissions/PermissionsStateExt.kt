package me.palazzini.bleexample.core.permissions

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@ExperimentalPermissionsApi
val PermissionState.isPermanentlyDenied: Boolean
    get() = !shouldShowRationale && !hasPermission