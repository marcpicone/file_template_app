package com.marcpicone.file_template_app.file_explorer

class FileExplorerModule {
    fun createFileExplorerManager(): FileExplorerManager {
        return FileExplorerManagerImpl()
    }
}
