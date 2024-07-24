package components

import data.PrettyText
import elmish.tree.Tree


open class ProblemNode {
    data class Exception(
        val summary: PrettyText?,
        val fullText: String,
        val parts: List<StackTracePart>
    ) : ProblemNode()

    data class StackTracePart(
        val lines: List<String>,
        val state: Tree.ViewState?
    )
}
