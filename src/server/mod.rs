use basalt_server::server::{self as srv, Reply};
use basalt_server::values::{Value, Writer};
use basalt_server::core::{Args, Ctx, Player};
use std::cell::Cell;

thread_local! {
    static WAITS: Cell<i64> = const { Cell::new(0) };
}

fn say(ctx: Ctx, text: &str) {
    basalt_server::commands::reply(ctx, "@server-line", &Args::new().add("text", text));
}

fn say_number(ctx: Ctx, label: &str, value: i64) {
    say(ctx, &format!("{label} {value}"));
}

fn note_number(label: &str, value: i64) {
    basalt_server::log::info(&format!("{label} {value}"));
}

fn source_player(ctx: Ctx) -> Player {
    basalt_server::players::by_name(&basalt_server::commands::source_name(ctx).unwrap_or_default())
}

fn int(value: Option<Value>) -> i64 {
    value.and_then(|v| v.as_i64()).unwrap_or(-1)
}

fn count(value: Option<Value>) -> i64 {
    value.and_then(|v| v.count()).map_or(-1, |n| n as i64)
}

fn permissions(ctx: Ctx) {
    let player = source_player(ctx);
    say_number(ctx, "has example.vip", srv::permission_has(player, "example.vip") as i64);
    say_number(ctx, "console has example.vip", srv::permission_check("console", "example.vip") as i64);
    say_number(ctx, "grant", srv::permission_grant(player, "example.vip") as i64);
    say_number(ctx, "has example.vip", srv::permission_has(player, "example.vip") as i64);
    say_number(ctx, "drop", srv::permission_drop(player, "example.vip") as i64);
    say_number(ctx, "has example.vip", srv::permission_has(player, "example.vip") as i64);
    say(ctx, &srv::permission_meta("Steve", "prefix").unwrap_or_default());
    say_number(ctx, "unknown subject", srv::permission_check("Nobody", "example.vip") as i64);
}

fn vip_provider(ctx: Ctx) {
    let node = srv::permission_query(ctx, "node").unwrap_or_default();
    let kind = srv::permission_query(ctx, "kind").unwrap_or_default();
    if kind.as_bytes().first() == Some(&b'c') {
        srv::permission_answer(ctx, if node.as_bytes().get(8) == Some(&b'v') { 1 } else { -1 });
    } else {
        srv::permission_answer_meta(ctx, "VIP");
    }
}

fn broadcast(ctx: Ctx) {
    let both = srv::BROADCAST_PLAYERS | srv::BROADCAST_CONSOLE;
    say_number(ctx, "reached", srv::broadcast("@server-line", &Args::new().add("text", "hello everyone"), both) as i64);
    say_number(ctx, "console only", srv::broadcast("plain text", &Args::new(), srv::BROADCAST_CONSOLE) as i64);
    say_number(ctx, "nobody", srv::broadcast("x", &Args::new(), 0) as i64);
    say(ctx, &srv::placeholder_fill("%player_name% is here", source_player(ctx)).unwrap_or_default());
}

fn info(ctx: Ctx) {
    let reply = srv::info();
    say_number(ctx, "info", srv::status(&reply) as i64);
    let Ok(reply) = reply else { return };
    let root = reply.root();
    say_number(ctx, "online", root.find("online").and_then(|v| v.as_i64()).unwrap_or(-1));
    let software = root.find("software").and_then(|v| v.as_str_bytes()).map_or(0, |s| s.len());
    say(ctx, &format!("software {software}"));
    say_number(ctx, "plugins", count(root.find("plugins")));
    say_number(ctx, "lane", int(root.find("plugin").and_then(|v| v.find("lane"))));
}

