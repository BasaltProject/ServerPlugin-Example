const std = @import("std");
const basalt = @import("basalt_server");
const srv = basalt.server;
const values = basalt.values;

const buffer_size = srv.buffer_size;
const no_args = [_]u8{};

fn say(ctx: basalt.core.Ctx, text: []const u8) void {
    var args = basalt.core.Args.init();
    _ = args.add("text", text);
    _ = basalt.commands.reply(ctx, "@server-line", &args);
}

fn labelled(buf: []u8, label: []const u8, value: i64) []const u8 {
    return std.fmt.bufPrint(buf, "{s} {d}", .{ label, value }) catch "";
}

fn sayNumber(ctx: basalt.core.Ctx, label: []const u8, value: i64) void {
    var line: [96]u8 = undefined;
    say(ctx, labelled(&line, label, value));
}

fn noteNumber(label: []const u8, value: i64) void {
    var line: [96]u8 = undefined;
    basalt.log.info(labelled(&line, label, value));
}

fn code(read: srv.Error!values.Value) i64 {
    _ = read catch |err| return srv.status(err);
    return 0;
}

fn sub(value: ?values.Value, name: []const u8) ?values.Value {
    return (value orelse return null).find(name);
}

fn intOr(value: ?values.Value, fallback: i64) i64 {
    return (value orelse return fallback).asInt() orelse fallback;
}

fn countOf(value: ?values.Value) i64 {
    return @intCast((value orelse return 0).count() orelse 0);
}

fn sourcePlayer(ctx: basalt.core.Ctx) basalt.core.Player {
    var name: [256]u8 = undefined;
    return basalt.players.byName(basalt.commands.sourceName(ctx, &name) orelse "");
}

fn permissions(ctx: basalt.core.Ctx) void {
    const player = sourcePlayer(ctx);
    sayNumber(ctx, "has example.vip", srv.permissionHas(player, "example.vip"));
    sayNumber(ctx, "console has example.vip", srv.permissionCheck("console", "example.vip"));
    sayNumber(ctx, "grant", srv.permissionGrant(player, "example.vip"));
    sayNumber(ctx, "has example.vip", srv.permissionHas(player, "example.vip"));
    sayNumber(ctx, "drop", srv.permissionDrop(player, "example.vip"));
    sayNumber(ctx, "has example.vip", srv.permissionHas(player, "example.vip"));
    var meta: [128]u8 = undefined;
    say(ctx, srv.permissionMeta("Steve", "prefix", &meta) orelse "");
    sayNumber(ctx, "unknown subject", srv.permissionCheck("Nobody", "example.vip"));
}

fn vipProvider(ctx: basalt.core.Ctx) void {
    var node_buf: [buffer_size]u8 = undefined;
    var kind_buf: [buffer_size]u8 = undefined;
    const node = srv.permissionQuery(ctx, "node", &node_buf) orelse "";
    const kind = srv.permissionQuery(ctx, "kind", &kind_buf) orelse "";
    if (kind.len > 0 and kind[0] == 'c') {
        _ = srv.permissionAnswer(ctx, if (node.len > 8 and node[8] == 'v') 1 else -1);
    } else {
        _ = srv.permissionAnswerMeta(ctx, "VIP");
    }
}

fn broadcast(ctx: basalt.core.Ctx) void {
    var args = basalt.core.Args.init();
    _ = args.add("text", "hello everyone");
    sayNumber(ctx, "reached", srv.broadcast("@server-line", &args, srv.broadcast_players | srv.broadcast_console));
    sayNumber(ctx, "console only", srv.broadcast("plain text", null, srv.broadcast_console));
    sayNumber(ctx, "nobody", srv.broadcast("x", null, 0));
    var filled: [256]u8 = undefined;
    say(ctx, srv.placeholderFill("%player_name% is here", sourcePlayer(ctx), &filled) orelse "");
}

fn info(ctx: basalt.core.Ctx) void {
    var buf: [buffer_size]u8 = undefined;
    const reply = srv.info(&buf);
    sayNumber(ctx, "info", code(reply));
    const value = reply catch null;
    sayNumber(ctx, "online", intOr(sub(value, "online"), -1));
    const software = if (sub(value, "software")) |s| s.asString() orelse "" else "";
    sayNumber(ctx, "software", @intCast(software.len));
    sayNumber(ctx, "plugins", countOf(sub(value, "plugins")));
    sayNumber(ctx, "lane", intOr(sub(sub(value, "plugin"), "lane"), -1));
}

fn numeric(value: ?values.Value, fallback: f64) f64 {
    const v = value orelse return fallback;
    if (v.asFloat()) |f| return f;
    if (v.asInt()) |i| return @floatFromInt(i);
    return fallback;
}

