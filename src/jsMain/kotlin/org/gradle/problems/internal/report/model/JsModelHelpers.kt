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
import kotlinx.serialization.json.Json


/**
 * Decodes the report models from the report JSON text.
 *
 * The report data is produced by `ConfigurationCacheReport` (see `configuration-cache-report-data.js`)
 * and reaches the page as a JavaScript object; the caller stringifies it and we parse the resulting
 * text with [Json.decodeFromString], so decoding is driven entirely by the `@Serializable` model
 * definitions instead of hand-written field extraction.
 */
private
val reportJson = Json {
    // A problems report is decoded from the same top-level object as a configuration cache report
    // (see JsProblemsModel), so the configuration-cache-specific fields are unknown to it and must
    // be ignored rather than treated as errors.
    ignoreUnknownKeys = true
}


/** Decodes a configuration cache report [JsModel] from the report JSON text. */
fun parseCcReportJsModel(jsonText: String): JsModel =
    reportJson.decodeFromString(JsModel.serializer(), jsonText)


/** Decodes a [JsProblemsModel] from the report JSON text, used when a problems report is present. */
fun parseProblemsJsModel(jsonText: String): JsProblemsModel =
    reportJson.decodeFromString(JsProblemsModel.serializer(), jsonText)


fun toPrettyText(message: List<JsMessageFragment>): PrettyText =
    PrettyText.build {
        message.forEach { fragment ->
            fragment.text?.let { text(it) }
            fragment.name?.let { ref(it) }
        }
    }
