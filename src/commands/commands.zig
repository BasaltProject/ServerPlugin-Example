const basalt = @import("basalt_server");
const coins = @import("../coins/coins.zig");
const shop = @import("../shop/shop.zig");

fn balance(ctx: basalt.core.Ctx) void {
    var target_buf: [256]u8 = undefined;
    var name_buf: [256]u8 = undefined;
    var number: [24]u8 = undefined;
    var args = basalt.core.Args.init();
    if (basalt.commands.argStr(ctx, "target", &target_buf)) |target| {
        const player = basalt.players.byName(target);
        if (player == 0) {
            _ = basalt.commands.reply(ctx, "@balance-usage", null);
            return;
        }
        _ = args.add("name", basalt.players.name(player, &name_buf) orelse "").add("amount", basalt.core.formatI64(coins.get(player), &number));
        _ = basalt.commands.reply(ctx, "@balance-other", &args);
        return;
    }
    if (basalt.commands.isConsole(ctx)) {
        _ = basalt.commands.reply(ctx, "@balance-usage", null);
        return;
    }
    const player = basalt.players.byName(basalt.commands.sourceName(ctx, &name_buf) orelse "");
    _ = args.add("amount", basalt.core.formatI64(coins.get(player), &number));
    _ = basalt.commands.reply(ctx, "@balance-self", &args);
}

fn readU32(bytes: []const u8) u32 {
    return @as(u32, bytes[0]) | @as(u32, bytes[1]) << 8 | @as(u32, bytes[2]) << 16 | @as(u32, bytes[3]) << 24;
}

fn readI64(bytes: []const u8) i64 {
    var value: u64 = 0;
    for (0..8) |i| value |= @as(u64, bytes[i]) << @intCast(8 * i);
    return @bitCast(value);
}

fn top(ctx: basalt.core.Ctx) void {
    const limit = basalt.commands.argI64(ctx, "limit") orelse 10;
    var buf: [1024]u8 = undefined;
    const rows = basalt.data.storeTop("coins", @intCast(limit), &buf) orelse &[_]u8{};
    if (rows.len == 0) {
        _ = basalt.commands.reply(ctx, "@top-empty", null);
        return;
    }
    _ = basalt.commands.reply(ctx, "@top-header", null);
    var at: usize = 0;
    var rank: i64 = 1;
    while (at + 4 <= rows.len) : (rank += 1) {
        const length = readU32(rows[at..]);
        const name = rows[at + 4 ..][0..length];
        const amount = readI64(rows[at + 4 + length ..]);
        at += 4 + length + 8;
        var rank_text: [24]u8 = undefined;
        var amount_text: [24]u8 = undefined;
        var args = basalt.core.Args.init();
        _ = args.add("rank", basalt.core.formatI64(rank, &rank_text)).add("name", name).add("amount", basalt.core.formatI64(amount, &amount_text));
        _ = basalt.commands.reply(ctx, "@top-row", &args);
    }
}

fn openShop(ctx: basalt.core.Ctx) void {
    if (basalt.commands.isConsole(ctx)) {
        _ = basalt.commands.reply(ctx, "@shop-players-only", null);
        return;
    }
    var name: [256]u8 = undefined;
    _ = basalt.menus.open(shop.menuHandle(), basalt.players.byName(basalt.commands.sourceName(ctx, &name) orelse ""));
}

pub fn register() void {
    _ = basalt.commands.register("coins [target: word]", "", balance);
    _ = basalt.commands.register("coins top [limit: int 1..25]", "", top);
    _ = basalt.commands.register("shop", "example.shop", openShop);
}
