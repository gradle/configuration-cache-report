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


/**
 * Configuration Cache-specific [ProblemNode] subtypes. Most describe elements of the
 * CC property trace — the path from the build graph root down to the offending value.
 */
sealed class ProblemCCNode : ProblemNode() {

    /**
     * A neutral-severity node representing a configuration cache input rather than a problem,
     * e.g. a `System.getProperty` access or a file system check recorded as a configuration input.
     *
     * Shares the shape of [ProblemNode.Error]/[ProblemNode.Warning] but is informational.
     *
     * @param label the input description; in practice always a [ProblemNode.Label].
     * @param docLink a link to relevant documentation; in practice always a [ProblemNode.Link] or null.
     */
    data class Info(val label: ProblemNode, val docLink: ProblemNode?) : ProblemNode()

    /**
     * A Gradle project referenced in the property trace, e.g. `:app` or `:lib:core`.
     */
    data class Project(val path: String) : ProblemNode()

    /**
     * A Gradle task referenced in the property trace, including its implementation type,
     * e.g. task `:app:compileJava` of type `org.gradle.api.tasks.compile.JavaCompile`.
     *
     * [path] is the build-tree-qualified identity path of the task. For tasks in included builds
     * this includes the build name prefix, e.g. `:my-build:app:compileJava`.
     */
    data class Task(val path: String, val type: String) : ProblemNode()

    /**
     * A Gradle task referenced only by path, without its type. Used where the task type is not
     * relevant, e.g. for tasks that are incompatible with the configuration cache.
     */
    data class TaskPath(val path: String) : ProblemNode()

    /**
     * A Java bean being serialized as part of the configuration cache entry,
     * e.g. `org.gradle.api.internal.artifacts.configurations.DefaultConfiguration`.
     */
    data class Bean(val type: String) : ProblemNode()

    /**
     * State captured by a lambda expression or method reference that appears in the property trace.
     *
     * [subkind] is chosen per functional object by whether its JVM implementation method is a
     * compiler-synthesized lambda method (`lambda$...`) — it does not split the captured values of a
     * single lambda into two categories.
     *
     * @param implClass the class that declares the implementation method, e.g. `com.example.MyPlugin`.
     *   For a lambda this is the class enclosing the lambda; for a method reference it is the class
     *   declaring the referenced method, which may differ from where the reference appears.
     * @param methodName the enclosing/referenced method name. For a lambda this is the assumed enclosing method,
     *   derived from the generated implementation method name;
     *   for a method reference it is the referenced method name.
     * @param subkind either `"lambdaBody"` (state captured by a lambda expression) or `"boundReceiver"`
     *   (the bound receiver of a method reference).
     */
    data class CapturedArguments(val implClass: String, val methodName: String, val subkind: String) : ProblemNode()

    /**
     * A lambda appearing in the property trace chain. The lambda itself is not necessarily the
     * source of the problem — it is an intermediate step on the path to the offending property.
     *
     * @param type the raw functional interface class name (no generic arguments),
     *   e.g. `org.gradle.api.Action`.
     * @param returnType the instantiated return type, e.g. `void` for an `Action`.
     */
    data class SerializedLambda(
        val type: String,
        val returnType: String
    ) : ProblemNode()

    /**
     * A JVM system property read during configuration, e.g. `java.io.tmpdir` or `user.home`.
     * Reading system properties is tracked as a configuration input.
     */
    data class SystemProperty(val name: String) : ProblemNode()

    /**
     * A named property that is part of the property trace. Covers four JSON trace kinds,
     * each mapped by [toProblemNode] to a human-readable [kind] string:
     *
     * - `Field` → `"field"` — a Java field; [owner] is the declaring class name.
     * - `InputProperty` → `"input property"` — a task `@Input*` property; [owner] is the task path.
     * - `OutputProperty` → `"output property"` — a task `@Output*` property; [owner] is the task path.
     * - `PropertyUsage` → `"property"` — a property accessed from a project; [owner] is the project path.
     *
     * @param kind a human-readable string (not the raw JSON kind) describing the property kind.
     * @param name the property name in code, e.g. `"outputDir"`.
     * @param owner the task path, project path, or declaring class depending on [kind].
     */
    data class Property(val kind: String, val name: String, val owner: String) : ProblemNode()

    /**
     * A synthetic property that has a user-facing name but no direct counterpart in code.
     * Used to give readable labels to concepts like the up-to-date predicate of `TaskOutputs`
     * or a task action registered via `doFirst`/`doLast`.
     *
     * Unlike [Property], the virtual property doesn't have a name in code and has no
     * property-kind classification.
     */
    data class VirtualProperty(val name: String, val owner: String) : ProblemNode()

    /**
     * A location in a build script or settings file, e.g. `build.gradle.kts:42` or
     * `settings.gradle:10`. The outermost element of the trace for problems that originate
     * directly in build logic.
     */
    data class BuildLogic(val location: String) : ProblemNode()

    /**
     * A class defined in build logic (a buildSrc class or an included build plugin class),
     * e.g. `com.example.MyPlugin`. Appears in the trace when a plugin class itself is
     * the source of the problem rather than a specific script line.
     */
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

        is ProblemCCNode.CapturedArguments -> viewPrettyText {
            if (node.subkind == "boundReceiver") {
                text("bound receiver of method ")
            } else {
                text("captured state from method ")
            }
            ref(node.methodName)
            text(" of ")
            ref(node.implClass)
        }

        is ProblemCCNode.SerializedLambda -> viewPrettyText {
            text("lambda of type ")
            ref(node.type)
            text(" returning ")
            ref(node.returnType)
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
