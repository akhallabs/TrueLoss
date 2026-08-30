plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
}
android { namespace = "tm.true.loss.core.domain" }
dependencies {
    implementation(project(":core:common"))
}
