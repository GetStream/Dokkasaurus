import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.32" // Used to create a javadoc jar
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

apply(from = "${rootDir}/scripts/publish.gradle")

val dokkaVersion: String by project

repositories {
    mavenCentral()
    jcenter()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    compileOnly("org.jetbrains.dokka:dokka-core:$dokkaVersion")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.dokka:dokka-base:$dokkaVersion")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.dokka:dokka-test-api:$dokkaVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
}
