plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
}
android { namespace = "tm.trueloss.core.domain" }
dependencies {
    implementation(project(":core:common"))
}
