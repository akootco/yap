package co.akoot.plugins.yap.extensions

import co.akoot.plugins.bluefox.extensions.legacyConfig
import net.dv8tion.jda.api.entities.Member
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

fun Player.isMuting(offlinePlayer: OfflinePlayer): Boolean = this.legacyConfig.getUUIDList("chat.mutedPlayers").contains(offlinePlayer.uniqueId)
fun Player.isBlocking(member: Member): Boolean = this.legacyConfig.getLongList("chat.mutedGuildMembers").contains(member.idLong)

fun Player.mute(offlinePlayer: OfflinePlayer) = this.legacyConfig.append("chat.mutedPlayers", offlinePlayer.uniqueId)
fun Player.block(member: Member) = this.legacyConfig.append("chat.mutedGuildMembers", member.idLong)

fun Player.unmute(offlinePlayer: OfflinePlayer) = this.legacyConfig.remove("chat.mutedPlayers", offlinePlayer.uniqueId)
fun Player.unblock(member: Member) = this.legacyConfig.remove("chat.mutedGuildMembers", member.idLong)
