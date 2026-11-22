import * as bsCommands from "basalt-server/commands";
import * as bsCore from "basalt-server/core";
import * as bsItems from "basalt-server/items";
import * as bsLog from "basalt-server/log";
import * as bsPlayers from "basalt-server/players";
import * as bsValues from "basalt-server/values";

const DOCUMENT: i32 = 2048;

let menuHandle: i32 = 0;

function sourcePlayer(ctx: bsCore.Ctx): bsCore.Player {
  const source = bsCommands.commandSourceName(ctx);
  return bsPlayers.playerByName(source ? source : "");
}

function say(ctx: bsCore.Ctx, label: string, value: i64): void {
  bsCommands.commandReply(ctx, "@items-line", new bsCore.Args().add("text", label + " " + value.toString()));
}

function countArg(ctx: bsCore.Ctx): i64 {
  return bsCommands.commandArgI64(ctx, "count", 1);
}

function stringArg(ctx: bsCore.Ctx, name: string): string {
  const value = bsCommands.commandArgStr(ctx, name);
  return value ? value : "";
}

function give(ctx: bsCore.Ctx): void {
  const id = stringArg(ctx, "id");
  const inventory = bsItems.Inventory.ofPlayer(sourcePlayer(ctx));
  say(ctx, "left over", inventory.give(id, <i32>countArg(ctx), "<gold>Gift"));
  inventory.release();
}

function take(ctx: bsCore.Ctx): void {
  const id = stringArg(ctx, "id");
  const inventory = bsItems.Inventory.ofPlayer(sourcePlayer(ctx));
  say(ctx, "taken", inventory.take(id, <i32>countArg(ctx)));
  inventory.release();
}

function count(ctx: bsCore.Ctx): void {
  const id = stringArg(ctx, "id");
  const inventory = bsItems.Inventory.ofPlayer(sourcePlayer(ctx));
  say(ctx, "count", inventory.count(id));
  say(ctx, "size", inventory.size());
  inventory.release();
}

function slot(ctx: bsCore.Ctx): void {
  const index = bsCommands.commandArgI64(ctx, "slot", 0);
  const inventory = bsItems.Inventory.ofPlayer(sourcePlayer(ctx));
  say(ctx, "in slot", inventory.slotCount(<i32>index));
  say(ctx, "bad slot", inventory.slotCount(99));
  inventory.release();
}

function hand(ctx: bsCore.Ctx): void {
  const inventory = bsItems.Inventory.ofPlayer(sourcePlayer(ctx));
  say(ctx, "held", inventory.heldSlot());
  say(ctx, "set held", inventory.setHeldSlot(4));
  say(ctx, "held", inventory.heldSlot());
  say(ctx, "contents bytes", inventory.contents(new Uint8Array(DOCUMENT)));
  inventory.release();
}

function chest(ctx: bsCore.Ctx): void {
  const block = bsItems.Inventory.ofBlock("world", 10, 64, 10);
  say(ctx, "chest", block.size());
  say(ctx, "bread", block.count("minecraft:bread"));
  say(ctx, "left over", block.give("minecraft:apple", 3));
  say(ctx, "apples", block.count("minecraft:apple"));
  say(ctx, "taken", block.take("minecraft:bread", 2));
  say(ctx, "no block", bsItems.Inventory.ofBlock("world", 0, 0, 0).handle);
  const hopper = bsItems.Inventory.ofBlock("world", 11, 64, 10);
  say(ctx, "hopper", hopper.size());
  hopper.release();
  block.release();
}

function ender(ctx: bsCore.Ctx): void {
  const inventory = bsItems.Inventory.ofEnder(sourcePlayer(ctx));
  say(ctx, "put", inventory.put(0, "minecraft:diamond", 5));
  say(ctx, "diamonds", inventory.count("minecraft:diamond"));
  say(ctx, "size", inventory.size());
  inventory.release();
}

function cooldown(ctx: bsCore.Ctx): void {
  const id = stringArg(ctx, "id");
  const ticks = bsCommands.commandArgI64(ctx, "ticks", 0);
  const player = sourcePlayer(ctx);
  say(ctx, "set", bsItems.itemSetCooldown(player, id, <i32>ticks));
  say(ctx, "left", bsItems.itemCooldown(player, id));
}

function registry(ctx: bsCore.Ctx): void {
  const kind = stringArg(ctx, "kind");
  const key = stringArg(ctx, "key");
  say(ctx, "bytes", bsItems.registryGet(kind, key, new Uint8Array(DOCUMENT)));
}

function level(ctx: bsCore.Ctx): void {
  const value = bsCommands.commandArgI64(ctx, "level", 1);
  const spec = new bsValues.ValueWriter(128);
  spec.beginMap().key("type").string("linear").key("base").float(1.0).key("per_level_above_first").float(0.5).end();
  const number = bsItems.enchantmentLevelValue(spec.finish()!, <i32>value);
  say(ctx, "value x100", <i64>(number * 100.0));
}

