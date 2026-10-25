package co.akoot.plugins.yap.listeners

import co.akoot.plugins.bluefox.extensions.profile
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.yap.Yap
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ChatListener(private val plugin: Yap) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onAsyncChat(event: AsyncChatEvent) {
        val sender = event.player
        val message = event.signedMessage().message()
        val component = message.parse()

        for (viewer in event.viewers()) {
            val player = viewer as? Player ?: break
            if (player.profile.isIgnoring(sender)) {
                event.viewers().remove(player)
            }
        }

        plugin.broadcast(component, event.viewers(), author = sender)
        event.viewers().clear()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage()?.let { plugin.broadcast(it) }
        event.deathMessage(null)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage()?.let { plugin.broadcast(it) }
        event.joinMessage(null)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage()?.let { plugin.broadcast(it) }
        event.quitMessage(null)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerAdvancementDone(event: PlayerAdvancementDoneEvent) {
        event.message()?.let { plugin.broadcast(it) }
        event.message(null)
    }
}