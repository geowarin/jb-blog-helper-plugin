package com.geowarin.blog

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JLabel


class PasteImageFromClipboard : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return
        val project = editor.project ?: return

        val imageFromClipboard: Image? = getImageFromClipboard()
        if (imageFromClipboard == null) {
            showError()
            return
        }
        val currentDocument = getCurrentDocumentAsFile(project) ?: return
        val bufferedImage: BufferedImage = imageFromClipboard.toBufferedImage() ?: return

        when (val dialogResult = showInsertDialog()) {
            is OkDialogResult -> {

                val imageName: String = dialogResult.imageName

                val imageFile = File(currentDocument.parentFile, "images/$imageName.png")
                bufferedImage.saveAs(imageFile)


                val relativePath = imageFile.relativeTo(currentDocument.parentFile)
                insertImageElement(editor, relativePath)

                // https://intellij-support.jetbrains.com/hc/en-us/community/posts/206144389-Create-virtual-file-from-file-path
                LocalFileSystem.getInstance().refreshAndFindFileByIoFile(imageFile)?.let { file ->
                    addToVcs(project, file)
                }
            }
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
                usedVcs.checkinEnvironment!!.scheduleUnversionedFilesForAddition(listOf(fileByPath))
            }
        }
    }

    private fun showError() {
        val builder = DialogBuilder()
        builder.setCenterPanel(JLabel("Clipboard does not contain any image"))
        builder.setDimensionServiceKey("PasteImageFromClipboard.NoImage")
        builder.setTitle("No Image in Clipboard")
        builder.removeAllActions()
        builder.addOkAction()
        builder.show()
    }

    private fun insertImageElement(editor: Editor, imageFile: File) {
        val relImagePath = imageFile.toString().replace('\\', '/')
        val r = Runnable { EditorModificationUtil.insertStringAtCaret(editor, "![]($relImagePath)") }
        WriteCommandAction.runWriteCommandAction(editor.project, r)
    }
}
