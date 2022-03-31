plugins {
    `maven-publish`
    signing
}

val artifactoryUrl
    get() = providers.environmentVariable("GRADLE_INTERNAL_REPO_URL").orNull ?: ""

val artifactoryUserName
    get() = providers.gradleProperty("artifactoryUserName").orNull

val artifactoryUserPassword
    get() = providers.gradleProperty("artifactoryUserPassword").orNull

val pgpSigningKey: Provider<String> = providers.environmentVariable("PGP_SIGNING_KEY")
val signArtifacts: Boolean = !pgpSigningKey.orNull.isNullOrEmpty()

tasks.withType<Sign>().configureEach { isEnabled = signArtifacts }

signing {
    useInMemoryPgpKeys(
        providers.environmentVariable("PGP_SIGNING_KEY").orNull,
        providers.environmentVariable("PGP_SIGNING_KEY_PASSPHRASE").orNull
    )
    publishing.publications.configureEach {
        if (signArtifacts) {
            signing.sign(this)
        }
    }
}

val jar by tasks.named("jar")

val sourceJar by tasks.registering(Jar::class) {
    from(layout.files("src/main/kotlin"))
    archiveClassifier.set("source")
}

val javadocJar by tasks.registering(Jar::class) {
    from(layout.files("README.md"))
    archiveClassifier.set("javadoc")
}

val moduleVersion
    get() = providers.gradleProperty("configuration-cache-report.version").get()

publishing {
    repositories {
        maven {
            name = "remote"
            val libsType = if (moduleVersion.endsWith("-SNAPSHOT")) "snapshots" else "releases"
            url = uri("$artifactoryUrl/libs-$libsType-local")
            credentials {
                username = artifactoryUserName
                password = artifactoryUserPassword
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.gradle.buildtool.internal"
            artifactId = "configuration-cache-report"
            version = moduleVersion
            artifact(jar)
            artifact(mapOf("source" to sourceJar, "classifier" to "source"))
            artifact(mapOf("source" to javadocJar, "classifier" to "javadoc"))

            pom {
                packaging = "jar"
                name.set("configuration-cache-report")
                description.set(
                    provider {
                        require(project.description != null) { "You must set the description of published project ${project.name}" }
                        project.description
                    }
                )
                url.set("https://github.com/gradle/configuration-cache-report")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("The Gradle team")
                        organization.set("Gradle Inc.")
                        organizationUrl.set("https://gradle.com")
                    }
                }
                scm {
                    connection.set("scm:git://github.com/gradle/configuration-cache-report.git")
                    developerConnection.set("scm:git:ssh://github.com:gradle/configuration-cache-report.git")
                    url.set("https://github.com/gradle/configuration-cache-report")
                }
            }
        }
    }
}
