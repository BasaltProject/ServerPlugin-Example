package org.basaltmc.server.example.services;

import org.basaltmc.server.commands.Registry;
import org.basaltmc.server.core.Args;
import org.basaltmc.server.services.RuntimeServices;
import org.basaltmc.server.services.RuntimeServices.Reply;
import org.basaltmc.server.values.Documents.Value;
import org.basaltmc.server.values.Documents.Writer;

import static org.basaltmc.server.commands.Sender.name;
import static org.basaltmc.server.commands.Sender.reply;
import static org.basaltmc.server.log.Logger.logInfo;
import static org.basaltmc.server.players.Directory.playerByName;

public final class ServiceExamples {
    private ServiceExamples() {}

    private static int g_waits;

    private static long i64(Value value, long fallback) {
        return value == null ? fallback : value.asLong(fallback);
    }

    private static int length(Value value) {
        byte[] text = value == null ? null : value.asStringBytes();
        return text == null ? 0 : text.length;
    }

    private static Value find(Value value, String key) {
        return value == null ? null : value.find(key);
    }

    private static void say(int ctx, String text) {
        reply(ctx, "@server-line", new Args().add("text", text));
    }

    private static void sayNumber(int ctx, String label, long value) {
        say(ctx, label + " " + value);
    }

    private static void noteNumber(String label, long value) {
        logInfo(label + " " + value);
    }

    private static int sourcePlayer(int ctx) {
        return playerByName(name(ctx));
    }

    private static void permissions(int ctx) {
        int player = sourcePlayer(ctx);
        sayNumber(ctx, "has example.vip", RuntimeServices.permissionHas(player, "example.vip"));
        sayNumber(ctx, "console has example.vip", RuntimeServices.permissionCheck("console", "example.vip"));
        sayNumber(ctx, "grant", RuntimeServices.permissionGrant(player, "example.vip"));
        sayNumber(ctx, "has example.vip", RuntimeServices.permissionHas(player, "example.vip"));
        sayNumber(ctx, "drop", RuntimeServices.permissionDrop(player, "example.vip"));
        sayNumber(ctx, "has example.vip", RuntimeServices.permissionHas(player, "example.vip"));
        String prefix = RuntimeServices.permissionMeta("Steve", "prefix");
        say(ctx, prefix == null ? "" : prefix);
        sayNumber(ctx, "unknown subject", RuntimeServices.permissionCheck("Nobody", "example.vip"));
    }

    private static void vipProvider(int ctx) {
        String node = RuntimeServices.permissionQuery(ctx, "node");
        String kind = RuntimeServices.permissionQuery(ctx, "kind");
        if (kind.charAt(0) == 'c') {
            RuntimeServices.permissionAnswer(ctx, node.charAt(8) == 'v' ? 1 : -1);
        } else {
            RuntimeServices.permissionAnswerMeta(ctx, "VIP");
        }
    }

    private static void broadcast(int ctx) {
        sayNumber(ctx, "reached", RuntimeServices.broadcast("@server-line", new Args().add("text", "hello everyone"),
                RuntimeServices.BROADCAST_PLAYERS | RuntimeServices.BROADCAST_CONSOLE));
        sayNumber(ctx, "console only", RuntimeServices.broadcast("plain text", null, RuntimeServices.BROADCAST_CONSOLE));
        sayNumber(ctx, "nobody", RuntimeServices.broadcast("x", null, 0));
        say(ctx, RuntimeServices.placeholderFill("%player_name% is here", sourcePlayer(ctx)));
    }

    private static void info(int ctx) {
        Reply reply = RuntimeServices.info();
        sayNumber(ctx, "info", reply.code);
        sayNumber(ctx, "online", i64(find(reply.value, "online"), -1));
        say(ctx, "software " + length(find(reply.value, "software")));
        Value plugins = find(reply.value, "plugins");
        sayNumber(ctx, "plugins", plugins == null ? -1 : plugins.count());
        sayNumber(ctx, "lane", i64(find(find(reply.value, "plugin"), "lane"), -1));
    }

