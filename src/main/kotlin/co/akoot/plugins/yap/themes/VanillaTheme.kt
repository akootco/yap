package co.akoot.plugins.yap.themes

import net.kyori.adventure.text.Component

class VanillaTheme: ChatTheme(
    id = "vanilla",
    name = "Vanilla",
    description = "",
    showRole = false,
    showTitle = false,
    showNickname = false,
    namePrefix = Component.text("<"),
    namePostFix = Component.text("> "),
    showHeart = false,
    showSkull = false,
    showStatus = false
)