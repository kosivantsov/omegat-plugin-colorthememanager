plugins {
    java
    distribution
    id("org.omegat.gradle") version "1.5.7"
}

version = "1.0"

omegat {
    version = "6.0.0"
    pluginClass = "org.truetranslation.omegat.plugin.ColorThemeManagerPlugin"
}

dependencies {
    implementation("org.omegat:omegat:6.0.0")
    packIntoJar("commons-cli:commons-cli:1.4")
    implementation("commons-io:commons-io:2.7")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("org.omegat:lib-mnemonics:1.0")
}

distributions {
    main {
        contents {
            from(tasks["jar"], "README.md", "COPYING")
        }
    }
}
