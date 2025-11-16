package co.akoot.plugins.yap.listeners

import co.akoot.plugins.bluefox.extensions.getPDC
import co.akoot.plugins.bluefox.extensions.getPDCList
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.Text.Companion.plus
import co.akoot.plugins.yap.Yap
import co.akoot.plugins.yap.extensions.isMuting
import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.UUID

class ChatListener(private val plugin: Yap): Listener, ChatRenderer {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onAsyncChat(event: AsyncChatEvent) {
        val sender = event.player
        val message = event.signedMessage().message()
        for(viewer in event.viewers()) {
            val player = viewer as? Player ?: break
            if(player.isMuting(sender)) {
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
        return Component.text("[") + sourceDisplayName + "] " + message
    }
}