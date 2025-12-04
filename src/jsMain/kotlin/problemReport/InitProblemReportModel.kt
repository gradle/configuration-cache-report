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
fun problemsReportPageModelFromJsModel(
    problemReportJsModel: ProblemReportJsModel,
    problems: Array<JsProblem>
): ProblemsReportPage.Model {
    val messageTree = createMessageTree(problems)
    val groupTree = createGroupTree(problems)
    val fileLocationTree = createLocationsTree(problems, fileLocationAccumulator)
    val pluginLocationTree = createLocationsTree(problems, pluginLocationAccumulator)
    val taskLocationTree = createLocationsTree(problems, taskLocationAccumulator)
    return ProblemsReportPage.Model(
        heading = PrettyText.ofText("Problems Report"),
        summary = description(problemReportJsModel, problems),
        learnMore = LearnMore(
            text = "reporting problems",
            documentationLink = problemReportJsModel.documentationLink
        ),
        messageTree,
        groupTree,
        fileLocationTree,
        pluginLocationTree,
        taskLocationTree,
        problems.size,
        Tab.ByMessage
    )
}


private
fun createLocationsTree(
    problems: Array<JsProblem>,
    locationAccumulator: (JsProblem, MutableMap<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>) -> Unit
): TreeView.Model<ProblemNode> {
    val locationMap = mutableMapOf<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>()
    problems.forEach { problem ->
        if (!problem.locations.isNullOrEmpty()) {
            locationAccumulator(problem, locationMap)
        }
    }

    val rootNodes = locationMap.values.map { it.first }
    return ProblemTreeModel(Tree(ProblemApiNode.Text("text"), rootNodes))
}


private
fun createLocationAccumulator(
    propertySelector: (JsLocation) -> String?
): (JsProblem, MutableMap<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>) -> Unit {
    return { problem, locationMap ->
        val filteredLocations = problem.locations?.filter { propertySelector(it) != null }
        if (!filteredLocations.isNullOrEmpty()) {
            filteredLocations.forEach {
                val location = propertySelector(it)!!
                createLocationNode(locationMap, location, problem, it)
            }
        }
    }
}


private
val fileLocationAccumulator = createLocationAccumulator { it.path }


private
val pluginLocationAccumulator = createLocationAccumulator { it.pluginId }


private
val taskLocationAccumulator = createLocationAccumulator { it.taskPath }


