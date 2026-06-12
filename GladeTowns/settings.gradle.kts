pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GladeTowns"
include(":app")
// Planned module split (Phase 2+, see docs/ARCHITECTURE.md §1.2):
// include(":core:domain", ":core:data", ":core:engine", ":core:ui")
