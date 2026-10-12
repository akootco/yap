package co.akoot.plugins.yap.api

import co.akoot.plugins.bluefox.util.deserialize
import co.akoot.plugins.bluefox.util.serialize
import net.kyori.adventure.text.Component
import java.io.File

class History(val file: File) {

    init {
        file.deleteOnExit()
    }

    fun clearHistory() {
        file.apply {
            delete()
            createNewFile()
        }
    }

    fun addMessage(component: Component): Int {
        val message = component.serialize()
        val messages = file.readLines()
        file.writeText((messages + message).joinToString("\n"))
        return messages.size
    }

    fun getMessage(index: Int): Component? {
        val messages = file.readLines()
        val line = messages.getOrNull(index) ?: return null
        return line.deserialize()
    }
}