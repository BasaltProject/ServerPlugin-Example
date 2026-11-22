package commands

import (
	bsCommands "basaltmc.org/sdk/server/commands"
	bsCore "basaltmc.org/sdk/server/core"
	bsData "basaltmc.org/sdk/server/data"
	bsMenus "basaltmc.org/sdk/server/menus"
	bsPlayers "basaltmc.org/sdk/server/players"
)

import appShop "basalt.plugin/example/internal/shop"
import appCoins "basalt.plugin/example/internal/coins"

import (
	"strconv"
)

func Balance(ctx bsCore.Ctx) {
	if target, ok := bsCommands.CommandArgStr(ctx, "target"); ok {
		player := bsPlayers.PlayerByName(target)
		if player == 0 {
			bsCommands.CommandReply(ctx, "@balance-usage", nil)
			return
		}
		name, _ := bsPlayers.PlayerName(player)
		bsCommands.CommandReply(ctx, "@balance-other", bsCore.NewArgs().Add("name", name).Add("amount", strconv.FormatInt(appCoins.Get(player), 10)))
		return
	}
	if bsCommands.CommandIsConsole(ctx) {
		bsCommands.CommandReply(ctx, "@balance-usage", nil)
		return
	}
	source, _ := bsCommands.CommandSourceName(ctx)
	player := bsPlayers.PlayerByName(source)
	bsCommands.CommandReply(ctx, "@balance-self", bsCore.NewArgs().Add("amount", strconv.FormatInt(appCoins.Get(player), 10)))
}

func Top(ctx bsCore.Ctx) {
	limit, ok := bsCommands.CommandArgI64(ctx, "limit")
	if !ok {
		limit = 10
	}
	rows := bsData.StoreTop("coins", int32(limit))
	if len(rows) == 0 {
		bsCommands.CommandReply(ctx, "@top-empty", nil)
		return
	}
	bsCommands.CommandReply(ctx, "@top-header", nil)
	for i, row := range rows {
		args := bsCore.NewArgs().Add("rank", strconv.Itoa(i+1)).Add("name", row.Name).Add("amount", strconv.FormatInt(row.Value, 10))
		bsCommands.CommandReply(ctx, "@top-row", args)
	}
}

func OpenShop(ctx bsCore.Ctx) {
	if bsCommands.CommandIsConsole(ctx) {
		bsCommands.CommandReply(ctx, "@shop-players-only", nil)
		return
	}
	source, _ := bsCommands.CommandSourceName(ctx)
	bsMenus.MenuOpen(appShop.Menu, bsPlayers.PlayerByName(source))
}

func Register() {
	bsCommands.Command("coins [target: word]", "", Balance)
	bsCommands.Command("coins top [limit: int 1..25]", "", Top)
	bsCommands.Command("shop", "example.shop", OpenShop)
}
