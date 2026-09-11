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
 * A top-level report model object that can serialize itself to JSON.
 * Serialization is performed for the exact type, so there is no polymorphic deserialization support.
 */
interface JsonSource {
    /**
     * Generates the JSON representation of this object.
     *
     * @param json the serializer entry point
     */
    fun toJson(json: Json): String
}


/**
 * The summary of a report: everything the report says about itself that isn't one of the streamed
 * diagnostics. A report has exactly one summary, and which kind it is decides how the report is
 * rendered.
 */
interface JsReportSummary : JsonSource


/**
 * One of the items a report is about: a configuration cache diagnostic or a reported problem,
 * depending on the kind of report. Producers stream these into the report as they arrive, before
 * they know the [summary][JsReportSummary].
 */
interface JsReportDiagnostic : JsonSource


/**
 * A fragment of a formatted message. A message is a sequence of such fragments, each being either a
 * run of prose ([text]) or a code-style reference such as a class name, task path or property name
 * ([name]). Exactly one of the two is set for a given fragment.
 */
@Serializable
data class JsMessageFragment(
    /** The prose content, when this fragment is a run of text. */
    val text: String? = null,
    /** The referenced identifier, when this fragment is a code reference. */
    val name: String? = null
)


/**
 * An exception associated with a diagnostic, such as a failure thrown from a task's `@Input` getter
 * or from a custom serialization method.
 *
 * @param summary a short description pointing at the first user-code frame, or null if none was found.
 * @param parts the stack trace split into consecutive sections. See [JsStackTracePart].
 */
@Serializable
data class JsError(
    val summary: List<JsMessageFragment>? = null,
    val parts: List<JsStackTracePart>? = null
)


/**
 * One consecutive section of a stack trace, grouped by whether its frames are user code or internal
 * (Gradle/JDK) frames. Exactly one of [text] or [internalText] is set, which also tells the two
 * kinds of section apart.
 */
@Serializable
data class JsStackTracePart(
    /** The frames of this section when they are user code. */
    val text: String? = null,
    /** The frames of this section when they are internal (Gradle/JDK) code. */
    val internalText: String? = null
)
