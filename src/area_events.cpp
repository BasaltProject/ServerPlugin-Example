#include "area_events.hpp"

#include "text.hpp"

namespace area_events {
namespace {

constexpr int kValueBytes = 512;
constexpr int kListBytes = 8192;

void say(const char* label, const char* text) {
    basalt::log_info(Buffer().add(label).add(text).c_str());
}

void on_form(basalt::Ctx ctx) {
    char buffer[kValueBytes];
    basalt::Value answer;
    if (basalt::completion_value(ctx, buffer, kValueBytes, answer) < 0) {
        return;
    }
    long long button = -1;
    answer.find("button").as_i64(button);
    basalt::log_info(Buffer().add("form button ").add(button).c_str());
}

void on_dialog(basalt::Ctx ctx) {
    char buffer[kValueBytes];
    basalt::Value answer;
    if (basalt::completion_value(ctx, buffer, kValueBytes, answer) < 0) {
        return;
    }
    const char* id = "";
    int length = 0;
    answer.find("id").as_str(id, length);
    char copy[48] = {};
    for (int i = 0; i < length && i < 47; ++i) {
        copy[i] = id[i];
    }
    say("dialog id ", copy);
}

void greet(basalt::Ctx ctx) {
    char buffer[kValueBytes];
    char listing[kListBytes];
    basalt::Value value;
    const basalt::Player player = basalt::event_player(ctx);
    basalt::event_get(ctx, "world", buffer, kValueBytes, value);
    basalt::event_fields(ctx, buffer, kValueBytes, value);

    basalt::event_describe("player.chat", buffer, kValueBytes, value);
    basalt::event_describe("no.such.event", buffer, kValueBytes, value);
    basalt::event_names(listing, kListBytes, value);

    const basalt::Text<64> uuid = basalt::player_uuid(player);
    basalt::player_by_uuid(uuid.c_str());
    basalt::player_by_uuid("00000000-0000-0000-0000-999999999999");
    basalt::player_uuid(9999);

    basalt::player_get(player, "food", buffer, kValueBytes, value);
    basalt::player_get(player, "no_such_property", buffer, kValueBytes, value);
    basalt::ValueBuffer<32> food;
    food.integer(18);
    basalt::player_set(player, "food", food.data(), food.finish());
    basalt::player_set(player, "name", food.data(), food.finish());
    basalt::player_describe(listing, kListBytes, value);
    basalt::ValueBuffer<32> none;
    none.list().end();
    basalt::player_call(player, "reset_idle", none.data(), none.finish(), buffer, kValueBytes, value);
    basalt::player_call(player, "no_such_operation", none.data(), none.finish(), buffer, kValueBytes, value);

    double health = 0;
    basalt::player_health(player, &health);
    basalt::player_set_health(player, 10.5);
    basalt::player_set_health(player, -1.0);
    basalt::player_gamemode(player);
    basalt::player_set_gamemode(player, "creative");
    basalt::player_set_gamemode(player, "bogus");
    basalt::player_teleport(player, "", 10.5, 64.0, -3.25, 90.0f, 15.0f);
    basalt::player_teleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0f, 0.0f);
    basalt::player_title(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10);
    basalt::player_title(player, "<gold>Welcome", "", -1, 40, 10);
    basalt::player_effect(player, "minecraft:speed", 200, 1);
    basalt::player_effect(player, "no_such_effect", 200, 1);

    basalt::ValueBuffer<64> trade;
    trade.map().key("entity").integer(1).end();
    basalt::player_open_inventory(player, "trades", trade.data(), trade.finish());
    basalt::player_open_inventory(player, "no_such_window", trade.data(), trade.finish());
    basalt::player_close_window(player);

