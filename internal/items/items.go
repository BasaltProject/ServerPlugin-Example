package items

import (
	bsCommands "basaltmc.org/sdk/server/commands"
	bsCore "basaltmc.org/sdk/server/core"
	bsItems "basaltmc.org/sdk/server/items"
	bsLog "basaltmc.org/sdk/server/log"
	bsPlayers "basaltmc.org/sdk/server/players"
	bsValues "basaltmc.org/sdk/server/values"
)

import (
	"strconv"
	"unsafe"
)

const documentBytes = 2048

var menuHandle int32

func ItemsSourcePlayer(ctx bsCore.Ctx) bsCore.Player {
	source, _ := bsCommands.CommandSourceName(ctx)
	return bsPlayers.PlayerByName(source)
}

func SayText(ctx bsCore.Ctx, text string) {
	bsCommands.CommandReply(ctx, "@items-line", bsCore.NewArgs().Add("text", text))
}

func ItemsSay(ctx bsCore.Ctx, label string, value int64) {
	SayText(ctx, label+" "+strconv.FormatInt(value, 10))
}

func CountArg(ctx bsCore.Ctx) int64 {
	count, ok := bsCommands.CommandArgI64(ctx, "count")
	if !ok {
		return 1
	}
	return count
}

func Give(ctx bsCore.Ctx) {
	id, _ := bsCommands.CommandArgStr(ctx, "id")
	inventory := bsItems.InventoryOfPlayer(ItemsSourcePlayer(ctx))
	defer inventory.Release()
	ItemsSay(ctx, "left over", int64(inventory.Give(id, int32(CountArg(ctx)), "<gold>Gift")))
}

func Take(ctx bsCore.Ctx) {
	id, _ := bsCommands.CommandArgStr(ctx, "id")
	inventory := bsItems.InventoryOfPlayer(ItemsSourcePlayer(ctx))
	defer inventory.Release()
	ItemsSay(ctx, "taken", int64(inventory.Take(id, int32(CountArg(ctx)))))
}

func Count(ctx bsCore.Ctx) {
	id, _ := bsCommands.CommandArgStr(ctx, "id")
	inventory := bsItems.InventoryOfPlayer(ItemsSourcePlayer(ctx))
	defer inventory.Release()
	ItemsSay(ctx, "count", int64(inventory.Count(id)))
	ItemsSay(ctx, "size", int64(inventory.Size()))
}

func Slot(ctx bsCore.Ctx) {
	index, _ := bsCommands.CommandArgI64(ctx, "slot")
	inventory := bsItems.InventoryOfPlayer(ItemsSourcePlayer(ctx))
	defer inventory.Release()
	ItemsSay(ctx, "in slot", int64(inventory.SlotCount(int32(index))))
	ItemsSay(ctx, "bad slot", int64(inventory.SlotCount(99)))
}

func Hand(ctx bsCore.Ctx) {
	inventory := bsItems.InventoryOfPlayer(ItemsSourcePlayer(ctx))
	defer inventory.Release()
	ItemsSay(ctx, "held", int64(inventory.HeldSlot()))
	ItemsSay(ctx, "set held", int64(inventory.SetHeldSlot(4)))
	ItemsSay(ctx, "held", int64(inventory.HeldSlot()))
	ItemsSay(ctx, "contents bytes", int64(inventory.Contents(make([]byte, documentBytes))))
}

func Chest(ctx bsCore.Ctx) {
	block := bsItems.InventoryOfBlock("world", 10, 64, 10)
	defer block.Release()
	ItemsSay(ctx, "chest", int64(block.Size()))
	ItemsSay(ctx, "bread", int64(block.Count("minecraft:bread")))
	ItemsSay(ctx, "left over", int64(block.Give("minecraft:apple", 3, "")))
	ItemsSay(ctx, "apples", int64(block.Count("minecraft:apple")))
	ItemsSay(ctx, "taken", int64(block.Take("minecraft:bread", 2)))
	ItemsSay(ctx, "no block", int64(bsItems.InventoryOfBlock("world", 0, 0, 0).Handle()))
	hopper := bsItems.InventoryOfBlock("world", 11, 64, 10)
	ItemsSay(ctx, "hopper", int64(hopper.Size()))
	hopper.Release()
}

