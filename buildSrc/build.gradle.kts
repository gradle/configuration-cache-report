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
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.25") {
        because("this includes the multiplatform plugin")
    }
    implementation("org.gradle.kotlin:gradle-kotlin-dsl-conventions:0.9.0")
}
