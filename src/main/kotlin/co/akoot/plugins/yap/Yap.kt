package co.akoot.plugins.yap

import co.akoot.plugins.bluefox.BlueFox
import co.akoot.plugins.bluefox.api.FoxConfig
import co.akoot.plugins.bluefox.api.FoxPlugin
import co.akoot.plugins.bluefox.util.async
import co.akoot.plugins.yap.events.DiscordListener
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
            println("channels: $channels")
        }
        jda?.addEventListener(DiscordListener())
    }

    override fun unload() {
        jda = null
        guild = null
        channels.clear()
    }

    override fun registerEvents() {
    }
}