func Ender(ctx bsCore.Ctx) {
	inventory := bsItems.InventoryOfEnder(ItemsSourcePlayer(ctx))
	defer inventory.Release()
	ItemsSay(ctx, "put", int64(inventory.Put(0, "minecraft:diamond", 5)))
	ItemsSay(ctx, "diamonds", int64(inventory.Count("minecraft:diamond")))
	ItemsSay(ctx, "size", int64(inventory.Size()))
}

func Cooldown(ctx bsCore.Ctx) {
	id, _ := bsCommands.CommandArgStr(ctx, "id")
	ticks, _ := bsCommands.CommandArgI64(ctx, "ticks")
	player := ItemsSourcePlayer(ctx)
	ItemsSay(ctx, "set", int64(bsItems.ItemSetCooldown(player, id, int32(ticks))))
	ItemsSay(ctx, "left", int64(bsItems.ItemCooldown(player, id)))
}

func Registry(ctx bsCore.Ctx) {
	kind, _ := bsCommands.CommandArgStr(ctx, "kind")
	key, _ := bsCommands.CommandArgStr(ctx, "key")
	ItemsSay(ctx, "bytes", int64(bsItems.RegistryGet(kind, key, make([]byte, documentBytes))))
}

func Level(ctx bsCore.Ctx) {
	value, ok := bsCommands.CommandArgI64(ctx, "level")
	if !ok {
		value = 1
	}
	spec := bsValues.NewValueWriterLimit(128)
	spec.BeginMap().Key("type").String("linear").Key("base").Float(1.0).Key("per_level_above_first").Float(0.5).End()
	doc, _ := spec.Finish()
	number := bsItems.EnchantmentLevelValue(doc, int32(value))
	ItemsSay(ctx, "value x100", int64(number*100.0))
}

func Recipes(ctx bsCore.Ctx) {
	json := "{\"type\":\"minecraft:crafting_shapeless\",\"ingredients\":[\"minecraft:dirt\"]," +
		"\"result\":{\"id\":\"minecraft:diamond\",\"count\":2}}"
	ItemsSay(ctx, "added", int64(bsItems.RecipeAdd("example:dirt_gems", json)))
	ItemsSay(ctx, "again", int64(bsItems.RecipeAdd("example:dirt_gems", json)))
	has := int64(0)
	if bsItems.RecipeKnown("example:dirt_gems") {
		has = 1
	}
	ItemsSay(ctx, "has", has)
	out := make([]byte, documentBytes)
	ItemsSay(ctx, "ids bytes", int64(bsItems.RecipeIds(out)))
	ItemsSay(ctx, "get bytes", int64(bsItems.RecipeGet("example:dirt_gems", out)))
	ItemsSay(ctx, "removed", int64(bsItems.RecipeRemove("example:dirt_gems")))
	ItemsSay(ctx, "removed again", int64(bsItems.RecipeRemove("example:dirt_gems")))
}

func Book(ctx bsCore.Ctx) {
	player := ItemsSourcePlayer(ctx)
	w := bsValues.NewValueWriterLimit(128)
	w.BeginList().String("minecraft:oak_planks").String("minecraft:stick").End()
	ids, _ := w.Finish()
	ItemsSay(ctx, "learned", int64(bsItems.RecipeBookSet(player, ids, true)))
	ItemsSay(ctx, "knows", int64(bsItems.RecipeBookHas(player, "minecraft:stick")))
	ItemsSay(ctx, "book bytes", int64(bsItems.RecipeBookGet(player, make([]byte, documentBytes))))
	ItemsSay(ctx, "forgot", int64(bsItems.RecipeBookSet(player, ids, false)))
	ItemsSay(ctx, "knows", int64(bsItems.RecipeBookHas(player, "minecraft:stick")))
	ItemsSay(ctx, "resent", int64(bsItems.RecipeBookResend(player)))
}

func Craft(ctx bsCore.Ctx) {
	w := bsValues.NewValueWriterLimit(512)
	w.BeginMap().Key("width").Int(1).Key("height").Int(1).Key("items").BeginList()
	w.BeginMap().Key("id").String("minecraft:oak_log").Key("count").Int(1).End()
	w.End().End()
	input, _ := w.Finish()
	answer, err := bsValues.CallValue(documentBytes, func(out *byte, cap int32) int32 {
		return bsItems.RecipeStation("crafting", input, unsafe.Slice(out, cap))
	})
	status := int64(0)
	if err != nil {
		status = int64(err.(bsValues.CallError))
	}
	ItemsSay(ctx, "status", status)
	result, _ := answer.Find("result")
	field, _ := result.Find("count")
	resultCount, ok := field.AsInt()
	if !ok {
		resultCount = -1
	}
	ItemsSay(ctx, "result count", resultCount)
	ItemsSay(ctx, "unknown", int64(bsItems.RecipeStation("grinder", input, make([]byte, documentBytes))))
}

