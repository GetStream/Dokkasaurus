# Dokkassaurus

This plugin creates Markdown files that can be used with Docussauros. Use this plugin in conjunction with [DokkasaurusSidebar](https://github.com/GetStream/DokkasaurusSidebar) to generated the page.

## Installing

The plugin is deployed to MavenCentra. You need to add `mavenCentral()` to your project and include it to your project (in the module level):

```
dokkaPlugin("io.getstream:dokkasaurus:0.1.9")
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
