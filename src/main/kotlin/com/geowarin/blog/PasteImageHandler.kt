package com.geowarin.blog

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionUtil
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

    private fun createAnEvent(context: DataContext): AnActionEvent {
        return AnActionEvent.createEvent(context, null, ActionPlaces.UNKNOWN, ActionUiKind.NONE, null)
    }

    override fun execute(editor: Editor, dataContext: DataContext, producer: Producer<out Transferable>?) {
        val caret = editor.caretModel.primaryCaret
        doExecute(editor, caret, dataContext)
    }

    public override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        val project = editor.project
        if (editor is EditorEx && editor.virtualFile?.fileType?.name == "Markdown" && hasImageInClipboard()) {
            assert(caret == null) { "Invocation of 'paste' operation for specific caret is not supported" }
            val action = PasteImageFromClipboard()
            val event = createAnEvent(dataContext)
            ActionUtil.performAction(action, event)
        } else {
            myOriginalHandler?.execute(editor, null, dataContext)
        }
    }
//    companion object {
//        private val LOG = Logger.getInstance("img2md.PasteHandler")
//    }
}
