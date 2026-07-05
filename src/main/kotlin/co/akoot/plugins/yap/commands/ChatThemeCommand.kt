package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.bluefox.extensions.profile
import co.akoot.plugins.bluefox.util.Text.Companion.plus
import co.akoot.plugins.bluefox.util.getColor
import co.akoot.plugins.bluefox.util.or
import co.akoot.plugins.bluefox.util.sendWarning
import co.akoot.plugins.yap.Yap
import net.kyori.adventure.text.format.TextColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChatThemeCommand(plugin: Yap): CatCommand(plugin, "chattheme") {
    init {
        noargs {
            permissionCheck(it) ?: return@noargs false
            val player = getPlayerSender(it) ?: return@noargs false
            sendTheme(player, player)
        }
        then {
            subcommand("format") {
                permissionCheck(it) ?: return@subcommand false
                val player = getPlayerSender(it) ?: return@subcommand false
                sendTheme(player, player)
            } then {
                string("format", suggestions = { _, builder -> suggest(builder, plugin.chatThemes.getKeys().toMutableList()) }) {
                    permissionCheck(it, "set") ?: return@string false
                    val player = getPlayerSender(it) ?: return@string false
                    val themeName = getString(it, "format")
                    if(themeName == "default") {
                        player.profile.chatFormat = ""
                    } else {
                        val format = plugin.getChatThemeFormat(themeName) ?: return@string false
                        player.profile.chatFormat = format
                    }
                    sendTheme(player, player)
                } then {
                    subcommand("global") {
                        permissionCheck(it, "set.global") ?: return@subcommand false
                        val player = getPlayerSender(it) ?: return@subcommand false
                        val themeName = getString(it, "format")
                        if(themeName == "default") {
                            player.profile.universalChatFormat = ""
                        } else {
                            val format = plugin.getChatThemeFormat(themeName) ?: return@subcommand false
                            player.profile.universalChatFormat = format
                        }
                        sendTheme(player, player)
                    }
                }
            }
        }
        then {
            subcommand("tint") {
                permissionCheck(it) ?: return@subcommand false
                val player = getPlayerSender(it) ?: return@subcommand false
                sendTheme(player, player)
            } then {
                string("tint") {
                    permissionCheck(it, "set") ?: return@string false
                    val player = getPlayerSender(it) ?: return@string false
                    val tint = getString(it, "tint")
                    if(tint != "none") {
                        getColor(tint) ?: return@string player.sendWarning("Invalid color!")
                        player.profile.chatTint = tint
                    } else {
                        player.profile.chatTint = ""
                    }
                    sendTheme(player, player)
                } then {
                    subcommand("global") {
                        permissionCheck(it, "set.global") ?: return@subcommand false
                        val player = getPlayerSender(it) ?: return@subcommand false
                        val tint = getString(it, "tint")
                        if(tint != "none") {
                            getColor(tint) ?: return@subcommand player.sendWarning("Invalid color!")
                            player.profile.universalChatTint = tint
                        } else {
                            player.profile.universalChatTint = ""
                        }
                        sendTheme(player, player)
                    }
                }
            }
        }
        then {
            subcommand("tintIntensity") {
                permissionCheck(it) ?: return@subcommand false
                val player = getPlayerSender(it) ?: return@subcommand false
                sendTheme(player, player)
            } then {
                double("tintIntensity", min = 0.0, max = 1.0) {
                    permissionCheck(it, "set") ?: return@double false
                    val player = getPlayerSender(it) ?: return@double false
                    val tintIntensity = getDouble(it, "tintIntensity")
                    player.profile.chatTintIntensity = tintIntensity
                    sendTheme(player, player)
                } then {
                    subcommand("global") {
                        permissionCheck(it, "set.global") ?: return@subcommand false
                        val player = getPlayerSender(it) ?: return@subcommand false
                        val tintIntensity = getDouble(it, "tintIntensity")
                        player.profile.universalChatTintIntensity = tintIntensity
                        sendTheme(player, player)
                    }
                }
            }
        }
        then {
            subcommand("get") {
                permissionCheck(it, "get") ?: false
            } then {
                offlinePlayer {
                    val sender = getSender(it)
                    val player = getOfflinePlayer(it) ?: return@offlinePlayer false
                    sendTheme(sender, player)
                }
            }
        }

    }

    fun sendTheme(sender: CommandSender, player: OfflinePlayer): Boolean {
        val format = player.profile.chatFormat or Yap.instance.defaultChatFormat
        val tint = getColor(player.profile.chatTint)
        val theme = if(sender is Player) {
            player.profile.parseTheme(
                format = format,
                tint = tint,
                profile = sender.profile
            )
        } else {
            player.profile.parseTheme(format, tint)
        }
        sender.sendMessage(theme + "This is a Lorem Ipsum.")
        return true
    }
}