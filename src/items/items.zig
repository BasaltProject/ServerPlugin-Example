const std = @import("std");
const basalt = @import("basalt_server");
const areas = basalt;
const raw = basalt.items.raw;
const values = areas.values;
const items = areas.items;

const document = 2048;

var menu_handle: i32 = 0;

fn sourcePlayer(ctx: basalt.core.Ctx) basalt.core.Player {
    var name: [256]u8 = undefined;
    return basalt.players.byName(basalt.commands.sourceName(ctx, &name) orelse "");
}

fn say(ctx: basalt.core.Ctx, label: []const u8, value: i64) void {
    var text: [96]u8 = undefined;
    var args = basalt.core.Args.init();
    _ = args.add("text", std.fmt.bufPrint(&text, "{s} {d}", .{ label, value }) catch "");
    _ = basalt.commands.reply(ctx, "@items-line", &args);
}

fn countArg(ctx: basalt.core.Ctx) i32 {
    return @intCast(basalt.commands.argI64(ctx, "count") orelse 1);
}

fn give(ctx: basalt.core.Ctx) void {
    var id: [256]u8 = undefined;
    const name = basalt.commands.argStr(ctx, "id", &id) orelse "";
    var inventory = items.Inventory.ofPlayer(sourcePlayer(ctx));
    defer inventory.release();
    say(ctx, "left over", inventory.give(name, countArg(ctx), "<gold>Gift"));
}

fn take(ctx: basalt.core.Ctx) void {
    var id: [256]u8 = undefined;
    const name = basalt.commands.argStr(ctx, "id", &id) orelse "";
    var inventory = items.Inventory.ofPlayer(sourcePlayer(ctx));
    defer inventory.release();
    say(ctx, "taken", inventory.take(name, countArg(ctx)));
}

fn count(ctx: basalt.core.Ctx) void {
    var id: [256]u8 = undefined;
    const name = basalt.commands.argStr(ctx, "id", &id) orelse "";
    var inventory = items.Inventory.ofPlayer(sourcePlayer(ctx));
    defer inventory.release();
    say(ctx, "count", inventory.count(name));
    say(ctx, "size", inventory.size());
}

fn slot(ctx: basalt.core.Ctx) void {
    const index: i32 = @intCast(basalt.commands.argI64(ctx, "slot") orelse 0);
    var inventory = items.Inventory.ofPlayer(sourcePlayer(ctx));
    defer inventory.release();
    say(ctx, "in slot", inventory.slotCount(index));
    say(ctx, "bad slot", inventory.slotCount(99));
}

fn hand(ctx: basalt.core.Ctx) void {
    var inventory = items.Inventory.ofPlayer(sourcePlayer(ctx));
    defer inventory.release();
    say(ctx, "held", inventory.heldSlot());
    say(ctx, "set held", inventory.setHeldSlot(4));
    say(ctx, "held", inventory.heldSlot());
    var contents: [document]u8 = undefined;
    say(ctx, "contents bytes", inventory.contents(&contents));
}

fn chest(ctx: basalt.core.Ctx) void {
    var block = items.Inventory.ofBlock("world", 10, 64, 10);
    defer block.release();
    say(ctx, "chest", block.size());
    say(ctx, "bread", block.count("minecraft:bread"));
    say(ctx, "left over", block.give("minecraft:apple", 3, null));
    say(ctx, "apples", block.count("minecraft:apple"));
    say(ctx, "taken", block.take("minecraft:bread", 2));
    var missing = items.Inventory.ofBlock("world", 0, 0, 0);
    say(ctx, "no block", missing.handle);
    missing.release();
    var hopper = items.Inventory.ofBlock("world", 11, 64, 10);
    say(ctx, "hopper", hopper.size());
    hopper.release();
}

fn ender(ctx: basalt.core.Ctx) void {
    var inventory = items.Inventory.ofEnder(sourcePlayer(ctx));
    defer inventory.release();
    say(ctx, "put", inventory.put(0, "minecraft:diamond", 5));
    say(ctx, "diamonds", inventory.count("minecraft:diamond"));
    say(ctx, "size", inventory.size());
}

fn cooldown(ctx: basalt.core.Ctx) void {
    var id: [256]u8 = undefined;
    const name = basalt.commands.argStr(ctx, "id", &id) orelse "";
    const ticks: i32 = @intCast(basalt.commands.argI64(ctx, "ticks") orelse 0);
    const player = sourcePlayer(ctx);
    say(ctx, "set", items.itemSetCooldown(player, name, ticks));
    say(ctx, "left", items.itemCooldown(player, name));
}

