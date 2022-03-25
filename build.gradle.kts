plugins {
    // Must be applied to root project, otherwise it throws:
    // Plugin must be applied to the root project but was applied to :configuration-cache-report
    id("gradlebuild.configure-maven-central")
}

group = "org.gradle.gradlebuild"