fn admin(ctx: basalt.core.Ctx) void {
    var buf: [buffer_size]u8 = undefined;
    var doc: [256]u8 = undefined;
    var key = values.Writer.init(&doc);
    key.beginMap();
    key.key("key");
    key.string("motd");
    key.end();
    var reply = srv.serverCall("settings.get", key.finish() catch &no_args, &buf);
    sayNumber(ctx, "settings.get", code(reply));
    const motd = if (reply) |v| v.asString() orelse "" else |_| "";
    sayNumber(ctx, "motd length", @intCast(motd.len));
    reply = srv.serverCall("tick.rate", &no_args, &buf);
    sayNumber(ctx, "tick.rate", code(reply));
    sayNumber(ctx, "rate", @intFromFloat(numeric(reply catch null, -1.0)));
    reply = srv.serverCall("ops", &no_args, &buf);
    sayNumber(ctx, "ops", code(reply));
    sayNumber(ctx, "op count", countOf(reply catch null));
    sayNumber(ctx, "unknown op", code(srv.serverCall("nope.nope", &no_args, &buf)));
    sayNumber(ctx, "datapacks", code(srv.serverCall("datapacks.list", &no_args, &buf)));
}

fn files(ctx: basalt.core.Ctx) void {
    sayNumber(ctx, "write", srv.fileWrite("notes/a.txt", "hello"));
    sayNumber(ctx, "exists", @intFromBool(srv.fileExists("notes/a.txt")));
    var text: [64]u8 = undefined;
    const size = srv.fileRead("notes/a.txt", text[0..63]);
    say(ctx, text[0..@intCast(@max(size, 0))]);
    var listing: [buffer_size]u8 = undefined;
    const list = srv.fileList("notes", &listing);
    sayNumber(ctx, "list", code(list));
    sayNumber(ctx, "entries", countOf(list catch null));
    var stat_buf: [buffer_size]u8 = undefined;
    const stat = srv.fileStat("notes/a.txt", &stat_buf);
    sayNumber(ctx, "stat", code(stat));
    sayNumber(ctx, "size", intOr(sub(stat catch null, "size"), -1));
    sayNumber(ctx, "outside", srv.fileWrite("secret/a.txt", "x"));
    sayNumber(ctx, "escape", srv.fileWrite("notes/../a.txt", "x"));
    var readme: [32]u8 = undefined;
    sayNumber(ctx, "packaged", srv.fileRead("data/readme.txt", &readme));
    sayNumber(ctx, "delete", srv.fileDelete("notes/a.txt", false));
    sayNumber(ctx, "gone", @intFromBool(srv.fileExists("notes/a.txt")));
}

fn store(ctx: basalt.core.Ctx) void {
    sayNumber(ctx, "set", srv.storeSetInt("visits", 3));
    var buf: [buffer_size]u8 = undefined;
    var reply = srv.storeGet("visits", &buf);
    sayNumber(ctx, "get", code(reply));
    sayNumber(ctx, "visits", intOr(reply catch null, -1));
    sayNumber(ctx, "missing", code(srv.storeGet("nothing", &buf)));
    var doc_buf: [256]u8 = undefined;
    var doc = values.Writer.init(&doc_buf);
    doc.beginMap();
    doc.key("kills");
    doc.int(7);
    doc.key("tags");
    doc.beginList();
    doc.string("a");
    doc.string("b");
    doc.end();
    doc.end();
    sayNumber(ctx, "write", srv.dataWrite("stats", doc.finish() catch &no_args));
    reply = srv.dataRead("stats", &buf);
    sayNumber(ctx, "read", code(reply));
    sayNumber(ctx, "kills", intOr(sub(reply catch null, "kills"), -1));
    sayNumber(ctx, "tags", countOf(sub(reply catch null, "tags")));
    reply = srv.configValue("shop", &buf);
    sayNumber(ctx, "config", code(reply));
    sayNumber(ctx, "price", intOr(sub(reply catch null, "sparkles-price"), -1));
    sayNumber(ctx, "dashed", code(srv.configValue("payout_period", &buf)));
}

fn echoService(ctx: basalt.core.Ctx) void {
    var buf: [buffer_size]u8 = undefined;
    const call = srv.result(ctx, &buf) catch null;
    var doc_buf: [64]u8 = undefined;
    var answer = values.Writer.init(&doc_buf);
    answer.beginMap();
    answer.key("echo");
    answer.int(intOr(sub(sub(call, "args"), "n"), 0) * 2);
    answer.end();
    _ = srv.serviceReply(ctx, answer.finish() catch &no_args);
}

fn echoDone(ctx: basalt.core.Ctx) void {
    var buf: [buffer_size]u8 = undefined;
    const reply = srv.result(ctx, &buf) catch null;
    const ok = if (sub(reply, "ok")) |v| v.asBool() orelse false else false;
    noteNumber("ok", @intFromBool(ok));
    noteNumber("echo", intOr(sub(sub(reply, "value"), "echo"), -1));
}

