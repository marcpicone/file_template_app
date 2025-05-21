plugins {
    kotlin("jvm")
    // https://proandroiddev.com/stop-using-gradle-buildsrc-use-composite-builds-instead-3c38ac7a2ab3
    id("com.marcpicone.build_src.const")
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

// KtLint - Static code analysis
// https://github.com/pinterest/ktlint/releases
val ktlint: Configuration by configurations.creating

dependencies {
    // KtLint - Static code analysis
    // https://github.com/pinterest/ktlint/releases
    ktlint("com.pinterest.ktlint:ktlint-cli:1.2.1")
}

// KtLint - Static code analysis
// https://github.com/pinterest/ktlint/releases
tasks.register<JavaExec>("ktlint") {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    args("**/src/**/*.kt", "!**/resources/**")
}

// KtLint - Static code format
// https://github.com/pinterest/ktlint/releases
tasks.register<JavaExec>("ktformat") {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    args("**/src/**/*.kt", "!**/resources/**")
}
