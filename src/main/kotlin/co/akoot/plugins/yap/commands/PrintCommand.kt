package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.yap.Yap

class PrintCommand(plugin: Yap) : CatCommand(plugin, "print") {
    init {
        then {
            greedyString("text") {
                permissionCheck(it) ?: return@greedyString false
                val sender = getSender(it)
                val text = getString(it, "text")
                sender.sendMessage(text.parse())
                true
            }
        }
    }
}