package org.basaltmc.server.example.shop

import org.basaltmc.server.example.coins.Coins
import org.basaltmc.server.core.Args
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.core.Handler

object Shop {
    private const val SPARKLES_SLOT = 11
    private const val FIREWORKS_SLOT = 15

    var menu = 0
        private set

    private fun buy(player: org.basaltmc.server.core.Player, key: String, price: Long): Boolean {
        if (Coins.take(player, price)) {
            val item = org.basaltmc.server.text.translate(key) ?: ""
            org.basaltmc.server.players.send(player, "@shop-bought", Args().add("item", item).add("price", price.toString()))
            return true
        }
        org.basaltmc.server.players.send(player, "@shop-cant-afford", Args().add("price", price.toString()).add("amount", Coins.get(player).toString()))
        return false
    }

    private fun buySparkles(ctx: Ctx) {
        val player = org.basaltmc.server.menus.menuClickPlayer(ctx)
        if (buy(player, "shop-item-sparkles", org.basaltmc.server.config.configI64("shop.sparkles-price", 100))) {
            org.basaltmc.server.players.particle(player, "minecraft:totem_of_undying", 30, 0.4)
        }
    }

    private fun buyFireworks(ctx: Ctx) {
        val player = org.basaltmc.server.menus.menuClickPlayer(ctx)
        if (buy(player, "shop-item-fireworks", org.basaltmc.server.config.configI64("shop.fireworks-price", 150))) {
            org.basaltmc.server.players.sound(player, "minecraft:entity.firework_rocket.launch", 1.0f, 1.0f)
        }
    }

    private fun addItem(slot: Int, item: String, nameKey: String, loreKey: String, price: Long, onClick: Handler) {
        val name = org.basaltmc.server.text.translate(nameKey) ?: ""
        val lore = org.basaltmc.server.text.translate(loreKey, Args().add("price", price.toString())) ?: ""
        org.basaltmc.server.menus.menuSet(menu, slot, item, name, lore, onClick)
    }

    fun build() {
        menu = org.basaltmc.server.menus.menuCreate(org.basaltmc.server.text.translate("shop-title") ?: "", 3)
        addItem(SPARKLES_SLOT, "minecraft:blaze_powder", "shop-item-sparkles", "shop-item-sparkles-lore",
            org.basaltmc.server.config.configI64("shop.sparkles-price", 100), ::buySparkles)
        addItem(FIREWORKS_SLOT, "minecraft:firework_rocket", "shop-item-fireworks", "shop-item-fireworks-lore",
            org.basaltmc.server.config.configI64("shop.fireworks-price", 150), ::buyFireworks)
    }
}
