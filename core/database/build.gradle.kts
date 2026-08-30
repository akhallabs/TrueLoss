plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
    id("tm.trueloss.android.room")
    id("tm.trueloss.android.hilt")
}
android { namespace = "tm.trueloss.core.database" }
dependencies { implementation(project(":core:domain")) }
