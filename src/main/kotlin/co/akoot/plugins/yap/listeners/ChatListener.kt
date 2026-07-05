package co.akoot.plugins.yap.listeners

import co.akoot.plugins.bluefox.extensions.or
import co.akoot.plugins.bluefox.extensions.profile
import co.akoot.plugins.bluefox.util.Color
import co.akoot.plugins.bluefox.util.Text.Companion.plus
import co.akoot.plugins.bluefox.util.getColor
import co.akoot.plugins.bluefox.util.or
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.yap.Yap
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ChatListener(private val plugin: Yap) : Listener, ChatRenderer {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onAsyncChat(event: AsyncChatEvent) {
        val sender = event.player
        val message = event.signedMessage().message()
        event.message(message.parse())
        for (viewer in event.viewers()) {
            val player = viewer as? Player ?: break
            if (player.profile.isIgnoring(sender)) {
                event.viewers().remove(player)
            }
        }
        event.renderer(this)
    }

    override fun render(
        source: Player,
        sourceDisplayName: Component,
        message: Component,
        viewer: Audience
    ): Component {
        return if (viewer is Player) {
            val tint = viewer.profile.universalChatTint or viewer.profile.chatTint or source.profile.chatTint
            val tintIntensity =
                viewer.profile.universalChatTintIntensity or viewer.profile.chatTintIntensity or source.profile.chatTintIntensity
            val format = viewer.profile.universalChatFormat or viewer.profile.chatFormat or source.profile.chatFormat
            viewer.profile.parseTheme(
                format = format or plugin.defaultChatFormat,
                tint = getColor(tint) ?: Color.Month,
                tintIntensity = tintIntensity or 0.1,
                profile = source.profile
            ) + message
        } else {
            Component.text("[") + sourceDisplayName + "] " + message
        }
    }
}