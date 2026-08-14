plugins {
    kotlin("jvm")

    id("gradlebuild.common-kotlin")
    id("gradlebuild.publish-libraries")
}

description = "Fixtures for reading the configuration cache problems HTML report"

publishing {
    publications {
        // The KMP plugin creates the publications for the report itself; this is a plain JVM
        // library, so it declares its own. Named after the target for consistency with those.
        create<MavenPublication>("jvm") {
            from(components["java"])
        }
    }
}
