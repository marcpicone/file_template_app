package com.marcpicone.file_template_app.file_template_feature_view

class FileTemplateFeatureViewModule {
    fun createFileTemplateFeatureViewManager(): FileTemplateFeatureViewManager {
        return FileTemplateFeatureViewManagerImpl()
    }
}
