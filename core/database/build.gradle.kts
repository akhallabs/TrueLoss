plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
    id("tm.trueloss.android.room")
}
android { namespace = "tm.trueloss.core.database" }
dependencies { implementation(project(":core:domain")) }