fn admin(ctx: Ctx) {
    let mut key = Writer::with_limit(256);
    key.begin_map().key("key").string("motd").end();
    let mut reply: Reply = srv::call("settings.get", Some(&key));
    say_number(ctx, "settings.get", srv::status(&reply) as i64);
    let motd = reply.as_ref().ok().and_then(|r| r.root().as_str_bytes().map(|s| s.len())).unwrap_or(0);
    say(ctx, &format!("motd length {motd}"));
    reply = srv::call("tick.rate", None);
    say_number(ctx, "tick.rate", srv::status(&reply) as i64);
    let rate = reply.as_ref().ok().and_then(|r| r.root().as_f64()).unwrap_or(-1.0);
    say_number(ctx, "rate", rate as i64);
    reply = srv::call("ops", None);
    say_number(ctx, "ops", srv::status(&reply) as i64);
    say_number(ctx, "op count", count(reply.as_ref().ok().map(|r| r.root())));
    say_number(ctx, "unknown op", srv::status(&srv::call("nope.nope", None)) as i64);
    say_number(ctx, "datapacks", srv::status(&srv::call("datapacks.list", None)) as i64);
}

fn files(ctx: Ctx) {
    say_number(ctx, "write", srv::file_write("notes/a.txt", "hello") as i64);
    say_number(ctx, "exists", srv::file_exists("notes/a.txt") as i64);
    let mut text = [0u8; 64];
    let size = srv::file_read("notes/a.txt", &mut text[..63]);
    say(ctx, &String::from_utf8_lossy(&text[..size.clamp(0, 63) as usize]));
    let listing = srv::file_list("notes");
    say_number(ctx, "list", srv::status(&listing) as i64);
    say_number(ctx, "entries", count(listing.as_ref().ok().map(|r| r.root())));
    let stat = srv::file_stat("notes/a.txt");
    say_number(ctx, "stat", srv::status(&stat) as i64);
    say_number(ctx, "size", int(stat.as_ref().ok().and_then(|r| r.root().find("size"))));
    say_number(ctx, "outside", srv::file_write("secret/a.txt", "x") as i64);
    say_number(ctx, "escape", srv::file_write("notes/../a.txt", "x") as i64);
    let mut readme = [0u8; 32];
    say_number(ctx, "packaged", srv::file_read("data/readme.txt", &mut readme) as i64);
    say_number(ctx, "delete", srv::file_delete("notes/a.txt", false) as i64);
    say_number(ctx, "gone", srv::file_exists("notes/a.txt") as i64);
}

fn store(ctx: Ctx) {
    say_number(ctx, "set", srv::store_set_i64("visits", 3) as i64);
    let mut reply = srv::store_get("visits");
    say_number(ctx, "get", srv::status(&reply) as i64);
    say_number(ctx, "visits", int(reply.as_ref().ok().map(|r| r.root())));
    say_number(ctx, "missing", srv::status(&srv::store_get("nothing")) as i64);
    let mut doc = Writer::with_limit(256);
    doc.begin_map().key("kills").int(7).key("tags").begin_list().string("a").string("b").end().end();
    say_number(ctx, "write", srv::data_write("stats", &doc) as i64);
    reply = srv::data_read("stats");
    say_number(ctx, "read", srv::status(&reply) as i64);
    let root = reply.as_ref().ok().map(|r| r.root());
    say_number(ctx, "kills", int(root.and_then(|v| v.find("kills"))));
    say_number(ctx, "tags", count(root.and_then(|v| v.find("tags"))));
    reply = srv::config_value("shop");
    say_number(ctx, "config", srv::status(&reply) as i64);
    say_number(ctx, "price", int(reply.as_ref().ok().and_then(|r| r.root().find("sparkles-price"))));
    say_number(ctx, "dashed", srv::status(&srv::config_value("payout_period")) as i64);
}

fn echo_service(ctx: Ctx) {
    let call = srv::result(ctx);
    let n = call.as_ref().ok().and_then(|r| r.root().find("args").and_then(|v| v.find("n")).and_then(|v| v.as_i64()));
    let mut answer = Writer::with_limit(256);
    answer.begin_map().key("echo").int(n.unwrap_or(0) * 2).end();
    srv::service_reply(ctx, &answer);
}

fn echo_done(ctx: Ctx) {
    let reply = srv::result(ctx);
    let root = reply.as_ref().ok().map(|r| r.root());
    let ok = root.and_then(|v| v.find("ok")).and_then(|v| v.as_bool()).unwrap_or(false);
    note_number("ok", ok as i64);
    note_number("echo", int(root.and_then(|v| v.find("value")).and_then(|v| v.find("echo"))));
}

