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
import data.LearnMore
import data.PrettyText
import elmish.tree.Tree
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
        fileLocationTree = createLocationTree(problems, LocationType.FILE),
        pluginLocationTree = createLocationTree(problems, LocationType.PLUGIN),
        taskLocationTree = createLocationTree(problems, LocationType.TASK),
        problemCount = problems.size,
        tab = Tab.ByMessage
    )


internal
enum class Tab(val text: String) {
    ByMessage("Messages"),
    ByGroup("Groups"),
    ByFileLocation("File Locations"),
    ByPluginLocation("Plugin Locations"),
    ByTaskLocation("Task Locations"),
}


//region SUMMARY


private
fun createSummary(problemReportJsModel: ProblemReportJsModel, problems: Array<JsProblem>) =
    buildList {
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
            children = problems.groupBy { it.problemId.messageTreeGroupingString }
                .entries.sortedBy { it.value.first().displayName }.map { it.value }
                .map { problemsForMessage ->
                    problemsForMessage.first().let { problemTemplate ->
                        Tree(
                            label = createPrimaryMessageNode(
                                problem = problemTemplate,
                                label = ProblemNode.Message(buildProblemPrettyText(problemTemplate.displayName)),
                                count = problemsForMessage.size
                            ),
                            children = problemsForMessage
                                .sortedBy { it.contextualLabel ?: it.displayName }
                                .map { prob ->
                                    createMessageTreeElement(prob, useContextualAsPrimary = true)
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
    groupIds.mapNotNull { id -> id.firstOrNull() }
        .distinct()
        .sortedBy { it.displayName }
        .map { groupIdSegment ->
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
        ?.sortedBy { it.displayName }
        ?.map { problem -> createMessageTreeElement(problem, useContextualAsPrimary = true) }
        ?: emptyList()

//endregion


//region LOCATION_TREE


private
enum class LocationType(
    val displayName: String,
    val propertySelector: (JsLocation) -> String?,
    val messageNodeLocation: (JsLocation) -> JsLocation?,
) {
    FILE(
        displayName = "File",
        propertySelector = { it.path },
        messageNodeLocation = { it },
    ),
    PLUGIN(
        displayName = "Plugin",
        propertySelector = { it.pluginId },
        messageNodeLocation = { null },
    ),
    TASK(
        displayName = "Task",
        propertySelector = { it.taskPath },
        messageNodeLocation = { null },
    );
}


private
fun createLocationTree(
    problems: Array<JsProblem>,
    type: LocationType
): ProblemTreeModel {

    val problemsByLocation: Map<String, MutableList<Pair<JsProblem, JsLocation>>> = buildMap {
        problems.forEach { problem ->
            problem.locations
                ?.mapNotNull { location -> type.propertySelector(location)?.let { it to location } }
                ?.forEach { (key, location) ->
                    getOrPut(key) { mutableListOf() }.add(problem to location)
                }
        }
    }

    return ProblemTreeModel(
        Tree(
            label = ProblemApiNode.Text("locations tree root"),
            children = problemsByLocation.keys.sorted().map { locationRef ->
                Tree(
                    label = ProblemIdNode(PrettyText.build {
                        text(type.displayName)
                        ref(locationRef)
                    }),
                    children = problemsByLocation[locationRef]
                        ?.let { problems -> createLocationTreeProblemChildren(type, problems) }
                        ?: emptyList()
                )
            }
        )
    )
}


private
fun createLocationTreeProblemChildren(
    type: LocationType,
    problems: List<Pair<JsProblem, JsLocation>>,
): List<Tree<ProblemNode>> =
    problems
        .sortedBy { it.second.lineReferencePart + it.first.displayName }
        .map { (problem, location) ->
            createMessageTreeElement(
                problem = problem,
                labelLocation = type.messageNodeLocation(location),
                useContextualAsPrimary = true
            )
        }


//endregion


//region MESSAGE_ELEMENT


private
fun createMessageTreeElement(
    problem: JsProblem,
    labelLocation: JsLocation? = null,
    useContextualAsPrimary: Boolean = false
): Tree<ProblemNode> =
    Tree(
        label = createMessageTreeElementLabel(problem, labelLocation, useContextualAsPrimary),
        children = createMessageTreeElementChildren(
            problem = problem,
            locationViewState = if (labelLocation != null) Tree.ViewState.Collapsed else Tree.ViewState.Expanded,
            skipContextual = useContextualAsPrimary
        )
    )


private
fun createMessageTreeElementLabel(
    problem: JsProblem,
    location: JsLocation? = null,
    useContextualAsPrimary: Boolean = false
): ProblemNode =
    createPrimaryMessageNode(
        problem = problem,
        label = ProblemNode.Message(
            buildProblemPrettyText(problem.getPrimaryLabelText(useContextualAsPrimary), location)
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
fun createPrimaryMessageNode(problem: JsProblem, label: ProblemNode.Message, count: Int? = null): ProblemNode {
    val docLink = problem.documentationLink?.let { ProblemNode.Link(it) }
    return when (problem.severity) {
        "WARNING" -> ProblemNode.Warning(label, docLink, count)
        "ERROR" -> ProblemNode.Error(label, docLink, count)
        "ADVICE" -> ProblemApiNode.Advice(label, docLink, count)
        else -> label.also { console.error("no severity ${problem.severity}") }
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
    problem: JsProblem,
    locationViewState: Tree.ViewState,
    skipContextual: Boolean,
): List<Tree<ProblemNode>> =
    buildList {
        // to avoid duplication on the UI, if the contextual label is used in a parent tree item, we skip it here
        if (!skipContextual && problem.contextualLabel != null) {
            add(Tree(ProblemNode.Message(PrettyText.ofText(problem.contextualLabel!!))))
        }

        problem.problemDetails?.let { problemDetails ->
            add(Tree(label = ProblemApiNode.Detail(problemDetails)))
        }

        problem.error
            ?.let(::problemNodeForError)
            ?.let { errorNode -> add(Tree(errorNode)) }

        createMessageLocationsNode(problem, locationViewState)?.let { add(it) }

        createMessageSolutionsNode(problem)?.let { add(it) }
    }


private
fun createMessageLocationsNode(problem: JsProblem, locationViewState: Tree.ViewState): Tree<ProblemNode>? =
    problem.locations?.takeIf { it.isNotEmpty() }?.let { locations ->
        Tree(
            label = ProblemNode.Label("Locations"),
            children = locations.map { location ->
                Tree(ProblemNode.ListElement(location.buildLabelPrettyText()))
            },
            state = locationViewState,
        )
    }


private
fun JsLocation.buildLabelPrettyText(): PrettyText =
    PrettyText.build {
        when {
            path != null -> {
                text(LocationType.FILE.displayName)
                ref("$path$lineReferencePart")
            }

            taskPath != null -> {
                text(LocationType.TASK.displayName)
                ref(taskPath!!)
            }

            pluginId != null -> {
                text(LocationType.PLUGIN.displayName)
                ref(pluginId!!)
            }

            else -> text("<undefined>")
        }
    }


private
fun createMessageSolutionsNode(problem: JsProblem): Tree<ProblemNode>? =
    problem.solutions?.takeIf { it.isNotEmpty() }?.let { solutions ->
        Tree(
            label = ProblemNode.TreeNode(PrettyText.ofText("Solutions")),
            children = solutions.map { solution ->
                Tree(ProblemNode.ListElement(PrettyText.ofText(solution)))
            },
            state = Tree.ViewState.Expanded
        )
    }

//endregion
