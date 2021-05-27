package io.getstream.dokkasaurus.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class StringExtensionsTest {

    @Test
    fun `tags should be scaped correctly with simple tags`() {
        val simpleTag = "List<Something>"

        Assertions.assertEquals("List&lt;Something&gt;", simpleTag.simpleScapeTags())
    }

    @Test
    fun `tags should be scaped correctly with complex tags`() {
        val complexTag = "List<Map<String, List<String>>>"

        Assertions.assertEquals(
            "List&lt;Map&lt;String, List&lt;String&gt;&gt;&gt;",
            complexTag.simpleScapeTags()
        )
    }

    @ParameterizedTest
    @MethodSource("generateCamelToKebabCaseArguments")
    fun `String should be converted from camelCase to kebab-case`(input: String, expectedResult: String) {
        Assertions.assertEquals(expectedResult, input.camelToKebabCase())
    }

    companion object {
        @JvmStatic
        fun generateCamelToKebabCaseArguments() = listOf(
            Arguments.of("a", "a"),
            Arguments.of("A", "a"),
            Arguments.of("ab", "ab"),
            Arguments.of("aB", "a-b"),
            Arguments.of("AB", "a-b"),
            Arguments.of("ABc", "a-bc"),
            Arguments.of("ABC", "a-b-c"),

        )
    }
}