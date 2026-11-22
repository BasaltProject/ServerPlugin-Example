package shop

import (
	bsConfig "basaltmc.org/sdk/server/config"
	bsCore "basaltmc.org/sdk/server/core"
	bsMenus "basaltmc.org/sdk/server/menus"
	bsPlayers "basaltmc.org/sdk/server/players"
	bsText "basaltmc.org/sdk/server/text"
)

import appCoins "basalt.plugin/example/internal/coins"

import (
	"strconv"
)

const (
	sparklesSlot  = 11
	fireworksSlot = 15
)

var Menu int32

func Buy(player bsCore.Player, key string, price int64) bool {
	if appCoins.Take(player, price) {
		item, _ := bsText.Translate(key, nil)
		bsPlayers.Send(player, "@shop-bought", bsCore.NewArgs().Add("item", item).Add("price", strconv.FormatInt(price, 10)))
		return true
	}
	args := bsCore.NewArgs().Add("price", strconv.FormatInt(price, 10)).Add("amount", strconv.FormatInt(appCoins.Get(player), 10))
	bsPlayers.Send(player, "@shop-cant-afford", args)
	return false
}

func BuySparkles(ctx bsCore.Ctx) {
	player := bsMenus.MenuClickPlayer(ctx)
	if Buy(player, "shop-item-sparkles", bsConfig.ConfigI64("shop.sparkles-price", 100)) {
		bsPlayers.Particle(player, "minecraft:totem_of_undying", 30, 0.4)
	}
}

func BuyFireworks(ctx bsCore.Ctx) {
	player := bsMenus.MenuClickPlayer(ctx)
	if Buy(player, "shop-item-fireworks", bsConfig.ConfigI64("shop.fireworks-price", 150)) {
		bsPlayers.Sound(player, "minecraft:entity.firework_rocket.launch", 1, 1)
	}
}

func AddItem(slot int32, item, nameKey, loreKey string, price int64, onClick bsCore.Handler) {
	name, _ := bsText.Translate(nameKey, nil)
	lore, _ := bsText.Translate(loreKey, bsCore.NewArgs().Add("price", strconv.FormatInt(price, 10)))
	bsMenus.MenuSet(Menu, slot, item, name, lore, onClick)
}

func Build() {
	title, _ := bsText.Translate("shop-title", nil)
	Menu = bsMenus.MenuCreate(title, 3)
	AddItem(sparklesSlot, "minecraft:blaze_powder", "shop-item-sparkles", "shop-item-sparkles-lore",
		bsConfig.ConfigI64("shop.sparkles-price", 100), BuySparkles)
	AddItem(fireworksSlot, "minecraft:firework_rocket", "shop-item-fireworks", "shop-item-fireworks-lore",
		bsConfig.ConfigI64("shop.fireworks-price", 150), BuyFireworks)
}
