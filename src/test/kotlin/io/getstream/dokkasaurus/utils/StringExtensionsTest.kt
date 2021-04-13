package io.getstream.dokkasaurus.utils

import org.junit.Test
import org.junit.jupiter.api.Assertions

internal class StringExtensionsTest {

    @Test
    fun `tags should be scaped correctly with simple tags`() {
        val simpleTag = "List<Something>"

        Assertions.assertEquals(simpleTag.scapeTags(), "List`<`Something`>`")
    }

    @Test
    fun `tags should be scaped correctly with complex tags`() {
        val simpleTag = "List<Map<String, List<String>>>"

        Assertions.assertEquals(
            simpleTag.scapeTags(),
            "List`<`Map`<`String, List`<`String`>>>`"
        )
    }
}