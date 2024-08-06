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

package configurationCache

import components.ProblemNode
import data.LearnMore
import data.PrettyText
import data.Trie
import data.found
import data.itsOrTheir
import data.wasOrWere
import elmish.tree.Tree
import elmish.tree.TreeView


private
data class ImportedProblem(
    val problem: JsDiagnostic,
    val message: PrettyText,
    val trace: List<ProblemNode>
)


fun reportPageModelFromJsModel(jsModel: JsModel): ConfigurationCacheReportPage.Model {
    val diagnostics = importDiagnostics(jsModel.diagnostics)
    val totalProblems = jsModel.totalProblemCount
    return ConfigurationCacheReportPage.Model(
        heading = headingPrettyText(jsModel),
        summary = summaryPrettyText(jsModel, diagnostics),
        learnMore = LearnMore("Gradle Configuration Cache", jsModel.documentationLink),
        messageTree = treeModelFor(
            ProblemNode.Label(ConfigurationCacheReportPage.Tab.ByMessage.text),
            problemNodesByMessage(diagnostics.problems)
        ),
        locationTree = treeModelFor(
            ProblemNode.Label(ConfigurationCacheReportPage.Tab.ByLocation.text),
            problemNodesByLocation(diagnostics.problems)
        ),
        inputTree = treeModelFor(
            ProblemNode.Label(ConfigurationCacheReportPage.Tab.Inputs.text),
            inputNodes(diagnostics.inputs)
        ),
        incompatibleTaskTree = treeModelFor(
            ProblemNode.Label(ConfigurationCacheReportPage.Tab.IncompatibleTasks.text),
            incompatibleTaskNodes(diagnostics.incompatibleTasks)
        ),
        tab = if (totalProblems == 0) ConfigurationCacheReportPage.Tab.Inputs else ConfigurationCacheReportPage.Tab.ByMessage
    )
}


fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }


private
fun headingPrettyText(model: JsModel): PrettyText {
    val buildName = model.buildName
    val requestedTasks = model.requestedTasks
    val manyTasks = requestedTasks?.contains(" ") ?: true
    return PrettyText.build {
        text("${model.cacheAction.capitalize()} the configuration cache for ")
        buildName?.let { ref(it) }
        buildName?.let { text(" build and ") }
        requestedTasks?.let { ref(it) } ?: text("default")
        text(if (manyTasks) " tasks" else " task")
    }
}


private
fun summaryPrettyText(jsModel: JsModel, diagnostics: ImportedDiagnostics): List<PrettyText> {
    val cacheActionDescription = jsModel.cacheActionDescription?.let(::toPrettyText)
    val inputsSummary = PrettyText.ofText(inputsSummary(diagnostics))
    val problemsSummary = PrettyText.ofText(problemsSummary(jsModel, diagnostics))

    return listOfNotNull(
        cacheActionDescription,
        inputsSummary,
        problemsSummary,
    )
}


private
fun inputsSummary(diagnostics: ImportedDiagnostics): String {
    val reportedInputs = diagnostics.inputs.size
    return found(reportedInputs, "build configuration input").let {
        if (reportedInputs > 0) "$it and will cause the cache to be discarded when ${itsOrTheir(reportedInputs)} value change"
        else it
    }
}


private
fun problemsSummary(jsModel: JsModel, diagnostics: ImportedDiagnostics): String {
    val totalProblems = jsModel.totalProblemCount
    val reportedProblems = diagnostics.problems.size
    return found(totalProblems, "problem").let {
        if (totalProblems > reportedProblems) "$it, only the first $reportedProblems ${wasOrWere(reportedProblems)} included in this report"
        else it
    }
}


private
class ImportedDiagnostics(
    val problems: List<ImportedProblem>,
    val inputs: List<ImportedProblem>,
    val incompatibleTasks: List<ImportedProblem>
)


