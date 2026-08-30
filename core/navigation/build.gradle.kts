plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
}
android { namespace = "tm.trueloss.core.navigation" }
dependencies {
    implementation("javax.inject:javax.inject:1")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
}
