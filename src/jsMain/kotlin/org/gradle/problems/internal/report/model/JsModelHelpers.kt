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

import data.PrettyText
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json


/**
 * Decodes the report models from the report JSON text.
 *
 * The report data is produced by `ConfigurationCacheReport` (see `configuration-cache-report-data.js`),
 * which carries each piece of the report in a `<script type="application/json">` element of its own.
 * We parse the text of those elements with
 * [Json.decodeFromString], so decoding is driven entirely by the `@Serializable` model definitions
 * instead of hand-written field extraction. Each piece is decoded on its own, which is why the
 * producer can stream the diagnostics without knowing the summary yet.
 */
private
val reportJson = Json {
    // Fields a producer adds ahead of the renderer that consumes them must not break the report.
    ignoreUnknownKeys = true
}


/** Decodes the summary of a configuration cache report. */
fun parseCcSummary(jsonText: String): JsConfigurationCacheSummary =
    reportJson.decodeFromString(JsConfigurationCacheSummary.serializer(), jsonText)


/** Decodes the summary of a problems report. */
fun parseProblemsSummary(jsonText: String): JsProblemsSummary =
    reportJson.decodeFromString(JsProblemsSummary.serializer(), jsonText)


/** Decodes the streamed diagnostics of a configuration cache report. */
fun parseCcDiagnostics(jsonText: String): List<JsDiagnostic> =
    reportJson.decodeFromString(ListSerializer(JsDiagnostic.serializer()), jsonText)


/** Decodes the streamed problems of a problems report. */
fun parseProblems(jsonText: String): List<JsProblem> =
    reportJson.decodeFromString(ListSerializer(JsProblem.serializer()), jsonText)


fun toPrettyText(message: List<JsMessageFragment>): PrettyText =
    PrettyText.build {
        message.forEach { fragment ->
            fragment.text?.let { text(it) }
            fragment.name?.let { ref(it) }
        }
    }
