package co.akoot.plugins.yap.themes

import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.Text.Companion.titleCase
import co.akoot.plugins.yap.fragments.Heart
import co.akoot.plugins.yap.fragments.Role
import co.akoot.plugins.yap.fragments.Skull
import co.akoot.plugins.yap.fragments.Status
import co.akoot.plugins.yap.fragments.Title
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor

open class ChatTheme(
    val id: String,
    val name: String = id.titleCase,
    val description: String = "No description provided.",
    val author: String = "AkootCo",

    val builder: TextComponent.Builder = Component.text(),
    val prefix: Component? = null,

    val showRole: Boolean = true,
    val rolePrefix: Component? = null,
    val roleTint: TextColor? = null,
    val roleTintPercentage: Float = 0f,
    val rolePostfix: Component? = null,

    val showTitle: Boolean = true,
    val titlePrefix: Component? = Component.text(" ["),
    val titleTint: TextColor? = null,
    val titleTintPercentage: Float = 0f,
    val titlePostfix: Component? = Component.text("]"),

    val showNickname: Boolean = true,
    val namePrefix: Component? = Component.text(" "),
    val nameTint: TextColor? = null,
    val nameTintPercentage: Float = 0f,
    val namePostFix: Component? = Component.text(" "),

    val messagePrefix: Component? = null,
    val messageTint: TextColor? = null,
    val messageTintPercentage: Float = 0f,
    val messagePostfix: Component? = null,

    val showHeart: Boolean = true,
    val heartPrefix: Component? = null,
    val heartTint: TextColor? = null,
    val heartTintPercentage: Float = 0f,
    val heartPostfix: Component? = null,

    val showSkull: Boolean = true,
    val skullPrefix: Component? = null,
    val skullTint: TextColor? = null,
    val skullTintPercentage: Float = 0f,
    val skullPostfix: Component? = null,

    val showStatus: Boolean = true,
    val statusPrefix: Component? = null,
    val statusIdle: TextColor = TextColor.color(0xf7cc6f),
    val statusDnD: TextColor = TextColor.color(0xf76f88),
    val statusUnavailable: TextColor = TextColor.color(0xc9dbe0),
    val customStatusTint: TextColor? = null,
    val customStatusTintPercentage: Float = 0f,
    val statusPostfix: Component? = null,

    val postfix: Component? = null
) {
    fun renderMessage(username: String, message: String, role: Role?, title: Title?, displayName: Component?, skull: Skull?, heart: Heart?, status: Status): Component {
        prefix?.let { builder.append(it) }
        if(showRole && role != null) {
            rolePrefix?.let { builder.append(it) }
//            builder.append(role.render().color)
            rolePostfix?.let { builder.append(it) }
        }
        return builder.build()
    }
}