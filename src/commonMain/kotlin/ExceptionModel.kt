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

data class ExceptionModel(
    val rawText: String,
    val message: String,
    val stackTraceParts: List<StackTracePart>
)


data class StackTracePart(
    /**
     * Stacktrace lines corresponding to Gradle or JDK internal calls
     */
    val isInternal: Boolean,
    val stackTraceLines: List<String>
)


fun exceptionModelFor(exceptionText: String): ExceptionModel {
    val exceptionLines = exceptionText.lines()

    val messageLines = exceptionLines.takeWhile { !it.trim().startsWith("at ") }
    val messageText = messageLines.joinToString("\n")

    val stackTraceLines = exceptionLines.subList(messageLines.size, exceptionLines.size)
        .map { it.trim() }
    val stackTraceParts = stackTraceLines.chunkedBy(::isInternalStackTraceLine)
        .map { (isInternal, partLines) -> StackTracePart(isInternal, partLines) }

    return ExceptionModel(exceptionText, messageText, stackTraceParts)
}


private
fun isInternalStackTraceLine(s: String): Boolean =
    // JDK calls
    s.startsWith("at java.") ||
        s.startsWith("at jdk.internal.") ||
        s.startsWith("at com.sun.proxy.") ||
        // Groovy calls
        s.startsWith("at groovy.lang.") ||
        s.startsWith("at org.codehaus.groovy.") ||
        // Gradle calls
        s.startsWith("at org.gradle.")


/**
 * Splits the list into chunks where each chunk corresponds to the continuous
 * range of list items corresponding to the same [category][categorize].
 */
private
fun <T, C> List<T>.chunkedBy(categorize: (T) -> C): List<Pair<C, List<T>>> {
    if (this.isEmpty()) {
        return emptyList()
    }

    val groups = mutableListOf<Pair<C, List<T>>>()
    var lastCategory: C? = null
    val buffer = mutableListOf<T>()
    for (item in this) {
        val category = categorize(item)
        if (lastCategory == null) {
            buffer += item
            lastCategory = category
        } else if (category == lastCategory) {
            buffer += item
        } else {
            if (buffer.isNotEmpty()) {
                groups += lastCategory to buffer.toList()
                buffer.clear()
            }
            buffer += item
            lastCategory = category
        }
    }

    if (buffer.isNotEmpty()) {
        val actualCategory = lastCategory!!
        groups += actualCategory to buffer.toList()
    }

    return groups
}
