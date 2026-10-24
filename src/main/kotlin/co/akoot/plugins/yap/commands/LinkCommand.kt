package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.bluefox.extensions.profile
import co.akoot.plugins.bluefox.util.async
import co.akoot.plugins.bluefox.util.primary
import co.akoot.plugins.bluefox.util.quote
import co.akoot.plugins.bluefox.util.secondary
import co.akoot.plugins.bluefox.util.sendText
import co.akoot.plugins.bluefox.util.sendWarning
import co.akoot.plugins.bluefox.util.sync
import co.akoot.plugins.yap.Yap
import co.akoot.plugins.yap.api.LinkTracker
import co.akoot.plugins.yap.extensions.getMember

object LinkCommand: CatCommand(Yap.instance, "link", "Link your Discord account!", onCommand = {

    noargs {
        it.playerSender?.sendWarning("To link your ", primary("Discord"), ", provide your ", secondary("Discord username"), "!") ?: false
    }

    then {
        greedyString("discord username") {
            val player = it.playerSender ?: return@greedyString false

            val currentLink = player.profile.getDiscordId()
            if (currentLink != null) {
                return@greedyString player.sendWarning("This Discord account is already linked! Use !unlink from Discord to unlink.")
            }

            val username = it.string("discord username")
            if(username.length !in 2..32) return@greedyString player.sendWarning("Invalid username size... What are we doing man...")
            val memberFuture = Yap.guild?.getMember(username) ?: return@greedyString player.sendWarning("This is so sad, I can't access Discord right now...")
            async {
                val member = memberFuture.get()
                if (member == null) {
                    sync {
                        player.sendWarning("Couldn't find that member!")
                    }
                    return@async
                }
                val id = member.idLong
                sync {
                    val result = Yap.linkTracker.confirm(player.uniqueId, id, LinkTracker.Platform.MINECRAFT)
                    when (result) {
                        LinkTracker.Result.WAITING ->
                            player.sendText("Now run ", quote("/link ${player.name}"), " on ", primary("Discord"), " to finish linking!")

                        LinkTracker.Result.LINKED ->
                            player.sendText("Your Minecraft and Discord accounts are now linked!")
                    }
                }
            }
            true
        }
    }
})