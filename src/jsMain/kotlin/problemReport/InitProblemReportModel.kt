@file:Suppress("UNUSED_PARAMETER")

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
//        summary = listOf(PrettyText.ofText("§")), //problemReportJsModel.cacheActionDescription.let { listOf(toPrettyText(it)) },
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
    val rootNodes = problems.filterNot { it.problem == null }.map { problem ->
        var firstCategoryNode: Tree<ProblemNode>? = null
        val categories = problem.category?.copyOf()
        categories?.reverse()
        val leafCategoryNodePair =
            categories?.fold(null as Pair<Tree<ProblemNode>, MutableList<Tree<ProblemNode>>>?) { previousCategoryNodePair, cat ->
                val categoryText = "${cat.displayName} (${cat.name})"
                val categoryNodePair = categoryToTreeMap.getOrPut(categoryText) {
                    val children: MutableList<Tree<ProblemNode>> = mutableListOf()
                    Pair(Tree(ProblemApiNode.Category(PrettyText.build {
                        text(cat.displayName)
                        ref(cat.name)
                    }), children), children)
                }
                if (firstCategoryNode == null) {
                    firstCategoryNode = categoryNodePair.first
                }
                previousCategoryNodePair?.second?.let {
                    if (it.contains(categoryNodePair.first).not()) {
                        it.add(categoryNodePair.first)
                    }
                }
                categoryNodePair
            }
        if (firstCategoryNode == null) {
            uncategorizedProblems.add(createMessageTreeElement(problem))
            uncategorizedNode
        } else {
            leafCategoryNodePair?.second?.add(createMessageTreeElement(problem))
            firstCategoryNode!!
        }
    }.distinct().toList()

    return ProblemTreeModel(Tree(ProblemApiNode.Text("text"), rootNodes))
}


private
fun description(problemReportJsModel: ProblemReportJsModel) =
    problemReportJsModel.description?.let { listOf(toPrettyText(it)) } ?: listOf()


fun createMessageTree(problems: Array<JsProblem>): ProblemTreeModel {
    val problemList = problems.filterNot { it.problem == null }
        .map { jsProblem ->
            createMessageTreeElement(jsProblem)
        }.toList()
    return ProblemTreeModel(
        Tree(ProblemApiNode.Text("text"), problemList)
    )
}


private
fun createMessageTreeElement(jsProblem: JsProblem): Tree<ProblemNode> {
    val t = toPrettyText(jsProblem.problem!!)
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
        }.toList()

        children.add(
            Tree(ProblemApiNode.Label(PrettyText.ofText("Solutions")), solutions)
        )
    }
    jsProblem.error?.let {
        problemNodeForError(it)?.let { errorNode ->
            children.add(
                Tree(errorNode)
            )
        }
    }

    jsProblem.category?.let { category ->
        val tn: Tree<ProblemNode>? = category.fold(null as Tree<ProblemNode>?) { previousCategoryNode, cat ->
            Tree(
                ProblemApiNode.Category(
                    PrettyText.ofText("Group: ${cat.displayName} (${cat.name})")
                ),
                if (previousCategoryNode == null) emptyList() else listOf(previousCategoryNode)
            )
        }
        tn?.let { children.add(it) }
    }
    return children.toList()
}
