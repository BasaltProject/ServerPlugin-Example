const basalt = @import("basalt_server");
const coins = @import("../coins/coins.zig");

fn onJoin(ctx: basalt.core.Ctx) void {
    const player = basalt.events.playerOf(ctx);
    if (player == 0) return;
    var name_buf: [256]u8 = undefined;
    var number: [24]u8 = undefined;
    const name = basalt.players.name(player, &name_buf) orelse "";
    var args = basalt.core.Args.init();
    if (!basalt.data.getBool(player, "welcomed", false)) {
        _ = basalt.data.setBool(player, "welcomed", true);
        const bonus = basalt.config.getI64("welcome-bonus", 50);
        _ = coins.add(player, bonus);
        _ = args.add("name", name).add("amount", basalt.core.formatI64(bonus, &number));
        _ = basalt.players.send(player, "@welcome", &args);
    } else {
        _ = args.add("name", name).add("amount", basalt.core.formatI64(coins.get(player), &number));
        _ = basalt.players.send(player, "@welcome-back", &args);
    }
}

var crowd: [4096]basalt.core.Player = undefined;

fn pay(players: []const basalt.core.Player, amount: i64) void {
    for (players) |player| {
        if (player == 0) continue;
        const balance = coins.add(player, amount);
        var given: [24]u8 = undefined;
        var total: [24]u8 = undefined;
        var args = basalt.core.Args.init();
        _ = args.add("amount", basalt.core.formatI64(amount, &given)).add("balance", basalt.core.formatI64(balance, &total));
        _ = basalt.players.actionBar(player, "@payout", &args);
    }
}

fn payout(_: basalt.core.Ctx) void {
    const amount = basalt.config.getI64("payout-amount", 5);
    var online: [64]basalt.core.Player = @splat(0);
    const count = basalt.players.online(&online);
    if (count < online.len) return pay(online[0..count], amount);
    @memset(&crowd, 0);
    pay(crowd[0..basalt.players.online(&crowd)], amount);
}

fn coinsPlaceholder(ctx: basalt.core.Ctx) void {
    const player = basalt.menus.placeholderPlayer(ctx);
    var number: [24]u8 = undefined;
    _ = basalt.menus.placeholderResult(ctx, if (player == 0) "0" else basalt.core.formatI64(coins.get(player), &number));
}

pub fn register() void {
    _ = basalt.events.on("player.join", basalt.core.Priority.normal, onJoin);
    // Delayed by its own period, so a payout does not stack with the welcome bonus.
    const period: i32 = @intCast(basalt.config.getI64("payout-period", 1200));
    _ = basalt.tasks.every(period, period, payout);
    _ = basalt.menus.placeholder("coins", coinsPlaceholder);
}
