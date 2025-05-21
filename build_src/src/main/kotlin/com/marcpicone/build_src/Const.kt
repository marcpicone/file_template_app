package com.marcpicone.build_src

import org.gradle.api.Plugin
import org.gradle.api.Project

class Const : Plugin<Project> {

    override fun apply(project: Project) {
        // Possibly common dependencies or can stay empty
    }

    companion object {

        const val versionName = "1.00.00"
    }
}
