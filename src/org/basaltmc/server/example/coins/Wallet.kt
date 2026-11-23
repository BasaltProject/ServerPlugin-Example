package org.basaltmc.server.example.coins

import org.basaltmc.server.core.Player
import org.basaltmc.server.data.dataGetI64
import org.basaltmc.server.data.dataSetI64

object Wallet {
    fun get(player: Player): Long = dataGetI64(player, "coins", 0)

    fun add(player: Player, amount: Long): Long {
        val balance = get(player) + amount
        dataSetI64(player, "coins", balance)
        return balance
    }

    fun take(player: Player, amount: Long): Boolean {
        val balance = get(player)
        if (balance < amount) return false
        dataSetI64(player, "coins", balance - amount)
        return true
    }
}
