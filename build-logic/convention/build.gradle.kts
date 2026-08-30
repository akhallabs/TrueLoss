plugins {
    `kotlin-dsl`
}
repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}
dependencies {
    implementation("com.android.tools.build:gradle:8.7.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
}
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "tm.trueloss.android.application"
            implementationClass = "tm.trueloss.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "tm.trueloss.android.library"
            implementationClass = "tm.trueloss.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "tm.trueloss.android.compose"
            implementationClass = "tm.trueloss.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "tm.trueloss.android.hilt"
            implementationClass = "tm.trueloss.AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "tm.trueloss.android.room"
            implementationClass = "tm.trueloss.AndroidRoomConventionPlugin"
        }
        register("featureDomain") {
            id = "tm.trueloss.feature.domain"
            implementationClass = "tm.trueloss.FeatureDomainConventionPlugin"
        }
        register("featureData") {
            id = "tm.trueloss.feature.data"
            implementationClass = "tm.trueloss.FeatureDataConventionPlugin"
        }
        register("featureUi") {
            id = "tm.trueloss.feature.ui"
            implementationClass = "tm.trueloss.FeatureUiConventionPlugin"
        }
    }
}
