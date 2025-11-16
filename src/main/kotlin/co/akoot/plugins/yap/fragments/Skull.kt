package co.akoot.plugins.yap.fragments

import co.akoot.plugins.bluefox.util.Text.Companion.titleCase
import net.kyori.adventure.text.format.ShadowColor
import net.kyori.adventure.text.format.TextColor

class Skull(
    id: String,
    displayName: String = id.titleCase,
    description: String? = null,
    color: Pair<TextColor?, ShadowColor?>? = null,
): Fragment(id, displayName, description, color)