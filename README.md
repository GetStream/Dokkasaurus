# Dokkassaurus

This plugin creates Markdown files that can be used with Docussauros. Use this plugin in conjunction with [DokkasaurusSidebar](https://github.com/GetStream/DokkasaurusSidebar) to generated the page.

## Installing

At the momment this plugin is not deployed yet at maven central. So you use `mavenLocal()` to use this plugin.

```
./gradlew assemble publishToMavenLocal
```

Then add `mavenLocal()` to your project and include it to your project (in the module level):

```
dokkaPlugin("io.getstream:dokkasaurus:0.1.4-SNAPSHOT")
```

For multi module projects, put the dokkaPlugin in every module that you would like
to include.

## Running
Run for single module projects:

```
./gradlew dokkaHtml
```
And for multi modiule projects:

```
./gradlew dokkaHtmlMultiModule
```

It will generate your documentation in your `build/dokka/html` file.
