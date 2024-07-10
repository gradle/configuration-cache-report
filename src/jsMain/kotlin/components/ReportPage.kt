package components

import data.LearnMore
import data.PrettyText
import elmish.Component
import elmish.View
import elmish.a
import elmish.attributes
import elmish.div
import elmish.span


//interface ReportPageContent<Model, Intent> : Component<Model, Intent> {
//
//    fun viewHeaderContent(model: Model): View<Intent>
//
//}


internal
class ReportPage<Model, Intent>(
    private val sub: Component<Model, Intent>
) : Component<ReportPage.PageModel<Model>, Intent> {

    data class PageModel<M>(
        val header: PrettyText,
        val introParagraphs: List<PrettyText>,
        val learnMore: LearnMore,
        val model: M
    )

    override fun step(intent: Intent, model: PageModel<Model>): PageModel<Model> {
        return model.copy(model = sub.step(intent, model.model))
    }

    override fun view(model: PageModel<Model>): View<Intent> = div(
        attributes { className("report-wrapper") },
        viewHeader(model),
        sub.view(model.model)
    )

    private
    fun viewHeader(model: PageModel<Model>): View<Intent> = div(
        attributes { className("header") },
        div(attributes { className("gradle-logo") }),
        learnMore(model.learnMore),
//        sub.viewHeaderContent(model.model)
//        div(
//            attributes { className("title") },
//            displaySummary(model),
//        ),
//        div(
//            attributes { className("groups") },
//            displayTabButton(Tab.Inputs, model.tab, model.reportedInputs),
//            displayTabButton(Tab.ByMessage, model.tab, model.messageTree.childCount),
//            displayTabButton(Tab.ByLocation, model.tab, model.locationTree.childCount),
//            displayTabButton(Tab.IncompatibleTasks, model.tab, model.reportedIncompatibleTasks)
//        )
    )

//    abstract fun viewHeaderContent(model: PageModel<Model>): View<Intent> = div(


    private
    fun learnMore(learnMore: LearnMore): View<Intent> = div(
        attributes { className("learn-more") },
        span("Learn more about the "),
        a(
            attributes { href(learnMore.documentationLink) },
            learnMore.text
        ),
        span(".")
    )

}