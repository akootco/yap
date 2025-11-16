package co.akoot.plugins.yap.themes

import net.kyori.adventure.text.Component

class VanillaPlusTheme: ChatTheme(
    id = "vanilla_plus",
    name = "Vanilla+",
    description = "",
    namePrefix = Component.text(" <"),
    namePostFix = Component.text("> ")
)