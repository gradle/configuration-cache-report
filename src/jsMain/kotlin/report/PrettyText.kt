package report

import elmish.Component
import elmish.View
import elmish.attributes
import elmish.code
import elmish.empty
import elmish.small
import elmish.span


internal
data class PrettyText(val fragments: List<Fragment>) {

    sealed class Fragment {

        data class Text(val text: String) : Fragment()

        data class Reference(val name: String) : Fragment()
    }

    companion object {

        fun ofText(text: String): PrettyText {
            return PrettyText(listOf(Fragment.Text(text)))
        }
    }
}


internal
class PrettyTextComponent<Intent>(
    private val copyableReferences: Boolean,
    private val getCopyIntent: (String) -> Intent,
) : Component<PrettyText, Intent> {

    override fun view(model: PrettyText): View<Intent> = viewPrettyText(model)

    override fun step(intent: Intent, model: PrettyText): PrettyText = model

    private
    fun viewPrettyText(model: PrettyText): View<Intent> = span(
        model.fragments.map {
            when (it) {
                is PrettyText.Fragment.Text -> span(it.text)
                is PrettyText.Fragment.Reference -> reference(it.name, copyableReferences)
            }
        }
    )

    private
    fun reference(name: String, isCopyable: Boolean): View<Intent> = span(
        invisibleBacktick(),
        code(name),
        invisibleBacktick(),
        if (!isCopyable) empty else copyButton(
            text = name,
            tooltip = "Copy reference to the clipboard"
        )
    )

    private
    fun copyButton(text: String, tooltip: String): View<Intent> = small(
        attributes {
            title(tooltip)
            className("copy-button")
            onClick { getCopyIntent(text) }
        }
    )
}