fn registry(ctx: basalt.core.Ctx) void {
    var kind: [256]u8 = undefined;
    var key: [256]u8 = undefined;
    const kind_text = basalt.commands.argStr(ctx, "kind", &kind) orelse "";
    const key_text = basalt.commands.argStr(ctx, "key", &key) orelse "";
    var out: [document]u8 = undefined;
    say(ctx, "bytes", items.registryGet(kind_text, key_text, &out));
}

fn level(ctx: basalt.core.Ctx) void {
    const value: i32 = @intCast(basalt.commands.argI64(ctx, "level") orelse 1);
    var buffer: [128]u8 = undefined;
    var spec = values.Writer.init(&buffer);
    spec.beginMap();
    spec.key("type");
    spec.string("linear");
    spec.key("base");
    spec.float(1.0);
    spec.key("per_level_above_first");
    spec.float(0.5);
    spec.end();
    const bytes = spec.finish() catch &[_]u8{};
    const number = raw.enchantment_level_value(bytes.ptr, @intCast(bytes.len), value);
    say(ctx, "value x100", @intFromFloat(number * 100.0));
}

fn recipes(ctx: basalt.core.Ctx) void {
    const json = "{\"type\":\"minecraft:crafting_shapeless\",\"ingredients\":[\"minecraft:dirt\"],\"result\":{\"id\":\"minecraft:diamond\",\"count\":2}}";
    const id = "example:dirt_gems";
    say(ctx, "added", raw.recipe_add(id, 17, json, json.len));
    say(ctx, "again", raw.recipe_add(id, 17, json, json.len));
    say(ctx, "has", if (items.recipeKnown(id)) 1 else 0);
    var out: [document]u8 = undefined;
    say(ctx, "ids bytes", raw.recipe_ids(&out, document));
    say(ctx, "get bytes", raw.recipe_get(id, 17, &out, document));
    say(ctx, "removed", raw.recipe_remove(id, 17));
    say(ctx, "removed again", raw.recipe_remove(id, 17));
}

fn book(ctx: basalt.core.Ctx) void {
    const player = sourcePlayer(ctx);
    var buffer: [128]u8 = undefined;
    var ids = values.Writer.init(&buffer);
    ids.beginList();
    ids.string("minecraft:oak_planks");
    ids.string("minecraft:stick");
    ids.end();
    const bytes = ids.finish() catch &[_]u8{};
    const length: i32 = @intCast(bytes.len);
    say(ctx, "learned", raw.recipe_book_set(player, bytes.ptr, length, 1));
    say(ctx, "knows", raw.recipe_book_has(player, "minecraft:stick", 15));
    var out: [document]u8 = undefined;
    say(ctx, "book bytes", raw.recipe_book_get(player, &out, document));
    say(ctx, "forgot", raw.recipe_book_set(player, bytes.ptr, length, 0));
    say(ctx, "knows", raw.recipe_book_has(player, "minecraft:stick", 15));
    say(ctx, "resent", raw.recipe_book_resend(player));
}

const Station = struct {
    input: []const u8,

    fn call(self: Station, out: [*]u8, cap: i32) i32 {
        return raw.recipe_station("crafting", 8, self.input.ptr, @intCast(self.input.len), out, cap);
    }
};

fn craft(ctx: basalt.core.Ctx) void {
    var buffer: [512]u8 = undefined;
    var input = values.Writer.init(&buffer);
    input.beginMap();
    input.key("width");
    input.int(1);
    input.key("height");
    input.int(1);
    input.key("items");
    input.beginList();
    input.beginMap();
    input.key("id");
    input.string("minecraft:oak_log");
    input.key("count");
    input.int(1);
    input.end();
    input.end();
    input.end();
    const bytes = input.finish() catch &[_]u8{};
    var out: [document]u8 = undefined;
    var spare: [document]u8 = undefined;
    var status: i64 = 0;
    var result_count: i64 = -1;
    if (values.callValue(Station{ .input = bytes }, Station.call, &out, &spare)) |answer| {
        if (answer.find("result")) |result| {
            if (result.find("count")) |found| result_count = found.asInt() orelse -1;
        }
    } else |_| {
        status = -3;
    }
    say(ctx, "status", status);
    say(ctx, "result count", result_count);
    say(ctx, "unknown", raw.recipe_station("grinder", 7, bytes.ptr, @intCast(bytes.len), &out, document));
}

