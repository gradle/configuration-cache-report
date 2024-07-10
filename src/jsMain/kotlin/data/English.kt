package data


internal
fun found(count: Int, what: String) =
    "${count.toStringOrNo()} ${what.sIfPlural(count)} ${wasOrWere(count)} found"


internal
fun Int.toStringOrNo() =
    if (this != 0) toString()
    else "No"


internal
fun String.sIfPlural(count: Int) =
    if (count < 2) this else "${this}s"


internal
fun wasOrWere(count: Int) =
    if (count <= 1) "was" else "were"


internal
fun itsOrTheir(count: Int) =
    if (count <= 1) "its" else "their"