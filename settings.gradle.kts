plugins {
    id("com.gradle.develocity").version("4.3.2")
    id("io.github.gradle.develocity-conventions-plugin").version("0.14.1")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    // NOTE: Kotlin/JS adds a repository for Node dependencies, so we cannot FAIL_ON_PROJECT_REPOS
}

rootProject.name = "configuration-cache-report"

include("configuration-cache-report-fixtures")

configureProjects()

fun configureProjects() {
    val versionValue = providers.gradleProperty("configuration-cache-report.version").get()

    gradle.lifecycle.beforeProject {
        group = "org.gradle.buildtool.internal"
        version = versionValue
    }
}
