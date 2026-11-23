package org.basaltmc.server.example.packets;

import java.nio.charset.StandardCharsets;
import org.basaltmc.server.commands.Registry;
import org.basaltmc.server.packets.Interceptor;
import org.basaltmc.server.values.Documents;

import static org.basaltmc.server.commands.Arguments.str;
import static org.basaltmc.server.commands.Sender.name;
import static org.basaltmc.server.log.Logger.logInfo;
import static org.basaltmc.server.players.Directory.playerByName;
import static org.basaltmc.server.players.Directory.send;

public final class ChannelExamples {
    private ChannelExamples() {}

    private static final String CHANNEL = "example:chan";
    private static final int CAPACITY = 1024;

    private static int commandListener;
    private static int lastCtx;

    private static byte[] bytes(String text) { return text.getBytes(StandardCharsets.UTF_8); }

    private static int source(int ctx) {
        String name = name(ctx);
        return playerByName(name == null ? "" : name);
    }

    private static void onChannel(int ctx) {
        Interceptor.packetCtxInfo(ctx);
        Documents.OutResult body = Interceptor.packetCtxBody(ctx);
        Interceptor.channelSend(Interceptor.packetCtxPlayer(ctx), CHANNEL, body.bytes);
        Interceptor.packetCtxCancel(ctx);
    }

    private static void onChat(int ctx) {
        lastCtx = ctx;
        byte[] body = Interceptor.packetCtxBody(ctx).bytes;
        Interceptor.packetCtxPlayer(ctx);
        if (body.length > 0 && body[0] == '!') {
            Interceptor.packetCtxCancel(ctx);
            return;
        }
        byte[] grown = new byte[body.length + 1];
        System.arraycopy(body, 0, grown, 0, body.length);
        grown[body.length] = '#';
        Interceptor.packetCtxSetBody(ctx, grown);
    }

    private static void onWatch(int ctx) {
        Documents.Value root = Interceptor.packetCtxInfo(ctx).value();
        if (root != null) {
            Documents.Value name = root.find("name");
            if (name != null && name.asString() != null) {
                logInfo(name.asString());
            }
        }
        Interceptor.packetCtxCancel(ctx);
    }

    private static void onCommandPacket(int ctx) {
        Interceptor.packetCtxSend(ctx, "clientbound.system_chat", bytes("ok"), false);
        Interceptor.packetCtxReceive(ctx, "serverbound.chat_message", bytes("hi"), true);
        Interceptor.packetCtxSend(ctx, "serverbound.chat_message", bytes("no"), false);
    }

    private static void info() {
        Interceptor.packetNames("serverbound");
        Interceptor.packetNames("");
        Interceptor.packetLookup("serverbound.chat_message");
        Interceptor.packetLookup("clientbound.play.*");
        Interceptor.packetLookup("bogus.name");
        Interceptor.packetPlayerActions();
    }

    private static void send(int player) {
        Interceptor.packetSend(player, "clientbound.set_title_text", bytes("hi"), false);
        Interceptor.packetSend(player, "serverbound.chat_message", bytes("x"), false);
        Interceptor.packetSend(player, "clientbound.system_chat", bytes("again"), true);
    }

    private static void receive(int player) {
        Interceptor.packetReceive(player, "serverbound.chat_command", bytes("/help"), false);
        Interceptor.packetReceive(player, "clientbound.set_title_text", bytes("x"), false);
    }

    private static void channel(int player) {
        Interceptor.channelSend(player, CHANNEL, bytes("ping"));
        Interceptor.channelSend(0, CHANNEL, bytes("ping"));
    }

    private static void action(int player) {
        byte[] held = new Documents.Writer().beginList().integer(3).end().finish();
        Interceptor.packetPlayerReceive(player, "held_slot", held);
        byte[] payload = new Documents.Writer().beginList().string(CHANNEL).string("pong").end().finish();
        Interceptor.packetPlayerReceive(player, "custom_payload", payload);
        Interceptor.packetPlayerReceive(player, "bogus", held);
    }

    private static void stale() {
        Interceptor.packetCtxCancel(lastCtx);
        Interceptor.packetCtxInfo(9999);
    }

    private static void unlisten() {
        Interceptor.packetUnlisten(commandListener);
        Interceptor.packetUnlisten(commandListener);
        Interceptor.packetUnlisten(0);
    }

    private static void onCommand(int ctx) {
        String word = str(ctx, "action");
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
        Interceptor.channelListen(CHANNEL, ChannelExamples::onChannel);
        Interceptor.packetListen("serverbound.chat_message", "first", false, ChannelExamples::onChat);
        Interceptor.packetListen("clientbound.*", null, true, ChannelExamples::onWatch);
        Interceptor.packetListen("serverbound.play.*", "late", true, ChannelExamples::onWatch);
        commandListener = Interceptor.packetListen("serverbound.chat_command", null, false, ChannelExamples::onCommandPacket);
        Interceptor.packetListen("nope", null, false, ChannelExamples::onWatch);
        Registry.register("pkt <action: word>", "", ChannelExamples::onCommand);
    }
}
