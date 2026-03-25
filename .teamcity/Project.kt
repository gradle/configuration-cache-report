import jetbrains.buildServer.configs.kotlin.AbsoluteId
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.CheckoutMode
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.RelativeId
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.triggers.vcs


object Project : Project({
    buildType(Check)
    buildType(PublishToMavenCentral)
    params {
        param("env.JAVA_HOME", "%linux.java17.adoptiumopenjdk.64bit%")
    }
})

abstract class AbstractCheck(init: BuildType.() -> Unit) : BuildType({
    init()

    vcs {
        root(AbsoluteId("Gradle_ConfigurationCacheReport"))

        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
    }

    requirements {
        contains("teamcity.agent.jvm.os.name", "Linux")
    }

    params {
        param("env.DEVELOCITY_ACCESS_KEY", "%ge.gradle.org.access.key%")
    }
})

object Check : AbstractCheck({
    id = RelativeId("Check")
    uuid = "Gradle_ConfigurationCacheReport_Check"
    name = "Run check on configuration-cache-report"
    description = "Run check on configuration-cache-report"

    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }

    steps {
        gradle {
            useGradleWrapper = true
            tasks = "check"
        }
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = "Gradle_ConfigurationCacheReport"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "%github.bot-teamcity.token%"
                }
            }
        }
    }
})

object PublishToMavenCentral : AbstractCheck({
    id = RelativeId("Publish")
    uuid = "Gradle_ConfigurationCacheReport_Publish"
    name = "Publish configuration-cache-report to repo.grdev.net"
    description = "Publish configuration-cache-report to repo.grdev.net"

    params {
        param("env.PGP_SIGNING_KEY", "%pgpSigningKey%")
        param("env.PGP_SIGNING_KEY_PASSPHRASE", "%pgpSigningPassphrase%")
        param("env.GRADLE_INTERNAL_REPO_URL", "%gradle.internal.repository.url%")
        param("env.ORG_GRADLE_PROJECT_artifactoryUserName", "%gradle.internal.repository.build-tool.publish.username%")
        password("env.ORG_GRADLE_PROJECT_artifactoryUserPassword", "%gradle.internal.repository.build-tool.publish.password%")
    }

    steps {
        gradle {
            useGradleWrapper = true
            gradleParams = "publishMavenPublicationToRemoteRepository"
        }
    }
})