func ClickDiamond(ctx bsCore.Ctx) {
	info := make([]byte, 256)
	length := bsItems.ContainerMenuClickInfo(ctx, info)
	if length > 0 && length <= int32(len(info)) {
		if root, ok := bsValues.OpenValue(info[:length]); ok {
			field, _ := root.Find("slot")
			slotIndex, found := field.AsInt()
			if !found {
				slotIndex = -1
			}
			bsLog.LogInfo("clicked slot " + strconv.FormatInt(slotIndex, 10))
		}
	}
	replacement, _ := bsItems.ItemStack("minecraft:emerald", 1, "")
	bsItems.ContainerMenuClickResult(ctx, replacement)
	bsLog.LogInfo("cancel " + strconv.Itoa(int(bsItems.ContainerMenuClickCancel(ctx, true))))
}

func ClickRefuse(ctx bsCore.Ctx) {
	bsLog.LogInfo("uncancel " + strconv.Itoa(int(bsItems.ContainerMenuClickCancel(ctx, false))))
	bsItems.ContainerMenuClickResult(ctx, []byte{0})
}

func Menu(ctx bsCore.Ctx) {
	player := ItemsSourcePlayer(ctx)
	if menuHandle == 0 {
		spec := bsValues.NewValueWriterLimit(128)
		spec.BeginMap().Key("title").String("<gold>Items menu").Key("rows").Int(1).End()
		doc, _ := spec.Finish()
		menuHandle = bsItems.ContainerMenuCreate(doc)
	}
	opened := int64(0)
	if menuHandle > 0 {
		opened = 1
	}
	ItemsSay(ctx, "menu", opened)
	ItemsSay(ctx, "slot 2", int64(bsItems.MenuSlot(menuHandle, 2, "minecraft:diamond", 1, "<aqua>Click me", ClickDiamond)))
	ItemsSay(ctx, "slot 4", int64(bsItems.MenuSlot(menuHandle, 4, "minecraft:barrier", 1, "<red>Refuse", ClickRefuse)))
	ItemsSay(ctx, "slot 99", int64(bsItems.MenuSlot(menuHandle, 99, "minecraft:diamond", 1, "", nil)))
	ItemsSay(ctx, "open", int64(bsItems.ContainerMenuOpen(menuHandle, player)))
}

func CloseMenu(ctx bsCore.Ctx) {
	ItemsSay(ctx, "close", int64(bsItems.ContainerMenuClose(menuHandle, ItemsSourcePlayer(ctx))))
	ItemsSay(ctx, "close again", int64(bsItems.ContainerMenuClose(menuHandle, ItemsSourcePlayer(ctx))))
}

func Window(ctx bsCore.Ctx) {
	player := ItemsSourcePlayer(ctx)
	w := bsValues.NewValueWriterLimit(512)
	w.BeginMap().Key("kind").String("hopper").Key("title").String("<green>Sorter").Key("items").BeginList()
	w.BeginMap().Key("id").String("minecraft:coal").Key("count").Int(7).End()
	w.End().End()
	spec, _ := w.Finish()
	ItemsSay(ctx, "container", int64(bsItems.WindowOpen(player, spec)))
	ItemsSay(ctx, "info bytes", int64(bsItems.WindowInfo(player, make([]byte, documentBytes))))
	u := bsValues.NewValueWriterLimit(128)
	u.BeginMap().Key("title").String("<red>Sorted").End()
	update, _ := u.Finish()
	ItemsSay(ctx, "update", int64(bsItems.WindowUpdate(player, update)))
	inventory := bsItems.InventoryOfWindow(player)
	defer inventory.Release()
	ItemsSay(ctx, "window size", int64(inventory.Size()))
	ItemsSay(ctx, "coal", int64(inventory.Count("minecraft:coal")))
	ItemsSay(ctx, "close", int64(bsItems.WindowClose(player)))
	ItemsSay(ctx, "close again", int64(bsItems.WindowClose(player)))
}

