plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
    id("tm.trueloss.android.room")
}
android { namespace = "tm.true.loss.core.database" }
dependencies { implementation(project(":core:domain")) }