    private static void admin(int ctx) {
        byte[] key = new Writer().beginMap().key("key").string("motd").end().finish();
        Reply reply = RuntimeServices.call("settings.get", key);
        sayNumber(ctx, "settings.get", reply.code);
        say(ctx, "motd length " + length(reply.value));
        reply = RuntimeServices.call("tick.rate", null);
        sayNumber(ctx, "tick.rate", reply.code);
        sayNumber(ctx, "rate", reply.value == null ? -1 : (long) reply.value.asDouble(-1.0));
        reply = RuntimeServices.call("ops", null);
        sayNumber(ctx, "ops", reply.code);
        sayNumber(ctx, "op count", reply.value == null ? -1 : reply.value.count());
        sayNumber(ctx, "unknown op", RuntimeServices.call("nope.nope", null).code);
        sayNumber(ctx, "datapacks", RuntimeServices.call("datapacks.list", null).code);
    }

    private static void files(int ctx) {
        sayNumber(ctx, "write", RuntimeServices.fileWrite("notes/a.txt", "hello"));
        sayNumber(ctx, "exists", RuntimeServices.fileExists("notes/a.txt") ? 1 : 0);
        byte[] text = new byte[63];
        int size = RuntimeServices.fileRead("notes/a.txt", text);
        say(ctx, new String(text, 0, size > 0 ? Math.min(size, text.length) : 0));
        Reply listing = RuntimeServices.fileList("notes");
        sayNumber(ctx, "list", listing.code);
        sayNumber(ctx, "entries", listing.value == null ? -1 : listing.value.count());
        Reply stat = RuntimeServices.fileStat("notes/a.txt");
        sayNumber(ctx, "stat", stat.code);
        sayNumber(ctx, "size", i64(find(stat.value, "size"), -1));
        sayNumber(ctx, "outside", RuntimeServices.fileWrite("secret/a.txt", "x"));
        sayNumber(ctx, "escape", RuntimeServices.fileWrite("notes/../a.txt", "x"));
        sayNumber(ctx, "packaged", RuntimeServices.fileRead("data/readme.txt", new byte[32]));
        sayNumber(ctx, "delete", RuntimeServices.fileDelete("notes/a.txt", false));
        sayNumber(ctx, "gone", RuntimeServices.fileExists("notes/a.txt") ? 1 : 0);
    }

    private static void store(int ctx) {
        sayNumber(ctx, "set", RuntimeServices.storeSet("visits", 3L));
        Reply reply = RuntimeServices.storeGet("visits");
        sayNumber(ctx, "get", reply.code);
        sayNumber(ctx, "visits", reply.value == null ? -1 : reply.value.asLong(-1));
        sayNumber(ctx, "missing", RuntimeServices.storeGet("nothing").code);
        byte[] doc = new Writer().beginMap().key("kills").integer(7).key("tags").beginList().string("a").string("b")
                .end().end().finish();
        sayNumber(ctx, "write", RuntimeServices.dataWrite("stats", doc));
        reply = RuntimeServices.dataRead("stats");
        sayNumber(ctx, "read", reply.code);
        sayNumber(ctx, "kills", i64(find(reply.value, "kills"), -1));
        Value tags = find(reply.value, "tags");
        sayNumber(ctx, "tags", tags == null ? -1 : tags.count());
        reply = RuntimeServices.configValue("shop");
        sayNumber(ctx, "config", reply.code);
        sayNumber(ctx, "price", i64(find(reply.value, "sparkles-price"), -1));
        sayNumber(ctx, "dashed", RuntimeServices.configValue("payout_period").code);
    }

    private static void echoService(int ctx) {
        Reply call = RuntimeServices.result(ctx);
        long n = i64(find(find(call.value, "args"), "n"), 0);
        RuntimeServices.serviceReply(ctx, new Writer().beginMap().key("echo").integer(n * 2).end().finish());
    }

    private static void echoDone(int ctx) {
        Reply reply = RuntimeServices.result(ctx);
        Value ok = find(reply.value, "ok");
        noteNumber("ok", ok != null && ok.asBool(false) ? 1 : 0);
        noteNumber("echo", i64(find(find(reply.value, "value"), "echo"), -1));
    }

    private static void missingDone(int ctx) {
        Reply reply = RuntimeServices.result(ctx);
        Value ok = find(reply.value, "ok");
        noteNumber("late ok", ok == null || ok.asBool(true) ? 1 : 0);
    }

