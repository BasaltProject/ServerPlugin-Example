const std = @import("std");
const basalt = @import("basalt_server");
const area_events = @import("area_events/area_events.zig");
const commands = @import("commands/commands.zig");
const entities = @import("entities/entities.zig");
const events = @import("events/events.zig");
const server = @import("server/server.zig");
const hud = @import("hud/hud.zig");
const packets = @import("packets/packets.zig");
const items = @import("items/items.zig");
const shop = @import("shop/shop.zig");
const worlds = @import("worlds/worlds.zig");

fn ready(_: basalt.core.Ctx) void {
    var line: [96]u8 = undefined;
    const amount = basalt.config.getI64("payout-amount", 5);
    const period = basalt.config.getI64("payout-period", 1200);
    basalt.log.info(std.fmt.bufPrint(&line, "ready: {d} coins every {d} ticks", .{ amount, period }) catch "");
}

pub fn init() void {
    shop.build();
    events.register();
    commands.register();
    area_events.register();
    server.register();
    worlds.register();
    hud.register();
    items.register();
    entities.register();
    _ = basalt.events.on("plugin.enable", basalt.core.Priority.normal, ready);
    packets.register();
}
