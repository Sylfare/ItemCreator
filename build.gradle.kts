plugins {
	`java-library`
    alias { libs.plugins.paperweight }
    alias { libs.plugins.runPaper }
    alias { libs.plugins.shadow }
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://libraries.minecraft.net")
    }
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
    compileOnly(libs.brigadier)
    implementation(libs.commonsIo)
    implementation(libs.miniMessage)
    compileOnly(libs.lombok)
	annotationProcessor(libs.lombok)
    implementation(libs.jackson)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.shadowJar {
    dependencies {
        include(dependency("tools.jackson.core:jackson-databind"))
        include(dependency("tools.jackson.core:jackson-core"))
        include(dependency("com.fasterxml.jackson.core:jackson-annotations"))
    }
}