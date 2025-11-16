package co.akoot.plugins.yap.extensions

import co.akoot.plugins.bluefox.extensions.config
import net.dv8tion.jda.api.entities.Member
import net.kyori.adventure.audience.Audience
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

fun Player.isMuting(offlinePlayer: OfflinePlayer): Boolean = this.config?.getUUIDList("chat.mutedPlayers")?.contains(offlinePlayer.uniqueId) == true
fun Player.isBlocking(member: Member): Boolean = this.config?.getLongList("chat.mutedGuildMembers")?.contains(member.idLong) == true

fun Player.mute(offlinePlayer: OfflinePlayer) = this.config?.append("chat.mutedPlayers", offlinePlayer.uniqueId)
fun Player.block(member: Member) = this.config?.append("chat.mutedGuildMembers", member.idLong)

fun Player.unmute(offlinePlayer: OfflinePlayer) = this.config?.remove("chat.mutedPlayers", offlinePlayer.uniqueId)
fun Player.unblock(member: Member) = this.config?.remove("chat.mutedGuildMembers", member.idLong)
