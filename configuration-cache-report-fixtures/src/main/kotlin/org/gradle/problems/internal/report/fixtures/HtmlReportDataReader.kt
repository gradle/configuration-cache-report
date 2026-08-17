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

package org.gradle.problems.internal.report.fixtures

import org.gradle.problems.internal.report.model.DIAGNOSTICS_ELEMENT_ID
import org.gradle.problems.internal.report.model.JsConfigurationCacheSummary
import org.gradle.problems.internal.report.model.JsProblemsSummary
import java.io.File


/**
 * Reads the pieces of report data out of an html report file, for tests that want to assert on the
 * contents of a report a build produced.
 *
 * This is the read counterpart of `HtmlReportWriter`: it knows which element carries which piece, so
 * that callers do not have to. Each piece is returned as the JSON text to parse; parsing it is left
 * to the caller, which usually has a JSON library of its own already.
 */
class HtmlReportDataReader(private val reportFile: File) {

    /**
     * Returns the JSON text of the reported items, an array.
     */
    fun readDiagnosticsJson(): String = readElement(DIAGNOSTICS_ELEMENT_ID)

    /**
     * Returns the JSON text of the report summary, an object.
     *
     * A report carries exactly one summary, so this returns whichever of the two kinds it has.
     */
    fun readSummaryJson(): String =
        findElement(JsConfigurationCacheSummary.ELEMENT_ID)
            ?: findElement(JsProblemsSummary.ELEMENT_ID)
            ?: throw IllegalArgumentException("malformed report file: no summary element in $reportFile")

    private
    fun readElement(elementId: String): String =
        requireNotNull(findElement(elementId)) {
            "malformed report file: no <script id=\"$elementId\"> element in $reportFile"
        }

    /**
     * Returns the text content of the `<script>` element with the given id, or null when the report
     * does not have one. The producer writes the opening tag, the content and the closing tag on
     * lines of their own, so the element is found by scanning for those lines.
     */
    private
    fun findElement(elementId: String): String? =
        reportFile.bufferedReader().use { reader ->
            val lines = reader.lineSequence()
                .dropWhile { !it.endsWith("""id="$elementId">""") }
                .drop(1)
                .takeWhile { it != "</script>" }
                .toList()
            lines.takeIf { it.isNotEmpty() }?.joinToString("\n")
        }
}
