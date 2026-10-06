plugins { id("org.jetbrains.kotlin.jvm") version "2.0.21" }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }

dependencies { testImplementation("junit:junit:4.13.2") }