private
fun importDiagnostics(jsDiagnostics: Array<JsDiagnostic>): ImportedDiagnostics {
    val importedProblems = mutableListOf<ImportedProblem>()
    val importedInputs = mutableListOf<ImportedProblem>()
    val incompatibleTasks = mutableListOf<ImportedProblem>()
    for (diagnostic in jsDiagnostics) {
        diagnostic.input?.let {
            importedInputs.add(toImportedProblem(it, diagnostic))
        } ?: diagnostic.incompatibleTask?.let {
            incompatibleTasks.add(toImportedProblem(it, diagnostic))
        } ?: diagnostic.problem!!.let {
            importedProblems.add(toImportedProblem(it, diagnostic))
        }
    }
    return ImportedDiagnostics(importedProblems, importedInputs, incompatibleTasks)
}


private
fun toImportedProblem(label: Array<JsMessageFragment>, jsProblem: JsDiagnostic) = ImportedProblem(
    jsProblem,
    label.let(::toPrettyText),
    jsProblem.trace.map(::toProblemNode)
)


private
fun inputNodes(inputs: List<ImportedProblem>): Sequence<List<ProblemNode>> =
    inputs.asSequence().map { input ->
        buildList {
            val message = input.message
            val inputType = message.fragments.first().unsafeCast<PrettyText.Fragment.Text>().text.trim()
            val inputDescription = message.copy(fragments = message.fragments.drop(1))
            add(
                ProblemCCNode.Info(
                    ProblemNode.Label(inputType),
                    docLinkFor(input.problem)
                )
            )
            add(ProblemNode.Message(inputDescription))
            addAll(input.trace)
        }
    }


private
fun incompatibleTaskNodes(incompatibleTasks: List<ImportedProblem>): Sequence<List<ProblemNode>> =
    incompatibleTasks.asSequence().map { incompatibleTask ->
        buildList {
            val message = incompatibleTask.message
            val incompatibleTaskDescription = message.copy(fragments = message.fragments)
            add(
                ProblemNode.Warning(
                    ProblemNode.Message(incompatibleTaskDescription),
                    docLinkFor(incompatibleTask.problem)
                )
            )
        }
    }


private
fun problemNodesByMessage(problems: List<ImportedProblem>): Sequence<List<ProblemNode>> =
    problems.asSequence().map { problem ->
        buildList {
            add(problemNodeFor(problem))
            addAll(problem.trace)
            addErrorCauseNode(problem)
        }
    }


private
fun problemNodesByLocation(problems: List<ImportedProblem>): Sequence<List<ProblemNode>> =
    problems.asSequence().map { problem ->
        buildList {
            addAll(problem.trace.asReversed())
            add(problemNodeFor(problem))
            addErrorCauseNode(problem)
        }
    }


private
fun MutableList<ProblemNode>.addErrorCauseNode(problem: ImportedProblem) {
    errorCauseNodeFor(problem.problem)?.let {
        add(it)
    }
}


private
fun problemNodeFor(problem: ImportedProblem) = errorOrWarningNodeFor(
    problem.problem,
    messageNodeFor(problem),
    docLinkFor(problem.problem)
)


