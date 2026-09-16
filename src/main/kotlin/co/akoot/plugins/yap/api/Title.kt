package co.akoot.plugins.yap.api

open class Title(val id: String, val name: String, val discordId: Long, val alt: String? = null)
class TimedTitle(id: String, name: String, discordId: Long, val playtime: Int, val days: Int, alt: String? = null): Title(id, name, discordId, alt)
class ConsequentialTitle(id: String, name: String, discordId: Long, val reason: String, alt: String? = null): Title(id, name, discordId, alt)
class RandomTitle(id: String, name: String, discordId: Long, val rarity: TitleRarity, alt: String? = null): Title(id, name, discordId, alt)