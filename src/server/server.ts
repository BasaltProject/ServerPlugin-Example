import * as bsCommands from "basalt-server/commands";
import * as bsCore from "basalt-server/core";
import * as bsLog from "basalt-server/log";
import * as bsPlayers from "basalt-server/players";
import * as bsServer from "basalt-server/server";
import * as bsValues from "basalt-server/values";

function say(ctx: bsCore.Ctx, text: string): void {
  bsCommands.commandReply(ctx, "@server-line", new bsCore.Args().add("text", text));
}

function sayNumber(ctx: bsCore.Ctx, label: string, value: i64): void {
  say(ctx, label + " " + value.toString());
}

function noteNumber(label: string, value: i64): void {
  bsLog.logInfo(label + " " + value.toString());
}

function str(text: string | null): string {
  return text === null ? "" : text;
}

function find(root: bsValues.Value | null, name: string): bsValues.Value | null {
  return root === null ? null : root.find(name);
}

function intOr(value: bsValues.Value | null, fallback: i64): i64 {
  return value === null ? fallback : value.toInt(fallback);
}

function strOf(value: bsValues.Value | null): string | null {
  if (value === null) return null;
  return value.toStr();
}

function byteLength(text: string | null): i32 {
  if (text === null) return 0;
  return String.UTF8.byteLength(text);
}

function countOf(value: bsValues.Value | null): i64 {
  return value === null ? -1 : value.count();
}

function sourcePlayer(ctx: bsCore.Ctx): bsCore.Player {
  return bsPlayers.playerByName(str(bsCommands.commandSourceName(ctx)));
}

function permissions(ctx: bsCore.Ctx): void {
  const player = sourcePlayer(ctx);
  sayNumber(ctx, "has example.vip", bsServer.permissionHas(player, "example.vip"));
  sayNumber(ctx, "console has example.vip", bsServer.permissionCheck("console", "example.vip"));
  sayNumber(ctx, "grant", bsServer.permissionGrant(player, "example.vip"));
  sayNumber(ctx, "has example.vip", bsServer.permissionHas(player, "example.vip"));
  sayNumber(ctx, "drop", bsServer.permissionDrop(player, "example.vip"));
  sayNumber(ctx, "has example.vip", bsServer.permissionHas(player, "example.vip"));
  say(ctx, str(bsServer.permissionMeta("Steve", "prefix")));
  sayNumber(ctx, "unknown subject", bsServer.permissionCheck("Nobody", "example.vip"));
}

function vipProvider(ctx: bsCore.Ctx): void {
  const node = str(bsServer.permissionQuery(ctx, "node"));
  const kind = str(bsServer.permissionQuery(ctx, "kind"));
  if (kind.charCodeAt(0) == 99) {
    bsServer.permissionAnswer(ctx, node.charCodeAt(8) == 118 ? 1 : -1);
  } else {
    bsServer.permissionAnswerMeta(ctx, "VIP");
  }
}

function broadcast(ctx: bsCore.Ctx): void {
  sayNumber(
    ctx,
    "reached",
    bsServer.broadcast("@server-line", new bsCore.Args().add("text", "hello everyone"), bsServer.BROADCAST_PLAYERS | bsServer.BROADCAST_CONSOLE),
  );
  sayNumber(ctx, "console only", bsServer.broadcast("plain text", null, bsServer.BROADCAST_CONSOLE));
  sayNumber(ctx, "nobody", bsServer.broadcast("x", null, 0));
  say(ctx, str(bsServer.placeholderFill("%player_name% is here", sourcePlayer(ctx))));
}

function info(ctx: bsCore.Ctx): void {
  const reply = bsServer.serverInfo();
  const root = reply.value();
  sayNumber(ctx, "info", reply.error);
  sayNumber(ctx, "online", intOr(find(root, "online"), -1));
  say(ctx, "software " + byteLength(strOf(find(root, "software"))).toString());
  sayNumber(ctx, "plugins", countOf(find(root, "plugins")));
  sayNumber(ctx, "lane", intOr(find(find(root, "plugin"), "lane"), -1));
}