    basalt::ValueBuffer<256> form;
    form.map().key("title").string("Pick one").key("buttons").list().string("Yes").string("No").end();
    form.key("fields").list().map().key("key").string("name").key("type").string("text").key("label").string("Name");
    form.end().end().end();
    basalt::form_open(player, form.data(), form.finish(), on_form);
    basalt::ValueBuffer<32> broken;
    broken.map().key("title").integer(1).end();
    basalt::form_open(player, broken.data(), broken.finish(), on_form);
    basalt::form_close(player);
    basalt::form_open(player, form.data(), form.finish(), on_form);
    basalt::dialog_show(player, "minecraft:confirm", on_dialog);
    basalt::dialog_clear(player);
    basalt::dialog_show(player, "minecraft:confirm", on_dialog);
    basalt::player_kick(9999, "bye");
}

void chat(basalt::Ctx ctx) {
    char buffer[kValueBytes];
    basalt::Value value;
    basalt::event_name(ctx);
    basalt::event_get(ctx, "message", buffer, kValueBytes, value);
    const char* text = "";
    int length = 0;
    value.as_str(text, length);
    basalt::event_get(ctx, "no_such_field", buffer, kValueBytes, value);
    basalt::event_cancelled(ctx);
    basalt::event_is_post(ctx);
    basalt::ValueBuffer<32> wrong;
    wrong.string("x");
    basalt::event_set(ctx, "player_name", wrong.data(), wrong.finish());
    if (length == 6 && text[0] == 'c') {
        basalt::event_cancel_with(ctx, "<red>Not allowed</red>");
        return;
    }
    if (length == 3 && text[0] == 'b') {
        basalt::player_kick(basalt::event_player(ctx), "<red>Bye</red>");
        return;
    }
    if (length == 4 && text[0] == 'p') {
        basalt::ValueBuffer<64> payload;
        payload.map().key("from").string("chat").end();
        basalt::event_emit("example:pong", payload.data(), payload.finish());
        basalt::event_emit("nocolon", payload.data(), payload.finish());
    }
    char changed[96];
    int at = 0;
    for (int i = 0; i < length && at < 90; ++i) {
        changed[at++] = text[i];
    }
    changed[at++] = '!';
    basalt::ValueBuffer<128> replacement;
    replacement.string(changed, at);
    basalt::event_set(ctx, "message", replacement.data(), replacement.finish());
    basalt::event_fields(ctx, buffer, kValueBytes, value);
}

void chat_post(basalt::Ctx ctx) {
    const bool post = basalt::event_is_post(ctx) == 1;
    const bool cancelled = basalt::event_cancelled(ctx) == 1;
    basalt::ValueBuffer<32> replacement;
    replacement.string("late");
    basalt::event_set(ctx, "message", replacement.data(), replacement.finish());
    basalt::event_cancel_with(ctx, "<red>Too late</red>");
    const basalt::Text<256> reason = basalt::event_cancel_message(ctx);
    basalt::log_info(Buffer()
                         .add(post ? "post " : "decision ")
                         .add(cancelled ? "cancelled " : "delivered ")
                         .add(reason.ok() ? reason.c_str() : "no message")
                         .c_str());
}

void ping(basalt::Ctx ctx) {
    char buffer[kValueBytes];
    basalt::Value payload;
    basalt::event_payload(ctx, buffer, kValueBytes, payload);
    long long level = 0;
    payload.find("level").as_i64(level);
    basalt::log_info(Buffer().add("ping level ").add(level).c_str());
}

} // namespace

void register_all() {
    basalt::event_on_ex("player.chat", basalt::kNormal, 0, nullptr, 0, chat);
    basalt::event_on_ex("player.chat", basalt::kHighest, basalt::kEventPost | basalt::kEventCancelled, nullptr, 0,
                        chat_post);
    basalt::ValueBuffer<64> filters;
    filters.map().key("world").string("world").end();
    basalt::event_on_ex("player.join", basalt::kLow, 0, filters.data(), filters.finish(), greet);
    basalt::event_on_ex("example:ping", basalt::kNormal, 0, nullptr, 0, ping);
    basalt::event_on_ex("no.such.event", basalt::kNormal, 0, nullptr, 0, ping);
}

} // namespace area_events
