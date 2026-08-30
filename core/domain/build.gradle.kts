plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
}
android { namespace = "tm.trueloss.core.domain" }
dependencies {
    implementation(project(":core:common"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}
