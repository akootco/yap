package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.bluefox.util.Color
import co.akoot.plugins.bluefox.util.italic
import co.akoot.plugins.bluefox.util.sendWarning
import co.akoot.plugins.bluefox.util.tint
import co.akoot.plugins.yap.Yap
import net.kyori.adventure.text.Component

object RegardingCommand: CatCommand(Yap.instance, "regarding", "Reply to a message in chat!", aliases = arrayOf("re"), onCommand = {
    then {
        int("index") {
            it.sender.sendWarning("What is your response?")
        } then {
            greedyString("message") {
                val string = it.string("message")
                val index = it.int("index")
                val message = Yap.history.getMessage(index) ?: return@greedyString it.sender.sendWarning("Message not found!")
                val component = Component.text("┏ ").append(message.tint(Color.Quote, 0.25).italic())
                plugin.server.broadcast(component)
                it.playerSender?.chat(string)
                true
            }
        }
    }
})