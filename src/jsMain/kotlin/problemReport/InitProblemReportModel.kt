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
import configurationCache.problemNodeForError
import configurationCache.toPrettyText
import data.LearnMore
import data.PrettyText
import elmish.tree.Tree
import elmish.tree.TreeView
import problemReport.ProblemApiNode.ProblemIdNode
import reporting.ProblemTreeModel


internal
fun problemsReportPageModelFromJsModel(problemReportJsModel: ProblemReportJsModel, problems: Array<JsProblem>) =
    ProblemsReportPage.Model(
        heading = PrettyText.ofText("Problems Report"),
        summary = createSummary(problemReportJsModel, problems),
        learnMore = LearnMore("reporting problems", problemReportJsModel.documentationLink),
        messageTree = createMessageTree(problems),
        groupTree = createGroupTree(problems),
        fileLocationTree = createLocationTree(problems, createLocationAccumulator { it.path }),
        pluginLocationTree = createLocationTree(problems, createLocationAccumulator { it.pluginId }),
        taskLocationTree = createLocationTree(problems, createLocationAccumulator { it.taskPath }),
        problemCount = problems.size,
        tab = Tab.ByMessage
    )


internal
enum class Tab(val text: String) {
    ByMessage("Messages"),
    ByGroup("Group"),
    ByFileLocation("File Locations"),
    ByPluginLocation("Plugin Locations"),
    ByTaskLocation("Task Locations"),
}


//region SUMMARY


private
fun createSummary(problemReportJsModel: ProblemReportJsModel, problems: Array<JsProblem>) =
    buildList {
        if (problemReportJsModel.description?.isNotEmpty() == true) {
            add(toPrettyText(problemReportJsModel.description!!))
        } else {
            add(
                PrettyText.build {
                    text("${problems.size} problem${if (problems.size == 1) " has" else "s have"} been reported during the execution")
                    problemReportJsModel.buildName?.let { buildName ->
                        text(" of build ")
                        ref(buildName)
                    }
                    problemReportJsModel.requestedTasks?.let { requestedTasks ->
                        text(" for the following tasks:")
                        ref(requestedTasks)
                    }
                }
            )
        }
        val skippedLocations = problems.count { it.locations == null }
        val skippedProblems = problemReportJsModel.summaries.sumOf { it.count }
        if (skippedLocations > 0 || skippedProblems > 0) {
            add(PrettyText.ofText(buildString {
                if (skippedLocations > 0) {
                    append(skippedLocations)
                    append(" of the displayed problems ")
                    append(if (skippedLocations == 1) "has" else "have")
                    append(" no location captured")
                }
                if (skippedProblems > 0) {
                    if (skippedLocations > 0) {
                        append(", and ")
                    }
                    append(skippedProblems)
                    append(" more problem")
                    append(if (skippedProblems == 1) " has" else "s have")
                    append(" been skipped")
                }
            }))
        }
    }

//endregion


//region MESSAGE_TREE


private
fun createMessageTree(problems: Array<JsProblem>): ProblemTreeModel =
    ProblemTreeModel(
        Tree(
            label = ProblemApiNode.Text("message tree root"),
            children = problems.groupBy { it.problemId.messageTreeGroupingString }.values.map { jsProblems ->
                jsProblems.first().let { jsProblem ->
                    Tree(
                        label = createPrimaryMessageNode(
                            jsProblem = jsProblem,
                            label = ProblemNode.Message(buildProblemPrettyText(jsProblem.displayName)),
                            count = jsProblems.size
                        ),
                        children = jsProblems.map { prob ->
                            createMessageTreeElement(prob, null, true)
                        }
                    )
                }
            })
    )


private
val Array<JsProblemIdElement>.messageTreeGroupingString: String
    get() = joinToString(":") { it.name }


//endregion


//region GROUP_TREE


private
fun createGroupTree(problems: Array<JsProblem>): ProblemTreeModel {

    // Data class for equality
    data class JsProblemIdElementData(override val name: String, override val displayName: String) : JsProblemIdElement

    val problemsByGroupId: Map<List<JsProblemIdElement>, MutableList<JsProblem>> = buildMap {
        problems.forEach { problem ->
            val key = problem.problemId.dropLast(1).map { JsProblemIdElementData(it.name, it.displayName) }
            getOrPut(key) { mutableListOf() }.add(problem)
        }
    }

    return ProblemTreeModel(
        Tree(
            label = ProblemApiNode.Text("group tree root"),
            children = createGroupTreeChildren(
                groupIds = problemsByGroupId.keys,
                problemsByGroupId = problemsByGroupId
            )
        )
    )
}


