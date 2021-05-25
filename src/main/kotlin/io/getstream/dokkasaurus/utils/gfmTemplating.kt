package io.getstream.dokkasaurus.utils

import org.jetbrains.dokka.links.DRI
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS

@JsonTypeInfo(use = CLASS)
sealed class Command {
    companion object {
        fun Appendable.templateCommand(command: Command, content: Appendable.() -> Unit) {
            content()
        }
    }
}

class ResolveLinkCommands(val dri: DRI) : Command()
