package org.basaltmc.server.example.events;

import static org.basaltmc.server.core.Priority.HIGHEST;
import static org.basaltmc.server.core.Priority.LOW;
import static org.basaltmc.server.core.Priority.NORMAL;
import static org.basaltmc.server.events.Events.eventPlayer;
import static org.basaltmc.server.log.Log.logInfo;
import org.basaltmc.server.events.Events;
import org.basaltmc.server.values.Values.Value;
import org.basaltmc.server.values.Values.Writer;

public final class AreaEvents {
    private AreaEvents() {}

    private static final int VALUE_BYTES = 512;
    private static final int LIST_BYTES = 8192;

    private static void onForm(int ctx) {
        Value answer = Events.completionValue(ctx, VALUE_BYTES);
        if (answer == null) {
            return;
        }
        logInfo("form button " + answer.find("button").asLong(-1));
    }

    private static void onDialog(int ctx) {
        Value answer = Events.completionValue(ctx, VALUE_BYTES);
        if (answer == null) {
            return;
        }
        String id = answer.find("id").asString();
        logInfo("dialog id " + (id == null ? "" : id));
    }

    private static void greet(int ctx) {
        int player = eventPlayer(ctx);
        Events.eventGet(ctx, "world", VALUE_BYTES);
        Events.eventFields(ctx, VALUE_BYTES);

        Events.eventDescribe("player.chat", VALUE_BYTES);
        Events.eventDescribe("no.such.event", VALUE_BYTES);
        Events.eventNames(LIST_BYTES);

        String uuid = Events.playerUuid(player);
        Events.playerByUuid(uuid == null ? "" : uuid);
        Events.playerByUuid("00000000-0000-0000-0000-999999999999");
        Events.playerUuid(9999);

        Events.playerGet(player, "food", VALUE_BYTES);
        Events.playerGet(player, "no_such_property", VALUE_BYTES);
        byte[] food = new Writer().integer(18).finish();
        Events.playerSet(player, "food", food);
        Events.playerSet(player, "name", food);
        Events.playerDescribe(LIST_BYTES);
        byte[] none = new Writer().beginList().end().finish();
        Events.playerCall(player, "reset_idle", none, VALUE_BYTES);
        Events.playerCall(player, "no_such_operation", none, VALUE_BYTES);

        Events.playerHealth(player);
        Events.playerSetHealth(player, 10.5);
        Events.playerSetHealth(player, -1.0);
        Events.playerGamemode(player);
        Events.playerSetGamemode(player, "creative");
        Events.playerSetGamemode(player, "bogus");
        Events.playerTeleport(player, "", 10.5, 64.0, -3.25, 90.0f, 15.0f);
        Events.playerTeleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0f, 0.0f);
        Events.playerTitle(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10, null);
        Events.playerTitle(player, "<gold>Welcome", "", -1, 40, 10, null);
        Events.playerEffect(player, "minecraft:speed", 200, 1);
        Events.playerEffect(player, "no_such_effect", 200, 1);

        byte[] trade = new Writer().beginMap().key("entity").integer(1).end().finish();
        Events.playerOpenInventory(player, "trades", trade);
        Events.playerOpenInventory(player, "no_such_window", trade);
        Events.playerCloseWindow(player);

        Writer builder = new Writer().beginMap().key("title").string("Pick one").key("buttons").beginList()
                .string("Yes").string("No").end();
        builder.key("fields").beginList().beginMap().key("key").string("name").key("type").string("text");
        byte[] form = builder.key("label").string("Name").end().end().end().finish();
        Events.formOpen(player, form, AreaEvents::onForm);
        byte[] broken = new Writer().beginMap().key("title").integer(1).end().finish();
        Events.formOpen(player, broken, AreaEvents::onForm);
        Events.formClose(player);
        Events.formOpen(player, form, AreaEvents::onForm);
        Events.dialogShow(player, "minecraft:confirm", AreaEvents::onDialog);
        Events.dialogClear(player);
        Events.dialogShow(player, "minecraft:confirm", AreaEvents::onDialog);
        Events.playerKick(9999, "bye", null);
    }

    private static void chat(int ctx) {
        Events.eventName(ctx);
        String text = "";
        Value message = Events.eventGet(ctx, "message", VALUE_BYTES);
        if (message != null && message.asString() != null) {
            text = message.asString();
        }
        Events.eventGet(ctx, "no_such_field", VALUE_BYTES);
        Events.eventCancelled(ctx);
        Events.eventIsPost(ctx);
        Events.eventSet(ctx, "player_name", new Writer().string("x").finish());
        if (text.length() == 6 && text.charAt(0) == 'c') {
            Events.eventCancelWith(ctx, "<red>Not allowed</red>", null);
            return;
        }
        if (text.length() == 3 && text.charAt(0) == 'b') {
            Events.playerKick(eventPlayer(ctx), "<red>Bye</red>", null);
            return;
        }
        if (text.length() == 4 && text.charAt(0) == 'p') {
            byte[] payload = new Writer().beginMap().key("from").string("chat").end().finish();
            Events.eventEmit("example:pong", payload);
            Events.eventEmit("nocolon", payload);
        }
        Events.eventSet(ctx, "message", new Writer().string(text + "!").finish());
        Events.eventFields(ctx, VALUE_BYTES);
    }

    private static void chatPost(int ctx) {
        boolean post = Events.eventIsPost(ctx) == 1;
        boolean cancelled = Events.eventCancelled(ctx) == 1;
        Events.eventSet(ctx, "message", new Writer().string("late").finish());
        Events.eventCancelWith(ctx, "<red>Too late</red>", null);
        String reason = Events.eventCancelMessage(ctx);
        logInfo((post ? "post " : "decision ") + (cancelled ? "cancelled " : "delivered ")
                + (reason == null ? "no message" : reason));
    }

    private static void ping(int ctx) {
        Value payload = Events.eventPayload(ctx, VALUE_BYTES);
        long level = payload == null ? 0 : payload.find("level").asLong(0);
        logInfo("ping level " + level);
    }

    public static void register() {
        Events.onEx("player.chat", NORMAL, 0, null, AreaEvents::chat);
        Events.onEx("player.chat", HIGHEST, Events.POST | Events.CANCELLED, null, AreaEvents::chatPost);
        byte[] filters = new Writer().beginMap().key("world").string("world").end().finish();
        Events.onEx("player.join", LOW, 0, filters, AreaEvents::greet);
        Events.onEx("example:ping", NORMAL, 0, null, AreaEvents::ping);
        Events.onEx("no.such.event", NORMAL, 0, null, AreaEvents::ping);
    }
}
