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

package org.gradle.problems.internal.report

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.gradle.problems.internal.report.model.JsDiagnostic
import org.gradle.problems.internal.report.model.JsMessageFragment
import org.gradle.problems.internal.report.model.JsModel
import org.gradle.problems.internal.report.model.JsTraceProject
import java.io.StringWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 * Verifies that the writer emits the report data in the layout the consumers rely on: the JS module
 * bundled with this library reads it back by calling `configurationCacheProblems()`, and the Gradle
 * integration test fixture parses the marked regions as plain JSON.
 *
 * The template these tests load is the real assembled report resource that ships in the jar, so a
 * template edit that drops the data placeholder fails here rather than at Gradle runtime.
 */
class HtmlReportWriterTest {

    private
    val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `splits the bundled report template at the data placeholder`() {
        val template = HtmlReportTemplateLoader().load()

        assertTrue(template.header.startsWith("<!DOCTYPE html>"), "unexpected header start: ${template.header.take(40)}")
        assertTrue(template.header.contains("""<div class="report" id="report">"""), "report element missing from the header")
        assertTrue(template.footer.trimEnd().endsWith("</html>"), "unexpected footer end: ${template.footer.takeLast(40)}")
        // The renderer bundle is inlined into the template by `assembleReport`, and reads the model
        // by calling the function the writer emits just before it.
        assertTrue(
            template.footer.contains("configurationCacheProblems"),
            "the renderer must follow the report data"
        )
    }

    @Test
    fun `emits the model and the diagnostics as JSON in their marked regions`() {
        val diagnostics = listOf(
            JsDiagnostic(
                problem = listOf(JsMessageFragment(text = "invocation of "), JsMessageFragment(name = "Task.project")),
                trace = listOf(JsTraceProject(":app"))
            ),
            JsDiagnostic(input = listOf(JsMessageFragment(text = "system property "), JsMessageFragment(name = "user.home")))
        )
        val envelope = JsModel(
            cacheAction = "storing",
            documentationLink = "https://docs/cc",
            totalProblemCount = 2,
            uniqueProblemCount = 2,
            overflownProblemCount = 0
        )

        val report = writeReport(diagnostics, envelope)

        assertEquals(envelope, json.decodeFromString(JsModel.serializer(), report.regionBetween("model")))
        assertEquals(
            diagnostics,
            json.decodeFromString(ListSerializer(JsDiagnostic.serializer()), report.regionBetween("diagnostics"))
        )
    }

    @Test
    fun `emits an empty diagnostics array when there is nothing to report`() {
        val envelope = JsModel(
            cacheAction = "storing",
            documentationLink = "https://docs/cc",
            totalProblemCount = 0,
            uniqueProblemCount = 0,
            overflownProblemCount = 0
        )

        val report = writeReport(emptyList(), envelope)

        assertEquals(
            emptyList(),
            json.decodeFromString(ListSerializer(JsDiagnostic.serializer()), report.regionBetween("diagnostics"))
        )
    }

    private
    fun writeReport(diagnostics: List<JsDiagnostic>, envelope: JsModel): String =
        StringWriter().also { out ->
            HtmlReportWriter(out).run {
                beginHtmlReport()
                diagnostics.forEach { writeDiagnostic(it) }
                endHtmlReport(envelope)
                close()
            }
        }.toString()

    /**
     * Extracts the text between the `// begin-report-$name`/`// end-report-$name` markers, which the
     * writer's contract says is valid JSON on its own.
     */
    private
    fun String.regionBetween(name: String): String {
        val begin = indexOf("// begin-report-$name")
        val end = indexOf("// end-report-$name")
        assertTrue(begin >= 0 && end > begin, "no `$name` region in the emitted report")
        return substring(begin + "// begin-report-$name".length, end)
    }
}
