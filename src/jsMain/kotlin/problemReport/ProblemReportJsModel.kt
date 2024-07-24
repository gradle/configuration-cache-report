package problemReport

import configurationCache.JsError
import configurationCache.JsMessageFragment


external interface JsProblem {
    val problem: Array<JsMessageFragment>?
    val documentationLink: String?
    val error: JsError?
    val problemDetails: Array<JsMessageFragment>?
    val solutions: Array<Array<JsMessageFragment>>?
    val additionalData: Map<String, String>?
}


external interface ProblemReportJsModel {
    val buildName: String?
    val requestedTasks: String?
    val description: Array<JsMessageFragment>?
    val documentationLink: String
    val totalProblemCount: Int
    val diagnostics: Array<JsProblem>
}