private
fun createGroupTreeChildren(
    previousGroupId: List<JsProblemIdElement> = emptyList(),
    groupIds: Set<List<JsProblemIdElement>>,
    problemsByGroupId: Map<List<JsProblemIdElement>, List<JsProblem>>
): List<Tree<ProblemNode>> =
    groupIds.mapNotNull { id -> id.firstOrNull() }.distinct().map { groupIdSegment ->
        val currentGroupId = previousGroupId + groupIdSegment
        Tree(
            label = ProblemNode.TreeNode(PrettyText.ofText(groupIdSegment.displayName)),
            children = createGroupTreeChildren(
                previousGroupId = currentGroupId,
                groupIds = groupIds
                    .filter { id -> id.firstOrNull() == groupIdSegment }
                    .map { id -> id.drop(1) }
                    .toSet(),
                problemsByGroupId = problemsByGroupId
            ) + createGroupTreeProblemChildren(
                groupId = currentGroupId,
                problemsByGroupId = problemsByGroupId
            )
        )
    }


private
fun createGroupTreeProblemChildren(
    groupId: List<JsProblemIdElement>,
    problemsByGroupId: Map<List<JsProblemIdElement>, List<JsProblem>>
): List<Tree<ProblemNode>> =
    problemsByGroupId[groupId]
        ?.map { problem -> createMessageTreeElement(problem) }
        ?: emptyList()

//endregion


//region LOCATION_TREE


private
typealias LocationMap = MutableMap<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>


private
typealias LocationAccumulator = (JsProblem, LocationMap) -> Unit


private
fun createLocationTree(
    problems: Array<JsProblem>,
    locationAccumulator: LocationAccumulator
): TreeView.Model<ProblemNode> {
    val locationMap = mutableMapOf<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>()
    problems.filter { it.locations?.isNotEmpty() == true }.forEach { problem ->
        locationAccumulator(problem, locationMap)
    }
    return ProblemTreeModel(
        Tree(
            label = ProblemApiNode.Text("text"),
            children = locationMap.values.map { it.first }
        )
    )
}


private
fun createLocationAccumulator(propertySelector: (JsLocation) -> String?): LocationAccumulator =
    { problem, locationMap ->
        problem.locations
            ?.filter { location -> propertySelector(location) != null }
            ?.takeIf { it.isNotEmpty() }
            ?.forEach { location ->
                createLocationNode(locationMap, propertySelector(location)!!, problem, location)
            }
    }


private
fun createLocationNode(
    locationMap: LocationMap,
    location: String,
    problem: JsProblem,
    jsLocation: JsLocation
) {
    val locationNodePair = locationMap.getOrPut(location) {
        val groupChildren = mutableListOf<Tree<ProblemNode>>()
        val tree = Tree(
            ProblemIdNode(PrettyText.build {
                ref(location)
            }),
            groupChildren,
        )
        tree to groupChildren
    }
    locationNodePair.second.add(createMessageTreeElement(problem, jsLocation))
}


//endregion


//region MESSAGE_ELEMENT


private
fun createMessageTreeElement(
    jsProblem: JsProblem,
    fileLocation: JsLocation? = null,
    useContextualAsPrimary: Boolean = false
): Tree<ProblemNode> =
    Tree(
        label = createPrimaryLabelMessageNode(jsProblem, fileLocation, useContextualAsPrimary),
        children = createMessageTreeElementChildren(jsProblem, fileLocation == null, useContextualAsPrimary)
    )


private
fun createPrimaryLabelMessageNode(
    jsProblem: JsProblem,
    fileLocation: JsLocation? = null,
    useContextualAsPrimary: Boolean = false
): ProblemNode =
    createPrimaryMessageNode(
        jsProblem = jsProblem,
        label = ProblemNode.Message(
            buildProblemPrettyText(jsProblem.getPrimaryLabelText(useContextualAsPrimary), fileLocation)
        )
    )