private
fun createLocationNode(
    locationMap: MutableMap<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>,
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
var globalCnt: Int = 0


private
data class ProblemNodeGroup(
    val tree: Tree<ProblemNode>,
    val children: MutableList<Tree<ProblemNode>> = mutableListOf(),
    val childGroups: MutableMap<String, ProblemNodeGroup> = mutableMapOf(),
    val id: Int = globalCnt++
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
        if (leaf == null) {
            ungroupedProblems.children.add(messageTreeElement)
        } else {
            leaf.children.add(messageTreeElement)
        }
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
    if (group.isEmpty()) {
        return null
    }
    var currentLeafMap = groupTree
    var currentLeaf: ProblemNodeGroup? = null
    var prevLeaf: ProblemNodeGroup?
    group.forEach {
        prevLeaf = currentLeaf
        currentLeaf = currentLeafMap.getOrPut("${it.displayName} (${it.name})") {
            val children = mutableListOf<Tree<ProblemNode>>()
            ProblemNodeGroup(
                Tree(
                    ProblemIdNode(PrettyText.build {
                        text(it.displayName)
                    }),
                    children,
                ),
                children
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
    val ungroupedNode = Tree(
        ProblemIdNode(PrettyText.ofText("Ungrouped"), separator = true), ungroupedProblems
    )
    return ProblemNodeGroup(ungroupedNode, ungroupedProblems, mutableMapOf())
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
            add(
                PrettyText.build {
                    text(
                        buildString {
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
                        }
                    )
                }
            )
        }
    }


private
fun createMessageTree(problems: Array<JsProblem>): ProblemTreeModel {
    val groupMap = mutableMapOf<String, MutableList<JsProblem>>()
    problems.forEach { problem ->
        groupMap.getOrPut(getGroupingString(problem.problemId)) {
            mutableListOf()
        }.add(problem)
    }

    val problemList = groupMap.entries
        .map { messageGroupEntry ->
            val problemsWithMessage =
                messageGroupEntry.value.map { prob -> createMessageTreeElement(prob, null, true) }.toMutableList()
            val jsProblem = messageGroupEntry.value.first()
            val problemLabel =
                createProblemPrettyText(getDisplayName(jsProblem))
                    .build()
            val label = ProblemNode.Message(problemLabel)
            val primaryLabelMessageNode = createPrimaryMessageNode(jsProblem, label, messageGroupEntry.value.size)

            Tree(primaryLabelMessageNode, problemsWithMessage)
        }

    return ProblemTreeModel(
        Tree(ProblemApiNode.Text("message tree root"), problemList)
    )
}


private
fun getGroupingString(problemId: Array<JsProblemIdElement>): String {
    return problemId.joinToString(":") { it.name }
}


private
fun createMessageTreeElement(
    jsProblem: JsProblem,
    fileLocation: JsLocation? = null,
    useContextualAsPrimary: Boolean = false
): Tree<ProblemNode> {
    val messageNode = createPrimaryLabelMessageNode(jsProblem, fileLocation, useContextualAsPrimary)
    val children = getMessageChildren(jsProblem, fileLocation == null, useContextualAsPrimary)

    return Tree(messageNode, children)
}


private
fun createPrimaryLabelMessageNode(
    jsProblem: JsProblem,
    fileLocation: JsLocation? = null,
    useContextualAsPrimary: Boolean = false
): ProblemNode {
    val problemLabel = createProblemPrettyText(
        getPrimaryLabelText(useContextualAsPrimary, jsProblem),
        fileLocation
    ).build()
    val label = ProblemNode.Message(problemLabel)
    return createPrimaryMessageNode(jsProblem, label)
}


private
fun getPrimaryLabelText(useContextualAsPrimary: Boolean, jsProblem: JsProblem) =
    if (useContextualAsPrimary && jsProblem.contextualLabel != null)
        jsProblem.contextualLabel!!
    else
        getDisplayName(jsProblem)


private
fun getDisplayName(jsProblem: JsProblem) = jsProblem.problemId.last().displayName


private
fun createPrimaryMessageNode(
    jsProblem: JsProblem,
    label: ProblemNode.Message,
    count: Int? = null
): ProblemNode {
    val docLink = jsProblem.documentationLink?.let { ProblemNode.Link(it) }
    val messageNode = when (jsProblem.severity) {
        "WARNING" -> {
            ProblemNode.Warning(label, docLink, count)
        }

        "ERROR" -> {
            ProblemNode.Error(label, docLink, count)
        }

        "ADVICE" -> {
            ProblemApiNode.Advice(label, docLink, count)
        }

        else -> {
            console.error("no severity ${jsProblem.severity}")
            label
        }
    }
    return messageNode
}


private
fun createProblemPrettyText(text: String, jsLocation: JsLocation? = null): PrettyText.Builder {
    return PrettyText.Builder().apply {
        text(text)

        jsLocation?.let { location ->
            when {
                location.line != null -> {
                    val reference = getLineReferencePart(location)
                    ref("$reference${getLengthPart(location)}", "${location.path}$reference")
                }

                location.taskPath != null -> {
                    ref(location.taskPath!!)
                }

                location.pluginId != null -> {
                    ref(location.pluginId!!)
                }
            }
        }
    }
}


private
fun getLengthPart(jsFileLocation: JsLocation) =
    if (jsFileLocation.line == null || jsFileLocation.length == null) {
        ""
    } else
        "-${jsFileLocation.length}"


private
fun getLineReferencePart(location: JsLocation) =
    location.line?.let { _ ->
        ":${location.line}" + (location.column?.let { ":$it" } ?: "")
    } ?: ""


private
fun getMessageChildren(
    jsProblem: JsProblem,
    addLocationNodes: Boolean,
    skipContextual: Boolean = false
): List<Tree<ProblemNode>> {
    val children: MutableList<Tree<ProblemNode>> = jsProblem.problemDetails?.let { detail ->
        // TODO this is assumes that the problemDetails only consist of one element, which is true currently, but may change.
        detail[0].text?.split("\n")?.map {
            // TODO get rid of this special case
            if (isJavaCompilation(jsProblem)) {
                // use non-breaking figure space instead of nbsp, because nbsp is narrower than a letter even in monospace font
                PrettyText.build { ref(it.replace(" ", "\u2007"), "") }
            } else {
                PrettyText.ofText(it)
            }
        }?.map<PrettyText, Tree<ProblemNode>> {
            Tree(ProblemNode.Message(it))
        }?.toMutableList() ?: mutableListOf()
    } ?: mutableListOf()

    // to avoid duplication on the UI, if the contextual label is used in a parent tree item, we skip it here
    if (!skipContextual && jsProblem.contextualLabel != null) {
        children.add(Tree(ProblemNode.Message(PrettyText.ofText(jsProblem.contextualLabel!!))))
    }

    getSolutionsNode(jsProblem)?.let { children.add(it) }

    jsProblem.error
        ?.let(::problemNodeForError)
        ?.let { errorNode -> children.add(Tree(errorNode)) }

    if (addLocationNodes && !jsProblem.locations.isNullOrEmpty()) {
        children.add(getLocationsNode(jsProblem))
    }
    return children
}


private
fun isJavaCompilation(jsProblem: JsProblem): Boolean {
    return jsProblem.problemId.find { it.name == "compilation" } != null
        && jsProblem.problemId.find { it.name == "java" } != null
}


private
fun getLocationsNode(jsProblem: JsProblem): Tree<ProblemNode> {
    val locationNodes = jsProblem.locations
        ?.map { location ->
            Tree<ProblemNode>(ProblemNode.Message(PrettyText.build {
                text("- ")
                ref(getLocationReferenceString(location))
            }))
        }
    return Tree(ProblemNode.Label("Locations"), locationNodes ?: listOf())
}


private
fun getLocationReferenceString(location: JsLocation): String = when {
    location.path != null -> "${location.path}${getLineReferencePart(location)}"
    location.taskPath != null -> location.taskPath!!
    location.pluginId != null -> location.pluginId!!
    else -> "<undefined>"
}


private
fun getSolutionsNode(
    jsProblem: JsProblem
): Tree<ProblemNode>? {
    if (jsProblem.solutions.isNullOrEmpty()) {
        return null
    }
    return Tree(
        ProblemNode.TreeNode(PrettyText.ofText("Solutions")), jsProblem.solutions!!.map { solution ->
            Tree(ProblemNode.ListElement(toPrettyText(solution)))
        })
}
