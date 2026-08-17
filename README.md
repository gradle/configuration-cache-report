# Configuration Cache Report

The `:configuration-cache-report` project produces the JavaScript / HTML
app for browsing and understanding problems occurred when running a
build with configuration caching.

The output of `:configuration-cache-report` is completely produced by the
`assembleReport` task into `build/report` which then gets published to repo.grdev.net.
It's consumed by `gradle/gradle`'s
[`configuration-problems-base` module](https://github.com/gradle/gradle/tree/master/platforms/core-configuration/configuration-problems-base).

## Architecture

The interface between `:configuration-cache` and
`:configuration-cache-report` is a JavaScript global function named
`configurationCacheProblems`, which returns the report model collected at
configuration time. In the HTML template it stands in as a `<script>` element
referencing `configuration-cache-report-data.js`; when a build writes a report,
that element is replaced by the generated function.

Both ends of this interface live in this project:
[the model classes](./src/commonMain/kotlin/org/gradle/problems/internal/report/model)
in `commonMain`, and
[`HtmlReportWriter`](./src/jvmMain/kotlin/org/gradle/problems/internal/report/HtmlReportWriter.kt)
in `jvmMain`, which splits the template and emits the function into it. `gradle/gradle`
uses the writer through the published jar, so the report data format can be changed here
without changing both repositories in lockstep.

An [example file](./src/jsMain/resources/configuration-cache-report-data.js)
is kept for documentation and testing purposes.

The app itself is built according to
[the Elm architecture](https://guide.elm-lang.org/architecture/).

## Setting up a quick feedback loop

Run in development mode with Kotlin sources in the browser debugger:

```shell
./gradlew :jsBrowserDevelopmentRun
```

Add live-reload by using Gradle's continuous mode:

```shell
./gradlew :jsBrowserDevelopmentRun --continuous
```

## Development with `gradle/gradle` and composite build

To quickly make and verify changes for `gradle/gradle` build, you can run
(this assumes you have cloned `configuration-cache-report` in the same directory as `gradle`):

```
./gradlew <TheTaskToBeRunInGradleBuild> --include-build ../configuration-cache-report -Dorg.gradle.dependency.verification=lenient
```

## Releasing

See [internal docs](https://bt-internal-docs.grdev.net/gbt/satellites/cc-report-release/#releasing).
