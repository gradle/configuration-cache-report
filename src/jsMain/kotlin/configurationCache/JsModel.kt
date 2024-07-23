package configurationCache

import data.PrettyText
import kotlin.js.JSON.stringify

external interface JsModel {
    val problemsReport: Any?
    val buildName: String?
    val cacheAction: String
    val requestedTasks: String?
    val cacheActionDescription: Array<JsMessageFragment>?
    val documentationLink: String
    val totalProblemCount: Int
    val diagnostics: Array<JsDiagnostic>
}

/** A diagnostic is one of [input], [problem] or [incompatibleTask] description. */
external interface JsDiagnostic {
    val input: Array<JsMessageFragment>?
    val problem: Array<JsMessageFragment>?
    val incompatibleTask: Array<JsMessageFragment>?
    val trace: Array<JsTrace>
    val documentationLink: String?
    val error: JsError?
}

external interface JsTrace {
    val kind: String
}

external interface JsTraceProject : JsTrace {
    val path: String
}

external interface JsTraceTask : JsTrace {
    val path: String
    val type: String
}

external interface JsTraceTaskPath : JsTrace {
    val path: String
}

external interface JsTraceBean : JsTrace {
    val type: String
}

external interface JsTraceField : JsTrace {
    val name: String
    val declaringType: String
}

external interface JsTraceProperty : JsTrace {
    val name: String
    val task: String
}

external interface JsTracePropertyUsage : JsTrace {
    val name: String
    val from: String
}

external interface JsTraceSystemProperty : JsTrace {
    val name: String
}

external interface JSBuildLogic : JsTrace {
    val location: String
}

external interface JSBuildLogicClass : JsTrace {
    val type: String
}

external interface JsMessageFragment {
    val text: String?
    val name: String?
}

external interface JsError {
    val summary: Array<JsMessageFragment>?
    val parts: Array<JsStackTracePart>?
}

external interface JsStackTracePart {
    val text: String?
    val internalText: String?
}

fun toPrettyText(message: Array<JsMessageFragment>) = PrettyText(
    message.map {
            it.text?.let(PrettyText.Fragment::Text)
            ?: it.name?.let(PrettyText.Fragment::Reference)
            ?: PrettyText.Fragment.Text("Unrecognised message fragment: ${stringify(it)}")
    }
)

