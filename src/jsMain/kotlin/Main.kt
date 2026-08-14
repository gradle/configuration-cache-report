/*
 * Copyright 2019 the original author or authors.
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

import configurationCache.ConfigurationCacheReportPage
import configurationCache.reportPageModelFromJsModel
import elmish.elementById
import elmish.findElementById
import elmish.mountComponentAt
import org.gradle.problems.internal.report.model.DIAGNOSTICS_ELEMENT_ID
import org.gradle.problems.internal.report.model.JsConfigurationCacheSummary
import org.gradle.problems.internal.report.model.JsProblemsSummary
import org.gradle.problems.internal.report.model.parseCcDiagnostics
import org.gradle.problems.internal.report.model.parseCcSummary
import org.gradle.problems.internal.report.model.parseProblems
import org.gradle.problems.internal.report.model.parseProblemsSummary
import org.w3c.dom.Element
import problemReport.ProblemsReportPage
import problemReport.problemsReportPageModelFromJsModel


fun main() {
    // The report data reaches the page as the text of `<script type="application/json">` elements,
    // so we hand each piece straight to kotlinx.serialization. Decoding a dynamic object with
    // `decodeFromDynamic` instead would pull in noticeably more code and inflate the report bundle,
    // and materializing the object at all is what we want to avoid for reports that can reach
    // several megabytes.
    //
    // The report carries exactly one summary, and which element it is in tells the two kinds of
    // report apart. The reported items are always under `diagnostics`, because the producer streams
    // them into the html report file as they arrive, before it knows anything else about the report.
    val diagnostics = jsonIn(elementById(DIAGNOSTICS_ELEMENT_ID))
    val problemsSummary = findElementById(JsProblemsSummary.ELEMENT_ID)
    if (problemsSummary == null) {
        mountComponentAt(
            elementById("report"),
            ConfigurationCacheReportPage,
            reportPageModelFromJsModel(
                parseCcSummary(jsonIn(elementById(JsConfigurationCacheSummary.ELEMENT_ID))),
                parseCcDiagnostics(diagnostics)
            )
        )
    } else {
        mountComponentAt(
            elementById("report"),
            ProblemsReportPage,
            problemsReportPageModelFromJsModel(
                parseProblemsSummary(jsonIn(problemsSummary)),
                parseProblems(diagnostics)
            )
        )
    }
}


/**
 * Returns the JSON text an `application/json` script element carries.
 */
private
fun jsonIn(element: Element): String = element.textContent.orEmpty()
