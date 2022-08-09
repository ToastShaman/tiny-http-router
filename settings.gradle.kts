plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.40.2"
}

rootProject.name = "tiny-http-router"

include("core")
include("aws")
include("servlet")
