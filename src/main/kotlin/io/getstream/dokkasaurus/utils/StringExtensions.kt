package io.getstream.dokkasaurus.utils

internal fun String.simpleScapeTags(): String {
    return this.replace("<", "&lt;").replace(">", "&gt;")
}
