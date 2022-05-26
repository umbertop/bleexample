package me.palazzini.bleexample.app_domain.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AndroidBleScanner

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AndroidIntentBleScanner

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NordicBleScanner