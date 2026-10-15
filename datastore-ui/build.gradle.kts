import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.agp.library.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    android {
        namespace = "dev.patrickgold.jetpref.datastore.ui"
        compileSdk = providers.gradleProperty("projectCompileSdk").map { it.toInt() }.get()
        minSdk = providers.gradleProperty("projectMinSdk").map { it.toInt() }.get()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
            optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
        }

        withHostTest { }

        optimization {
            consumerKeepRules.publish = true
        }
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.compose.material.icons.extended)
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.ui)
                implementation(libs.androidx.core.ktx)
                implementation(projects.datastoreModel)
                implementation(projects.materialUi)
            }
        }
    }
}

mavenPublishing {
    coordinates(
        groupId = providers.gradleProperty("projectGroupId").get(),
        artifactId = "jetpref-datastore-ui",
        version = providers.gradleProperty("projectVersion").get(),
    )
    pom {
        name = "JetPref Library"
        description = "Android preference library with Jetpack Compose UI support - fork of patrickgold/jetpref"
        inceptionYear = "2022"
        url = "https://github.com/dot166/jetpref"
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id = "dot166"
                name = "._______166"
                url = "https://dot166.github.io"
            }
            developer {
                id = "patrickgold"
                name = "Patrick Goldinger"
                url = "https://patrickgold.dev"
            }
        }
        scm {
            url = "https://github.com/dot166/jetpref"
            connection = "scm:git:git://github.com/dot166/jetpref.git"
            developerConnection = "scm:git:ssh://git@github.com/dot166/jetpref.git"
        }
    }
}
