package com.geowarin.blog

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File


private fun imageTemplate(path: File): String {
    val relImagePath = path.toString().replace('\\', '/')
    return "{{< figure src=\"$relImagePath\" >}}"
}

class PasteImageFromClipboard : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return
        val project = editor.project ?: return

        val imageFromClipboard: Image? = getImageFromClipboard()
        if (imageFromClipboard == null) {
            showErrorDialog()
            return
        }
        val currentDocument = getCurrentDocumentAsFile(project) ?: return
        val bufferedImage: BufferedImage = imageFromClipboard.toBufferedImage() ?: return

        when (val dialogResult = showInsertDialog()) {
            is OkDialogResult -> {

                val imageFile = File(currentDocument.parentFile, "images/${dialogResult.imageName}.png")
                bufferedImage.saveAs(imageFile)

                val relativePath = imageFile.relativeTo(currentDocument.parentFile)
                runWriteCommandAction(project) {
                    EditorModificationUtil.insertStringAtCaret(editor, imageTemplate(relativePath))
                }

                // https://intellij-support.jetbrains.com/hc/en-us/community/posts/206144389-Create-virtual-file-from-file-path
                LocalFileSystem.getInstance().refreshAndFindFileByIoFile(imageFile)?.let { file ->
                    addToVcs(project, file)
                }
            }

            else -> {}
        }
    }


    // from http://stackoverflow.com/questions/17915688/intellij-plugin-get-code-from-current-open-file
    private fun getCurrentDocumentAsFile(project: Project): File? {
        val currentDoc = FileEditorManager.getInstance(project).selectedTextEditor?.document ?: return null
        val currentFile = FileDocumentManager.getInstance().getFile(currentDoc) ?: return null
        return File(currentFile.path)
    }

    private fun addToVcs(project: Project, fileByPath: VirtualFile) {
        val usedVcs = ProjectLevelVcsManager.getInstance(project).getVcsFor(fileByPath)
        if (usedVcs != null && usedVcs.checkinEnvironment != null) {
            ApplicationManager.getApplication().executeOnPooledThread {
                usedVcs.checkinEnvironment?.scheduleUnversionedFilesForAddition(listOf(fileByPath))
            }
        }
    }

}
