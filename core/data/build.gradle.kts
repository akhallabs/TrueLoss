plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
    id("tm.trueloss.android.hilt")
}
android { namespace = "tm.true.loss.core.data" }
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
}
