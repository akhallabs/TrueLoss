plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
    id("tm.trueloss.android.compose")
    id("tm.trueloss.android.hilt")
}
android { namespace = "tm.trueloss.feature.settings.ui" }
dependencies {
    implementation(project(":feature:settings:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:datastore"))
    implementation(libs.androidx.hilt.navigation.compose)
}
