package co.akoot.plugins.yap

import co.akoot.plugins.bluefox.BlueFox
import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.extensions.or
import co.akoot.plugins.bluefox.extensions.profile
import co.akoot.plugins.bluefox.extensions.touch
import co.akoot.plugins.bluefox.util.Color
import co.akoot.plugins.bluefox.util.Text.Companion.plus
import co.akoot.plugins.bluefox.util.async
import co.akoot.plugins.bluefox.util.getColor
import co.akoot.plugins.bluefox.util.hover
import co.akoot.plugins.bluefox.util.or
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.bluefox.util.suggest
import co.akoot.plugins.yap.api.ConsequentialTitle
import co.akoot.plugins.yap.api.History
import co.akoot.plugins.yap.api.LinkTracker
import co.akoot.plugins.yap.api.RandomTitle
import co.akoot.plugins.yap.api.TimedTitle
import co.akoot.plugins.yap.api.Title
import co.akoot.plugins.yap.api.TitleRarity
import co.akoot.plugins.yap.api.TitleType
import co.akoot.plugins.yap.commands.DiscordLinkCommand
import co.akoot.plugins.yap.commands.ChatThemeCommand
import co.akoot.plugins.yap.commands.LinkCommand
import co.akoot.plugins.yap.commands.NickCommand
import co.akoot.plugins.yap.commands.PrintCommand
import co.akoot.plugins.yap.commands.RegardingCommand
import co.akoot.plugins.yap.commands.TitleCommand
import co.akoot.plugins.yap.listeners.ChatListener
import co.akoot.plugins.yap.listeners.DiscordListener
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.requests.GatewayIntent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class Yap : FoxPlugin("yap") {

    companion object {

        lateinit var instance: Yap
        lateinit var auth: FoxConfig
        lateinit var titles: FoxConfig

        lateinit var history: History

        val linkTracker = LinkTracker()
    }

    val chatThemes = registerConfig("themes")

    var titles: List<Title> = listOf()

    private fun getTitles(config: FoxConfig, type: TitleType): List<Title> {
        val titles = mutableListOf<Title>()
        config.apply {
            val root = type.name.lowercase()
            for (titleId in getKeys(root)) {
                val key = "$root.$titleId"
                val titleName = getString("$key.name") ?: continue
                val discordId = getLong("$key.discord") ?: 0
                val alt = getString("$key.alt")
                val title = when(type) {
                    TitleType.CONSEQUENTIAL -> {
                        ConsequentialTitle(titleId, titleName, discordId, getString("$key.reason") ?: "Unknown", alt)
                    }
                    TitleType.TIMED -> {
                        val playtime = getInt("$key.playtime") ?: continue
                        val days = getInt("$key.days") ?: continue
                        TimedTitle(titleId, titleName, discordId, playtime, days, alt)
                    }
                    TitleType.RANDOM -> {
                        val rarity = getEnum<TitleRarity>("$key.rarity") ?: continue
                        RandomTitle(titleId, titleName, discordId, rarity, alt)
                    }
                    else -> Title(titleId, titleName, discordId, alt)
                }
                titles.add(title)
            }
        }
        logger.info("Loaded ${titles.size} titles.")
        return titles
    }

    fun getTitle(id: String): Title? {
        return titles.find { it.id == id }
    }

    val defaultChatFormat = "{bracketColor}[{title}{bracketColor}] [{nick}]({name}) "
    fun getChatThemeFormat(name: String): String? {
        return chatThemes.getString(name)
    }

    fun randomTitle(luck: Double = 0.0): Title? {
        val roll = Math.random() - luck.coerceIn(0.0, 1.0)
        val rarity =
            if(roll <= 0.05) TitleRarity.LEGENDARY
            else if(roll <= 0.10) TitleRarity.RARE
            else if(roll <= 0.25) TitleRarity.UNCOMMON
            else TitleRarity.COMMON
        return titles.filter { it is RandomTitle && it.rarity == rarity }.randomOrNull()
    }

    override fun registerConfigs() {
        auth = registerConfig("auth")
        Yap.titles = registerConfig("titles") { config ->
            val allTitles = mutableListOf<Title>()
            TitleType.entries.forEach { type ->
                allTitles.addAll(getTitles(config, type))
            }
            titles = allTitles
            logger.info("Loaded ${titles.size} titles.")
        }
    }

    override fun load() {
        instance = this
        BlueFox.jda?.addEventListener(DiscordListener(this))
        history = History(dataFolder.resolve("history.txt").touch())
    }

    override fun unload() {
    }

    override fun registerEvents() {
        registerEventListener(ChatListener(this))
    }

    override fun registerCommands() {
        registerCommand(PrintCommand(this))
        registerCommand(ChatThemeCommand(this))
        registerCommand(NickCommand(this))
        registerCommand(TitleCommand)
        registerCommand(RegardingCommand)
        registerCommand(LinkCommand)
        DiscordLinkCommand { discordId, username ->
            val player = BlueFox.getOfflinePlayer(username) ?: return@DiscordLinkCommand "Player not found!"
            val result = linkTracker.confirm(player.uniqueId, discordId, LinkTracker.Platform.DISCORD)
            when (result) {
                LinkTracker.Result.WAITING ->
                    "Now run /link ${player.name} on Minecraft to finish linking!"

                LinkTracker.Result.LINKED ->
                    "Your Minecraft and Discord accounts are now linked!"
            }
        }.register()

    }

    fun broadcast(message: String, viewers: Collection<Audience> = server.onlinePlayers, discordMessage: String? = null, discordEmbed: MessageEmbed? = null, author: Player? = null) = broadcast(message.parse(), viewers)

    fun broadcast(component: Component, viewers: Collection<Audience> = server.onlinePlayers, discordMessage: String? = null, discordEmbed: MessageEmbed? = null, author: Player? = null) {
        val index = if(author != null) {
            history.addMessage(author.profile.parseTheme(defaultChatFormat).append(component))
        } else {
            history.addMessage(component)
        }
        viewers.forEach {
            val message = if(it is Player) {
                if(author != null) {
                    renderChatMessage(
                        author,
                        author.displayName(),
                        component,
                        it
                    )
                } else {
                    it.profile.tint(component)
                }
            } else {
                component
            }

            it.sendMessage(message.clickEvent(suggest("/re $index "))
                .hover("Reply"))
        }
        //todo: discord stuff
    }

    fun renderChatMessage(
        source: Player,
        sourceDisplayName: Component,
        message: Component,
        viewer: Audience
    ): Component {
        return if (viewer is Player) {
            val tint = viewer.profile.globalChatTint or viewer.profile.chatTint or source.profile.chatTint
            val tintIntensity =
                viewer.profile.globalChatTintIntensity or viewer.profile.chatTintIntensity or source.profile.chatTintIntensity
            val format = viewer.profile.globalChatFormat or viewer.profile.chatFormat or source.profile.chatFormat
            viewer.profile.parseTheme(
                format = format or defaultChatFormat,
                tint = getColor(tint) ?: Color.Month,
                tintIntensity = tintIntensity or 0.1,
                profile = source.profile
            ) + message
        } else {
            Component.text("[") + sourceDisplayName + "] " + message
        }
    }
}