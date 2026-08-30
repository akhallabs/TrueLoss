plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
}
android { namespace = "tm.true.loss.core.navigation" }
dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
}
