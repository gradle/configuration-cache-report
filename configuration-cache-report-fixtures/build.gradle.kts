plugins {
    kotlin("jvm")

    id("gradlebuild.common-kotlin")
    id("gradlebuild.publish-libraries")
}

description = "Fixtures for reading the configuration cache problems HTML report"

dependencies {
    // The element ids the report data is found under belong to the report model.
    api(project(":"))
}

publishing {
    publications {
        // The KMP plugin creates the publications for the report itself; this is a plain JVM
        // library, so it declares its own. Named after the target for consistency with those.
        create<MavenPublication>("jvm") {
            from(components["java"])
        }
    }
}
