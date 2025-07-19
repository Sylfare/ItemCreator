plugins {
	`java-library`
    alias { libs.plugins.paperweight }
    alias { libs.plugins.runPaper }
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