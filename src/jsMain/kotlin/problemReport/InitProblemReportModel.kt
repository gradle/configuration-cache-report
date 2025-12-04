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


enum class Tab(val text: String) {
    ByMessage("Messages"),
    ByGroup("Group"),
    ByFileLocation("File Locations"),
    ByPluginLocation("Plugin Locations"),
    ByTaskLocation("Task Locations"),
}


// A data class for the purpose of being able to construct new instances with the information contained
// in JsProblemIdElement (external interfaces can not be constructed)
private
data class ProblemIdElement(
    val name: String,
    val displayName: String
)


internal
fun problemsReportPageModelFromJsModel(problemReportJsModel: ProblemReportJsModel, problems: Array<JsProblem>) =
    ProblemsReportPage.Model(
        heading = PrettyText.ofText("Problems Report"),
        summary = description(problemReportJsModel, problems),
        learnMore = LearnMore("reporting problems", problemReportJsModel.documentationLink),
        messageTree = createMessageTree(problems),
        groupTree = createGroupTree(problems),
        fileLocationTree = createLocationsTree(problems, createLocationAccumulator { it.path }),
        pluginLocationTree = createLocationsTree(problems, createLocationAccumulator { it.pluginId }),
        taskLocationTree = createLocationsTree(problems, createLocationAccumulator { it.taskPath }),
        problemCount = problems.size,
        tab = Tab.ByMessage
    )


private
typealias LocationMap = MutableMap<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>


private
typealias LocationAccumulator = (JsProblem, LocationMap) -> Unit


