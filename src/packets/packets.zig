const basalt = @import("basalt_server");
const net = basalt.packets;
const values = basalt.values;

const channel_name = "example:chan";
const capacity = 1024;

var command_listener: i32 = 0;
var last_ctx: basalt.core.Ctx = 0;

fn source(ctx: basalt.core.Ctx) basalt.core.Player {
    var name_buf: [256]u8 = undefined;
    return basalt.players.byName(basalt.commands.sourceName(ctx, &name_buf) orelse "");
}

fn onChannel(ctx: basalt.core.Ctx) void {
    var info_buf: [capacity]u8 = undefined;
    var body: [capacity]u8 = undefined;
    _ = net.packetCtxInfo(ctx, &info_buf);
    const size: usize = @intCast(net.packetCtxBody(ctx, &body));
    _ = net.channelSend(net.packetCtxPlayer(ctx), channel_name, body[0..size]);
    _ = net.packetCtxCancel(ctx);
}

fn onChat(ctx: basalt.core.Ctx) void {
    last_ctx = ctx;
    var body: [capacity]u8 = undefined;
    const size: usize = @intCast(net.packetCtxBody(ctx, &body));
    _ = net.packetCtxPlayer(ctx);
    if (size > 0 and body[0] == '!') {
        _ = net.packetCtxCancel(ctx);
        return;
    }
    body[size] = '#';
    _ = net.packetCtxSetBody(ctx, body[0 .. size + 1]);
}

fn onWatch(ctx: basalt.core.Ctx) void {
    var info_buf: [capacity]u8 = undefined;
    const size: usize = @intCast(net.packetCtxInfo(ctx, &info_buf));
    if (values.Value.open(info_buf[0..size])) |root| {
        if (root.find("name")) |name| {
            if (name.asString()) |text| basalt.log.info(text);
        }
    }
    _ = net.packetCtxCancel(ctx);
}

fn onCommandPacket(ctx: basalt.core.Ctx) void {
    _ = net.packetCtxSend(ctx, "clientbound.system_chat", "ok", false);
    _ = net.packetCtxReceive(ctx, "serverbound.chat_message", "hi", true);
    _ = net.packetCtxSend(ctx, "serverbound.chat_message", "no", false);
}

fn info() void {
    var out: [capacity]u8 = undefined;
    _ = net.packetNames("serverbound", &out);
    _ = net.packetNames("", &out);
    _ = net.packetLookup("serverbound.chat_message", &out);
    _ = net.packetLookup("clientbound.play.*", &out);
    _ = net.packetLookup("bogus.name", &out);
    _ = net.packetPlayerActions(&out);
}

fn send(player: basalt.core.Player) void {
    _ = net.packetSend(player, "clientbound.set_title_text", "hi", false);
    _ = net.packetSend(player, "serverbound.chat_message", "x", false);
    _ = net.packetSend(player, "clientbound.system_chat", "again", true);
}

fn receive(player: basalt.core.Player) void {
    _ = net.packetReceive(player, "serverbound.chat_command", "/help", false);
    _ = net.packetReceive(player, "clientbound.set_title_text", "x", false);
}

fn channel(player: basalt.core.Player) void {
    _ = net.channelSend(player, channel_name, "ping");
    _ = net.channelSend(0, channel_name, "ping");
}

fn action(player: basalt.core.Player) void {
    var held_buf: [64]u8 = undefined;
    var held = values.Writer.init(&held_buf);
    held.beginList();
    held.int(3);
    held.end();
    const held_doc = held.finish() catch return;
    _ = net.packetPlayerReceive(player, "held_slot", held_doc);
    var payload_buf: [96]u8 = undefined;
    var payload = values.Writer.init(&payload_buf);
    payload.beginList();
    payload.string(channel_name);
    payload.string("pong");
    payload.end();
    const payload_doc = payload.finish() catch return;
    _ = net.packetPlayerReceive(player, "custom_payload", payload_doc);
    _ = net.packetPlayerReceive(player, "bogus", held_doc);
}

fn stale() void {
    var out: [capacity]u8 = undefined;
    _ = net.packetCtxCancel(last_ctx);
    _ = net.packetCtxInfo(9999, &out);
}

fn unlisten() void {
    _ = net.packetUnlisten(command_listener);
    _ = net.packetUnlisten(command_listener);
    _ = net.packetUnlisten(0);
}

fn eql(a: []const u8, b: []const u8) bool {
    return @import("std").mem.eql(u8, a, b);
}

fn onCommand(ctx: basalt.core.Ctx) void {
    var word_buf: [256]u8 = undefined;
    const word = basalt.commands.argStr(ctx, "action", &word_buf) orelse "";
    const player = source(ctx);
    if (eql(word, "info")) {
        info();
    } else if (eql(word, "send")) {
        send(player);
    } else if (eql(word, "recv")) {
        receive(player);
    } else if (eql(word, "chan")) {
        channel(player);
    } else if (eql(word, "action")) {
        action(player);
    } else if (eql(word, "stale")) {
        stale();
    } else if (eql(word, "unlisten")) {
        unlisten();
    }
}

pub fn register() void {
    _ = net.channelListen(channel_name, onChannel);
    _ = net.packetListen("serverbound.chat_message", "first", false, onChat);
    _ = net.packetListen("clientbound.*", null, true, onWatch);
    _ = net.packetListen("serverbound.play.*", "late", true, onWatch);
    command_listener = net.packetListen("serverbound.chat_command", null, false, onCommandPacket);
    _ = net.packetListen("nope", null, false, onWatch);
    _ = basalt.commands.register("pkt <action: word>", "", onCommand);
}
