/*
 * Copyright 2024 the original author or authors.
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

import kotlinx.serialization.json.Json
import org.gradle.problems.internal.report.model.DIAGNOSTICS_ELEMENT_ID
import org.gradle.problems.internal.report.model.JsReportDiagnostic
import org.gradle.problems.internal.report.model.JsReportSummary
import java.io.Writer


/**
 * Writes the configuration cache / problems html report.
 *
 * The report data is carried by `<script type="application/json">` elements, one per piece, which
 * the page finds by id and hands straight to a JSON parser. Nothing has to build a JavaScript object
 * of the whole report, which matters for reports that can reach several megabytes:
 * - the diagnostics, under `id="diagnostics"`, streamed into the element as they arrive
 * - the summary, under the id that says which kind of report this is (see [JsReportSummary]),
 *   written last, once its totals are known.
 *
 * The same elements are what the test fixtures read the report back from, so each piece is the plain
 * JSON text of that piece, with no wrapping to undo.
 */
class HtmlReportWriter internal constructor(
    private val writer: Writer,
    private val htmlTemplate: HtmlReportTemplate
) {
    constructor(writer: Writer) : this(writer, HtmlReportTemplateLoader().load())

    private
    val json = Json

    private
    var firstDiagnostic = true

    fun beginHtmlReport() {
        writer.append(htmlTemplate.header)
        writer.run {
            appendLine(ReportDataElement.openingTag(DIAGNOSTICS_ELEMENT_ID))
            appendLine("[")
        }
    }

    /**
     * Appends one diagnostic to the streamed `diagnostics` array, on a line of its own.
     */
    fun writeDiagnostic(diagnostic: JsReportDiagnostic) {
        writer.run {
            if (!firstDiagnostic) {
                appendLine(",")
            }
            firstDiagnostic = false
            appendEscaped(diagnostic.toJson(json))
        }
    }

    /**
     * Closes the report by writing its summary.
     *
     * @param summary the summary of the report
     */
    fun endHtmlReport(summary: JsReportSummary) {
        writer.run {
            if (!firstDiagnostic) {
                appendLine()
            }
            appendLine("]")
            appendLine(ReportDataElement.CLOSING_TAG)
            appendLine(ReportDataElement.openingTag(summary.elementId))
            appendEscaped(summary.toJson(json))
            appendLine()
            appendLine(ReportDataElement.CLOSING_TAG)
        }
        writer.append(htmlTemplate.footer)
    }

    fun close() {
        writer.close()
    }
}


/**
 * Appends [json] as the text content of a `<script>` element.
 *
 * The html parser reads that content in its own tokenizer state, where a `<` can end the element
 * early or change how the rest of it is read, so no `<` may reach the page as itself. Escaping it as
 * `\u003c` leaves the content valid JSON, which is all the page ever parses it as.
 */
private
fun Writer.appendEscaped(json: String) {
    for (character in json) {
        if (character == '<') {
            append("\\u003c")
        } else {
            append(character)
        }
    }
}
