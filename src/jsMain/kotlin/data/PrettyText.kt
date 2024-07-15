package data


data class PrettyText(val fragments: List<Fragment>) {

    sealed class Fragment {

        data class Text(val text: String) : Fragment()

        data class Reference(val name: String) : Fragment()
    }

    class Builder {

        private
        val fragments = mutableListOf<Fragment>()

        fun text(text: String) = apply { fragments.add(Fragment.Text(text)) }

        fun ref(reference: String) = apply { fragments.add(Fragment.Reference(reference)) }

        fun build() = PrettyText(fragments.toList())
    }

    companion object {

        fun ofText(text: String): PrettyText {
            return PrettyText(listOf(Fragment.Text(text)))
        }

        fun build(builder: Builder.() -> Unit): PrettyText {
            return Builder().apply(builder).build()
        }
    }
}
