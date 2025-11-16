package co.akoot.plugins.yap.components

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.OfflinePlayer

class Status(symbol: String, color: TextColor? = null) : ChatComponent("symbol", TextColor.color(0xf7f6ef)) {
    override fun render(offlinePlayer: OfflinePlayer, viewer: Audience): Component {
        return super.render(offlinePlayer, viewer)
    }
}