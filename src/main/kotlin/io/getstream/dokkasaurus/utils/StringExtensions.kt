package io.getstream.dokkasaurus.utils
private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

internal fun String.simpleScapeTags(): String {
    return this.replace("<", "&lt;").replace(">", "&gt;")
}

internal fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "-${it.value}"
    }.toLowerCase()
}