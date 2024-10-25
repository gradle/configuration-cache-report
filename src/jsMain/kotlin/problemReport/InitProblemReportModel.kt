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
import problemReport.ProblemApiNode.ProblemId


enum class Tab(val text: String) {
    ByMessage("Messages"),
    ByGroup("Group"),
    ByFileLocation("Locations"),
}


fun reportProblemsReportPageModelFromJsModel(
    problemReportJsModel: ProblemReportJsModel,
    problems: Array<JsProblem>
): ProblemsReportPage.Model {
    val messageTree = createMessageTree(problems)
    val groupTree = createGroupTree(problems)
    val fileLocationTree = createFileLocationsTree(problems)
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
        problems.size,
        calcDefaultTab(messageTree, groupTree, fileLocationTree)
    )
}


fun calcDefaultTab(
    messageTree: ProblemTreeModel,
    groupTree: ProblemTreeModel,
    fileLocationTree: ProblemTreeModel,
) =
    when {
        fileLocationTree.childCount > 0 -> Tab.ByFileLocation
        messageTree.childCount > 0 -> Tab.ByMessage
        groupTree.childCount > 0 -> Tab.ByGroup
        else -> Tab.ByMessage
    }


fun createFileLocationsTree(problems: Array<JsProblem>): TreeView.Model<ProblemNode> {
    val unlocatedProblems = mutableListOf<Tree<ProblemNode>>()
    val locationMap = mutableMapOf<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>()
    problems.forEach { problem ->
        if (problem.locations.isNullOrEmpty()) {
            unlocatedProblems.add(createMessageTreeElement(problem))
        } else {
            problem.locations?.filter { it.path != null }
                ?.forEach {
                    val location = it.path!!
                    createLocationNode(locationMap, location, problem, it)
                }

            problem.locations?.filter { it.pluginId != null }
                ?.forEach {
                    val location = it.pluginId!!
                    createLocationNode(locationMap, location, problem, it)
                }
            problem.locations?.filter { it.taskPath != null }
                ?.forEach {
                    val location = it.taskPath!!
                    createLocationNode(locationMap, location, problem, it)
                }
        }
    }

    val rootNodes = getRootNodes(locationMap, unlocatedProblems)

    return ProblemTreeModel(Tree(ProblemApiNode.Text("text"), rootNodes))
}


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
            ProblemId(PrettyText.build {
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
    unlocatedProblems: List<Tree<ProblemNode>>
): List<Tree<ProblemNode>> {
    val locationNodes = locationMap.values.map { it.first }
    return if (unlocatedProblems.isEmpty())
        locationNodes
    else
        locationNodes + Tree(ProblemId(PrettyText.ofText("no location"), true), unlocatedProblems)
}


var globalCnt: Int = 0


data class ProblemNodeGroup(
    val tree: Tree<ProblemNode>,
    val children: MutableList<Tree<ProblemNode>> = mutableListOf(),
    val childGroups: MutableMap<String, ProblemNodeGroup> = mutableMapOf(),
    val id: Int = globalCnt++
)


fun createGroupTree(problems: Array<JsProblem>): TreeView.Model<ProblemNode> {
    val ungroupedProblems = createGroupedArtifacts()

    val groupToTreeMap = mutableMapOf<String, ProblemNodeGroup>()

    problems.forEach { problem ->
        val groups = problem.problemId.copyOf().dropLast(1).reversed()

        val leaf = getLeafNodeToAdd(groupToTreeMap, groups)
        val messageTreeElement = createMessageTreeElement(problem)
        if (leaf == null) {
            ungroupedProblems.children.add(messageTreeElement)
        } else {
            leaf.children.add(messageTreeElement)
        }
    }

    val rootNodes = groupToTreeMap.values.map { it.tree }.toMutableList()
    rootNodes.add(ungroupedProblems.tree)

    return ProblemTreeModel(Tree(ProblemApiNode.Text("text"), rootNodes))
}


fun getLeafNodeToAdd(
    groupTree: MutableMap<String, ProblemNodeGroup>,
    groups: List<JsProblemIdElement>
): ProblemNodeGroup? {
    if (groups.isEmpty()) {
        return null
    }
    var currentLeafMap = groupTree
    var currentLeaf: ProblemNodeGroup? = null
    var prevLeaf: ProblemNodeGroup?
    groups.forEach {
        prevLeaf = currentLeaf
        currentLeaf = currentLeafMap.getOrPut("${it.displayName} (${it.name})") {
            val children = mutableListOf<Tree<ProblemNode>>()
            ProblemNodeGroup(
                Tree(
                    ProblemId(PrettyText.build {
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
fun getGroupNodePair(
    groupToTreeMap: MutableMap<String, ProblemNodeGroup>,
    groupText: String,
    group: JsProblemIdElement
): ProblemNodeGroup {
    return groupToTreeMap.getOrPut(groupText) {
        val children: MutableList<Tree<ProblemNode>> = mutableListOf()
        ProblemNodeGroup(
            Tree(
                ProblemId(PrettyText.build {
                    text(group.displayName)
                    ref(group.name)
                }), children,
                Expanded
            ),
            children,
            mutableMapOf()
        )
    }
}


private
fun createGroupedArtifacts(): ProblemNodeGroup {
    val ungroupedProblems = mutableListOf<Tree<ProblemNode>>()
    val ungroupedNode = Tree(
        ProblemId(PrettyText.ofText("Ungrouped"), true), ungroupedProblems
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


fun createMessageTree(problems: Array<JsProblem>): ProblemTreeModel {
    val groupMap = mutableMapOf<String, MutableList<JsProblem>>()
    problems.forEach {
        groupMap.getOrPut(getGroupingString(it)) {
            mutableListOf()
        }.add(it)
    }

    val problemList = groupMap.entries
        .map {
            val problemsWithMessage = it.value.map { prob -> createMessageTreeElement(prob, null, true) }
            val jsProblem = it.value.first()
            val problemLabel =
                createProblemPrettyText(getDisplayName(jsProblem))
                    .text(" (${it.value.size})")
                    .build()
            val label = ProblemNode.Message(problemLabel)
            val primaryLabelMessageNode = createPrimaryMessageNode(jsProblem, label)
            Tree(primaryLabelMessageNode, problemsWithMessage)
        }
    return ProblemTreeModel(
        Tree(ProblemApiNode.Text("text"), problemList)
    )
}


private
fun getGroupingString(it: JsProblem): String {
    return it.problemId.map { it.name }.joinToString(":")
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
    label: ProblemNode.Message
): ProblemNode {
    val messageNode = when (jsProblem.severity) {
        "WARNING" -> {
            ProblemNode.Warning(label, jsProblem.documentationLink?.let { ProblemNode.Link(it, "") })
        }

        "ERROR" -> {
            ProblemNode.Error(label, jsProblem.documentationLink?.let { ProblemNode.Link(it, "") })
        }

        "ADVICE" -> {
            ProblemApiNode.Advice(label, jsProblem.documentationLink?.let { ProblemNode.Link(it, "") })
        }

        else -> {
            console.error("no severity ${jsProblem.severity}")
            label
        }
    }
    return messageNode
}


private
fun createProblemPrettyText(text: String, fileLocation: JsLocation? = null): PrettyText.Builder {
    val builder = PrettyText.Builder()
    builder.run {
        text(text)
        fileLocation?.let {
            if (it.line != null) {
                val reference = getLineReferencePart(it)
                ref("$reference${getLengthPart(it)}", "${it.path}$reference")
            }
            it.taskPath?.let {
                ref(it)
            }
            it.pluginId?.let {
                ref(it)
            }
        }
    }
    return builder
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
            if (isJavaCompilation(jsProblem)) {
                // use non breaking figure space instead of nbsp, because nbsp is narrower than a letter even in monospace font
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

    createGroupNode(jsProblem)?.let { children.add(it) }

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
                ref("${location.path}${getLineReferencePart(location)}")
            }))
        }
    return Tree(ProblemNode.Label("Locations"), locationNodes ?: listOf())
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


private
fun createGroupNode(jsProblem: JsProblem) =
    jsProblem.problemId.copyOf().drop(1).let { group ->
        group.fold(null as Tree<ProblemNode>?) { previousGroupNode, cat ->
            Tree(
                ProblemId(
                    PrettyText.build {
                        text(cat.displayName)
                        ref(cat.name)
                    }
                ),
                previousGroupNode?.let { listOf(it) } ?: listOf()
            )
        }
    }
