# Custom Component Development


[![GitHub tag](https://img.shields.io/github/tag/adaptris/interlok-custom-component-example.svg)](https://github.com/adaptris/interlok-custom-component-example/tags)
[![license](https://img.shields.io/github/license/adaptris/interlok-custom-component-example.svg)](https://github.com/adaptris/interlok-custom-component-example/blob/develop/LICENSE)
[![Actions Status](https://github.com/adaptris/interlok-custom-component-example/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/adaptris/interlok-custom-component-example/actions)
[![codecov](https://codecov.io/gh/adaptris/interlok-custom-component-example/branch/gradle/graph/badge.svg)](https://codecov.io/gh/adaptris/interlok-custom-component-example)
[![CodeQL](https://github.com/adaptris/interlok-custom-component-example/workflows/CodeQL/badge.svg)](https://github.com/adaptris/interlok-custom-component-example/security/code-scanning)
[![Known Vulnerabilities](https://snyk.io/test/github/adaptris/interlok-custom-component-example/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/adaptris/interlok-custom-component-example?targetFile=build.gradle)
[![Closed PRs](https://img.shields.io/github/issues-pr-closed/adaptris/interlok-custom-component-example)](https://github.com/adaptris/interlok-custom-component-example/pulls?q=is%3Apr+is%3Aclosed)
[![CircleCI](https://circleci.com/gh/adaptris/interlok-custom-component-example/tree/gradle.svg?style=svg)](https://circleci.com/gh/adaptris/interlok-custom-component-example/tree/gradle)
[![AppVeyor](https://ci.appveyor.com/api/projects/status/bxixbf1d9wt4ju4o?svg=true)](https://ci.appveyor.com/project/adaptris/interlok-custom-component-example)


A quick start to writing your own custom component; this is just an example of a custom service.

## Intro

* Java 11 is required for the 4.x branch.
* Java 17 is required for the 5.x branch.

We default to using gradle as the build tool; this is the preferred build system. You can opt to use something else (check the other branches), but we find gradle gives us the most flexibility along with useful conventions.

## Before you start

The things you should consider changing are (You don't have to, but it will make your project a mystery when it comes to discoverability).

Some of these properties can be overridden by putting (the ones that use ?: ternary operators) those properties in a `gradle.properties` file. In most cases variables that should be environment specific are done like this; variables that are specific to the build are left as normal variables defined in the gradle script.

Gradle Entry | Impact | Description |
-------|------------|------|
componentName | JAR Manifest / UI | A short description of your component that shows up as the title in the optional components page |
organizationName | JAR Manifest | Your organization (apologies for the americanism) |
releaseVersion | JAR Manifest / Maven Repo | The version of your artifact |
artifactGroup | JAR Manifest / Maven Repo | Your artifacts group name |
componentDescription | UI |  A longer description that shows up in the UI |
componentTargetInterlokVersion | UI| Your target interlok version |
componentInterlokTags | UI | Tags you want to show up in the UI |
componentRequiresLicense | UI | Whether or not your custom component requires a license (true/false) |
componentMainDocumentation | UI | URL to where the main documentation for the component is |
componentSupplementaryDocumentation | UI | URL to further documentation that you think would be useful for the end user |
componentReadMe | UI | URL to this projects README.md |
componentRepositoryLocation | UI | URL of where the user can checkout the source code for the component |
componentNotes | UI | Text that might be important for the user to know before downloading |
componentDeprecatedText | UI | Text to inform the user that the component is deprecated (leave blank if not deprecated) |
componentDeveloperOnly | UI | (true/false) to inform the user that this component is a stub or development only component |
mavenPublishUrl | Build | Where you are going to publish the artifact to. We don't yet have a good answer for a centralised Interlok artifact repository; this may cause some discoverability issues. |
defaultNexusRepo | Build | if you have a custom maven repository that contains additional dependencies, then specify it here |

## Your first build

```
$ ./gradlew clean check

> Task :test
Picked up JAVA_TOOL_OPTIONS: -Djava.io.tmpdir=C:/Adaptris/TEMP

> Task :spotbugsMain

BUILD SUCCESSFUL in 11s
8 actionable tasks: 8 executed
```

You should be able to see various reports in `build/**/*.html`; along with a new jar in _build/libs_

## 3rd party plugins/apps

All these 3rd party items are either fully opensource, or free for opensource projects. We have enabled them on this sample project; but it is beyond the scope of this README to actually document *why* and *how* you use the plugins...

Also, we get badges from the apps; and you know what, you [_gotta catch 'em all_](https://www.youtube.com/watch?v=lrHJhKEtQEI). Traditionally, in our opensource development, we always try to use (at least) 3 separate CI build tools following the reasoning that if 1/3 fails, then we can still be confident things are mostly OK; 2/3 we probably have a regression.

* ~~[LGTM](https://lgtm.com) is enabled via a `.lgtm.yml` file; we use this to contextually scan source code.~~
* CodeQL (LGTM replacement) is enabled via a github action .github/workflows/codeql-analysis.yml; we use this to contextually scan source code.
* jacoco coverage is enabled and reports will be generated when `check` is invoked
    * If you don't want spotbugs checks then run `./gradlew clean test jacocoTestReport`
* Spotbugs is enabled via a gradle plugin; this is used to statically analyze source code; the gradle file is annotated such that you can add various filters as required.
    * This will be executed when you do `check`
    * Spotbugs analysis is disabled on test resources.
* [OWASP dependency checks](https://owasp.org/www-project-dependency-check/) are enabled via a gradle plugin
    * You need to execute this manually using `./gradlew dependencyCheckAnalyze`
    * We have included a pointer to our standard suppression file
* [CircleCI](https://circleci.com) is enabled on the project to test building both with Java11 via `.circleci/config.yml`
    * ~~Coverage results from jacoco are injected into [codecov.io](https://codecov.io)~~
* ~~[Travis-CI](https://travis-ci.com) is enabled on the project via `.travis.yml`~~
* Github Actions are enabled via `.github/workflows/gradle.yml` - this runs the `check` task
    * Coverage results from jacoco are injected into [codecov.io](https://codecov.io)
* [AppVeyor](https://www.appveyor.com/) configuration is in `appveyor.yml`
* [dependabot](https://dependabot.com) is enabled via `.github/dependabot.yml` for managing dependencies; you could use whitesource renovate instead to manage updates to your dependencies.

## Additional features

### Versioning info based on git branch

If your project source control is git; then the generated `adaptris-version` will reflect the git branch. If it is what is considered the default branch, then the current date is used. All of this is done via the git executable which needs to be present on the path. If this isn't a git repo then a static "No Git Info" message is used for build information.

- You might consider using something like `net.nemerosa.versioning` plugin to generate your version.

In this instance the generated `adaptris-version` file should contain a `build.info=gradle` since this is on the gradle branch.
