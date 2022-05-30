apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(Coroutines.coroutines)

    "implementation"(NordicSemiconductor.ble)
    "implementation"(NordicSemiconductor.bleScanner)

}