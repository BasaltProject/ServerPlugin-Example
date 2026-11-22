const basalt = @import("basalt_server");
const coins = @import("../coins/coins.zig");

const sparkles_slot = 11;
const fireworks_slot = 15;

var menu: i32 = 0;

pub fn menuHandle() i32 {
    return menu;
}

fn buy(player: basalt.core.Player, key: []const u8, price: i64) bool {
    var number: [24]u8 = undefined;
    var item: [256]u8 = undefined;
    var args = basalt.core.Args.init();
    if (coins.take(player, price)) {
        _ = args.add("item", basalt.text.translate(key, null, &item) orelse "").add("price", basalt.core.formatI64(price, &number));
        _ = basalt.players.send(player, "@shop-bought", &args);
        return true;
    }
    var owned: [24]u8 = undefined;
    _ = args.add("price", basalt.core.formatI64(price, &number)).add("amount", basalt.core.formatI64(coins.get(player), &owned));
    _ = basalt.players.send(player, "@shop-cant-afford", &args);
    return false;
}

fn buySparkles(ctx: basalt.core.Ctx) void {
    const player = basalt.menus.clickPlayer(ctx);
    if (buy(player, "shop-item-sparkles", basalt.config.getI64("shop.sparkles-price", 100))) {
        _ = basalt.players.particle(player, "minecraft:totem_of_undying", 30, 0.4);
    }
}

fn buyFireworks(ctx: basalt.core.Ctx) void {
    const player = basalt.menus.clickPlayer(ctx);
    if (buy(player, "shop-item-fireworks", basalt.config.getI64("shop.fireworks-price", 150))) {
        _ = basalt.players.sound(player, "minecraft:entity.firework_rocket.launch", 1, 1);
    }
}

fn addItem(slot: i32, item: []const u8, name_key: []const u8, lore_key: []const u8, price: i64, on_click: basalt.core.Handler) void {
    var name: [256]u8 = undefined;
    var lore: [256]u8 = undefined;
    var number: [24]u8 = undefined;
    const label = basalt.text.translate(name_key, null, &name) orelse "";
    var args = basalt.core.Args.init();
    _ = args.add("price", basalt.core.formatI64(price, &number));
    _ = basalt.menus.set(menu, slot, item, label, basalt.text.translate(lore_key, &args, &lore) orelse "", on_click);
}

pub fn build() void {
    var title: [256]u8 = undefined;
    menu = basalt.menus.create(basalt.text.translate("shop-title", null, &title) orelse "", 3);
    addItem(sparkles_slot, "minecraft:blaze_powder", "shop-item-sparkles", "shop-item-sparkles-lore", basalt.config.getI64("shop.sparkles-price", 100), buySparkles);
    addItem(fireworks_slot, "minecraft:firework_rocket", "shop-item-fireworks", "shop-item-fireworks-lore", basalt.config.getI64("shop.fireworks-price", 150), buyFireworks);
}
