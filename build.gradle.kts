import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.32" // Used to create a javadoc jar
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = "io.getstream"
version = "0.1.7-SNAPSHOT"
val dokkaVersion: String by project
val publicationName="Dokkasaurus"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compileOnly("org.jetbrains.dokka:dokka-core:$dokkaVersion")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.dokka:dokka-base:$dokkaVersion")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.dokka:dokka-test-api:$dokkaVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
}

val dokkaOutputDir = "$buildDir/dokka"

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    dokkaHtml {
        outputDirectory.set(file(dokkaOutputDir))
    }
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        val dokkasaurusPlugin by creating(MavenPublication::class) {
            artifactId = project.name
            from(components["java"])
            artifact(javadocJar.get())

            pom {
                name.set(publicationName)
                description.set("A plugin for Dokka to generate markdown files compatible with Docusaurus")
                url.set("https://github.com/Kotlin/dokka-plugin-template/")

                licenses {
                    license {

                        name.set("Stream License")
                        url.set("https://github.com/GetStream/Dokkasaurus/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("leandroBorgesFerreira")
                        name.set("Leandro Borges Ferreira")
                        email.set("leandro@getstream.io")
                        organization.set("Stream")
                        organizationUrl.set("https://getstream.io/")
                    }
                    developer {
                        id.set("jcminarro")
                        name.set("Jc Miñarro")
                        organization.set("Stream")
                        email.set("josecarlos@getstream.io")
                        organizationUrl.set("https://getstream.io/")
                    }
                    developer {
                        id.set("adasiewiczr")
                        name.set("Rafal Adasiewicz")
                        organization.set("Stream")
                        email.set("rafal@getstream.io")
                        organizationUrl.set("https://getstream.io/")
                    }
                    developer {
                        id.set("ogkuzmin")
                        name.set("Oleg Kuzmin")
                        organization.set("Stream")
                        email.set("oleg@getstream.io")
                        organizationUrl.set("https://getstream.io/")
                    }
                    developer {
                        id.set("zsmb13")
                        name.set("Márton Braun")
                        organization.set("Stream")
                        email.set("marton@getstream.io")
                        organizationUrl.set("https://getstream.io/")
                    }
                    developer {
                        id.set("bychkovdmitry")
                        name.set("Dmitrii Bychkov")
                        organization.set("Stream")
                        email.set("dmitrii@getstream.io")
                        organizationUrl.set("https://getstream.io/")
                    }
                }

                scm {
                    connection.set("scm:git:github.com/getstream/dokkasaurus.git")
                    url.set("https://github.com/getstream/dokkasaurus/tree/master")
                }
            }
        }

        signPublicationsIfKeyPresent(dokkasaurusPlugin)
    }
}

fun Project.signPublicationsIfKeyPresent(publication: MavenPublication) {
    val signingKey: String? = System.getenv("SIGN_KEY")
    val signingKeyPassphrase: String? = System.getenv("SIGN_KEY_PASSPHRASE")

    if (!signingKey.isNullOrBlank()) {
        extensions.configure<SigningExtension>("signing") {
            useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
            sign(publication)
        }
    }
}

// Set up Sonatype repository
nexusPublishing {
    repositories {
        sonatype {
            username.set(System.getenv("OSSRH_USERNAME"))
            password.set(System.getenv("OSSRH_PASSWORD"))
        }
    }
}