fn missingDone(ctx: basalt.core.Ctx) void {
    var buf: [buffer_size]u8 = undefined;
    const reply = srv.result(ctx, &buf) catch null;
    const ok = if (sub(reply, "ok")) |v| v.asBool() orelse true else true;
    noteNumber("late ok", @intFromBool(ok));
}

fn service(ctx: basalt.core.Ctx) void {
    sayNumber(ctx, "has", @intFromBool(srv.serviceHas("example.echo")));
    sayNumber(ctx, "has other", @intFromBool(srv.serviceHas("example.other")));
    var doc_buf: [64]u8 = undefined;
    var args = values.Writer.init(&doc_buf);
    args.beginMap();
    args.key("n");
    args.int(21);
    args.end();
    const doc = args.finish() catch &no_args;
    sayNumber(ctx, "call", @intFromBool(srv.serviceCall("example.echo", doc, 0, echoDone) > 0));
    sayNumber(ctx, "absent", srv.serviceCall("example.other", doc, 0, missingDone));
}

var waits: i64 = 0;

fn waited(_: basalt.core.Ctx) void {
    waits += 1;
    noteNumber("waited", waits);
}

fn placed(_: basalt.core.Ctx) void {
    waits += 1;
    noteNumber("placed", waits);
}

fn looked(ctx: basalt.core.Ctx) void {
    var buf: [buffer_size]u8 = undefined;
    const reply = srv.result(ctx, &buf) catch null;
    const uuid = if (sub(reply, "uuid")) |v| v.asString() orelse "" else "";
    const null_reply = if (reply) |v| v.isNull() else false;
    noteNumber("profile", if (null_reply) 0 else @intCast(uuid.len));
}

fn later(ctx: basalt.core.Ctx) void {
    sayNumber(ctx, "wait", @intFromBool(srv.wait(20, waited) > 0));
    sayNumber(ctx, "at", @intFromBool(srv.at("world", 0.0, 0.0, placed) > 0));
    sayNumber(ctx, "at unknown", srv.at("elsewhere", 0.0, 0.0, placed));
    sayNumber(ctx, "lookup", @intFromBool(srv.profileLookup("Notch", looked) > 0));
}

fn dynamic(ctx: basalt.core.Ctx) void {
    say(ctx, "dynamic ran");
    const amount = srv.commandArgF64(ctx, "amount") orelse 0.0;
    const flag = srv.commandArgBool(ctx, "loud") orelse false;
    sayNumber(ctx, "amount", @intFromFloat(amount * 10));
    sayNumber(ctx, "loud", @intFromBool(flag));
}

fn dynamicComplete(ctx: basalt.core.Ctx) void {
    _ = srv.completeAdd(ctx, "alpha");
    _ = srv.completeAdd(ctx, "beta");
    var buf: [256]u8 = undefined;
    const typed = srv.completeInput(ctx, &buf) orelse "";
    if (typed.len > 0) _ = srv.completeAdd(ctx, typed);
}

fn manage(ctx: basalt.core.Ctx) void {
    const spec = "dyn [amount: float] [loud: bool]";
    sayNumber(ctx, "register", srv.command(spec, "", dynamic, dynamicComplete));
    sayNumber(ctx, "again", srv.command(spec, "", dynamic, null));
    sayNumber(ctx, "alias", srv.commandAlias("dyn", "dy"));
    sayNumber(ctx, "alias missing", srv.commandAlias("nothing", "no"));
    sayNumber(ctx, "run", srv.commandRun("srv hello", 0));
    sayNumber(ctx, "run as player", srv.commandRun("srv hello", sourcePlayer(ctx)));
}

fn unmanage(ctx: basalt.core.Ctx) void {
    sayNumber(ctx, "unregister", srv.commandUnregister("dyn"));
    sayNumber(ctx, "unregister again", srv.commandUnregister("dyn"));
}

fn hello(ctx: basalt.core.Ctx) void {
    say(ctx, "hello from the command");
}

pub fn register() void {
    _ = srv.permissionProvide(10, srv.provide_check | srv.provide_meta, vipProvider);
    _ = srv.serviceRegister("example.echo", echoService);
    _ = basalt.commands.register("srv hello", "", hello);
    _ = basalt.commands.register("srv perms", "", permissions);
    _ = basalt.commands.register("srv broadcast", "", broadcast);
    _ = basalt.commands.register("srv info", "", info);
    _ = basalt.commands.register("srv admin", "", admin);
    _ = basalt.commands.register("srv files", "", files);
    _ = basalt.commands.register("srv store", "", store);
    _ = basalt.commands.register("srv service", "", service);
    _ = basalt.commands.register("srv later", "", later);
    _ = basalt.commands.register("srv manage", "", manage);
    _ = basalt.commands.register("srv unmanage", "", unmanage);
}
