package com.geowarin.blog

import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class ImageInsertSettingsPanel : JPanel() {

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
    val contentPanel = ImageInsertSettingsPanel()

    val builder = DialogBuilder()
    with(builder) {
        setCenterPanel(contentPanel)
        setTitle("Paste Image Settings")
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
