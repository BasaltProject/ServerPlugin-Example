package org.basaltmc.server.example.shop

import org.basaltmc.server.config.configI64
import org.basaltmc.server.core.Args
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.core.Handler
import org.basaltmc.server.core.Player
import org.basaltmc.server.example.coins.Wallet
import org.basaltmc.server.menus.menuClickPlayer
import org.basaltmc.server.menus.menuCreate
import org.basaltmc.server.menus.menuSet
import org.basaltmc.server.players.particle
import org.basaltmc.server.players.send
import org.basaltmc.server.players.sound
import org.basaltmc.server.text.translate

object Storefront {
    private const val SPARKLES_SLOT = 11
    private const val FIREWORKS_SLOT = 15

    var menu = 0
        private set

    private fun buy(player: Player, key: String, price: Long): Boolean {
        if (Wallet.take(player, price)) {
            val item = translate(key) ?: ""
            send(player, "@shop-bought", Args().add("item", item).add("price", price.toString()))
            return true
        }
        send(player, "@shop-cant-afford", Args().add("price", price.toString()).add("amount", Wallet.get(player).toString()))
        return false
    }

    private fun buySparkles(ctx: Ctx) {
        val player = menuClickPlayer(ctx)
        if (buy(player, "shop-item-sparkles", configI64("shop.sparkles-price", 100))) {
            particle(player, "minecraft:totem_of_undying", 30, 0.4)
        }
    }

    private fun buyFireworks(ctx: Ctx) {
        val player = menuClickPlayer(ctx)
        if (buy(player, "shop-item-fireworks", configI64("shop.fireworks-price", 150))) {
            sound(player, "minecraft:entity.firework_rocket.launch", 1.0f, 1.0f)
        }
    }

    private fun addItem(slot: Int, item: String, nameKey: String, loreKey: String, price: Long, onClick: Handler) {
        val name = translate(nameKey) ?: ""
        val lore = translate(loreKey, Args().add("price", price.toString())) ?: ""
        menuSet(menu, slot, item, name, lore, onClick)
    }

    fun build() {
        menu = menuCreate(translate("shop-title") ?: "", 3)
        addItem(SPARKLES_SLOT, "minecraft:blaze_powder", "shop-item-sparkles", "shop-item-sparkles-lore",
            configI64("shop.sparkles-price", 100), ::buySparkles)
        addItem(FIREWORKS_SLOT, "minecraft:firework_rocket", "shop-item-fireworks", "shop-item-fireworks-lore",
            configI64("shop.fireworks-price", 150), ::buyFireworks)
    }
}
