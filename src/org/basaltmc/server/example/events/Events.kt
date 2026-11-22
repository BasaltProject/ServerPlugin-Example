package org.basaltmc.server.example.events

import org.basaltmc.server.example.coins.Coins
import org.basaltmc.server.core.Args
import org.basaltmc.server.core.Ctx

object Events {
    private fun onJoin(ctx: Ctx) {
        val player = org.basaltmc.server.events.eventPlayer(ctx)
        if (player == 0) return
        val name = org.basaltmc.server.players.playerName(player) ?: ""
        if (!org.basaltmc.server.data.dataGetBool(player, "welcomed", false)) {
            org.basaltmc.server.data.dataSetBool(player, "welcomed", true)
            val bonus = org.basaltmc.server.config.configI64("welcome-bonus", 50)
            Coins.add(player, bonus)
            org.basaltmc.server.players.send(player, "@welcome", Args().add("name", name).add("amount", bonus.toString()))
        } else {
            org.basaltmc.server.players.send(player, "@welcome-back", Args().add("name", name).add("amount", Coins.get(player).toString()))
        }
    }

    private fun payout(ctx: Ctx) {
        val amount = org.basaltmc.server.config.configI64("payout-amount", 5)
        for (player in org.basaltmc.server.players.playersOnline()) {
            val balance = Coins.add(player, amount)
            org.basaltmc.server.players.actionBar(player, "@payout", Args().add("amount", amount.toString()).add("balance", balance.toString()))
        }
    }

    private fun coinsPlaceholder(ctx: Ctx) {
        val player = org.basaltmc.server.menus.placeholderPlayer(ctx)
        org.basaltmc.server.menus.placeholderResult(ctx, if (player == 0) "0" else Coins.get(player).toString())
    }

    fun register() {
        org.basaltmc.server.events.on("player.join", org.basaltmc.server.core.Priority.NORMAL, ::onJoin)
        // Delayed by its own period, so a payout does not stack with the welcome bonus.
        val period = org.basaltmc.server.config.configI64("payout-period", 1200).toInt()
        org.basaltmc.server.tasks.every(period, period, ::payout)
        org.basaltmc.server.menus.placeholder("coins", ::coinsPlaceholder)
    }
}
