package co.akoot.plugins.yap.fragments

import co.akoot.plugins.bluefox.util.Text.Companion.titleCase
import net.kyori.adventure.text.format.ShadowColor
import net.kyori.adventure.text.format.TextColor

class Title(
    id: String,
    displayName: String = id.titleCase,
    description: String? = null,
    val colors: List<Pair<TextColor, ShadowColor?>>,
): Fragment(id, displayName, description)