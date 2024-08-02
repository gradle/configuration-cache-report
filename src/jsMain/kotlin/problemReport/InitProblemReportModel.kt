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
import reporting.ProblemTreeModel
import configurationCache.problemNodeForError
import configurationCache.toPrettyText
import data.LearnMore
import data.PrettyText
import elmish.tree.Tree
import elmish.tree.Tree.ViewState.Expanded
import elmish.tree.TreeView
import problemReport.ProblemApiNode.Category


fun reportProblemsReportPageModelFromJsModel(
    problemReportJsModel: ProblemReportJsModel,
    problems: Array<JsProblem>
): ProblemsReportPage.Model {
    return ProblemsReportPage.Model(
        heading = PrettyText.ofText("Problems Report"),
        summary = description(problemReportJsModel, problems),
        learnMore = LearnMore(
            text = "reporting problems",
            documentationLink = problemReportJsModel.documentationLink
        ),
        createMessageTree(problems),
        createCategoryTree(problems),
        createFileLocationsTree(problems), // file locations
        ProblemsReportPage.Tab.ByFileLocation,
        problems.size
    )
}


fun createFileLocationsTree(problems: Array<JsProblem>): TreeView.Model<ProblemNode> {
    val unlocatedProblems = mutableListOf<Tree<ProblemNode>>()
    val locationMap = mutableMapOf<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>()
    problems.forEach { problem ->
        problem.fileLocations
            ?.forEach {
                val location =
                    "${it.path}"
                val locationNodePair = locationMap.getOrPut(location) {
                    val categoryChildren = mutableListOf<Tree<ProblemNode>>()
                    val tree = Tree(
                        Category(PrettyText.build {
                            ref(it.path)
                        }),
                        categoryChildren,
                        Expanded
                    )
                    tree to categoryChildren
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
        locationNodes + Tree(Category(PrettyText.ofText("no location"), true), unlocatedProblems)
}


fun createCategoryTree(problems: Array<JsProblem>): TreeView.Model<ProblemNode> {
    val (uncategorizedProblems, uncategorizedNode) = createUncategorizedArtifacts()

    val categoryToTreeMap = mutableMapOf<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>()
    val rootNodes = problems.map { problem ->
        var firstCategoryNode: MutableList<Tree<ProblemNode>>? = null
        val categories = problem.category.copyOf().drop(1).reversed()
        val leafCategoryNodePair =
            categories.foldRight(null as Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>?) { cat, previousCategoryNodePair ->
                val categoryText = "${cat.displayName} (${cat.name})"
                val categoryNodePair = categoryToTreeMap.getOrPut(categoryText) {
                    val children: MutableList<Tree<ProblemNode>> = mutableListOf()
                    Pair(
                        Tree(
                            Category(PrettyText.build {
                                text(cat.displayName)
                                ref(cat.name)
                            }), children,
                            Expanded
                        ), children
                    )
                }

                val currentNodeChildren = categoryNodePair.second
                previousCategoryNodePair?.first?.let {
                    if (currentNodeChildren.contains(it).not()) {
                        currentNodeChildren.add(it)
                    }
                }
                if (firstCategoryNode == null) {
                    firstCategoryNode = currentNodeChildren
                }

                categoryNodePair
            }
        val messageTreeElement = createMessageTreeElement(problem)
        if (firstCategoryNode == null) {
            uncategorizedProblems.add(messageTreeElement)
            uncategorizedNode
        } else {
            firstCategoryNode!!.add(messageTreeElement)
            leafCategoryNodePair!!.first
        }
    }.distinct()

    return ProblemTreeModel(Tree(ProblemApiNode.Text("text"), rootNodes))
}


private
fun createUncategorizedArtifacts(): Pair<MutableList<Tree<ProblemNode>>, Tree<ProblemNode>> {
    val uncategorizedProblems = mutableListOf<Tree<ProblemNode>>()
    val uncategorizedNode = Tree(
        Category(PrettyText.ofText("Uncategorized"), true), uncategorizedProblems
    )
    return uncategorizedProblems to uncategorizedNode
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
    val problemList = problems
        .map(::createMessageTreeElement)
    return ProblemTreeModel(
        Tree(ProblemApiNode.Text("text"), problemList)
    )
}


private
fun createMessageTreeElement(jsProblem: JsProblem, fileLocation: JsFileLocation? = null): Tree<ProblemNode> {
    val problemLabel = createProblemLabel(jsProblem, fileLocation)
    val label = ProblemNode.Message(problemLabel)
    val children = getMessageChildren(jsProblem, fileLocation == null)
    val messageNode = when (jsProblem.severity) {
        "WARNING" -> {
            ProblemNode.Warning(label, jsProblem.documentationLink?.let { ProblemNode.Link(it, "") })
        }

        "ERROR" -> {
            ProblemNode.Error(label, jsProblem.documentationLink?.let { ProblemNode.Link(it, "") })
        }

        else -> {
            console.error("no severity ${jsProblem.severity}")
            label
        }
    }

    return Tree(messageNode, children)
}


private
fun createProblemLabel(
    jsProblem: JsProblem,
    fileLocation: JsFileLocation?
) = PrettyText.build {
    text(jsProblem.category.first().displayName)
    fileLocation?.let {
        if (it.line != null) {
            val reference = getLineReferencePart(it)
            ref(reference, "${it.path}$reference")
        }
    }
}


private
fun getLineReferencePart(location: JsFileLocation) =
    location.line?.let { _ ->
        val column = location.column?.let { ":$it" } ?: ""
        val length = location.length?.let { ":$it" } ?: ""
        ":${location.line}" + column + length
    } ?: ""


private
fun getMessageChildren(jsProblem: JsProblem, addLocationNodes: Boolean): List<Tree<ProblemNode>> {
    val children = jsProblem.problemDetails?.let {
        mutableListOf(Tree<ProblemNode>(ProblemNode.Message(toPrettyText(it))))
    } ?: mutableListOf()

    getSolutionsNode(jsProblem)?.let { children.add(it) }

    jsProblem.error
        ?.let(::problemNodeForError)
        ?.let { errorNode -> children.add(Tree(errorNode)) }

    children.add(Tree(ProblemNode.Message(PrettyText.build {
        text("ID: ")
        ref(jsProblem.category.first().name)
    })))

    createCategoryNode(jsProblem)?.let { children.add(it) }

    if (addLocationNodes) {
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
fun createCategoryNode(jsProblem: JsProblem) =
    jsProblem.category.copyOf().drop(1).let { category ->
        category.fold(null as Tree<ProblemNode>?) { previousCategoryNode, cat ->
            Tree(
                Category(
                    PrettyText.build {
                        text(cat.displayName)
                        ref(cat.name)
                    }
                ),
                previousCategoryNode?.let { listOf(it) } ?: listOf()
            )
        }
    }
