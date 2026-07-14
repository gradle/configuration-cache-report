/*
 * Copyright 2026 the original author or authors.
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

package org.gradle.problems.internal.report.model

import data.PrettyText


/**
 * Builds a [JsModel] from the `dynamic` JSON object embedded in the report page.
 */
fun buildJsModel(json: dynamic): JsModel =
    JsModel(
        problemsReport = json.problemsReport,
        buildName = json.buildName as String?,
        cacheAction = json.cacheAction as String,
        requestedTasks = json.requestedTasks as String?,
        cacheActionDescription = buildJsMessageFragmentsOrNull(json.cacheActionDescription),
        documentationLink = json.documentationLink as String,
        totalProblemCount = json.totalProblemCount as Int,
        uniqueProblemCount = json.uniqueProblemCount as Int,
        overflownProblemCount = json.overflownProblemCount as Int,
        diagnostics = json.diagnostics.unsafeCast<Array<dynamic>>().map(::buildJsDiagnostic)
    )


fun buildJsDiagnostic(json: dynamic): JsDiagnostic =
    JsDiagnostic(
        input = buildJsMessageFragmentsOrNull(json.input),
        problem = buildJsMessageFragmentsOrNull(json.problem),
        incompatibleTask = buildJsMessageFragmentsOrNull(json.incompatibleTask),
        trace = json.trace.unsafeCast<Array<dynamic>>().map(::buildJsTrace),
        documentationLink = json.documentationLink as String?,
        error = buildJsErrorOrNull(json.error)
    )


fun buildJsTrace(json: dynamic): JsTrace =
    when (val kind = json.kind as String?) {
        "Project" -> JsTraceProject(json.path as String)
        "Task" -> JsTraceTask(json.path as String, json.type as String)
        "TaskPath" -> JsTraceTaskPath(json.path as String)
        "Bean" -> JsTraceBean(json.type as String)
        "CapturedArguments" -> JsTraceCapturedArguments(
            json.`class` as String,
            json.method as String,
            json.subkind as String
        )
        "SerializedLambda" -> JsTraceSerializedLambda(json.type as String, json.returns as String)
        "Field" -> JsTraceField(json.name as String, json.declaringType as String)
        "InputProperty" -> JsTraceInputProperty(json.name as String, json.task as String)
        "OutputProperty" -> JsTraceOutputProperty(json.name as String, json.task as String)
        "VirtualProperty" -> JsTraceVirtualProperty(json.name as String, json.owner as String)
        "PropertyUsage" -> JsTracePropertyUsage(json.name as String, json.from as String)
        "SystemProperty" -> JsTraceSystemProperty(json.name as String)
        "BuildLogic" -> JsBuildLogic(json.location as String)
        "BuildLogicClass" -> JsBuildLogicClass(json.type as String)
        else -> JsGenericTrace(kind ?: "")
    }


fun buildJsError(json: dynamic): JsError =
    JsError(
        summary = buildJsMessageFragmentsOrNull(json.summary),
        parts = buildJsStackTracePartsOrNull(json.parts)
    )


fun buildJsStackTracePart(json: dynamic): JsStackTracePart =
    JsStackTracePart(
        text = json.text as String?,
        internalText = json.internalText as String?
    )


fun buildJsMessageFragment(json: dynamic): JsMessageFragment =
    JsMessageFragment(
        text = json.text as String?,
        name = json.name as String?
    )


fun toPrettyText(message: List<JsMessageFragment>): PrettyText =
    PrettyText.build {
        message.forEach { fragment ->
            fragment.text?.let { text(it) }
            fragment.name?.let { ref(it) }
        }
    }


private
fun buildJsErrorOrNull(json: dynamic): JsError? =
    if (json == null) null
    else buildJsError(json)


private
fun buildJsStackTracePartsOrNull(json: dynamic): List<JsStackTracePart>? =
    if (json == null) null
    else json.unsafeCast<Array<dynamic>>().map(::buildJsStackTracePart)


private
fun buildJsMessageFragmentsOrNull(json: dynamic): List<JsMessageFragment>? =
    if (json == null) null
    else json.unsafeCast<Array<dynamic>>().map(::buildJsMessageFragment)
