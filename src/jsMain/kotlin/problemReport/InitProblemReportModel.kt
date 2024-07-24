@file:Suppress("UNUSED_PARAMETER")

package problemReport

import components.ProblemNode
import configurationCache.ProblemTreeModel
import configurationCache.problemNodeForError
import configurationCache.toPrettyText
import data.LearnMore
import data.PrettyText
import elmish.tree.Tree


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
        createTree(problems),
        ProblemsReportPage.Tab.ByMessage
    )
}


private
fun description(problemReportJsModel: ProblemReportJsModel) =
    problemReportJsModel.description?.let { listOf(toPrettyText(it)) } ?: listOf()


fun createTree(problems: Array<JsProblem>): ProblemTreeModel {
    val problemList = problems.filterNot { it.problem == null }
        .map { jsProblem ->
            val t = toPrettyText(jsProblem.problem!!)
            val children = getChildren(jsProblem)
            Tree(ProblemApiNode.Label(t), children)
        }.toList()
    return ProblemTreeModel(
        Tree(ProblemApiNode.Text("text"), problemList)
    )
}


private
fun getChildren(jsProblem: JsProblem): List<Tree<ProblemNode>> {
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
    return children.toList()
}