function recipes(ctx: bsCore.Ctx): void {
  const json =
    '{"type":"minecraft:crafting_shapeless","ingredients":["minecraft:dirt"],' +
    '"result":{"id":"minecraft:diamond","count":2}}';
  say(ctx, "added", bsItems.recipeAdd("example:dirt_gems", json));
  say(ctx, "again", bsItems.recipeAdd("example:dirt_gems", json));
  say(ctx, "has", bsItems.recipeKnown("example:dirt_gems") ? 1 : 0);
  const out = new Uint8Array(DOCUMENT);
  say(ctx, "ids bytes", bsItems.recipeIds(out));
  say(ctx, "get bytes", bsItems.recipeGet("example:dirt_gems", out));
  say(ctx, "removed", bsItems.recipeRemove("example:dirt_gems"));
  say(ctx, "removed again", bsItems.recipeRemove("example:dirt_gems"));
}

function book(ctx: bsCore.Ctx): void {
  const player = sourcePlayer(ctx);
  const w = new bsValues.ValueWriter(128);
  w.beginList().string("minecraft:oak_planks").string("minecraft:stick").end();
  const ids = w.finish()!;
  say(ctx, "learned", bsItems.recipeBookSet(player, ids, true));
  say(ctx, "knows", bsItems.recipeBookHas(player, "minecraft:stick"));
  say(ctx, "book bytes", bsItems.recipeBookGet(player, new Uint8Array(DOCUMENT)));
  say(ctx, "forgot", bsItems.recipeBookSet(player, ids, false));
  say(ctx, "knows", bsItems.recipeBookHas(player, "minecraft:stick"));
  say(ctx, "resent", bsItems.recipeBookResend(player));
}

function craft(ctx: bsCore.Ctx): void {
  const w = new bsValues.ValueWriter(512);
  w.beginMap().key("width").int(1).key("height").int(1).key("items").beginList();
  w.beginMap().key("id").string("minecraft:oak_log").key("count").int(1).end();
  w.end().end();
  const input = w.finish()!;
  const result = bsValues.callOut(new bsItems.RecipeStationCall("crafting", input), DOCUMENT);
  say(ctx, "status", result.error);
  let resultCount: i64 = -1;
  const answer = result.value();
  if (answer !== null) {
    const found = answer.find("result");
    const field = found !== null ? found.find("count") : null;
    if (field !== null) resultCount = field.toInt(-1);
  }
  say(ctx, "result count", resultCount);
  say(ctx, "unknown", bsItems.recipeStation("grinder", input, new Uint8Array(DOCUMENT)));
}

function clickDiamond(ctx: bsCore.Ctx): void {
  const info = new Uint8Array(256);
  const length = bsItems.containerMenuClickInfo(ctx, info);
  if (length > 0 && length <= info.length) {
    const root = bsValues.Value.open(info.subarray(0, length));
    if (root !== null) {
      const field = root.find("slot");
      bsLog.logInfo("clicked slot " + (field !== null ? field.toInt(-1) : -1).toString());
    }
  }
  bsItems.containerMenuClickResult(ctx, bsItems.itemStack("minecraft:emerald", 1)!);
  bsLog.logInfo("cancel " + bsItems.containerMenuClickCancel(ctx, true).toString());
}

function clickRefuse(ctx: bsCore.Ctx): void {
  bsLog.logInfo("uncancel " + bsItems.containerMenuClickCancel(ctx, false).toString());
  bsItems.containerMenuClickResult(ctx, new Uint8Array(1));
}

function menu(ctx: bsCore.Ctx): void {
  const player = sourcePlayer(ctx);
  if (menuHandle == 0) {
    const spec = new bsValues.ValueWriter(128);
    spec.beginMap().key("title").string("<gold>Items menu").key("rows").int(1).end();
    menuHandle = bsItems.containerMenuCreate(spec.finish()!);
  }
  say(ctx, "menu", menuHandle > 0 ? 1 : 0);
  say(ctx, "slot 2", bsItems.menuSlot(menuHandle, 2, "minecraft:diamond", 1, "<aqua>Click me", clickDiamond));
  say(ctx, "slot 4", bsItems.menuSlot(menuHandle, 4, "minecraft:barrier", 1, "<red>Refuse", clickRefuse));
  say(ctx, "slot 99", bsItems.menuSlot(menuHandle, 99, "minecraft:diamond", 1, "", null));
  say(ctx, "open", bsItems.containerMenuOpen(menuHandle, player));
}

