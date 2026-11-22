#include "packets/packets.hpp"

#include <basalt/server.hpp>

namespace packets {

namespace {

constexpr const char* kChannel = "example:chan";
constexpr int kCapacity = 1024;

int command_listener = 0;
basalt::server::core::Ctx last_ctx = 0;

bool is(const char* text, const char* word) {
    while (*text != 0 && *text == *word) {
        ++text;
        ++word;
    }
    return *text == *word;
}

basalt::server::core::Player source(basalt::server::core::Ctx ctx) {
    const auto name = basalt::server::commands::command_source_name(ctx);
    return basalt::server::players::player_by_name(name.c_str());
}

void on_channel(basalt::server::core::Ctx ctx) {
    char info[kCapacity];
    char body[kCapacity];
    basalt::server::packets::packet_ctx_info(ctx, info, kCapacity);
    const int size = basalt::server::packets::packet_ctx_body(ctx, body, kCapacity);
    basalt::server::packets::channel_send(basalt::server::packets::packet_ctx_player(ctx), kChannel, body, size);
    basalt::server::packets::packet_ctx_cancel(ctx);
}

void on_chat(basalt::server::core::Ctx ctx) {
    last_ctx = ctx;
    char body[kCapacity];
    const int size = basalt::server::packets::packet_ctx_body(ctx, body, kCapacity);
    basalt::server::packets::packet_ctx_player(ctx);
    if (size > 0 && body[0] == '!') {
        basalt::server::packets::packet_ctx_cancel(ctx);
        return;
    }
    body[size] = '#';
    basalt::server::packets::packet_ctx_set_body(ctx, body, size + 1);
}

void on_watch(basalt::server::core::Ctx ctx) {
    char info[kCapacity];
    const int size = basalt::server::packets::packet_ctx_info(ctx, info, kCapacity);
    basalt::server::values::Value root;
    const char* name = nullptr;
    int length = 0;
    if (root.open(info, size) && root.find("name").as_str(name, length)) {
        char line[96] = {};
        for (int i = 0; i < length && i < 95; ++i) {
            line[i] = name[i];
        }
        basalt::server::log::log_info(line);
    }
    basalt::server::packets::packet_ctx_cancel(ctx);
}

void on_command_packet(basalt::server::core::Ctx ctx) {
    basalt::server::packets::packet_ctx_send(ctx, "clientbound.system_chat", "ok", 2, false);
    basalt::server::packets::packet_ctx_receive(ctx, "serverbound.chat_message", "hi", 2, true);
    basalt::server::packets::packet_ctx_send(ctx, "serverbound.chat_message", "no", 2, false);
}

void info(basalt::server::core::Player) {
    char out[kCapacity];
    basalt::server::packets::packet_names("serverbound", out, kCapacity);
    basalt::server::packets::packet_names("", out, kCapacity);
    basalt::server::packets::packet_lookup("serverbound.chat_message", out, kCapacity);
    basalt::server::packets::packet_lookup("clientbound.play.*", out, kCapacity);
    basalt::server::packets::packet_lookup("bogus.name", out, kCapacity);
    basalt::server::packets::packet_player_actions(out, kCapacity);
}

void send(basalt::server::core::Player player) {
    basalt::server::packets::packet_send(player, "clientbound.set_title_text", "hi", 2, false);
    basalt::server::packets::packet_send(player, "serverbound.chat_message", "x", 1, false);
    basalt::server::packets::packet_send(player, "clientbound.system_chat", "again", 5, true);
}

void receive(basalt::server::core::Player player) {
    basalt::server::packets::packet_receive(player, "serverbound.chat_command", "/help", 5, false);
    basalt::server::packets::packet_receive(player, "clientbound.set_title_text", "x", 1, false);
}

void channel(basalt::server::core::Player player) {
    basalt::server::packets::channel_send(player, kChannel, "ping", 4);
    basalt::server::packets::channel_send(0, kChannel, "ping", 4);
}

void action(basalt::server::core::Player player) {
    basalt::server::values::ValueBuffer<64> held;
    held.list().integer(3).end();
    basalt::server::packets::packet_player_receive(player, "held_slot", held.data(), held.finish());
    basalt::server::values::ValueBuffer<96> payload;
    payload.list().string(kChannel).string("pong").end();
    basalt::server::packets::packet_player_receive(player, "custom_payload", payload.data(), payload.finish());
    basalt::server::packets::packet_player_receive(player, "bogus", held.data(), held.finish());
}

void stale(basalt::server::core::Player) {
    char out[kCapacity];
    basalt::server::packets::packet_ctx_cancel(last_ctx);
    basalt::server::packets::packet_ctx_info(9999, out, kCapacity);
}

void unlisten(basalt::server::core::Player) {
    basalt::server::packets::packet_unlisten(command_listener);
    basalt::server::packets::packet_unlisten(command_listener);
    basalt::server::packets::packet_unlisten(0);
}

void on_command(basalt::server::core::Ctx ctx) {
    const auto action_name = basalt::server::commands::command_arg_str(ctx, "action");
    const char* word = action_name.c_str();
    const basalt::server::core::Player player = source(ctx);
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
    basalt::server::packets::channel_listen(kChannel, on_channel);
    basalt::server::packets::packet_listen("serverbound.chat_message", "first", false, on_chat);
    basalt::server::packets::packet_listen("clientbound.*", nullptr, true, on_watch);
    basalt::server::packets::packet_listen("serverbound.play.*", "late", true, on_watch);
    command_listener =
        basalt::server::packets::packet_listen("serverbound.chat_command", nullptr, false, on_command_packet);
    basalt::server::packets::packet_listen("nope", nullptr, false, on_watch);
    basalt::server::commands::command("pkt <action: word>", "", on_command);
}

} // namespace packets
