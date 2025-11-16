package co.akoot.plugins.yap.components

import co.akoot.plugins.bluefox.extensions.mix
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

abstract class ChatComponent(val symbol: String, val color: TextColor? = null) {
    open fun onHover(offlinePlayer: OfflinePlayer, viewer: Audience): Component? = null
    open fun onClick(offlinePlayer: OfflinePlayer, viewer: Audience): ClickEvent? = null
    open fun render(offlinePlayer: OfflinePlayer, viewer: Audience): Component {
        val component = Component.text(symbol).color(color).toBuilder()
        component.hoverEvent(onHover(offlinePlayer, viewer)?.let { HoverEvent.showText(it) })
        component.clickEvent(onClick(offlinePlayer, viewer))
        return component.build()
    }
}