fn clickDiamond(ctx: basalt.core.Ctx) void {
    var info: [256]u8 = undefined;
    var line: [96]u8 = undefined;
    const length = raw.container_menu_click_info(ctx, &info, 256);
    if (length > 0 and length <= 256) {
        if (values.Value.open(info[0..@intCast(length)])) |root| {
            const clicked = if (root.find("slot")) |found| found.asInt() orelse -1 else -1;
            basalt.log.info(std.fmt.bufPrint(&line, "clicked slot {d}", .{clicked}) catch "");
        }
    }
    const replacement = items.Item.init("minecraft:emerald", 1, null);
    _ = raw.container_menu_click_result(ctx, replacement.bytes().ptr, replacement.length);
    basalt.log.info(std.fmt.bufPrint(&line, "cancel {d}", .{raw.container_menu_click_cancel(ctx, 1)}) catch "");
}

fn clickRefuse(ctx: basalt.core.Ctx) void {
    var line: [96]u8 = undefined;
    basalt.log.info(std.fmt.bufPrint(&line, "uncancel {d}", .{raw.container_menu_click_cancel(ctx, 0)}) catch "");
    const empty = [1]u8{0};
    _ = raw.container_menu_click_result(ctx, &empty, 1);
}

fn menu(ctx: basalt.core.Ctx) void {
    const player = sourcePlayer(ctx);
    if (menu_handle == 0) {
        var buffer: [128]u8 = undefined;
        var spec = values.Writer.init(&buffer);
        spec.beginMap();
        spec.key("title");
        spec.string("<gold>Items menu");
        spec.key("rows");
        spec.int(1);
        spec.end();
        const bytes = spec.finish() catch &[_]u8{};
        menu_handle = raw.container_menu_create(bytes.ptr, @intCast(bytes.len));
    }
    say(ctx, "menu", if (menu_handle > 0) 1 else 0);
    say(ctx, "slot 2", items.menuSlot(menu_handle, 2, "minecraft:diamond", 1, "<aqua>Click me", clickDiamond));
    say(ctx, "slot 4", items.menuSlot(menu_handle, 4, "minecraft:barrier", 1, "<red>Refuse", clickRefuse));
    say(ctx, "slot 99", items.menuSlot(menu_handle, 99, "minecraft:diamond", 1, null, null));
    say(ctx, "open", raw.container_menu_open(menu_handle, player));
}

fn closeMenu(ctx: basalt.core.Ctx) void {
    say(ctx, "close", raw.container_menu_close(menu_handle, sourcePlayer(ctx)));
    say(ctx, "close again", raw.container_menu_close(menu_handle, sourcePlayer(ctx)));
}

fn window(ctx: basalt.core.Ctx) void {
    const player = sourcePlayer(ctx);
    var buffer: [512]u8 = undefined;
    var spec = values.Writer.init(&buffer);
    spec.beginMap();
    spec.key("kind");
    spec.string("hopper");
    spec.key("title");
    spec.string("<green>Sorter");
    spec.key("items");
    spec.beginList();
    spec.beginMap();
    spec.key("id");
    spec.string("minecraft:coal");
    spec.key("count");
    spec.int(7);
    spec.end();
    spec.end();
    spec.end();
    const bytes = spec.finish() catch &[_]u8{};
    say(ctx, "container", raw.window_open(player, bytes.ptr, @intCast(bytes.len)));
    var out: [document]u8 = undefined;
    say(ctx, "info bytes", raw.window_info(player, &out, document));
    var update_buffer: [128]u8 = undefined;
    var update = values.Writer.init(&update_buffer);
    update.beginMap();
    update.key("title");
    update.string("<red>Sorted");
    update.end();
    const update_bytes = update.finish() catch &[_]u8{};
    say(ctx, "update", raw.window_update(player, update_bytes.ptr, @intCast(update_bytes.len)));
    var inventory = items.Inventory.ofWindow(player);
    defer inventory.release();
    say(ctx, "window size", inventory.size());
    say(ctx, "coal", inventory.count("minecraft:coal"));
    say(ctx, "close", raw.window_close(player));
    say(ctx, "close again", raw.window_close(player));
}

