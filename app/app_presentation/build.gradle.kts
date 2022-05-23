apply {
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.coreUi))
    "implementation"(project(Modules.appDomain))

    "implementation"(Google.accompanistPermissions)

    "implementation"(NordicSemiconductor.ble)
    "implementation"(NordicSemiconductor.bleScanner)
}