function admin(ctx: bsCore.Ctx): void {
  const key = new bsValues.ValueWriter(256).beginMap().key("key").string("motd").end().finish();
  let reply = bsServer.serverCall("settings.get", key);
  sayNumber(ctx, "settings.get", reply.error);
  say(ctx, "motd length " + byteLength(strOf(reply.value())).toString());
  reply = bsServer.serverCall("tick.rate", null);
  sayNumber(ctx, "tick.rate", reply.error);
  const rate = reply.value();
  sayNumber(ctx, "rate", rate === null ? -1 : rate.kind() == bsValues.ValueKind.Int ? rate.toInt(-1) : <i64>rate.toFloat(-1.0));
  reply = bsServer.serverCall("ops", null);
  sayNumber(ctx, "ops", reply.error);
  sayNumber(ctx, "op count", countOf(reply.value()));
  sayNumber(ctx, "unknown op", bsServer.serverCall("nope.nope", null).error);
  sayNumber(ctx, "datapacks", bsServer.serverCall("datapacks.list", null).error);
}

function files(ctx: bsCore.Ctx): void {
  sayNumber(ctx, "write", bsServer.fileWrite("notes/a.txt", "hello"));
  sayNumber(ctx, "exists", bsServer.fileExists("notes/a.txt") ? 1 : 0);
  const text = new Uint8Array(64);
  const size = bsServer.fileRead("notes/a.txt", text, text.length - 1);
  say(ctx, size > 0 ? String.UTF8.decodeUnsafe(changetype<usize>(text.dataStart), size) : "");
  const listing = bsServer.fileList("notes");
  sayNumber(ctx, "list", listing.error);
  sayNumber(ctx, "entries", countOf(listing.value()));
  const stat = bsServer.fileStat("notes/a.txt");
  sayNumber(ctx, "stat", stat.error);
  sayNumber(ctx, "size", intOr(find(stat.value(), "size"), -1));
  sayNumber(ctx, "outside", bsServer.fileWrite("secret/a.txt", "x"));
  sayNumber(ctx, "escape", bsServer.fileWrite("notes/../a.txt", "x"));
  const readme = new Uint8Array(32);
  sayNumber(ctx, "packaged", bsServer.fileRead("data/readme.txt", readme, readme.length));
  sayNumber(ctx, "delete", bsServer.fileDelete("notes/a.txt"));
  sayNumber(ctx, "gone", bsServer.fileExists("notes/a.txt") ? 1 : 0);
}

function store(ctx: bsCore.Ctx): void {
  sayNumber(ctx, "set", bsServer.storeSetInt("visits", 3));
  let reply = bsServer.storeGet("visits");
  sayNumber(ctx, "get", reply.error);
  sayNumber(ctx, "visits", intOr(reply.value(), -1));
  sayNumber(ctx, "missing", bsServer.storeGet("nothing").error);
  const doc = new bsValues.ValueWriter(256)
    .beginMap()
    .key("kills")
    .int(7)
    .key("tags")
    .beginList()
    .string("a")
    .string("b")
    .end()
    .end()
    .finish();
  sayNumber(ctx, "write", bsServer.dataWrite("stats", doc));
  reply = bsServer.dataRead("stats");
  sayNumber(ctx, "read", reply.error);
  sayNumber(ctx, "kills", intOr(find(reply.value(), "kills"), -1));
  sayNumber(ctx, "tags", countOf(find(reply.value(), "tags")));
  reply = bsServer.configValue("shop");
  sayNumber(ctx, "config", reply.error);
  sayNumber(ctx, "price", intOr(find(reply.value(), "sparkles-price"), -1));
  sayNumber(ctx, "dashed", bsServer.configValue("payout_period").error);
}

function echoService(ctx: bsCore.Ctx): void {
  const call = bsServer.valueResult(ctx).value();
  const n = intOr(find(find(call, "args"), "n"), 0);
  bsServer.serviceReply(ctx, new bsValues.ValueWriter(256).beginMap().key("echo").int(n * 2).end().finish());
}

function echoDone(ctx: bsCore.Ctx): void {
  const reply = bsServer.valueResult(ctx).value();
  const ok = find(reply, "ok");
  noteNumber("ok", ok !== null && ok.toBool(false) ? 1 : 0);
  noteNumber("echo", intOr(find(find(reply, "value"), "echo"), -1));
}

