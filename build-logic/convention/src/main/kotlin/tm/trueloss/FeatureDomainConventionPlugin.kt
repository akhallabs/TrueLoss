package tm.trueloss
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FeatureDomainConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.android")
            // pure domain - no android lib, but kotlin android for consistency
            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:domain"))
                add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            }
        }
    }
}
