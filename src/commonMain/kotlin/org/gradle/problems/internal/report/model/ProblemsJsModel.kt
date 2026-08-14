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

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


/**
 * The model of a problems report.
 *
 * A build produces this model to report the problems collected through the Problems API, and the
 * report renderer turns it into the HTML page shown to the user. The reported problems themselves
 * are carried separately; this model holds the surrounding context and the summaries of problems
 * that were aggregated rather than listed individually.
 */
@Serializable
data class ProblemReportJsModel(
    /** The name of the build the report was produced for, or null for the root build. */
    val buildName: String? = null,
    /** The tasks requested for the build as a display string, or null when the build ran its default tasks. */
    val requestedTasks: String? = null,
    /** A link to the documentation about reporting problems. */
    val documentationLink: String,
    /** Counts of problems that were aggregated by their id instead of being reported individually. */
    val summaries: List<JsProblemSummary> = emptyList()
)


/**
 * The root of a report rendered as a problems report rather than a configuration cache report.
 *
 * When the top-level report object carries a [problemsReport] field it is decoded into this model.
 * The [problemsReport] holds the surrounding context (see [ProblemReportJsModel]), while the
 * reported problems are carried in the same top-level `diagnostics` array that a configuration cache
 * report uses — hence [diagnostics] here holds [JsProblem]s rather than [JsDiagnostic]s. Producers
 * write the problems in a predefined order directly to the report file, which is why the two report
 * kinds share the one array. The configuration-cache-specific top-level fields are ignored in this
 * mode.
 */
@Serializable
data class JsProblemsModel(
    /** The surrounding context of the problems report. */
    val problemsReport: ProblemReportJsModel,
    /** The reported problems, sharing the top-level `diagnostics` array with configuration cache reports. */
    val diagnostics: List<JsProblem> = emptyList()
) : JsReportSummary {
    override fun toJson(json: Json): String = json.encodeToString(this)
}


/**
 * A single problem reported through the Problems API.
 */
@Serializable
data class JsProblem(
    /**
     * The hierarchical identifier of the problem, from the outermost group down to the specific
     * problem as its last element. See [JsProblemIdElement].
     */
    val problemId: List<JsProblemIdElement>,
    /** A link to documentation relevant to this problem, if any. */
    val documentationLink: String? = null,
    /** The problem's severity, such as `ERROR`, `WARNING` or `ADVICE`. */
    val severity: String,
    /** The exception associated with this problem, if it was reported with one. */
    val error: JsError? = null,
    /** A longer, free-form description of the problem, if provided. */
    val problemDetails: String? = null,
    /**
     * A label describing this specific occurrence of the problem in its context, if provided.
     * Preferred over the problem's generic display name when present.
     */
    val contextualLabel: String? = null,
    /** Suggested ways to fix the problem, if any were provided. */
    val solutions: List<String>? = null,
    /** Where the problem occurred, if any locations were captured. See [JsLocation]. */
    val locations: List<JsLocation>? = null
) : JsReportDiagnostic {
    override fun toJson(json: Json): String = json.encodeToString(this)
}


/**
 * One segment of a problem's hierarchical identifier, e.g. a group such as `deprecation` or the
 * problem itself as the final segment.
 *
 * @param name the stable, machine-readable identifier of the segment.
 * @param displayName the human-readable name of the segment.
 */
@Serializable
data class JsProblemIdElement(
    val name: String,
    val displayName: String
)


/**
 * A place a problem is associated with — a position in a source file, a plugin, or a task.
 * Which fields are set depends on the kind of location captured.
 *
 * @param path the file path, when the location points into a source file.
 * @param line the 1-based line within [path], if known.
 * @param column the 1-based column within [line], if known.
 * @param length the length in characters of the offending region, if known.
 * @param pluginId the plugin the problem is attributed to, when the location is a plugin.
 * @param taskPath the path of the task the problem is attributed to, when the location is a task.
 */
@Serializable
data class JsLocation(
    val path: String? = null,
    val line: Int? = null,
    val column: Int? = null,
    val length: Int? = null,
    val pluginId: String? = null,
    val taskPath: String? = null
)


/**
 * The number of problems sharing a given [problemId] that were aggregated into a count rather than
 * reported individually in the report.
 */
@Serializable
data class JsProblemSummary(
    val problemId: List<JsProblemIdElement>,
    val count: Int
)
