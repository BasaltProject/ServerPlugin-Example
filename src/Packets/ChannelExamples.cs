using Basalt.Server.Commands;
using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Log;
using Basalt.Server.Packets;
using Basalt.Server.Players;
using Basalt.Server.Values;
using System.Text;

namespace Basalt.Server.Example.Packets;

public static class ChannelExamples
{
    const string Channel = "example:chan";

    static int commandListener;
    static int lastCtx;

    static byte[] Bytes(string text) => Encoding.UTF8.GetBytes(text);

    static int Source(int ctx) => Directory.PlayerByName(Sender.Name(ctx) ?? "");

    static void OnChannel(int ctx)
    {
        Interceptor.PacketCtxInfo(ctx);
        byte[] body = Interceptor.PacketCtxBody(ctx).Bytes;
        Interceptor.ChannelSend(Interceptor.PacketCtxPlayer(ctx), Channel, body);
        Interceptor.PacketCtxCancel(ctx);
    }

    static void OnChat(int ctx)
    {
        lastCtx = ctx;
        byte[] body = Interceptor.PacketCtxBody(ctx).Bytes;
        Interceptor.PacketCtxPlayer(ctx);
        if (body.Length > 0 && body[0] == (byte)'!')
        {
            Interceptor.PacketCtxCancel(ctx);
            return;
        }
        byte[] grown = new byte[body.Length + 1];
        body.CopyTo(grown, 0);
        grown[body.Length] = (byte)'#';
        Interceptor.PacketCtxSetBody(ctx, grown);
    }

    static void OnWatch(int ctx)
    {
        if (Interceptor.PacketCtxInfo(ctx).TryValue(out Value root) && root.TryFind("name", out Value name)
            && name.TryString(out string text))
        {
            Logger.LogInfo(text);
        }
        Interceptor.PacketCtxCancel(ctx);
    }

    static void OnCommandPacket(int ctx)
    {
        Interceptor.PacketCtxSend(ctx, "clientbound.system_chat", Bytes("ok"), false);
        Interceptor.PacketCtxReceive(ctx, "serverbound.chat_message", Bytes("hi"), true);
        Interceptor.PacketCtxSend(ctx, "serverbound.chat_message", Bytes("no"), false);
    }

    static void Info()
    {
        Interceptor.PacketNames("serverbound");
        Interceptor.PacketNames("");
        Interceptor.PacketLookup("serverbound.chat_message");
        Interceptor.PacketLookup("clientbound.play.*");
        Interceptor.PacketLookup("bogus.name");
        Interceptor.PacketPlayerActions();
    }

    static void Send(int player)
    {
        Interceptor.PacketSend(player, "clientbound.set_title_text", Bytes("hi"), false);
        Interceptor.PacketSend(player, "serverbound.chat_message", Bytes("x"), false);
        Interceptor.PacketSend(player, "clientbound.system_chat", Bytes("again"), true);
    }

    static void Receive(int player)
    {
        Interceptor.PacketReceive(player, "serverbound.chat_command", Bytes("/help"), false);
        Interceptor.PacketReceive(player, "clientbound.set_title_text", Bytes("x"), false);
    }

    static void Channels(int player)
    {
        Interceptor.ChannelSend(player, Channel, Bytes("ping"));
        Interceptor.ChannelSend(0, Channel, Bytes("ping"));
    }

    static void Action(int player)
    {
        byte[] held = new ValueWriter().BeginList().Int(3).End().Finish();
        Interceptor.PacketPlayerReceive(player, "held_slot", held);
        byte[] payload = new ValueWriter().BeginList().String(Channel).String("pong").End().Finish();
        Interceptor.PacketPlayerReceive(player, "custom_payload", payload);
        Interceptor.PacketPlayerReceive(player, "bogus", held);
    }

    static void Stale()
    {
        Interceptor.PacketCtxCancel(lastCtx);
        Interceptor.PacketCtxInfo(9999);
    }

    static void Unlisten()
    {
        Interceptor.PacketUnlisten(commandListener);
        Interceptor.PacketUnlisten(commandListener);
        Interceptor.PacketUnlisten(0);
    }

    static void OnCommand(int ctx)
    {
        string word = Arguments.Str(ctx, "action") ?? "";
        int player = Source(ctx);
        switch (word)
        {
            case "info": Info(); break;
            case "send": Send(player); break;
            case "recv": Receive(player); break;
            case "chan": Channels(player); break;
            case "action": Action(player); break;
            case "stale": Stale(); break;
            case "unlisten": Unlisten(); break;
        }
    }

    public static void Register()
    {
        Interceptor.ChannelListen(Channel, OnChannel);
        Interceptor.PacketListen("serverbound.chat_message", "first", false, OnChat);
        Interceptor.PacketListen("clientbound.*", null, true, OnWatch);
        Interceptor.PacketListen("serverbound.play.*", "late", true, OnWatch);
        commandListener = Interceptor.PacketListen("serverbound.chat_command", null, false, OnCommandPacket);
        Interceptor.PacketListen("nope", null, false, OnWatch);
        Registry.Register("pkt <action: word>", "", OnCommand);
    }
}
