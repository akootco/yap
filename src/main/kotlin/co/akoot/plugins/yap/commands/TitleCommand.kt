package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.bluefox.api.Profile
import co.akoot.plugins.bluefox.extensions.profile
import co.akoot.plugins.bluefox.extensions.username
import co.akoot.plugins.bluefox.extensions.usernamePossessive
import co.akoot.plugins.bluefox.util.*
import co.akoot.plugins.yap.Yap
import co.akoot.plugins.yap.api.Title
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.format.TextColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

private val titleStyles: List<String> get() = Profile.TitleStyle.entries.map { it.name.lowercase() }
private val titles: List<String> get() = Yap.instance.titles.map { it.id }
private val colors: Set<String> get() = colorCodes.keys

object TitleCommand : CatCommand(Yap.instance, "title", "Set your title!", onCommand = {

    val titleStyleSuggestions: (ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) -> Unit =
        { _, builder ->
            suggest(builder, titleStyles)
        }

    val titleSuggestions: (ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) -> Unit =
        { _, builder ->
            suggest(builder, titles)
        }

    val unlockedTitleSuggestions: (ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) -> Unit =
        { ctx, builder ->
            ctx.playerSender?.let {
                suggest(builder, it.profile.unlockedTitles)
            }
        }

    fun unlockedTitleSuggestions(playerArg: String = "player"): (ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) -> Unit =
        { ctx, builder ->
            ctx.offlinePlayer(playerArg)?.let {
                suggest(builder, it.profile.unlockedTitles)
            }
        }

    val colorSuggestions: (ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) -> Unit =
        { _, builder ->
            suggest(builder, colors)
        }

    val unlockedColorSuggestions: (ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) -> Unit =
        { ctx, builder ->
            ctx.playerSender?.let {
                suggest(builder, it.profile.unlockedColors)
            }
        }

    fun CommandSender.getColors(string: String): List<TextColor> {
        val colors = string.colors()
        if (colors.isEmpty()) sendWarning("Could not extract a single color from that...")
        return colors
    }

    fun CommandSender.getColor(string: String): TextColor? {
        val color = co.akoot.plugins.bluefox.util.getColor(string)
        if (color == null) sendWarning("Invalid color!")
        return color
    }

    fun CommandSender.getTitle(id: String): Title? {
        val title = (plugin as Yap).getTitle(id)
        if (title == null) sendWarning("Title \"", primary(id), "\" does not exist!")
        return title
    }

    fun CommandSender.getTitleStyle(style: String): Profile.TitleStyle? {
        val titleStyle = runCatching { Profile.TitleStyle.valueOf(style) }.getOrNull()
        if (titleStyle == null) {
            sendWarning("That is not a valid title style! Pick from the following:")
            sendWarning(quote(titleStyles.joinToString(", ")))
        }
        return titleStyle
    }

    fun CommandSender.sendTitles(player: OfflinePlayer): Boolean {
        val titles = player.profile.unlockedTitles
        return sendText(titles.joinToString("\n"))
    }

    fun CommandSender.giveTitle(player: OfflinePlayer, title: String): Boolean {
        val title = getTitle(title) ?: return false
        player.profile.giveTitle(title.id)
        return sendText("Gave ", player, " the ", title.name.parse(), " title!")
    }

    fun Player.select(title: String): Boolean {
        val title = getTitle(title) ?: return false
        if (profile.hasTitle(title.id)) return sendWarning("You do not have that title unlocked!")
        profile.title = title.id
        return sendText("Set title to ", title.name.parse(), "!")
    }

    fun CommandSender.setTitle(player: OfflinePlayer, title: String): Boolean {
        if (this == player) {
            player.player?.select(title)?.let { return it }
        }
        val title = getTitle(title) ?: return false
        player.profile.giveTitle(title.id)
        player.profile.title = title.id
        return sendText("Set ", player.usernamePossessive, " title to ", title.name.parse())
    }

    fun Player.sendTitle(player: OfflinePlayer, title: String): Boolean {
        val title = getTitle(title) ?: return false
        if (!profile.hasTitle(title.id)) return sendWarning("You do not have that title unlocked!")
        profile.removeTitle(title.id)
        player.profile.giveTitle(title.id)
        return sendText("Sent title ", title, " to ", primary(player.username), "!")
    }

    fun Player.setTitleStyle(style: String): Boolean {
        val titleStyle = getTitleStyle(style) ?: return false
        profile.titleStyle = titleStyle
        return sendText("Set title style to ", primary(titleStyle.name.lowercase()), "!")
    }

    fun Player.setTitleColor(color: String): Boolean {
        val textColor = getColor(color) ?: return false
        profile.titleColor = color
        return sendText("Set title color to ", textColor + color, "!")
    }

    //title
    noargs { ctx ->
        ctx.playerSender?.let { ctx.sender.sendTitles(it) } ?: false
    }

    //title give <player> <title>
    then {
        subcommand("give") {
            it.sender.sendWarning("Give who what?")
        } then {
            offlinePlayer(false) {
                it.sender.sendWarning("What title?")
            } then {
                string("title", titleSuggestions) {
                    permissionCheck(it, "give") ?: return@string false
                    it.sender.giveTitle(it.player, it.string("title"))
                }
            }
        }
    }

    //title set <player> <title>
    then {
        subcommand("set") {
            it.sender.sendWarning("Set who's title to what?")
        } then {
            offlinePlayer {
                it.sender.sendWarning("What title?")
            } then {
                string("title", titleSuggestions) { ctx ->
                    permissionCheck(ctx, "set") ?: return@string false
                    ctx.playerSender?.let { ctx.sender.setTitle(it, ctx.string("title")) } ?: false
                }
            }
        }
    }

    //title select <title>
    then {
        subcommand("select") {
            it.sender.sendWarning("Set your title to what?")
        } then {
            string("title", unlockedTitleSuggestions) { ctx ->
                permissionCheck(ctx, "select") ?: return@string false
                ctx.playerSender?.select(ctx.string("title")) ?: false
            }
        }
    }

    //title style <style>
    then {
        subcommand("style") {
            it.sender.sendWarning("Set your title's style to what?")
        } then {
            string("style", titleStyleSuggestions) {
                permissionCheck(it, "style") ?: return@string false
                it.playerSender?.setTitleStyle(it.string("style")) ?: false
            }
        }
    }

    //title color <color>
    then {
        subcommand("color") {
            it.sender.sendWarning("What color?")
        } then {
            string("color") {
                permissionCheck(it, "color") ?: return@string false
                it.playerSender?.setTitleColor(it.string("color")) ?: false
            }
        }
    }

    //title send <player> <title>
    then {
        subcommand("send") {
            it.sender.sendWarning("Send it to who?")
        } then {
            offlinePlayer(false) {
                it.sender.sendWarning("What title?")
            } then {
                string("title", unlockedTitleSuggestions) {
                    permissionCheck(it, "send") ?: return@string false
                    val player = it.offlinePlayer ?: return@string false
                    it.playerSender?.sendTitle(player, it.string("title")) ?: return@string false
                }
            }
        }
    }

})