fn missing_done(ctx: Ctx) {
    let reply = srv::result(ctx);
    let ok = reply.as_ref().ok().and_then(|r| r.root().find("ok")).and_then(|v| v.as_bool()).unwrap_or(true);
    note_number("late ok", ok as i64);
}

fn service(ctx: Ctx) {
    say_number(ctx, "has", srv::service_has("example.echo") as i64);
    say_number(ctx, "has other", srv::service_has("example.other") as i64);
    let mut args = Writer::with_limit(256);
    args.begin_map().key("n").int(21).end();
    say_number(ctx, "call", (srv::service_call("example.echo", &args, 0, echo_done) > 0) as i64);
    say_number(ctx, "absent", srv::service_call("example.other", &args, 0, missing_done) as i64);
}

fn bump(label: &'static str) -> impl FnMut(Ctx) {
    move |_| {
        let n = WAITS.with(|waits| {
            waits.set(waits.get() + 1);
            waits.get()
        });
        note_number(label, n);
    }
}

fn looked(ctx: Ctx) {
    let reply = srv::result(ctx);
    let length = match &reply {
        Ok(r) if r.root().is_null() => 0,
        Ok(r) => r.root().find("uuid").and_then(|v| v.as_str_bytes()).map_or(0, |s| s.len()),
        Err(_) => 0,
    };
    note_number("profile", length as i64);
}

fn later(ctx: Ctx) {
    say_number(ctx, "wait", (srv::wait(20, bump("waited")) > 0) as i64);
    say_number(ctx, "at", (srv::at("world", 0.0, 0.0, bump("placed")) > 0) as i64);
    say_number(ctx, "at unknown", srv::at("elsewhere", 0.0, 0.0, bump("placed")) as i64);
    say_number(ctx, "lookup", (srv::profile_lookup("Notch", looked) > 0) as i64);
}

fn dynamic(ctx: Ctx) {
    say(ctx, "dynamic ran");
    let amount = srv::command_arg_f64(ctx, "amount").unwrap_or(0.0);
    let flag = srv::command_arg_bool(ctx, "loud").unwrap_or(false);
    say_number(ctx, "amount", (amount * 10.0) as i64);
    say_number(ctx, "loud", flag as i64);
}

fn dynamic_complete(ctx: Ctx) {
    srv::complete_add(ctx, "alpha");
    srv::complete_add(ctx, "beta");
    if let Ok(typed) = srv::complete_input(ctx) {
        if !typed.is_empty() {
            srv::complete_add(ctx, &typed);
        }
    }
}

fn manage(ctx: Ctx) {
    let spec = "dyn [amount: float] [loud: bool]";
    say_number(ctx, "register", srv::command_ex(spec, "", dynamic, Some(dynamic_complete)) as i64);
    say_number(ctx, "again", srv::command(spec, "", dynamic) as i64);
    say_number(ctx, "alias", srv::command_alias("dyn", "dy") as i64);
    say_number(ctx, "alias missing", srv::command_alias("nothing", "no") as i64);
    say_number(ctx, "run", srv::command_run("srv hello", 0) as i64);
    say_number(ctx, "run as player", srv::command_run("srv hello", source_player(ctx)) as i64);
}

fn unmanage(ctx: Ctx) {
    say_number(ctx, "unregister", srv::command_unregister("dyn") as i64);
    say_number(ctx, "unregister again", srv::command_unregister("dyn") as i64);
}

fn hello(ctx: Ctx) {
    say(ctx, "hello from the command");
}

pub fn register() {
    srv::permission_provide(10, srv::PROVIDE_CHECK | srv::PROVIDE_META, vip_provider);
    srv::service_register("example.echo", echo_service);
    basalt_server::commands::register("srv hello", "", hello);
    basalt_server::commands::register("srv perms", "", permissions);
    basalt_server::commands::register("srv broadcast", "", broadcast);
    basalt_server::commands::register("srv info", "", info);
    basalt_server::commands::register("srv admin", "", admin);
    basalt_server::commands::register("srv files", "", files);
    basalt_server::commands::register("srv store", "", store);
    basalt_server::commands::register("srv service", "", service);
    basalt_server::commands::register("srv later", "", later);
    basalt_server::commands::register("srv manage", "", manage);
    basalt_server::commands::register("srv unmanage", "", unmanage);
}