func Merchant(ctx bsCore.Ctx) {
	w := bsValues.NewValueWriterLimit(1024)
	w.BeginMap().Key("title").String("<gold>Trader").Key("offers").BeginList()
	w.BeginMap().Key("buy").BeginMap().Key("id").String("minecraft:emerald").Key("count").Int(2).End()
	w.Key("sell").BeginMap().Key("id").String("minecraft:bread").Key("count").Int(6).End()
	w.Key("max_uses").Int(8).Key("xp").Int(3).Key("price_multiplier").Float(0.05).End()
	w.End().End()
	spec, _ := w.Finish()
	ItemsSay(ctx, "merchant", int64(bsItems.MerchantOpen(ItemsSourcePlayer(ctx), spec)))
	b := bsValues.NewValueWriterLimit(64)
	b.BeginMap().End()
	bad, _ := b.Finish()
	ItemsSay(ctx, "bad merchant", int64(bsItems.MerchantOpen(ItemsSourcePlayer(ctx), bad)))
}

func Loot(ctx bsCore.Ctx) {
	out := make([]byte, documentBytes)
	ItemsSay(ctx, "tables bytes", int64(bsItems.LootTables(out)))
	r := bsValues.NewValueWriterLimit(128)
	r.BeginMap().Key("seed").Int(3).Key("luck").Float(1.5).End()
	roll, _ := r.Finish()
	ItemsSay(ctx, "zombie bytes", int64(bsItems.LootRoll("minecraft:entities/zombie", roll, out)))
	ItemsSay(ctx, "no table", int64(bsItems.LootRoll("minecraft:nothing", nil, out)))
	table := "{\"type\":\"minecraft:chest\",\"pools\":[]}"
	ItemsSay(ctx, "json bytes", int64(bsItems.LootRollJson(table, nil, out)))
	ItemsSay(ctx, "bad json", int64(bsItems.LootRollJson("nope", nil, out)))
	ItemsSay(ctx, "number bytes", int64(bsItems.LootNumber("7", true, 0, out)))
	a := bsValues.NewValueWriterLimit(512)
	a.BeginMap().Key("functions").String("{\"function\":\"minecraft:set_count\"}").Key("item").BeginMap()
	a.Key("id").String("minecraft:bread").Key("count").Int(2).End().End()
	apply, _ := a.Finish()
	ItemsSay(ctx, "apply bytes", int64(bsItems.LootApply(apply, out)))
	e := bsValues.NewValueWriterLimit(512)
	e.BeginMap().Key("item").BeginMap().Key("id").String("minecraft:diamond_sword").End().Key("levels").Int(30).End()
	enchant, _ := e.Finish()
	ItemsSay(ctx, "enchant bytes", int64(bsItems.LootEnchant(enchant, out)))
	ItemsSay(ctx, "trades bytes", int64(bsItems.TradesRoll("minecraft:farmer", 2, nil, out)))
	ItemsSay(ctx, "no profession", int64(bsItems.TradesRoll("minecraft:nothing", 2, nil, out)))
}

func Register() {
	bsCommands.Command("items give <id: word> [count: int 1..64]", "", Give)
	bsCommands.Command("items take <id: word> [count: int 1..64]", "", Take)
	bsCommands.Command("items count <id: word>", "", Count)
	bsCommands.Command("items slot <slot: int 0..40>", "", Slot)
	bsCommands.Command("items hand", "", Hand)
	bsCommands.Command("items chest", "", Chest)
	bsCommands.Command("items ender", "", Ender)
	bsCommands.Command("items cooldown <id: word> <ticks: int 0..1000>", "", Cooldown)
	bsCommands.Command("items registry <kind: word> <key: word>", "", Registry)
	bsCommands.Command("items level <level: int 1..10>", "", Level)
	bsCommands.Command("items recipes", "", Recipes)
	bsCommands.Command("items book", "", Book)
	bsCommands.Command("items craft", "", Craft)
	bsCommands.Command("items menu", "", Menu)
	bsCommands.Command("items closemenu", "", CloseMenu)
	bsCommands.Command("items window", "", Window)
	bsCommands.Command("items merchant", "", Merchant)
	bsCommands.Command("items loot", "", Loot)
}
