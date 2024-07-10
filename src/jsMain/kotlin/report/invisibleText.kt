package report

import elmish.View
import elmish.attributes
import elmish.span


@Suppress("UNCHECKED_CAST")
internal
fun <Intent> invisibleBacktick(): View<Intent> =
    invisibleBacktick as View<Intent>


@Suppress("UNCHECKED_CAST")
internal
fun <Intent> invisibleSpace(): View<Intent> =
    invisibleSpace as View<Intent>


@Suppress("UNCHECKED_CAST")
internal
fun <Intent> invisibleOpenParen(): View<Intent> =
    invisibleOpenParen as View<Intent>


@Suppress("UNCHECKED_CAST")
internal
fun <Intent> invisibleCloseParen(): View<Intent> =
    invisibleCloseParen as View<Intent>


private
val invisibleBacktick: View<*> = invisibleSpanWithTextForCopy("`")


private
val invisibleSpace: View<*> = invisibleSpanWithTextForCopy(" ")


private
val invisibleOpenParen: View<*> = invisibleSpanWithTextForCopy("(")


private
val invisibleCloseParen: View<*> = invisibleSpanWithTextForCopy(")")


private
fun invisibleSpanWithTextForCopy(text: String): View<Unit> = span(
    attributes { classNames("invisible-text", "text-for-copy") },
    text
)