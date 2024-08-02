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

package components

import data.PrettyText
import elmish.Component
import elmish.View
import elmish.code
import elmish.empty
import elmish.span


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
                is PrettyText.Fragment.Reference -> reference(it.name, it.clipboardString)
            }
        }
    )

    private
    fun reference(name: String, clipboardString: String): View<Intent> = span(
        invisibleBacktick,
        code(name),
        invisibleBacktick,
        getCopyIntent?.let { copy ->
            CopyButtonComponent(copy).view(
                text = clipboardString,
                tooltip = "Copy reference to the clipboard"
            )
        } ?: empty
    )
}