private
fun toProblemNode(trace: JsTrace): ProblemNode = when (trace.kind) {
    "Project" -> trace.unsafeCast<JsTraceProject>().run {
        ProblemCCNode.Project(path)
    }

    "Task" -> trace.unsafeCast<JsTraceTask>().run {
        ProblemCCNode.Task(path, type)
    }

    "TaskPath" -> trace.unsafeCast<JsTraceTaskPath>().run {
        ProblemCCNode.TaskPath(path)
    }

    "Bean" -> trace.unsafeCast<JsTraceBean>().run {
        ProblemCCNode.Bean(type)
    }

    "Field" -> trace.unsafeCast<JsTraceField>().run {
        ProblemCCNode.Property("field", name, declaringType)
    }

    "InputProperty" -> trace.unsafeCast<JsTraceProperty>().run {
        ProblemCCNode.Property("input property", name, task)
    }

    "OutputProperty" -> trace.unsafeCast<JsTraceProperty>().run {
        ProblemCCNode.Property("output property", name, task)
    }

    "SystemProperty" -> trace.unsafeCast<JsTraceSystemProperty>().run {
        ProblemCCNode.SystemProperty(name)
    }

    "PropertyUsage" -> trace.unsafeCast<JsTracePropertyUsage>().run {
        ProblemCCNode.Property("property", name, from)
    }

    "BuildLogic" -> trace.unsafeCast<JSBuildLogic>().run {
        ProblemCCNode.BuildLogic(location)
    }

    "BuildLogicClass" -> trace.unsafeCast<JSBuildLogicClass>().run {
        ProblemCCNode.BuildLogicClass(type)
    }

    else -> ProblemNode.Label("Gradle runtime")
}


private
fun errorOrWarningNodeFor(problem: JsDiagnostic, label: ProblemNode, docLink: ProblemNode?): ProblemNode =
    problem.error?.let {
        ProblemNode.Error(label, docLink)
    } ?: ProblemNode.Warning(label, docLink)


private
fun messageNodeFor(importedProblem: ImportedProblem) =
    ProblemNode.Message(importedProblem.message)


private
fun errorCauseNodeFor(diagnostic: JsDiagnostic): ProblemNode? {
    val error = diagnostic.error ?: return null
    return problemNodeForError(error)
}


fun problemNodeForError(error: JsError): ProblemNode? {
    val parts = error.parts
    if (parts == null) {
        val summary = error.summary ?: return null
        return ProblemNode.Message(toPrettyText(summary))
    }

    return ProblemNode.Exception(
        summary = error.summary?.let { toPrettyText(it) },
        fullText = parts.mapNotNull { it.textContent }.joinToString("\n"),
        parts = parts.mapNotNull { part ->
            stackTracePartFor(part)
        }
    )
}


private
fun stackTracePartFor(part: JsStackTracePart): ProblemNode.StackTracePart? {
    val partText = part.textContent ?: return null
    val lines = partText.lineSequence().filter { it.isNotEmpty() }.toList()
    val isInternal = part.internalText != null
    return ProblemNode.StackTracePart(lines, defaultViewStateFor(isInternal, lines.size))
}


private
val JsStackTracePart.textContent: String?
    get() = text ?: internalText


private
fun defaultViewStateFor(isInternal: Boolean, linesCount: Int): Tree.ViewState? {
    return if (isInternal && linesCount > 1) Tree.ViewState.Collapsed else null
}


private
fun docLinkFor(jsDiagnostic: JsDiagnostic): ProblemNode? =
    jsDiagnostic.documentationLink?.let { ProblemNode.Link(it, "") }


private
fun <T> treeModelFor(
    label: T,
    sequence: Sequence<List<T>>
): TreeView.Model<T> = TreeView.Model(
    treeFromTrie(
        label,
        Trie.from(sequence),
        Tree.ViewState.Collapsed
    )
)


private
fun <T> treeFromTrie(label: T, trie: Trie<T>, state: Tree.ViewState): Tree<T> {
    val subTreeState = if (trie.size == 1) Tree.ViewState.Expanded else Tree.ViewState.Collapsed
    return Tree(
        label,
        subTreesFromTrie(trie, subTreeState),
        // nodes with no children such as Exception nodes are considered `Collapsed` by default
        if (trie.size == 0) Tree.ViewState.Collapsed else state
    )
}


private
fun <T> subTreesFromTrie(trie: Trie<T>, state: Tree.ViewState): List<Tree<T>> =
    trie.entries.sortedBy { (label, _) -> label.toString() }.map { (label, subTrie) ->
        treeFromTrie(
            label,
            subTrie,
            state
        )
    }.toList()
