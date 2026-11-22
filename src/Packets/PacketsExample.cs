using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkCommands = global::Basalt.Server.Commands.Commands;
using SdkLogger = global::Basalt.Server.Log.Logger;
using SdkPackets = global::Basalt.Server.Packets.Packets;
using SdkPlayers = global::Basalt.Server.Players.Players;
using System.Text;

namespace Basalt.Server.Example.Packets;


public static class PacketsExample
{
    const string Channel = "example:chan";

    static int commandListener;
    static int lastCtx;

    static byte[] Bytes(string text) => Encoding.UTF8.GetBytes(text);

    static int Source(int ctx) => SdkPlayers.PlayerByName(SdkCommands.CommandSourceName(ctx) ?? "");

    static void OnChannel(int ctx)
    {
        SdkPackets.PacketCtxInfo(ctx);
        byte[] body = SdkPackets.PacketCtxBody(ctx).Bytes;
        SdkPackets.ChannelSend(SdkPackets.PacketCtxPlayer(ctx), Channel, body);
        SdkPackets.PacketCtxCancel(ctx);
    }

    static void OnChat(int ctx)
    {
        lastCtx = ctx;
        byte[] body = SdkPackets.PacketCtxBody(ctx).Bytes;
        SdkPackets.PacketCtxPlayer(ctx);
        if (body.Length > 0 && body[0] == (byte)'!')
        {
            SdkPackets.PacketCtxCancel(ctx);
            return;
        }
        byte[] grown = new byte[body.Length + 1];
        body.CopyTo(grown, 0);
        grown[body.Length] = (byte)'#';
        SdkPackets.PacketCtxSetBody(ctx, grown);
    }

    static void OnWatch(int ctx)
    {
        if (SdkPackets.PacketCtxInfo(ctx).TryValue(out Value root) && root.TryFind("name", out Value name)
            && name.TryString(out string text))
        {
            SdkLogger.LogInfo(text);
        }
        SdkPackets.PacketCtxCancel(ctx);
    }

    static void OnCommandPacket(int ctx)
    {
        SdkPackets.PacketCtxSend(ctx, "clientbound.system_chat", Bytes("ok"), false);
        SdkPackets.PacketCtxReceive(ctx, "serverbound.chat_message", Bytes("hi"), true);
        SdkPackets.PacketCtxSend(ctx, "serverbound.chat_message", Bytes("no"), false);
    }

    static void Info()
    {
        SdkPackets.PacketNames("serverbound");
        SdkPackets.PacketNames("");
        SdkPackets.PacketLookup("serverbound.chat_message");
        SdkPackets.PacketLookup("clientbound.play.*");
        SdkPackets.PacketLookup("bogus.name");
        SdkPackets.PacketPlayerActions();
    }

    static void Send(int player)
    {
        SdkPackets.PacketSend(player, "clientbound.set_title_text", Bytes("hi"), false);
        SdkPackets.PacketSend(player, "serverbound.chat_message", Bytes("x"), false);
        SdkPackets.PacketSend(player, "clientbound.system_chat", Bytes("again"), true);
    }

    static void Receive(int player)
    {
        SdkPackets.PacketReceive(player, "serverbound.chat_command", Bytes("/help"), false);
        SdkPackets.PacketReceive(player, "clientbound.set_title_text", Bytes("x"), false);
    }

    static void Channels(int player)
    {
        SdkPackets.ChannelSend(player, Channel, Bytes("ping"));
        SdkPackets.ChannelSend(0, Channel, Bytes("ping"));
    }

    static void Action(int player)
    {
        byte[] held = new ValueWriter().BeginList().Int(3).End().Finish();
        SdkPackets.PacketPlayerReceive(player, "held_slot", held);
        byte[] payload = new ValueWriter().BeginList().String(Channel).String("pong").End().Finish();
        SdkPackets.PacketPlayerReceive(player, "custom_payload", payload);
        SdkPackets.PacketPlayerReceive(player, "bogus", held);
    }

    static void Stale()
    {
        SdkPackets.PacketCtxCancel(lastCtx);
        SdkPackets.PacketCtxInfo(9999);
    }

    static void Unlisten()
    {
        SdkPackets.PacketUnlisten(commandListener);
        SdkPackets.PacketUnlisten(commandListener);
        SdkPackets.PacketUnlisten(0);
    }

    static void OnCommand(int ctx)
    {
        string word = SdkCommands.CommandArgStr(ctx, "action") ?? "";
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
        SdkPackets.ChannelListen(Channel, OnChannel);
        SdkPackets.PacketListen("serverbound.chat_message", "first", false, OnChat);
        SdkPackets.PacketListen("clientbound.*", null, true, OnWatch);
        SdkPackets.PacketListen("serverbound.play.*", "late", true, OnWatch);
        commandListener = SdkPackets.PacketListen("serverbound.chat_command", null, false, OnCommandPacket);
        SdkPackets.PacketListen("nope", null, false, OnWatch);
        SdkCommands.Command("pkt <action: word>", "", OnCommand);
    }
}
