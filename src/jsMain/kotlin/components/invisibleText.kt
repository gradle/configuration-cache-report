package components

import elmish.View
import elmish.attributes
import elmish.span


internal
val invisibleBacktick: View<Nothing> = invisibleSpanWithTextForCopy("`")


internal
val invisibleSpace: View<Nothing> = invisibleSpanWithTextForCopy(" ")


internal
val invisibleOpenParen: View<Nothing> = invisibleSpanWithTextForCopy("(")


internal
val invisibleCloseParen: View<Nothing> = invisibleSpanWithTextForCopy(")")


/**
 * Creates an element that is not visible on the page and does not affect the layout,
 * but is copied in the clipboard as [text] together with the surrounding content.
 */
private
fun invisibleSpanWithTextForCopy(text: String): View<Nothing> = span(
    attributes { classNames("invisible-text", "text-for-copy") },
    text
)
