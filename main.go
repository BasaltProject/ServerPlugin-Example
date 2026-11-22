package main

import (
	bsConfig "basaltmc.org/sdk/server/config"
	bsCore "basaltmc.org/sdk/server/core"
	bsEvents "basaltmc.org/sdk/server/events"
	bsLog "basaltmc.org/sdk/server/log"
)

import appWorlds "basalt.plugin/example/internal/worlds"
import appShop "basalt.plugin/example/internal/shop"
import appServer "basalt.plugin/example/internal/server"
import appPackets "basalt.plugin/example/internal/packets"
import appItems "basalt.plugin/example/internal/items"
import appHud "basalt.plugin/example/internal/hud"
import appEvents "basalt.plugin/example/internal/events"
import appEntities "basalt.plugin/example/internal/entities"
import appCommands "basalt.plugin/example/internal/commands"
import appAreaevents "basalt.plugin/example/internal/area_events"

import (
	"strconv"
)

func init() { bsCore.Init(setup) }

func main() {}

func ready(bsCore.Ctx) {
	amount := strconv.FormatInt(bsConfig.ConfigI64("payout-amount", 5), 10)
	period := strconv.FormatInt(bsConfig.ConfigI64("payout-period", 1200), 10)
	bsLog.LogInfo("ready: " + amount + " coins every " + period + " ticks")
}

func setup() {
	appShop.Build()
	appEvents.Register()
	appCommands.Register()
	appAreaevents.Register()
	appServer.Register()
	appWorlds.Register()
	appHud.Register()
	appItems.Register()
	appEntities.Register()
	bsEvents.On("plugin.enable", bsCore.Normal, ready)
	appPackets.Register()
}
