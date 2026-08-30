plugins { alias(libs.plugins.kotlin.android); id("tm.trueloss.android.library") }
android { namespace = "tm.true.loss.feature.result.domain" }
dependencies { implementation(project(":core:common")); implementation(project(":core:domain")) }
