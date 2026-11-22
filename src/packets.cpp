#include "packets.hpp"

#include <basalt.hpp>

namespace packets {

namespace {

constexpr const char* kChannel = "example:chan";
constexpr int kCapacity = 1024;

int command_listener = 0;
basalt::Ctx last_ctx = 0;

bool is(const char* text, const char* word) {
    while (*text != 0 && *text == *word) {
        ++text;
        ++word;
    }
    return *text == *word;
}

basalt::Player source(basalt::Ctx ctx) {
    const auto name = basalt::command_source_name(ctx);
    return basalt::player_by_name(name.c_str());
}

void on_channel(basalt::Ctx ctx) {
    char info[kCapacity];
    char body[kCapacity];
    basalt::packet_ctx_info(ctx, info, kCapacity);
    const int size = basalt::packet_ctx_body(ctx, body, kCapacity);
    basalt::channel_send(basalt::packet_ctx_player(ctx), kChannel, body, size);
    basalt::packet_ctx_cancel(ctx);
}

void on_chat(basalt::Ctx ctx) {
    last_ctx = ctx;
    char body[kCapacity];
    const int size = basalt::packet_ctx_body(ctx, body, kCapacity);
    basalt::packet_ctx_player(ctx);
    if (size > 0 && body[0] == '!') {
        basalt::packet_ctx_cancel(ctx);
        return;
    }
    body[size] = '#';
    basalt::packet_ctx_set_body(ctx, body, size + 1);
}

void on_watch(basalt::Ctx ctx) {
    char info[kCapacity];
    const int size = basalt::packet_ctx_info(ctx, info, kCapacity);
    basalt::Value root;
    const char* name = nullptr;
    int length = 0;
    if (root.open(info, size) && root.find("name").as_str(name, length)) {
        char line[96] = {};
        for (int i = 0; i < length && i < 95; ++i) {
            line[i] = name[i];
        }
        basalt::log_info(line);
    }
    basalt::packet_ctx_cancel(ctx);
}

void on_command_packet(basalt::Ctx ctx) {
    basalt::packet_ctx_send(ctx, "clientbound.system_chat", "ok", 2, false);
    basalt::packet_ctx_receive(ctx, "serverbound.chat_message", "hi", 2, true);
    basalt::packet_ctx_send(ctx, "serverbound.chat_message", "no", 2, false);
}

void info(basalt::Player) {
    char out[kCapacity];
    basalt::packet_names("serverbound", out, kCapacity);
    basalt::packet_names("", out, kCapacity);
    basalt::packet_lookup("serverbound.chat_message", out, kCapacity);
    basalt::packet_lookup("clientbound.play.*", out, kCapacity);
    basalt::packet_lookup("bogus.name", out, kCapacity);
    basalt::packet_player_actions(out, kCapacity);
}

void send(basalt::Player player) {
    basalt::packet_send(player, "clientbound.set_title_text", "hi", 2, false);
    basalt::packet_send(player, "serverbound.chat_message", "x", 1, false);
    basalt::packet_send(player, "clientbound.system_chat", "again", 5, true);
}

void receive(basalt::Player player) {
    basalt::packet_receive(player, "serverbound.chat_command", "/help", 5, false);
    basalt::packet_receive(player, "clientbound.set_title_text", "x", 1, false);
}

void channel(basalt::Player player) {
    basalt::channel_send(player, kChannel, "ping", 4);
    basalt::channel_send(0, kChannel, "ping", 4);
}

void action(basalt::Player player) {
    basalt::ValueBuffer<64> held;
    held.list().integer(3).end();
    basalt::packet_player_receive(player, "held_slot", held.data(), held.finish());
    basalt::ValueBuffer<96> payload;
    payload.list().string(kChannel).string("pong").end();
    basalt::packet_player_receive(player, "custom_payload", payload.data(), payload.finish());
    basalt::packet_player_receive(player, "bogus", held.data(), held.finish());
}

void stale(basalt::Player) {
    char out[kCapacity];
    basalt::packet_ctx_cancel(last_ctx);
    basalt::packet_ctx_info(9999, out, kCapacity);
}

void unlisten(basalt::Player) {
    basalt::packet_unlisten(command_listener);
    basalt::packet_unlisten(command_listener);
    basalt::packet_unlisten(0);
}

void on_command(basalt::Ctx ctx) {
    const auto action_name = basalt::command_arg_str(ctx, "action");
    const char* word = action_name.c_str();
    const basalt::Player player = source(ctx);
    if (is(word, "info")) {
        info(player);
    } else if (is(word, "send")) {
        send(player);
    } else if (is(word, "recv")) {
        receive(player);
    } else if (is(word, "chan")) {
        channel(player);
    } else if (is(word, "action")) {
        action(player);
    } else if (is(word, "stale")) {
        stale(player);
    } else if (is(word, "unlisten")) {
        unlisten(player);
    }
}

} // namespace

void register_all() {
    basalt::channel_listen(kChannel, on_channel);
    basalt::packet_listen("serverbound.chat_message", "first", false, on_chat);
    basalt::packet_listen("clientbound.*", nullptr, true, on_watch);
    basalt::packet_listen("serverbound.play.*", "late", true, on_watch);
    command_listener = basalt::packet_listen("serverbound.chat_command", nullptr, false, on_command_packet);
    basalt::packet_listen("nope", nullptr, false, on_watch);
    basalt::command("pkt <action: word>", "", on_command);
}

} // namespace packets
