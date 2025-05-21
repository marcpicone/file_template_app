plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        create("constPlugin") {
            id = "com.marcpicone.build_src.const"
            implementationClass = "com.marcpicone.build_src.Const"
        }
    }
}
