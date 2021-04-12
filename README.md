# Dokkassauros

This plugin creates Markdown files that can be used with Docussauros. Use this plugin in conjunction with [DokkasaurosSidebar](https://github.com/GetStream/DokkasaurosSidebar) to generated the page. 

## Installing

At the momment this plugin is not deployed yet at maven central. So you use `mavenLocal()` to use this plugin. 

```
./gradlew assemble publishToMavenLocal
```

Then add `mavenLocal()` to your project and include it to your project:

```
dokkaPlugin("br.com.leandro:dokkasaurus:0.1.3-SNAPSHOT")
```

## Running 
Run for single module projects:

```
./gradlew dokkaHtml
```
And for multi modiule projects:

```
./gradlew dokkaHtmlMultimodule
```

It will generate your documentation in your `build/dokka/html` file.