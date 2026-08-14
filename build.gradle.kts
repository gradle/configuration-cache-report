plugins {
    id("gradlebuild.configuration-cache-report")
    id("gradlebuild.publish-libraries")
}

description = "Configuration cache problems HTML report"

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0") {
                    because("Exposes serialization types in the wire model classes")
                }
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