function missingDone(ctx: bsCore.Ctx): void {
  const ok = find(bsServer.valueResult(ctx).value(), "ok");
  noteNumber("late ok", ok === null || ok.toBool(true) ? 1 : 0);
}

function service(ctx: bsCore.Ctx): void {
  sayNumber(ctx, "has", bsServer.serviceHas("example.echo") ? 1 : 0);
  sayNumber(ctx, "has other", bsServer.serviceHas("example.other") ? 1 : 0);
  const args = new bsValues.ValueWriter(256).beginMap().key("n").int(21).end().finish();
  sayNumber(ctx, "call", bsServer.serviceCall("example.echo", args, 0, echoDone) > 0 ? 1 : 0);
  sayNumber(ctx, "absent", bsServer.serviceCall("example.other", args, 0, missingDone));
}

let waits: i64 = 0;

function waited(ctx: bsCore.Ctx): void {
  noteNumber("waited", ++waits);
}

function placed(ctx: bsCore.Ctx): void {
  noteNumber("placed", ++waits);
}

function looked(ctx: bsCore.Ctx): void {
  const reply = bsServer.valueResult(ctx).value();
  noteNumber("profile", byteLength(strOf(find(reply, "uuid"))));
}

function later(ctx: bsCore.Ctx): void {
  sayNumber(ctx, "wait", bsServer.taskWait(20, waited) > 0 ? 1 : 0);
  sayNumber(ctx, "at", bsServer.taskAt("world", 0.0, 0.0, placed) > 0 ? 1 : 0);
  sayNumber(ctx, "at unknown", bsServer.taskAt("elsewhere", 0.0, 0.0, placed));
  sayNumber(ctx, "lookup", bsServer.profileLookup("Notch", looked) > 0 ? 1 : 0);
}

function dynamic(ctx: bsCore.Ctx): void {
  say(ctx, "dynamic ran");
  const amount = bsServer.commandArgF64(ctx, "amount", 0.0);
  const loud = bsServer.commandArgBool(ctx, "loud", false);
  sayNumber(ctx, "amount", <i64>(amount * 10));
  sayNumber(ctx, "loud", loud ? 1 : 0);
}

function dynamicComplete(ctx: bsCore.Ctx): void {
  bsServer.completeAdd(ctx, "alpha");
  bsServer.completeAdd(ctx, "beta");
  const typed = str(bsServer.completeInput(ctx));
  if (typed.length > 0) bsServer.completeAdd(ctx, typed);
}

function manage(ctx: bsCore.Ctx): void {
  sayNumber(ctx, "register", bsServer.commandEx("dyn [amount: float] [loud: bool]", "", dynamic, dynamicComplete));
  sayNumber(ctx, "again", bsServer.commandEx("dyn [amount: float] [loud: bool]", "", dynamic));
  sayNumber(ctx, "alias", bsServer.commandAlias("dyn", "dy"));
  sayNumber(ctx, "alias missing", bsServer.commandAlias("nothing", "no"));
  sayNumber(ctx, "run", bsServer.commandRun("srv hello", 0));
  sayNumber(ctx, "run as player", bsServer.commandRun("srv hello", sourcePlayer(ctx)));
}

function unmanage(ctx: bsCore.Ctx): void {
  sayNumber(ctx, "unregister", bsServer.commandUnregister("dyn"));
  sayNumber(ctx, "unregister again", bsServer.commandUnregister("dyn"));
}

function hello(ctx: bsCore.Ctx): void {
  say(ctx, "hello from the command");
}

export function register(): void {
  bsServer.permissionProvide(10, bsServer.PROVIDE_CHECK | bsServer.PROVIDE_META, vipProvider);
  bsServer.serviceRegister("example.echo", echoService);
  bsCommands.command("srv hello", "", hello);
  bsCommands.command("srv perms", "", permissions);
  bsCommands.command("srv broadcast", "", broadcast);
  bsCommands.command("srv info", "", info);
  bsCommands.command("srv admin", "", admin);
  bsCommands.command("srv files", "", files);
  bsCommands.command("srv store", "", store);
  bsCommands.command("srv service", "", service);
  bsCommands.command("srv later", "", later);
  bsCommands.command("srv manage", "", manage);
  bsCommands.command("srv unmanage", "", unmanage);
}
