plugins { alias(libs.plugins.kotlin.android); id("tm.trueloss.android.library") }
android { namespace = "tm.trueloss.feature.result.domain" }
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("javax.inject:javax.inject:1")
}
