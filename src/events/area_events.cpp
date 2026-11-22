#include "events/area_events.hpp"

#include "common/text.hpp"

namespace area_events {
namespace {

constexpr int kValueBytes = 512;
constexpr int kListBytes = 8192;

void say(const char* label, const char* text) { basalt::server::log::log_info(Buffer().add(label).add(text).c_str()); }

void on_form(basalt::server::core::Ctx ctx) {
    char buffer[kValueBytes];
    basalt::server::values::Value answer;
    if (basalt::server::events::completion_value(ctx, buffer, kValueBytes, answer) < 0) {
        return;
    }
    long long button = -1;
    answer.find("button").as_i64(button);
    basalt::server::log::log_info(Buffer().add("form button ").add(button).c_str());
}

void on_dialog(basalt::server::core::Ctx ctx) {
    char buffer[kValueBytes];
    basalt::server::values::Value answer;
    if (basalt::server::events::completion_value(ctx, buffer, kValueBytes, answer) < 0) {
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

void greet(basalt::server::core::Ctx ctx) {
    char buffer[kValueBytes];
    char listing[kListBytes];
    basalt::server::values::Value value;
    const basalt::server::core::Player player = basalt::server::events::event_player(ctx);
    basalt::server::events::event_get(ctx, "world", buffer, kValueBytes, value);
    basalt::server::events::event_fields(ctx, buffer, kValueBytes, value);

    basalt::server::events::event_describe("player.chat", buffer, kValueBytes, value);
    basalt::server::events::event_describe("no.such.event", buffer, kValueBytes, value);
    basalt::server::events::event_names(listing, kListBytes, value);

    const basalt::server::core::Text<64> uuid = basalt::server::events::player_uuid(player);
    basalt::server::events::player_by_uuid(uuid.c_str());
    basalt::server::events::player_by_uuid("00000000-0000-0000-0000-999999999999");
    basalt::server::events::player_uuid(9999);

    basalt::server::events::player_get(player, "food", buffer, kValueBytes, value);
    basalt::server::events::player_get(player, "no_such_property", buffer, kValueBytes, value);
    basalt::server::values::ValueBuffer<32> food;
    food.integer(18);
    basalt::server::events::player_set(player, "food", food.data(), food.finish());
    basalt::server::events::player_set(player, "name", food.data(), food.finish());
    basalt::server::events::player_describe(listing, kListBytes, value);
    basalt::server::values::ValueBuffer<32> none;
    none.list().end();
    basalt::server::events::player_call(player, "reset_idle", none.data(), none.finish(), buffer, kValueBytes, value);
    basalt::server::events::player_call(player, "no_such_operation", none.data(), none.finish(), buffer, kValueBytes,
                                        value);

    double health = 0;
    basalt::server::events::player_health(player, &health);
    basalt::server::events::player_set_health(player, 10.5);
    basalt::server::events::player_set_health(player, -1.0);
    basalt::server::events::player_gamemode(player);
    basalt::server::events::player_set_gamemode(player, "creative");
    basalt::server::events::player_set_gamemode(player, "bogus");
    basalt::server::events::player_teleport(player, "", 10.5, 64.0, -3.25, 90.0f, 15.0f);
    basalt::server::events::player_teleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0f, 0.0f);
    basalt::server::events::player_title(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10);
    basalt::server::events::player_title(player, "<gold>Welcome", "", -1, 40, 10);
    basalt::server::events::player_effect(player, "minecraft:speed", 200, 1);
    basalt::server::events::player_effect(player, "no_such_effect", 200, 1);

    basalt::server::values::ValueBuffer<64> trade;
    trade.map().key("entity").integer(1).end();
    basalt::server::events::player_open_inventory(player, "trades", trade.data(), trade.finish());
    basalt::server::events::player_open_inventory(player, "no_such_window", trade.data(), trade.finish());
    basalt::server::events::player_close_window(player);

    basalt::server::values::ValueBuffer<256> form;
    form.map().key("title").string("Pick one").key("buttons").list().string("Yes").string("No").end();
    form.key("fields").list().map().key("key").string("name").key("type").string("text").key("label").string("Name");
    form.end().end().end();
    basalt::server::events::form_open(player, form.data(), form.finish(), on_form);
    basalt::server::values::ValueBuffer<32> broken;
    broken.map().key("title").integer(1).end();
    basalt::server::events::form_open(player, broken.data(), broken.finish(), on_form);
    basalt::server::events::form_close(player);
    basalt::server::events::form_open(player, form.data(), form.finish(), on_form);
    basalt::server::events::dialog_show(player, "minecraft:confirm", on_dialog);
    basalt::server::events::dialog_clear(player);
    basalt::server::events::dialog_show(player, "minecraft:confirm", on_dialog);
    basalt::server::events::player_kick(9999, "bye");
}

void chat(basalt::server::core::Ctx ctx) {
    char buffer[kValueBytes];
    basalt::server::values::Value value;
    basalt::server::events::event_name(ctx);
    basalt::server::events::event_get(ctx, "message", buffer, kValueBytes, value);
    const char* text = "";
    int length = 0;
    value.as_str(text, length);
    basalt::server::events::event_get(ctx, "no_such_field", buffer, kValueBytes, value);
    basalt::server::events::event_cancelled(ctx);
    basalt::server::events::event_is_post(ctx);
    basalt::server::values::ValueBuffer<32> wrong;
    wrong.string("x");
    basalt::server::events::event_set(ctx, "player_name", wrong.data(), wrong.finish());
    if (length == 6 && text[0] == 'c') {
        basalt::server::events::event_cancel_with(ctx, "<red>Not allowed</red>");
        return;
    }
    if (length == 3 && text[0] == 'b') {
        basalt::server::events::player_kick(basalt::server::events::event_player(ctx), "<red>Bye</red>");
        return;
    }
    if (length == 4 && text[0] == 'p') {
        basalt::server::values::ValueBuffer<64> payload;
        payload.map().key("from").string("chat").end();
        basalt::server::events::event_emit("example:pong", payload.data(), payload.finish());
        basalt::server::events::event_emit("nocolon", payload.data(), payload.finish());
    }
    char changed[96];
    int at = 0;
    for (int i = 0; i < length && at < 90; ++i) {
        changed[at++] = text[i];
    }
    changed[at++] = '!';
    basalt::server::values::ValueBuffer<128> replacement;
    replacement.string(changed, at);
    basalt::server::events::event_set(ctx, "message", replacement.data(), replacement.finish());
    basalt::server::events::event_fields(ctx, buffer, kValueBytes, value);
}

void chat_post(basalt::server::core::Ctx ctx) {
    const bool post = basalt::server::events::event_is_post(ctx) == 1;
    const bool cancelled = basalt::server::events::event_cancelled(ctx) == 1;
    basalt::server::values::ValueBuffer<32> replacement;
    replacement.string("late");
    basalt::server::events::event_set(ctx, "message", replacement.data(), replacement.finish());
    basalt::server::events::event_cancel_with(ctx, "<red>Too late</red>");
    const basalt::server::core::Text<256> reason = basalt::server::events::event_cancel_message(ctx);
    basalt::server::log::log_info(Buffer()
                                      .add(post ? "post " : "decision ")
                                      .add(cancelled ? "cancelled " : "delivered ")
                                      .add(reason.ok() ? reason.c_str() : "no message")
                                      .c_str());
}

void ping(basalt::server::core::Ctx ctx) {
    char buffer[kValueBytes];
    basalt::server::values::Value payload;
    basalt::server::events::event_payload(ctx, buffer, kValueBytes, payload);
    long long level = 0;
    payload.find("level").as_i64(level);
    basalt::server::log::log_info(Buffer().add("ping level ").add(level).c_str());
}

} // namespace

void register_all() {
    basalt::server::events::event_on_ex("player.chat", basalt::server::core::kNormal, 0, nullptr, 0, chat);
    basalt::server::events::event_on_ex("player.chat", basalt::server::core::kHighest,
                                        basalt::server::events::kEventPost | basalt::server::events::kEventCancelled,
                                        nullptr, 0, chat_post);
    basalt::server::values::ValueBuffer<64> filters;
    filters.map().key("world").string("world").end();
    basalt::server::events::event_on_ex("player.join", basalt::server::core::kLow, 0, filters.data(), filters.finish(),
                                        greet);
    basalt::server::events::event_on_ex("example:ping", basalt::server::core::kNormal, 0, nullptr, 0, ping);
    basalt::server::events::event_on_ex("no.such.event", basalt::server::core::kNormal, 0, nullptr, 0, ping);
}

} // namespace area_events
