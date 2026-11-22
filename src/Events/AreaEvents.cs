using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkEvents = global::Basalt.Server.Events.Events;
using SdkEventsArea = global::Basalt.Server.Events.EventsArea;
using SdkLogger = global::Basalt.Server.Log.Logger;

namespace Basalt.Server.Example.Events;

public static class AreaEvents
{
    const int ValueBytes = 512;
    const int ListBytes = 8192;

    static void OnForm(int ctx)
    {
        if (!SdkEventsArea.CompletionValue(ctx, ValueBytes, out Value answer))
        {
            return;
        }
        long button = -1;
        answer.Find("button").TryInt(out button);
        SdkLogger.LogInfo("form button " + button.ToString());
    }

    static void OnDialog(int ctx)
    {
        if (!SdkEventsArea.CompletionValue(ctx, ValueBytes, out Value answer))
        {
            return;
        }
        string id = "";
        answer.Find("id").TryString(out id);
        SdkLogger.LogInfo("dialog id " + id);
    }

    static void Greet(int ctx)
    {
        int player = SdkEvents.EventPlayer(ctx);
        SdkEventsArea.EventGet(ctx, "world", ValueBytes, out _);
        SdkEventsArea.EventFields(ctx, ValueBytes, out _);

        SdkEventsArea.EventDescribe("player.chat", ValueBytes, out _);
        SdkEventsArea.EventDescribe("no.such.event", ValueBytes, out _);
        SdkEventsArea.EventNames(ListBytes, out _);

        SdkEventsArea.PlayerByUuid(SdkEventsArea.PlayerUuid(player) ?? "");
        SdkEventsArea.PlayerByUuid("00000000-0000-0000-0000-999999999999");
        SdkEventsArea.PlayerUuid(9999);

        SdkEventsArea.PlayerGet(player, "food", ValueBytes, out _);
        SdkEventsArea.PlayerGet(player, "no_such_property", ValueBytes, out _);
        byte[] food = new ValueWriter().Int(18).Finish();
        SdkEventsArea.PlayerSet(player, "food", food);
        SdkEventsArea.PlayerSet(player, "name", food);
        SdkEventsArea.PlayerDescribe(ListBytes, out _);
        byte[] none = new ValueWriter().BeginList().End().Finish();
        SdkEventsArea.PlayerCall(player, "reset_idle", none, ValueBytes, out _);
        SdkEventsArea.PlayerCall(player, "no_such_operation", none, ValueBytes, out _);

        SdkEventsArea.PlayerHealth(player, out _);
        SdkEventsArea.PlayerSetHealth(player, 10.5);
        SdkEventsArea.PlayerSetHealth(player, -1.0);
        SdkEventsArea.PlayerGamemode(player);
        SdkEventsArea.PlayerSetGamemode(player, "creative");
        SdkEventsArea.PlayerSetGamemode(player, "bogus");
        SdkEventsArea.PlayerTeleport(player, "", 10.5, 64.0, -3.25, 90.0f, 15.0f);
        SdkEventsArea.PlayerTeleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0f, 0.0f);
        SdkEventsArea.PlayerTitle(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10);
        SdkEventsArea.PlayerTitle(player, "<gold>Welcome", "", -1, 40, 10);
        SdkEventsArea.PlayerEffect(player, "minecraft:speed", 200, 1);
        SdkEventsArea.PlayerEffect(player, "no_such_effect", 200, 1);

        byte[] trade = new ValueWriter().BeginMap().Key("entity").Int(1).End().Finish();
        SdkEventsArea.PlayerOpenInventory(player, "trades", trade);
        SdkEventsArea.PlayerOpenInventory(player, "no_such_window", trade);
        SdkEventsArea.PlayerCloseWindow(player);

        ValueWriter builder = new ValueWriter().BeginMap().Key("title").String("Pick one").Key("buttons").BeginList()
            .String("Yes").String("No").End();
        builder.Key("fields").BeginList().BeginMap().Key("key").String("name").Key("type").String("text");
        byte[] form = builder.Key("label").String("Name").End().End().End().Finish();
        SdkEventsArea.FormOpen(player, form, OnForm);
        byte[] broken = new ValueWriter().BeginMap().Key("title").Int(1).End().Finish();
        SdkEventsArea.FormOpen(player, broken, OnForm);
        SdkEventsArea.FormClose(player);
        SdkEventsArea.FormOpen(player, form, OnForm);
        SdkEventsArea.DialogShow(player, "minecraft:confirm", OnDialog);
        SdkEventsArea.DialogClear(player);
        SdkEventsArea.DialogShow(player, "minecraft:confirm", OnDialog);
        SdkEventsArea.PlayerKick(9999, "bye");
    }

    static void Chat(int ctx)
    {
        SdkEventsArea.EventName(ctx);
        string text = "";
        if (SdkEventsArea.EventGet(ctx, "message", ValueBytes, out Value message))
        {
            message.TryString(out text);
        }
        SdkEventsArea.EventGet(ctx, "no_such_field", ValueBytes, out _);
        SdkEventsArea.EventCancelled(ctx);
        SdkEventsArea.EventIsPost(ctx);
        SdkEventsArea.EventSet(ctx, "player_name", new ValueWriter().String("x").Finish());
        if (text.Length == 6 && text[0] == 'c')
        {
            SdkEventsArea.EventCancelWith(ctx, "<red>Not allowed</red>");
            return;
        }
        if (text.Length == 3 && text[0] == 'b')
        {
            SdkEventsArea.PlayerKick(SdkEvents.EventPlayer(ctx), "<red>Bye</red>");
            return;
        }
        if (text.Length == 4 && text[0] == 'p')
        {
            byte[] payload = new ValueWriter().BeginMap().Key("from").String("chat").End().Finish();
            SdkEventsArea.EventEmit("example:pong", payload);
            SdkEventsArea.EventEmit("nocolon", payload);
        }
        SdkEventsArea.EventSet(ctx, "message", new ValueWriter().String(text + "!").Finish());
        SdkEventsArea.EventFields(ctx, ValueBytes, out _);
    }

    static void ChatPost(int ctx)
    {
        bool post = SdkEventsArea.EventIsPost(ctx) == 1;
        bool cancelled = SdkEventsArea.EventCancelled(ctx) == 1;
        SdkEventsArea.EventSet(ctx, "message", new ValueWriter().String("late").Finish());
        SdkEventsArea.EventCancelWith(ctx, "<red>Too late</red>");
        string reason = SdkEventsArea.EventCancelMessage(ctx) ?? "no message";
        SdkLogger.LogInfo((post ? "post " : "decision ") + (cancelled ? "cancelled " : "delivered ") + reason);
    }

    static void Ping(int ctx)
    {
        long level = 0;
        if (SdkEventsArea.EventPayload(ctx, ValueBytes, out Value payload))
        {
            payload.Find("level").TryInt(out level);
        }
        SdkLogger.LogInfo("ping level " + level.ToString());
    }

    public static void Register()
    {
        SdkEventsArea.OnEx("player.chat", Priority.Normal, 0, null, Chat);
        SdkEventsArea.OnEx("player.chat", Priority.Highest, SdkEventsArea.Post | SdkEventsArea.Cancelled, null, ChatPost);
        byte[] filters = new ValueWriter().BeginMap().Key("world").String("world").End().Finish();
        SdkEventsArea.OnEx("player.join", Priority.Low, 0, filters, Greet);
        SdkEventsArea.OnEx("example:ping", Priority.Normal, 0, null, Ping);
        SdkEventsArea.OnEx("no.such.event", Priority.Normal, 0, null, Ping);
    }
}
