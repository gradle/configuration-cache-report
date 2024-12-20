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

import configurationCache.JsError
import configurationCache.JsMessageFragment


external interface JsProblemIdElement {
    val name: String
    val displayName: String
}


external interface JsLocation {
    val path: String?
    val line: Int?
    val column: Int?
    val length: Int?
    val pluginId: String?
    val taskPath: String?
}


external interface JsProblem {
    val problemId: Array<JsProblemIdElement>
    val documentationLink: String?
    val severity: String
    val error: JsError?
    val problemDetails: Array<JsMessageFragment>?
    val contextualLabel: String?
    val solutions: Array<Array<JsMessageFragment>>?
    val locations: Array<JsLocation>?
    val additionalData: Map<String, String>?
}


external interface JsProblemSummary {
    val problemId: Array<JsProblemIdElement>
    val count: Int
}


external interface ProblemReportJsModel {
    val buildName: String?
    val requestedTasks: String?
    val description: Array<JsMessageFragment>?
    val documentationLink: String
    val summaries: Array<JsProblemSummary>
}
