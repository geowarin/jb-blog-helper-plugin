package com.geowarin.blog

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.actionSystem.EditorTextInsertHandler
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.guessProjectDir
import com.intellij.util.Producer
import java.awt.datatransfer.Transferable


class PasteImageHandler(private val myOriginalHandler: EditorActionHandler?) : EditorActionHandler(),
    EditorTextInsertHandler {

    private fun createAnEvent(action: AnAction, context: DataContext): AnActionEvent {
        val presentation = action.templatePresentation.clone()
        return AnActionEvent(null, context, ActionPlaces.UNKNOWN, presentation, ActionManager.getInstance(), 0)
    }

    override fun execute(editor: Editor, dataContext: DataContext, producer: Producer<out Transferable>?) {
        val caret = editor.caretModel.primaryCaret
        doExecute(editor, caret, dataContext)
    }

    public override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        val project = editor.project
        if (isHugoProject(project) && editor is EditorEx && editor.virtualFile?.fileType?.name == "Markdown" && hasImageInClipboard()) {
            assert(caret == null) { "Invocation of 'paste' operation for specific caret is not supported" }
            val action = PasteImageFromClipboard()
            val event = createAnEvent(action, dataContext)
            action.actionPerformed(event)
        } else {
            myOriginalHandler?.execute(editor, null, dataContext)
        }
    }

    private fun isHugoProject(project: com.intellij.openapi.project.Project?): Boolean {
        val projectDir = project?.guessProjectDir() ?: return false
        return projectDir.findChild("hugo.toml") != null ||
                projectDir.findChild("config.toml") != null ||
                projectDir.findChild("config.yaml") != null ||
                projectDir.findChild("config.json") != null
    }

//    companion object {
//        private val LOG = Logger.getInstance("img2md.PasteHandler")
//    }
}
