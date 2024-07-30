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
import configurationCache.ProblemTreeModel
import configurationCache.problemNodeForError
import configurationCache.toPrettyText
import data.LearnMore
import data.PrettyText
import elmish.tree.Tree
import elmish.tree.TreeView


fun reportProblemsReportPageModelFromJsModel(
    problemReportJsModel: ProblemReportJsModel,
    problems: Array<JsProblem>
): ProblemsReportPage.Model {
    return ProblemsReportPage.Model(
        heading = PrettyText.ofText("Problems Report"),
        summary = description(problemReportJsModel),
        learnMore = LearnMore(
            text = "reporting problems",
            documentationLink = problemReportJsModel.documentationLink
        ),
        createMessageTree(problems),
        createCategoryTree(problems),
        ProblemsReportPage.Tab.ByCategory,
    )
}


fun createCategoryTree(problems: Array<JsProblem>): TreeView.Model<ProblemNode> {
    val uncategorizedProblems = mutableListOf<Tree<ProblemNode>>()
    val uncategorizedNode = Tree(
        ProblemApiNode.Category(PrettyText.ofText("Uncategorized")),
        uncategorizedProblems
    )

    val categoryToTreeMap = mutableMapOf<String, Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>>()
    val rootNodes = problems.map { problem ->
        var firstCategoryNode: MutableList<Tree<ProblemNode>>? = null
        val categories = problem.category.copyOf().drop(1).reversed()
        val leafCategoryNodePair =
            categories.foldRight(null as Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>?) { cat, previousCategoryNodePair ->
                val categoryText = "${cat.displayName} (${cat.name})"
                val categoryNodePair = categoryToTreeMap.getOrPut(categoryText) {
                    val children: MutableList<Tree<ProblemNode>> = mutableListOf()
                    Pair(Tree(ProblemApiNode.Category(PrettyText.build {
                        text(cat.displayName)
                        ref(cat.name)
                    }), children), children)
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
        if (firstCategoryNode == null) {
            uncategorizedProblems.add(createMessageTreeElement(problem))
            uncategorizedNode
        } else {
            firstCategoryNode?.add(createMessageTreeElement(problem))
            leafCategoryNodePair!!.first
        }
    }.distinct()

    return ProblemTreeModel(Tree(ProblemApiNode.Text("text"), rootNodes))
}


private
fun description(problemReportJsModel: ProblemReportJsModel) =
    problemReportJsModel.description?.let { listOf(toPrettyText(it)) } ?: listOf()


fun createMessageTree(problems: Array<JsProblem>): ProblemTreeModel {
    val problemList = problems
        .map(::createMessageTreeElement)
    return ProblemTreeModel(
        Tree(ProblemApiNode.Text("text"), problemList)
    )
}


private
fun createMessageTreeElement(jsProblem: JsProblem): Tree<ProblemNode> {
    val t = PrettyText.build {
        text(jsProblem.category[0].displayName)
        ref(jsProblem.category[0].name)
    }
    val children = getMessageChildren(jsProblem)
    return Tree(ProblemApiNode.Label(t), children)
}


private
fun getMessageChildren(jsProblem: JsProblem): List<Tree<ProblemNode>> {
    val children = jsProblem.problemDetails?.let {
        mutableListOf(Tree<ProblemNode>(ProblemApiNode.Message(toPrettyText(it))))
    } ?: mutableListOf()

    jsProblem.solutions?.let {
        val solutions = it.map { solution ->
            Tree<ProblemNode>(ProblemApiNode.Message(toPrettyText(solution)))
        }

        children.add(
            Tree(ProblemApiNode.Label(PrettyText.ofText("Solutions")), solutions)
        )
    }
    jsProblem.error
        ?.let { problemNodeForError(it) }
        ?.let { errorNode -> children.add(Tree(errorNode)) }

    jsProblem.category.copyOf().drop(1).let { category ->
        category.fold(null as Tree<ProblemNode>?) { previousCategoryNode, cat ->
            Tree(
                ProblemApiNode.Category(
                    PrettyText.build {
                        text(cat.displayName)
                        ref(cat.name)
                    }
                ),
                if (previousCategoryNode == null) listOf() else listOf(previousCategoryNode)
            )
        }
    }?.let { children.add(it) }
    return children
}