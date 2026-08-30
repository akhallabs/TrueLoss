plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
    id("tm.trueloss.android.compose")
}
android { namespace = "tm.true.loss.core.designsystem" }
dependencies {
    api(platform(libs.compose.bom))
    api(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
}
