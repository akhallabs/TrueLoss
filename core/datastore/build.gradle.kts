plugins {
    alias(libs.plugins.kotlin.android)
    id("tm.trueloss.android.library")
    id("tm.trueloss.android.hilt")
}
android { namespace = "tm.true.loss.core.datastore" }
dependencies { implementation(libs.androidx.datastore.preferences) }
