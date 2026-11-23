using Basalt.Server.Commands;
using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Log;
using Basalt.Server.Players;
using Basalt.Server.Services;
using Basalt.Server.Values;
using System;
using System.Text;

namespace Basalt.Server.Example.Services;

public static class ServiceExamples
{
    static void Say(int ctx, string text)
    {
        Args args = new();
        args.Add("text", text);
        Sender.Reply(ctx, "@server-line", args);
    }

    static void SayNumber(int ctx, string label, long value) => Say(ctx, label + " " + value.ToString());

    static void NoteNumber(string label, long value) => Logger.LogInfo(label + " " + value.ToString());

    static int SourcePlayer(int ctx) => Directory.PlayerByName(Sender.Name(ctx) ?? "");

    static long IntOr(Value value, long fallback) => value.TryInt(out long n) ? n : fallback;

    static int StrLength(Value value) => value.TryStringBytes(out byte[] data) ? data.Length : 0;

    static void Permissions(int ctx)
    {
        int player = SourcePlayer(ctx);
        SayNumber(ctx, "has example.vip", RuntimeServices.PermissionHas(player, "example.vip"));
        SayNumber(ctx, "console has example.vip", RuntimeServices.PermissionCheck("console", "example.vip"));
        SayNumber(ctx, "grant", RuntimeServices.PermissionGrant(player, "example.vip"));
        SayNumber(ctx, "has example.vip", RuntimeServices.PermissionHas(player, "example.vip"));
        SayNumber(ctx, "drop", RuntimeServices.PermissionDrop(player, "example.vip"));
        SayNumber(ctx, "has example.vip", RuntimeServices.PermissionHas(player, "example.vip"));
        Say(ctx, RuntimeServices.PermissionMeta("Steve", "prefix") ?? "");
        SayNumber(ctx, "unknown subject", RuntimeServices.PermissionCheck("Nobody", "example.vip"));
    }

    static void VipProvider(int ctx)
    {
        string node = RuntimeServices.PermissionQuery(ctx, "node") ?? "";
        string kind = RuntimeServices.PermissionQuery(ctx, "kind") ?? "";
        if (kind.Length > 0 && kind[0] == 'c')
        {
            RuntimeServices.PermissionAnswer(ctx, node.Length > 8 && node[8] == 'v' ? 1 : -1);
        }
        else
        {
            RuntimeServices.PermissionAnswerMeta(ctx, "VIP");
        }
    }

    static void Broadcast(int ctx)
    {
        Args args = new();
        args.Add("text", "hello everyone");
        SayNumber(ctx, "reached", RuntimeServices.Broadcast("@server-line", args, RuntimeServices.BroadcastPlayers | RuntimeServices.BroadcastConsole));
        SayNumber(ctx, "console only", RuntimeServices.Broadcast("plain text", null, RuntimeServices.BroadcastConsole));
        SayNumber(ctx, "nobody", RuntimeServices.Broadcast("x", null, 0));
        Say(ctx, RuntimeServices.PlaceholderFill("%player_name% is here", SourcePlayer(ctx)) ?? "");
    }

    static void Info(int ctx)
    {
        SayNumber(ctx, "info", RuntimeServices.Info(out Value reply));
        SayNumber(ctx, "online", IntOr(reply.Find("online"), -1));
        Say(ctx, "software " + StrLength(reply.Find("software")).ToString());
        SayNumber(ctx, "plugins", reply.Find("plugins").Count);
        SayNumber(ctx, "lane", IntOr(reply.Find("plugin").Find("lane"), -1));
    }

    static void Admin(int ctx)
    {
        byte[] key = new ValueWriter(256).BeginMap().Key("key").String("motd").End().Finish();
        SayNumber(ctx, "settings.get", RuntimeServices.Call("settings.get", key, out Value reply));
        Say(ctx, "motd length " + StrLength(reply).ToString());
        SayNumber(ctx, "tick.rate", RuntimeServices.Call("tick.rate", null, out reply));
        SayNumber(ctx, "rate", reply.TryFloat(out double rate) ? (long)rate : -1);
        SayNumber(ctx, "ops", RuntimeServices.Call("ops", null, out reply));
        SayNumber(ctx, "op count", reply.Count);
        SayNumber(ctx, "unknown op", RuntimeServices.Call("nope.nope", null, out reply));
        SayNumber(ctx, "datapacks", RuntimeServices.Call("datapacks.list", null, out reply));
    }

