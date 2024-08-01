package reporting

import components.PrettyTextComponent
import components.ProblemNode
import configurationCache.ConfigurationCacheReportPage.Intent
import configurationCache.treeButtonFor
import data.PrettyText
import elmish.View
import elmish.attributes
import elmish.div
import elmish.empty
import elmish.span
import elmish.tree.Tree
import elmish.tree.TreeView


val errorIcon = span<BaseIntent>(
    attributes { classNames("invisible-text", "error-icon") },
    "[error] "
)


val warningIcon = span<BaseIntent>(
    attributes { classNames("invisible-text", "warning-icon") },
    "[warn]  " // two spaces to align with [error] prefix
)


val enumIcon = span<BaseIntent>(
    attributes { classNames("invisible-text", "enum-icon") },
    "[enum]  " // two spaces to align with [error] prefix
)


val PrettyTextNoCopy =
    PrettyTextComponent<Intent>()


val PrettyTextWithCopy =
    PrettyTextComponent<BaseIntent> { BaseIntent.Copy(it) }


fun viewPrettyText(text: PrettyText): View<BaseIntent> =
    PrettyTextWithCopy.view(text)


fun treeLabel(
    treeIntent: (ProblemTreeIntent) -> BaseIntent,
    viewNode: (node: ProblemNode) -> View<BaseIntent>,
    focus: Tree.Focus<ProblemNode>,
    label: ProblemNode,
    docLink: ProblemNode? = null,
    prefix: View<BaseIntent> = empty,
    suffix: View<BaseIntent> = empty
): View<BaseIntent> {
    console.error("treeLabel: $label")
    return div(
        treeButtonFor(focus, treeIntent),
        prefix,
        viewNode(label),
        docLink?.let(viewNode) ?: empty,
        suffix
    )
}


fun <I> viewTreeButton(child: Tree.Focus<ProblemNode>, treeIntent: (ProblemTreeIntent) -> I): View<I> = span(
    attributes {
        classNames("invisible-text", "tree-btn")
        if (child.tree.state === Tree.ViewState.Collapsed) {
            className("collapsed")
        }
        if (child.tree.state === Tree.ViewState.Expanded) {
            className("expanded")
        }
        title("Click to ${toggleVerb(child.tree.state)}")
        onClick { treeIntent(TreeView.Intent.Toggle(child)) }
    },
    copyTextPrefixForTreeNode(child)
)


fun copyTextPrefixForTreeNode(child: Tree.Focus<ProblemNode>) =
    "    ".repeat(child.depth - 1) + "- "


fun toggleVerb(state: Tree.ViewState): String = when (state) {
    Tree.ViewState.Collapsed -> "expand"
    Tree.ViewState.Expanded -> "collapse"
}
