/*
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

import data.mapAt
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
import elmish.view
import kotlinx.browser.window


internal
sealed class ProblemNode {

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

    data class Exception(
        val summary: PrettyText?,
        val fullText: String,
        val parts: List<StackTracePart>
    ) : ProblemNode()

    internal
    data class StackTracePart(
        val lines: List<String>,
        val state: Tree.ViewState?
    )
}


internal
data class PrettyText(val fragments: List<Fragment>) {

    sealed class Fragment {

        data class Text(val text: String) : Fragment()

        data class Reference(val name: String) : Fragment()
    }
}


internal
typealias ProblemTreeModel = TreeView.Model<ProblemNode>


internal
typealias ProblemTreeIntent = TreeView.Intent<ProblemNode>


internal
val ProblemTreeModel.childCount: Int
    get() = tree.children.size


internal
object ConfigurationCacheReportPage : Component<ConfigurationCacheReportPage.Model, ConfigurationCacheReportPage.Intent> {

    data class Model(
        val buildName: String?,
        val cacheAction: String,
        val requestedTasks: String?,
        val documentationLink: String,
        val totalProblems: Int,
        val reportedProblems: Int,
        val messageTree: ProblemTreeModel,
        val locationTree: ProblemTreeModel,
        val reportedInputs: Int,
        val inputTree: ProblemTreeModel,
        val reportedIncompatibleTasks: Int,
        val incompatibleTaskTree: ProblemTreeModel,
        val tab: Tab = if (totalProblems == 0) Tab.Inputs else Tab.ByMessage
    )

    enum class Tab(val text: String) {
        Inputs("Build configuration inputs"),
        ByMessage("Problems grouped by message"),
        ByLocation("Problems grouped by location"),
        IncompatibleTasks("Incompatible tasks")
    }

    sealed class Intent {

        sealed class TreeIntent : Intent() {
            abstract val delegate: ProblemTreeIntent
        }

        data class TaskTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class MessageTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class InputTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class IncompatibleTaskTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class ToggleStackTracePart(val partIndex: Int, val location: TreeIntent) : Intent()

        data class Copy(val text: String) : Intent()

        data class SetTab(val tab: Tab) : Intent()
    }

    override fun step(intent: Intent, model: Model): Model = when (intent) {
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

        is Intent.Copy -> {
            window.navigator.clipboard.writeText(intent.text)
            model
        }

        is Intent.SetTab -> model.copy(
            tab = intent.tab
        )
    }

    private
    fun Model.updateNodeAt(
        tree: Intent.TreeIntent,
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
    }

    private
    fun ProblemTreeModel.updateNodeAt(
        tree: Intent.TreeIntent,
        update: ProblemNode.() -> ProblemNode
    ): TreeView.Model<ProblemNode> = updateLabelAt(
        tree.delegate.focus,
        update
    )

    override fun view(model: Model): View<Intent> = div(
        attributes { className("report-wrapper") },
        viewHeader(model),
        viewProblems(model)
    )

    private
    fun viewHeader(model: Model): View<Intent> = div(
        attributes { className("header") },
        div(attributes { className("gradle-logo") }),
        learnMore(model.documentationLink),
        div(
            attributes { className("title") },
            displaySummary(model),
        ),
        div(
            attributes { className("groups") },
            displayTabButton(Tab.Inputs, model.tab, model.reportedInputs),
            displayTabButton(Tab.ByMessage, model.tab, model.messageTree.childCount),
            displayTabButton(Tab.ByLocation, model.tab, model.locationTree.childCount),
            displayTabButton(Tab.IncompatibleTasks, model.tab, model.reportedIncompatibleTasks)
        )
    )

    private
    fun viewProblems(model: Model) = div(
        attributes { className("content") },
        when (model.tab) {
            Tab.Inputs -> viewInputs(model.inputTree)
            Tab.IncompatibleTasks -> viewIncompatibleTasks(model.incompatibleTaskTree)
            Tab.ByMessage -> viewTree(model.messageTree, Intent::MessageTreeIntent)
            Tab.ByLocation -> viewTree(model.locationTree, Intent::TaskTreeIntent)
        }
    )

    private
    fun viewInputs(inputTree: ProblemTreeModel): View<Intent> =
        div(
            attributes { className("inputs") },
            viewTree(
                inputTree.tree.focus().children,
                Intent::InputTreeIntent
            ) { _, focus ->
                countBalloon(focus.tree.children.size)
            }
        )

    private
    fun viewIncompatibleTasks(incompatibleTaskTree: ProblemTreeModel): View<Intent> =
        div(
            attributes { className("incompatibleTasks") },
            viewTree(
                incompatibleTaskTree.tree.focus().children,
                Intent::IncompatibleTaskTreeIntent
            ) { _, focus ->
                countBalloon(focus.tree.children.size)
            }
        )

    private
    fun displaySummary(model: Model): View<Intent> {
        val buildName = model.buildName
        val requestedTasks = model.requestedTasks
        val manyTasks = requestedTasks?.contains(" ") ?: true
        return h1(
            "${model.cacheAction.capitalize()} the configuration cache for ",
            buildName.view { code(it) },
            buildName.view { span(" build and ") },
            requestedTasks?.let { code(it) } ?: span("default"),
            span(if (manyTasks) " tasks" else " task"),
            br(),
            small(model.inputsSummary()),
            br(),
            small(model.problemsSummary()),
        )
    }

    private
    fun Model.inputsSummary() =
        found(reportedInputs, "build configuration input").let {
            if (reportedInputs > 0) "$it and will cause the cache to be discarded when ${itsOrTheir(reportedInputs)} value change"
            else it
        }

    private
    fun Model.problemsSummary() =
        found(totalProblems, "problem").let {
            if (totalProblems > reportedProblems) "$it, only the first $reportedProblems ${wasOrWere(reportedProblems)} included in this report"
            else it
        }

    private
    fun found(count: Int, what: String) =
        "${count.toStringOrNo()} ${what.sIfPlural(count)} ${wasOrWere(count)} found"

    private
    fun Int.toStringOrNo() =
        if (this != 0) toString()
        else "No"

    private
    fun String.sIfPlural(count: Int) =
        if (count < 2) this else "${this}s"

    private
    fun wasOrWere(count: Int) =
        if (count <= 1) "was" else "were"

    private
    fun itsOrTheir(count: Int) =
        if (count <= 1) "its" else "their"

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
        "$count"
    )

    private
    fun learnMore(documentationLink: String): View<Intent> = div(
        attributes { className("learn-more") },
        span("Learn more about the "),
        a(
            attributes { href(documentationLink) },
            "Gradle Configuration Cache"
        ),
        span(".")
    )

    private
    fun viewTree(model: ProblemTreeModel, treeIntent: (ProblemTreeIntent) -> Intent.TreeIntent): View<Intent> =
        viewTree(model.tree.focus().children, treeIntent)

    private
    fun viewTree(
        subTrees: Sequence<Tree.Focus<ProblemNode>>,
        treeIntent: (ProblemTreeIntent) -> Intent.TreeIntent,
        suffixForInfo: (ProblemNode.Info, Tree.Focus<ProblemNode>) -> View<Intent> = { _, _ -> empty }
    ): View<Intent> = div(
        ol(
            viewSubTrees(subTrees) { focus ->
                when (val labelNode = focus.tree.label) {
                    is ProblemNode.Error -> {
                        treeLabel(
                            treeIntent,
                            focus,
                            labelNode.label,
                            labelNode.docLink,
                            prefix = errorIcon
                        )
                    }

                    is ProblemNode.Warning -> {
                        treeLabel(
                            treeIntent,
                            focus,
                            labelNode.label,
                            labelNode.docLink,
                            prefix = warningIcon
                        )
                    }

                    is ProblemNode.Info -> {
                        treeLabel(
                            treeIntent,
                            focus,
                            labelNode.label,
                            labelNode.docLink,
                            prefix = squareIcon,
                            suffix = suffixForInfo(labelNode, focus)
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
    fun viewNode(node: ProblemNode): View<Intent> = when (node) {
        is ProblemNode.Project -> span(
            span("project"),
            reference(node.path)
        )

        is ProblemNode.Property -> span(
            span(node.kind),
            reference(node.name),
            span(" of "),
            reference(node.owner)
        )

        is ProblemNode.SystemProperty -> span(
            span("system property"),
            reference(node.name),
        )

        is ProblemNode.Task -> span(
            span("task"),
            reference(node.path),
            span(" of type "),
            reference(node.type)
        )

        is ProblemNode.Bean -> span(
            span("bean of type "),
            reference(node.type)
        )

        is ProblemNode.BuildLogic -> span(
            span(node.location)
        )

        is ProblemNode.BuildLogicClass -> span(
            span("class "),
            reference(node.type)
        )

        is ProblemNode.Label -> span(
            node.text
        )

        is ProblemNode.Message -> viewPrettyText(
            node.prettyText
        )

        is ProblemNode.Link -> a(
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
        treeIntent: (ProblemTreeIntent) -> Intent,
        focus: Tree.Focus<ProblemNode>,
        label: ProblemNode,
        docLink: ProblemNode? = null,
        prefix: View<Intent> = empty,
        suffix: View<Intent> = empty
    ): View<Intent> = div(
        treeButtonFor(focus, treeIntent),
        prefix,
        viewNode(label),
        docLink?.let(::viewNode) ?: empty,
        suffix
    )

    private
    fun treeButtonFor(child: Tree.Focus<ProblemNode>, treeIntent: (ProblemTreeIntent) -> Intent): View<Intent> =
        when {
            child.tree.isNotEmpty() -> viewTreeButton(child, treeIntent)
            else -> squareIcon
        }

    private
    fun viewTreeButton(child: Tree.Focus<ProblemNode>, treeIntent: (ProblemTreeIntent) -> Intent): View<Intent> = span(
        attributes {
            className("tree-btn")
            if (child.tree.state === Tree.ViewState.Collapsed) {
                className("collapsed")
            }
            if (child.tree.state === Tree.ViewState.Expanded) {
                className("expanded")
            }
            title("Click to ${toggleVerb(child.tree.state)}")
            onClick { treeIntent(TreeView.Intent.Toggle(child)) }
        },
        when (child.tree.state) {
            Tree.ViewState.Collapsed -> "› "
            Tree.ViewState.Expanded -> "⌄ "
        }
    )

    private
    val errorIcon = span<Intent>(
        attributes { className("error-icon") },
        "⨉"
    )

    private
    val warningIcon = span<Intent>(
        attributes { className("warning-icon") },
        "⚠️"
    )

    private
    val squareIcon = span<Intent>(
        attributes { className("tree-icon") },
        "■"
    )

    private
    fun viewPrettyText(text: PrettyText): View<Intent> = span(
        text.fragments.map {
            when (it) {
                is PrettyText.Fragment.Text -> span(it.text)
                is PrettyText.Fragment.Reference -> reference(it.name)
            }
        }
    )

    private
    fun reference(name: String): View<Intent> = span(
        code(name),
        copyButton(
            text = name,
            tooltip = "Copy reference to the clipboard"
        )
    )

    private
    fun copyButton(text: String, tooltip: String): View<Intent> = small(
        attributes {
            title(tooltip)
            className("copy-button")
            onClick { Intent.Copy(text) }
        }
    )

    private
    fun viewException(
        treeIntent: (ProblemTreeIntent) -> Intent.TreeIntent,
        child: Tree.Focus<ProblemNode>,
        node: ProblemNode.Exception
    ): View<Intent> = div(
        viewTreeButton(child, treeIntent),
        span("Exception"),
        span(copyButton(text = node.fullText, tooltip = "Copy exception to the clipboard")),
        node.summary?.let { span(" ") } ?: empty,
        node.summary?.let { viewPrettyText(it) } ?: empty,
        when (child.tree.state) {
            Tree.ViewState.Collapsed -> empty
            Tree.ViewState.Expanded -> exception(node) { treeIntent(TreeView.Intent.Toggle(child)) }
        }
    )

    private
    fun exception(node: ProblemNode.Exception, owner: () -> Intent.TreeIntent): View<Intent> = div(
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

    private
    fun internalLinesToggle(
        hiddenLinesCount: Int,
        partIndex: Int,
        state: Tree.ViewState,
        location: () -> Intent.TreeIntent
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

    private
    fun toggleVerb(state: Tree.ViewState): String = when (state) {
        Tree.ViewState.Collapsed -> "expand"
        Tree.ViewState.Expanded -> "collapse"
    }

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
    fun String.capitalize() =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
