package org.basaltmc.server.example.events

import org.basaltmc.server.config.configI64
import org.basaltmc.server.core.Args
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.core.Priority
import org.basaltmc.server.data.dataGetBool
import org.basaltmc.server.data.dataSetBool
import org.basaltmc.server.events.eventPlayer
import org.basaltmc.server.events.on
import org.basaltmc.server.example.coins.Wallet
import org.basaltmc.server.menus.placeholder
import org.basaltmc.server.menus.placeholderPlayer
import org.basaltmc.server.menus.placeholderResult
import org.basaltmc.server.players.actionBar
import org.basaltmc.server.players.playerName
import org.basaltmc.server.players.playersOnline
import org.basaltmc.server.players.send
import org.basaltmc.server.tasks.every

object PlayerListeners {
    private fun onJoin(ctx: Ctx) {
        val player = eventPlayer(ctx)
        if (player == 0) return
        val name = playerName(player) ?: ""
        if (!dataGetBool(player, "welcomed", false)) {
            dataSetBool(player, "welcomed", true)
            val bonus = configI64("welcome-bonus", 50)
            Wallet.add(player, bonus)
            send(player, "@welcome", Args().add("name", name).add("amount", bonus.toString()))
        } else {
            send(player, "@welcome-back", Args().add("name", name).add("amount", Wallet.get(player).toString()))
        }
    }

    private fun payout(ctx: Ctx) {
        val amount = configI64("payout-amount", 5)
        for (player in playersOnline()) {
            val balance = Wallet.add(player, amount)
            actionBar(player, "@payout", Args().add("amount", amount.toString()).add("balance", balance.toString()))
        }
    }

    private fun coinsPlaceholder(ctx: Ctx) {
        val player = placeholderPlayer(ctx)
        placeholderResult(ctx, if (player == 0) "0" else Wallet.get(player).toString())
    }

    fun register() {
        on("player.join", Priority.NORMAL, ::onJoin)
        // Delayed by its own period, so a payout does not stack with the welcome bonus.
        val period = configI64("payout-period", 1200).toInt()
        every(period, period, ::payout)
        placeholder("coins", ::coinsPlaceholder)
    }
}
