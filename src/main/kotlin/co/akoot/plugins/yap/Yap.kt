package co.akoot.plugins.yap

import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.util.async
import co.akoot.plugins.yap.api.ConsequentialTitle
import co.akoot.plugins.yap.api.RandomTitle
import co.akoot.plugins.yap.api.TimedTitle
import co.akoot.plugins.yap.api.Title
import co.akoot.plugins.yap.api.TitleRarity
import co.akoot.plugins.yap.api.TitleType
import co.akoot.plugins.yap.commands.ChatThemeCommand
import co.akoot.plugins.yap.commands.NickCommand
import co.akoot.plugins.yap.commands.PrintCommand
import co.akoot.plugins.yap.commands.TitleCommand
import co.akoot.plugins.yap.listeners.ChatListener
import co.akoot.plugins.yap.listeners.DiscordListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.requests.GatewayIntent

class Yap : FoxPlugin("yap") {

    companion object {

        lateinit var instance: Yap
        lateinit var auth: FoxConfig
        lateinit var titles: FoxConfig

        var jda: JDA? = null
        var guild: Guild? = null
        val channels: MutableMap<String, TextChannel> = mutableMapOf()

        fun sendDiscordMessage(message: String, channel: String = "minecraft") {
            val channel = channels[channel] ?: return
            channel.sendMessage(message).queue()
        }

        fun sendEmbed(embed: MessageEmbed, vararg other: MessageEmbed, channel: String = "minecraft") {
            val channel = channels[channel] ?: return
            channel.sendMessageEmbeds(embed, *other).queue()
        }

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

    private fun getJDA(): JDA? {
        val token = auth.getString("discord.token")
        if (token == null) {
            logger.severe("Invalid Discord Token in auth.conf, Discord features disabled.")
            return null
        }
        val builder = JDABuilder.createDefault(token)
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS)
        return try {
            builder.build()
        } catch (e: Exception) {
            logger.severe("Could not load JDA, Discord features disabled.")
            e.printStackTrace()
            null
        }
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
        jda = getJDA()
        async {
            jda?.awaitReady()
            guild = settings.getLong("discord.guildId")?.let { jda?.getGuildById(it) }
            guild ?: logger.severe("Invalid Guild ID in settings.conf, Discord features disabled.")
            guild?.textChannels?.forEach {
                channels += it.name to it
            }
        }
        jda?.addEventListener(DiscordListener())
        Yap.titles.load()
    }

    override fun unload() {
        jda = null
        guild = null
        channels.clear()
    }

    override fun registerEvents() {
        registerEventListener(ChatListener(this))
    }

    override fun registerCommands() {
        registerCommand(PrintCommand(this))
        registerCommand(ChatThemeCommand(this))
        registerCommand(NickCommand(this))
        //registerCommand(TitleCommand)
    }
}