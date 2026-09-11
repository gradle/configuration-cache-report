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

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


/**
 * Pins the JSON the model classes encode to, which is the layout of a report file on disk.
 *
 * The page that renders a report decodes the same classes with the same library, so a change to the
 * model can never break it. It does change what everything else reading a report sees: the fixtures
 * the Gradle integration tests parse reports with, and whatever tooling users have pointed at the
 * report file. A failure here need not be wrong, but it is a format change to make on purpose and
 * to call out.
 *
 * The round trips cover what sharing the classes does not guarantee by itself: that the page decodes
 * with a configuration that accepts what the producer writes, and that a field the producer leaves
 * out reads back as absent rather than as empty.
 */
class JsModelWireFormatTest {

    /** The producers encode with the default-configured Json, see `HtmlReportWriter`. */
    private
    val producerJson = Json

    private
    val ccSummary = JsConfigurationCacheSummary(
        buildName = "sampleProject",
        cacheAction = "storing",
        requestedTasks = "clean build",
        cacheActionDescription = listOf(JsMessageFragment(text = "Calculating "), JsMessageFragment(name = "build.gradle")),
        documentationLink = "https://docs/cc",
        totalProblemCount = 20,
        uniqueProblemCount = 19,
        overflownProblemCount = 1
    )

    private
    val ccDiagnostics = listOf(
        JsDiagnostic(
            problem = listOf(JsMessageFragment(text = "invocation of "), JsMessageFragment(name = "Task.project")),
            trace = listOf(JsBuildLogic("build file 'build.gradle'")),
            documentationLink = "https://docs/problem",
            error = JsError(
                summary = listOf(JsMessageFragment(text = "at "), JsMessageFragment(name = "build.gradle:5")),
                parts = listOf(JsStackTracePart(text = "user frame"), JsStackTracePart(internalText = "internal frame"))
            )
        ),
        JsDiagnostic(
            input = listOf(JsMessageFragment(text = "system property "), JsMessageFragment(name = "someMessage")),
            trace = listOf(JsTraceSystemProperty("someMessage"), JsTraceProject(":sub-b"))
        ),
        JsDiagnostic(
            incompatibleTask = listOf(JsMessageFragment(text = "task "), JsMessageFragment(name = ":incompatible")),
            trace = listOf(JsTraceTaskPath(":incompatible"))
        ),
        JsDiagnostic(
            problem = listOf(JsMessageFragment(text = "no error details")),
            error = JsError()
        )
    )

    /** Every trace element, and the JSON object each is written as. */
    private
    val traceKinds = mapOf(
        JsTraceProject(":app") to """{"kind":"Project","path":":app"}""",
        JsTraceTask(":app:compileJava", "JavaCompile") to """{"kind":"Task","path":":app:compileJava","type":"JavaCompile"}""",
        JsTraceTaskPath(":app:jar") to """{"kind":"TaskPath","path":":app:jar"}""",
        JsTraceBean("com.example.Bean") to """{"kind":"Bean","type":"com.example.Bean"}""",
        JsTraceCapturedArguments("com.example.C", "m", "lambdaBody") to """{"kind":"CapturedArguments","class":"com.example.C","method":"m","subkind":"lambdaBody"}""",
        JsTraceSerializedLambda("org.gradle.api.Action", "void") to """{"kind":"SerializedLambda","type":"org.gradle.api.Action","returns":"void"}""",
        JsTraceField("f", "com.example.D") to """{"kind":"Field","name":"f","declaringType":"com.example.D"}""",
        JsTraceInputProperty("sourceDir", ":app:compileJava") to """{"kind":"InputProperty","name":"sourceDir","task":":app:compileJava"}""",
        JsTraceOutputProperty("outputDir", ":app:compileJava") to """{"kind":"OutputProperty","name":"outputDir","task":":app:compileJava"}""",
        JsTraceVirtualProperty("upToDate", ":app:jar") to """{"kind":"VirtualProperty","name":"upToDate","owner":":app:jar"}""",
        JsTracePropertyUsage("prop", ":app") to """{"kind":"PropertyUsage","name":"prop","from":":app"}""",
        JsTraceSystemProperty("user.home") to """{"kind":"SystemProperty","name":"user.home"}""",
        JsBuildLogic("build.gradle.kts:42") to """{"kind":"BuildLogic","location":"build.gradle.kts:42"}""",
        JsBuildLogicClass("com.example.MyPlugin") to """{"kind":"BuildLogicClass","type":"com.example.MyPlugin"}""",
        JsTraceGradle to """{"kind":"Gradle"}""",
        JsTraceUnknown to """{"kind":"Unknown"}"""
    )

