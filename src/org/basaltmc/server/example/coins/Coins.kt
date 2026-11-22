package org.basaltmc.server.example.coins


object Coins {
    fun get(player: org.basaltmc.server.core.Player): Long = org.basaltmc.server.data.dataGetI64(player, "coins", 0)

    fun add(player: org.basaltmc.server.core.Player, amount: Long): Long {
        val balance = get(player) + amount
        org.basaltmc.server.data.dataSetI64(player, "coins", balance)
        return balance
    }

    fun take(player: org.basaltmc.server.core.Player, amount: Long): Boolean {
        val balance = get(player)
        if (balance < amount) return false
        org.basaltmc.server.data.dataSetI64(player, "coins", balance - amount)
        return true
    }
}
