import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing

plugins {
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin")
}

val artifactoryUserName
    get() = findProperty("artifactoryUserName") as String?
val artifactoryUserPassword
    get() = findProperty("artifactoryUserPassword") as String?

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(artifactoryUserName)
            password.set(artifactoryUserPassword)
        }
    }
}
