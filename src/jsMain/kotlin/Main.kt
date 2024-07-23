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
import configurationCache.JsModel
import configurationCache.reportPageModelFromJsModel
import elmish.elementById
import elmish.mountComponentAt
import problemReport.JsProblem
import problemReport.ProblemReportJsModel
import problemReport.ProblemsReportPage
import problemReport.reportProblemsReportPageModelFromJsModel


fun main() {
    val jsModel = configurationCacheProblems()
    if(jsModel.problemsReport == null) {
        mountComponentAt(
            elementById("report"),
            ConfigurationCacheReportPage,
            reportPageModelFromJsModel(jsModel)
        )
    }
    else {
        val problemReportJsModel = jsModel.problemsReport.unsafeCast<ProblemReportJsModel>()
        mountComponentAt(
            elementById("report"),
            ProblemsReportPage,
            reportProblemsReportPageModelFromJsModel(problemReportJsModel, jsModel.diagnostics.unsafeCast<Array<JsProblem>>())
        )
    }
}

private
inline fun <reified T> Any.uncheckedCast(): T = this as T


/**
 * External model defined in `configuration-cache-report-data.js`, a file generated by `ConfigurationCacheReport`.
 */
private
external val configurationCacheProblems: () -> JsModel
