package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.BlueFox
import co.akoot.plugins.bluefox.util.sync
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

class DiscordLinkCommand(
    private val link: (member: User, username: String) -> String
) : ListenerAdapter() {

    fun register() {
        BlueFox.guild?.upsertCommand(
            Commands.slash("link", "Link your Minecraft account!")
                .addOption(
                    OptionType.STRING,
                    "username",
                    "Your Minecraft username",
                    true
                )
        )?.queue()

        BlueFox.jda?.addEventListener(this)
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != "link") return

        val username = event.getOption("username")!!.asString

        event.deferReply(true).queue { hook ->
            sync {
                runCatching { link(event.user, username) }
                    .onSuccess { hook.editOriginal(it).queue() }
                    .onFailure {
                        it.printStackTrace()
                        hook.editOriginal("Couldn't link your account. Please try again.").queue()
                    }
            }
        }
    }
}