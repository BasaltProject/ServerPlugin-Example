package org.basaltmc.server.example.packets;

import static org.basaltmc.server.commands.Commands.command;
import static org.basaltmc.server.commands.Commands.commandArgStr;
import static org.basaltmc.server.commands.Commands.commandSourceName;
import static org.basaltmc.server.log.Log.logInfo;
import static org.basaltmc.server.players.SharedPlayers.playerByName;
import static org.basaltmc.server.players.SharedPlayers.send;
import org.basaltmc.server.packets.Packets;
import org.basaltmc.server.values.Values;
import java.nio.charset.StandardCharsets;

public final class PacketsExample {
    private PacketsExample() {}

    private static final String CHANNEL = "example:chan";
    private static final int CAPACITY = 1024;

    private static int commandListener;
    private static int lastCtx;

    private static byte[] bytes(String text) { return text.getBytes(StandardCharsets.UTF_8); }

    private static int source(int ctx) {
        String name = commandSourceName(ctx);
        return playerByName(name == null ? "" : name);
    }

    private static void onChannel(int ctx) {
        Packets.packetCtxInfo(ctx);
        Values.OutResult body = Packets.packetCtxBody(ctx);
        Packets.channelSend(Packets.packetCtxPlayer(ctx), CHANNEL, body.bytes);
        Packets.packetCtxCancel(ctx);
    }

    private static void onChat(int ctx) {
        lastCtx = ctx;
        byte[] body = Packets.packetCtxBody(ctx).bytes;
        Packets.packetCtxPlayer(ctx);
        if (body.length > 0 && body[0] == '!') {
            Packets.packetCtxCancel(ctx);
            return;
        }
        byte[] grown = new byte[body.length + 1];
        System.arraycopy(body, 0, grown, 0, body.length);
        grown[body.length] = '#';
        Packets.packetCtxSetBody(ctx, grown);
    }

    private static void onWatch(int ctx) {
        Values.Value root = Packets.packetCtxInfo(ctx).value();
        if (root != null) {
            Values.Value name = root.find("name");
            if (name != null && name.asString() != null) {
                logInfo(name.asString());
            }
        }
        Packets.packetCtxCancel(ctx);
    }

    private static void onCommandPacket(int ctx) {
        Packets.packetCtxSend(ctx, "clientbound.system_chat", bytes("ok"), false);
        Packets.packetCtxReceive(ctx, "serverbound.chat_message", bytes("hi"), true);
        Packets.packetCtxSend(ctx, "serverbound.chat_message", bytes("no"), false);
    }

    private static void info() {
        Packets.packetNames("serverbound");
        Packets.packetNames("");
        Packets.packetLookup("serverbound.chat_message");
        Packets.packetLookup("clientbound.play.*");
        Packets.packetLookup("bogus.name");
        Packets.packetPlayerActions();
    }

    private static void send(int player) {
        Packets.packetSend(player, "clientbound.set_title_text", bytes("hi"), false);
        Packets.packetSend(player, "serverbound.chat_message", bytes("x"), false);
        Packets.packetSend(player, "clientbound.system_chat", bytes("again"), true);
    }

    private static void receive(int player) {
        Packets.packetReceive(player, "serverbound.chat_command", bytes("/help"), false);
        Packets.packetReceive(player, "clientbound.set_title_text", bytes("x"), false);
    }

    private static void channel(int player) {
        Packets.channelSend(player, CHANNEL, bytes("ping"));
        Packets.channelSend(0, CHANNEL, bytes("ping"));
    }

    private static void action(int player) {
        byte[] held = new Values.Writer().beginList().integer(3).end().finish();
        Packets.packetPlayerReceive(player, "held_slot", held);
        byte[] payload = new Values.Writer().beginList().string(CHANNEL).string("pong").end().finish();
        Packets.packetPlayerReceive(player, "custom_payload", payload);
        Packets.packetPlayerReceive(player, "bogus", held);
    }

    private static void stale() {
        Packets.packetCtxCancel(lastCtx);
        Packets.packetCtxInfo(9999);
    }

    private static void unlisten() {
        Packets.packetUnlisten(commandListener);
        Packets.packetUnlisten(commandListener);
        Packets.packetUnlisten(0);
    }

    private static void onCommand(int ctx) {
        String word = commandArgStr(ctx, "action");
        int player = source(ctx);
        switch (word == null ? "" : word) {
            case "info" -> info();
            case "send" -> send(player);
            case "recv" -> receive(player);
            case "chan" -> channel(player);
            case "action" -> action(player);
            case "stale" -> stale();
            case "unlisten" -> unlisten();
            default -> {}
        }
    }

    public static void register() {
        Packets.channelListen(CHANNEL, PacketsExample::onChannel);
        Packets.packetListen("serverbound.chat_message", "first", false, PacketsExample::onChat);
        Packets.packetListen("clientbound.*", null, true, PacketsExample::onWatch);
        Packets.packetListen("serverbound.play.*", "late", true, PacketsExample::onWatch);
        commandListener = Packets.packetListen("serverbound.chat_command", null, false, PacketsExample::onCommandPacket);
        Packets.packetListen("nope", null, false, PacketsExample::onWatch);
        command("pkt <action: word>", "", PacketsExample::onCommand);
    }
}
