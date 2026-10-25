package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.bluefox.extensions.profile
import co.akoot.plugins.bluefox.util.quote
import co.akoot.plugins.bluefox.util.sendText
import co.akoot.plugins.bluefox.util.sendWarning
import co.akoot.plugins.yap.Yap

object UnlinkCommand: CatCommand(Yap.instance, "unlink", "Unlink your Discord account!", onCommand = {
    noargs {
        val player = it.playerSender ?: return@noargs false
        player.profile.getDiscordId() ?: return@noargs player.sendWarning("You haven't linked your Discord account yet!\nUse ",
            quote("/link <discord username>", " to link it.")
        )
        player.profile.unlinkDiscordId()
        player.sendText("Your Minecraft and Discord accounts are no longer linked!")
    }
})