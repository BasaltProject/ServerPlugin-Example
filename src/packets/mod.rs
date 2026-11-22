use basalt_server::packets as net;
use basalt_server::values::{Value, Writer};
use basalt_server::core::{Ctx, Player};
use std::cell::Cell;

const CHANNEL: &str = "example:chan";
const CAPACITY: usize = 1024;

thread_local! {
    static COMMAND_LISTENER: Cell<i32> = Cell::new(0);
    static LAST_CTX: Cell<Ctx> = Cell::new(0);
}

fn source(ctx: Ctx) -> Player {
    let name = basalt_server::commands::source_name(ctx).unwrap_or_default();
    basalt_server::players::by_name(&name)
}

fn on_channel(ctx: Ctx) {
    let mut info = [0u8; CAPACITY];
    let mut body = [0u8; CAPACITY];
    net::packet_ctx_info(ctx, &mut info);
    let size = net::packet_ctx_body(ctx, &mut body) as usize;
    net::channel_send(net::packet_ctx_player(ctx), CHANNEL, &body[..size]);
    net::packet_ctx_cancel(ctx);
}

fn on_chat(ctx: Ctx) {
    LAST_CTX.with(|last| last.set(ctx));
    let mut body = [0u8; CAPACITY];
    let size = net::packet_ctx_body(ctx, &mut body) as usize;
    net::packet_ctx_player(ctx);
    if size > 0 && body[0] == b'!' {
        net::packet_ctx_cancel(ctx);
        return;
    }
    body[size] = b'#';
    net::packet_ctx_set_body(ctx, &body[..size + 1]);
}

fn on_watch(ctx: Ctx) {
    let mut info = [0u8; CAPACITY];
    let size = net::packet_ctx_info(ctx, &mut info) as usize;
    if let Some(root) = Value::open(&info[..size]) {
        if let Some(name) = root.find("name").and_then(|v| v.as_str()) {
            basalt_server::log::info(name);
        }
    }
    net::packet_ctx_cancel(ctx);
}

fn on_command_packet(ctx: Ctx) {
    net::packet_ctx_send(ctx, "clientbound.system_chat", b"ok", false);
    net::packet_ctx_receive(ctx, "serverbound.chat_message", b"hi", true);
    net::packet_ctx_send(ctx, "serverbound.chat_message", b"no", false);
}

fn info() {
    let mut out = [0u8; CAPACITY];
    net::packet_names("serverbound", &mut out);
    net::packet_names("", &mut out);
    net::packet_lookup("serverbound.chat_message", &mut out);
    net::packet_lookup("clientbound.play.*", &mut out);
    net::packet_lookup("bogus.name", &mut out);
    net::packet_player_actions(&mut out);
}

fn send(player: Player) {
    net::packet_send(player, "clientbound.set_title_text", b"hi", false);
    net::packet_send(player, "serverbound.chat_message", b"x", false);
    net::packet_send(player, "clientbound.system_chat", b"again", true);
}

fn receive(player: Player) {
    net::packet_receive(player, "serverbound.chat_command", b"/help", false);
    net::packet_receive(player, "clientbound.set_title_text", b"x", false);
}

fn channel(player: Player) {
    net::channel_send(player, CHANNEL, b"ping");
    net::channel_send(0, CHANNEL, b"ping");
}

fn action(player: Player) {
    let mut held = Writer::new();
    held.begin_list().int(3).end();
    let held = held.into_vec().unwrap_or_default();
    net::packet_player_receive(player, "held_slot", &held);
    let mut payload = Writer::new();
    payload.begin_list().string(CHANNEL).string("pong").end();
    let payload = payload.into_vec().unwrap_or_default();
    net::packet_player_receive(player, "custom_payload", &payload);
    net::packet_player_receive(player, "bogus", &held);
}

fn stale() {
    let mut out = [0u8; CAPACITY];
    net::packet_ctx_cancel(LAST_CTX.with(|last| last.get()));
    net::packet_ctx_info(9999, &mut out);
}

fn unlisten() {
    let listener = COMMAND_LISTENER.with(|id| id.get());
    net::packet_unlisten(listener);
    net::packet_unlisten(listener);
    net::packet_unlisten(0);
}

fn on_command(ctx: Ctx) {
    let word = basalt_server::commands::arg_str(ctx, "action").unwrap_or_default();
    let player = source(ctx);
    match word.as_str() {
        "info" => info(),
        "send" => send(player),
        "recv" => receive(player),
        "chan" => channel(player),
        "action" => action(player),
        "stale" => stale(),
        "unlisten" => unlisten(),
        _ => {}
    }
}

pub fn register() {
    net::channel_listen(CHANNEL, on_channel);
    net::packet_listen("serverbound.chat_message", Some("first"), false, on_chat);
    net::packet_listen("clientbound.*", None, true, on_watch);
    net::packet_listen("serverbound.play.*", Some("late"), true, on_watch);
    let id = net::packet_listen("serverbound.chat_command", None, false, on_command_packet);
    COMMAND_LISTENER.with(|cell| cell.set(id));
    net::packet_listen("nope", None, false, on_watch);
    basalt_server::commands::register("pkt <action: word>", "", on_command);
}
