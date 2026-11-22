package coins

import bsCore "basaltmc.org/sdk/server/core"

import (
	bsData "basaltmc.org/sdk/server/data"
)

func Get(player bsCore.Player) int64 {
	return bsData.DataGetI64(player, "coins", 0)
}

func Add(player bsCore.Player, amount int64) int64 {
	balance := Get(player) + amount
	bsData.DataSetI64(player, "coins", balance)
	return balance
}

func Take(player bsCore.Player, amount int64) bool {
	balance := Get(player)
	if balance < amount {
		return false
	}
	bsData.DataSetI64(player, "coins", balance-amount)
	return true
}
