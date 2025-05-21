package com.marcpicone.file_template_app.file_template_template_view

import com.marcpicone.file_template_app.file_template.FileTemplate
import com.marcpicone.file_template_app.file_template.FileTemplateManager
import com.marcpicone.file_template_app.file_template_feature_view.FileTemplateFeatureViewManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FileTemplateTemplateViewPresenter(
    private val fileTemplateFeatureViewManager: Lazy<FileTemplateFeatureViewManager>,
    private val fileTemplateManager: Lazy<FileTemplateManager>,
    private val fileTemplateTemplateViewManager: Lazy<FileTemplateTemplateViewManager>,
) : FileTemplateTemplateViewContract {

    private val innerState = createState()
    override val event: FileTemplateTemplateViewContract.Event = createEvent()
    override val uiState: FileTemplateTemplateViewContract.UiState = innerState
    private val fileTemplaFeatureViewListener = createFileTemplateFeatureViewListener()
    private val fileTemplateListener = createFileTemplateListener()
    private val fileTemplateTemplateViewListener = createFileTemplateTemplateViewListener()

    private fun createEvent() = object : FileTemplateTemplateViewContract.Event {

        override fun onAttached() {
            fileTemplateFeatureViewManager.value.addListener(fileTemplaFeatureViewListener)
            fileTemplateManager.value.addListener(fileTemplateListener)
            fileTemplateTemplateViewManager.value.addListener(fileTemplateTemplateViewListener)
            updateScreen()
        }

        override fun onDetached() {
            fileTemplateFeatureViewManager.value.removeListener(fileTemplaFeatureViewListener)
            fileTemplateManager.value.removeListener(fileTemplateListener)
            fileTemplateTemplateViewManager.value.removeListener(fileTemplateTemplateViewListener)
        }

        override fun onGroupClicked(group: String) {
            if (innerState.currentGroup.value == group) {
                return
            }
            fileTemplateTemplateViewManager.value.setGroup(group)
        }

        override fun onTemplateClicked(fileTemplate: FileTemplate.ParentFileTemplate) {
            if (fileTemplateFeatureViewManager.value.getCurrentFeatureId() == null) {
                // todo advice user to set a feature first
                return
            }
            fileTemplateTemplateViewManager.value.toggleTemplateId(fileTemplate.id)
        }
    }

    private fun updateScreen() {
        updateTemplates()
        updateCurrentGroup()
        updateCurrentTemplates()
    }

    private fun updateCurrentGroup() {
        // Auto set group if not yet selected
        if (fileTemplateTemplateViewManager.value.getGroup() == null) {
            fileTemplateTemplateViewManager.value.setGroup(createCurrentGroup())
            return // updated in listener
        }
        innerState.currentGroup.update { createCurrentGroup() }
    }

    private fun updateCurrentTemplates() {
        innerState.currentTemplates.update { createCurrentTemplates() }
    }

    private fun updateTemplates() {
        innerState.templates.update { createTemplates() }
    }

    private fun createCurrentGroup(): String {
        return fileTemplateTemplateViewManager.value.getGroup() ?: createGroups().first()
    }

    private fun createCurrentTemplates(): List<FileTemplate.ParentFileTemplate> {
        val templateIds = fileTemplateTemplateViewManager.value.getCurrentFeatureTemplateIds()
        val currentTemplatesForGroup = createTemplates()
            .filter { it.groups.contains(createCurrentGroup()) }
        return currentTemplatesForGroup
            .filter { templateIds.contains(it.id) }
    }

    private fun createGroups(): List<String> {
        return fileTemplateManager.value
            .getFileTemplateIds()
            .flatMap { fileTemplateManager.value.getFileTemplate(it).groups }
            .distinct()
    }

    private fun createTemplates(): List<FileTemplate.ParentFileTemplate> {
        return fileTemplateManager.value
            .getFileTemplateIds()
            .map { fileTemplateManager.value.getFileTemplate(it) }
    }

    private fun createState() = object : FileTemplateTemplateViewContract.UiState {
        // @formatter:off
        override val currentTemplates: MutableStateFlow<List<FileTemplate.ParentFileTemplate>> = MutableStateFlow(emptyList())
        override val currentGroup: MutableStateFlow<String> = MutableStateFlow("")
        override val templates: MutableStateFlow<List<FileTemplate.ParentFileTemplate>> = MutableStateFlow(emptyList())
        // @formatter:on
    }

    private fun createFileTemplateFeatureViewListener() = object : FileTemplateFeatureViewManager.Listener {
        override fun onChanged() {
            updateCurrentTemplates()
        }
    }

    private fun createFileTemplateListener() = object : FileTemplateManager.Listener {
        override fun onChanged() {
            updateTemplates()
        }
    }

    private fun createFileTemplateTemplateViewListener() = object : FileTemplateTemplateViewManager.Listener {
        override fun onChanged() {
            updateCurrentGroup()
            updateCurrentTemplates()
        }
    }
}
