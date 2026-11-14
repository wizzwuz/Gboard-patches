rootProject.name = "gboard-patches"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "CodebergMorphe"
            url = uri("https://codeberg.org/api/packages/JohninNL/maven")
            content { includeGroupByRegex("app\\.morphe(\\..*)?") }
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        maven {
            name = "CodebergMorphe"
            url = uri("https://codeberg.org/api/packages/JohninNL/maven")
            content { includeGroupByRegex("app\\.morphe(\\..*)?") }
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/MorpheApp/registry")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
        maven { url = uri("https://jitpack.io") }
    }
}

plugins {
    id("app.morphe.patches") version "2.1.0"
}
