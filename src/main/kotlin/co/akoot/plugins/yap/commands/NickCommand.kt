package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.yap.Yap

class NickCommand(plugin: Yap): CatCommand(plugin, "nick") {
    init {
        noargs {
            true
        }
    }
}