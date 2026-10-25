package co.akoot.plugins.yap.listeners

import co.akoot.plugins.bluefox.BlueFox
import co.akoot.plugins.bluefox.extensions.profile
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.yap.Yap
import co.akoot.plugins.yap.extensions.asEmbed
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.audit.ActionType
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.time.Duration
import java.time.Instant

class DiscordListener(val plugin: Yap) : ListenerAdapter() {
    private val auditLookBack = Duration.ofSeconds(10)

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val user = event.user
        event.guild.retrieveAuditLogs()
            .limit(5)
            .queue { entries ->
                val now = Instant.now()
                val entry = entries.firstOrNull { entry ->
                    entry.targetIdLong == user.idLong &&
                            Duration.between(entry.timeCreated.toInstant(), now) < auditLookBack
                }
                val moderator = entry?.user

                val embed = when (entry?.type) {
                    ActionType.KICK -> {
                        EmbedBuilder().apply {
                            setAuthor(
                                "Good riddance, ${user.effectiveName} was kicked!",
                                null,
                                moderator?.effectiveAvatarUrl
                            )
                            setImage(user.effectiveAvatarUrl)
                            setDescription("Thank you ${moderator?.effectiveName ?: "Anonymous"}, I sure hope @${user.name} learns their lesson!")
                            setFooter(entry.reason?.let { "\"$it\" - @${moderator?.name ?: "anonymous"}" })
                            setColor(0xfc6305)
                        }.build()

                    }

                    ActionType.BAN -> {
                        EmbedBuilder().apply {
                            setAuthor(
                                "And stay out! ${user.effectiveName} was BANNED!",
                                null,
                                moderator?.effectiveAvatarUrl
                            )
                            setImage(user.effectiveAvatarUrl)
                            setDescription("Thank you ${moderator?.effectiveName ?: "Anonymous"}, may that vagabond @${user.name} find a dumpster to lay in, away from here!")
                            setFooter(entry.reason?.let { "\"$it\" - @${moderator?.name ?: "anonymous"}" })
                            setColor(0xf22602)
                        }.build()
                    }

                    else -> {
                        event.user.asEmbed(
                            "This is so sad, ${user.effectiveName} has left us...",
                            "Rest in peace, @${user.name}. *You may be missed...*",
                            smallPic = false,
                            color = Color(
                                0x487587
                            )
                        )
                    }
                }
                BlueFox.instance.sendEmbed(embed, channel = "logs")
            }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val authorId = event.author.idLong
        val message = event.message.contentDisplay
        if (event.channel != BlueFox.channels["minecraft"]) return
        val viewers = plugin.server.onlinePlayers.filterNot {
            it.profile.getDiscordId() == authorId || authorId in it.profile.ignoredDiscordUsers
        }
        plugin.broadcast(message.parse(), viewers = viewers)
    }

    override fun onReady(event: ReadyEvent) {
        BlueFox.channels["minecraft"]?.sendMessageEmbeds(
            BlueFox.instance.embed(
                "Server Started!",
                0xfbbf3a,
                "https://em-content.zobj.net/source/apple/453/sun_2600-fe0f.png"
            )
        )
    }

    override fun onSessionDisconnect(event: SessionDisconnectEvent) {
        BlueFox.channels["minecraft"]?.sendMessageEmbeds(
            BlueFox.instance.embed(
                "Session Disconnected!",
                0x0,
                "https://em-content.zobj.net/source/google/481/electric-plug_1f50c.png"
            )
        )
    }

    override fun onChannelCreate(event: ChannelCreateEvent) {
        if (event.channelType != ChannelType.TEXT) return
        BlueFox.channels += event.channel.name to event.channel.asTextChannel()
    }

    override fun onChannelDelete(event: ChannelDeleteEvent) {
        if (event.channelType != ChannelType.TEXT) return
        BlueFox.channels -= event.channel.name
    }
}