plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
    id("tm.trueloss.android.hilt")
}
android { namespace = "tm.true.loss.feature.trace.data" }
dependencies {
    implementation(project(":feature:trace:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
}
