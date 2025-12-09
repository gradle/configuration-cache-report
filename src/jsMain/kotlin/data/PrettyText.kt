/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package data


data class PrettyText(val fragments: List<Fragment>) {

    sealed class Fragment {

        data class Text(val text: String) : Fragment()

        data class Reference(val name: String, val clipboardString: String) : Fragment()
    }

    class Builder {

        private
        val fragments = mutableListOf<Fragment>()

        fun text(text: String) = apply {
            fragments.add(Fragment.Text(text))
        }

        fun ref(reference: String, clipboardString: String = reference) = apply {
            fragments.add(Fragment.Reference(reference, clipboardString))
        }

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
