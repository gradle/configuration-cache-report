/*
 * Copyright 2026 the original author or authors.
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

package org.gradle.problems.internal.report.model

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue


/**
 * Verifies that the `@Serializable` models decode the exact wire format produced by
 * `ConfigurationCacheReport` (see `src/jsMain/resources/configuration-cache-report-data.js`).
 * These tests pin down wire compatibility independently of the JS-only decoding entry points.
 */
class JsModelWireCompatibilityTest {

    private
    val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decodes a configuration cache report`() {
        val summary = json.decodeFromString(
            JsConfigurationCacheSummary.serializer(),
            """
            {
                "buildName": "sampleProject",
                "cacheAction": "storing",
                "cacheActionDescription": [{"text": "Calculating "}, {"name": "build.gradle"}],
                "requestedTasks": "clean build",
                "documentationLink": "https://docs/cc",
                "totalProblemCount": 20,
                "uniqueProblemCount": 19,
                "overflownProblemCount": 1
            }
            """.trimIndent()
        )

        val diagnostics = json.decodeFromString(
            ListSerializer(JsDiagnostic.serializer()),
            """
            [
                    {
                        "trace": [{"kind": "BuildLogic", "location": "build file 'build.gradle'"}],
                        "problem": [{"text": "invocation of "}, {"name": "Task.project"}],
                        "documentationLink": "https://docs/problem",
                        "error": {
                            "summary": [{"text": "at "}, {"name": "build.gradle:5"}],
                            "parts": [{"text": "user frame"}, {"internalText": "internal frame"}]
                        }
                    },
                    {
                        "trace": [
                            {"kind": "SystemProperty", "name": "someMessage"},
                            {"kind": "Project", "path": ":sub-b"},
                            {"kind": "PropertyUsage", "name": "foo", "from": ":sub-b"}
                        ],
                        "input": [{"text": "system property "}, {"name": "someMessage"}]
                    },
                    {
                        "trace": [{"kind": "TaskPath", "path": ":incompatible"}],
                        "incompatibleTask": [{"text": "task "}, {"name": ":incompatible"}]
                    },
                    {
                        "trace": [],
                        "problem": [{"text": "no error details"}],
                        "error": {}
                    }
            ]
            """.trimIndent()
        )

        assertEquals("sampleProject", summary.buildName)
        assertEquals("storing", summary.cacheAction)
        assertEquals("clean build", summary.requestedTasks)
        assertEquals("https://docs/cc", summary.documentationLink)
        assertEquals(20, summary.totalProblemCount)
        assertEquals(19, summary.uniqueProblemCount)
        assertEquals(1, summary.overflownProblemCount)
        assertEquals(
            listOf(JsMessageFragment(text = "Calculating "), JsMessageFragment(name = "build.gradle")),
            summary.cacheActionDescription
        )
        assertEquals(4, diagnostics.size)

        val problem = diagnostics[0]
        assertEquals(listOf(JsMessageFragment(text = "invocation of "), JsMessageFragment(name = "Task.project")), problem.problem)
        assertNull(problem.input)
        assertNull(problem.incompatibleTask)
        assertEquals("https://docs/problem", problem.documentationLink)
        assertEquals(JsBuildLogic("build file 'build.gradle'"), problem.trace.single())
        assertEquals(
            listOf(JsStackTracePart(text = "user frame"), JsStackTracePart(internalText = "internal frame")),
            problem.error?.parts
        )

        val input = diagnostics[1]
        assertEquals(listOf(JsMessageFragment(text = "system property "), JsMessageFragment(name = "someMessage")), input.input)
        assertEquals(
            listOf(
                JsTraceSystemProperty("someMessage"),
                JsTraceProject(":sub-b"),
                JsTracePropertyUsage("foo", ":sub-b")
            ),
            input.trace
        )

        val incompatible = diagnostics[2]
        assertEquals(JsTraceTaskPath(":incompatible"), incompatible.trace.single())
        assertTrue(incompatible.incompatibleTask != null)

        val emptyError = diagnostics[3]
        // An empty `{}` error object must decode to an error with neither summary nor parts.
        assertEquals(JsError(summary = null, parts = null), emptyError.error)
    }

    @Test
    fun `decodes every trace kind`() {
        val diagnostic = json.decodeFromString(
            JsDiagnostic.serializer(),
            """
            {
                "problem": [{"text": "p"}],
                "trace": [
                    {"kind": "Project", "path": ":app"},
                    {"kind": "Task", "path": ":app:compileJava", "type": "JavaCompile"},
                    {"kind": "TaskPath", "path": ":app:jar"},
                    {"kind": "Bean", "type": "com.example.Bean"},
                    {"kind": "CapturedArguments", "class": "com.example.C", "method": "m", "subkind": "lambdaBody"},
                    {"kind": "SerializedLambda", "type": "org.gradle.api.Action", "returns": "void"},
                    {"kind": "Field", "name": "f", "declaringType": "com.example.D"},
                    {"kind": "InputProperty", "name": "sourceDir", "task": ":app:compileJava"},
                    {"kind": "OutputProperty", "name": "outputDir", "task": ":app:compileJava"},
                    {"kind": "VirtualProperty", "name": "upToDate", "owner": ":app:jar"},
                    {"kind": "PropertyUsage", "name": "prop", "from": ":app"},
                    {"kind": "SystemProperty", "name": "user.home"},
                    {"kind": "BuildLogic", "location": "build.gradle.kts:42"},
                    {"kind": "BuildLogicClass", "type": "com.example.MyPlugin"},
                    {"kind": "Gradle"},
                    {"kind": "Unknown"}
                ]
            }
            """.trimIndent()
        )

        assertEquals(
            listOf(
                JsTraceProject(":app"),
                JsTraceTask(":app:compileJava", "JavaCompile"),
                JsTraceTaskPath(":app:jar"),
                JsTraceBean("com.example.Bean"),
                JsTraceCapturedArguments("com.example.C", "m", "lambdaBody"),
                JsTraceSerializedLambda("org.gradle.api.Action", "void"),
                JsTraceField("f", "com.example.D"),
                JsTraceInputProperty("sourceDir", ":app:compileJava"),
                JsTraceOutputProperty("outputDir", ":app:compileJava"),
                JsTraceVirtualProperty("upToDate", ":app:jar"),
                JsTracePropertyUsage("prop", ":app"),
                JsTraceSystemProperty("user.home"),
                JsBuildLogic("build.gradle.kts:42"),
                JsBuildLogicClass("com.example.MyPlugin"),
                JsTraceGradle,
                JsTraceUnknown
            ),
            diagnostic.trace
        )
    }

    @Test
    fun `decodes a problems report`() {
        val summary = json.decodeFromString(
            JsProblemsSummary.serializer(),
            """
            {
                "totalProblemCount": 41,
                "buildName": "problems-playground",
                "requestedTasks": "help",
                "documentationLink": "https://docs/problems",
                "documentationLinkCaption": "Problem report",
                "summaries": [
                    {"problemId": [{"name": "deprecation", "displayName": "Deprecation"}], "count": 3}
                ]
            }
            """.trimIndent()
        )

        val problems = json.decodeFromString(
            ListSerializer(JsProblem.serializer()),
            """
            [
                    {
                        "locations": [
                            {"path": "src/main/java/MyClass.java", "line": 42, "column": 8},
                            {"pluginId": "java"},
                            {"taskPath": ":compileJava"}
                        ],
                        "severity": "WARNING",
                        "contextualLabel": "Variable 'x' is never used",
                        "problemId": [
                            {"name": "compilation", "displayName": "Compilation"},
                            {"name": "unused-variable", "displayName": "Unused variable"}
                        ],
                        "solutions": ["Remove the unused variable"]
                    },
                    {
                        "severity": "ERROR",
                        "problemId": [{"name": "compiler-err", "displayName": "Java compilation error"}]
                    }
            ]
            """.trimIndent()
        )

        // Unknown fields are ignored, not errors.
        assertEquals("problems-playground", summary.buildName)
        assertEquals("help", summary.requestedTasks)
        assertEquals("https://docs/problems", summary.documentationLink)
        assertEquals(listOf(JsProblemIdSummary(listOf(JsProblemIdElement("deprecation", "Deprecation")), 3)), summary.summaries)

        assertEquals(2, problems.size)

        val warning = problems[0]
        assertEquals("WARNING", warning.severity)
        assertEquals("Variable 'x' is never used", warning.contextualLabel)
        assertEquals(listOf("Remove the unused variable"), warning.solutions)
        assertEquals(
            listOf(
                JsLocation(path = "src/main/java/MyClass.java", line = 42, column = 8),
                JsLocation(pluginId = "java"),
                JsLocation(taskPath = ":compileJava")
            ),
            warning.locations
        )

        val error = problems[1]
        assertEquals("ERROR", error.severity)
        // Absent optional collections must stay null so the renderer can distinguish "none captured".
        assertNull(error.locations)
        assertNull(error.solutions)
        assertNull(error.contextualLabel)
    }

    @Test
    fun `summaries serialize as the producer emits them`() {
        // The producer serializes a summary on its own and writes it into the report under its
        // elementId, which is also what tells the two kinds of report apart. Json is the
        // default-configured one the producers use, see CommonReport.
        val producerJson = Json

        val ccSummary = JsConfigurationCacheSummary(
            cacheAction = "storing",
            documentationLink = "https://docs/cc",
            totalProblemCount = 0,
            uniqueProblemCount = 0,
            overflownProblemCount = 0
        )
        assertEquals("configuration-cache-summary", ccSummary.elementId)
        assertEquals(
            """{"cacheAction":"storing","documentationLink":"https://docs/cc","totalProblemCount":0,"uniqueProblemCount":0,"overflownProblemCount":0}""",
            ccSummary.toJson(producerJson)
        )

        val problemsSummary = JsProblemsSummary(documentationLink = "https://docs/problems")
        assertEquals("problems-summary", problemsSummary.elementId)
        assertEquals(
            """{"documentationLink":"https://docs/problems"}""",
            problemsSummary.toJson(producerJson)
        )
    }

    @Test
    fun `trace round-trips through the kind discriminator`() {
        val original: JsTrace = JsTraceTask(":app:compileJava", "JavaCompile")
        val encoded = json.encodeToString(JsTrace.serializer(), original)
        assertTrue(encoded.contains("\"kind\":\"Task\""), "expected a kind discriminator in $encoded")
        assertEquals(original, json.decodeFromString(JsTrace.serializer(), encoded))
    }

    @Test
    fun `assertIs sanity on polymorphic decode`() {
        val trace = json.decodeFromString(JsTrace.serializer(), """{"kind": "Bean", "type": "com.example.Bean"}""")
        assertIs<JsTraceBean>(trace)
        assertEquals("com.example.Bean", trace.type)
    }
}
