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

package components

import data.PrettyText
import elmish.tree.Tree


/**
 * A node in the problem tree data model.
 *
 * Nodes are composed into a [Tree]. The open hierarchy allows report-specific subtypes
 * (e.g. [configurationCache.ProblemCCNode]) to extend it with domain-specific node kinds.
 */
open class ProblemNode {

    /**
     * A Java exception associated with a problem, e.g. a failure in a task's `@Input` property
     * getter, a `task.project` access during task execution, or a custom serialization method
     * that throws.
     *
     * @param summary a short PrettyText excerpt pointing to the first user-code frame, or null
     *   if no user-code frame was found in the stack trace.
     * @param fullText the full plain-text stack trace.
     * @param parts the stack trace split into alternating user-code and internal sections.
     */
    data class Exception(val summary: PrettyText?, val fullText: String, val parts: List<StackTracePart>) :
        ProblemNode()

    /**
     * One contiguous section of a stack trace, grouped by whether the frames are internal
     * (Gradle/JDK) or user code.
     *
     * @param lines individual stack trace lines in this section.
     * @param state present for internal sections, and null for user-code sections.
     */
    data class StackTracePart(val lines: List<String>, val state: Tree.ViewState?)

    /**
     * A problem of error severity, e.g. a configuration cache incompatibility that causes
     * the cache entry to be discarded.
     *
     * @param label the primary description of the problem; in practice always a [Message] or [Label].
     * @param docLink a link to the relevant documentation; in practice always a [Link] or null.
     * @param count optional number of occurrences when several identical problems are grouped.
     */
    data class Error(val label: ProblemNode, val docLink: ProblemNode?, val count: Int? = null) : ProblemNode()

    /**
     * A problem of warning severity, e.g. a configuration cache problem that is reported but
     * does not prevent the cache entry from being stored.
     *
     * @param label the primary description of the problem; in practice always a [Message] or [Label].
     * @param docLink a link to the relevant documentation; in practice always a [Link] or null.
     * @param count optional number of occurrences when several identical problems are grouped.
     */
    data class Warning(val label: ProblemNode, val docLink: ProblemNode?, val count: Int? = null) : ProblemNode()

    /**
     * A formatted message composed of text and reference fragments, e.g. the human-readable
     * description of a problem ("value of type 'SomeClass' cannot be serialized").
     */
    data class Message(val prettyText: PrettyText) : ProblemNode()

    /**
     * A leaf item that has no children of its own, e.g. a file location ("build.gradle:42")
     * or a suggested solution ("Use a Provider instead of eagerly resolving the value").
     */
    data class ListElement(val prettyText: PrettyText) : ProblemNode()

    /**
     * A named group that contains child nodes, e.g. "Solutions" or a problem category
     * name from the problem id hierarchy ("deprecation", "dependency-version-catalog").
     */
    data class TreeNode(val prettyText: PrettyText) : ProblemNode()

    /**
     * A documentation link pointing at the given URL.
     * Always appears as the [Error.docLink] or [Warning.docLink] of a parent node.
     */
    data class Link(val href: String) : ProblemNode()

    /**
     * A plain unformatted text label. Used for:
     * - the root node of each tree, which carries the tree's name (e.g. "Problems grouped by
     *   message") but is not itself part of the problem data.
     * - input category prefixes extracted from the first text fragment of an input message
     *   (e.g. "read system property")
     */
    data class Label(val text: String) : ProblemNode()
}
