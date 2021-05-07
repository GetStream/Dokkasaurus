package io.getstream.dokkasaurus.utils

import org.junit.Test
import org.junit.jupiter.api.Assertions

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
}