    private
    val problemsSummary = JsProblemsSummary(
        buildName = "problems-playground",
        requestedTasks = "help",
        documentationLink = "https://docs/problems",
        summaries = listOf(JsProblemIdSummary(listOf(JsProblemIdElement("deprecation", "Deprecation")), 3))
    )

    private
    val problems = listOf(
        JsProblem(
            problemId = listOf(JsProblemIdElement("compilation", "Compilation"), JsProblemIdElement("unused-variable", "Unused variable")),
            documentationLink = "https://docs/unused",
            severity = "WARNING",
            error = JsError(parts = listOf(JsStackTracePart(text = "user frame"))),
            problemDetails = "The variable is assigned but never read",
            contextualLabel = "Variable 'x' is never used",
            solutions = listOf("Remove the unused variable"),
            locations = listOf(
                JsLocation(path = "src/main/java/MyClass.java", line = 42, column = 8, length = 1),
                JsLocation(pluginId = "java"),
                JsLocation(taskPath = ":compileJava")
            )
        ),
        JsProblem(
            problemId = listOf(JsProblemIdElement("compiler-err", "Java compilation error")),
            severity = "ERROR"
        )
    )

    @Test
    fun `report pieces are written under their element ids`() {
        assertEquals("diagnostics", DIAGNOSTICS_ELEMENT_ID)
        assertEquals("configuration-cache-summary", ccSummary.elementId)
        assertEquals("problems-summary", problemsSummary.elementId)
    }

    @Test
    fun `configuration cache summary is written as one object`() {
        assertEquals(
            """{"buildName":"sampleProject","cacheAction":"storing","requestedTasks":"clean build","cacheActionDescription":[{"text":"Calculating "},{"name":"build.gradle"}],"documentationLink":"https://docs/cc","totalProblemCount":20,"uniqueProblemCount":19,"overflownProblemCount":1}""",
            ccSummary.toJson(producerJson)
        )
        // Fields the producer has nothing to say about are left out, not written as null.
        assertEquals(
            """{"cacheAction":"loading","documentationLink":"https://docs/cc","totalProblemCount":0,"uniqueProblemCount":0,"overflownProblemCount":0}""",
            JsConfigurationCacheSummary(
                cacheAction = "loading",
                documentationLink = "https://docs/cc",
                totalProblemCount = 0,
                uniqueProblemCount = 0,
                overflownProblemCount = 0
            ).toJson(producerJson)
        )
    }

    @Test
    fun `configuration cache diagnostics are written with their trace and error`() {
        assertEquals(
            listOf(
                """{"problem":[{"text":"invocation of "},{"name":"Task.project"}],"trace":[{"kind":"BuildLogic","location":"build file 'build.gradle'"}],"documentationLink":"https://docs/problem","error":{"summary":[{"text":"at "},{"name":"build.gradle:5"}],"parts":[{"text":"user frame"},{"internalText":"internal frame"}]}}""",
                """{"input":[{"text":"system property "},{"name":"someMessage"}],"trace":[{"kind":"SystemProperty","name":"someMessage"},{"kind":"Project","path":":sub-b"}]}""",
                """{"incompatibleTask":[{"text":"task "},{"name":":incompatible"}],"trace":[{"kind":"TaskPath","path":":incompatible"}]}""",
                """{"problem":[{"text":"no error details"}],"error":{}}"""
            ),
            ccDiagnostics.map { it.toJson(producerJson) }
        )
    }

