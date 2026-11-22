package events

import (
	bsConfig "basaltmc.org/sdk/server/config"
	bsCore "basaltmc.org/sdk/server/core"
	bsData "basaltmc.org/sdk/server/data"
	bsEvents "basaltmc.org/sdk/server/events"
	bsMenus "basaltmc.org/sdk/server/menus"
	bsPlayers "basaltmc.org/sdk/server/players"
	bsTasks "basaltmc.org/sdk/server/tasks"
)

import appCoins "basalt.plugin/example/internal/coins"

import (
	"strconv"
)

func OnJoin(ctx bsCore.Ctx) {
	player := bsEvents.EventPlayer(ctx)
	if player == 0 {
		return
	}
	name, _ := bsPlayers.PlayerName(player)
	if !bsData.DataGetBool(player, "welcomed", false) {
		bsData.DataSetBool(player, "welcomed", true)
		bonus := bsConfig.ConfigI64("welcome-bonus", 50)
		appCoins.Add(player, bonus)
		bsPlayers.Send(player, "@welcome", bsCore.NewArgs().Add("name", name).Add("amount", strconv.FormatInt(bonus, 10)))
	} else {
		bsPlayers.Send(player, "@welcome-back", bsCore.NewArgs().Add("name", name).Add("amount", strconv.FormatInt(appCoins.Get(player), 10)))
	}
}

func Payout(bsCore.Ctx) {
	amount := bsConfig.ConfigI64("payout-amount", 5)
	for _, player := range bsPlayers.PlayersOnline() {
		balance := appCoins.Add(player, amount)
		args := bsCore.NewArgs().Add("amount", strconv.FormatInt(amount, 10)).Add("balance", strconv.FormatInt(balance, 10))
		bsPlayers.ActionBar(player, "@payout", args)
	}
}

func CoinsPlaceholder(ctx bsCore.Ctx) {
	player := bsMenus.PlaceholderPlayer(ctx)
	if player == 0 {
		bsMenus.PlaceholderResult(ctx, "0")
		return
	}
	bsMenus.PlaceholderResult(ctx, strconv.FormatInt(appCoins.Get(player), 10))
}

func Register() {
	bsEvents.On("player.join", bsCore.Normal, OnJoin)
	// Delayed by its own period, so a payout does not stack with the welcome bonus.
	period := int32(bsConfig.ConfigI64("payout-period", 1200))
	bsTasks.Every(period, period, Payout)
	bsMenus.Placeholder("coins", CoinsPlaceholder)
}
