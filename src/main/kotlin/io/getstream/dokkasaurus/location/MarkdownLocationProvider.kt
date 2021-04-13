package io.getstream.dokkasaurus.location

import org.jetbrains.dokka.base.resolvers.local.DokkaLocationProvider
import org.jetbrains.dokka.base.resolvers.local.LocationProviderFactory
import org.jetbrains.dokka.pages.ModulePageNode
import org.jetbrains.dokka.pages.PackagePageNode
import org.jetbrains.dokka.pages.PageNode
import org.jetbrains.dokka.pages.RootPageNode
import org.jetbrains.dokka.plugability.DokkaContext
import java.util.*

class MarkdownLocationProvider(
    pageGraphRoot: RootPageNode,
    dokkaContext: DokkaContext
) : DokkaLocationProvider(pageGraphRoot, dokkaContext, ".md") {

    class Factory(private val context: DokkaContext) : LocationProviderFactory {
        override fun getLocationProvider(pageNode: RootPageNode) =
            MarkdownLocationProvider(pageNode, context)
    }

    override val pathsIndex: Map<PageNode, List<String>> = IdentityHashMap<PageNode, List<String>>().apply {
        fun registerPath(page: PageNode, prefix: List<String>) {
            if (page is RootPageNode && page.forceTopLevelName) {
                put(page, prefix + PAGE_WITH_CHILDREN_SUFFIX)
                page.children.forEach { registerPath(it, prefix) }
            } else {
                val newPrefix = prefix + page.pathName
                put(page, if (page is ModulePageNode) prefix else newPrefix)
                page.children.forEach { registerPath(it, newPrefix) }
            }

        }
        put(pageGraphRoot, emptyList())
        pageGraphRoot.children.forEach { registerPath(it, emptyList()) }
    }

    private val PageNode.pathName: String
        get() = if (this is PackagePageNode) name else identifierToFilename(name)

    companion object {
        private val reservedFilenames = setOf("index", "con", "aux", "lst", "prn", "nul", "eof", "inp", "out")

        //Taken from: https://stackoverflow.com/questions/1976007/what-characters-are-forbidden-in-windows-and-linux-directory-names
        private val reservedCharacters = setOf('|', '>', '<', '*', ':', '"', '?', '%')

        fun identifierToFilename(name: String): String {
            if (name.isEmpty()) return "--root--"
            return sanitizeFileName(name, reservedFilenames, reservedCharacters)
        }
    }

}

private fun sanitizeFileName(name: String, reservedFileNames: Set<String>, reservedCharacters: Set<Char>): String {
    val withoutReservedFileNames = if (name in reservedFileNames) "--$name--" else name
    return reservedCharacters.fold(withoutReservedFileNames) { acc, character ->
        if (character in acc) acc.replace(character.toString(), "[${character.toInt()}]")
        else acc
    }
}