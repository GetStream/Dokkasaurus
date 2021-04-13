package io.getstream.dokkasaurus

import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.renderers.PackageListCreator
import org.jetbrains.dokka.base.renderers.RootCreator
import org.jetbrains.dokka.base.resolvers.shared.RecognizedLinkFormat
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.transformers.pages.PageTransformer
import io.getstream.dokkasaurus.location.MarkdownLocationProvider
import io.getstream.dokkasaurus.renderer.DocusaurusRenderer

class DokkasaurusPlugin : DokkaPlugin() {

    val preprocessors by extensionPoint<PageTransformer>()

    private val dokkaBase by lazy { plugin<DokkaBase>() }

    val renderer by extending {
        CoreExtensions.renderer providing ::DocusaurusRenderer override dokkaBase.htmlRenderer
    }

    val locationProvider by extending {
        dokkaBase.locationProviderFactory providing MarkdownLocationProvider::Factory override dokkaBase.locationProvider
    }

    val rootCreator by extending {
        preprocessors with RootCreator
    }

    val packageListCreator by extending {
        (preprocessors
                providing { PackageListCreator(it, RecognizedLinkFormat.DokkaGFM) }
                order { after(rootCreator) })
    }
}