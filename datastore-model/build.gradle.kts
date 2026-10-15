import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.agp.library.multiplatform)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    android {
        namespace = "dev.patrickgold.jetpref.datastore"
        compileSdk = providers.gradleProperty("projectCompileSdk").map { it.toInt() }.get()
        minSdk = providers.gradleProperty("projectMinSdk").map { it.toInt() }.get()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }

        withHostTest {}

        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.files.add(File("consumer-rules.pro"))
        }
    }

    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.junit.jupiter.api)
                implementation(libs.junit.jupiter.params)
            }
        }
    }
}

dependencies {
    listOf(
        "kspJvmTest",
        "kspAndroidHostTest",
    ).forEach { configurationName ->
        add(configurationName, projects.datastoreModelProcessor)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

mavenPublishing {
    coordinates(
        groupId = providers.gradleProperty("projectGroupId").get(),
        artifactId = "jetpref-datastore-model",
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
