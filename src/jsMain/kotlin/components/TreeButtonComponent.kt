package components

import elmish.View
import elmish.attributes
import elmish.span
import elmish.tree.Tree

class TreeButtonComponent<M, I>(
    private val treeIntent: (Tree.Focus<M>) -> I
) {

    fun view(child: Tree.Focus<M>): View<I> = treeButtonFor(child)

    private
    fun treeButtonFor(child: Tree.Focus<M>): View<I> =
        when {
            child.tree.isNotEmpty() -> viewTreeButton(child)
            else -> viewLeafIcon(child)
        }

    private
    fun viewTreeButton(child: Tree.Focus<M>): View<I> = span(
        attributes {
            classNames("invisible-text", "tree-btn")
            if (child.tree.state === Tree.ViewState.Collapsed) {
                className("collapsed")
            }
            if (child.tree.state === Tree.ViewState.Expanded) {
                className("expanded")
            }
            title("Click to ${toggleVerb(child.tree.state)}")
            onClick { treeIntent(child) }
        },
        copyTextPrefixForTreeNode(child)
    )

    private
    fun viewLeafIcon(child: Tree.Focus<M>): View<I> = span(
        attributes { classNames("invisible-text", "leaf-icon") },
        copyTextPrefixForTreeNode(child)
    )

    private
    fun copyTextPrefixForTreeNode(child: Tree.Focus<M>) =
        "    ".repeat(child.depth - 1) + "- "

    private
    fun toggleVerb(state: Tree.ViewState): String = when (state) {
        Tree.ViewState.Collapsed -> "expand"
        Tree.ViewState.Expanded -> "collapse"
    }
}
