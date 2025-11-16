package co.akoot.plugins.yap

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.Audiences

enum class Context {
    GLOBAL, WORLD, GROUP, SYSTEM, INFO;
    var audience: Array<out Audience> = emptyArray()
    fun with(vararg audience: Audience): Context {
        this.audience = audience
        return this
    }
}