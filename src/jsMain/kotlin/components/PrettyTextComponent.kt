package components

import data.PrettyText
import elmish.Component
import elmish.View
import elmish.code
import elmish.empty
import elmish.span


internal
class PrettyTextComponent<Intent>(
    private val getCopyIntent: ((String) -> Intent)? = null
) : Component<PrettyText, Intent> {

    override fun view(model: PrettyText): View<Intent> = viewPrettyText(model)

    override fun step(intent: Intent, model: PrettyText): PrettyText = model

    private
    fun viewPrettyText(model: PrettyText): View<Intent> = span(
        model.fragments.map {
            when (it) {
                is PrettyText.Fragment.Text -> span(it.text)
                is PrettyText.Fragment.Reference -> reference(it.name)
            }
        }
    )

    private
    fun reference(name: String): View<Intent> = span(
        invisibleBacktick,
        code(name),
        invisibleBacktick,
        getCopyIntent?.let { copy ->
            CopyButtonComponent(copy).view(
                text = name,
                tooltip = "Copy reference to the clipboard"
            )
        } ?: empty
    )
}