    static void Files(int ctx)
    {
        SayNumber(ctx, "write", RuntimeServices.FileWrite("notes/a.txt", "hello"));
        SayNumber(ctx, "exists", RuntimeServices.FileExists("notes/a.txt") ? 1 : 0);
        byte[] text = new byte[63];
        int size = RuntimeServices.FileRead("notes/a.txt", text);
        Say(ctx, size > 0 ? Encoding.UTF8.GetString(text, 0, Math.Min(size, text.Length)) : "");
        SayNumber(ctx, "list", RuntimeServices.FileList("notes", out Value listing));
        SayNumber(ctx, "entries", listing.Count);
        SayNumber(ctx, "stat", RuntimeServices.FileStat("notes/a.txt", out Value stat));
        SayNumber(ctx, "size", IntOr(stat.Find("size"), -1));
        SayNumber(ctx, "outside", RuntimeServices.FileWrite("secret/a.txt", "x"));
        SayNumber(ctx, "escape", RuntimeServices.FileWrite("notes/../a.txt", "x"));
        SayNumber(ctx, "packaged", RuntimeServices.FileRead("data/readme.txt", new byte[32]));
        SayNumber(ctx, "delete", RuntimeServices.FileDelete("notes/a.txt"));
        SayNumber(ctx, "gone", RuntimeServices.FileExists("notes/a.txt") ? 1 : 0);
    }

    static void Store(int ctx)
    {
        SayNumber(ctx, "set", RuntimeServices.StoreSet("visits", 3L));
        SayNumber(ctx, "get", RuntimeServices.StoreGet("visits", out Value reply));
        SayNumber(ctx, "visits", IntOr(reply, -1));
        SayNumber(ctx, "missing", RuntimeServices.StoreGet("nothing", out reply));
        byte[] doc = new ValueWriter(256).BeginMap().Key("kills").Int(7).Key("tags").BeginList().String("a").String("b").End().End().Finish();
        SayNumber(ctx, "write", RuntimeServices.DataWrite("stats", doc));
        SayNumber(ctx, "read", RuntimeServices.DataRead("stats", out reply));
        SayNumber(ctx, "kills", IntOr(reply.Find("kills"), -1));
        SayNumber(ctx, "tags", reply.Find("tags").Count);
        SayNumber(ctx, "config", RuntimeServices.ConfigValue("shop", out reply));
        SayNumber(ctx, "price", IntOr(reply.Find("sparkles-price"), -1));
        SayNumber(ctx, "dashed", RuntimeServices.ConfigValue("payout_period", out reply));
    }

    static void EchoService(int ctx)
    {
        RuntimeServices.ValueResult(ctx, out Value call);
        byte[] answer = new ValueWriter(256).BeginMap().Key("echo").Int(IntOr(call.Find("args").Find("n"), 0) * 2).End().Finish();
        RuntimeServices.ServiceReply(ctx, answer);
    }

    static void EchoDone(int ctx)
    {
        RuntimeServices.ValueResult(ctx, out Value reply);
        NoteNumber("ok", reply.Find("ok").TryBool(out bool ok) && ok ? 1 : 0);
        NoteNumber("echo", IntOr(reply.Find("value").Find("echo"), -1));
    }

    static void MissingDone(int ctx)
    {
        RuntimeServices.ValueResult(ctx, out Value reply);
        bool ok = !reply.Find("ok").TryBool(out bool value) || value;
        NoteNumber("late ok", ok ? 1 : 0);
    }