private
fun JsProblem.getPrimaryLabelText(useContextualAsPrimary: Boolean): String =
    if (useContextualAsPrimary && contextualLabel != null) contextualLabel!!
    else displayName


private
val JsProblem.displayName: String
    get() = problemId.last().displayName


private
fun createPrimaryMessageNode(jsProblem: JsProblem, label: ProblemNode.Message, count: Int? = null): ProblemNode {
    val docLink = jsProblem.documentationLink?.let { ProblemNode.Link(it) }
    return when (jsProblem.severity) {
        "WARNING" -> ProblemNode.Warning(label, docLink, count)
        "ERROR" -> ProblemNode.Error(label, docLink, count)
        "ADVICE" -> ProblemApiNode.Advice(label, docLink, count)
        else -> label.also { console.error("no severity ${jsProblem.severity}") }
    }
}


private
fun buildProblemPrettyText(text: String, location: JsLocation? = null): PrettyText =
    PrettyText.build {
        text(text)
        if (location != null) {
            when {
                location.line != null ->
                    location.lineReferencePart.let { reference ->
                        ref("$reference${location.lengthPart}", "${location.path}$reference")
                    }

                location.taskPath != null ->
                    ref(location.taskPath!!)

                location.pluginId != null ->
                    ref(location.pluginId!!)
            }
        }
    }


private
val JsLocation.lengthPart: String
    get() =
        if (line == null || length == null) ""
        else "-$length"


private
val JsLocation.lineReferencePart: String
    get() = buildString {
        if (line != null) append(":$line")
        if (column != null) append(":$column")
    }


private
fun createMessageTreeElementChildren(
    jsProblem: JsProblem,
    addLocationNodes: Boolean,
    skipContextual: Boolean = false
): List<Tree<ProblemNode>> =
    buildList {
        // TODO this is assumes that the problemDetails only consist of one element, which is true currently, but may change.
        jsProblem.problemDetails?.get(0)?.text?.split("\n")
            ?.map {
                // TODO get rid of this special case
                if (isJavaCompilation(jsProblem)) {
                    // use non-breaking figure space instead of nbsp, because nbsp is narrower than a letter even in monospace font
                    PrettyText.build { ref(it.replace(" ", "\u2007"), "") }
                } else {
                    PrettyText.ofText(it)
                }
            }
            ?.map { Tree<ProblemNode>(ProblemNode.Message(it)) }
            ?.forEach { add(it) }

        // to avoid duplication on the UI, if the contextual label is used in a parent tree item, we skip it here
        if (!skipContextual && jsProblem.contextualLabel != null) {
            add(Tree(ProblemNode.Message(PrettyText.ofText(jsProblem.contextualLabel!!))))
        }

        createMessageSolutionsNode(jsProblem)?.let { add(it) }

        jsProblem.error
            ?.let(::problemNodeForError)
            ?.let { errorNode -> add(Tree(errorNode)) }

        if (addLocationNodes) {
            createMessageLocationsNode(jsProblem)?.let { add(it) }
        }
    }


private
fun isJavaCompilation(jsProblem: JsProblem): Boolean =
    jsProblem.problemId.any { it.name == "compilation" } && jsProblem.problemId.any { it.name == "java" }


private
fun createMessageLocationsNode(jsProblem: JsProblem): Tree<ProblemNode>? =
    jsProblem.locations?.let { locations ->
        Tree(
            label = ProblemNode.Label("Locations"),
            children = locations.map { location ->
                Tree(ProblemNode.Message(PrettyText.build {
                    text("- ")
                    ref(location.referenceString)
                }))
            }
        )
    }


private
val JsLocation.referenceString: String
    get() = when {
        path != null -> "$path$lineReferencePart"
        taskPath != null -> taskPath!!
        pluginId != null -> pluginId!!
        else -> "<undefined>"
    }


private
fun createMessageSolutionsNode(jsProblem: JsProblem): Tree<ProblemNode>? =
    jsProblem.solutions?.takeIf { it.isNotEmpty() }?.let { solutions ->
        Tree(
            label = ProblemNode.TreeNode(PrettyText.ofText("Solutions")),
            children = solutions.map { solution ->
                Tree(ProblemNode.ListElement(toPrettyText(solution)))
            }
        )
    }

//endregion
