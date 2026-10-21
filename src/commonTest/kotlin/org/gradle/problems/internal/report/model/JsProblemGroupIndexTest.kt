/*
 * Copyright 2026 the original author or authors.
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

package org.gradle.problems.internal.report.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class JsProblemGroupIndexTest {

    private
    val index = JsProblemGroupIndex(
        listOf(
            JsProblemGroup(listOf("Gradle"), "Gradle runtime."),
            JsProblemGroup(listOf("Gradle", "Deprecation"), "Deprecated usage."),
            JsProblemGroup(listOf("Dependencies"), "Dependencies."),
            JsProblemGroup(listOf("Compilation")),
            JsProblemGroup(listOf("Compilation", "Java")),
            JsProblemGroup(listOf("Compilation", "Undefined"), "Without a sub-group."),
        )
    )

    @Test
    fun `siblings follow the order of the list, not their names`() {
        assertEquals(
            listOf(listOf("Gradle"), listOf("Dependencies"), listOf("Compilation")),
            listOf(listOf("Compilation"), listOf("Gradle"), listOf("Dependencies")).sortedWith(index.siblingOrder)
        )
        assertEquals(
            listOf(listOf("Compilation", "Java"), listOf("Compilation", "Undefined")),
            listOf(listOf("Compilation", "Undefined"), listOf("Compilation", "Java")).sortedWith(index.siblingOrder)
        )
    }

    @Test
    fun `groups missing from the list come after the listed ones, by name`() {
        assertEquals(
            listOf(listOf("Compilation", "Java"), listOf("Compilation", "Undefined"), listOf("Compilation", "Groovy"), listOf("Compilation", "Kotlin")),
            listOf(listOf("Compilation", "Kotlin"), listOf("Compilation", "Undefined"), listOf("Compilation", "Groovy"), listOf("Compilation", "Java"))
                .sortedWith(index.siblingOrder)
        )
    }

    @Test
    fun `without a list, groups are ordered by name`() {
        assertEquals(
            listOf(listOf("Compilation"), listOf("Dependencies"), listOf("Gradle")),
            listOf(listOf("Gradle"), listOf("Compilation"), listOf("Dependencies")).sortedWith(JsProblemGroupIndex(emptyList()).siblingOrder)
        )
    }

    @Test
    fun `descriptions are found by path`() {
        assertEquals("Gradle runtime.", index.description(listOf("Gradle")))
        assertEquals("Deprecated usage.", index.description(listOf("Gradle", "Deprecation")))
        assertNull(index.description(listOf("Compilation")))
        assertNull(index.description(listOf("Deprecation")))
        assertNull(index.description(listOf("Others", "My plugin")))
    }
}
