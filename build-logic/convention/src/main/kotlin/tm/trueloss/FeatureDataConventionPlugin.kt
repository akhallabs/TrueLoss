package tm.trueloss
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FeatureDataConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("tm.trueloss.android.library")
            pluginManager.apply("tm.trueloss.android.hilt")
            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:data"))
                add("implementation", project(":core:network"))
                add("implementation", project(":core:database"))
                // feature domain will be added per feature explicitly
            }
        }
    }
}