    @Test
    fun `every trace element is written under its kind`() {
        for ((trace, expected) in traceKinds) {
            assertEquals(expected, producerJson.encodeToString(JsTrace.serializer(), trace))
        }
    }

    @Test
    fun `problems summary is written as one object`() {
        assertEquals(
            """{"buildName":"problems-playground","requestedTasks":"help","documentationLink":"https://docs/problems","summaries":[{"problemId":[{"name":"deprecation","displayName":"Deprecation"}],"count":3}]}""",
            problemsSummary.toJson(producerJson)
        )
        assertEquals(
            """{"documentationLink":"https://docs/problems"}""",
            JsProblemsSummary(documentationLink = "https://docs/problems").toJson(producerJson)
        )
    }

    @Test
    fun `problems are written with their id, locations and solutions`() {
        assertEquals(
            listOf(
                """{"problemId":[{"name":"compilation","displayName":"Compilation"},{"name":"unused-variable","displayName":"Unused variable"}],"documentationLink":"https://docs/unused","severity":"WARNING","error":{"parts":[{"text":"user frame"}]},"problemDetails":"The variable is assigned but never read","contextualLabel":"Variable 'x' is never used","solutions":["Remove the unused variable"],"locations":[{"path":"src/main/java/MyClass.java","line":42,"column":8,"length":1},{"pluginId":"java"},{"taskPath":":compileJava"}]}""",
                """{"problemId":[{"name":"compiler-err","displayName":"Java compilation error"}],"severity":"ERROR"}"""
            ),
            problems.map { it.toJson(producerJson) }
        )
    }

    @Test
    fun `page decodes what the producer writes`() {
        assertEquals(ccSummary, parseCcSummary(ccSummary.toJson(producerJson)))
        assertEquals(ccDiagnostics, parseCcDiagnostics(ccDiagnostics.asDiagnosticsArray()))
        assertEquals(problemsSummary, parseProblemsSummary(problemsSummary.toJson(producerJson)))
        assertEquals(problems, parseProblems(problems.asDiagnosticsArray()))

        val everyTrace = JsDiagnostic(problem = listOf(JsMessageFragment(text = "p")), trace = traceKinds.keys.toList())
        assertEquals(listOf(everyTrace), parseCcDiagnostics(listOf(everyTrace).asDiagnosticsArray()))
    }

    @Test
    fun `fields the producer left out read back as absent`() {
        // The page tells "no locations were captured" from "there were none" by the field being null,
        // so the optional collections must not decode to empty ones.
        val problem = parseProblems(listOf(problems[1]).asDiagnosticsArray()).single()
        assertNull(problem.locations)
        assertNull(problem.solutions)
        assertNull(problem.contextualLabel)
        assertNull(problem.problemDetails)
        assertNull(problem.documentationLink)
        assertNull(problem.error)

        // An error written as `{}` is an error without details, not a missing one.
        val diagnostic = parseCcDiagnostics(listOf(ccDiagnostics[3]).asDiagnosticsArray()).single()
        assertEquals(JsError(summary = null, parts = null), diagnostic.error)
        assertEquals(emptyList(), diagnostic.trace)
    }

    @Test
    fun `page ignores fields it does not know`() {
        assertEquals(
            JsProblemsSummary(documentationLink = "https://docs/problems"),
            parseProblemsSummary("""{"documentationLink":"https://docs/problems","documentationLinkCaption":"Problem report"}""")
        )
        assertEquals(
            listOf(JsDiagnostic(problem = listOf(JsMessageFragment(text = "p")), trace = listOf(JsTraceProject(":app")))),
            parseCcDiagnostics("""[{"problem":[{"text":"p"}],"trace":[{"kind":"Project","path":":app","displayName":"app"}],"since":"9.0"}]""")
        )
    }

    /** Lays the items out the way `HtmlReportWriter` does: a JSON array with one item per line. */
    private
    fun List<JsReportDiagnostic>.asDiagnosticsArray(): String =
        joinToString(",\n", "[\n", "\n]") { it.toJson(producerJson) }
}
