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


open class ProblemNode {
    data class Exception(val summary: PrettyText?, val fullText: String, val parts: List<StackTracePart>) :
        ProblemNode()

    data class StackTracePart(val lines: List<String>, val state: Tree.ViewState?)

    data class Error(val label: ProblemNode, val docLink: ProblemNode?) : ProblemNode()

    data class Warning(val label: ProblemNode, val docLink: ProblemNode?) : ProblemNode()

    data class Message(val prettyText: PrettyText) : ProblemNode()

    data class ListElement(val prettyText: PrettyText) : ProblemNode()

    data class TreeNode(val prettyText: PrettyText) : ProblemNode()

    data class Link(val href: String, val label: String) : ProblemNode()

    data class Label(val text: String) : ProblemNode()
}