    private static void service(int ctx) {
        sayNumber(ctx, "has", RuntimeServices.serviceHas("example.echo") ? 1 : 0);
        sayNumber(ctx, "has other", RuntimeServices.serviceHas("example.other") ? 1 : 0);
        byte[] args = new Writer().beginMap().key("n").integer(21).end().finish();
        sayNumber(ctx, "call", RuntimeServices.serviceCall("example.echo", args, 0, ServiceExamples::echoDone) > 0 ? 1 : 0);
        sayNumber(ctx, "absent", RuntimeServices.serviceCall("example.other", args, 0, ServiceExamples::missingDone));
    }

    private static void waited(int ctx) {
        noteNumber("waited", ++g_waits);
    }

    private static void placed(int ctx) {
        noteNumber("placed", ++g_waits);
    }

    private static void looked(int ctx) {
        Reply reply = RuntimeServices.result(ctx);
        noteNumber("profile", reply.value == null || reply.value.isNull() ? 0 : length(find(reply.value, "uuid")));
    }

    private static void later(int ctx) {
        sayNumber(ctx, "wait", RuntimeServices.wait(20, ServiceExamples::waited) > 0 ? 1 : 0);
        sayNumber(ctx, "at", RuntimeServices.at("world", 0.0, 0.0, ServiceExamples::placed) > 0 ? 1 : 0);
        sayNumber(ctx, "at unknown", RuntimeServices.at("elsewhere", 0.0, 0.0, ServiceExamples::placed));
        sayNumber(ctx, "lookup", RuntimeServices.profileLookup("Notch", ServiceExamples::looked) > 0 ? 1 : 0);
    }

    private static void dynamic(int ctx) {
        reply(ctx, "@server-line", new Args().add("text", "dynamic ran"));
        Double amount = RuntimeServices.commandArgF64(ctx, "amount");
        Boolean flag = RuntimeServices.commandArgBool(ctx, "loud");
        sayNumber(ctx, "amount", (long) ((amount == null ? 0.0 : amount) * 10));
        sayNumber(ctx, "loud", flag != null && flag ? 1 : 0);
    }

    private static void dynamicComplete(int ctx) {
        RuntimeServices.completeAdd(ctx, "alpha");
        RuntimeServices.completeAdd(ctx, "beta");
        String typed = RuntimeServices.completeInput(ctx);
        if (typed != null && !typed.isEmpty()) {
            RuntimeServices.completeAdd(ctx, typed);
        }
    }

    private static void manage(int ctx) {
        String spec = "dyn [amount: float] [loud: bool]";
        sayNumber(ctx, "register", RuntimeServices.command(spec, "", ServiceExamples::dynamic, ServiceExamples::dynamicComplete));
        sayNumber(ctx, "again", RuntimeServices.command(spec, "", ServiceExamples::dynamic));
        sayNumber(ctx, "alias", RuntimeServices.commandAlias("dyn", "dy"));
        sayNumber(ctx, "alias missing", RuntimeServices.commandAlias("nothing", "no"));
        sayNumber(ctx, "run", RuntimeServices.commandRun("srv hello", 0));
        sayNumber(ctx, "run as player", RuntimeServices.commandRun("srv hello", sourcePlayer(ctx)));
    }

    private static void unmanage(int ctx) {
        sayNumber(ctx, "unregister", RuntimeServices.commandUnregister("dyn"));
        sayNumber(ctx, "unregister again", RuntimeServices.commandUnregister("dyn"));
    }

    private static void hello(int ctx) {
        say(ctx, "hello from the command");
    }

    public static void register() {
        RuntimeServices.permissionProvide(10, RuntimeServices.PROVIDE_CHECK | RuntimeServices.PROVIDE_META, ServiceExamples::vipProvider);
        RuntimeServices.serviceRegister("example.echo", ServiceExamples::echoService);
        Registry.register("srv hello", "", ServiceExamples::hello);
        Registry.register("srv perms", "", ServiceExamples::permissions);
        Registry.register("srv broadcast", "", ServiceExamples::broadcast);
        Registry.register("srv info", "", ServiceExamples::info);
        Registry.register("srv admin", "", ServiceExamples::admin);
        Registry.register("srv files", "", ServiceExamples::files);
        Registry.register("srv store", "", ServiceExamples::store);
        Registry.register("srv service", "", ServiceExamples::service);
        Registry.register("srv later", "", ServiceExamples::later);
        Registry.register("srv manage", "", ServiceExamples::manage);
        Registry.register("srv unmanage", "", ServiceExamples::unmanage);
    }
}
