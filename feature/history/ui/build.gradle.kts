plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
    id("tm.trueloss.android.compose")
    id("tm.trueloss.android.hilt")
}
android { namespace = "tm.true.loss.feature.history.ui" }
dependencies {
    implementation(project(":feature:history:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(libs.androidx.hilt.navigation.compose)
}
