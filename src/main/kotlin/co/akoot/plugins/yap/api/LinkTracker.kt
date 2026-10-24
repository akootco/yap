package co.akoot.plugins.yap.api

import co.akoot.plugins.bluefox.extensions.profile
import org.bukkit.Bukkit
import java.time.Duration
import java.util.UUID

class LinkTracker(
    private val timeout: Duration = Duration.ofMinutes(10)
) {
    enum class Platform {
        MINECRAFT,
        DISCORD
    }

    enum class Result {
        WAITING,
        LINKED
    }

    private data class AccountPair(
        val minecraftUuid: UUID,
        val discordId: Long
    )

    private data class PendingLink(
        val platform: Platform,
        val createdAt: Long = System.nanoTime()
    )

    private val pending = mutableMapOf<AccountPair, PendingLink>()

    fun confirm(
        minecraftUuid: UUID,
        discordId: Long,
        platform: Platform
    ): Result {
        check(Bukkit.isPrimaryThread()) {
            "LinkTracker.confirm must run on the server thread"
        }

        val now = System.nanoTime()

        pending.entries.removeIf {
            now - it.value.createdAt >= timeout.toNanos()
        }

        val pair = AccountPair(minecraftUuid, discordId)
        val existing = pending[pair]

        if (existing == null) {
            pending[pair] = PendingLink(platform)
            return Result.WAITING
        }

        // Repeating the command on one platform cannot confirm both sides.
        if (existing.platform == platform) {
            return Result.WAITING
        }

        Bukkit.getOfflinePlayer(minecraftUuid)
            .profile.setDiscordId(discordId)

        // Clear other pending requests involving either account.
        pending.keys.removeIf {
            it.minecraftUuid == minecraftUuid || it.discordId == discordId
        }

        return Result.LINKED
    }
}