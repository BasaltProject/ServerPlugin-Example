const std = @import("std");
const basalt = @import("basalt_server");
const ev = basalt.events;
const values = basalt.values;

const value_bytes = 512;
const list_bytes = 8192;

fn say(label: []const u8, text: []const u8) void {
    var line: [96]u8 = undefined;
    basalt.log.info(std.fmt.bufPrint(&line, "{s}{s}", .{ label, text }) catch "");
}

fn onForm(ctx: basalt.core.Ctx) void {
    var buffer: [value_bytes]u8 = undefined;
    const answer = ev.completionValue(ctx, &buffer) catch return;
    const button = if (answer.find("button")) |b| b.asInt() orelse -1 else -1;
    var line: [96]u8 = undefined;
    basalt.log.info(std.fmt.bufPrint(&line, "form button {d}", .{button}) catch "");
}

fn onDialog(ctx: basalt.core.Ctx) void {
    var buffer: [value_bytes]u8 = undefined;
    const answer = ev.completionValue(ctx, &buffer) catch return;
    say("dialog id ", if (answer.find("id")) |i| i.asString() orelse "" else "");
}

fn doc(storage: []u8, comptime build: fn (*values.Writer) void) []const u8 {
    var writer = values.Writer.init(storage);
    build(&writer);
    return writer.finish() catch "";
}

fn foodDoc(w: *values.Writer) void {
    w.int(18);
}

fn emptyList(w: *values.Writer) void {
    w.beginList();
    w.end();
}

fn tradeDoc(w: *values.Writer) void {
    w.beginMap();
    w.key("entity");
    w.int(1);
    w.end();
}

fn formDoc(w: *values.Writer) void {
    w.beginMap();
    w.key("title");
    w.string("Pick one");
    w.key("buttons");
    w.beginList();
    w.string("Yes");
    w.string("No");
    w.end();
    w.key("fields");
    w.beginList();
    w.beginMap();
    w.key("key");
    w.string("name");
    w.key("type");
    w.string("text");
    w.key("label");
    w.string("Name");
    w.end();
    w.end();
    w.end();
}

fn brokenDoc(w: *values.Writer) void {
    w.beginMap();
    w.key("title");
    w.int(1);
    w.end();
}

fn greet(ctx: basalt.core.Ctx) void {
    var buffer: [value_bytes]u8 = undefined;
    var scratch: [256]u8 = undefined;
    var listing: [list_bytes]u8 = undefined;
    const player = basalt.events.playerOf(ctx);
    _ = ev.eventGet(ctx, "world", &buffer) catch {};
    _ = ev.eventFields(ctx, &buffer) catch {};

    _ = ev.eventDescribe("player.chat", &buffer) catch {};
    _ = ev.eventDescribe("no.such.event", &buffer) catch {};
    _ = ev.eventNames(&listing) catch {};

    var uuid_buf: [64]u8 = undefined;
    const uuid = ev.playerUuid(player, &uuid_buf) orelse "";
    _ = ev.playerByUuid(uuid);
    _ = ev.playerByUuid("00000000-0000-0000-0000-999999999999");
    _ = ev.playerUuid(9999, &uuid_buf);

    _ = ev.playerGet(player, "food", &buffer) catch {};
    _ = ev.playerGet(player, "no_such_property", &buffer) catch {};
    const food = doc(&scratch, foodDoc);
    _ = ev.playerSet(player, "food", food);
    _ = ev.playerSet(player, "name", food);
    _ = ev.playerDescribe(&listing) catch {};
    const none = doc(&scratch, emptyList);
    _ = ev.playerCall(player, "reset_idle", none, &buffer) catch {};
    _ = ev.playerCall(player, "no_such_operation", none, &buffer) catch {};

    _ = ev.playerHealth(player);
    _ = ev.playerSetHealth(player, 10.5);
    _ = ev.playerSetHealth(player, -1.0);
    var mode: [16]u8 = undefined;
    _ = ev.playerGamemode(player, &mode);
    _ = ev.playerSetGamemode(player, "creative");
    _ = ev.playerSetGamemode(player, "bogus");
    _ = ev.playerTeleport(player, "", 10.5, 64.0, -3.25, 90.0, 15.0);
    _ = ev.playerTeleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0, 0.0);
    _ = ev.playerTitle(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10, null);
    _ = ev.playerTitle(player, "<gold>Welcome", "", -1, 40, 10, null);
    _ = ev.playerEffect(player, "minecraft:speed", 200, 1);
    _ = ev.playerEffect(player, "no_such_effect", 200, 1);

    const trade = doc(&scratch, tradeDoc);
    _ = ev.playerOpenInventory(player, "trades", trade);
    _ = ev.playerOpenInventory(player, "no_such_window", trade);
    _ = ev.playerCloseWindow(player);

    var form_buf: [256]u8 = undefined;
    const form = doc(&form_buf, formDoc);
    _ = ev.formOpen(player, form, onForm);
    _ = ev.formOpen(player, doc(&scratch, brokenDoc), onForm);
    _ = ev.formClose(player);
    _ = ev.formOpen(player, form, onForm);
    _ = ev.dialogShow(player, "minecraft:confirm", onDialog);
    _ = ev.dialogClear(player);
    _ = ev.dialogShow(player, "minecraft:confirm", onDialog);
    _ = ev.playerKick(9999, "bye", null);
}

