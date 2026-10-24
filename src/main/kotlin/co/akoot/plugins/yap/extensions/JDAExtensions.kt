package co.akoot.plugins.yap.extensions

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import java.util.concurrent.CompletableFuture

val User.names: String get() = "**$effectiveName**" + if (name == effectiveName) "" else " (@${name})"
fun User.asEmbed(
    title: String,
    description: String? = null,
    footer: String? = null,
    color: Color = Color(0x7289DA),
    smallPic: Boolean = true
): MessageEmbed {
    return EmbedBuilder().apply {
        if (smallPic) setAuthor(title, null, effectiveAvatarUrl) else setImage(effectiveAvatarUrl)
        if (!smallPic) setTitle(title)
        setColor(color)
        setDescription(description)
        setFooter(footer)
    }.build()
}

val User.username: String get() = asTag.substringBeforeLast('#')

fun Guild.getMember(username: String): CompletableFuture<Member?> {
    val name = username.trim().removePrefix("@")
    val future = CompletableFuture<Member?>()

    if (name.isBlank()) {
        future.complete(null)
        return future
    }

    retrieveMembersByPrefix(name, 100)
        .onSuccess { members ->
            val match = members.firstOrNull {
                it.user.name.equals(name, ignoreCase = true)
            }

            when {
                match != null -> future.complete(match)

                members.size >= 100 -> future.completeExceptionally(
                    IllegalStateException(
                        "Member search reached its result limit without an exact match."
                    )
                )

                else -> future.complete(null)
            }
        }
        .onError { future.completeExceptionally(it) }

    return future
}