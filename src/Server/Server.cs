using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkCommands = global::Basalt.Server.Commands.Commands;
using SdkLogger = global::Basalt.Server.Log.Logger;
using SdkPlayers = global::Basalt.Server.Players.Players;
using SdkServerArea = global::Basalt.Server.Server.ServerArea;
using System;
using System.Text;

namespace Basalt.Server.Example.Server;


public static class ServerExample
{
    static void Say(int ctx, string text)
    {
        Args args = new();
        args.Add("text", text);
        SdkCommands.CommandReply(ctx, "@server-line", args);
    }

    static void SayNumber(int ctx, string label, long value) => Say(ctx, label + " " + value.ToString());

    static void NoteNumber(string label, long value) => SdkLogger.LogInfo(label + " " + value.ToString());

    static int SourcePlayer(int ctx) => SdkPlayers.PlayerByName(SdkCommands.CommandSourceName(ctx) ?? "");

    static long IntOr(Value value, long fallback) => value.TryInt(out long n) ? n : fallback;

    static int StrLength(Value value) => value.TryStringBytes(out byte[] data) ? data.Length : 0;

    static void Permissions(int ctx)
    {
        int player = SourcePlayer(ctx);
        SayNumber(ctx, "has example.vip", SdkServerArea.PermissionHas(player, "example.vip"));
        SayNumber(ctx, "console has example.vip", SdkServerArea.PermissionCheck("console", "example.vip"));
        SayNumber(ctx, "grant", SdkServerArea.PermissionGrant(player, "example.vip"));
        SayNumber(ctx, "has example.vip", SdkServerArea.PermissionHas(player, "example.vip"));
        SayNumber(ctx, "drop", SdkServerArea.PermissionDrop(player, "example.vip"));
        SayNumber(ctx, "has example.vip", SdkServerArea.PermissionHas(player, "example.vip"));
        Say(ctx, SdkServerArea.PermissionMeta("Steve", "prefix") ?? "");
        SayNumber(ctx, "unknown subject", SdkServerArea.PermissionCheck("Nobody", "example.vip"));
    }

    static void VipProvider(int ctx)
    {
        string node = SdkServerArea.PermissionQuery(ctx, "node") ?? "";
        string kind = SdkServerArea.PermissionQuery(ctx, "kind") ?? "";
        if (kind.Length > 0 && kind[0] == 'c')
        {
            SdkServerArea.PermissionAnswer(ctx, node.Length > 8 && node[8] == 'v' ? 1 : -1);
        }
        else
        {
            SdkServerArea.PermissionAnswerMeta(ctx, "VIP");
        }
    }

    static void Broadcast(int ctx)
    {
        Args args = new();
        args.Add("text", "hello everyone");
        SayNumber(ctx, "reached", SdkServerArea.Broadcast("@server-line", args, SdkServerArea.BroadcastPlayers | SdkServerArea.BroadcastConsole));
        SayNumber(ctx, "console only", SdkServerArea.Broadcast("plain text", null, SdkServerArea.BroadcastConsole));
        SayNumber(ctx, "nobody", SdkServerArea.Broadcast("x", null, 0));
        Say(ctx, SdkServerArea.PlaceholderFill("%player_name% is here", SourcePlayer(ctx)) ?? "");
    }

    static void Info(int ctx)
    {
        SayNumber(ctx, "info", SdkServerArea.Info(out Value reply));
        SayNumber(ctx, "online", IntOr(reply.Find("online"), -1));
        Say(ctx, "software " + StrLength(reply.Find("software")).ToString());
        SayNumber(ctx, "plugins", reply.Find("plugins").Count);
        SayNumber(ctx, "lane", IntOr(reply.Find("plugin").Find("lane"), -1));
    }

