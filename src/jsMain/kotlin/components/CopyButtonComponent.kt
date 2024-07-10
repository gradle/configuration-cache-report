package components

import elmish.Component
import elmish.View
import elmish.attributes
import elmish.small


internal
class CopyButtonComponent<Intent>(
    private val getCopyIntent: (String) -> Intent,
) : Component<CopyButtonComponent.Model, Intent> {

    data class Model(
        val text: String,
        val tooltip: String
    )

    fun view(text: String, tooltip: String): View<Intent> =
        view(Model(text, tooltip))

    override fun view(model: Model): View<Intent> = small(
        attributes {
            title(model.tooltip)
            className("copy-button")
            onClick { getCopyIntent(model.text) }
        }
    )

    override fun step(intent: Intent, model: Model): Model = model
}
