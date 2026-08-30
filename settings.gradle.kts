pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "TrueLoss"
include(":app")
include(":core:common")
include(":core:domain")
include(":core:ui")
include(":core:designsystem")
include(":core:navigation")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:data")
include(":feature:trace:domain")
include(":feature:trace:data")
include(":feature:trace:ui")
include(":feature:history:domain")
include(":feature:history:data")
include(":feature:history:ui")
include(":feature:result:domain")
include(":feature:result:data")
include(":feature:result:ui")
include(":feature:settings:domain")
include(":feature:settings:data")
include(":feature:settings:ui")
