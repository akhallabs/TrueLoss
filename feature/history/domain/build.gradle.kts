plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
}
android { namespace = "tm.trueloss.feature.history.domain" }
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
}
