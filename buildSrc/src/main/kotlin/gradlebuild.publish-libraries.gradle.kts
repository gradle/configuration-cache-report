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

// The JS target is only an intermediate build input -- its webpack bundle is inlined into
// the report HTML that the JVM jar carries -- and nothing downstream consumes the JS klib,
// so we don't publish it. We publish the JVM module and the Kotlin Multiplatform root
// module: the root carries the Gradle Module Metadata that redirects the bare coordinate to
// the JVM variant, which is the documented consumption path. Disabling the JS publish tasks
// makes `publish` (and publishToMavenLocal) do the right thing.
tasks.withType<AbstractPublishToMaven>().configureEach {
    onlyIf("the JS target is not published") { !name.contains("JsPublication") }
}

signing {
    useInMemoryPgpKeys(
        providers.environmentVariable("PGP_SIGNING_KEY").orNull,
        providers.environmentVariable("PGP_SIGNING_KEY_PASSPHRASE").orNull
    )
    publishing.publications.configureEach {
        if (signArtifacts && name != "js") {
            // We're not publishing JS artifacts, so there is no need to sign them.
            signing.sign(this)
        }
    }
}

val isSnapshot
    get() = version.toString().endsWith("-SNAPSHOT")

publishing {
    repositories {
        maven {
            name = "remote"
            val libsType = if (isSnapshot) "snapshots" else "releases"
            url = uri("$artifactoryUrl/libs-$libsType-local")
            credentials {
                username = artifactoryUserName
                password = artifactoryUserPassword
            }
        }
    }

    // The Kotlin Multiplatform plugin creates one publication per target (`jvm`, `js`) plus
    // the root `kotlinMultiplatform` module. We publish the root and `jvm` (JS is skipped by
    // the publish-task filter above). Customize the shared POM metadata across them.
    publications.withType<MavenPublication>().configureEach {
        val publicationName = name
        pom {
            name.set("${project.name} ($publicationName)")
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
                    organization.set("Gradle Technologies")
                    organizationUrl.set("https://gradle.org")
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
