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
 * The serialized model of a configuration cache report.
 *
 * A build produces this model when it finishes storing or loading the configuration cache, and the
 * report renderer turns it into the HTML page shown to the user. It summarizes what the cache did
 * and lists every build configuration input, problem and incompatible task that was encountered.
 */
data class JsModel(
    /**
     * The contents of a generic Problems API report shown in place of a configuration cache report.
     * When set, the configuration cache specific fields of this model are not meaningful and the
     * report is rendered as a plain problems report instead. This is a temporary coupling of the two
     * report kinds that is expected to be separated in the future.
     */
    val problemsReport: Any?,
    /** The name of the build the report was produced for, or null for the root build. */
    val buildName: String?,
    /**
     * The action the build performed on the configuration cache, such as storing or loading it.
     * Used as the leading verb of the report heading.
     */
    val cacheAction: String,
    /**
     * The tasks requested for the build as a display string (for example `build` or `clean test`),
     * or null when the build ran its default tasks.
     */
    val requestedTasks: String?,
    /** An optional, more detailed explanation of the [cacheAction], shown in the report summary. */
    val cacheActionDescription: List<JsMessageFragment>?,
    /** A link to the configuration cache documentation. */
    val documentationLink: String,
    /** The total number of problems encountered, counting duplicates and any beyond the reporting limit. */
    val totalProblemCount: Int,
    /** The number of distinct problems among the [totalProblemCount]. */
    val uniqueProblemCount: Int,
    /** The number of problems that exceeded the reporting limit and are therefore not present in [diagnostics]. */
    val overflownProblemCount: Int,
    /** The build configuration inputs, problems and incompatible tasks to display. */
    val diagnostics: List<JsDiagnostic>
)


/**
 * A single item shown in the report. Every diagnostic is exactly one of a build configuration
 * [input], a configuration cache [problem], or an [incompatibleTask] description, together with the
 * [trace] that locates it and an optional [error] that caused it.
 */
data class JsDiagnostic(
    /**
     * Set when the diagnostic describes a build configuration input — a value read while the build is
     * configured (such as a system property, environment variable or file) that causes the cache to
     * be discarded when it changes. Holds the input's description.
     */
    val input: List<JsMessageFragment>?,
    /** Set when the diagnostic describes a configuration cache problem. Holds the problem's description. */
    val problem: List<JsMessageFragment>?,
    /**
     * Set when the diagnostic describes a task that is incompatible with the configuration cache.
     * Holds the task's description.
     */
    val incompatibleTask: List<JsMessageFragment>?,
    /** Locates the reported item, from the build graph root down to the offending value. See [JsTrace]. */
    val trace: List<JsTrace>,
    /** A link to documentation relevant to this diagnostic, if any. */
    val documentationLink: String?,
    /** The exception this item was caused by or reported with, if any. */
    val error: JsError?
)


/**
 * A single element of the trace that locates a reported item: the path from the build graph root
 * (a project or task) down through the beans, properties and lambdas that lead to the offending
 * value.
 *
 * Each concrete subtype captures a different kind of element on that path. An element that the model
 * does not describe with a dedicated type is represented by [JsGenericTrace].
 */
sealed class JsTrace


/** A Gradle project on the trace, e.g. `:app` or `:lib:core`. */
data class JsTraceProject(val path: String) : JsTrace()


/**
 * A Gradle task on the trace, including its implementation type, e.g. task `:app:compileJava` of
 * type `org.gradle.api.tasks.compile.JavaCompile`.
 *
 * @param path the build-tree-qualified identity path of the task. For tasks in included builds this
 *   includes the build name prefix, e.g. `:my-build:app:compileJava`.
 * @param type the task's implementation type.
 */
data class JsTraceTask(val path: String, val type: String) : JsTrace()


/**
 * A Gradle task on the trace referenced only by its [path], without its type. Used where the task
 * type is not relevant, such as for tasks that are incompatible with the configuration cache.
 */
data class JsTraceTaskPath(val path: String) : JsTrace()


