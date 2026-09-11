# Configuration Cache Report

The `:configuration-cache-report` project produces the JavaScript / HTML
app for browsing and understanding problems occurred when running a
build with configuration caching.

The output of `:configuration-cache-report` is completely produced by the
`assembleReport` task into `build/report` which then gets published to repo.grdev.net.
It's consumed by `gradle/gradle`'s
[`configuration-problems-base` module](https://github.com/gradle/gradle/tree/master/platforms/core-configuration/configuration-problems-base).

## Architecture

The interface between `:configuration-cache` and `:configuration-cache-report`
is a set of `<script type="application/json">` elements the page finds by id:
the reported items under `diagnostics`, and the summary of the report under an
id that says which kind of report it is. Each element carries the JSON of that
piece, so the page hands it straight to a parser without ever building a
JavaScript object of the whole report. In the HTML template these elements
stand in as a `<script>` element referencing `configuration-cache-report-data.js`;
when a build writes a report, that element is replaced by the real data.

Both ends of this interface live in this project:
[the model classes](./src/commonMain/kotlin/org/gradle/problems/internal/report/model)
in `commonMain`, and
[`HtmlReportWriter`](./src/jvmMain/kotlin/org/gradle/problems/internal/report/HtmlReportWriter.kt)
in `jvmMain`, which splits the template and writes the data into it. `gradle/gradle`
uses the writer through the published jar, so the report data format can be changed here
without changing both repositories in lockstep.

An [example file](./src/jsMain/resources/configuration-cache-report-data.js)
builds the same elements from sample data authored as objects, for development
and testing purposes.

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
