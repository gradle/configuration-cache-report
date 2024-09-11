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

package problemReport

import components.ProblemNode
import components.invisibleCloseParen
import components.invisibleOpenParen
import components.invisibleSpace
import configurationCache.childCount
import configurationCache.viewDocLink
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
import kotlinx.browser.document
import kotlinx.browser.window
import reporting.BaseIntent
import reporting.BaseIntent.TreeIntent
import reporting.PrettyTextNoCopy
import reporting.ProblemTreeIntent
import reporting.ProblemTreeModel
import reporting.adviceIcon
import reporting.enumIcon
import reporting.errorIcon
import reporting.treeButtonFor
import reporting.treeLabel
import reporting.updateNodeTreeAt
import reporting.viewException
import reporting.viewPrettyText
import reporting.warningIcon
import reporting.BaseIntent.TreeIntent as BaseIntentTreeIntent


sealed class ProblemApiNode : ProblemNode() {
    data class Text(val text: String) : ProblemApiNode()

    data class ProblemId(val prettyText: PrettyText, val separator: Boolean = false) : ProblemApiNode()

    data class Advice(val label: ProblemNode, val docLink: ProblemNode?) : ProblemNode()
}


object ProblemsReportPage :
    Component<ProblemsReportPage.Model, BaseIntent> {

    init {
        document.title = "Gradle Problem Report"
    }

    data class Model(
        val heading: PrettyText,
        val summary: List<PrettyText>,
        val learnMore: LearnMore,
        val messageTree: ProblemTreeModel,
        val problemIdTree: ProblemTreeModel,
        val fileLocationTree: ProblemTreeModel,
        val problemCount: Int,
        val tab: Tab
    )

    sealed class Intent : BaseIntent() {
        data class MessageTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class ProblemIdTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class FileLocationTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class SetTab(val tab: Tab) : Intent()
    }


    private
    fun Model.updateNodeAt(
        tree: TreeIntent,
        update: ProblemNode.() -> ProblemNode
    ) = when (tree) {
        is Intent.MessageTreeIntent -> copy(
            messageTree = messageTree.updateNodeTreeAt(tree, update)
        )

        is Intent.ProblemIdTreeIntent -> copy(
            problemIdTree = problemIdTree.updateNodeTreeAt(tree, update)
        )

        is Intent.FileLocationTreeIntent -> copy(
            fileLocationTree = fileLocationTree.updateNodeTreeAt(tree, update)
        )

        else -> {
            console.error("Unhandled tree intent: $tree")
            this
        }
    }

    override fun step(intent: BaseIntent, model: Model): Model = when (intent) {
        is Intent.FileLocationTreeIntent -> model.copy(
            fileLocationTree = TreeView.step(intent.delegate, model.fileLocationTree)
        )

        is Intent.ProblemIdTreeIntent -> model.copy(
            problemIdTree = TreeView.step(intent.delegate, model.problemIdTree)
        )

        is Intent.MessageTreeIntent -> model.copy(
            messageTree = TreeView.step(intent.delegate, model.messageTree)
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

    override fun view(model: Model): View<BaseIntent> = div(
        attributes { className("report-wrapper") },
        viewHeader(model),
        viewProblems(model)
    )

    private
    fun viewHeader(model: Model): View<BaseIntent> {
        val tabs = mutableListOf<View<BaseIntent>>()
        if (model.messageTree.childCount > 0) {
            tabs.add(displayTabButton(Tab.ByMessage, model.tab, model.problemCount))
        }
        if (model.problemIdTree.childCount > 0) {
            tabs.add(displayTabButton(Tab.ByGroup, model.tab, model.problemCount))
        }
        if (model.fileLocationTree.childCount > 0) {
            tabs.add(displayTabButton(Tab.ByFileLocation, model.tab, model.problemCount))
        }

        return div(
            attributes { className("header") },
            div(attributes { className("gradle-logo") }),
            learnMore(model.learnMore),
            div(
                attributes { className("title") },
                displaySummary(model),
            ),
            div(
                attributes { className("groups") },
                tabs
            )
        )
    }

    private
    fun viewProblems(model: Model) = div(
        attributes { className("content") },
        when (model.tab) {
            Tab.ByMessage -> viewTree(model.messageTree, Intent::MessageTreeIntent)
            Tab.ByGroup -> viewTree(model.problemIdTree, Intent::ProblemIdTreeIntent)
            Tab.ByFileLocation -> viewTree(model.fileLocationTree, Intent::FileLocationTreeIntent)
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
        span("Learn more about "),
        a(
            attributes { href(learnMore.documentationLink) },
            learnMore.text
        ),
        span(".")
    )

    private
    fun viewTree(model: ProblemTreeModel, treeIntent: (ProblemTreeIntent) -> BaseIntentTreeIntent): View<BaseIntent> =
        viewTree(model.tree.focus().children, treeIntent)

    private
    fun viewTree(
        subTrees: Sequence<Tree.Focus<ProblemNode>>,
        treeIntent: (ProblemTreeIntent) -> BaseIntentTreeIntent
    ): View<BaseIntent> = div(
        ol(
            viewSubTrees(subTrees) { focus ->
                viewNode(focus.tree.label, focus, treeIntent)
            }
        )
    )

    private
    fun viewIt(node: ProblemNode): View<BaseIntent> {
        return when (node) {
            is ProblemNode.Link -> viewDocLink(node)
            is ProblemNode.Label -> viewPrettyText(PrettyText.ofText(node.text))
            is ProblemNode.Message -> viewPrettyText(node.prettyText)
            else -> {
                val o = "Unknown node type viewIt: $node"
                console.error(o)
                span(o)
            }
        }
    }

    private
    fun viewNode(
        label: ProblemNode,
        focus: Tree.Focus<ProblemNode>,
        treeIntent: (ProblemTreeIntent) -> TreeIntent
    ): View<BaseIntent> = when (label) {
        is ProblemApiNode.Text -> viewPrettyText(PrettyText.ofText(label.text))
        is ProblemApiNode.ProblemId -> {
            div(
                attributes {
                    if (label.separator) {
                        className("uncategorized")
                    }
                },
                div(
                    treeButtonFor(focus, treeIntent),
                    viewPrettyText(label.prettyText)
                )
            )
        }

        is ProblemNode.Exception -> viewException(treeIntent, focus, label)
        is ProblemNode.Message -> {
            viewPrettyText(label.prettyText)
        }

        is ProblemNode.ListElement -> {
            div(
                enumIcon,
                viewPrettyText(label.prettyText)
            )
        }

        is ProblemNode.TreeNode -> {
            div(
                treeButtonFor(focus, treeIntent),
                viewPrettyText(label.prettyText)
            )
        }

        is ProblemNode.Error -> {
            treeLabel(
                treeIntent,
                ::viewIt,
                focus,
                label.label,
                label.docLink,
                errorIcon
            )
        }

        is ProblemApiNode.Advice -> {
            treeLabel(
                treeIntent,
                ::viewIt,
                focus,
                label.label,
                label.docLink,
                adviceIcon
            )
        }

        is ProblemNode.Warning -> {
            treeLabel(
                treeIntent,
                ::viewIt,
                focus,
                label.label,
                label.docLink,
                warningIcon
            )
        }

        is ProblemNode.Label -> {
            div(
                treeButtonFor(focus, treeIntent),
                viewPrettyText(PrettyText.ofText(label.text))
            )
        }

        else -> {
            span("Unknown node type viewNode: $label")
        }
    }
}