private
fun createLocationsTree(
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


private
data class ProblemNodeGroup(
    val tree: Tree<ProblemNode>,
    val children: MutableList<Tree<ProblemNode>>,
    val childGroups: MutableMap<String, ProblemNodeGroup> = mutableMapOf(),
)


private
fun createGroupTree(problems: Array<JsProblem>): TreeView.Model<ProblemNode> {
    val ungroupedProblems = createUngroupedProblemNodeGroup()

    val groupToTreeMap = mutableMapOf<String, ProblemNodeGroup>()

    problems.forEach { problem ->
        // drop the last entry (which is the problem specific id) to get the group part
        val groups = problem.problemId.dropLast(1).reversed().map { ProblemIdElement(it.name, it.displayName) }

        val leaf = getGroupLeafNodeToAdd(groupToTreeMap, groups)
        val messageTreeElement = createMessageTreeElement(problem)
        if (leaf == null) ungroupedProblems.children.add(messageTreeElement)
        else leaf.children.add(messageTreeElement)
    }

    val groupedRootNodes = groupToTreeMap.values.map { it.tree }
    val rootNodes =
        if (ungroupedProblems.children.isNotEmpty()) groupedRootNodes + ungroupedProblems.tree
        else groupedRootNodes

    return ProblemTreeModel(Tree(ProblemApiNode.Text("group tree root"), rootNodes))
}


private
fun getGroupLeafNodeToAdd(
    groupTree: MutableMap<String, ProblemNodeGroup>,
    group: List<ProblemIdElement>
): ProblemNodeGroup? {
    if (group.isEmpty()) return null
    var currentLeafMap = groupTree
    var currentLeaf: ProblemNodeGroup? = null
    var prevLeaf: ProblemNodeGroup?
    group.forEach {
        prevLeaf = currentLeaf
        currentLeaf = currentLeafMap.getOrPut("${it.displayName} (${it.name})") {
            val children = mutableListOf<Tree<ProblemNode>>()
            ProblemNodeGroup(
                tree = Tree(
                    label = ProblemIdNode(PrettyText.ofText(it.displayName)),
                    children = children,
                ),
                children = children
            )
        }

        if (prevLeaf != null && prevLeaf.children.contains(currentLeaf.tree).not()) {
            prevLeaf.children.add(currentLeaf.tree)
        }

        currentLeafMap = currentLeaf.childGroups
    }
    return currentLeaf
}


private
fun createUngroupedProblemNodeGroup(): ProblemNodeGroup {
    val ungroupedProblems = mutableListOf<Tree<ProblemNode>>()
    return ProblemNodeGroup(
        tree = Tree(
            label = ProblemIdNode(PrettyText.ofText("Ungrouped"), separator = true),
            children = ungroupedProblems
        ),
        children = ungroupedProblems,
    )
}


private
fun description(problemReportJsModel: ProblemReportJsModel, problems: Array<JsProblem>) =
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


private
fun createMessageTree(problems: Array<JsProblem>): ProblemTreeModel =
    ProblemTreeModel(
        Tree(
            label = ProblemApiNode.Text("message tree root"),
            children = problems.groupBy { getGroupingString(it.problemId) }.values.map { jsProblems ->
                jsProblems.first().let { jsProblem ->
                    Tree(
                        label = createPrimaryMessageNode(
                            jsProblem = jsProblem,
                            label = ProblemNode.Message(
                                createProblemPrettyText(getDisplayName(jsProblem)).build()
                            ),
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
fun getGroupingString(problemId: Array<JsProblemIdElement>): String =
    problemId.joinToString(":") { it.name }


private
fun createMessageTreeElement(
    jsProblem: JsProblem,
    fileLocation: JsLocation? = null,
    useContextualAsPrimary: Boolean = false
): Tree<ProblemNode> =
    Tree(
        label = createPrimaryLabelMessageNode(jsProblem, fileLocation, useContextualAsPrimary),
        children = getMessageChildren(jsProblem, fileLocation == null, useContextualAsPrimary)
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
            createProblemPrettyText(
                getPrimaryLabelText(useContextualAsPrimary, jsProblem),
                fileLocation
            ).build()
        )
    )


private
fun getPrimaryLabelText(useContextualAsPrimary: Boolean, jsProblem: JsProblem) =
    if (useContextualAsPrimary && jsProblem.contextualLabel != null) jsProblem.contextualLabel!!
    else getDisplayName(jsProblem)


private
fun getDisplayName(jsProblem: JsProblem) =
    jsProblem.problemId.last().displayName


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
fun createProblemPrettyText(text: String, location: JsLocation? = null): PrettyText.Builder =
    PrettyText.Builder().apply {
        text(text)
        if (location != null) {
            when {
                location.line != null ->
                    getLineReferencePart(location).let { reference ->
                        ref("$reference${getLengthPart(location)}", "${location.path}$reference")
                    }

                location.taskPath != null ->
                    ref(location.taskPath!!)

                location.pluginId != null ->
                    ref(location.pluginId!!)
            }
        }
    }


private
fun getLengthPart(jsFileLocation: JsLocation) =
    if (jsFileLocation.line == null || jsFileLocation.length == null) ""
    else "-${jsFileLocation.length}"


private
fun getLineReferencePart(location: JsLocation) =
    buildString {
        if (location.line != null) append(":${location.line}")
        if (location.column != null) append(":${location.column}")
    }


private
fun getMessageChildren(
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

        getSolutionsNode(jsProblem)?.let { add(it) }

        jsProblem.error
            ?.let(::problemNodeForError)
            ?.let { errorNode -> add(Tree(errorNode)) }

        if (addLocationNodes && !jsProblem.locations.isNullOrEmpty()) {
            add(getLocationsNode(jsProblem))
        }
    }


private
fun isJavaCompilation(jsProblem: JsProblem): Boolean =
    jsProblem.problemId.any { it.name == "compilation" } && jsProblem.problemId.any { it.name == "java" }


private
fun getLocationsNode(jsProblem: JsProblem): Tree<ProblemNode> =
    Tree(
        label = ProblemNode.Label("Locations"),
        children = jsProblem.locations
            ?.map { location ->
                Tree(ProblemNode.Message(PrettyText.build {
                    text("- ")
                    ref(getLocationReferenceString(location))
                }))
            }
            ?: emptyList()
    )


private
fun getLocationReferenceString(location: JsLocation): String =
    when {
        location.path != null -> "${location.path}${getLineReferencePart(location)}"
        location.taskPath != null -> location.taskPath!!
        location.pluginId != null -> location.pluginId!!
        else -> "<undefined>"
    }


private
fun getSolutionsNode(jsProblem: JsProblem): Tree<ProblemNode>? =
    jsProblem.solutions?.let { solutions ->
        Tree(
            label = ProblemNode.TreeNode(PrettyText.ofText("Solutions")),
            children = solutions.map { solution ->
                Tree(ProblemNode.ListElement(toPrettyText(solution)))
            }
        )
    }
