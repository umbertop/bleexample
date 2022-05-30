package me.palazzini.bleexample.data.di

import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.palazzini.bleexample.data.repository.BleManagerImpl
import me.palazzini.bleexample.domain.di.AndroidBleScanner
import me.palazzini.bleexample.domain.di.AndroidIntentBleScanner
import me.palazzini.bleexample.domain.di.NordicBleScanner
import me.palazzini.bleexample.domain.repository.BleManager
import me.palazzini.bleexample.domain.repository.BleScanner
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
    @NordicBleScanner
    fun provideNordicBleScanner(bluetoothManager: BluetoothManager): BleScanner =
        me.palazzini.bleexample.data.repository.NordicBleScanner(bluetoothManager = bluetoothManager)

    @Provides
    @Singleton
    @AndroidBleScanner
    fun provideAndroidBleScanner(bluetoothManager: BluetoothManager): BleScanner =
        me.palazzini.bleexample.data.repository.AndroidBleScanner(bluetoothManager = bluetoothManager)

    @Provides
    @Singleton
    @AndroidIntentBleScanner
    fun provideAndroidIntentBleScanner(
        @ApplicationContext context: Context,
        bluetoothManager: BluetoothManager
    ): BleScanner =
        me.palazzini.bleexample.data.repository.AndroidIntentBleScanner(
            context = context,
            bluetoothManager = bluetoothManager,
        )

    @Provides
    @Singleton
    fun provideBleManager(
        @ApplicationContext context: Context
    ): BleManager = BleManagerImpl(context)
}