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
    val groupTree = createIdTree(problems)
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
        problem.fileLocations
            ?.forEach {
                val location = it.path
                val locationNodePair = locationMap.getOrPut(location) {
                    val groupChildren = mutableListOf<Tree<ProblemNode>>()
                    val tree = Tree(
                        ProblemId(PrettyText.build {
                            ref(it.path)
                        }),
                        groupChildren,
                        Expanded
                    )
                    tree to groupChildren
                }
                locationNodePair.second.add(createMessageTreeElement(problem, it))
            }

        if (problem.fileLocations.isNullOrEmpty()) {
            unlocatedProblems.add(createMessageTreeElement(problem))
        }
    }

    val rootNodes = getRootNodes(locationMap, unlocatedProblems)

    return ProblemTreeModel(Tree(ProblemApiNode.Text("text"), rootNodes))
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


fun createIdTree(problems: Array<JsProblem>): TreeView.Model<ProblemNode> {
    val (ungroupedProblems, ungroupedNode) = createGroupedArtifacts()

    val groupToTreeMap = mutableMapOf<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>()
    val rootNodes = problems.map { problem ->
        var firstIdNode: MutableList<Tree<ProblemNode>>? = null
        val groups = problem.problemId.copyOf().drop(1).reversed()
        val leafGroupNodePair =
            groups.foldRight(null as Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>?) { cat, previousGroupNodePair ->
                val groupText = "${cat.displayName} (${cat.name})"
                val groupNodePair = groupToTreeMap.getOrPut(groupText) {
                    val children: MutableList<Tree<ProblemNode>> = mutableListOf()
                    Pair(
                        Tree(
                            ProblemId(PrettyText.build {
                                text(cat.displayName)
                                ref(cat.name)
                            }), children,
                            Expanded
                        ), children
                    )
                }

                val currentNodeChildren = groupNodePair.second
                previousGroupNodePair?.first?.let {
                    if (currentNodeChildren.contains(it).not()) {
                        currentNodeChildren.add(it)
                    }
                }
                if (firstIdNode == null) {
                    firstIdNode = currentNodeChildren
                }

                groupNodePair
            }
        val messageTreeElement = createMessageTreeElement(problem)
        if (firstIdNode == null) {
            ungroupedProblems.add(messageTreeElement)
            ungroupedNode
        } else {
            firstIdNode!!.add(messageTreeElement)
            leafGroupNodePair!!.first
        }
    }.distinct()

    return ProblemTreeModel(Tree(ProblemApiNode.Text("text"), rootNodes))
}


private
fun createGroupedArtifacts(): Pair<MutableList<Tree<ProblemNode>>, Tree<ProblemNode>> {
    val ungroupedProblems = mutableListOf<Tree<ProblemNode>>()
    val ungroupedNode = Tree(
        ProblemId(PrettyText.ofText("Ungrouped"), true), ungroupedProblems
    )
    return ungroupedProblems to ungroupedNode
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
                createProblemPrettyText(jsProblem.problemId.first().displayName).text(" (${it.value.size})")
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
    fileLocation: JsFileLocation? = null,
    useContextualAsPrimary: Boolean = false
): Tree<ProblemNode> {
    val messageNode = createPrimaryLabelMessageNode(jsProblem, fileLocation, useContextualAsPrimary)
    val children = getMessageChildren(jsProblem, fileLocation == null, useContextualAsPrimary)

    return Tree(messageNode, children)
}


private
fun createPrimaryLabelMessageNode(
    jsProblem: JsProblem,
    fileLocation: JsFileLocation? = null,
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
        jsProblem.problemId.first().displayName


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
fun createProblemPrettyText(text: String, fileLocation: JsFileLocation? = null): PrettyText.Builder {
    val builder = PrettyText.Builder()
    builder.run {
        text(text)
        fileLocation?.let {
            if (it.line != null) {
                val reference = getLineReferencePart(it)
                ref("$reference${getLengthPart(it)}", "${it.path}$reference")
            }
        }
    }
    return builder
}


private
fun getLengthPart(jsFileLocation: JsFileLocation) =
    if (jsFileLocation.line == null || jsFileLocation.length == null) {
        ""
    } else
        "-${jsFileLocation.length}"


private
fun getLineReferencePart(location: JsFileLocation) =
    location.line?.let { _ ->
        ":${location.line}" + (location.column?.let { ":$it" } ?: "")
    } ?: ""


private
fun getMessageChildren(
    jsProblem: JsProblem,
    addLocationNodes: Boolean,
    skipContextual: Boolean = false
): List<Tree<ProblemNode>> {
    val children = jsProblem.problemDetails?.let {
        mutableListOf(Tree<ProblemNode>(ProblemNode.Message(toPrettyText(it))))
    } ?: mutableListOf()

    // to avoid duplication on the UI, if the contextual label is used in a parent tree item, we skip it here
    if (!skipContextual && jsProblem.contextualLabel != null) {
        children.add(Tree(ProblemNode.Message(PrettyText.ofText(jsProblem.contextualLabel!!))))
    }

    getSolutionsNode(jsProblem)?.let { children.add(it) }

    jsProblem.error
        ?.let(::problemNodeForError)
        ?.let { errorNode -> children.add(Tree(errorNode)) }

    children.add(Tree(ProblemNode.Message(PrettyText.build {
        text("ID: ")
        ref(jsProblem.problemId.first().name)
    })))

    createGroupNode(jsProblem)?.let { children.add(it) }

    if (addLocationNodes && !jsProblem.fileLocations.isNullOrEmpty()) {
        children.add(getLocationsNode(jsProblem))
    }
    return children
}


private
fun getLocationsNode(jsProblem: JsProblem): Tree<ProblemNode> {
    val locationNodes = jsProblem.fileLocations
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
