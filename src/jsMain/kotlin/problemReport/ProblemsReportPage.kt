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

import components.CopyButtonComponent
import components.PrettyTextComponent
import components.ProblemNode
import components.invisibleCloseParen
import components.invisibleOpenParen
import components.invisibleSpace
import configurationCache.BaseIntent
import configurationCache.ProblemTreeIntent
import configurationCache.ProblemTreeModel
import configurationCache.treeButtonFor
import configurationCache.viewException
import data.LearnMore
import data.PrettyText
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
import configurationCache.BaseIntent.TreeIntent as BaseIntentTreeIntent


sealed class ProblemApiNode : ProblemNode() {
    data class Text(val text: String) : ProblemApiNode()
    data class Message(val prettyText: PrettyText) : ProblemApiNode()
    data class Label(val prettyText: PrettyText) : ProblemApiNode()
    data class Category(val prettyText: PrettyText) : ProblemApiNode()
}


object ProblemsReportPage :
    Component<ProblemsReportPage.Model, BaseIntent> {

    data class Model(
        val heading: PrettyText,
        val summary: List<PrettyText>,
        val learnMore: LearnMore,
        val messageTree: ProblemTreeModel,
        val categoryTree: ProblemTreeModel,
        val tab: Tab
    )

    enum class Tab(val text: String) {
        ByMessage("Problems grouped by message"),
        ByCategory("Problems grouped by category"),
    }

    sealed class Intent : BaseIntent() {
        data class MessageTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()
        data class CategoryTreeIntent(override val delegate: ProblemTreeIntent) : TreeIntent()

        data class SetTab(val tab: Tab) : Intent()
    }

    override fun step(intent: BaseIntent, model: Model): Model = when (intent) {
        is Intent.CategoryTreeIntent -> model.copy(
            categoryTree = TreeView.step(intent.delegate, model.categoryTree)
        )
        is Intent.MessageTreeIntent -> model.copy(
            messageTree = TreeView.step(intent.delegate, model.messageTree)
        )

        is BaseIntent.Copy -> {
            window.navigator.clipboard.writeText(intent.text)
            model
        }

        is Intent.SetTab -> model.copy(
            tab = intent.tab
        )

        else -> {
            model
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
            displayTabButton(Tab.ByMessage, model.tab, model.messageTree.tree.children.size),
            displayTabButton(Tab.ByCategory, model.tab, model.categoryTree.tree.children.size),
        )
    )

    private
    fun viewProblems(model: Model) = div(
        attributes { className("content") },
        when (model.tab) {
            Tab.ByMessage -> viewTree(model.messageTree, Intent::MessageTreeIntent)
            Tab.ByCategory -> viewTree(model.categoryTree, Intent::CategoryTreeIntent)
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
    fun viewTree(model: ProblemTreeModel, treeIntent: (ProblemTreeIntent) -> BaseIntentTreeIntent): View<BaseIntent> =
        viewTree(model.tree.focus().children, treeIntent)

    private
    fun viewTree(
        subTrees: Sequence<Tree.Focus<ProblemNode>>,
        treeIntent: (ProblemTreeIntent) -> BaseIntentTreeIntent
    ): View<BaseIntent> = div(
        ol(
            viewSubTrees(subTrees) { focus ->
                when (val labelNode = focus.tree.label) {
                    is ProblemApiNode.Text -> viewPrettyText(PrettyText.ofText(labelNode.text))
                    is ProblemApiNode.Category -> {
                        div(
                            treeButtonFor(focus, treeIntent),
                            viewPrettyText(labelNode.prettyText)
                        )
                    }

                    is ProblemNode.Exception -> viewException(treeIntent, focus, labelNode)
                    is ProblemApiNode.Message -> {
                        viewPrettyText(labelNode.prettyText)
                    }

                    is ProblemApiNode.Label -> {
                        div(
                            treeButtonFor(focus, treeIntent),
                            viewPrettyText(labelNode.prettyText)
                        )
                    }

                    else -> {
                        div(
                            treeButtonFor(focus, treeIntent),
                            span("Unknown node type: $labelNode")
                        )
                    }
                }
            }
        )
    )

    private
    fun viewPrettyText(text: PrettyText): View<BaseIntent> =
        PrettyTextWithCopy.view(text)

    private
    val PrettyTextNoCopy =
        PrettyTextComponent() { BaseIntent.Copy(it) }

    private
    val PrettyTextWithCopy =
        PrettyTextComponent() { BaseIntent.Copy(it) }

    private
    val CopyButton =
        CopyButtonComponent { BaseIntent.Copy(it) }
}
