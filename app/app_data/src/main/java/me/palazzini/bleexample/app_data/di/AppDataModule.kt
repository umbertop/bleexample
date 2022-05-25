package me.palazzini.bleexample.app_data.di

import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.palazzini.bleexample.app_data.repository.BleManagerImpl
import me.palazzini.bleexample.app_data.repository.BleScannerImpl
import me.palazzini.bleexample.app_domain.repository.BleManager
import me.palazzini.bleexample.app_domain.repository.BleScanner
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDataModule {

    @Provides
    @Singleton
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    @Provides
    @Singleton
    fun provideBleScanner(bluetoothManager: BluetoothManager): BleScanner =
        BleScannerImpl(bluetoothManager = bluetoothManager)

    @Provides
    @Singleton
    fun provideBleManager(
        @ApplicationContext context: Context
    ): BleManager = BleManagerImpl(context)
}