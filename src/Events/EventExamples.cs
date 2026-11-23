using Basalt.Server.Core;
using Basalt.Server.Events;
using Basalt.Server.Items;
using Basalt.Server.Log;
using Basalt.Server.Values;

namespace Basalt.Server.Example.Events;

public static class EventExamples
{
    const int ValueBytes = 512;
    const int ListBytes = 8192;

    static void OnForm(int ctx)
    {
        if (!EventBus.CompletionValue(ctx, ValueBytes, out Value answer))
        {
            return;
        }
        long button = -1;
        answer.Find("button").TryInt(out button);
        Logger.LogInfo("form button " + button.ToString());
    }

    static void OnDialog(int ctx)
    {
        if (!EventBus.CompletionValue(ctx, ValueBytes, out Value answer))
        {
            return;
        }
        string id = "";
        answer.Find("id").TryString(out id);
        Logger.LogInfo("dialog id " + id);
    }

    static void Greet(int ctx)
    {
        int player = EventBus.EventPlayer(ctx);
        EventBus.EventGet(ctx, "world", ValueBytes, out _);
        EventBus.EventFields(ctx, ValueBytes, out _);

        EventBus.EventDescribe("player.chat", ValueBytes, out _);
        EventBus.EventDescribe("no.such.event", ValueBytes, out _);
        EventBus.EventNames(ListBytes, out _);

        EventBus.PlayerByUuid(EventBus.PlayerUuid(player) ?? "");
        EventBus.PlayerByUuid("00000000-0000-0000-0000-999999999999");
        EventBus.PlayerUuid(9999);

        EventBus.PlayerGet(player, "food", ValueBytes, out _);
        EventBus.PlayerGet(player, "no_such_property", ValueBytes, out _);
        byte[] food = new ValueWriter().Int(18).Finish();
        EventBus.PlayerSet(player, "food", food);
        EventBus.PlayerSet(player, "name", food);
        EventBus.PlayerDescribe(ListBytes, out _);
        byte[] none = new ValueWriter().BeginList().End().Finish();
        EventBus.PlayerCall(player, "reset_idle", none, ValueBytes, out _);
        EventBus.PlayerCall(player, "no_such_operation", none, ValueBytes, out _);

        EventBus.PlayerHealth(player, out _);
        EventBus.PlayerSetHealth(player, 10.5);
        EventBus.PlayerSetHealth(player, -1.0);
        EventBus.PlayerGamemode(player);
        EventBus.PlayerSetGamemode(player, "creative");
        EventBus.PlayerSetGamemode(player, "bogus");
        EventBus.PlayerTeleport(player, "", 10.5, 64.0, -3.25, 90.0f, 15.0f);
        EventBus.PlayerTeleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0f, 0.0f);
        EventBus.PlayerTitle(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10);
        EventBus.PlayerTitle(player, "<gold>Welcome", "", -1, 40, 10);
        EventBus.PlayerEffect(player, "minecraft:speed", 200, 1);
        EventBus.PlayerEffect(player, "no_such_effect", 200, 1);

        byte[] trade = new ValueWriter().BeginMap().Key("entity").Int(1).End().Finish();
        EventBus.PlayerOpenInventory(player, "trades", trade);
        EventBus.PlayerOpenInventory(player, "no_such_window", trade);
        EventBus.PlayerCloseWindow(player);

        ValueWriter builder = new ValueWriter().BeginMap().Key("title").String("Pick one").Key("buttons").BeginList()
            .String("Yes").String("No").End();
        builder.Key("fields").BeginList().BeginMap().Key("key").String("name").Key("type").String("text");
        byte[] form = builder.Key("label").String("Name").End().End().End().Finish();
        EventBus.FormOpen(player, form, OnForm);
        byte[] broken = new ValueWriter().BeginMap().Key("title").Int(1).End().Finish();
        EventBus.FormOpen(player, broken, OnForm);
        EventBus.FormClose(player);
        EventBus.FormOpen(player, form, OnForm);
        EventBus.DialogShow(player, "minecraft:confirm", OnDialog);
        EventBus.DialogClear(player);
        EventBus.DialogShow(player, "minecraft:confirm", OnDialog);
        EventBus.PlayerKick(9999, "bye");
    }

    static void Chat(int ctx)
    {
        EventBus.EventName(ctx);
        string text = "";
        if (EventBus.EventGet(ctx, "message", ValueBytes, out Value message))
        {
            message.TryString(out text);
        }
        EventBus.EventGet(ctx, "no_such_field", ValueBytes, out _);
        EventBus.EventCancelled(ctx);
        EventBus.EventIsPost(ctx);
        EventBus.EventSet(ctx, "player_name", new ValueWriter().String("x").Finish());
        if (text.Length == 6 && text[0] == 'c')
        {
            EventBus.EventCancelWith(ctx, "<red>Not allowed</red>");
            return;
        }
        if (text.Length == 3 && text[0] == 'b')
        {
            EventBus.PlayerKick(EventBus.EventPlayer(ctx), "<red>Bye</red>");
            return;
        }
        if (text.Length == 4 && text[0] == 'p')
        {
            byte[] payload = new ValueWriter().BeginMap().Key("from").String("chat").End().Finish();
            EventBus.EventEmit("example:pong", payload);
            EventBus.EventEmit("nocolon", payload);
        }
        EventBus.EventSet(ctx, "message", new ValueWriter().String(text + "!").Finish());
        EventBus.EventFields(ctx, ValueBytes, out _);
    }

    static void ChatPost(int ctx)
    {
        bool post = EventBus.EventIsPost(ctx) == 1;
        bool cancelled = EventBus.EventCancelled(ctx) == 1;
        EventBus.EventSet(ctx, "message", new ValueWriter().String("late").Finish());
        EventBus.EventCancelWith(ctx, "<red>Too late</red>");
        string reason = EventBus.EventCancelMessage(ctx) ?? "no message";
        Logger.LogInfo((post ? "post " : "decision ") + (cancelled ? "cancelled " : "delivered ") + reason);
    }

    static void Ping(int ctx)
    {
        long level = 0;
        if (EventBus.EventPayload(ctx, ValueBytes, out Value payload))
        {
            payload.Find("level").TryInt(out level);
        }
        Logger.LogInfo("ping level " + level.ToString());
    }

    public static void Register()
    {
        EventBus.OnEx("player.chat", Priority.Normal, 0, null, Chat);
        EventBus.OnEx("player.chat", Priority.Highest, EventBus.Post | EventBus.Cancelled, null, ChatPost);
        byte[] filters = new ValueWriter().BeginMap().Key("world").String("world").End().Finish();
        EventBus.OnEx("player.join", Priority.Low, 0, filters, Greet);
        EventBus.OnEx("example:ping", Priority.Normal, 0, null, Ping);
        EventBus.OnEx("no.such.event", Priority.Normal, 0, null, Ping);
    }
}