    static void Admin(int ctx)
    {
        byte[] key = new ValueWriter(256).BeginMap().Key("key").String("motd").End().Finish();
        SayNumber(ctx, "settings.get", SdkServerArea.Call("settings.get", key, out Value reply));
        Say(ctx, "motd length " + StrLength(reply).ToString());
        SayNumber(ctx, "tick.rate", SdkServerArea.Call("tick.rate", null, out reply));
        SayNumber(ctx, "rate", reply.TryFloat(out double rate) ? (long)rate : -1);
        SayNumber(ctx, "ops", SdkServerArea.Call("ops", null, out reply));
        SayNumber(ctx, "op count", reply.Count);
        SayNumber(ctx, "unknown op", SdkServerArea.Call("nope.nope", null, out reply));
        SayNumber(ctx, "datapacks", SdkServerArea.Call("datapacks.list", null, out reply));
    }

    static void Files(int ctx)
    {
        SayNumber(ctx, "write", SdkServerArea.FileWrite("notes/a.txt", "hello"));
        SayNumber(ctx, "exists", SdkServerArea.FileExists("notes/a.txt") ? 1 : 0);
        byte[] text = new byte[63];
        int size = SdkServerArea.FileRead("notes/a.txt", text);
        Say(ctx, size > 0 ? Encoding.UTF8.GetString(text, 0, Math.Min(size, text.Length)) : "");
        SayNumber(ctx, "list", SdkServerArea.FileList("notes", out Value listing));
        SayNumber(ctx, "entries", listing.Count);
        SayNumber(ctx, "stat", SdkServerArea.FileStat("notes/a.txt", out Value stat));
        SayNumber(ctx, "size", IntOr(stat.Find("size"), -1));
        SayNumber(ctx, "outside", SdkServerArea.FileWrite("secret/a.txt", "x"));
        SayNumber(ctx, "escape", SdkServerArea.FileWrite("notes/../a.txt", "x"));
        SayNumber(ctx, "packaged", SdkServerArea.FileRead("data/readme.txt", new byte[32]));
        SayNumber(ctx, "delete", SdkServerArea.FileDelete("notes/a.txt"));
        SayNumber(ctx, "gone", SdkServerArea.FileExists("notes/a.txt") ? 1 : 0);
    }

    static void Store(int ctx)
    {
        SayNumber(ctx, "set", SdkServerArea.StoreSet("visits", 3L));
        SayNumber(ctx, "get", SdkServerArea.StoreGet("visits", out Value reply));
        SayNumber(ctx, "visits", IntOr(reply, -1));
        SayNumber(ctx, "missing", SdkServerArea.StoreGet("nothing", out reply));
        byte[] doc = new ValueWriter(256).BeginMap().Key("kills").Int(7).Key("tags").BeginList().String("a").String("b").End().End().Finish();
        SayNumber(ctx, "write", SdkServerArea.DataWrite("stats", doc));
        SayNumber(ctx, "read", SdkServerArea.DataRead("stats", out reply));
        SayNumber(ctx, "kills", IntOr(reply.Find("kills"), -1));
        SayNumber(ctx, "tags", reply.Find("tags").Count);
        SayNumber(ctx, "config", SdkServerArea.ConfigValue("shop", out reply));
        SayNumber(ctx, "price", IntOr(reply.Find("sparkles-price"), -1));
        SayNumber(ctx, "dashed", SdkServerArea.ConfigValue("payout_period", out reply));
    }

    static void EchoService(int ctx)
    {
        SdkServerArea.ValueResult(ctx, out Value call);
        byte[] answer = new ValueWriter(256).BeginMap().Key("echo").Int(IntOr(call.Find("args").Find("n"), 0) * 2).End().Finish();
        SdkServerArea.ServiceReply(ctx, answer);
    }

    static void EchoDone(int ctx)
    {
        SdkServerArea.ValueResult(ctx, out Value reply);
        NoteNumber("ok", reply.Find("ok").TryBool(out bool ok) && ok ? 1 : 0);
        NoteNumber("echo", IntOr(reply.Find("value").Find("echo"), -1));
    }

    static void MissingDone(int ctx)
    {
        SdkServerArea.ValueResult(ctx, out Value reply);
        bool ok = !reply.Find("ok").TryBool(out bool value) || value;
        NoteNumber("late ok", ok ? 1 : 0);
    }

