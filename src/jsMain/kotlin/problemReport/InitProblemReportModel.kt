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
import configurationCache.childCount
import reporting.ProblemTreeModel
import configurationCache.problemNodeForError
import configurationCache.toPrettyText
import data.LearnMore
import data.PrettyText
import elmish.tree.Tree
import elmish.tree.Tree.ViewState.Expanded
import elmish.tree.TreeView
import problemReport.ProblemApiNode.ProblemIdNode


enum class Tab(val text: String) {
    ByMessage("Messages"),
    ByGroup("Group"),
    ByFileLocation("File Locations"),
    ByPluginLocation("Plugin Locations"),
    ByTaskLocation("Task Locations"),
}


// A data class for the purpose of being able to construct new instances with the information contained
// in JsProblemIdElement (external interfaces can not be constructed)
data class ProblemIdElement(
    val name: String,
    val displayName: String
)


// A data class for the purpose of being able to construct new instances with the information contained
// in JsProblemSummary (external interfaces can not be constructed)
data class ProblemSummary(
    val problemId: List<ProblemIdElement>,
    val count: Int
)


fun reportProblemsReportPageModelFromJsModel(
    problemReportJsModel: ProblemReportJsModel,
    problems: Array<JsProblem>
): ProblemsReportPage.Model {
    val summaries = problemReportJsModel.summaries.map { summary ->
        ProblemSummary(summary.problemId.map {
            ProblemIdElement(
                it.name,
                it.displayName
            )
        }, summary.count)
    }
    val messageTree = createMessageTree(problems, summaries)
    val groupTree = createGroupTree(problems, summaries)
    val fileLocationTree = createLocationsTree(problems, summaries.sumOf { it.count }, locationPathFilter)
    val pluginLocationTree = createLocationsTree(problems, summaries.sumOf { it.count }, locationPluginFilter)
    val taskLocationTree = createLocationsTree(problems, summaries.sumOf { it.count }, locationTaskFilter)
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
        calcDefaultTab(messageTree, groupTree, fileLocationTree, pluginLocationTree, taskLocationTree)
    )
}


fun calcDefaultTab(
    messageTree: ProblemTreeModel,
    groupTree: ProblemTreeModel,
    fileLocationTree: ProblemTreeModel,
    pluginLocationTree: ProblemTreeModel,
    taskLocationTree: ProblemTreeModel,
) =
    when {
        fileLocationTree.childCount > 0 -> Tab.ByFileLocation
        messageTree.childCount > 0 -> Tab.ByMessage
        groupTree.childCount > 0 -> Tab.ByGroup
        pluginLocationTree.childCount > 0 -> Tab.ByPluginLocation
        taskLocationTree.childCount > 0 -> Tab.ByTaskLocation
        else -> Tab.ByMessage
    }


fun createLocationsTree(
    problems: Array<JsProblem>,
    totalSkippedProblems: Int,
    locationFilter: (JsProblem, MutableMap<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>) -> Boolean
): TreeView.Model<ProblemNode> {
    val unlocatedProblems = mutableListOf<Tree<ProblemNode>>()
    val locationMap = mutableMapOf<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>()
    problems.forEach { problem ->
        if (problem.locations.isNullOrEmpty()) {
            unlocatedProblems.add(createMessageTreeElement(problem))
        } else {
            val handled = locationFilter(problem, locationMap)
            if (!handled) {
                unlocatedProblems.add(createMessageTreeElement(problem))
            }
        }
    }

    val rootNodes = getRootNodes(locationMap, unlocatedProblems, totalSkippedProblems)
    return ProblemTreeModel(Tree(ProblemApiNode.Text("text"), rootNodes))
}


private
fun createLocationFilter(
    propertySelector: (JsLocation) -> String?
): (JsProblem, MutableMap<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>) -> Boolean {
    return { problem, locationMap ->
        val filteredLocations = problem.locations?.filter { propertySelector(it) != null }
        if (filteredLocations.isNullOrEmpty()) {
            false
        } else {
            filteredLocations.forEach {
                val location = propertySelector(it)!!
                createLocationNode(locationMap, location, problem, it)
            }
            true
        }
    }
}


// Then define the specific filters using the factory function
private
val locationPathFilter = createLocationFilter { it.path }


private
val locationPluginFilter = createLocationFilter { it.pluginId }


private
val locationTaskFilter = createLocationFilter { it.taskPath }


private
fun getSkippedProblemsNode(skippedProblems: Int): Tree<ProblemNode> =
    Tree(ProblemNode.Message(PrettyText.ofText("$skippedProblems more problem${if (skippedProblems > 1) "s have" else " has"} been skipped")))


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
            Expanded
        )
        tree to groupChildren
    }
    locationNodePair.second.add(createMessageTreeElement(problem, jsLocation))
}