fn merchant(ctx: basalt.core.Ctx) void {
    var buffer: [1024]u8 = undefined;
    var spec = values.Writer.init(&buffer);
    spec.beginMap();
    spec.key("title");
    spec.string("<gold>Trader");
    spec.key("offers");
    spec.beginList();
    spec.beginMap();
    spec.key("buy");
    spec.beginMap();
    spec.key("id");
    spec.string("minecraft:emerald");
    spec.key("count");
    spec.int(2);
    spec.end();
    spec.key("sell");
    spec.beginMap();
    spec.key("id");
    spec.string("minecraft:bread");
    spec.key("count");
    spec.int(6);
    spec.end();
    spec.key("max_uses");
    spec.int(8);
    spec.key("xp");
    spec.int(3);
    spec.key("price_multiplier");
    spec.float(0.05);
    spec.end();
    spec.end();
    spec.end();
    const bytes = spec.finish() catch &[_]u8{};
    say(ctx, "merchant", raw.merchant_open(sourcePlayer(ctx), bytes.ptr, @intCast(bytes.len)));
    var bad_buffer: [64]u8 = undefined;
    var bad = values.Writer.init(&bad_buffer);
    bad.beginMap();
    bad.end();
    const bad_bytes = bad.finish() catch &[_]u8{};
    say(ctx, "bad merchant", raw.merchant_open(sourcePlayer(ctx), bad_bytes.ptr, @intCast(bad_bytes.len)));
}

fn loot(ctx: basalt.core.Ctx) void {
    var out: [document]u8 = undefined;
    say(ctx, "tables bytes", raw.loot_tables(&out, document));
    var roll_buffer: [128]u8 = undefined;
    var roll = values.Writer.init(&roll_buffer);
    roll.beginMap();
    roll.key("seed");
    roll.int(3);
    roll.key("luck");
    roll.float(1.5);
    roll.end();
    const roll_bytes = roll.finish() catch &[_]u8{};
    say(ctx, "zombie bytes", raw.loot_roll("minecraft:entities/zombie", 25, roll_bytes.ptr, @intCast(roll_bytes.len), &out, document));
    say(ctx, "no table", raw.loot_roll("minecraft:nothing", 17, &out, 0, &out, document));
    const table = "{\"type\":\"minecraft:chest\",\"pools\":[]}";
    say(ctx, "json bytes", raw.loot_roll_json(table, table.len, &out, 0, &out, document));
    say(ctx, "bad json", raw.loot_roll_json("nope", 4, &out, 0, &out, document));
    say(ctx, "number bytes", raw.loot_number("7", 1, 1, 0, &out, document));
    var apply_buffer: [512]u8 = undefined;
    var apply = values.Writer.init(&apply_buffer);
    apply.beginMap();
    apply.key("functions");
    apply.string("{\"function\":\"minecraft:set_count\"}");
    apply.key("item");
    apply.beginMap();
    apply.key("id");
    apply.string("minecraft:bread");
    apply.key("count");
    apply.int(2);
    apply.end();
    apply.end();
    const apply_bytes = apply.finish() catch &[_]u8{};
    say(ctx, "apply bytes", raw.loot_apply(apply_bytes.ptr, @intCast(apply_bytes.len), &out, document));
    var enchant_buffer: [512]u8 = undefined;
    var enchant = values.Writer.init(&enchant_buffer);
    enchant.beginMap();
    enchant.key("item");
    enchant.beginMap();
    enchant.key("id");
    enchant.string("minecraft:diamond_sword");
    enchant.end();
    enchant.key("levels");
    enchant.int(30);
    enchant.end();
    const enchant_bytes = enchant.finish() catch &[_]u8{};
    say(ctx, "enchant bytes", raw.loot_enchant(enchant_bytes.ptr, @intCast(enchant_bytes.len), &out, document));
    say(ctx, "trades bytes", raw.trades_roll("minecraft:farmer", 16, 2, &out, 0, &out, document));
    say(ctx, "no profession", raw.trades_roll("minecraft:nothing", 17, 2, &out, 0, &out, document));
}

pub fn register() void {
    _ = basalt.commands.register("items give <id: word> [count: int 1..64]", "", give);
    _ = basalt.commands.register("items take <id: word> [count: int 1..64]", "", take);
    _ = basalt.commands.register("items count <id: word>", "", count);
    _ = basalt.commands.register("items slot <slot: int 0..40>", "", slot);
    _ = basalt.commands.register("items hand", "", hand);
    _ = basalt.commands.register("items chest", "", chest);
    _ = basalt.commands.register("items ender", "", ender);
    _ = basalt.commands.register("items cooldown <id: word> <ticks: int 0..1000>", "", cooldown);
    _ = basalt.commands.register("items registry <kind: word> <key: word>", "", registry);
    _ = basalt.commands.register("items level <level: int 1..10>", "", level);
    _ = basalt.commands.register("items recipes", "", recipes);
    _ = basalt.commands.register("items book", "", book);
    _ = basalt.commands.register("items craft", "", craft);
    _ = basalt.commands.register("items menu", "", menu);
    _ = basalt.commands.register("items closemenu", "", closeMenu);
    _ = basalt.commands.register("items window", "", window);
    _ = basalt.commands.register("items merchant", "", merchant);
    _ = basalt.commands.register("items loot", "", loot);
}
