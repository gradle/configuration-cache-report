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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonClassDiscriminator


/**
 * The summary of a configuration cache report: what the build did with the cache, and the problem
 * counts that the individually reported diagnostics are a subset of.
 *
 * A build produces this summary when it finishes storing or loading the configuration cache, and the
 * report renderer turns it, together with the separately streamed [JsDiagnostic]s, into the HTML page
 * shown to the user.
 *
 * Being a configuration cache summary rather than a problems one (see [JsProblemsSummary]) is what
 * makes the report render as a configuration cache report; see [JsReportSummary].
 */
@Serializable
data class JsConfigurationCacheSummary(
    /** The name of the build the report was produced for, or null for the root build. */
    val buildName: String? = null,
    /**
     * The action the build performed on the configuration cache, such as storing or loading it.
     * Used as the leading verb of the report heading.
     */
    val cacheAction: String,
    /**
     * The tasks requested for the build as a display string (for example `build` or `clean test`),
     * or null when the build ran its default tasks.
     */
    val requestedTasks: String? = null,
    /** An optional, more detailed explanation of the [cacheAction], shown in the report summary. */
    val cacheActionDescription: List<JsMessageFragment>? = null,
    /** A link to the configuration cache documentation. */
    val documentationLink: String,
    /** The total number of problems encountered, counting duplicates and any beyond the reporting limit. */
    val totalProblemCount: Int,
    /** The number of distinct problems among the [totalProblemCount]. */
    val uniqueProblemCount: Int,
    /**
     * The number of problems that exceeded the reporting limit and are therefore not among the
     * reported [JsDiagnostic]s.
     */
    val overflownProblemCount: Int
) : JsReportSummary {
    override val elementId: String
        get() = ELEMENT_ID

    override fun toJson(json: Json): String = json.encodeToString(this)

    companion object {
        /** The id of the `<script>` element carrying this summary. */
        const val ELEMENT_ID = "configuration-cache-summary"
    }
}


/**
 * A single item shown in the report. Every diagnostic is exactly one of a build configuration
 * [input], a configuration cache [problem], or an [incompatibleTask] description, together with the
 * [trace] that locates it and an optional [error] that caused it.
 */
@Serializable
data class JsDiagnostic(
    /**
     * Set when the diagnostic describes a build configuration input — a value read while the build is
     * configured (such as a system property, environment variable or file) that causes the cache to
     * be discarded when it changes. Holds the input's description.
     */
    val input: List<JsMessageFragment>? = null,
    /** Set when the diagnostic describes a configuration cache problem. Holds the problem's description. */
    val problem: List<JsMessageFragment>? = null,
    /**
     * Set when the diagnostic describes a task that is incompatible with the configuration cache.
     * Holds the task's description.
     */
    val incompatibleTask: List<JsMessageFragment>? = null,
    /** Locates the reported item, from the build graph root down to the offending value. See [JsTrace]. */
    val trace: List<JsTrace> = emptyList(),
    /** A link to documentation relevant to this diagnostic, if any. */
    val documentationLink: String? = null,
    /** The exception this item was caused by or reported with, if any. */
    val error: JsError? = null
) : JsReportDiagnostic {
    override fun toJson(json: Json): String = json.encodeToString(this)
}


/**
 * A single element of the trace that locates a reported item: the path from the build graph root
 * (a project or task) down through the beans, properties and lambdas that lead to the offending
 * value.
 *
 * Each concrete subtype captures a different kind of element on that path.
 *
 * On the wire each element is a JSON object tagged by a `"kind"` discriminator (for example
 * `{"kind": "Project", "path": ":app"}`), which kotlinx.serialization dispatches on to pick the
 * concrete subtype.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed class JsTrace


/** A Gradle project on the trace, e.g. `:app` or `:lib:core`. */
@Serializable
@SerialName("Project")
data class JsTraceProject(val path: String) : JsTrace()


/**
 * A Gradle task on the trace, including its implementation type, e.g. task `:app:compileJava` of
 * type `org.gradle.api.tasks.compile.JavaCompile`.
 *
 * @param path the build-tree-qualified identity path of the task. For tasks in included builds this
 *   includes the build name prefix, e.g. `:my-build:app:compileJava`.
 * @param type the task's implementation type.
 */
@Serializable
@SerialName("Task")
data class JsTraceTask(val path: String, val type: String) : JsTrace()


/**
 * A Gradle task on the trace referenced only by its [path], without its type. Used where the task
 * type is not relevant, such as for tasks that are incompatible with the configuration cache.
 */
