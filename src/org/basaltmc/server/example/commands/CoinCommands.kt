package org.basaltmc.server.example.commands

import org.basaltmc.server.commands.command
import org.basaltmc.server.commands.commandArgI64
import org.basaltmc.server.commands.commandArgStr
import org.basaltmc.server.commands.commandIsConsole
import org.basaltmc.server.commands.commandReply
import org.basaltmc.server.commands.commandSourceName
import org.basaltmc.server.core.Args
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.data.storeTop
import org.basaltmc.server.example.coins.Wallet
import org.basaltmc.server.example.shop.Storefront
import org.basaltmc.server.menus.menuOpen
import org.basaltmc.server.players.playerByName
import org.basaltmc.server.players.playerName

object CoinCommands {
    private fun balance(ctx: Ctx) {
        val target = commandArgStr(ctx, "target")
        if (target != null) {
            val player = playerByName(target)
            if (player == 0) {
                commandReply(ctx, "@balance-usage")
                return
            }
            val name = playerName(player) ?: ""
            commandReply(ctx, "@balance-other", Args().add("name", name).add("amount", Wallet.get(player).toString()))
            return
        }
        if (commandIsConsole(ctx)) {
            commandReply(ctx, "@balance-usage")
            return
        }
        val player = playerByName(commandSourceName(ctx) ?: "")
        commandReply(ctx, "@balance-self", Args().add("amount", Wallet.get(player).toString()))
    }

    private fun top(ctx: Ctx) {
        val limit = commandArgI64(ctx, "limit") ?: 10L
        val rows = storeTop("coins", limit.toInt())
        if (rows.isEmpty()) {
            commandReply(ctx, "@top-empty")
            return
        }
        commandReply(ctx, "@top-header")
        for ((index, row) in rows.withIndex()) {
            val args = Args().add("rank", (index + 1).toString()).add("name", row.name).add("amount", row.value.toString())
            commandReply(ctx, "@top-row", args)
        }
    }

    private fun openShop(ctx: Ctx) {
        if (commandIsConsole(ctx)) {
            commandReply(ctx, "@shop-players-only")
            return
        }
        menuOpen(Storefront.menu, playerByName(commandSourceName(ctx) ?: ""))
    }

    fun register() {
        command("coins [target: word]", "", ::balance)
        command("coins top [limit: int 1..25]", "", ::top)
        command("shop", "example.shop", ::openShop)
    }
}