/**
 * A Java bean being serialized as part of the configuration cache entry,
 * e.g. `org.gradle.api.internal.artifacts.configurations.DefaultConfiguration`.
 */
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
data class JsTraceSerializedLambda(val type: String, val returns: String) : JsTrace()


/**
 * A Java field on the trace.
 *
 * @param name the field name.
 * @param declaringType the name of the class that declares the field.
 */
data class JsTraceField(val name: String, val declaringType: String) : JsTrace()


/**
 * A task input property on the trace, e.g. a property annotated with `@Input` or `@InputFiles`.
 *
 * @param name the property name in code, e.g. `sourceDir`.
 * @param task the path of the task the property belongs to.
 */
data class JsTraceInputProperty(val name: String, val task: String) : JsTrace()


/**
 * A task output property on the trace, e.g. a property annotated with `@OutputFile` or
 * `@OutputDirectory`.
 *
 * @param name the property name in code, e.g. `outputDir`.
 * @param task the path of the task the property belongs to.
 */
data class JsTraceOutputProperty(val name: String, val task: String) : JsTrace()


/**
 * A synthetic property on the trace that has a user-facing name but no direct counterpart in code,
 * such as the up-to-date predicate of a task's outputs or an action registered via `doFirst`/`doLast`.
 *
 * @param name the user-facing name of the property.
 * @param owner the task or object the property belongs to.
 */
data class JsTraceVirtualProperty(val name: String, val owner: String) : JsTrace()


/**
 * A property accessed from a project on the trace.
 *
 * @param name the property name.
 * @param from the path of the project the property was accessed from.
 */
data class JsTracePropertyUsage(val name: String, val from: String) : JsTrace()


/**
 * A JVM system property read while the build is configured, e.g. `java.io.tmpdir` or `user.home`.
 * Reading a system property is tracked as a build configuration input.
 */
data class JsTraceSystemProperty(val name: String) : JsTrace()


/**
 * A location in a build script or settings file, e.g. `build.gradle.kts:42` or `settings.gradle:10`.
 * The outermost element of the trace for items that originate directly in build logic.
 */
data class JsBuildLogic(val location: String) : JsTrace()


/**
 * A class defined in build logic — a `buildSrc` class or an included-build plugin class,
 * e.g. `com.example.MyPlugin`. Appears when a plugin class itself is the source of the problem
 * rather than a specific script line.
 */
data class JsBuildLogicClass(val type: String) : JsTrace()


/**
 * A trace element whose kind is not one of the specific kinds above. Acts as a forward-compatible
 * fallback so that elements introduced by newer producers can still be represented and displayed.
 *
 * @param kind the name identifying the element kind.
 */
data class JsGenericTrace(val kind: String) : JsTrace()


/**
 * A fragment of a formatted message. A message is a sequence of such fragments, each being either a
 * run of prose ([text]) or a code-style reference such as a class name, task path or property name
 * ([name]). Exactly one of the two is set for a given fragment.
 */
data class JsMessageFragment(
    /** The prose content, when this fragment is a run of text. */
    val text: String?,
    /** The referenced identifier, when this fragment is a code reference. */
    val name: String?
)


/**
 * An exception associated with a diagnostic, such as a failure thrown from a task's `@Input` getter
 * or from a custom serialization method.
 *
 * @param summary a short description pointing at the first user-code frame, or null if none was found.
 * @param parts the stack trace split into consecutive sections. See [JsStackTracePart].
 */
data class JsError(
    val summary: List<JsMessageFragment>?,
    val parts: List<JsStackTracePart>?
)


/**
 * One consecutive section of a stack trace, grouped by whether its frames are user code or internal
 * (Gradle/JDK) frames. Exactly one of [text] or [internalText] is set, which also tells the two
 * kinds of section apart.
 */
data class JsStackTracePart(
    /** The frames of this section when they are user code. */
    val text: String?,
    /** The frames of this section when they are internal (Gradle/JDK) code. */
    val internalText: String?
)
