package reporting

import components.CopyButtonComponent
import components.PrettyTextComponent
import components.ProblemNode
import data.PrettyText
import data.sIfPlural
import elmish.View
import elmish.attributes
import elmish.code
import elmish.div
import elmish.empty
import elmish.li
import elmish.span
import elmish.tree.Tree
import elmish.tree.Tree.ViewState.Collapsed
import elmish.tree.Tree.ViewState.Expanded
import elmish.tree.TreeView
import elmish.ul
import reporting.BaseIntent.TreeIntent


val errorIcon = span<BaseIntent>(
    attributes { classNames("invisible-text", "error-icon") },
    "[error] "
)


val adviceIcon = span<BaseIntent>(
    attributes { classNames("invisible-text", "advice-icon") },
    "[advice] "
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
    PrettyTextComponent<BaseIntent>()


val PrettyTextWithCopy =
    PrettyTextComponent<BaseIntent> { BaseIntent.Copy(it) }


fun viewPrettyText(text: PrettyText): View<BaseIntent> =
    PrettyTextWithCopy.view(text)


fun viewPrettyText(textBuilder: PrettyText.Builder.() -> Unit): View<BaseIntent> =
    PrettyTextWithCopy.view(PrettyText.build(textBuilder))


fun <I> treeButtonFor(child: Tree.Focus<ProblemNode>, treeIntent: (ProblemTreeIntent) -> I): View<I> {
    return if (child.tree.isNotEmpty()) {
        viewTreeButton(child, treeIntent)
    } else {
        viewLeafIcon(child)
    }
}


fun <I> viewLeafIcon(child: Tree.Focus<ProblemNode>): View<I> = span(
    attributes { classNames("invisible-text", "leaf-icon") },
    copyTextPrefixForTreeNode(child)
)


private
val CopyButton =
    CopyButtonComponent { BaseIntent.Copy(it) }


fun exception(node: ProblemNode.Exception, owner: () -> TreeIntent): View<BaseIntent> = div(
    attributes { className("java-exception") },
    node.parts.mapIndexed { index, part ->
        if (part.state != null) {
            val collapsableLineCount = part.lines.size
            val internalLinesToggle = internalLinesToggle(collapsableLineCount, index, part.state, owner)
            when (part.state) {
                Collapsed -> {
                    exceptionPart(part.lines.takeLast(1), internalLinesToggle)
                }

                Expanded -> {
                    exceptionPart(part.lines, internalLinesToggle)
                }
            }
        } else {
            exceptionPart(part.lines)
        }
    }
)


private
fun visibilityToggleVerb(state: Tree.ViewState): String = when (state) {
    Collapsed -> "show"
    Expanded -> "hide"
}


private
fun visibility(state: Tree.ViewState): String = when (state) {
    Collapsed -> "hidden"
    Expanded -> "shown"
}


private
fun internalLinesToggle(
    hiddenLinesCount: Int,
    partIndex: Int,
    state: Tree.ViewState,
    location: () -> TreeIntent
): View<BaseIntent> = span(
    attributes {
        className("java-exception-part-toggle")
        onClick {
            BaseIntent.ToggleStackTracePart(partIndex, location())
        }
        title("Click to ${visibilityToggleVerb(state)}")
    },
    "($hiddenLinesCount internal ${"line".sIfPlural(hiddenLinesCount)} ${visibility(state)})"
)


private
fun exceptionPart(lines: List<String>, firstLineTail: View<BaseIntent> = empty): View<BaseIntent> = ul(
    lines.mapIndexed { i, line -> exceptionLine(line, if (i == 0) firstLineTail else empty) }
)


private
fun exceptionLine(line: String, lineTail: View<BaseIntent> = empty): View<BaseIntent> =
    li(code(line), lineTail)


fun viewException(
    treeIntent: (ProblemTreeIntent) -> TreeIntent,
    child: Tree.Focus<ProblemNode>,
    node: ProblemNode.Exception
): View<BaseIntent> = div(
    viewTreeButton(child, treeIntent),
    span("Exception"),
    span(CopyButton.view(text = node.fullText, tooltip = "Copy exception to the clipboard")),
    node.summary?.let { span(" ") } ?: empty,
    node.summary?.let { viewPrettyText(it) } ?: empty,
    when (child.tree.state) {
        Collapsed -> empty
        Expanded -> exception(node) { treeIntent(TreeView.Intent.Toggle(child)) }
    }
)


fun treeLabel(
    treeIntent: (ProblemTreeIntent) -> BaseIntent,
    viewNode: (node: ProblemNode) -> View<BaseIntent>,
    focus: Tree.Focus<ProblemNode>,
    label: ProblemNode,
    docLink: ProblemNode? = null,
    prefix: View<BaseIntent> = empty,
    suffix: View<BaseIntent> = empty
): View<BaseIntent> {
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
        if (child.tree.state === Collapsed) {
            className("collapsed")
        }
        if (child.tree.state === Expanded) {
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
    Collapsed -> "expand"
    Expanded -> "collapse"
}