    static void Service(int ctx)
    {
        SayNumber(ctx, "has", RuntimeServices.ServiceHas("example.echo") ? 1 : 0);
        SayNumber(ctx, "has other", RuntimeServices.ServiceHas("example.other") ? 1 : 0);
        byte[] args = new ValueWriter(256).BeginMap().Key("n").Int(21).End().Finish();
        SayNumber(ctx, "call", RuntimeServices.ServiceCall("example.echo", args, 0, EchoDone) > 0 ? 1 : 0);
        SayNumber(ctx, "absent", RuntimeServices.ServiceCall("example.other", args, 0, MissingDone));
    }

    static int waits;

    static void Waited(int ctx) => NoteNumber("waited", ++waits);

    static void Placed(int ctx) => NoteNumber("placed", ++waits);

    static void Looked(int ctx)
    {
        RuntimeServices.ValueResult(ctx, out Value reply);
        NoteNumber("profile", reply.IsNull ? 0 : StrLength(reply.Find("uuid")));
    }

    static void Later(int ctx)
    {
        SayNumber(ctx, "wait", RuntimeServices.Wait(20, Waited) > 0 ? 1 : 0);
        SayNumber(ctx, "at", RuntimeServices.At("world", 0.0, 0.0, Placed) > 0 ? 1 : 0);
        SayNumber(ctx, "at unknown", RuntimeServices.At("elsewhere", 0.0, 0.0, Placed));
        SayNumber(ctx, "lookup", RuntimeServices.ProfileLookup("Notch", Looked) > 0 ? 1 : 0);
    }

    static void Dynamic(int ctx)
    {
        Say(ctx, "dynamic ran");
        RuntimeServices.CommandArgF64(ctx, "amount", out double amount);
        RuntimeServices.CommandArgBool(ctx, "loud", out bool flag);
        SayNumber(ctx, "amount", (long)(amount * 10));
        SayNumber(ctx, "loud", flag ? 1 : 0);
    }

    static void DynamicComplete(int ctx)
    {
        RuntimeServices.CompleteAdd(ctx, "alpha");
        RuntimeServices.CompleteAdd(ctx, "beta");
        string typed = RuntimeServices.CompleteInput(ctx);
        if (!string.IsNullOrEmpty(typed))
        {
            RuntimeServices.CompleteAdd(ctx, typed);
        }
    }

    static void Manage(int ctx)
    {
        const string spec = "dyn [amount: float] [loud: bool]";
        SayNumber(ctx, "register", RuntimeServices.Command(spec, "", Dynamic, DynamicComplete));
        SayNumber(ctx, "again", RuntimeServices.Command(spec, "", Dynamic));
        SayNumber(ctx, "alias", RuntimeServices.CommandAlias("dyn", "dy"));
        SayNumber(ctx, "alias missing", RuntimeServices.CommandAlias("nothing", "no"));
        SayNumber(ctx, "run", RuntimeServices.CommandRun("srv hello", 0));
        SayNumber(ctx, "run as player", RuntimeServices.CommandRun("srv hello", SourcePlayer(ctx)));
    }

    static void Unmanage(int ctx)
    {
        SayNumber(ctx, "unregister", RuntimeServices.CommandUnregister("dyn"));
        SayNumber(ctx, "unregister again", RuntimeServices.CommandUnregister("dyn"));
    }

    static void Hello(int ctx) => Say(ctx, "hello from the command");

    public static void RegisterAll()
    {
        RuntimeServices.PermissionProvide(10, RuntimeServices.ProvideCheck | RuntimeServices.ProvideMeta, VipProvider);
        RuntimeServices.ServiceRegister("example.echo", EchoService);
        Registry.Register("srv hello", "", Hello);
        Registry.Register("srv perms", "", Permissions);
        Registry.Register("srv broadcast", "", Broadcast);
        Registry.Register("srv info", "", Info);
        Registry.Register("srv admin", "", Admin);
        Registry.Register("srv files", "", Files);
        Registry.Register("srv store", "", Store);
        Registry.Register("srv service", "", Service);
        Registry.Register("srv later", "", Later);
        Registry.Register("srv manage", "", Manage);
        Registry.Register("srv unmanage", "", Unmanage);
    }
}
