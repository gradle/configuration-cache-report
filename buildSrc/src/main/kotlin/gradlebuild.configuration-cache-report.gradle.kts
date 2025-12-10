/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import gradlebuild.configcachereport.tasks.MergeReportAssets
import gradlebuild.configcachereport.tasks.VerifyJar
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform")
    id("org.gradle.kotlin-dsl.ktlint-convention")
}

kotlin {
    js {
        browser {
            commonWebpackConfig {
                sourceMaps = true
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

rootProject.run {
    // Move yarn.lock to the build directory, out of VCS control
    plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
        configure<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension> {
            lockFileDirectory = layout.buildDirectory.file("kotlin-js-store").get().asFile
        }
    }

    providers.environmentVariable("YARNPKG_MIRROR_URL").orNull?.let { mirrorUrl ->
        tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
            args += listOf("--registry", mirrorUrl)
        }
    }
}

tasks {
    withType<KotlinJsCompile>().configureEach {
        compilerOptions {
            allWarningsAsErrors = true
            moduleKind = org.jetbrains.kotlin.gradle.dsl.JsModuleKind.MODULE_PLAIN
        }
    }

    val jsBrowserProductionWebpack = named<KotlinWebpack>("jsBrowserProductionWebpack")

    val assembleReport = register<MergeReportAssets>("assembleReport") {
        htmlFile = layout.buildDirectory.file("processedResources/js/main/configuration-cache-report.html")
        logoFile = layout.buildDirectory.file("processedResources/js/main/configuration-cache-report-logo.png")
        cssFile = layout.buildDirectory.file("processedResources/js/main/configuration-cache-report.css")
        jsFile = jsBrowserProductionWebpack.flatMap {
            it.outputDirectory.file("configuration-cache-report.js")
        }
        outputFile = layout.buildDirectory.file("$name/configuration-cache-report.html")

        // This is a workaround for "undeclared" dependency when running the build as included build of gradle/gradle.
        // The KMP plugin declares its outputs both on Webpack and lifecycle task, which trips over missing dependency
        // detector.
        dependsOn("jsBrowserDistribution")
        dependsOn("jsProcessResources")
    }

    val jar = register<Jar>("jar") {
        from(assembleReport)
    }

    assemble {
        dependsOn(jar)
    }

    val verifyJar = register<VerifyJar>("verifyJar") {
        jarFile = jar.flatMap { it.archiveFile }
        receiptFile = layout.buildDirectory.file("$name/receipt.txt")
    }

    check {
        dependsOn(verifyJar)
    }
}

configurations.consumable("configurationCacheReport") {
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("configuration-cache-report"))
    }
    outgoing.artifact(tasks.named<Jar>("jar"))
}
