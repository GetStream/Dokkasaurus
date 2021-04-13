package io.getstream.dokkasaurus.renderer

import org.jetbrains.dokka.DokkaException
import org.jetbrains.dokka.base.renderers.DefaultRenderer
import org.jetbrains.dokka.base.renderers.isImage
import org.jetbrains.dokka.base.resolvers.local.LocationProvider
import org.jetbrains.dokka.model.DisplaySourceSet
import org.jetbrains.dokka.pages.*
import org.jetbrains.dokka.plugability.DokkaContext
import io.getstream.dokkasaurus.utils.scapeTags

class DocusaurusRenderer(
    context: DokkaContext,
    private val fileExtension: String = ".md"
) : DefaultRenderer<StringBuilder>(context) {

    private val isPartial = context.configuration.delayTemplateSubstitution

    override fun buildError(node: ContentNode) {
        context.logger.warn("Docusaurus renderer has encountered problem. The unmatched node is $node")
    }

    override fun buildPage(page: ContentPage, content: (StringBuilder, ContentPage) -> Unit): String =
        buildString {
            buildDocusaurusHeader(page.title(locationProvider))
            content(this, page)
        }

    override fun StringBuilder.buildHeader(level: Int, node: ContentHeader, content: StringBuilder.() -> Unit) {
        buildNewLine()
        buildNewLine()
        append("#".repeat(level) + " ")
        content()
        buildNewLine()
    }

    override fun StringBuilder.buildLink(address: String, content: StringBuilder.() -> Unit) {
        append("[")
        content()
        append("]($address)")
    }

    override fun StringBuilder.buildList(
        node: ContentList,
        pageContext: ContentPage,
        sourceSetRestriction: Set<DisplaySourceSet>?
    ) {
        if (node.ordered) {
            append("<ol>")
            buildListItem(node.children, pageContext)
            append("</ol>")
        } else {
            append("<ul>")
            buildListItem(node.children, pageContext)
            append("</ul>")
        }
    }

    override fun StringBuilder.buildNavigation(page: PageNode) {
        locationProvider.ancestors(page).asReversed().forEach { node ->
            append("/")
            if (node.isNavigable) buildLink(node, page)
            else append(node.name)
        }

        buildNewParagraph()
    }

    override fun StringBuilder.buildNewLine() {
        append("  \n")
    }

    override fun StringBuilder.buildResource(node: ContentEmbeddedResource, pageContext: ContentPage) {
        if (node.isImage()) {
            append("!")
        }
        append("[${node.altText}](${node.address})")
    }

    override fun StringBuilder.buildTable(
        node: ContentTable,
        pageContext: ContentPage,
        sourceSetRestriction: Set<DisplaySourceSet>?
    ) {
        buildNewLine()
        if (node.dci.kind == ContentKind.Sample || node.dci.kind == ContentKind.Parameters) {
            node.sourceSets.forEach { sourcesetData ->
                append(sourcesetData.name)
                buildNewLine()
                buildTable(
                    node.copy(
                        children = node.children.filter { it.sourceSets.contains(sourcesetData) },
                        dci = node.dci.copy(kind = ContentKind.Main)
                    ), pageContext, sourceSetRestriction
                )
                buildNewLine()
            }
        } else {
            val size = node.header.firstOrNull()?.children?.size ?: node.children.firstOrNull()?.children?.size ?: 0

            if (node.header.isNotEmpty()) {
                node.header.forEach { contentGroup ->
                    append("| ")
                    contentGroup.children.forEach { contentNode ->
                        append(" ")
                        contentNode.build(this, pageContext, contentNode.sourceSets)
                        append(" | ")
                    }
                    append("\n")
                }
            } else {
                append("| ".repeat(size))
                if (size > 0) append("|\n")
            }

            append("|---".repeat(size))
            if (size > 0) append("|\n")

            node.children.forEach { contentGroup ->
                val builder = StringBuilder()
                contentGroup.children.forEach { contentNode ->
                    builder.append("| ")
                    builder.append("<a name=\"${contentNode.dci.dri.first()}\"></a>")
                    builder.append(
                        buildString { contentNode.build(this, pageContext) }.replace(
                            Regex("#+ "),
                            ""
                        )
                    )  // Workaround for headers inside tables
                }
                append(builder.toString())
                append("|".repeat(size + 1 - contentGroup.children.size))
                append("\n")
            }
        }
    }

    override fun StringBuilder.buildText(textNode: ContentText) {
        if (textNode.text.isNotBlank()) {
            val decorators = parseDecorators(textNode.style)
            append(textNode.text.takeWhile { it == ' ' })
            append(decorators)
            append(textNode.text.scapeTags().trim())
            append(decorators.reversed())
            append(textNode.text.takeLastWhile { it == ' ' })
        }
    }

//    override fun StringBuilder.buildDRILink(
//        node: ContentDRILink,
//        pageContext: ContentPage,
//        sourceSetRestriction: Set<DisplaySourceSet>?
//    ) {
//        locationProvider.resolve(node.address, node.sourceSets, pageContext)?.let {
//            buildLink(it) {
//                buildText(node.children, pageContext, sourceSetRestriction)
//            }
//        } ?: if (isPartial) {
//            templateCommand(ResolveLinkGfmCommand(node.address)) {
//                buildText(node.children, pageContext, sourceSetRestriction)
//            }
//        } else Unit
//    }

    override suspend fun renderPage(page: PageNode) {
        val path by lazy {
            locationProvider.resolve(page, skipExtension = true)
                ?: throw DokkaException("Cannot resolve path for ${page.name}")
        }
        when (page) {
            is ContentPage -> outputWriter.write(path, buildPage(page, ::buildPageContent), fileExtension)
            is RendererSpecificPage -> when (val strategy = page.strategy) {
                is RenderingStrategy.Copy -> outputWriter.writeResources(strategy.from, path)
                is RenderingStrategy.Write -> outputWriter.write(path, strategy.text, fileExtension)
                is RenderingStrategy.Callback -> outputWriter.write(
                    path,
                    strategy.instructions(this, page),
                    fileExtension
                )
                is RenderingStrategy.DriLocationResolvableWrite -> outputWriter.write(
                    path,
                    strategy.contentToResolve(locationProvider::resolve),
                    ""
                )
                is RenderingStrategy.PageLocationResolvableWrite -> outputWriter.write(
                    path,
                    strategy.contentToResolve(locationProvider::resolve),
                    ""
                )
                RenderingStrategy.DoNothing -> Unit
            }
            else -> throw AssertionError(
                "Page ${page.name} cannot be rendered by renderer as it is not renderer specific nor contains content"
            )
        }
    }

    private fun StringBuilder.buildListItem(items: List<ContentNode>, pageContext: ContentPage) {
        items.forEach {
            if (it is ContentList) {
                buildList(it, pageContext)
            } else {
                append("<li>")
                append(buildString { it.build(this, pageContext, it.sourceSets) }.trim())
                append("</li>")
            }
        }
    }

    private fun parseDecorators(styles: Set<Style>) = buildString {
        styles.forEach {
            when (it) {
                TextStyle.Bold -> append("**")
                TextStyle.Italic -> append("*")
                TextStyle.Strong -> append("**")
                TextStyle.Strikethrough -> append("~~")
                else -> Unit
            }
        }
    }

    private fun StringBuilder.buildDocusaurusHeader(title: String = "defaultTitle") {
        append("---\n")
        append("title: $title\n")
        append("---\n")
    }

    private fun StringBuilder.buildNewParagraph() {
        buildNewLine()
        buildNewLine()
    }

    private fun StringBuilder.buildLink(to: PageNode, from: PageNode) =
        buildLink(locationProvider.resolve(to, from)!!) {
            append(to.name)
        }

    private fun String.withEntersAsHtml(): String = replace("\n", "<br>")

    private val PageNode.isNavigable: Boolean
        get() = this !is RendererSpecificPage || strategy != RenderingStrategy.DoNothing

    private fun PageNode.title(locationProvider: LocationProvider): String =
        locationProvider.resolve(this)
            ?.substringAfterLast("/")
            ?.substringBeforeLast(".")
            ?: this.name
}