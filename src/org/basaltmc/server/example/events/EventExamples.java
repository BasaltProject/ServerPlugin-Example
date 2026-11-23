package org.basaltmc.server.example.events;

import org.basaltmc.server.events.EventBus;
import org.basaltmc.server.values.Documents.Value;
import org.basaltmc.server.values.Documents.Writer;

import static org.basaltmc.server.core.Priority.HIGHEST;
import static org.basaltmc.server.core.Priority.LOW;
import static org.basaltmc.server.core.Priority.NORMAL;
import static org.basaltmc.server.events.EventBus.eventPlayer;
import static org.basaltmc.server.log.Logger.logInfo;

public final class EventExamples {
    private EventExamples() {}

    private static final int VALUE_BYTES = 512;
    private static final int LIST_BYTES = 8192;

    private static void onForm(int ctx) {
        Value answer = EventBus.completionValue(ctx, VALUE_BYTES);
        if (answer == null) {
            return;
        }
        logInfo("form button " + answer.find("button").asLong(-1));
    }

    private static void onDialog(int ctx) {
        Value answer = EventBus.completionValue(ctx, VALUE_BYTES);
        if (answer == null) {
            return;
        }
        String id = answer.find("id").asString();
        logInfo("dialog id " + (id == null ? "" : id));
    }

    private static void greet(int ctx) {
        int player = eventPlayer(ctx);
        EventBus.eventGet(ctx, "world", VALUE_BYTES);
        EventBus.eventFields(ctx, VALUE_BYTES);

        EventBus.eventDescribe("player.chat", VALUE_BYTES);
        EventBus.eventDescribe("no.such.event", VALUE_BYTES);
        EventBus.eventNames(LIST_BYTES);

        String uuid = EventBus.playerUuid(player);
        EventBus.playerByUuid(uuid == null ? "" : uuid);
        EventBus.playerByUuid("00000000-0000-0000-0000-999999999999");
        EventBus.playerUuid(9999);

        EventBus.playerGet(player, "food", VALUE_BYTES);
        EventBus.playerGet(player, "no_such_property", VALUE_BYTES);
        byte[] food = new Writer().integer(18).finish();
        EventBus.playerSet(player, "food", food);
        EventBus.playerSet(player, "name", food);
        EventBus.playerDescribe(LIST_BYTES);
        byte[] none = new Writer().beginList().end().finish();
        EventBus.playerCall(player, "reset_idle", none, VALUE_BYTES);
        EventBus.playerCall(player, "no_such_operation", none, VALUE_BYTES);

        EventBus.playerHealth(player);
        EventBus.playerSetHealth(player, 10.5);
        EventBus.playerSetHealth(player, -1.0);
        EventBus.playerGamemode(player);
        EventBus.playerSetGamemode(player, "creative");
        EventBus.playerSetGamemode(player, "bogus");
        EventBus.playerTeleport(player, "", 10.5, 64.0, -3.25, 90.0f, 15.0f);
        EventBus.playerTeleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0f, 0.0f);
        EventBus.playerTitle(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10, null);
        EventBus.playerTitle(player, "<gold>Welcome", "", -1, 40, 10, null);
        EventBus.playerEffect(player, "minecraft:speed", 200, 1);
        EventBus.playerEffect(player, "no_such_effect", 200, 1);

        byte[] trade = new Writer().beginMap().key("entity").integer(1).end().finish();
        EventBus.playerOpenInventory(player, "trades", trade);
        EventBus.playerOpenInventory(player, "no_such_window", trade);
        EventBus.playerCloseWindow(player);

        Writer builder = new Writer().beginMap().key("title").string("Pick one").key("buttons").beginList()
                .string("Yes").string("No").end();
        builder.key("fields").beginList().beginMap().key("key").string("name").key("type").string("text");
        byte[] form = builder.key("label").string("Name").end().end().end().finish();
        EventBus.formOpen(player, form, EventExamples::onForm);
        byte[] broken = new Writer().beginMap().key("title").integer(1).end().finish();
        EventBus.formOpen(player, broken, EventExamples::onForm);
        EventBus.formClose(player);
        EventBus.formOpen(player, form, EventExamples::onForm);
        EventBus.dialogShow(player, "minecraft:confirm", EventExamples::onDialog);
        EventBus.dialogClear(player);
        EventBus.dialogShow(player, "minecraft:confirm", EventExamples::onDialog);
        EventBus.playerKick(9999, "bye", null);
    }

    private static void chat(int ctx) {
        EventBus.eventName(ctx);
        String text = "";
        Value message = EventBus.eventGet(ctx, "message", VALUE_BYTES);
        if (message != null && message.asString() != null) {
            text = message.asString();
        }
        EventBus.eventGet(ctx, "no_such_field", VALUE_BYTES);
        EventBus.eventCancelled(ctx);
        EventBus.eventIsPost(ctx);
        EventBus.eventSet(ctx, "player_name", new Writer().string("x").finish());
        if (text.length() == 6 && text.charAt(0) == 'c') {
            EventBus.eventCancelWith(ctx, "<red>Not allowed</red>", null);
            return;
        }
        if (text.length() == 3 && text.charAt(0) == 'b') {
            EventBus.playerKick(eventPlayer(ctx), "<red>Bye</red>", null);
            return;
        }
        if (text.length() == 4 && text.charAt(0) == 'p') {
            byte[] payload = new Writer().beginMap().key("from").string("chat").end().finish();
            EventBus.eventEmit("example:pong", payload);
            EventBus.eventEmit("nocolon", payload);
        }
        EventBus.eventSet(ctx, "message", new Writer().string(text + "!").finish());
        EventBus.eventFields(ctx, VALUE_BYTES);
    }

    private static void chatPost(int ctx) {
        boolean post = EventBus.eventIsPost(ctx) == 1;
        boolean cancelled = EventBus.eventCancelled(ctx) == 1;
        EventBus.eventSet(ctx, "message", new Writer().string("late").finish());
        EventBus.eventCancelWith(ctx, "<red>Too late</red>", null);
        String reason = EventBus.eventCancelMessage(ctx);
        logInfo((post ? "post " : "decision ") + (cancelled ? "cancelled " : "delivered ")
                + (reason == null ? "no message" : reason));
    }

    private static void ping(int ctx) {
        Value payload = EventBus.eventPayload(ctx, VALUE_BYTES);
        long level = payload == null ? 0 : payload.find("level").asLong(0);
        logInfo("ping level " + level);
    }

    public static void register() {
        EventBus.onEx("player.chat", NORMAL, 0, null, EventExamples::chat);
        EventBus.onEx("player.chat", HIGHEST, EventBus.POST | EventBus.CANCELLED, null, EventExamples::chatPost);
        byte[] filters = new Writer().beginMap().key("world").string("world").end().finish();
        EventBus.onEx("player.join", LOW, 0, filters, EventExamples::greet);
        EventBus.onEx("example:ping", NORMAL, 0, null, EventExamples::ping);
        EventBus.onEx("no.such.event", NORMAL, 0, null, EventExamples::ping);
    }
}
