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

package reporting

import components.ProblemNode
import reporting.BaseIntent.TreeIntent
import elmish.tree.TreeView


internal
typealias ProblemTreeModel = TreeView.Model<ProblemNode>


fun ProblemTreeModel.updateNodeTreeAt(
    tree: TreeIntent,
    update: ProblemNode.() -> ProblemNode
): TreeView.Model<ProblemNode> = updateLabelAt(
    tree.delegate.focus,
    update
)


internal
typealias ProblemTreeIntent = TreeView.Intent<ProblemNode>


open class BaseIntent {
    data class Copy(val text: String) : BaseIntent()

    abstract class TreeIntent : BaseIntent() {
        abstract val delegate: ProblemTreeIntent
    }

    data class ToggleStackTracePart(val partIndex: Int, val location: TreeIntent) : BaseIntent()
}