function closeMenu(ctx: bsCore.Ctx): void {
  say(ctx, "close", bsItems.containerMenuClose(menuHandle, sourcePlayer(ctx)));
  say(ctx, "close again", bsItems.containerMenuClose(menuHandle, sourcePlayer(ctx)));
}

function window(ctx: bsCore.Ctx): void {
  const player = sourcePlayer(ctx);
  const w = new bsValues.ValueWriter(512);
  w.beginMap().key("kind").string("hopper").key("title").string("<green>Sorter").key("items").beginList();
  w.beginMap().key("id").string("minecraft:coal").key("count").int(7).end();
  w.end().end();
  say(ctx, "container", bsItems.windowOpen(player, w.finish()!));
  say(ctx, "info bytes", bsItems.windowInfo(player, new Uint8Array(DOCUMENT)));
  const u = new bsValues.ValueWriter(128);
  u.beginMap().key("title").string("<red>Sorted").end();
  say(ctx, "update", bsItems.windowUpdate(player, u.finish()!));
  const inventory = bsItems.Inventory.ofWindow(player);
  say(ctx, "window size", inventory.size());
  say(ctx, "coal", inventory.count("minecraft:coal"));
  say(ctx, "close", bsItems.windowClose(player));
  say(ctx, "close again", bsItems.windowClose(player));
  inventory.release();
}

function merchant(ctx: bsCore.Ctx): void {
  const w = new bsValues.ValueWriter(1024);
  w.beginMap().key("title").string("<gold>Trader").key("offers").beginList();
  w.beginMap().key("buy").beginMap().key("id").string("minecraft:emerald").key("count").int(2).end();
  w.key("sell").beginMap().key("id").string("minecraft:bread").key("count").int(6).end();
  w.key("max_uses").int(8).key("xp").int(3).key("price_multiplier").float(0.05).end();
  w.end().end();
  say(ctx, "merchant", bsItems.merchantOpen(sourcePlayer(ctx), w.finish()!));
  const b = new bsValues.ValueWriter(64);
  b.beginMap().end();
  say(ctx, "bad merchant", bsItems.merchantOpen(sourcePlayer(ctx), b.finish()!));
}

function loot(ctx: bsCore.Ctx): void {
  const out = new Uint8Array(DOCUMENT);
  say(ctx, "tables bytes", bsItems.lootTables(out));
  const r = new bsValues.ValueWriter(128);
  r.beginMap().key("seed").int(3).key("luck").float(1.5).end();
  say(ctx, "zombie bytes", bsItems.lootRoll("minecraft:entities/zombie", r.finish(), out));
  say(ctx, "no table", bsItems.lootRoll("minecraft:nothing", null, out));
  const table = '{"type":"minecraft:chest","pools":[]}';
  say(ctx, "json bytes", bsItems.lootRollJson(table, null, out));
  say(ctx, "bad json", bsItems.lootRollJson("nope", null, out));
  say(ctx, "number bytes", bsItems.lootNumber("7", true, 0, out));
  const a = new bsValues.ValueWriter(512);
  a.beginMap().key("functions").string('{"function":"minecraft:set_count"}').key("item").beginMap();
  a.key("id").string("minecraft:bread").key("count").int(2).end().end();
  say(ctx, "apply bytes", bsItems.lootApply(a.finish()!, out));
  const e = new bsValues.ValueWriter(512);
  e.beginMap().key("item").beginMap().key("id").string("minecraft:diamond_sword").end().key("levels").int(30).end();
  say(ctx, "enchant bytes", bsItems.lootEnchant(e.finish()!, out));
  say(ctx, "trades bytes", bsItems.tradesRoll("minecraft:farmer", 2, null, out));
  say(ctx, "no profession", bsItems.tradesRoll("minecraft:nothing", 2, null, out));
}

export function register(): void {
  bsCommands.command("items give <id: word> [count: int 1..64]", "", give);
  bsCommands.command("items take <id: word> [count: int 1..64]", "", take);
  bsCommands.command("items count <id: word>", "", count);
  bsCommands.command("items slot <slot: int 0..40>", "", slot);
  bsCommands.command("items hand", "", hand);
  bsCommands.command("items chest", "", chest);
  bsCommands.command("items ender", "", ender);
  bsCommands.command("items cooldown <id: word> <ticks: int 0..1000>", "", cooldown);
  bsCommands.command("items registry <kind: word> <key: word>", "", registry);
  bsCommands.command("items level <level: int 1..10>", "", level);
  bsCommands.command("items recipes", "", recipes);
  bsCommands.command("items book", "", book);
  bsCommands.command("items craft", "", craft);
  bsCommands.command("items menu", "", menu);
  bsCommands.command("items closemenu", "", closeMenu);
  bsCommands.command("items window", "", window);
  bsCommands.command("items merchant", "", merchant);
  bsCommands.command("items loot", "", loot);
}
