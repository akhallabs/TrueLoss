package tm.trueloss
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FeatureUiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("tm.trueloss.android.library")
            pluginManager.apply("tm.trueloss.android.compose")
            pluginManager.apply("tm.trueloss.android.hilt")
            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:navigation"))
                add("implementation", "androidx.lifecycle:lifecycle-runtime-compose:2.9.2")
                add("implementation", "androidx.navigation:navigation-compose:2.8.4")
            }
        }
    }
}
