package co.akoot.plugins.yap.commands

import co.akoot.plugins.bluefox.api.CatCommand
import co.akoot.plugins.bluefox.util.parse
import co.akoot.plugins.bluefox.util.sendText
import co.akoot.plugins.yap.Yap
import co.akoot.plugins.yap.api.RandomTitle

object TitleCommand: CatCommand(Yap.instance, "title", "Set your title!", onCommand = {

    noargs {
        it.sender.sendText("[Titles]\n", Yap.instance.titles.joinToString("\n") { title -> title.name })
    }

    then {
        string("title", suggestions = { _, builder -> suggest(builder, Yap.instance.titles.map { it.id })}) {
            val title = Yap.instance.getTitle(it.string("title")) ?: return@string false
            it.sender.sendText("Set title to ", title.name.parse())
        }
    }

    then {
        subcommand("random") {
            val title = Yap.instance.randomTitle() as? RandomTitle ?: return@subcommand false
            it.sender.sendText("Set title to ", title.name.parse())
            it.sender.sendText("It was ", title.rarity.display.parse())
        }
    }

})