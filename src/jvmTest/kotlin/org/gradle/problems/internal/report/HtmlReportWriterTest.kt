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
import org.gradle.problems.internal.report.model.DIAGNOSTICS_ELEMENT_ID
import org.gradle.problems.internal.report.model.JsConfigurationCacheSummary
import org.gradle.problems.internal.report.model.JsDiagnostic
import org.gradle.problems.internal.report.model.JsMessageFragment
import org.gradle.problems.internal.report.model.JsReportSummary
import org.gradle.problems.internal.report.model.JsTraceProject
import java.io.StringWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


/**
 * Verifies that the writer emits the report data in the layout the consumers rely on: the page
 * bundled with this library looks the pieces up by element id, and so does the fixtures library the
 * Gradle integration tests read reports with.
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
        // The renderer bundle is inlined into the template by `assembleReport`, and finds the report
        // data in the elements the writer emits just before it.
        assertTrue(
            template.footer.contains(JsConfigurationCacheSummary.ELEMENT_ID),
            "the renderer must follow the report data and look it up by id"
        )
    }

    @Test
    fun `emits the summary and the diagnostics as JSON in their own elements`() {
        val diagnostics = listOf(
            JsDiagnostic(
                problem = listOf(JsMessageFragment(text = "invocation of "), JsMessageFragment(name = "Task.project")),
                trace = listOf(JsTraceProject(":app"))
            ),
            JsDiagnostic(input = listOf(JsMessageFragment(text = "system property "), JsMessageFragment(name = "user.home")))
        )
        val summary = configurationCacheSummary(totalProblemCount = 2)

        val report = writeReport(diagnostics, summary)

        assertEquals(
            summary,
            json.decodeFromString(JsConfigurationCacheSummary.serializer(), report.jsonIn(summary.elementId))
        )
        assertEquals(
            diagnostics,
            json.decodeFromString(ListSerializer(JsDiagnostic.serializer()), report.jsonIn(DIAGNOSTICS_ELEMENT_ID))
        )
    }

    @Test
    fun `writes each diagnostic on a line of its own`() {
        val diagnostics = (1..3).map { JsDiagnostic(problem = listOf(JsMessageFragment(text = "problem $it"))) }

        val report = writeReport(diagnostics, configurationCacheSummary(totalProblemCount = 3))

        // The array is read by a JSON parser either way, but one diagnostic per line keeps a report
        // that can reach several megabytes greppable and diffable.
        assertEquals(
            3,
            report.jsonIn(DIAGNOSTICS_ELEMENT_ID).lines().count { it.startsWith("{") }
        )
    }

    @Test
    fun `emits an empty diagnostics array when there is nothing to report`() {
        val report = writeReport(emptyList(), configurationCacheSummary(totalProblemCount = 0))

        assertEquals(
            emptyList(),
            json.decodeFromString(ListSerializer(JsDiagnostic.serializer()), report.jsonIn(DIAGNOSTICS_ELEMENT_ID))
        )
    }

    @Test
    fun `escapes report data that would otherwise end the enclosing element`() {
        val diagnostics = listOf(
            JsDiagnostic(problem = listOf(JsMessageFragment(text = """</script><script>alert("x")</script>""")))
        )

        val report = writeReport(diagnostics, configurationCacheSummary(totalProblemCount = 1))

        // The html parser reads the content of a script element in its own tokenizer state, where a
        // `<` can end the element early or change how the rest of it is read.
        assertFalse(
            report.jsonIn(DIAGNOSTICS_ELEMENT_ID).contains("<"),
            "no `<` may reach the page as itself"
        )
        assertEquals(
            diagnostics,
            json.decodeFromString(ListSerializer(JsDiagnostic.serializer()), report.jsonIn(DIAGNOSTICS_ELEMENT_ID))
        )
    }

    private
    fun configurationCacheSummary(totalProblemCount: Int) = JsConfigurationCacheSummary(
        cacheAction = "storing",
        documentationLink = "https://docs/cc",
        totalProblemCount = totalProblemCount,
        uniqueProblemCount = totalProblemCount,
        overflownProblemCount = 0
    )

    private
    fun writeReport(diagnostics: List<JsDiagnostic>, summary: JsReportSummary): String =
        StringWriter().also { out ->
            HtmlReportWriter(out).run {
                beginHtmlReport()
                diagnostics.forEach { writeDiagnostic(it) }
                endHtmlReport(summary)
                close()
            }
        }.toString()

    /**
     * Returns the text of the `<script>` element with the given id, which the writer's contract says
     * is the JSON of that piece of the report.
     */
    private
    fun String.jsonIn(elementId: String): String {
        val lines = lines()
            .dropWhile { !it.endsWith("""id="$elementId">""") }
            .drop(1)
            .takeWhile { it != "</script>" }
        assertTrue(lines.isNotEmpty(), "no `$elementId` element in the emitted report")
        return lines.joinToString("\n")
    }
}
