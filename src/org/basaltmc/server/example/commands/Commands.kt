package org.basaltmc.server.example.commands

import org.basaltmc.server.example.coins.Coins
import org.basaltmc.server.example.shop.Shop
import org.basaltmc.server.core.Args
import org.basaltmc.server.core.Ctx

object Commands {
    private fun balance(ctx: Ctx) {
        val target = org.basaltmc.server.commands.commandArgStr(ctx, "target")
        if (target != null) {
            val player = org.basaltmc.server.players.playerByName(target)
            if (player == 0) {
                org.basaltmc.server.commands.commandReply(ctx, "@balance-usage")
                return
            }
            val name = org.basaltmc.server.players.playerName(player) ?: ""
            org.basaltmc.server.commands.commandReply(ctx, "@balance-other", Args().add("name", name).add("amount", Coins.get(player).toString()))
            return
        }
        if (org.basaltmc.server.commands.commandIsConsole(ctx)) {
            org.basaltmc.server.commands.commandReply(ctx, "@balance-usage")
            return
        }
        val player = org.basaltmc.server.players.playerByName(org.basaltmc.server.commands.commandSourceName(ctx) ?: "")
        org.basaltmc.server.commands.commandReply(ctx, "@balance-self", Args().add("amount", Coins.get(player).toString()))
    }

    private fun top(ctx: Ctx) {
        val limit = org.basaltmc.server.commands.commandArgI64(ctx, "limit") ?: 10L
        val rows = org.basaltmc.server.data.storeTop("coins", limit.toInt())
        if (rows.isEmpty()) {
            org.basaltmc.server.commands.commandReply(ctx, "@top-empty")
            return
        }
        org.basaltmc.server.commands.commandReply(ctx, "@top-header")
        for ((index, row) in rows.withIndex()) {
            val args = Args().add("rank", (index + 1).toString()).add("name", row.name).add("amount", row.value.toString())
            org.basaltmc.server.commands.commandReply(ctx, "@top-row", args)
        }
    }

    private fun openShop(ctx: Ctx) {
        if (org.basaltmc.server.commands.commandIsConsole(ctx)) {
            org.basaltmc.server.commands.commandReply(ctx, "@shop-players-only")
            return
        }
        org.basaltmc.server.menus.menuOpen(Shop.menu, org.basaltmc.server.players.playerByName(org.basaltmc.server.commands.commandSourceName(ctx) ?: ""))
    }

    fun register() {
        org.basaltmc.server.commands.command("coins [target: word]", "", ::balance)
        org.basaltmc.server.commands.command("coins top [limit: int 1..25]", "", ::top)
        org.basaltmc.server.commands.command("shop", "example.shop", ::openShop)
    }
}
