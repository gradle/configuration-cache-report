function configurationCacheProblems() {
    // Toggle between problems report and CC report
    const useProblemsReport = false; // Set to false for CC report

    const baseConfig = {
        "buildName": "sampleProject",
        "cacheAction": "storing",
        "cacheActionDescription": [{"text": "Calculating task graph as configuration cache cannot be reused because file"}, {"name": "build.gradle"}, {"text": " has changed."}],
        "requestedTasks": "clean build",
        "documentationLink": "https://docs.gradle.org/current/userguide/configuration_cache.html",
        "totalProblemCount": 20,
        "uniqueProblemCount": 19,
        "overflownProblemCount": 1,
    };

    if (useProblemsReport) {
        return {
            ...baseConfig,
            "problemsReport": {
                "totalProblemCount": 41,
                "buildName": "problems-playground",
                "requestedTasks": "help",
                "documentationLink": "https://docs.gradle.org/9.2.0/userguide/reporting_problems.html",
                "documentationLinkCaption": "Problem report",
                "summaries": []
            },
            "diagnostics": [{
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 42, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x0' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 43, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x1' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 44, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x2' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 45, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x3' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 46, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x4' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 47, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x5' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 48, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x6' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 49, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x7' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 50, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x8' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 51, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x9' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 52, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x10' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [
                    {"path": "src/main/java/com/example/MyClass.java", "line": 53, "column": 8},
                    {"pluginId": "java"},
                    {"taskPath": ":some:compileJava"}
                ],
                "severity": "WARNING",
                "contextualLabel": "Variable 'x11' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [{"path": "src/main/java/com/example/MyOtherClass.java", "line": 42, "column": 8}],
                "severity": "WARNING",
                "contextualLabel": "Variable 'b' is never used",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "unused-variable", "displayName": "Unused variable"}
                ],
                "solutions": ["Remove the unused variable"]
            }, {
                "locations": [{"path": "src/main/java/com/example/Calculator.java", "line": 15, "column": 12}],
                "severity": "ERROR",
                "contextualLabel": "Incompatible types: String cannot be converted to int",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "incompatible-types", "displayName": "Incompatible types"}
                ],
                "solutions": ["Change the type or convert the value appropriately"]
            }, {
                "locations": [
                    {"path": "src/main/kotlin/com/example/App.kt", "line": 28, "column": 4},
                    {"pluginId": "kotlin"},
                    {"taskPath": ":some:compileKotlin"}
                ],
                "severity": "ERROR",
                "contextualLabel": "Unresolved reference: myFunction",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "kotlin", "displayName": "Kotlin"},
                    {"name": "unresolved-reference", "displayName": "Unresolved reference"}
                ],
                "solutions": ["Import the missing function or check the spelling"]
            }, {
                "locations": [{"path": "src/main/cpp/main.cpp", "line": 1, "column": 0}],
                "severity": "ERROR",
                "contextualLabel": "Fatal error: 'iostream' file not found",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "cpp", "displayName": "C++"},
                    {"name": "missing-header", "displayName": "Missing header file"}
                ],
                "solutions": ["Ensure the C++ standard library is properly configured"]
            }, {
                "locations": [{"path": "src/test/java/com/example/CalculatorTest.java", "line": 35, "column": 8}],
                "severity": "ERROR",
                "contextualLabel": "Test 'shouldCalculateCorrectly' failed: expected 42 but was 43",
                "problemId": [
                    {"name": "verification", "displayName": "Verification"},
                    {"name": "unit-test", "displayName": "Unit Tests"},
                    {"name": "test-failure", "displayName": "Test failure"}
                ],
                "solutions": ["Fix the test implementation or the code under test"]
            }, {
                "locations": [{"path": "src/test/java/com/example/DatabaseTest.java", "line": 50, "column": 4}],
                "severity": "ERROR",
                "contextualLabel": "Test 'shouldConnectToDatabase' timed out after 30 seconds",
                "problemId": [
                    {"name": "verification", "displayName": "Verification"},
                    {"name": "integration-test", "displayName": "Integration Tests"},
                    {"name": "integration-test-timeout", "displayName": "Integration test timeout"}
                ],
                "solutions": ["Increase timeout or optimize the test"]
            }, {
                "locations": [{"path": "src/main/java/com/example/Calculator.java", "line": 20, "column": 4}],
                "severity": "WARNING",
                "contextualLabel": "Missing Javadoc comment for public method 'calculate'",
                "problemId": [
                    {"name": "verification", "displayName": "Verification"},
                    {"name": "checkstyle", "displayName": "Checkstyle"},
                    {"name": "missing-javadoc", "displayName": "Missing Javadoc"}
                ],
                "solutions": ["Add Javadoc comment to the method"]
            }, {
                "locations": [{"path": "src/main/java/com/example/MyClass.java", "line": 67, "column": 12}],
                "severity": "WARNING",
                "contextualLabel": "Possible null pointer dereference in com.example.MyClass.process()",
                "problemId": [
                    {"name": "verification", "displayName": "Verification"},
                    {"name": "spotbugs", "displayName": "SpotBugs"},
                    {"name": "null-pointer-dereference", "displayName": "Possible null pointer dereference"}
                ],
                "solutions": ["Add null check before accessing the variable"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "Code coverage is 65% but minimum required is 80%",
                "problemId": [
                    {"name": "verification", "displayName": "Verification"},
                    {"name": "code-coverage", "displayName": "Code Coverage"},
                    {"name": "coverage-threshold-not-met", "displayName": "Coverage threshold not met"}
                ],
                "solutions": ["Add more tests to increase coverage"]
            }, {
                "locations": [{"path": "src/main/java/com/example/MyClass.java", "line": 3, "column": 0}],
                "severity": "WARNING",
                "contextualLabel": "Unused import: java.util.ArrayList",
                "problemId": [
                    {"name": "verification", "displayName": "Verification"},
                    {"name": "linting", "displayName": "Linting"},
                    {"name": "unused-import", "displayName": "Unused import"}
                ],
                "solutions": ["Remove the unused import"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "High severity vulnerability in dependency org.example:vulnerable-lib:1.0.0 (CVE-2023-12345)",
                "problemId": [
                    {"name": "verification", "displayName": "Verification"},
                    {"name": "security-scan", "displayName": "Security Scan"},
                    {"name": "vulnerability-detected", "displayName": "Security vulnerability detected"}
                ],
                "solutions": ["Update to version 1.0.1 or higher"]
            }, {
                "locations": [{"path": "build.gradle.kts", "line": 45, "column": 4}],
                "severity": "ERROR",
                "contextualLabel": "Could not find com.example:missing-library:1.0.0",
                "problemId": [
                    {"name": "dependencies", "displayName": "Dependencies"},
                    {"name": "resolution", "displayName": "Dependency Resolution"},
                    {"name": "artifact-not-found", "displayName": "Artifact not found"}
                ],
                "solutions": ["Check that the artifact exists and the repository is configured correctly"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "Checksum verification failed for com.example:library:1.0.0",
                "problemId": [
                    {"name": "dependencies", "displayName": "Dependencies"},
                    {"name": "verification", "displayName": "Dependency Verification"},
                    {"name": "checksum-mismatch", "displayName": "Checksum mismatch"}
                ],
                "solutions": ["Verify the artifact integrity or update verification metadata"]
            }, {
                "locations": [{"path": "gradle/libs.versions.toml", "line": 23, "column": 0}],
                "severity": "ERROR",
                "contextualLabel": "In version catalog libs, dependency alias builder 'myLib' was not finished",
                "problemId": [
                    {"name": "dependencies", "displayName": "Dependencies"},
                    {"name": "version-catalog", "displayName": "Version Catalog"},
                    {"name": "alias-not-finished", "displayName": "Dependency alias builder not finished"}
                ],
                "solutions": ["Call `.version()` to give the alias a version"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "No matching variant of org:test:1.0 was found.",
                "problemId": [
                    {"name": "dependencies", "displayName": "Dependencies"},
                    {"name": "variant-resolution", "displayName": "Variant Resolution"},
                    {"name": "no-matching-variant", "displayName": "No matching variant found"}
                ],
                "solutions": ["Check that the dependency provides compatible variants"]
            }, {
                "locations": [{"path": "build.gradle.kts", "line": 18, "column": 12}],
                "severity": "ERROR",
                "contextualLabel": "Unresolved reference foo",
                "problemId": [
                    {"name": "configuration", "displayName": "Configuration"},
                    {"name": "build-script", "displayName": "Build Script"},
                    {"name": "invalid-syntax", "displayName": "Script compilation error"}
                ]
            }, {
                "locations": [{"path": "src/main/java/com/example/MyTask.java", "line": 15, "column": 4}],
                "severity": "WARNING",
                "contextualLabel": "Property 'myProperty' of primitive type should be annotated with @Optional",
                "problemId": [
                    {"name": "configuration", "displayName": "Configuration"},
                    {"name": "property-validation", "displayName": "Property Validation"},
                    {
                        "name": "cannot-use-optional-on-primitive-types",
                        "displayName": "Property should be annotated with @Optional"
                    }
                ],
                "solutions": ["Add @Optional annotation to the property"]
            }, {
                "locations": [{"path": "gradle/libs.versions.toml", "line": 23, "column": 0}],
                "severity": "ERROR",
                "contextualLabel": "Dependency alias 'myLib' was not finished with a version or reference",
                "problemId": [
                    {"name": "dependencies", "displayName": "Dependencies"},
                    {"name": "version-catalog", "displayName": "Version Catalog"},
                    {"name": "alias-not-finished", "displayName": "Dependency alias builder was not finished"}
                ],
                "solutions": ["Call .version() or .versionRef() on the alias builder"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "Task name 'test' matches multiple tasks: ':test', ':integrationTest'",
                "problemId": [
                    {"name": "invocation", "displayName": "Invocation"},
                    {"name": "ambiguous-matches", "displayName": "Ambiguous task matches"}
                ],
                "solutions": ["Use the full task path to disambiguate"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "Task ':test' option 'tests' expects a test name glob",
                "problemId": [
                    {"name": "invocation", "displayName": "Invocation"},
                    {"name": "invalid-task-option-value", "displayName": "Invalid task option value"}
                ],
                "solutions": ["Use a valid test name glob as the 'tests' option for the ':test' task"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "Could not execute build using connection to Gradle installation '/path/to/gradle-installation'.Gradle requires JVM 17 or later to run. Your build is currently configured to use JVM 8.",
                "problemId": [
                    {"name": "environment", "displayName": "Environment"},
                    {"name": "jdk", "displayName": "JDK"},
                    {
                        "name": "jdk-not-found",
                        "displayName": "Could not execute build using connection to Gradle installation"
                    }
                ],
                "solutions": ["Install JDK 17 or configure the path to an existing installation"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "Connection timeout connecting to repo.maven.apache.org",
                "problemId": [
                    {"name": "environment", "displayName": "Environment"},
                    {"name": "network", "displayName": "Network"},
                    {"name": "connection-timeout", "displayName": "Connection timeout"}
                ],
                "solutions": ["Check your network connection and firewall settings"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "Permission denied writing to /opt/app/output",
                "problemId": [
                    {"name": "environment", "displayName": "Environment"},
                    {"name": "filesystem", "displayName": "Filesystem"},
                    {"name": "permission-denied", "displayName": "Permission denied"}
                ],
                "solutions": ["Ensure the user has write permissions to the directory"]
            }, {
                "locations": [{"path": "build.gradle.kts", "line": 78, "column": 12}],
                "severity": "WARNING",
                "contextualLabel": "Using deprecated API: Task.project property access at execution time",
                "problemId": [
                    {"name": "deprecation", "displayName": "Deprecation"},
                    {"name": "deprecated-api-usage", "displayName": "Deprecated API usage"}
                ],
                "solutions": ["Access project properties at configuration time instead"]
            }, {
                "locations": [
                    {"path": "build.gradle.kts", "line": 45, "column": 4},
                    {"path": "build.gradle.kts", "line": 23, "column": 12}
                ],
                "severity": "ERROR",
                "contextualLabel": "Failed to resolve artifact for dependency 'com.example:library:1.0'",
                "problemId": [
                    {"name": "dependencies", "displayName": "Dependencies"},
                    {"name": "resolution", "displayName": "Dependency Resolution"},
                    {"name": "unknown-artifact-selection-failure", "displayName": "Unknown artifact selection failure"}
                ],
                "solutions": ["Check that the dependency exists and the repository is configured correctly"]
            }, {
                "severity": "WARNING",
                "contextualLabel": "Executing Gradle on JVM 8 is deprecated and will be removed in Gradle 10.0",
                "problemId": [
                    {"name": "deprecation", "displayName": "Deprecation"},
                    {
                        "name": "executing-gradle-on-jvm-versions-and-lower",
                        "displayName": "Executing Gradle on JVM versions and lower is deprecated"
                    }
                ],
                "solutions": ["Update to JVM 11 or higher"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "An unexpected error occurred during build execution",
                "problemId": [
                    {"name": "miscellaneous", "displayName": "Miscellaneous"},
                    {"name": "unknown-error", "displayName": "Unknown error"}
                ],
                "solutions": ["Check the build logs for more details"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "Cannot locate tasks that match ':ba' as task 'ba' is ambiguous in root project 'root'. Candidates are: 'bar', 'baz'.",
                "problemId": [
                    {"name": "invocation", "displayName": "Invocation"},
                    {"name": "task-selection-ambiguous", "displayName": "Ambiguous matches"}
                ],
                "solutions": ["Use the full task path to disambiguate"]
            }, {
                "severity": "ERROR",
                "contextualLabel": "Custom build validation failed: Project must have a README.md file",
                "problemId": [
                    {"name": "miscellaneous", "displayName": "Miscellaneous"},
                    {"name": "custom-failure", "displayName": "Custom failure"}
                ],
                "solutions": ["Add a README.md file to the project root"],
                "problemDetails": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis varius bibendum tellus ac pulvinar. Donec eget risus et enim placerat pellentesque vitae vitae est. Ut a nisl in ligula vehicula malesuada. Integer quam tellus, auctor sit amet semper vitae, tempor et orci. In tempus gravida dapibus. Sed vitae lectus ante. Etiam quis lectus sed mauris pretium auctor. Quisque laoreet sapien ornare, iaculis libero sed, condimentum magna. Donec at dignissim mi."
            }, {
                "locations": [{}, {"pluginId": "org.jetbrains.kotlin.multiplatform"}],
                "severity": "WARNING",
                "problemDetails": "This is scheduled to be removed in Gradle 10.0.",
                "contextualLabel": "The StartParameter.isConfigurationCacheRequested property has been deprecated.",
                "documentationLink": "https://docs.gradle.org/8.14/userguide/upgrading_version_8.html#deprecated_startparameter_is_configuration_cache_requested",
                "problemId": [
                    {"name": "deprecation", "displayName": "Deprecation"},
                    {
                        "name": "the-startparameter-isconfigurationcacherequested-property-has-been-deprecated",
                        "displayName": "The StartParameter.isConfigurationCacheRequested property has been deprecated."
                    }
                ],
                "solutions": ["Please use 'configurationCache.requested' property on 'BuildFeatures' service instead."]
            }, {
                "locations": [
                    {"pluginId": "org.jetbrains.kotlin.multiplatform"},
                    {"path": "testing/internal-testing/src/main/groovy/org/gradle/util/internal/RedirectStdOutAndErr.java"},
                    {"taskPath": "tassskskskspahththt"}
                ],
                "severity": "ADVICE",
                "contextualLabel": "testing/internal-testing/src/main/groovy/org/gradle/util/internal/RedirectStdOutAndErr.java uses or overrides a deprecated API.",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "java-compilation-note", "displayName": "Java compilation note"}
                ]
            }, {
                "severity": "ERROR",
                "contextualLabel": "No matching variant of org.codehaus.groovy:groovy:3.0.22 was found. The consumer was configured to find documentation of type 'gradle-source-folders' for use during runtime, as well as attribute 'gradlebuild.basics.GradleModuleApiAttribute' with value 'API' but:\n  - Variant 'compile':\n      - Incompatible because this component declares a library for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'enforced-platform-compile':\n      - Incompatible because this component declares an enforced platform for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'enforced-platform-runtime' declares a component for use during runtime:\n      - Incompatible because this component declares an enforced platform and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'javadoc' declares documentation for use during runtime:\n      - Incompatible because this component declares javadocs and the consumer needed documentation of type 'gradle-source-folders'\n      - Other compatible attribute:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n  - Variant 'platform-compile':\n      - Incompatible because this component declares a platform for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'platform-runtime' declares a component for use during runtime:\n      - Incompatible because this component declares a platform and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'runtime' declares a component for use during runtime:\n      - Incompatible because this component declares a library and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'sources' declares documentation for use during runtime:\n      - Incompatible because this component declares sources and the consumer needed documentation of type 'gradle-source-folders'\n      - Other compatible attribute:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')",
                "documentationLink": "https://docs.gradle.org/8.11-20240906074155+0000/userguide/variant_model.html#sec:variant-select-errors",
                "problemId": [
                    {"name": "dependencies", "displayName": "Dependencies"},
                    {"name": "variant-resolution", "displayName": "Variant Resolution"},
                    {"name": "no-compatible-variants", "displayName": "No variants exist that would match the request"}
                ]
            }, {
                "severity": "ERROR",
                "contextualLabel": "No matching variant of javax.inject:javax.inject:1 was found. The consumer was configured to find documentation of type 'gradle-source-folders' for use during runtime, as well as attribute 'gradlebuild.basics.GradleModuleApiAttribute' with value 'API' but:\n  - Variant 'compile':\n      - Incompatible because this component declares a library for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'enforced-platform-compile':\n      - Incompatible because this component declares an enforced platform for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'enforced-platform-runtime' declares a component for use during runtime:\n      - Incompatible because this component declares an enforced platform and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'javadoc' declares documentation for use during runtime:\n      - Incompatible because this component declares javadocs and the consumer needed documentation of type 'gradle-source-folders'\n      - Other compatible attribute:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n  - Variant 'platform-compile':\n      - Incompatible because this component declares a platform for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'platform-runtime' declares a component for use during runtime:\n      - Incompatible because this component declares a platform and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'runtime' declares a component for use during runtime:\n      - Incompatible because this component declares a library and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'sources' declares documentation for use during runtime:\n      - Incompatible because this component declares sources and the consumer needed documentation of type 'gradle-source-folders'\n      - Other compatible attribute:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')",
                "documentationLink": "https://docs.gradle.org/8.11-20240906074155+0000/userguide/variant_model.html#sec:variant-select-errors",
                "problemId": [
                    {"name": "dependencies", "displayName": "Dependencies"},
                    {"name": "variant-resolution", "displayName": "Variant Resolution"},
                    {"name": "no-compatible-variants", "displayName": "No variants exist that would match the request"}
                ]
            }, {
                "severity": "ERROR",
                "problemDetails": "javax.inject:javax.inject:1",
                "contextualLabel": "No matching variant of javax.inject:javax.inject:1 was found. The consumer was configured to find documentation of type 'gradle-source-folders' for use during runtime, as well as attribute 'gradlebuild.basics.GradleModuleApiAttribute' with value 'API' but:\n  - Variant 'compile':\n      - Incompatible because this component declares a library for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'enforced-platform-compile':\n      - Incompatible because this component declares an enforced platform for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'enforced-platform-runtime' declares a component for use during runtime:\n      - Incompatible because this component declares an enforced platform and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'javadoc' declares documentation for use during runtime:\n      - Incompatible because this component declares javadocs and the consumer needed documentation of type 'gradle-source-folders'\n      - Other compatible attribute:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n  - Variant 'platform-compile':\n      - Incompatible because this component declares a platform for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'platform-runtime' declares a component for use during runtime:\n      - Incompatible because this component declares a platform and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'runtime' declares a component for use during runtime:\n      - Incompatible because this component declares a library and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'sources' declares documentation for use during runtime:\n      - Incompatible because this component declares sources and the consumer needed documentation of type 'gradle-source-folders'\n      - Other compatible attribute:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')",
                "documentationLink": "https://docs.gradle.org/8.11-20240904103647+0000/userguide/variant_model.html#sec:variant-select-errors",
                "problemId": [
                    {"name": "dependencies", "displayName": "Dependencies"},
                    {"name": "variant-resolution", "displayName": "Variant Resolution"},
                    {"name": "no-compatible-variants", "displayName": "No variants exist that would match the request"}
                ]
            }, {
                "severity": "ERROR",
                "problemDetails": " com.google.code.findbugs:jsr305:3.0.2",
                "contextualLabel": "No matching variant of com.google.code.findbugs:jsr305:3.0.2 was found. The consumer was configured to find documentation of type 'gradle-source-folders' for use during runtime, as well as attribute 'gradlebuild.basics.GradleModuleApiAttribute' with value 'API' but:\n  - Variant 'compile':\n      - Incompatible because this component declares a library for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'enforced-platform-compile':\n      - Incompatible because this component declares an enforced platform for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'enforced-platform-runtime' declares a component for use during runtime:\n      - Incompatible because this component declares an enforced platform and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'javadoc' declares documentation for use during runtime:\n      - Incompatible because this component declares javadocs and the consumer needed documentation of type 'gradle-source-folders'\n      - Other compatible attribute:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n  - Variant 'platform-compile':\n      - Incompatible because this component declares a platform for use during compile-time and the consumer needed documentation for use during runtime\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'platform-runtime' declares a component for use during runtime:\n      - Incompatible because this component declares a platform and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'runtime' declares a component for use during runtime:\n      - Incompatible because this component declares a library and the consumer needed documentation\n      - Other compatible attributes:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')\n          - Doesn't say anything about the documentation type (required documentation of type 'gradle-source-folders')\n  - Variant 'sources' declares documentation for use during runtime:\n      - Incompatible because this component declares sources and the consumer needed documentation of type 'gradle-source-folders'\n      - Other compatible attribute:\n          - Doesn't say anything about gradlebuild.basics.GradleModuleApiAttribute (required 'API')",
                "documentationLink": "https://docs.gradle.org/8.11-20240904103647+0000/userguide/variant_model.html#sec:variant-select-errors",
                "problemId": [
                    {"name": "dependencies", "displayName": "Dependencies"},
                    {"name": "variant-resolution", "displayName": "Variant Resolution"},
                    {"name": "no-compatible-variants", "displayName": "No variants exist that would match the request"}
                ]
            }, {
                "severity": "ADVICE",
                "problemDetails": "Some input files use or override a deprecated API.",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "java-compilation-note", "displayName": "Java compilation note"}
                ],
                "solutions": []
            }, {
                "problemDetails": "details Task.project text.",
                "solutions": ["details 1 Task.project text. 1", "details 2 Task.project text. 2"],
                "problemId": [
                    {"name": "verification", "displayName": "Verification"},
                    {"name": "type-validation", "displayName": "Gradle type validation"},
                    {
                        "name": "invocation-of-Task-project-at-execution-time-is-unsupported",
                        "displayName": "invocation of `Task.project` at execution time is unsupported"
                    }
                ],
                "severity": "ERROR",
                "documentationLink": "https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:requirements:use_project_during_execution",
                "error": {
                    "summary": [{"text": "at "}, {"name": "build_44nw1yvono72kvlw7g0lm2rd7$_run_closure1$_closure2.doCall(/Users/paul/src/gradle-related/gradle/subprojects/docs/src/snippets/configurationCache/problemsGroovy/groovy/build.gradle:5)"}],
                    "parts": [
                        {"text": "org.gradle.api.InvalidUserCodeException: Invocation of 'Task.project' by task ':someTask' at execution time is unsupported."},
                        {"internalText": "\tat org.gradle.configurationcache.initialization.DefaultConfigurationCacheProblemsListener.onTaskExecutionAccessProblem(ConfigurationCacheProblemsListener.kt:55)\n\tat org.gradle.configurationcache.initialization.DefaultConfigurationCacheProblemsListener.onProjectAccess(ConfigurationCacheProblemsListener.kt:46)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\n\tat org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)\n\tat org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)\n\tat org.gradle.internal.event.DefaultListenerManager$ListenerDetails.dispatch(DefaultListenerManager.java:398)\n\tat org.gradle.internal.event.DefaultListenerManager$ListenerDetails.dispatch(DefaultListenerManager.java:380)\n\tat org.gradle.internal.event.AbstractBroadcastDispatch.dispatch(AbstractBroadcastDispatch.java:58)\n\tat org.gradle.internal.event.DefaultListenerManager$EventBroadcast$ListenerDispatch.dispatch(DefaultListenerManager.java:368)\n\tat org.gradle.internal.event.DefaultListenerManager$EventBroadcast.dispatch(DefaultListenerManager.java:179)\n\tat org.gradle.internal.event.DefaultListenerManager$EventBroadcast.dispatch(DefaultListenerManager.java:153)\n\tat org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:94)\n\tat com.sun.proxy.$Proxy75.onProjectAccess(Unknown Source)\n\tat org.gradle.api.internal.AbstractTask.notifyProjectAccess(AbstractTask.java:978)\n\tat org.gradle.api.internal.AbstractTask.getProject(AbstractTask.java:217)\n\tat jdk.internal.reflect.GeneratedMethodAccessor5.invoke(Unknown Source)\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\n\tat org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:101)\n\tat groovy.lang.MetaBeanProperty.getProperty(MetaBeanProperty.java:59)\n\tat org.gradle.internal.metaobject.BeanDynamicObject$MetaClassAdapter.getProperty(BeanDynamicObject.java:233)\n\tat org.gradle.internal.metaobject.BeanDynamicObject.tryGetProperty(BeanDynamicObject.java:176)\n\tat org.gradle.internal.metaobject.CompositeDynamicObject.tryGetProperty(CompositeDynamicObject.java:55)\n\tat org.gradle.internal.metaobject.AbstractDynamicObject.getProperty(AbstractDynamicObject.java:60)\n\tat org.gradle.api.DefaultTask_Decorated.getProperty(Unknown Source)\n\tat org.codehaus.groovy.runtime.InvokerHelper.getProperty(InvokerHelper.java:190)\n\tat groovy.lang.Closure.getPropertyTryThese(Closure.java:313)\n\tat groovy.lang.Closure.getPropertyDelegateFirst(Closure.java:303)\n\tat groovy.lang.Closure.getProperty(Closure.java:288)\n\tat org.codehaus.groovy.runtime.callsite.PogoGetPropertySite.getProperty(PogoGetPropertySite.java:49)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callGroovyObjectGetProperty(AbstractCallSite.java:309)\n"},
                        {"text": "\tat build_44nw1yvono72kvlw7g0lm2rd7$_run_closure1$_closure2.doCall(/Users/paul/src/gradle-related/gradle/subprojects/docs/src/snippets/configurationCache/problemsGroovy/groovy/build.gradle:5)\n"},
                        {"internalText": "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\n\tat org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:101)\n\tat groovy.lang.MetaMethod.doMethodInvoke(MetaMethod.java:323)\n\tat org.codehaus.groovy.runtime.metaclass.ClosureMetaClass.invokeMethod(ClosureMetaClass.java:263)\n\tat groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1041)\n\tat groovy.lang.Closure.call(Closure.java:405)\n\tat groovy.lang.Closure.call(Closure.java:421)\n\tat org.gradle.api.internal.AbstractTask$ClosureTaskAction.doExecute(AbstractTask.java:681)\n\tat org.gradle.api.internal.AbstractTask$ClosureTaskAction.lambda$execute$0(AbstractTask.java:668)\n\tat org.gradle.configuration.internal.DefaultUserCodeApplicationContext$CurrentApplication.reapply(DefaultUserCodeApplicationContext.java:86)\n\tat org.gradle.api.internal.AbstractTask$ClosureTaskAction.execute(AbstractTask.java:668)\n\tat org.gradle.api.internal.AbstractTask$ClosureTaskAction.execute(AbstractTask.java:643)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter$3.run(ExecuteActionsTaskExecuter.java:569)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$RunnableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:395)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$RunnableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:387)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$1.execute(DefaultBuildOperationExecutor.java:157)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:242)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:150)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:84)\n\tat org.gradle.internal.operations.DelegatingBuildOperationExecutor.run(DelegatingBuildOperationExecutor.java:31)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeAction(ExecuteActionsTaskExecuter.java:554)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeActions(ExecuteActionsTaskExecuter.java:537)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.access$300(ExecuteActionsTaskExecuter.java:108)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter$TaskExecution.executeWithPreviousOutputFiles(ExecuteActionsTaskExecuter.java:278)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter$TaskExecution.execute(ExecuteActionsTaskExecuter.java:267)\n\tat org.gradle.internal.execution.steps.ExecuteStep.lambda$execute$1(ExecuteStep.java:33)\n\tat java.base/java.util.Optional.orElseGet(Optional.java:369)\n\tat org.gradle.internal.execution.steps.ExecuteStep.execute(ExecuteStep.java:33)\n\tat org.gradle.internal.execution.steps.ExecuteStep.execute(ExecuteStep.java:26)\n\tat org.gradle.internal.execution.steps.CleanupOutputsStep.execute(CleanupOutputsStep.java:67)\n\tat org.gradle.internal.execution.steps.CleanupOutputsStep.execute(CleanupOutputsStep.java:36)\n\tat org.gradle.internal.execution.steps.ResolveInputChangesStep.execute(ResolveInputChangesStep.java:49)\n\tat org.gradle.internal.execution.steps.ResolveInputChangesStep.execute(ResolveInputChangesStep.java:34)\n\tat org.gradle.internal.execution.steps.CancelExecutionStep.execute(CancelExecutionStep.java:43)\n\tat org.gradle.internal.execution.steps.TimeoutStep.executeWithoutTimeout(TimeoutStep.java:73)\n\tat org.gradle.internal.execution.steps.TimeoutStep.execute(TimeoutStep.java:54)\n\tat org.gradle.internal.execution.steps.CatchExceptionStep.execute(CatchExceptionStep.java:34)\n\tat org.gradle.internal.execution.steps.CreateOutputsStep.execute(CreateOutputsStep.java:44)\n\tat org.gradle.internal.execution.steps.SnapshotOutputsStep.execute(SnapshotOutputsStep.java:54)\n\tat org.gradle.internal.execution.steps.SnapshotOutputsStep.execute(SnapshotOutputsStep.java:38)\n\tat org.gradle.internal.execution.steps.BroadcastChangingOutputsStep.execute(BroadcastChangingOutputsStep.java:49)\n\tat org.gradle.internal.execution.steps.CacheStep.executeWithoutCache(CacheStep.java:159)\n\tat org.gradle.internal.execution.steps.CacheStep.execute(CacheStep.java:72)\n\tat org.gradle.internal.execution.steps.CacheStep.execute(CacheStep.java:43)\n\tat org.gradle.internal.execution.steps.StoreExecutionStateStep.execute(StoreExecutionStateStep.java:44)\n\tat org.gradle.internal.execution.steps.StoreExecutionStateStep.execute(StoreExecutionStateStep.java:33)\n\tat org.gradle.internal.execution.steps.RecordOutputsStep.execute(RecordOutputsStep.java:38)\n\tat org.gradle.internal.execution.steps.RecordOutputsStep.execute(RecordOutputsStep.java:24)\n\tat org.gradle.internal.execution.steps.SkipUpToDateStep.executeBecause(SkipUpToDateStep.java:92)\n\tat org.gradle.internal.execution.steps.SkipUpToDateStep.lambda$execute$0(SkipUpToDateStep.java:85)\n\tat java.base/java.util.Optional.map(Optional.java:265)\n\tat org.gradle.internal.execution.steps.SkipUpToDateStep.execute(SkipUpToDateStep.java:55)\n\tat org.gradle.internal.execution.steps.SkipUpToDateStep.execute(SkipUpToDateStep.java:39)\n\tat org.gradle.internal.execution.steps.ResolveChangesStep.execute(ResolveChangesStep.java:76)\n\tat org.gradle.internal.execution.steps.ResolveChangesStep.execute(ResolveChangesStep.java:37)\n\tat org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsFinishedStep.execute(MarkSnapshottingInputsFinishedStep.java:36)\n\tat org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsFinishedStep.execute(MarkSnapshottingInputsFinishedStep.java:26)\n\tat org.gradle.internal.execution.steps.ResolveCachingStateStep.execute(ResolveCachingStateStep.java:94)\n\tat org.gradle.internal.execution.steps.ResolveCachingStateStep.execute(ResolveCachingStateStep.java:49)\n\tat org.gradle.internal.execution.steps.CaptureStateBeforeExecutionStep.execute(CaptureStateBeforeExecutionStep.java:79)\n\tat org.gradle.internal.execution.steps.CaptureStateBeforeExecutionStep.execute(CaptureStateBeforeExecutionStep.java:53)\n\tat org.gradle.internal.execution.steps.ValidateStep.execute(ValidateStep.java:74)\n\tat org.gradle.internal.execution.steps.SkipEmptyWorkStep.lambda$execute$2(SkipEmptyWorkStep.java:78)\n\tat java.base/java.util.Optional.orElseGet(Optional.java:369)\n\tat org.gradle.internal.execution.steps.SkipEmptyWorkStep.execute(SkipEmptyWorkStep.java:78)\n\tat org.gradle.internal.execution.steps.SkipEmptyWorkStep.execute(SkipEmptyWorkStep.java:34)\n\tat org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsStartedStep.execute(MarkSnapshottingInputsStartedStep.java:39)\n\tat org.gradle.internal.execution.steps.LoadExecutionStateStep.execute(LoadExecutionStateStep.java:40)\n\tat org.gradle.internal.execution.steps.LoadExecutionStateStep.execute(LoadExecutionStateStep.java:28)\n\tat org.gradle.internal.execution.impl.DefaultWorkExecutor.execute(DefaultWorkExecutor.java:33)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeIfValid(ExecuteActionsTaskExecuter.java:194)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.execute(ExecuteActionsTaskExecuter.java:186)\n\tat org.gradle.api.internal.tasks.execution.CleanupStaleOutputsExecuter.execute(CleanupStaleOutputsExecuter.java:114)\n\tat org.gradle.api.internal.tasks.execution.FinalizePropertiesTaskExecuter.execute(FinalizePropertiesTaskExecuter.java:46)\n\tat org.gradle.api.internal.tasks.execution.ResolveTaskExecutionModeExecuter.execute(ResolveTaskExecutionModeExecuter.java:62)\n\tat org.gradle.api.internal.tasks.execution.SkipTaskWithNoActionsExecuter.execute(SkipTaskWithNoActionsExecuter.java:57)\n\tat org.gradle.api.internal.tasks.execution.SkipOnlyIfTaskExecuter.execute(SkipOnlyIfTaskExecuter.java:56)\n\tat org.gradle.api.internal.tasks.execution.CatchExceptionTaskExecuter.execute(CatchExceptionTaskExecuter.java:36)\n\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.executeTask(EventFiringTaskExecuter.java:77)\n\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:55)\n\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:52)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$CallableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:409)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$CallableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:399)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$1.execute(DefaultBuildOperationExecutor.java:157)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:242)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:150)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.call(DefaultBuildOperationExecutor.java:94)\n\tat org.gradle.internal.operations.DelegatingBuildOperationExecutor.call(DelegatingBuildOperationExecutor.java:36)\n\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter.execute(EventFiringTaskExecuter.java:52)\n\tat org.gradle.execution.plan.LocalTaskNodeExecutor.execute(LocalTaskNodeExecutor.java:41)\n\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:372)\n\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:359)\n\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:352)\n\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:338)\n\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.lambda$run$0(DefaultPlanExecutor.java:127)\n\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.execute(DefaultPlanExecutor.java:191)\n\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.executeNextNode(DefaultPlanExecutor.java:182)\n\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.run(DefaultPlanExecutor.java:124)\n\tat org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)\n\tat org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48)\n\tat java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)\n\tat java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)\n\tat org.gradle.internal.concurrent.ThreadFactoryImpl$ManagedThreadRunnable.run(ThreadFactoryImpl.java:56)\n\tat java.base/java.lang.Thread.run(Thread.java:834)\n"}
                    ]
                }
            }, {
                "problemDetails": "details Task.project text.",
                "documentationLink": "https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:requirements:disallowed_types",
                "locations": [{"path": "project3/build.gradle", "line": 5, "column": 4}],
                "problemId": [
                    {"name": "verification", "displayName": "Verification"},
                    {"name": "property-validation", "displayName": "Gradle property validation"},
                    {
                        "name": "cannot-serialize-object-of-type-java-lang-Thread-a-subtype-of-java-lang-Thread-as-these-are-not-supported-with-the-configuration-cache",
                        "displayName": "cannot serialize object of type `java.lang.Thread`, a subtype of `java.lang.Thread`, as these are not supported with the configuration cache."
                    }
                ],
                "severity": "WARNING"
            }, {
                "locations": [
                    {"path": "project1/build.gradle", "line": 5, "column": 0, "length": 4},
                    {"path": "project2/build.gradle"}, {"path": "project3/build.gradle", "line": 5, "column": 0}
                ],
                "problemId": [{
                    "name": "Project-sub-c-cannot-dynamically-lookup-a-property-in-the-parent-project",
                    "displayName": "Project `:sub-c` cannot dynamically lookup a property in the parent project"
                }],
                "severity": "ERROR",
                "error": {}
            }, {
                "locations": [{
                    "path": "lib/src/main/java/org/gradle/example/MyLibraryClass.java",
                    "line": 5,
                    "column": 24,
                    "length": 0
                }],
                "severity": "ERROR",
                "problemDetails": "lib/src/main/java/org/gradle/example/MyLibraryClass.java:5: error: '(' or '[' expected\n        new MyOtherLib raryClass();\n                       ^",
                "contextualLabel": "'(' or '[' expected",
                "problemId": [
                    {"name": "compilation", "displayName": "Compilation"},
                    {"name": "java", "displayName": "Java"},
                    {"name": "compiler-err-expected2", "displayName": "Java compilation error"}
                ],
                "solutions": ["Check your code and dependencies to fix the compilation error(s)"]
            }]
        };
    } else {
        // CC report (Configuration Cache) format
        return {
            ...baseConfig,
            "diagnostics": [
                {
                    "trace": [{"kind": "BuildLogic", "location": "build file 'build.gradle'"}],
                    "problem": [{"text": "invocation of "}, {"name": "Task.project"}, {"text": " at execution time is unsupported."}],
                    "documentationLink": "https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:requirements:use_project_during_execution",
                    "error": {
                        "summary": [{"text": "at "}, {"name": "build_44nw1yvono72kvlw7g0lm2rd7$_run_closure1$_closure2.doCall(/Users/paul/src/gradle-related/gradle/subprojects/docs/src/snippets/configurationCache/problemsGroovy/groovy/build.gradle:5)"}],
                        "parts": [{
                            "text": "org.gradle.api.InvalidUserCodeException: Invocation of 'Task.project' by task ':someTask' at execution time is unsupported."
                        }, {
                            "internalText": "\tat org.gradle.configurationcache.initialization.DefaultConfigurationCacheProblemsListener.onTaskExecutionAccessProblem(ConfigurationCacheProblemsListener.kt:55)\n\tat org.gradle.configurationcache.initialization.DefaultConfigurationCacheProblemsListener.onProjectAccess(ConfigurationCacheProblemsListener.kt:46)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\n\tat org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)\n\tat org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)\n\tat org.gradle.internal.event.DefaultListenerManager$ListenerDetails.dispatch(DefaultListenerManager.java:398)\n\tat org.gradle.internal.event.DefaultListenerManager$ListenerDetails.dispatch(DefaultListenerManager.java:380)\n\tat org.gradle.internal.event.AbstractBroadcastDispatch.dispatch(AbstractBroadcastDispatch.java:58)\n\tat org.gradle.internal.event.DefaultListenerManager$EventBroadcast$ListenerDispatch.dispatch(DefaultListenerManager.java:368)\n\tat org.gradle.internal.event.DefaultListenerManager$EventBroadcast.dispatch(DefaultListenerManager.java:179)\n\tat org.gradle.internal.event.DefaultListenerManager$EventBroadcast.dispatch(DefaultListenerManager.java:153)\n\tat org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:94)\n\tat com.sun.proxy.$Proxy75.onProjectAccess(Unknown Source)\n\tat org.gradle.api.internal.AbstractTask.notifyProjectAccess(AbstractTask.java:978)\n\tat org.gradle.api.internal.AbstractTask.getProject(AbstractTask.java:217)\n\tat jdk.internal.reflect.GeneratedMethodAccessor5.invoke(Unknown Source)\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\n\tat org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:101)\n\tat groovy.lang.MetaBeanProperty.getProperty(MetaBeanProperty.java:59)\n\tat org.gradle.internal.metaobject.BeanDynamicObject$MetaClassAdapter.getProperty(BeanDynamicObject.java:233)\n\tat org.gradle.internal.metaobject.BeanDynamicObject.tryGetProperty(BeanDynamicObject.java:176)\n\tat org.gradle.internal.metaobject.CompositeDynamicObject.tryGetProperty(CompositeDynamicObject.java:55)\n\tat org.gradle.internal.metaobject.AbstractDynamicObject.getProperty(AbstractDynamicObject.java:60)\n\tat org.gradle.api.DefaultTask_Decorated.getProperty(Unknown Source)\n\tat org.codehaus.groovy.runtime.InvokerHelper.getProperty(InvokerHelper.java:190)\n\tat groovy.lang.Closure.getPropertyTryThese(Closure.java:313)\n\tat groovy.lang.Closure.getPropertyDelegateFirst(Closure.java:303)\n\tat groovy.lang.Closure.getProperty(Closure.java:288)\n\tat org.codehaus.groovy.runtime.callsite.PogoGetPropertySite.getProperty(PogoGetPropertySite.java:49)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callGroovyObjectGetProperty(AbstractCallSite.java:309)\n"
                        }, {
                            "text": "\tat build_44nw1yvono72kvlw7g0lm2rd7$_run_closure1$_closure2.doCall(/Users/paul/src/gradle-related/gradle/subprojects/docs/src/snippets/configurationCache/problemsGroovy/groovy/build.gradle:5)\n"
                        }, {
                            "internalText": "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\n\tat org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:101)\n\tat groovy.lang.MetaMethod.doMethodInvoke(MetaMethod.java:323)\n\tat org.codehaus.groovy.runtime.metaclass.ClosureMetaClass.invokeMethod(ClosureMetaClass.java:263)\n\tat groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1041)\n\tat groovy.lang.Closure.call(Closure.java:405)\n\tat groovy.lang.Closure.call(Closure.java:421)\n\tat org.gradle.api.internal.AbstractTask$ClosureTaskAction.doExecute(AbstractTask.java:681)\n\tat org.gradle.api.internal.AbstractTask$ClosureTaskAction.lambda$execute$0(AbstractTask.java:668)\n\tat org.gradle.configuration.internal.DefaultUserCodeApplicationContext$CurrentApplication.reapply(DefaultUserCodeApplicationContext.java:86)\n\tat org.gradle.api.internal.AbstractTask$ClosureTaskAction.execute(AbstractTask.java:668)\n\tat org.gradle.api.internal.AbstractTask$ClosureTaskAction.execute(AbstractTask.java:643)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter$3.run(ExecuteActionsTaskExecuter.java:569)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$RunnableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:395)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$RunnableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:387)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$1.execute(DefaultBuildOperationExecutor.java:157)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:242)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:150)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:84)\n\tat org.gradle.internal.operations.DelegatingBuildOperationExecutor.run(DelegatingBuildOperationExecutor.java:31)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeAction(ExecuteActionsTaskExecuter.java:554)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeActions(ExecuteActionsTaskExecuter.java:537)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.access$300(ExecuteActionsTaskExecuter.java:108)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter$TaskExecution.executeWithPreviousOutputFiles(ExecuteActionsTaskExecuter.java:278)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter$TaskExecution.execute(ExecuteActionsTaskExecuter.java:267)\n\tat org.gradle.internal.execution.steps.ExecuteStep.lambda$execute$1(ExecuteStep.java:33)\n\tat java.base/java.util.Optional.orElseGet(Optional.java:369)\n\tat org.gradle.internal.execution.steps.ExecuteStep.execute(ExecuteStep.java:33)\n\tat org.gradle.internal.execution.steps.ExecuteStep.execute(ExecuteStep.java:26)\n\tat org.gradle.internal.execution.steps.CleanupOutputsStep.execute(CleanupOutputsStep.java:67)\n\tat org.gradle.internal.execution.steps.CleanupOutputsStep.execute(CleanupOutputsStep.java:36)\n\tat org.gradle.internal.execution.steps.ResolveInputChangesStep.execute(ResolveInputChangesStep.java:49)\n\tat org.gradle.internal.execution.steps.ResolveInputChangesStep.execute(ResolveInputChangesStep.java:34)\n\tat org.gradle.internal.execution.steps.CancelExecutionStep.execute(CancelExecutionStep.java:43)\n\tat org.gradle.internal.execution.steps.TimeoutStep.executeWithoutTimeout(TimeoutStep.java:73)\n\tat org.gradle.internal.execution.steps.TimeoutStep.execute(TimeoutStep.java:54)\n\tat org.gradle.internal.execution.steps.CatchExceptionStep.execute(CatchExceptionStep.java:34)\n\tat org.gradle.internal.execution.steps.CreateOutputsStep.execute(CreateOutputsStep.java:44)\n\tat org.gradle.internal.execution.steps.SnapshotOutputsStep.execute(SnapshotOutputsStep.java:54)\n\tat org.gradle.internal.execution.steps.SnapshotOutputsStep.execute(SnapshotOutputsStep.java:38)\n\tat org.gradle.internal.execution.steps.BroadcastChangingOutputsStep.execute(BroadcastChangingOutputsStep.java:49)\n\tat org.gradle.internal.execution.steps.CacheStep.executeWithoutCache(CacheStep.java:159)\n\tat org.gradle.internal.execution.steps.CacheStep.execute(CacheStep.java:72)\n\tat org.gradle.internal.execution.steps.CacheStep.execute(CacheStep.java:43)\n\tat org.gradle.internal.execution.steps.StoreExecutionStateStep.execute(StoreExecutionStateStep.java:44)\n\tat org.gradle.internal.execution.steps.StoreExecutionStateStep.execute(StoreExecutionStateStep.java:33)\n\tat org.gradle.internal.execution.steps.RecordOutputsStep.execute(RecordOutputsStep.java:38)\n\tat org.gradle.internal.execution.steps.RecordOutputsStep.execute(RecordOutputsStep.java:24)\n\tat org.gradle.internal.execution.steps.SkipUpToDateStep.executeBecause(SkipUpToDateStep.java:92)\n\tat org.gradle.internal.execution.steps.SkipUpToDateStep.lambda$execute$0(SkipUpToDateStep.java:85)\n\tat java.base/java.util.Optional.map(Optional.java:265)\n\tat org.gradle.internal.execution.steps.SkipUpToDateStep.execute(SkipUpToDateStep.java:55)\n\tat org.gradle.internal.execution.steps.SkipUpToDateStep.execute(SkipUpToDateStep.java:39)\n\tat org.gradle.internal.execution.steps.ResolveChangesStep.execute(ResolveChangesStep.java:76)\n\tat org.gradle.internal.execution.steps.ResolveChangesStep.execute(ResolveChangesStep.java:37)\n\tat org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsFinishedStep.execute(MarkSnapshottingInputsFinishedStep.java:36)\n\tat org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsFinishedStep.execute(MarkSnapshottingInputsFinishedStep.java:26)\n\tat org.gradle.internal.execution.steps.ResolveCachingStateStep.execute(ResolveCachingStateStep.java:94)\n\tat org.gradle.internal.execution.steps.ResolveCachingStateStep.execute(ResolveCachingStateStep.java:49)\n\tat org.gradle.internal.execution.steps.CaptureStateBeforeExecutionStep.execute(CaptureStateBeforeExecutionStep.java:79)\n\tat org.gradle.internal.execution.steps.CaptureStateBeforeExecutionStep.execute(CaptureStateBeforeExecutionStep.java:53)\n\tat org.gradle.internal.execution.steps.ValidateStep.execute(ValidateStep.java:74)\n\tat org.gradle.internal.execution.steps.SkipEmptyWorkStep.lambda$execute$2(SkipEmptyWorkStep.java:78)\n\tat java.base/java.util.Optional.orElseGet(Optional.java:369)\n\tat org.gradle.internal.execution.steps.SkipEmptyWorkStep.execute(SkipEmptyWorkStep.java:78)\n\tat org.gradle.internal.execution.steps.SkipEmptyWorkStep.execute(SkipEmptyWorkStep.java:34)\n\tat org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsStartedStep.execute(MarkSnapshottingInputsStartedStep.java:39)\n\tat org.gradle.internal.execution.steps.LoadExecutionStateStep.execute(LoadExecutionStateStep.java:40)\n\tat org.gradle.internal.execution.steps.LoadExecutionStateStep.execute(LoadExecutionStateStep.java:28)\n\tat org.gradle.internal.execution.impl.DefaultWorkExecutor.execute(DefaultWorkExecutor.java:33)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeIfValid(ExecuteActionsTaskExecuter.java:194)\n\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.execute(ExecuteActionsTaskExecuter.java:186)\n\tat org.gradle.api.internal.tasks.execution.CleanupStaleOutputsExecuter.execute(CleanupStaleOutputsExecuter.java:114)\n\tat org.gradle.api.internal.tasks.execution.FinalizePropertiesTaskExecuter.execute(FinalizePropertiesTaskExecuter.java:46)\n\tat org.gradle.api.internal.tasks.execution.ResolveTaskExecutionModeExecuter.execute(ResolveTaskExecutionModeExecuter.java:62)\n\tat org.gradle.api.internal.tasks.execution.SkipTaskWithNoActionsExecuter.execute(SkipTaskWithNoActionsExecuter.java:57)\n\tat org.gradle.api.internal.tasks.execution.SkipOnlyIfTaskExecuter.execute(SkipOnlyIfTaskExecuter.java:56)\n\tat org.gradle.api.internal.tasks.execution.CatchExceptionTaskExecuter.execute(CatchExceptionTaskExecuter.java:36)\n\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.executeTask(EventFiringTaskExecuter.java:77)\n\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:55)\n\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter$1.call(EventFiringTaskExecuter.java:52)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$CallableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:409)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$CallableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:399)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$1.execute(DefaultBuildOperationExecutor.java:157)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:242)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:150)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.call(DefaultBuildOperationExecutor.java:94)\n\tat org.gradle.internal.operations.DelegatingBuildOperationExecutor.call(DelegatingBuildOperationExecutor.java:36)\n\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecuter.execute(EventFiringTaskExecuter.java:52)\n\tat org.gradle.execution.plan.LocalTaskNodeExecutor.execute(LocalTaskNodeExecutor.java:41)\n\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:372)\n\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:359)\n\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:352)\n\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:338)\n\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.lambda$run$0(DefaultPlanExecutor.java:127)\n\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.execute(DefaultPlanExecutor.java:191)\n\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.executeNextNode(DefaultPlanExecutor.java:182)\n\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.run(DefaultPlanExecutor.java:124)\n\tat org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)\n\tat org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48)\n\tat java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)\n\tat java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)\n\tat org.gradle.internal.concurrent.ThreadFactoryImpl$ManagedThreadRunnable.run(ThreadFactoryImpl.java:56)\n\tat java.base/java.lang.Thread.run(Thread.java:834)\n"
                        }]
                    }
                },
                {
                    "trace": [
                        {"kind": "SystemProperty", "name": "org.example.property"},
                        {"kind": "BuildLogic", "location": "build file 'build.gradle.kts'"}
                    ],
                    "problem": [{"text": "cannot "}, {"text": "serialize"}, {"text": " object of type "}, {"name": "java.lang.Thread"}, {"text": ", a subtype of "}, {"name": "java.lang.Thread"}, {"text": ","}, {"text": " as these are not supported with the configuration cache."}],
                    "documentationLink": "https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:requirements:disallowed_types"
                },
                {
                    "trace": [{"kind": "BuildLogic", "location": "build file 'build.gradle'"}],
                    "input": [{"text": "system property "}, {"name": "someMessage"}],
                    "documentationLink": "https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:requirements:reading_sys_props_and_env_vars"
                },
                {
                    "trace": [
                        {"kind": "PropertyUsage", "name": "foo", "from": ":sub-b"},
                        {"kind": "Project", "path": ":sub-b"},
                        {"kind": "BuildLogic", "location": "build file 'sub-b/build.gradle'"}
                    ],
                    "problem": [{"text": "Project "}, {"name": ":sub-b"}, {"text": " cannot dynamically lookup a "}, {"text": "property"}, {"text": " in the parent project "}, {"name": ":"}],
                    "error": {
                        "parts": [{
                            "text": "org.gradle.api.InvalidUserCodeException: Project ':sub-b' cannot dynamically lookup a property in the parent project ':'\n"
                        }, {
                            "internalText": "\tat org.gradle.configurationcache.CrossProjectModelAccessTrackingParentDynamicObject.onAccess(CrossProjectModelAccessTrackingParentDynamicObject.kt:133)\n\tat org.gradle.configurationcache.CrossProjectModelAccessTrackingParentDynamicObject.tryGetProperty(CrossProjectModelAccessTrackingParentDynamicObject.kt:60)\n\tat org.gradle.internal.metaobject.CompositeDynamicObject.tryGetProperty(CompositeDynamicObject.java:55)\n\tat org.gradle.groovy.scripts.BasicScript$ScriptDynamicObject.tryGetProperty(BasicScript.java:147)\n\tat org.gradle.internal.metaobject.AbstractDynamicObject.getProperty(AbstractDynamicObject.java:60)\n\tat org.gradle.groovy.scripts.BasicScript.getProperty(BasicScript.java:66)\n\tat org.codehaus.groovy.runtime.callsite.PogoGetPropertySite.getProperty(PogoGetPropertySite.java:49)\n\tat org.codehaus.groovy.runtime.callsite.AbstractCallSite.callGroovyObjectGetProperty(AbstractCallSite.java:341)\n"
                        }, {
                            "text": "\tat build_6mp8ik6osksmvfonrl02vsp2g.run(/Users/sergey.igushkin/Projects/Playgrounds/gradle-isolated-projects/sub-b/build.gradle:8)\n"
                        }, {
                            "internalText": "\tat org.gradle.groovy.scripts.internal.DefaultScriptRunnerFactory$ScriptRunnerImpl.run(DefaultScriptRunnerFactory.java:91)\n\tat org.gradle.configuration.DefaultScriptPluginFactory$ScriptPluginImpl.lambda$apply$0(DefaultScriptPluginFactory.java:133)\n\tat org.gradle.configuration.ProjectScriptTarget.addConfiguration(ProjectScriptTarget.java:79)\n\tat org.gradle.configuration.DefaultScriptPluginFactory$ScriptPluginImpl.apply(DefaultScriptPluginFactory.java:136)\n\tat org.gradle.configuration.BuildOperationScriptPlugin$1.run(BuildOperationScriptPlugin.java:65)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:29)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:26)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.run(DefaultBuildOperationRunner.java:47)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:68)\n\tat org.gradle.configuration.BuildOperationScriptPlugin.lambda$apply$0(BuildOperationScriptPlugin.java:62)\n\tat org.gradle.configuration.internal.DefaultUserCodeApplicationContext.apply(DefaultUserCodeApplicationContext.java:44)\n\tat org.gradle.configuration.BuildOperationScriptPlugin.apply(BuildOperationScriptPlugin.java:62)\n\tat org.gradle.api.internal.project.DefaultProjectStateRegistry$ProjectStateImpl.lambda$applyToMutableState$0(DefaultProjectStateRegistry.java:351)\n\tat org.gradle.api.internal.project.DefaultProjectStateRegistry$ProjectStateImpl.fromMutableState(DefaultProjectStateRegistry.java:369)\n\tat org.gradle.api.internal.project.DefaultProjectStateRegistry$ProjectStateImpl.applyToMutableState(DefaultProjectStateRegistry.java:350)\n\tat org.gradle.configuration.project.BuildScriptProcessor.execute(BuildScriptProcessor.java:42)\n\tat org.gradle.configuration.project.BuildScriptProcessor.execute(BuildScriptProcessor.java:26)\n\tat org.gradle.configuration.project.ConfigureActionsProjectEvaluator.evaluate(ConfigureActionsProjectEvaluator.java:35)\n\tat org.gradle.configuration.project.LifecycleProjectEvaluator$EvaluateProject.lambda$run$0(LifecycleProjectEvaluator.java:109)\n\tat org.gradle.api.internal.project.DefaultProjectStateRegistry$ProjectStateImpl.lambda$applyToMutableState$0(DefaultProjectStateRegistry.java:351)\n\tat org.gradle.api.internal.project.DefaultProjectStateRegistry$ProjectStateImpl.fromMutableState(DefaultProjectStateRegistry.java:369)\n\tat org.gradle.api.internal.project.DefaultProjectStateRegistry$ProjectStateImpl.applyToMutableState(DefaultProjectStateRegistry.java:350)\n\tat org.gradle.configuration.project.LifecycleProjectEvaluator$EvaluateProject.run(LifecycleProjectEvaluator.java:100)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:29)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:26)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.run(DefaultBuildOperationRunner.java:47)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:68)\n\tat org.gradle.configuration.project.LifecycleProjectEvaluator.evaluate(LifecycleProjectEvaluator.java:72)\n\tat org.gradle.api.internal.project.DefaultProject.evaluate(DefaultProject.java:766)\n\tat org.gradle.api.internal.project.DefaultProject.evaluate(DefaultProject.java:154)\n\tat org.gradle.api.internal.project.ProjectLifecycleController.lambda$ensureSelfConfigured$1(ProjectLifecycleController.java:63)\n\tat org.gradle.internal.model.StateTransitionController.lambda$doTransition$12(StateTransitionController.java:236)\n\tat org.gradle.internal.model.StateTransitionController.doTransition(StateTransitionController.java:247)\n\tat org.gradle.internal.model.StateTransitionController.doTransition(StateTransitionController.java:235)\n\tat org.gradle.internal.model.StateTransitionController.lambda$maybeTransitionIfNotCurrentlyTransitioning$9(StateTransitionController.java:196)\n\tat org.gradle.internal.work.DefaultSynchronizer.withLock(DefaultSynchronizer.java:34)\n\tat org.gradle.internal.model.StateTransitionController.maybeTransitionIfNotCurrentlyTransitioning(StateTransitionController.java:192)\n\tat org.gradle.api.internal.project.ProjectLifecycleController.ensureSelfConfigured(ProjectLifecycleController.java:63)\n\tat org.gradle.api.internal.project.DefaultProjectStateRegistry$ProjectStateImpl.ensureConfigured(DefaultProjectStateRegistry.java:325)\n\tat org.gradle.execution.TaskPathProjectEvaluator.configure(TaskPathProjectEvaluator.java:33)\n\tat org.gradle.execution.DefaultTaskSelector.getSelection(DefaultTaskSelector.java:99)\n\tat org.gradle.execution.DefaultTaskSelector.getSelection(DefaultTaskSelector.java:82)\n\tat org.gradle.execution.CompositeAwareTaskSelector.getSelection(CompositeAwareTaskSelector.java:94)\n\tat org.gradle.execution.commandline.CommandLineTaskParser.parseTasks(CommandLineTaskParser.java:43)\n\tat org.gradle.execution.TaskNameResolvingBuildConfigurationAction.configure(TaskNameResolvingBuildConfigurationAction.java:44)\n\tat org.gradle.execution.DefaultBuildConfigurationActionExecuter.configure(DefaultBuildConfigurationActionExecuter.java:49)\n\tat org.gradle.execution.DefaultBuildConfigurationActionExecuter.access$000(DefaultBuildConfigurationActionExecuter.java:25)\n\tat org.gradle.execution.DefaultBuildConfigurationActionExecuter$1.proceed(DefaultBuildConfigurationActionExecuter.java:62)\n\tat org.gradle.execution.DefaultTasksBuildExecutionAction.configure(DefaultTasksBuildExecutionAction.java:48)\n\tat org.gradle.execution.DefaultBuildConfigurationActionExecuter.configure(DefaultBuildConfigurationActionExecuter.java:49)\n\tat org.gradle.execution.DefaultBuildConfigurationActionExecuter.lambda$select$0(DefaultBuildConfigurationActionExecuter.java:36)\n\tat org.gradle.internal.Factories$1.create(Factories.java:31)\n\tat org.gradle.internal.work.DefaultWorkerLeaseService.withReplacedLocks(DefaultWorkerLeaseService.java:345)\n\tat org.gradle.api.internal.project.DefaultProjectStateRegistry$DefaultBuildProjectRegistry.withMutableStateOfAllProjects(DefaultProjectStateRegistry.java:197)\n\tat org.gradle.api.internal.project.DefaultProjectStateRegistry$DefaultBuildProjectRegistry.withMutableStateOfAllProjects(DefaultProjectStateRegistry.java:190)\n\tat org.gradle.execution.DefaultBuildConfigurationActionExecuter.select(DefaultBuildConfigurationActionExecuter.java:35)\n\tat org.gradle.initialization.DefaultTaskExecutionPreparer.prepareForTaskExecution(DefaultTaskExecutionPreparer.java:42)\n\tat org.gradle.initialization.VintageBuildModelController.lambda$scheduleRequestedTasks$1(VintageBuildModelController.java:81)\n\tat org.gradle.internal.model.StateTransitionController.lambda$inState$1(StateTransitionController.java:110)\n\tat org.gradle.internal.model.StateTransitionController.lambda$inState$2(StateTransitionController.java:125)\n\tat org.gradle.internal.work.DefaultSynchronizer.withLock(DefaultSynchronizer.java:44)\n\tat org.gradle.internal.model.StateTransitionController.inState(StateTransitionController.java:121)\n\tat org.gradle.internal.model.StateTransitionController.inState(StateTransitionController.java:109)\n\tat org.gradle.initialization.VintageBuildModelController.scheduleRequestedTasks(VintageBuildModelController.java:81)\n\tat org.gradle.configurationcache.ConfigurationCacheAwareBuildModelController.scheduleRequestedTasks(ConfigurationCacheAwareBuildModelController.kt:60)\n\tat org.gradle.internal.build.DefaultBuildLifecycleController$DefaultWorkGraphBuilder.addRequestedTasks(DefaultBuildLifecycleController.java:244)\n\tat org.gradle.internal.build.DefaultBuildLifecycleController.lambda$populateWorkGraph$4(DefaultBuildLifecycleController.java:144)\n\tat org.gradle.internal.build.DefaultBuildWorkPreparer.populateWorkGraph(DefaultBuildWorkPreparer.java:42)\n\tat org.gradle.internal.build.BuildOperationFiringBuildWorkPreparer$PopulateWorkGraph.populateTaskGraph(BuildOperationFiringBuildWorkPreparer.java:141)\n\tat org.gradle.internal.build.BuildOperationFiringBuildWorkPreparer$PopulateWorkGraph.run(BuildOperationFiringBuildWorkPreparer.java:91)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:29)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:26)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.run(DefaultBuildOperationRunner.java:47)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:68)\n\tat org.gradle.internal.build.BuildOperationFiringBuildWorkPreparer.populateWorkGraph(BuildOperationFiringBuildWorkPreparer.java:68)\n\tat org.gradle.internal.build.DefaultBuildLifecycleController.lambda$populateWorkGraph$5(DefaultBuildLifecycleController.java:144)\n\tat org.gradle.internal.model.StateTransitionController.lambda$inState$1(StateTransitionController.java:110)\n\tat org.gradle.internal.model.StateTransitionController.lambda$inState$2(StateTransitionController.java:125)\n\tat org.gradle.internal.work.DefaultSynchronizer.withLock(DefaultSynchronizer.java:44)\n\tat org.gradle.internal.model.StateTransitionController.inState(StateTransitionController.java:121)\n\tat org.gradle.internal.model.StateTransitionController.inState(StateTransitionController.java:109)\n\tat org.gradle.internal.build.DefaultBuildLifecycleController.populateWorkGraph(DefaultBuildLifecycleController.java:144)\n\tat org.gradle.internal.build.DefaultBuildWorkGraphController$DefaultBuildWorkGraph.populateWorkGraph(DefaultBuildWorkGraphController.java:126)\n\tat org.gradle.composite.internal.DefaultBuildController.populateWorkGraph(DefaultBuildController.java:71)\n\tat org.gradle.composite.internal.DefaultIncludedBuildTaskGraph$DefaultBuildTreeWorkGraphBuilder.withWorkGraph(DefaultIncludedBuildTaskGraph.java:142)\n\tat org.gradle.internal.buildtree.DefaultBuildTreeWorkPreparer.lambda$scheduleRequestedTasks$0(DefaultBuildTreeWorkPreparer.java:34)\n\tat org.gradle.composite.internal.DefaultIncludedBuildTaskGraph$DefaultBuildTreeWorkGraph$1.run(DefaultIncludedBuildTaskGraph.java:170)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:29)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:26)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.run(DefaultBuildOperationRunner.java:47)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:68)\n\tat org.gradle.composite.internal.DefaultIncludedBuildTaskGraph$DefaultBuildTreeWorkGraph.scheduleWork(DefaultIncludedBuildTaskGraph.java:167)\n\tat org.gradle.internal.buildtree.DefaultBuildTreeWorkPreparer.scheduleRequestedTasks(DefaultBuildTreeWorkPreparer.java:34)\n\tat org.gradle.configurationcache.ConfigurationCacheAwareBuildTreeWorkPreparer$scheduleRequestedTasks$1.invoke(ConfigurationCacheAwareBuildTreeWorkPreparer.kt:29)\n\tat org.gradle.configurationcache.ConfigurationCacheAwareBuildTreeWorkPreparer$scheduleRequestedTasks$1.invoke(ConfigurationCacheAwareBuildTreeWorkPreparer.kt:28)\n\tat org.gradle.configurationcache.DefaultConfigurationCache$loadOrScheduleRequestedTasks$1.invoke(DefaultConfigurationCache.kt:133)\n\tat org.gradle.configurationcache.DefaultConfigurationCache$loadOrScheduleRequestedTasks$1.invoke(DefaultConfigurationCache.kt:132)\n\tat org.gradle.configurationcache.DefaultConfigurationCache.runWorkThatContributesToCacheEntry(DefaultConfigurationCache.kt:276)\n\tat org.gradle.configurationcache.DefaultConfigurationCache.loadOrScheduleRequestedTasks(DefaultConfigurationCache.kt:132)\n\tat org.gradle.configurationcache.ConfigurationCacheAwareBuildTreeWorkPreparer.scheduleRequestedTasks(ConfigurationCacheAwareBuildTreeWorkPreparer.kt:28)\n\tat org.gradle.internal.buildtree.DefaultBuildTreeLifecycleController.lambda$doScheduleAndRunTasks$2(DefaultBuildTreeLifecycleController.java:89)\n\tat org.gradle.composite.internal.DefaultIncludedBuildTaskGraph.withNewWorkGraph(DefaultIncludedBuildTaskGraph.java:101)\n\tat org.gradle.internal.buildtree.DefaultBuildTreeLifecycleController.doScheduleAndRunTasks(DefaultBuildTreeLifecycleController.java:88)\n\tat org.gradle.internal.buildtree.DefaultBuildTreeLifecycleController.lambda$runBuild$4(DefaultBuildTreeLifecycleController.java:106)\n\tat org.gradle.internal.model.StateTransitionController.lambda$transition$5(StateTransitionController.java:166)\n\tat org.gradle.internal.model.StateTransitionController.doTransition(StateTransitionController.java:247)\n\tat org.gradle.internal.model.StateTransitionController.lambda$transition$6(StateTransitionController.java:166)\n\tat org.gradle.internal.work.DefaultSynchronizer.withLock(DefaultSynchronizer.java:44)\n\tat org.gradle.internal.model.StateTransitionController.transition(StateTransitionController.java:166)\n\tat org.gradle.internal.buildtree.DefaultBuildTreeLifecycleController.runBuild(DefaultBuildTreeLifecycleController.java:103)\n\tat org.gradle.internal.buildtree.DefaultBuildTreeLifecycleController.scheduleAndRunTasks(DefaultBuildTreeLifecycleController.java:69)\n\tat org.gradle.tooling.internal.provider.ExecuteBuildActionRunner.run(ExecuteBuildActionRunner.java:31)\n\tat org.gradle.launcher.exec.ChainingBuildActionRunner.run(ChainingBuildActionRunner.java:35)\n\tat org.gradle.internal.buildtree.ProblemReportingBuildActionRunner.run(ProblemReportingBuildActionRunner.java:49)\n\tat org.gradle.launcher.exec.BuildOutcomeReportingBuildActionRunner.run(BuildOutcomeReportingBuildActionRunner.java:65)\n\tat org.gradle.tooling.internal.provider.FileSystemWatchingBuildActionRunner.run(FileSystemWatchingBuildActionRunner.java:136)\n\tat org.gradle.launcher.exec.BuildCompletionNotifyingBuildActionRunner.run(BuildCompletionNotifyingBuildActionRunner.java:41)\n\tat org.gradle.launcher.exec.RootBuildLifecycleBuildActionExecutor.lambda$execute$0(RootBuildLifecycleBuildActionExecutor.java:40)\n\tat org.gradle.composite.internal.DefaultRootBuildState.run(DefaultRootBuildState.java:127)\n\tat org.gradle.launcher.exec.RootBuildLifecycleBuildActionExecutor.execute(RootBuildLifecycleBuildActionExecutor.java:40)\n\tat org.gradle.internal.buildtree.DefaultBuildTreeContext.execute(DefaultBuildTreeContext.java:40)\n\tat org.gradle.launcher.exec.BuildTreeLifecycleBuildActionExecutor.lambda$execute$0(BuildTreeLifecycleBuildActionExecutor.java:65)\n\tat org.gradle.internal.buildtree.BuildTreeState.run(BuildTreeState.java:53)\n\tat org.gradle.launcher.exec.BuildTreeLifecycleBuildActionExecutor.execute(BuildTreeLifecycleBuildActionExecutor.java:65)\n\tat org.gradle.launcher.exec.RunAsBuildOperationBuildActionExecutor$3.call(RunAsBuildOperationBuildActionExecutor.java:61)\n\tat org.gradle.launcher.exec.RunAsBuildOperationBuildActionExecutor$3.call(RunAsBuildOperationBuildActionExecutor.java:57)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:204)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$CallableBuildOperationWorker.execute(DefaultBuildOperationRunner.java:199)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)\n\tat org.gradle.internal.operations.DefaultBuildOperationRunner.call(DefaultBuildOperationRunner.java:53)\n\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.call(DefaultBuildOperationExecutor.java:73)\n\tat org.gradle.launcher.exec.RunAsBuildOperationBuildActionExecutor.execute(RunAsBuildOperationBuildActionExecutor.java:57)\n\tat org.gradle.launcher.exec.RunAsWorkerThreadBuildActionExecutor.lambda$execute$0(RunAsWorkerThreadBuildActionExecutor.java:36)\n\tat org.gradle.internal.work.DefaultWorkerLeaseService.withLocks(DefaultWorkerLeaseService.java:249)\n\tat org.gradle.internal.work.DefaultWorkerLeaseService.runAsWorkerThread(DefaultWorkerLeaseService.java:109)\n\tat org.gradle.launcher.exec.RunAsWorkerThreadBuildActionExecutor.execute(RunAsWorkerThreadBuildActionExecutor.java:36)\n\tat org.gradle.tooling.internal.provider.continuous.ContinuousBuildActionExecutor.execute(ContinuousBuildActionExecutor.java:110)\n\tat org.gradle.tooling.internal.provider.SubscribableBuildActionExecutor.execute(SubscribableBuildActionExecutor.java:64)\n\tat org.gradle.internal.session.DefaultBuildSessionContext.execute(DefaultBuildSessionContext.java:46)\n\tat org.gradle.tooling.internal.provider.BuildSessionLifecycleBuildActionExecuter$ActionImpl.apply(BuildSessionLifecycleBuildActionExecuter.java:100)\n\tat org.gradle.tooling.internal.provider.BuildSessionLifecycleBuildActionExecuter$ActionImpl.apply(BuildSessionLifecycleBuildActionExecuter.java:88)\n\tat org.gradle.internal.session.BuildSessionState.run(BuildSessionState.java:69)\n\tat org.gradle.tooling.internal.provider.BuildSessionLifecycleBuildActionExecuter.execute(BuildSessionLifecycleBuildActionExecuter.java:62)\n\tat org.gradle.tooling.internal.provider.BuildSessionLifecycleBuildActionExecuter.execute(BuildSessionLifecycleBuildActionExecuter.java:41)\n\tat org.gradle.tooling.internal.provider.StartParamsValidatingActionExecuter.execute(StartParamsValidatingActionExecuter.java:63)\n\tat org.gradle.tooling.internal.provider.StartParamsValidatingActionExecuter.execute(StartParamsValidatingActionExecuter.java:31)\n\tat org.gradle.tooling.internal.provider.SessionFailureReportingActionExecuter.execute(SessionFailureReportingActionExecuter.java:52)\n\tat org.gradle.tooling.internal.provider.SessionFailureReportingActionExecuter.execute(SessionFailureReportingActionExecuter.java:40)\n\tat org.gradle.tooling.internal.provider.SetupLoggingActionExecuter.execute(SetupLoggingActionExecuter.java:47)\n\tat org.gradle.tooling.internal.provider.SetupLoggingActionExecuter.execute(SetupLoggingActionExecuter.java:31)\n\tat org.gradle.launcher.daemon.server.exec.ExecuteBuild.doBuild(ExecuteBuild.java:65)\n\tat org.gradle.launcher.daemon.server.exec.BuildCommandOnly.execute(BuildCommandOnly.java:37)\n\tat org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)\n\tat org.gradle.launcher.daemon.server.exec.WatchForDisconnection.execute(WatchForDisconnection.java:39)\n\tat org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)\n\tat org.gradle.launcher.daemon.server.exec.ResetDeprecationLogger.execute(ResetDeprecationLogger.java:29)\n\tat org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)\n\tat org.gradle.launcher.daemon.server.exec.RequestStopIfSingleUsedDaemon.execute(RequestStopIfSingleUsedDaemon.java:35)\n\tat org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)\n\tat org.gradle.launcher.daemon.server.exec.ForwardClientInput$2.create(ForwardClientInput.java:78)\n\tat org.gradle.launcher.daemon.server.exec.ForwardClientInput$2.create(ForwardClientInput.java:75)\n\tat org.gradle.util.internal.Swapper.swap(Swapper.java:38)\n\tat org.gradle.launcher.daemon.server.exec.ForwardClientInput.execute(ForwardClientInput.java:75)\n\tat org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)\n\tat org.gradle.launcher.daemon.server.exec.LogAndCheckHealth.execute(LogAndCheckHealth.java:55)\n\tat org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)\n\tat org.gradle.launcher.daemon.server.exec.LogToClient.doBuild(LogToClient.java:63)\n\tat org.gradle.launcher.daemon.server.exec.BuildCommandOnly.execute(BuildCommandOnly.java:37)\n\tat org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)\n\tat org.gradle.launcher.daemon.server.exec.EstablishBuildEnvironment.doBuild(EstablishBuildEnvironment.java:84)\n\tat org.gradle.launcher.daemon.server.exec.BuildCommandOnly.execute(BuildCommandOnly.java:37)\n\tat org.gradle.launcher.daemon.server.api.DaemonCommandExecution.proceed(DaemonCommandExecution.java:104)\n\tat org.gradle.launcher.daemon.server.exec.StartBuildOrRespondWithBusy$1.run(StartBuildOrRespondWithBusy.java:52)\n\tat org.gradle.launcher.daemon.server.DaemonStateCoordinator$1.run(DaemonStateCoordinator.java:297)\n\tat org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)\n\tat org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48)\n\tat java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)\n\tat java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)\n\tat java.base/java.lang.Thread.run(Thread.java:833)\n"
                        }]
                    }
                },
                {
                    "trace": [
                        {"kind": "PropertyUsage", "name": "bar", "from": ":sub-c"},
                        {"kind": "Project", "path": ":sub-b"},
                        {"kind": "BuildLogic", "location": "build file 'sub-c/build.gradle'"}
                    ],
                    "problem": [{"text": "Project "}, {"name": ":sub-c"}, {"text": " cannot dynamically lookup a "}, {"text": "property"}, {"text": " in the parent project "}, {"name": ":"}],
                    "error": {}
                },
                {
                    "trace": [{"kind": "TaskPath", "path": ":anotherReportedlyIncompatible"}],
                    "incompatibleTask": [
                        {"text": "task "}, {"name": ":anotherReportedlyIncompatible"}, {"text": " is incompatible with the configuration cache. "},
                        {"text": "Reason: "}, {"text": "some other reason"}, {"text": "."}
                    ],
                    "documentationLink": "https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:task_opt_out"
                },
                {
                    "trace": [{"kind": "TaskPath", "path": ":reportedlyIncompatible"}],
                    "incompatibleTask": [
                        {"text": "task "}, {"name": ":reportedlyIncompatible"}, {"text": " is incompatible with the configuration cache. "},
                        {"text": "Reason: "}, {"text": "declares itself as not compatible"}, {"text": "."}
                    ],
                    "documentationLink": "https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:task_opt_out"
                }
            ]
        };
    }
}
