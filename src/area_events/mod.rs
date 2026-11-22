use basalt_server::events as ev;
use basalt_server::values::Writer;
use basalt_server::core::{Args, Ctx};

const VALUE_BYTES: usize = 512;
const LIST_BYTES: usize = 8192;

fn doc(build: impl FnOnce(&mut Writer)) -> Vec<u8> {
    let mut writer = Writer::new();
    build(&mut writer);
    writer.into_vec().unwrap_or_default()
}

fn on_form(ctx: Ctx) {
    let Ok(answer) = ev::completion_value(ctx, VALUE_BYTES) else {
        return;
    };
    let button = answer.root().find("button").and_then(|b| b.as_i64()).unwrap_or(-1);
    basalt_server::log::info(&format!("form button {button}"));
}

fn on_dialog(ctx: Ctx) {
    let Ok(answer) = ev::completion_value(ctx, VALUE_BYTES) else {
        return;
    };
    let id = answer.root().find("id").and_then(|i| i.as_str()).unwrap_or("").to_string();
    basalt_server::log::info(&format!("dialog id {id}"));
}

fn greet(ctx: Ctx) {
    let player = basalt_server::events::player(ctx);
    let _ = ev::event_get(ctx, "world", VALUE_BYTES);
    let _ = ev::event_fields(ctx, VALUE_BYTES);

    let _ = ev::event_describe("player.chat", VALUE_BYTES);
    let _ = ev::event_describe("no.such.event", VALUE_BYTES);
    let _ = ev::event_names(LIST_BYTES);

    let uuid = ev::player_uuid(player).unwrap_or_default();
    ev::player_by_uuid(&uuid);
    ev::player_by_uuid("00000000-0000-0000-0000-999999999999");
    ev::player_uuid(9999);

    let _ = ev::player_get(player, "food", VALUE_BYTES);
    let _ = ev::player_get(player, "no_such_property", VALUE_BYTES);
    let food = doc(|w| {
        w.int(18);
    });
    ev::player_set(player, "food", &food);
    ev::player_set(player, "name", &food);
    let _ = ev::player_describe(LIST_BYTES);
    let none = doc(|w| {
        w.begin_list().end();
    });
    let _ = ev::player_call(player, "reset_idle", &none, VALUE_BYTES);
    let _ = ev::player_call(player, "no_such_operation", &none, VALUE_BYTES);

    ev::player_health(player);
    ev::player_set_health(player, 10.5);
    ev::player_set_health(player, -1.0);
    ev::player_gamemode(player);
    ev::player_set_gamemode(player, "creative");
    ev::player_set_gamemode(player, "bogus");
    let no_args = Args::new();
    ev::player_teleport(player, "", 10.5, 64.0, -3.25, 90.0, 15.0);
    ev::player_teleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0, 0.0);
    ev::player_title(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10, &no_args);
    ev::player_title(player, "<gold>Welcome", "", -1, 40, 10, &no_args);
    ev::player_effect(player, "minecraft:speed", 200, 1);
    ev::player_effect(player, "no_such_effect", 200, 1);

    let trade = doc(|w| {
        w.begin_map().key("entity").int(1).end();
    });
    ev::player_open_inventory(player, "trades", &trade);
    ev::player_open_inventory(player, "no_such_window", &trade);
    ev::player_close_window(player);

    let form = doc(|w| {
        w.begin_map().key("title").string("Pick one").key("buttons").begin_list().string("Yes").string("No").end();
        w.key("fields").begin_list().begin_map().key("key").string("name").key("type").string("text");
        w.key("label").string("Name").end().end().end();
    });
    ev::form_open(player, &form, on_form);
    let broken = doc(|w| {
        w.begin_map().key("title").int(1).end();
    });
    ev::form_open(player, &broken, on_form);
    ev::form_close(player);
    ev::form_open(player, &form, on_form);
    ev::dialog_show(player, "minecraft:confirm", on_dialog);
    ev::dialog_clear(player);
    ev::dialog_show(player, "minecraft:confirm", on_dialog);
    ev::player_kick(9999, "bye", &no_args);
}

fn chat(ctx: Ctx) {
    let no_args = Args::new();
    ev::event_name(ctx);
    let text = ev::event_get(ctx, "message", VALUE_BYTES)
        .ok()
        .and_then(|v| v.root().as_str().map(str::to_string))
        .unwrap_or_default();
    let _ = ev::event_get(ctx, "no_such_field", VALUE_BYTES);
    ev::event_cancelled(ctx);
    ev::event_is_post(ctx);
    let wrong = doc(|w| {
        w.string("x");
    });
    ev::event_set(ctx, "player_name", &wrong);
    if text.len() == 6 && text.starts_with('c') {
        ev::event_cancel_with(ctx, "<red>Not allowed</red>", &no_args);
        return;
    }
    if text.len() == 3 && text.starts_with('b') {
        ev::player_kick(basalt_server::events::player(ctx), "<red>Bye</red>", &no_args);
        return;
    }
    if text.len() == 4 && text.starts_with('p') {
        let payload = doc(|w| {
            w.begin_map().key("from").string("chat").end();
        });
        ev::event_emit("example:pong", &payload);
        ev::event_emit("nocolon", &payload);
    }
    let replacement = doc(|w| {
        w.string(&format!("{text}!"));
    });
    ev::event_set(ctx, "message", &replacement);
    let _ = ev::event_fields(ctx, VALUE_BYTES);
}

fn chat_post(ctx: Ctx) {
    let no_args = Args::new();
    let post = ev::event_is_post(ctx) == 1;
    let cancelled = ev::event_cancelled(ctx) == 1;
    let late = doc(|w| {
        w.string("late");
    });
    ev::event_set(ctx, "message", &late);
    ev::event_cancel_with(ctx, "<red>Too late</red>", &no_args);
    let reason = ev::event_cancel_message(ctx).unwrap_or_else(|| "no message".to_string());
    basalt_server::log::info(&format!(
        "{}{}{}",
        if post { "post " } else { "decision " },
        if cancelled { "cancelled " } else { "delivered " },
        reason
    ));
}

fn ping(ctx: Ctx) {
    let level = ev::event_payload(ctx, VALUE_BYTES)
        .ok()
        .and_then(|v| v.root().find("level").and_then(|l| l.as_i64()))
        .unwrap_or(0);
    basalt_server::log::info(&format!("ping level {level}"));
}

pub fn register() {
    ev::on_ex("player.chat", basalt_server::core::NORMAL, 0, &[], chat);
    ev::on_ex("player.chat", basalt_server::core::HIGHEST, ev::POST | ev::CANCELLED, &[], chat_post);
    let filters = doc(|w| {
        w.begin_map().key("world").string("world").end();
    });
    ev::on_ex("player.join", basalt_server::core::LOW, 0, &filters, greet);
    ev::on_ex("example:ping", basalt_server::core::NORMAL, 0, &[], ping);
    ev::on_ex("no.such.event", basalt_server::core::NORMAL, 0, &[], ping);
}
