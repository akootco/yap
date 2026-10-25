package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.BlueFox
import co.akoot.plugins.bluefox.util.sync
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

class DiscordUnlinkCommand(
    private val unlink: (discordId: Long) -> String
) : ListenerAdapter() {

    fun register() {
        BlueFox.guild?.upsertCommand(
            Commands.slash("unlink", "Unlink your Minecraft account!")
        )?.queue()

        BlueFox.jda?.addEventListener(this)
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != "unlink") return

        event.deferReply(true).queue { hook ->
            sync {
                runCatching { unlink(event.user.idLong) }
                    .onSuccess { hook.editOriginal(it).queue() }
                    .onFailure {
                        it.printStackTrace()
                        hook.editOriginal("Couldn't unlink your account. Please try again.").queue()
                    }
            }
        }
    }
}