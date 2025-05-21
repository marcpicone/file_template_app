import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import com.marcpicone.build_src.Const

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.marcpicone"
version = Const.versionName

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)

    // common
    implementation("androidx.annotation:annotation:1.8.0")
    implementation("org.json:json:20231013")

    // material 3
    implementation("org.jetbrains.compose.material3:material3-desktop:1.2.1")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-swing
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")

    // Apache Velocity : https://velocity.apache.org/engine/
    implementation("org.apache.velocity:velocity-engine-core:2.3")

    // Unit-Testing-only dependencies
    testImplementation("io.mockk:mockk:1.12.5")
    testImplementation("junit:junit:4.13.2")
}

compose.desktop {
    application {
        mainClass = "com.marcpicone.file_template_app.application.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "file_template_app"
            packageVersion = Const.versionName
        }
    }
    sourceSets {
        getByName("main") {
            // Split resources.
            // https://medium.com/google-developer-experts/android-project-structure-alternative-way-29ce766682f0#.sjnhetuhb

            // Please keep alphabetical order for folder. Why?
            // To match the order of Android Studio "Project" panel and be easier to read
            resources.setSrcDirs(
                listOf(
                    "src/main/resources",
                    "src/main/resources/file_template_structure_preview_view"
                )
            )
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}
