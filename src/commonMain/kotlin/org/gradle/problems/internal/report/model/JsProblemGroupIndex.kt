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


/**
 * Reads the [groups][JsProblemsSummary.groups] of a problems summary: the description of a group, and
 * the order of sibling groups.
 *
 * A group is found by its path of names from the root group. The producer decides the order and the
 * page follows it. A group missing from the list comes after the listed ones, ordered by name; the
 * producer lists every group, so this only guards against an incomplete list.
 */
class JsProblemGroupIndex(groups: List<JsProblemGroup>) {

    private
    val positions: Map<List<String>, Int> = groups.withIndex().associate { (index, group) -> group.path to index }

    private
    val descriptions: Map<List<String>, String> =
        groups.mapNotNull { group -> group.description?.let { group.path to it } }.toMap()

    /**
     * Returns the description of the group with the given path, or null if it has none.
     */
    fun description(path: List<String>): String? = descriptions[path]

    /**
     * Orders sibling groups, given by their paths.
     */
    val siblingOrder: Comparator<List<String>> =
        compareBy<List<String>> { positions[it] ?: Int.MAX_VALUE }.thenBy { it.last() }
}
