package me.palazzini.bleexample.domain.di

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