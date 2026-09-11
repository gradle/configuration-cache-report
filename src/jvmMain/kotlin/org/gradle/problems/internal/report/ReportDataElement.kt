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


/**
 * The `<script>` element that carries one piece of report data in an html report file.
 *
 * [HtmlReportWriter] writes the tags on lines of their own, and readers of a written report find a
 * piece by looking for those lines, so both ends agree through this one definition rather than
 * through separately spelled-out markup.
 */
object ReportDataElement {

    const val CLOSING_TAG = "</script>"

    fun openingTag(elementId: String) = """<script type="application/json" id="$elementId">"""
}