    static void Service(int ctx)
    {
        SayNumber(ctx, "has", SdkServerArea.ServiceHas("example.echo") ? 1 : 0);
        SayNumber(ctx, "has other", SdkServerArea.ServiceHas("example.other") ? 1 : 0);
        byte[] args = new ValueWriter(256).BeginMap().Key("n").Int(21).End().Finish();
        SayNumber(ctx, "call", SdkServerArea.ServiceCall("example.echo", args, 0, EchoDone) > 0 ? 1 : 0);
        SayNumber(ctx, "absent", SdkServerArea.ServiceCall("example.other", args, 0, MissingDone));
    }

    static int waits;

    static void Waited(int ctx) => NoteNumber("waited", ++waits);

    static void Placed(int ctx) => NoteNumber("placed", ++waits);

    static void Looked(int ctx)
    {
        SdkServerArea.ValueResult(ctx, out Value reply);
        NoteNumber("profile", reply.IsNull ? 0 : StrLength(reply.Find("uuid")));
    }

    static void Later(int ctx)
    {
        SayNumber(ctx, "wait", SdkServerArea.Wait(20, Waited) > 0 ? 1 : 0);
        SayNumber(ctx, "at", SdkServerArea.At("world", 0.0, 0.0, Placed) > 0 ? 1 : 0);
        SayNumber(ctx, "at unknown", SdkServerArea.At("elsewhere", 0.0, 0.0, Placed));
        SayNumber(ctx, "lookup", SdkServerArea.ProfileLookup("Notch", Looked) > 0 ? 1 : 0);
    }

    static void Dynamic(int ctx)
    {
        Say(ctx, "dynamic ran");
        SdkServerArea.CommandArgF64(ctx, "amount", out double amount);
        SdkServerArea.CommandArgBool(ctx, "loud", out bool flag);
        SayNumber(ctx, "amount", (long)(amount * 10));
        SayNumber(ctx, "loud", flag ? 1 : 0);
    }

    static void DynamicComplete(int ctx)
    {
        SdkServerArea.CompleteAdd(ctx, "alpha");
        SdkServerArea.CompleteAdd(ctx, "beta");
        string typed = SdkServerArea.CompleteInput(ctx);
        if (!string.IsNullOrEmpty(typed))
        {
            SdkServerArea.CompleteAdd(ctx, typed);
        }
    }

    static void Manage(int ctx)
    {
        const string spec = "dyn [amount: float] [loud: bool]";
        SayNumber(ctx, "register", SdkServerArea.Command(spec, "", Dynamic, DynamicComplete));
        SayNumber(ctx, "again", SdkServerArea.Command(spec, "", Dynamic));
        SayNumber(ctx, "alias", SdkServerArea.CommandAlias("dyn", "dy"));
        SayNumber(ctx, "alias missing", SdkServerArea.CommandAlias("nothing", "no"));
        SayNumber(ctx, "run", SdkServerArea.CommandRun("srv hello", 0));
        SayNumber(ctx, "run as player", SdkServerArea.CommandRun("srv hello", SourcePlayer(ctx)));
    }

    static void Unmanage(int ctx)
    {
        SayNumber(ctx, "unregister", SdkServerArea.CommandUnregister("dyn"));
        SayNumber(ctx, "unregister again", SdkServerArea.CommandUnregister("dyn"));
    }

    static void Hello(int ctx) => Say(ctx, "hello from the command");

    public static void RegisterAll()
    {
        SdkServerArea.PermissionProvide(10, SdkServerArea.ProvideCheck | SdkServerArea.ProvideMeta, VipProvider);
        SdkServerArea.ServiceRegister("example.echo", EchoService);
        SdkCommands.Command("srv hello", "", Hello);
        SdkCommands.Command("srv perms", "", Permissions);
        SdkCommands.Command("srv broadcast", "", Broadcast);
        SdkCommands.Command("srv info", "", Info);
        SdkCommands.Command("srv admin", "", Admin);
        SdkCommands.Command("srv files", "", Files);
        SdkCommands.Command("srv store", "", Store);
        SdkCommands.Command("srv service", "", Service);
        SdkCommands.Command("srv later", "", Later);
        SdkCommands.Command("srv manage", "", Manage);
        SdkCommands.Command("srv unmanage", "", Unmanage);
    }
}
