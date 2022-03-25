import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.CheckoutMode
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.RelativeId
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs


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
})

object Check : AbstractCheck({
    id = RelativeId("Check")
    uuid = "Gradle_ConfigurationCacheReport_Check"
    name = "Run check on configuration-cache-report"
    description = "Run check on configuration-cache-report"

    triggers {
        vcs {
            branchFilter = """
    +:refs/heads/*
""".trimIndent()
        }
    }

    steps {
        gradle {
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
    id = RelativeId("PublishToMavenCentral")
    uuid = "Gradle_ConfigurationCacheReport_PublishToMavenCentral"
    name = "Publish configuration-cache-report to Maven Central"
    description = "Publish configuration-cache-report to Maven Central"

    params {
        param("env.PGP_SIGNING_KEY", "%pgpSigningKey%")
        param("env.PGP_SIGNING_KEY_PASSPHRASE", "%pgpSigningPassphrase%")
        param("env.ORG_GRADLE_PROJECT_sonatypeUsername", "%sonatypeUsername%")
        param("env.ORG_GRADLE_PROJECT_sonatypePassword", "%sonatypePassword%")
    }

    steps {
        gradle {
            tasks = "publishToSonatype closeAndReleaseSonatypeStagingRepository"
        }
    }
})

