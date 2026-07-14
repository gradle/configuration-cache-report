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


/**
 * The model of a problems report.
 *
 * A build produces this model to report the problems collected through the Problems API, and the
 * report renderer turns it into the HTML page shown to the user. The reported problems themselves
 * are carried separately; this model holds the surrounding context and the summaries of problems
 * that were aggregated rather than listed individually.
 */
data class ProblemReportJsModel(
    /** The name of the build the report was produced for, or null for the root build. */
    val buildName: String?,
    /** The tasks requested for the build as a display string, or null when the build ran its default tasks. */
    val requestedTasks: String?,
    /** A link to the documentation about reporting problems. */
    val documentationLink: String,
    /** Counts of problems that were aggregated by their id instead of being reported individually. */
    val summaries: List<JsProblemSummary>
)


/**
 * A single problem reported through the Problems API.
 */
data class JsProblem(
    /**
     * The hierarchical identifier of the problem, from the outermost group down to the specific
     * problem as its last element. See [JsProblemIdElement].
     */
    val problemId: List<JsProblemIdElement>,
    /** A link to documentation relevant to this problem, if any. */
    val documentationLink: String?,
    /** The problem's severity, such as `ERROR`, `WARNING` or `ADVICE`. */
    val severity: String,
    /** The exception associated with this problem, if it was reported with one. */
    val error: JsError?,
    /** A longer, free-form description of the problem, if provided. */
    val problemDetails: String?,
    /**
     * A label describing this specific occurrence of the problem in its context, if provided.
     * Preferred over the problem's generic display name when present.
     */
    val contextualLabel: String?,
    /** Suggested ways to fix the problem, if any were provided. */
    val solutions: List<String>?,
    /** Where the problem occurred, if any locations were captured. See [JsLocation]. */
    val locations: List<JsLocation>?
)


/**
 * One segment of a problem's hierarchical identifier, e.g. a group such as `deprecation` or the
 * problem itself as the final segment.
 *
 * @param name the stable, machine-readable identifier of the segment.
 * @param displayName the human-readable name of the segment.
 */
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
data class JsLocation(
    val path: String?,
    val line: Int?,
    val column: Int?,
    val length: Int?,
    val pluginId: String?,
    val taskPath: String?
)


/**
 * The number of problems sharing a given [problemId] that were aggregated into a count rather than
 * reported individually in the report.
 */
data class JsProblemSummary(
    val problemId: List<JsProblemIdElement>,
    val count: Int
)
