package io.getstream.dokkasaurus.utils

internal fun String.scapeTags(): String {
    var insideTagBegin = false
    var insideTagEnd = false


    return foldIndexed("") { index, acc, char ->
        when {
            !insideTagBegin && char == '<' -> {
                insideTagBegin = true

                "$acc`$char"
            }

            insideTagBegin && char == '<' -> "$acc$char"

            insideTagBegin && char != '<' -> {
                insideTagBegin = false
                "$acc`$char"
            }

            !insideTagEnd && char == '>' ->
                if (index == length - 1) {
                    "$acc`$char`"
                } else {
                    insideTagEnd = true
                    "$acc`$char"
                }

            //The end was reached inside tag end
            insideTagEnd && index == length - 1 -> "$acc$char`"

            insideTagEnd && char == '>' -> "$acc$char"

            insideTagEnd && char != '>' -> {
                insideTagBegin = false
                "$acc$char`"
            }

            else -> "$acc$char"
        }
    }
}

internal fun String.simpleScapeTags(): String {
    return this.replace("<", "&lt;").replace(">", "&gt;")
}