private
fun getRootNodes(
    locationMap: Map<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>,
    unlocatedProblems: List<Tree<ProblemNode>>,
    totalSkippedProblems: Int
): List<Tree<ProblemNode>> {
    val locationNodes = locationMap.values.map { it.first }.toMutableList()
    if (unlocatedProblems.isNotEmpty()) {
        locationNodes.add(Tree(ProblemIdNode(PrettyText.ofText("no location"), true), unlocatedProblems))
    }
    if (totalSkippedProblems > 0) {
        locationNodes.add(getSkippedProblemsNode(totalSkippedProblems))
    }
    return locationNodes
}


var globalCnt: Int = 0


data class ProblemNodeGroup(
    val tree: Tree<ProblemNode>,
    val children: MutableList<Tree<ProblemNode>> = mutableListOf(),
    val childGroups: MutableMap<String, ProblemNodeGroup> = mutableMapOf(),
    val id: Int = globalCnt++
)


fun createGroupTree(problems: Array<JsProblem>, summaries: List<ProblemSummary>): TreeView.Model<ProblemNode> {
    val ungroupedProblems = createGroupedArtifacts()

    val groupToTreeMap = mutableMapOf<String, ProblemNodeGroup>()

    problems.forEach { problem ->
        // drop the last entry (which is the problem specific id) to get the group part
        val groups = problem.problemId.dropLast(1).reversed().map { ProblemIdElement(it.name, it.displayName) }

        val leaf = getLeafNodeToAdd(groupToTreeMap, groups)
        val messageTreeElement = createMessageTreeElement(problem)
        if (leaf == null) {
            ungroupedProblems.children.add(messageTreeElement)
        } else {
            leaf.children.add(messageTreeElement)
        }
    }

    summaries
        // drop the last entry (which is the problem specific id) to get the group part
        .map { ProblemSummary(it.problemId.dropLast(1).reversed(), it.count) }
        .groupBy { it.problemId }
        .entries.map { entry ->
            ProblemSummary(entry.key, entry.value.sumOf { it.count })
        }
        .forEach {
            getLeafNodeToAdd(groupToTreeMap, it.problemId)
                ?.children
                ?.add(getSkippedProblemsNode(it.count))
        }

    val rootNodes = groupToTreeMap.values.map { it.tree } + ungroupedProblems.tree

    return ProblemTreeModel(Tree(ProblemApiNode.Text("group tree root"), rootNodes))
}


fun getLeafNodeToAdd(
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
                        ref(it.name)
                    }),
                    children,
                    Expanded
                ),
                children
            )
        }

        if (prevLeaf != null && prevLeaf!!.children.contains(currentLeaf!!.tree).not()) {
            prevLeaf!!.children.add(currentLeaf!!.tree)
        }

        currentLeafMap = currentLeaf!!.childGroups
    }
    return currentLeaf
}


private
fun createGroupedArtifacts(): ProblemNodeGroup {
    val ungroupedProblems = mutableListOf<Tree<ProblemNode>>()
    val ungroupedNode = Tree(
        ProblemIdNode(PrettyText.ofText("Ungrouped"), true), ungroupedProblems
    )
    return ProblemNodeGroup(ungroupedNode, ungroupedProblems, mutableMapOf())
}


private
fun description(problemReportJsModel: ProblemReportJsModel, problems: Array<JsProblem>) =
    problemReportJsModel.description?.let {
        listOf(toPrettyText(it))
    }
        ?: listOf(PrettyText.build {
            text("${problems.size} problems have been reported during the execution")
            problemReportJsModel.buildName?.let { buildName ->
                text(" of build ")
                ref(buildName)
            }
            problemReportJsModel.requestedTasks?.let { requestedTasks ->
                text(" for the following tasks:")
                ref(requestedTasks)
            }
        })


fun createMessageTree(problems: Array<JsProblem>, summaries: List<ProblemSummary>): ProblemTreeModel {
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

            summaries
                .find { isEqual(it.problemId, jsProblem.problemId) }
                ?.let {
                    // TODO add doc link once there is doc on summarization
                    problemsWithMessage.add(getSkippedProblemsNode(it.count))
                }

            Tree(primaryLabelMessageNode, problemsWithMessage)
        }

    return ProblemTreeModel(
        Tree(ProblemApiNode.Text("message tree root"), problemList)
    )
}


private
fun isEqual(left: List<ProblemIdElement>, right: Array<JsProblemIdElement>): Boolean {
    if (left.size == right.size) {
        return left
            .zip(right)
            .all {
                it.first.name == it.second.name &&
                    it.first.displayName == it.second.displayName
            }
    }
    return false
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
    val docLink = jsProblem.documentationLink?.let { ProblemNode.Link(it, "") }
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