fn xDoc(w: *values.Writer) void {
    w.string("x");
}

fn lateDoc(w: *values.Writer) void {
    w.string("late");
}

fn chatPayload(w: *values.Writer) void {
    w.beginMap();
    w.key("from");
    w.string("chat");
    w.end();
}

fn chat(ctx: basalt.core.Ctx) void {
    var buffer: [value_bytes]u8 = undefined;
    var scratch: [128]u8 = undefined;
    var name: [64]u8 = undefined;
    _ = ev.eventName(ctx, &name);
    var text: []const u8 = "";
    if (ev.eventGet(ctx, "message", &buffer)) |value| {
        text = value.asString() orelse "";
    } else |_| {}
    var changed: [96]u8 = undefined;
    const length = @min(text.len, 90);
    @memcpy(changed[0..length], text[0..length]);
    _ = ev.eventGet(ctx, "no_such_field", &buffer) catch {};
    _ = ev.eventCancelled(ctx);
    _ = ev.eventIsPost(ctx);
    _ = ev.eventSet(ctx, "player_name", doc(&scratch, xDoc));
    if (text.len == 6 and text[0] == 'c') {
        _ = ev.eventCancelWith(ctx, "<red>Not allowed</red>", null);
        return;
    }
    if (text.len == 3 and text[0] == 'b') {
        _ = ev.playerKick(basalt.events.playerOf(ctx), "<red>Bye</red>", null);
        return;
    }
    if (text.len == 4 and text[0] == 'p') {
        const payload = doc(&scratch, chatPayload);
        _ = ev.eventEmit("example:pong", payload);
        _ = ev.eventEmit("nocolon", payload);
    }
    changed[length] = '!';
    var replacement: [128]u8 = undefined;
    var writer = values.Writer.init(&replacement);
    writer.string(changed[0 .. length + 1]);
    _ = ev.eventSet(ctx, "message", writer.finish() catch "");
    _ = ev.eventFields(ctx, &buffer) catch {};
}

fn chatPost(ctx: basalt.core.Ctx) void {
    const post = ev.eventIsPost(ctx) == 1;
    const cancelled = ev.eventCancelled(ctx) == 1;
    var scratch: [32]u8 = undefined;
    _ = ev.eventSet(ctx, "message", doc(&scratch, lateDoc));
    _ = ev.eventCancelWith(ctx, "<red>Too late</red>", null);
    var reason: [256]u8 = undefined;
    const message = ev.eventCancelMessage(ctx, &reason) orelse "no message";
    var line: [96]u8 = undefined;
    const kind = if (post) "post " else "decision ";
    const state = if (cancelled) "cancelled " else "delivered ";
    basalt.log.info(std.fmt.bufPrint(&line, "{s}{s}{s}", .{ kind, state, message }) catch "");
}

fn ping(ctx: basalt.core.Ctx) void {
    var buffer: [value_bytes]u8 = undefined;
    const payload = ev.eventPayload(ctx, &buffer) catch return;
    const level = if (payload.find("level")) |l| l.asInt() orelse 0 else 0;
    var line: [96]u8 = undefined;
    basalt.log.info(std.fmt.bufPrint(&line, "ping level {d}", .{level}) catch "");
}

fn worldFilter(w: *values.Writer) void {
    w.beginMap();
    w.key("world");
    w.string("world");
    w.end();
}

pub fn register() void {
    _ = ev.onEx("player.chat", basalt.core.Priority.normal, 0, "", chat);
    _ = ev.onEx("player.chat", basalt.core.Priority.highest, ev.post | ev.cancelled, "", chatPost);
    var filters: [64]u8 = undefined;
    _ = ev.onEx("player.join", basalt.core.Priority.low, 0, doc(&filters, worldFilter), greet);
    _ = ev.onEx("example:ping", basalt.core.Priority.normal, 0, "", ping);
    _ = ev.onEx("no.such.event", basalt.core.Priority.normal, 0, "", ping);
}
