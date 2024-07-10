package data


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
