package co.akoot.plugins.yap

import co.akoot.plugins.bluefox.api.FoxPlugin

class Yap : FoxPlugin("yap") {

    companion object {
        lateinit var instance: Yap
    }

    override fun load() {
        instance = this
    }

    override fun unload() {
    }

    override fun registerEvents() {
    }
}