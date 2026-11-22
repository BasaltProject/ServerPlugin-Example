package org.basaltmc.server.example.server;

import static org.basaltmc.server.commands.Commands.command;
import static org.basaltmc.server.commands.Commands.commandReply;
import static org.basaltmc.server.commands.Commands.commandSourceName;
import static org.basaltmc.server.log.Log.logInfo;
import static org.basaltmc.server.players.SharedPlayers.playerByName;
import org.basaltmc.server.core.Args;
import org.basaltmc.server.server.Server;
import org.basaltmc.server.server.Server.Reply;
import org.basaltmc.server.values.Values.Value;
import org.basaltmc.server.values.Values.Writer;

public final class ServerTour {
    private ServerTour() {}

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
        commandReply(ctx, "@server-line", new Args().add("text", text));
    }

    private static void sayNumber(int ctx, String label, long value) {
        say(ctx, label + " " + value);
    }

    private static void noteNumber(String label, long value) {
        logInfo(label + " " + value);
    }

    private static int sourcePlayer(int ctx) {
        return playerByName(commandSourceName(ctx));
    }

    private static void permissions(int ctx) {
        int player = sourcePlayer(ctx);
        sayNumber(ctx, "has example.vip", Server.permissionHas(player, "example.vip"));
        sayNumber(ctx, "console has example.vip", Server.permissionCheck("console", "example.vip"));
        sayNumber(ctx, "grant", Server.permissionGrant(player, "example.vip"));
        sayNumber(ctx, "has example.vip", Server.permissionHas(player, "example.vip"));
        sayNumber(ctx, "drop", Server.permissionDrop(player, "example.vip"));
        sayNumber(ctx, "has example.vip", Server.permissionHas(player, "example.vip"));
        String prefix = Server.permissionMeta("Steve", "prefix");
        say(ctx, prefix == null ? "" : prefix);
        sayNumber(ctx, "unknown subject", Server.permissionCheck("Nobody", "example.vip"));
    }

    private static void vipProvider(int ctx) {
        String node = Server.permissionQuery(ctx, "node");
        String kind = Server.permissionQuery(ctx, "kind");
        if (kind.charAt(0) == 'c') {
            Server.permissionAnswer(ctx, node.charAt(8) == 'v' ? 1 : -1);
        } else {
            Server.permissionAnswerMeta(ctx, "VIP");
        }
    }

    private static void broadcast(int ctx) {
        sayNumber(ctx, "reached", Server.broadcast("@server-line", new Args().add("text", "hello everyone"),
                Server.BROADCAST_PLAYERS | Server.BROADCAST_CONSOLE));
        sayNumber(ctx, "console only", Server.broadcast("plain text", null, Server.BROADCAST_CONSOLE));
        sayNumber(ctx, "nobody", Server.broadcast("x", null, 0));
        say(ctx, Server.placeholderFill("%player_name% is here", sourcePlayer(ctx)));
    }

    private static void info(int ctx) {
        Reply reply = Server.info();
        sayNumber(ctx, "info", reply.code);
        sayNumber(ctx, "online", i64(find(reply.value, "online"), -1));
        say(ctx, "software " + length(find(reply.value, "software")));
        Value plugins = find(reply.value, "plugins");
        sayNumber(ctx, "plugins", plugins == null ? -1 : plugins.count());
        sayNumber(ctx, "lane", i64(find(find(reply.value, "plugin"), "lane"), -1));
    }

    private static void admin(int ctx) {
        byte[] key = new Writer().beginMap().key("key").string("motd").end().finish();
        Reply reply = Server.call("settings.get", key);
        sayNumber(ctx, "settings.get", reply.code);
        say(ctx, "motd length " + length(reply.value));
        reply = Server.call("tick.rate", null);
        sayNumber(ctx, "tick.rate", reply.code);
        sayNumber(ctx, "rate", reply.value == null ? -1 : (long) reply.value.asDouble(-1.0));
        reply = Server.call("ops", null);
        sayNumber(ctx, "ops", reply.code);
        sayNumber(ctx, "op count", reply.value == null ? -1 : reply.value.count());
        sayNumber(ctx, "unknown op", Server.call("nope.nope", null).code);
        sayNumber(ctx, "datapacks", Server.call("datapacks.list", null).code);
    }

    private static void files(int ctx) {
        sayNumber(ctx, "write", Server.fileWrite("notes/a.txt", "hello"));
        sayNumber(ctx, "exists", Server.fileExists("notes/a.txt") ? 1 : 0);
        byte[] text = new byte[63];
        int size = Server.fileRead("notes/a.txt", text);
        say(ctx, new String(text, 0, size > 0 ? Math.min(size, text.length) : 0));
        Reply listing = Server.fileList("notes");
        sayNumber(ctx, "list", listing.code);
        sayNumber(ctx, "entries", listing.value == null ? -1 : listing.value.count());
        Reply stat = Server.fileStat("notes/a.txt");
        sayNumber(ctx, "stat", stat.code);
        sayNumber(ctx, "size", i64(find(stat.value, "size"), -1));
        sayNumber(ctx, "outside", Server.fileWrite("secret/a.txt", "x"));
        sayNumber(ctx, "escape", Server.fileWrite("notes/../a.txt", "x"));
        sayNumber(ctx, "packaged", Server.fileRead("data/readme.txt", new byte[32]));
        sayNumber(ctx, "delete", Server.fileDelete("notes/a.txt", false));
        sayNumber(ctx, "gone", Server.fileExists("notes/a.txt") ? 1 : 0);
    }

    private static void store(int ctx) {
        sayNumber(ctx, "set", Server.storeSet("visits", 3L));
        Reply reply = Server.storeGet("visits");
        sayNumber(ctx, "get", reply.code);
        sayNumber(ctx, "visits", reply.value == null ? -1 : reply.value.asLong(-1));
        sayNumber(ctx, "missing", Server.storeGet("nothing").code);
        byte[] doc = new Writer().beginMap().key("kills").integer(7).key("tags").beginList().string("a").string("b")
                .end().end().finish();
        sayNumber(ctx, "write", Server.dataWrite("stats", doc));
        reply = Server.dataRead("stats");
        sayNumber(ctx, "read", reply.code);
        sayNumber(ctx, "kills", i64(find(reply.value, "kills"), -1));
        Value tags = find(reply.value, "tags");
        sayNumber(ctx, "tags", tags == null ? -1 : tags.count());
        reply = Server.configValue("shop");
        sayNumber(ctx, "config", reply.code);
        sayNumber(ctx, "price", i64(find(reply.value, "sparkles-price"), -1));
        sayNumber(ctx, "dashed", Server.configValue("payout_period").code);
    }

    private static void echoService(int ctx) {
        Reply call = Server.result(ctx);
        long n = i64(find(find(call.value, "args"), "n"), 0);
        Server.serviceReply(ctx, new Writer().beginMap().key("echo").integer(n * 2).end().finish());
    }

    private static void echoDone(int ctx) {
        Reply reply = Server.result(ctx);
        Value ok = find(reply.value, "ok");
        noteNumber("ok", ok != null && ok.asBool(false) ? 1 : 0);
        noteNumber("echo", i64(find(find(reply.value, "value"), "echo"), -1));
    }

    private static void missingDone(int ctx) {
        Reply reply = Server.result(ctx);
        Value ok = find(reply.value, "ok");
        noteNumber("late ok", ok == null || ok.asBool(true) ? 1 : 0);
    }

    private static void service(int ctx) {
        sayNumber(ctx, "has", Server.serviceHas("example.echo") ? 1 : 0);
        sayNumber(ctx, "has other", Server.serviceHas("example.other") ? 1 : 0);
        byte[] args = new Writer().beginMap().key("n").integer(21).end().finish();
        sayNumber(ctx, "call", Server.serviceCall("example.echo", args, 0, ServerTour::echoDone) > 0 ? 1 : 0);
        sayNumber(ctx, "absent", Server.serviceCall("example.other", args, 0, ServerTour::missingDone));
    }

    private static void waited(int ctx) {
        noteNumber("waited", ++g_waits);
    }

    private static void placed(int ctx) {
        noteNumber("placed", ++g_waits);
    }

    private static void looked(int ctx) {
        Reply reply = Server.result(ctx);
        noteNumber("profile", reply.value == null || reply.value.isNull() ? 0 : length(find(reply.value, "uuid")));
    }

    private static void later(int ctx) {
        sayNumber(ctx, "wait", Server.wait(20, ServerTour::waited) > 0 ? 1 : 0);
        sayNumber(ctx, "at", Server.at("world", 0.0, 0.0, ServerTour::placed) > 0 ? 1 : 0);
        sayNumber(ctx, "at unknown", Server.at("elsewhere", 0.0, 0.0, ServerTour::placed));
        sayNumber(ctx, "lookup", Server.profileLookup("Notch", ServerTour::looked) > 0 ? 1 : 0);
    }

    private static void dynamic(int ctx) {
        commandReply(ctx, "@server-line", new Args().add("text", "dynamic ran"));
        Double amount = Server.commandArgF64(ctx, "amount");
        Boolean flag = Server.commandArgBool(ctx, "loud");
        sayNumber(ctx, "amount", (long) ((amount == null ? 0.0 : amount) * 10));
        sayNumber(ctx, "loud", flag != null && flag ? 1 : 0);
    }

    private static void dynamicComplete(int ctx) {
        Server.completeAdd(ctx, "alpha");
        Server.completeAdd(ctx, "beta");
        String typed = Server.completeInput(ctx);
        if (typed != null && !typed.isEmpty()) {
            Server.completeAdd(ctx, typed);
        }
    }

    private static void manage(int ctx) {
        String spec = "dyn [amount: float] [loud: bool]";
        sayNumber(ctx, "register", Server.command(spec, "", ServerTour::dynamic, ServerTour::dynamicComplete));
        sayNumber(ctx, "again", Server.command(spec, "", ServerTour::dynamic));
        sayNumber(ctx, "alias", Server.commandAlias("dyn", "dy"));
        sayNumber(ctx, "alias missing", Server.commandAlias("nothing", "no"));
        sayNumber(ctx, "run", Server.commandRun("srv hello", 0));
        sayNumber(ctx, "run as player", Server.commandRun("srv hello", sourcePlayer(ctx)));
    }

    private static void unmanage(int ctx) {
        sayNumber(ctx, "unregister", Server.commandUnregister("dyn"));
        sayNumber(ctx, "unregister again", Server.commandUnregister("dyn"));
    }

    private static void hello(int ctx) {
        say(ctx, "hello from the command");
    }

    public static void register() {
        Server.permissionProvide(10, Server.PROVIDE_CHECK | Server.PROVIDE_META, ServerTour::vipProvider);
        Server.serviceRegister("example.echo", ServerTour::echoService);
        command("srv hello", "", ServerTour::hello);
        command("srv perms", "", ServerTour::permissions);
        command("srv broadcast", "", ServerTour::broadcast);
        command("srv info", "", ServerTour::info);
        command("srv admin", "", ServerTour::admin);
        command("srv files", "", ServerTour::files);
        command("srv store", "", ServerTour::store);
        command("srv service", "", ServerTour::service);
        command("srv later", "", ServerTour::later);
        command("srv manage", "", ServerTour::manage);
        command("srv unmanage", "", ServerTour::unmanage);
    }
}
