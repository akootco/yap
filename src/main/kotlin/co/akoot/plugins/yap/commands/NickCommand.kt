package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.bluefox.extensions.profile
import co.akoot.plugins.bluefox.extensions.sendMessage
import co.akoot.plugins.bluefox.extensions.usernamePossessive
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.bluefox.util.primary
import co.akoot.plugins.bluefox.util.sendText
import co.akoot.plugins.bluefox.util.sendWarning
import co.akoot.plugins.yap.Yap

class NickCommand(plugin: Yap): CatCommand(plugin, "nick") {
    init {
        noargs {
            permissionCheck(it) ?: return@noargs false
            val player = getPlayerSender(it) ?: return@noargs false
            val nickname = player.profile.nickname
            if(nickname.isEmpty()) player.sendWarning("You do not have a nickname (yet?)")
            else player.sendText("Your nickname is ", nickname.parse())
        }
        then {
            greedyString("nick") {
                permissionCheck(it, "set") ?: return@greedyString false
                val player = getPlayerSender(it) ?: return@greedyString false
                val nick = getString(it, "nick")
                player.profile.nickname = nick
                player.sendText("Changed your nickname to ", nick.parse(), "!")
            }
        }
        then {
            offlinePlayer {
                permissionCheck(it) ?: return@offlinePlayer false
                val sender = getSender(it)
                val player = getOfflinePlayer(it) ?: return@offlinePlayer false
                val nickname = player.profile.nickname
                if(nickname.isEmpty()) sender.sendWarning(primary(player.usernamePossessive), " does not have a nickname (yet?)")
                else sender.sendText(primary(player.usernamePossessive), " nickname is ", nickname.parse())
            } then {
                greedyString("nick") {
                    permissionCheck(it, "set") ?: return@greedyString false
                    val sender = getSender(it)
                    val player = getOfflinePlayer(it) ?: return@greedyString false
                    val nick = getString(it, "nick")
                    player.profile.nickname = nick
                    sender.sendText("Changed ", primary(player.usernamePossessive), " nickname to ", nick.parse(), "!")
                    player.player?.sendText("Your nickname was set to ", nick.parse(), "!") ?: true
                }
            }
        }
    }
}