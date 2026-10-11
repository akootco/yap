package co.akoot.plugins.yap.api

import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.yap.Yap
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import java.io.File
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.StandardOpenOption

class History(val file: File) {

    init {
        file.deleteOnExit()
    }

    companion object {
        const val DELIMITER = "§"
    }

    fun clearHistory() {
        file.apply {
            delete()
            createNewFile()
        }
    }

    fun addMessage(source: ChatSource, sender: String, content: String): Int {
        val message = "${source.ordinal}$DELIMITER$sender$DELIMITER$content"
        val messages = file.readLines()
        file.writeText((messages + message).joinToString("\n"))
        return messages.size
    }

    fun getMessage(index: Int): Component? {
        val messages = file.readLines()
        val line = messages.getOrNull(index) ?: return null
        return getMessage(line)
    }

    fun getMessage(source: ChatSource, author: String): Component? {
        val messages = file.readLines()
        val line = messages.findLast {
            it.startsWith(source.ordinal.toString()) && it.contains(author)
        } ?: return null
        return getMessage(line)
    }

    fun getMessage(line: String): Component? {
        val parts = line.split(DELIMITER)
        val source = parts.getOrNull(0)?.toIntOrNull()?.let { ChatSource.entries.elementAt(it) } ?: return null
        val name = parts.getOrNull(1)  ?: return null
        val sender = if(source == ChatSource.MINECRAFT) {
            val component = Yap.instance.server.getPlayerExact(name)?.displayName() ?: Component.text(name)
            component.append(Component.text(": "))
        } else {
            Component.empty()
        }
        val content = parts.getOrNull(2) ?: return null
        return sender.append(content.parse())
    }
}