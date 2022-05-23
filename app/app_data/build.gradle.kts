apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.appDomain))

    "implementation"(NordicSemiconductor.ble)
    "implementation"(NordicSemiconductor.bleScanner)

    // "implementation"(Retrofit.okHttp)
    // "implementation"(Retrofit.retrofit)
    // "implementation"(Retrofit.okHttpLoggingInterceptor)
    // "implementation"(Retrofit.moshiConverter)

    // "implementation"(Moshi.moshi)
    // "implementation"(Moshi.moshiKotlin)
    // "implementation"(Moshi.moshiAdapters)

    // "kapt"(Room.roomCompiler)
    // "implementation"(Room.roomKtx)
    // "implementation"(Room.roomRuntime)
}