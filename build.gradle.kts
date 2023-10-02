plugins {
    id("gradlebuild.configuration-cache-report")
    id("gradlebuild.publish-libraries")
}

group = "org.gradle.buildtool.internal"
description = "Configuration cache problems HTML report"
version = "1.0"

repositories {
    mavenCentral()
}
