package me.palazzini.bleexample.core.permissions

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState

@ExperimentalPermissionsApi
val PermissionState.isPermanentlyDenied: Boolean
    get() = !shouldShowRationale && !hasPermission

@ExperimentalPermissionsApi
val MultiplePermissionsState.allPermanentlyDenied: Boolean
    get() = this.permissions.all { it.isPermanentlyDenied }