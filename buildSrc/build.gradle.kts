plugins {
    `kotlin-dsl`
    `java-library`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation("org.gradle.kotlin:gradle-kotlin-dsl-conventions:0.7.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:10.1.0")
}
