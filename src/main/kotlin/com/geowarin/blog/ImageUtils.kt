package com.geowarin.blog

import com.intellij.util.ui.ImageUtil
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


fun getImageFromClipboard(): Image? {
    val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
    return when {
        !transferable.isDataFlavorSupported(DataFlavor.imageFlavor) -> null
        else -> transferable.getTransferData(DataFlavor.imageFlavor) as Image
    }
}

fun hasImageInClipboard(): Boolean {
    val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
    return transferable.isDataFlavorSupported(DataFlavor.imageFlavor)
}

fun Image.toBufferedImage(): BufferedImage? {
    if (this is BufferedImage) {
        return this
    }
    val width = getWidth(null)
    val height = getHeight(null)
    if (width < 0 || height < 0) {
        return null
    }

    val dest = ImageUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val g2 = dest.createGraphics()
    g2.drawImage(this, 0, 0, null)
    g2.dispose()
    return dest
}

fun BufferedImage.saveAs(file: File) {
    try {
        file.parentFile.mkdirs()
        ImageIO.write(this, file.extension, file)
    } catch (e: Throwable) {
        throw RuntimeException("Write error for ${file.path}", e)
    }
}
