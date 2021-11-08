package com.geowarin.blog

import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.panels.RowGridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

internal class ImageInsertPanel : JPanel(RowGridLayout(1, 1, 10)) {

    internal val nameField = JTextField()

    init {
        add(JLabel("Image name"))
        add(nameField)
    }
}


sealed class DialogResult

data class OkDialogResult(
    val imageName: String
) : DialogResult()

object CancelDialogResult : DialogResult()

fun showInsertDialog(): DialogResult {
    val contentPanel = ImageInsertPanel()

    val builder = DialogBuilder()
        .centerPanel(contentPanel)
        .title("Paste Image").apply {
            setPreferredFocusComponent(contentPanel.nameField)
        }

    return when (builder.show()) {
        DialogWrapper.OK_EXIT_CODE -> {
            if (contentPanel.nameField.text.isEmpty()) CancelDialogResult else
                OkDialogResult(imageName = contentPanel.nameField.text)
        }
        else -> CancelDialogResult
    }

}

fun showErrorDialog() {
    DialogBuilder()
        .centerPanel(JLabel("Clipboard does not contain any image"))
        .title("No Image in Clipboard")
        .apply {
            addOkAction()
        }.show()
}
