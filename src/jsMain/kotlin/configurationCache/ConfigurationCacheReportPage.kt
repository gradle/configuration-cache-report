package configurationCache/*
 * Copyright 2019 the original author or authors.
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

import components.*
import components.CopyButtonComponent
import components.PrettyTextComponent
import components.invisibleCloseParen
import components.invisibleOpenParen
import components.invisibleSpace
import configurationCache.BaseIntent.TreeIntent
import configurationCache.ConfigurationCacheReportPage.Intent
import data.LearnMore
import data.PrettyText
import data.mapAt
import data.sIfPlural
import elmish.Component
import elmish.View
import elmish.a
import elmish.attributes
import elmish.br
import elmish.code
import elmish.div
import elmish.empty
import elmish.h1
import elmish.li
import elmish.ol
import elmish.small
import elmish.span
import elmish.tree.Tree
import elmish.tree.TreeView
import elmish.tree.viewSubTrees
import elmish.ul
import kotlinx.browser.window


sealed class ProblemCCNode: ProblemNode() {

    data class Error(val label: ProblemNode, val docLink: ProblemNode?) : ProblemNode()

    data class Warning(val label: ProblemNode, val docLink: ProblemNode?) : ProblemNode()

    data class Info(val label: ProblemNode, val docLink: ProblemNode?) : ProblemNode()

    data class Project(val path: String) : ProblemNode()

    data class Task(val path: String, val type: String) : ProblemNode()

    data class TaskPath(val path: String) : ProblemNode()

    data class Bean(val type: String) : ProblemNode()

    data class SystemProperty(val name: String) : ProblemNode()

    data class Property(val kind: String, val name: String, val owner: String) : ProblemNode()

    data class BuildLogic(val location: String) : ProblemNode()

    data class BuildLogicClass(val type: String) : ProblemNode()

    data class Label(val text: String) : ProblemNode()

    data class Link(val href: String, val label: String) : ProblemNode()

    data class Message(val prettyText: PrettyText) : ProblemNode()
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

private
fun viewPrettyText(text: PrettyText): View<BaseIntent> =
    PrettyTextWithCopy.view(text)

private
fun viewPrettyText(textBuilder: PrettyText.Builder.() -> Unit): View<BaseIntent> =
    PrettyTextWithCopy.view(PrettyText.build(textBuilder))

private
val PrettyTextNoCopy =
    PrettyTextComponent<Intent>()

private
val PrettyTextWithCopy =
    PrettyTextComponent<BaseIntent> { BaseIntent.Copy(it) }

fun <I> treeButtonFor(child: Tree.Focus<ProblemNode>, treeIntent: (ProblemTreeIntent) -> I): View<I> =
    when {
        child.tree.isNotEmpty() -> viewTreeButton(child, treeIntent)
        else -> viewLeafIcon(child)
    }

fun <I> viewLeafIcon(child: Tree.Focus<ProblemNode>): View<I> = span(
    attributes { classNames("invisible-text", "leaf-icon") },
    copyTextPrefixForTreeNode(child)
)

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
        Tree.ViewState.Collapsed -> empty
        Tree.ViewState.Expanded -> exception(node) { treeIntent(TreeView.Intent.Toggle(child)) }
    }
)

private
fun visibilityToggleVerb(state: Tree.ViewState): String = when (state) {
    Tree.ViewState.Collapsed -> "show"
    Tree.ViewState.Expanded -> "hide"
}

private
fun visibility(state: Tree.ViewState): String = when (state) {
    Tree.ViewState.Collapsed -> "hidden"
    Tree.ViewState.Expanded -> "shown"
}

private
val CopyButton =
    CopyButtonComponent { BaseIntent.Copy(it) }

private
fun internalLinesToggle(
    hiddenLinesCount: Int,
    partIndex: Int,
    state: Tree.ViewState,
    location: () -> TreeIntent
): View<Intent> = span(
    attributes {
        className("java-exception-part-toggle")
        onClick {
            Intent.ToggleStackTracePart(partIndex, location())
        }
        title("Click to ${visibilityToggleVerb(state)}")
    },
    "($hiddenLinesCount internal ${"line".sIfPlural(hiddenLinesCount)} ${visibility(state)})"
)

private
fun exceptionPart(lines: List<String>, firstLineTail: View<Intent> = empty): View<Intent> = ul(
    lines.mapIndexed { i, line -> exceptionLine(line, if (i == 0) firstLineTail else empty) }
)

private
fun exceptionLine(line: String, lineTail: View<Intent> = empty): View<Intent> =
    li(code(line), lineTail)

fun exception(node: ProblemNode.Exception, owner: () -> TreeIntent): View<Intent> = div(
    attributes { className("java-exception") },
    node.parts.mapIndexed { index, part ->
        if (part.state != null) {
            val collapsableLineCount = part.lines.size
            val internalLinesToggle = internalLinesToggle(collapsableLineCount, index, part.state, owner)
            when (part.state) {
                Tree.ViewState.Collapsed -> {
                    exceptionPart(part.lines.takeLast(1), internalLinesToggle)
                }

                Tree.ViewState.Expanded -> {
                    exceptionPart(part.lines, internalLinesToggle)
                }
            }
        } else {
            exceptionPart(part.lines)
        }
    }
)

internal
val ProblemTreeModel.childCount: Int
    get() = tree.children.size


object ConfigurationCacheReportPage :
    Component<ConfigurationCacheReportPage.Model, BaseIntent> {

    data class Model(
        val heading: PrettyText,
        val summary: List<PrettyText>,
        val learnMore: LearnMore,
        val messageTree: ProblemTreeModel,
        val locationTree: ProblemTreeModel,
        val inputTree: ProblemTreeModel,
        val incompatibleTaskTree: ProblemTreeModel,
        val tab: Tab
    )

    enum class Tab(val text: String) {
        Inputs("Build configuration inputs"),
        ByMessage("Problems grouped by message"),
        ByLocation("Problems grouped by location"),
        IncompatibleTasks("Incompatible tasks")
    }

    sealed class Intent: BaseIntent() {

        data class TaskTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class MessageTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class InputTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class IncompatibleTaskTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class ToggleStackTracePart(val partIndex: Int, val location: TreeIntent) : Intent()

        data class SetTab(val tab: Tab) : Intent()
    }

    override fun step(intent: BaseIntent, model: Model): Model = when (intent) {
        is Intent.TaskTreeIntent -> model.copy(
            locationTree = TreeView.step(intent.delegate, model.locationTree)
        )

        is Intent.MessageTreeIntent -> model.copy(
            messageTree = TreeView.step(intent.delegate, model.messageTree)
        )

        is Intent.InputTreeIntent -> model.copy(
            inputTree = TreeView.step(intent.delegate, model.inputTree)
        )

        is Intent.IncompatibleTaskTreeIntent -> model.copy(
            incompatibleTaskTree = TreeView.step(intent.delegate, model.incompatibleTaskTree)
        )

        is Intent.ToggleStackTracePart -> model.updateNodeAt(intent.location) {
            require(this is ProblemNode.Exception)
            copy(parts = parts.mapAt(intent.partIndex) {
                it.copy(state = it.state?.toggle())
            })
        }

        is BaseIntent.Copy -> {
            window.navigator.clipboard.writeText(intent.text)
            model
        }

        is Intent.SetTab -> model.copy(
            tab = intent.tab
        )

        else -> {
            console.error("Unhandled intent: $intent")
            model
        }
    }

    private
    fun Model.updateNodeAt(
        tree: TreeIntent,
        update: ProblemNode.() -> ProblemNode
    ) = when (tree) {
        is Intent.MessageTreeIntent -> copy(
            messageTree = messageTree.updateNodeAt(tree, update)
        )

        is Intent.TaskTreeIntent -> copy(
            locationTree = locationTree.updateNodeAt(tree, update)
        )

        is Intent.InputTreeIntent -> copy(
            inputTree = inputTree.updateNodeAt(tree, update)
        )

        is Intent.IncompatibleTaskTreeIntent -> copy(
            incompatibleTaskTree = incompatibleTaskTree.updateNodeAt(tree, update)
        )

        else -> {
            console.error("Unhandled tree intent: $tree")
            this
        }
    }

    private
    fun ProblemTreeModel.updateNodeAt(
        tree: TreeIntent,
        update: ProblemNode.() -> ProblemNode
    ): TreeView.Model<ProblemNode> = updateLabelAt(
        tree.delegate.focus,
        update
    )

    override fun view(model: Model): View<BaseIntent> = div(
        attributes { className("report-wrapper") },
        viewHeader(model),
        viewProblems(model)
    )

    private
    fun viewHeader(model: Model): View<BaseIntent> = div(
        attributes { className("header") },
        div(attributes { className("gradle-logo") }),
        learnMore(model.learnMore),
        div(
            attributes { className("title") },
            displaySummary(model),
        ),
        div(
            attributes { className("groups") },
            displayTabButton(Tab.Inputs, model.tab, model.inputTree.childCount),
            displayTabButton(Tab.ByMessage, model.tab, model.messageTree.childCount),
            displayTabButton(Tab.ByLocation, model.tab, model.locationTree.childCount),
            displayTabButton(Tab.IncompatibleTasks, model.tab, model.incompatibleTaskTree.childCount)
        )
    )

    private
    fun viewProblems(model: Model) = div(
        attributes { className("content") },
        when (model.tab) {
            Tab.Inputs -> viewTree(model.inputTree, Intent::InputTreeIntent)
            Tab.IncompatibleTasks -> viewTree(model.incompatibleTaskTree, Intent::IncompatibleTaskTreeIntent)
            Tab.ByMessage -> viewTree(model.messageTree, Intent::MessageTreeIntent)
            Tab.ByLocation -> viewTree(model.locationTree, Intent::TaskTreeIntent)
        }
    )

    private
    fun displaySummary(model: Model): View<BaseIntent> = div(
        displayHeading(model),
        viewSummaryParagraphs(model),
    )

    private
    fun viewSummaryParagraphs(model: Model): View<BaseIntent> = div(
        model.summary.flatMapIndexed { index, item ->
            if (index == 0) listOf(viewSummaryParagraph(item))
            else listOf(br(), viewSummaryParagraph(item))
        }
    )

    private
    fun viewSummaryParagraph(content: PrettyText): View<BaseIntent> = small(viewPrettyText(content))

    private
    fun displayHeading(model: Model): View<Intent> = h1(PrettyTextNoCopy.view(model.heading))

    private
    fun displayTabButton(tab: Tab, activeTab: Tab, problemsCount: Int): View<Intent> = div(
        attributes {
            className("group-selector")
            when {
                problemsCount == 0 -> className("group-selector--disabled")
                tab == activeTab -> className("group-selector--active")
                else -> onClick { Intent.SetTab(tab) }
            }
        },
        span(
            tab.text,
            countBalloon(problemsCount)
        )
    )

    private
    fun countBalloon(count: Int): View<Intent> = span(
        attributes { className("group-selector__count") },
        invisibleSpace,
        invisibleOpenParen,
        span("$count"),
        invisibleCloseParen
    )

    private
    fun learnMore(learnMore: LearnMore): View<Intent> = div(
        attributes { className("learn-more") },
        span("Learn more about the "),
        a(
            attributes { href(learnMore.documentationLink) },
            learnMore.text
        ),
        span(".")
    )

    private
    fun viewTree(model: ProblemTreeModel, treeIntent: (ProblemTreeIntent) -> TreeIntent): View<BaseIntent> =
        viewTree(model.tree.focus().children, treeIntent)

    private
    fun viewTree(
        subTrees: Sequence<Tree.Focus<ProblemNode>>,
        treeIntent: (ProblemTreeIntent) -> TreeIntent
    ): View<BaseIntent> = div(
        ol(
            viewSubTrees(subTrees) { focus ->
                when (val labelNode = focus.tree.label) {
                    is ProblemCCNode.Error -> {
                        treeLabel(
                            treeIntent,
                            focus,
                            labelNode.label,
                            labelNode.docLink,
                            prefix = errorIcon
                        )
                    }

                    is ProblemCCNode.Warning -> {
                        treeLabel(
                            treeIntent,
                            focus,
                            labelNode.label,
                            labelNode.docLink,
                            prefix = warningIcon
                        )
                    }

                    is ProblemCCNode.Info -> {
                        treeLabel(
                            treeIntent,
                            focus,
                            labelNode.label,
                            labelNode.docLink,
                            suffix = countBalloon(focus.tree.children.size)
                        )
                    }

                    is ProblemNode.Exception -> {
                        viewException(treeIntent, focus, labelNode)
                    }

                    else -> {
                        treeLabel(treeIntent, focus, labelNode)
                    }
                }
            }
        )
    )

    private
    fun viewNode(node: ProblemNode): View<BaseIntent> = when (node) {
        is ProblemCCNode.Project -> viewPrettyText {
            text("project ")
            ref(node.path)
        }

        is ProblemCCNode.Property -> viewPrettyText {
            text("${node.kind} ")
            ref(node.name)
            text(" of ")
            ref(node.owner)
        }

        is ProblemCCNode.SystemProperty -> viewPrettyText {
            text("system property ")
            ref(node.name)
        }

        is ProblemCCNode.Task -> viewPrettyText {
            text("task ")
            ref(node.path)
            text(" of type ")
            ref(node.type)
        }

        is ProblemCCNode.Bean -> viewPrettyText {
            text("bean of type ")
            ref(node.type)
        }

        is ProblemCCNode.BuildLogic -> viewPrettyText {
            text(node.location)
        }

        is ProblemCCNode.BuildLogicClass -> viewPrettyText {
            text("class ")
            ref(node.type)
        }

        is ProblemCCNode.Label -> viewPrettyText {
            text(node.text)
        }

        is ProblemCCNode.Message -> viewPrettyText(node.prettyText)

        is ProblemCCNode.Link -> a(
            attributes {
                className("documentation-button")
                href(node.href)
            },
            node.label
        )

        else -> span(
            node.toString()
        )
    }

    private
    fun treeLabel(
        treeIntent: (ProblemTreeIntent) -> BaseIntent,
        focus: Tree.Focus<ProblemNode>,
        label: ProblemNode,
        docLink: ProblemNode? = null,
        prefix: View<BaseIntent> = empty,
        suffix: View<BaseIntent> = empty
    ): View<BaseIntent> = div(
        treeButtonFor(focus, treeIntent),
        prefix,
        viewNode(label),
        docLink?.let(ConfigurationCacheReportPage::viewNode) ?: empty,
        suffix
    )

    private
    val errorIcon = span<Intent>(
        attributes { classNames("invisible-text", "error-icon") },
        "[error] "
    )

    private
    val warningIcon = span<Intent>(
        attributes { classNames("invisible-text", "warning-icon") },
        "[warn]  " // two spaces to align with [error] prefix
    )
}
