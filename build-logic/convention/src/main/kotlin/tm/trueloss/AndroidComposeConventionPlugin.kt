package tm.trueloss
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            val isApp = pluginManager.hasPlugin("com.android.application")
            if (isApp) {
                extensions.configure<ApplicationExtension> {
                    buildFeatures { compose = true }
                }
            } else {
                extensions.configure<LibraryExtension> {
                    buildFeatures { compose = true }
                }
            }
            dependencies {
                add("implementation", platform("androidx.compose:compose-bom:2025.09.00"))
                add("implementation", "androidx.compose.ui:ui")
                add("implementation", "androidx.compose.ui:ui-tooling-preview")
                add("implementation", "androidx.compose.material3:material3")
                add("implementation", "androidx.compose.material:material-icons-extended")
                add("debugImplementation", "androidx.compose.ui:ui-tooling")
            }
        }
    }
}