@Serializable
@SerialName("TaskPath")
data class JsTraceTaskPath(val path: String) : JsTrace()


/**
 * A Java bean being serialized as part of the configuration cache entry,
 * e.g. `org.gradle.api.internal.artifacts.configurations.DefaultConfiguration`.
 */
@Serializable
@SerialName("Bean")
data class JsTraceBean(val type: String) : JsTrace()


/**
 * State captured by a lambda expression or method reference on the trace.
 *
 * @param `class` the class that declares the implementation method. For a lambda this is the class
 *   enclosing the lambda; for a method reference it is the class declaring the referenced method,
 *   which may differ from where the reference appears.
 * @param method the enclosing method for a lambda (derived from the generated implementation method)
 *   or the referenced method for a method reference.
 * @param subkind `"lambdaBody"` for state captured by a lambda expression, or `"boundReceiver"` for
 *   the bound receiver of a method reference.
 */
@Serializable
@SerialName("CapturedArguments")
data class JsTraceCapturedArguments(
    val `class`: String,
    val method: String,
    val subkind: String
) : JsTrace()


/**
 * A lambda on the trace. The lambda itself is not necessarily the source of the problem — it is an
 * intermediate step on the path to the offending value.
 *
 * @param type the raw functional interface class name, without generic arguments, e.g. `org.gradle.api.Action`.
 * @param returns the instantiated return type, e.g. `void` for an `Action`.
 */
@Serializable
@SerialName("SerializedLambda")
data class JsTraceSerializedLambda(val type: String, val returns: String) : JsTrace()


/**
 * A Java field on the trace.
 *
 * @param name the field name.
 * @param declaringType the name of the class that declares the field.
 */
@Serializable
@SerialName("Field")
data class JsTraceField(val name: String, val declaringType: String) : JsTrace()


/**
 * A task input property on the trace, e.g. a property annotated with `@Input` or `@InputFiles`.
 *
 * @param name the property name in code, e.g. `sourceDir`.
 * @param task the path of the task the property belongs to.
 */
@Serializable
@SerialName("InputProperty")
data class JsTraceInputProperty(val name: String, val task: String) : JsTrace()


/**
 * A task output property on the trace, e.g. a property annotated with `@OutputFile` or
 * `@OutputDirectory`.
 *
 * @param name the property name in code, e.g. `outputDir`.
 * @param task the path of the task the property belongs to.
 */
@Serializable
@SerialName("OutputProperty")
data class JsTraceOutputProperty(val name: String, val task: String) : JsTrace()


/**
 * A synthetic property on the trace that has a user-facing name but no direct counterpart in code,
 * such as the up-to-date predicate of a task's outputs or an action registered via `doFirst`/`doLast`.
 *
 * @param name the user-facing name of the property.
 * @param owner the task or object the property belongs to.
 */
@Serializable
@SerialName("VirtualProperty")
data class JsTraceVirtualProperty(val name: String, val owner: String) : JsTrace()


/**
 * A property accessed from a project on the trace.
 *
 * @param name the property name.
 * @param from the path of the project the property was accessed from.
 */
@Serializable
@SerialName("PropertyUsage")
data class JsTracePropertyUsage(val name: String, val from: String) : JsTrace()


/**
 * A JVM system property read while the build is configured, e.g. `java.io.tmpdir` or `user.home`.
 * Reading a system property is tracked as a build configuration input.
 */
@Serializable
@SerialName("SystemProperty")
data class JsTraceSystemProperty(val name: String) : JsTrace()


/**
 * A location in a build script or settings file, e.g. `build.gradle.kts:42` or `settings.gradle:10`.
 * The outermost element of the trace for items that originate directly in build logic.
 */
@Serializable
@SerialName("BuildLogic")
data class JsBuildLogic(val location: String) : JsTrace()


/**
 * A class defined in build logic — a `buildSrc` class or an included-build plugin class,
 * e.g. `com.example.MyPlugin`. Appears when a plugin class itself is the source of the problem
 * rather than a specific script line.
 */
@Serializable
@SerialName("BuildLogicClass")
data class JsBuildLogicClass(val type: String) : JsTrace()


/**
 * The Gradle runtime itself as the origin of a reported item, used when the item cannot be
 * attributed to any more specific location on the trace.
 */
@Serializable
@SerialName("Gradle")
data object JsTraceGradle : JsTrace()


/**
 * An unknown origin, used when no location could be determined for a reported item.
 */
@Serializable
@SerialName("Unknown")
data object JsTraceUnknown : JsTrace()
