package configurationCache

import components.ProblemNode
import elmish.tree.TreeView


internal
typealias ProblemTreeModel = TreeView.Model<ProblemNode>


internal
typealias ProblemTreeIntent = TreeView.Intent<ProblemNode>

open class BaseIntent {
    data class Copy(val text: String) : BaseIntent()

    abstract class TreeIntent : BaseIntent() {
        abstract val delegate: ProblemTreeIntent
    }
}
