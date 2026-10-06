pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "SmartLedger"
include(":app")
include(":core:common")
include(":core:domain")
include(":core:database")
include(":core:security")
include(":core:localization")
include(":core:design-system")
include(":core:testing")
include(":core:backup")
