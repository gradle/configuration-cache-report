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

import java.io.File


/**
 * Reads the pieces of report data out of an html report file, for tests that want to assert on the
 * contents of a report a build produced.
 *
 * This is the read counterpart of `HtmlReportWriter`: it knows where in the file the pieces are, so
 * that callers do not have to. Each piece is returned as the JSON text to parse; parsing it is left
 * to the caller, which usually has a JSON library of its own already.
 */
class HtmlReportDataReader(private val reportFile: File) {

    /**
     * Returns the JSON text of the reported items, an array.
     */
    fun readDiagnosticsJson(): String = readRegion("diagnostics")

    /**
     * Returns the JSON text of the report model, an object.
     */
    fun readModelJson(): String = readRegion("model")

    private
    fun readRegion(name: String): String {
        val region = linesBetween("// begin-report-$name", "// end-report-$name")
        require(region.isNotEmpty()) {
            "malformed report file: $name region not found in $reportFile"
        }
        return region
    }

    private
    fun linesBetween(beginLine: String, endLine: String): String =
        reportFile.bufferedReader().use { reader ->
            reader.lineSequence()
                .dropWhile { it != beginLine }
                .drop(1)
                .takeWhile { it != endLine }
                .joinToString("\n")
        }
}
