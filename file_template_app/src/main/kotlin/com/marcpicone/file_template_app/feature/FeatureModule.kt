package com.marcpicone.file_template_app.feature

class FeatureModule {
    fun createFeatureManager(): FeatureManager {
        return FeatureManagerImpl()
    }
}
