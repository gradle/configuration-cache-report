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

package configurationCache

import components.ProblemNode
import components.invisibleCloseParen
import components.invisibleOpenParen
import components.invisibleSpace
import data.LearnMore
import data.PrettyText
import data.mapAt
import elmish.Component
import elmish.View
import elmish.a
import elmish.attributes
import elmish.br
import elmish.div
import elmish.h1
import elmish.ol
import elmish.small
import elmish.span
import elmish.tree.Tree
import elmish.tree.TreeView
import elmish.tree.viewSubTrees
import kotlinx.browser.window
import reporting.BaseIntent
import reporting.BaseIntent.TreeIntent
import reporting.PrettyTextNoCopy
import reporting.ProblemTreeIntent
import reporting.ProblemTreeModel
import reporting.errorIcon
import reporting.treeLabel
import reporting.updateNodeTreeAt
import reporting.viewException
import reporting.viewPrettyText
import reporting.warningIcon


sealed class ProblemCCNode : ProblemNode() {
    data class Info(val label: ProblemNode, val docLink: ProblemNode?) : ProblemNode()

    data class Project(val path: String) : ProblemNode()

    data class Task(val path: String, val type: String) : ProblemNode()

    data class TaskPath(val path: String) : ProblemNode()

    data class Bean(val type: String) : ProblemNode()

    data class SystemProperty(val name: String) : ProblemNode()

    data class Property(val kind: String, val name: String, val owner: String) : ProblemNode()

    /**
     * Unlike real [Property], the virtual property doesn't have a name in code and thus has a slightly different
     * rendering.
     * It can be used to give better user-facing names to concepts not really obvious from their implementation,
     * like up-to-date predicates of `TaskOutputs` or task actions.
     */
    data class VirtualProperty(val name: String, val owner: String) : ProblemNode()

    data class BuildLogic(val location: String) : ProblemNode()

    data class BuildLogicClass(val type: String) : ProblemNode()
}


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

    sealed class Intent : BaseIntent() {

        data class TaskTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class MessageTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class InputTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class IncompatibleTaskTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

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

        is BaseIntent.ToggleStackTracePart -> model.updateNodeAt(intent.location) {
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
            messageTree = messageTree.updateNodeTreeAt(tree, update)
        )

        is Intent.TaskTreeIntent -> copy(
            locationTree = locationTree.updateNodeTreeAt(tree, update)
        )

        is Intent.InputTreeIntent -> copy(
            inputTree = inputTree.updateNodeTreeAt(tree, update)
        )

        is Intent.IncompatibleTaskTreeIntent -> copy(
            incompatibleTaskTree = incompatibleTaskTree.updateNodeTreeAt(tree, update)
        )

        else -> {
            console.error("Unhandled tree intent: $tree")
            this
        }
    }

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
    fun displayHeading(model: Model): View<BaseIntent> = h1(PrettyTextNoCopy.view(model.heading))

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
                    is ProblemNode.Error -> {
                        treeLabel(
                            treeIntent,
                            ::viewNode,
                            focus,
                            labelNode.label,
                            labelNode.docLink,
                            prefix = errorIcon
                        )
                    }

                    is ProblemNode.Warning -> {
                        treeLabel(
                            treeIntent,
                            ::viewNode,
                            focus,
                            labelNode.label,
                            labelNode.docLink,
                            prefix = warningIcon
                        )
                    }

                    is ProblemCCNode.Info -> {
                        treeLabel(
                            treeIntent,
                            ::viewNode,
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
                        treeLabel(
                            treeIntent, ::viewNode,
                            focus, labelNode
                        )
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

        is ProblemCCNode.VirtualProperty -> viewPrettyText {
            text("${node.name} of ")
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

        is ProblemNode.Label -> viewPrettyText {
            text(node.text)
        }

        is ProblemNode.Message -> viewPrettyText(node.prettyText)

        is ProblemNode.Link -> viewDocLink(node)

        else -> span(
            node.toString()
        )
    }
}


fun viewDocLink(node: ProblemNode.Link): View<BaseIntent> = a(
    attributes {
        className("documentation-button")
        href(node.href)
    }
)
