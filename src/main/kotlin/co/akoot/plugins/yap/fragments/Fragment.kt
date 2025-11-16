package co.akoot.plugins.yap.fragments

import co.akoot.plugins.bluefox.extensions.mix
import co.akoot.plugins.bluefox.util.Text
import co.akoot.plugins.bluefox.util.Text.Companion.titleCase
import net.kyori.adventure.text.format.ShadowColor
import net.kyori.adventure.text.format.TextColor

open class Fragment(
    val id: String,
    val displayName: String = id.titleCase,
    val description: String? = null,
    val color: Pair<TextColor?, ShadowColor?>? = null,
) {
    open fun render(tint: Pair<TextColor?, ShadowColor?>? = null) = Text(displayName, color?.first?.mix(tint?.first))
        .shadowColor(color?.second)
        .hover(description, color?.first)
}