use basalt_server::hud;
use basalt_server::values::{Value, Writer};
use basalt_server::core::{Args, Ctx};
use std::sync::atomic::{AtomicI32, Ordering};

static G_NPC: AtomicI32 = AtomicI32::new(0);
static G_CLAIM: AtomicI32 = AtomicI32::new(0);

fn entity_of(npc: i32) -> i32 {
    let mut raw = [0u8; 768];
    let length = hud::npc_get(npc, &mut raw);
    if length <= 0 || length as usize > raw.len() {
        return 0;
    }
    Value::open(&raw[..length as usize]).and_then(|value| value.find("id")).and_then(|id| id.as_i64()).unwrap_or(0) as i32
}

fn clicked(_: Ctx) {
    basalt_server::log::info("npc clicked");
}

fn render(ctx: Ctx) {
    let mut w1 = Writer::new();
    w1.begin_map().key("title").string("<gold>Example").key("lines").begin_list().string("<white>Hello %player%").string("<gray>second line").end().end();
    let spec2 = w1.into_vec().unwrap_or_default();
    hud::surface_answer(ctx, &spec2);
}

fn board(ctx: Ctx) {
    let source = basalt_server::commands::source_name(ctx).unwrap_or_default();
    let player = basalt_server::players::by_name(&source);
    let main_board = hud::scoreboard_main();
    let own = hud::scoreboard_create();
    let mut w1 = Writer::new();
    w1.begin_map().key("criterion").string("dummy").key("title").string("<gold>Kills").key("hearts").boolean(false).end();
    let spec2 = w1.into_vec().unwrap_or_default();
    hud::objective_add(own, "kills", &spec2);
    let mut w3 = Writer::new();
    w3.begin_map().key("criterion").string("no_such_criterion").end();
    let spec4 = w3.into_vec().unwrap_or_default();
    hud::objective_add(own, "broken", &spec4);
    let mut buf5 = [0u8; 256];
    hud::scoreboard_list(own, "objectives", &mut buf5);
    hud::display_set(own, "sidebar", "kills");
    let mut buf6 = [0u8; 256];
    hud::display_get(own, "sidebar", &mut buf6);
    hud::display_set(own, "sidebar", "missing");
    hud::display_set(own, "nowhere", "kills");
    hud::score_set(own, "Steve", "kills", 7);
    hud::score_set(own, "Alex", "kills", 3);
    let mut out7 = 0i32;
    hud::score_get(own, "Steve", "kills", &mut out7);
    let mut buf8 = [0u8; 256];
    hud::scoreboard_list(own, "entries", &mut buf8);
    hud::score_reset(own, "Steve", "kills");
    let mut out9 = 0i32;
    hud::score_get(own, "Steve", "kills", &mut out9);
    hud::team_add(own, "red");
    hud::team_add(own, "red");
    let mut w10 = Writer::new();
    w10.begin_map().key("display_name").string("<red>Red").key("prefix").string("[R] ").key("suffix").string("!").key("color").string("red").key("friendly_fire").boolean(false).key("see_invisible").boolean(true).key("name_tags").string("hideForOtherTeams").key("death_messages").string("never").key("collision").string("pushOtherTeams").end();
    let spec11 = w10.into_vec().unwrap_or_default();
    hud::team_set(own, "red", &spec11);
    let mut w12 = Writer::new();
    w12.begin_map().key("color").string("blue").end();
    let spec13 = w12.into_vec().unwrap_or_default();
    hud::team_set(own, "blue", &spec13);
    hud::team_join(own, "red", "Steve");
    hud::team_join(own, "gone", "Steve");
    let mut buf14 = [0u8; 256];
    hud::team_of(own, "Steve", &mut buf14);
    let mut buf15 = [0u8; 256];
    hud::scoreboard_list(own, "teams", &mut buf15);
    let mut buf16 = [0u8; 256];
    hud::scoreboard_list(own, "bogus", &mut buf16);
    hud::team_leave(own, "red", "Steve");
    hud::team_leave(own, "red", "Steve");
    let mut buf17 = [0u8; 256];
    hud::team_of(own, "Steve", &mut buf17);
    hud::team_remove(own, "red");
    hud::team_remove(own, "red");
    hud::scoreboard_show(player, own);
    hud::scoreboard_shown(player);
    hud::scoreboard_show(player, main_board);
    hud::scoreboard_shown(player);
    hud::objective_remove(own, "kills");
    hud::objective_remove(own, "kills");
    hud::scoreboard_release(own);
    hud::scoreboard_release(own);
    hud::scoreboard_release(main_board);
    basalt_server::commands::reply(ctx, "board done", &Args::new());
}

fn bars(ctx: Ctx) {
    let source = basalt_server::commands::source_name(ctx).unwrap_or_default();
    let player = basalt_server::players::by_name(&source);
    let mut w1 = Writer::new();
    w1.begin_map().key("title").string("<green>Wave 1").key("color").string("green").key("style").string("notched_10").key("progress").float(0.5).key("darken_sky").boolean(false).key("boss_music").boolean(false).key("fog").boolean(false).key("visible").boolean(true).key("key").string("example:wave").end();
    let spec2 = w1.into_vec().unwrap_or_default();
    let bar = hud::bossbar_create(&spec2);
    let mut w3 = Writer::new();
    w3.begin_map().key("title").string("<green>Wave 1").key("key").string("example:wave").end();
    let spec4 = w3.into_vec().unwrap_or_default();
    hud::bossbar_create(&spec4);
    let mut buf5 = [0u8; 512];
    hud::bossbar_kept(&mut buf5);
    let mut buf6 = [0u8; 512];
    hud::bossbar_get(bar, &mut buf6);
    let attached = hud::bossbar_attach("example:wave");
    hud::bossbar_attach("example:none");
    hud::bossbar_player_add(bar, player);
    hud::bossbar_player_add(bar, player);
    let mut buf7 = [0u8; 512];
    hud::bossbar_players(bar, &mut buf7);
    let mut w8 = Writer::new();
    w8.begin_map().key("title").string("<red>Wave 2").key("color").string("red").key("progress").float(0.25).end();
    let spec9 = w8.into_vec().unwrap_or_default();
    hud::bossbar_update(bar, &spec9);
    let mut w10 = Writer::new();
    w10.begin_map().key("progress").string("half").end();
    let spec11 = w10.into_vec().unwrap_or_default();
    hud::bossbar_update(bar, &spec11);
    hud::bossbar_player_remove(bar, player);
    hud::bossbar_player_remove(bar, player);
    hud::bossbar_remove(bar);
    let mut w12 = Writer::new();
    w12.begin_map().key("title").string("<red>Late").end();
    let spec13 = w12.into_vec().unwrap_or_default();
    hud::bossbar_update(attached, &spec13);
    hud::bossbar_remove(bar);
    basalt_server::commands::reply(ctx, "bars done", &Args::new());
}

fn holograms(ctx: Ctx) {
    let source = basalt_server::commands::source_name(ctx).unwrap_or_default();
    let player = basalt_server::players::by_name(&source);
    let name = basalt_server::players::name(player).unwrap_or_default();
    let holo = hud::hologram_create("world", "<yellow>Hello", 0.5, 64.0, 0.5);
    hud::hologram_create("nowhere", "text", 0.0, 0.0, 0.0);
    let mut w1 = Writer::new();
    w1.begin_map().key("text").string("<yellow>Moved").key("x").float(2.5).key("y").float(65.0).key("z").float(-1.5).end();
    let spec2 = w1.into_vec().unwrap_or_default();
    hud::hologram_set(holo, &spec2);
    let mut w3 = Writer::new();
    w3.begin_map().key("x").float(1.0).end();
    let spec4 = w3.into_vec().unwrap_or_default();
    hud::hologram_set(holo, &spec4);
    let mut w5 = Writer::new();
    w5.begin_map().key("audience").begin_list().string(&name).end().end();
    let spec6 = w5.into_vec().unwrap_or_default();
    hud::hologram_set(holo, &spec6);
    let mut buf7 = [0u8; 512];
    hud::hologram_get(holo, &mut buf7);
    let mut w8 = Writer::new();
    w8.begin_map().key("audience").null().end();
    let spec9 = w8.into_vec().unwrap_or_default();
    hud::hologram_set(holo, &spec9);
    hud::hologram_remove(holo);
    hud::hologram_remove(holo);
    basalt_server::commands::reply(ctx, "holograms done", &Args::new());
}

fn npcs(ctx: Ctx) {
    let source = basalt_server::commands::source_name(ctx).unwrap_or_default();
    let player = basalt_server::players::by_name(&source);
    let mut w1 = Writer::new();
    w1.begin_map().key("world").string("world").key("name").string("<aqua>Guide").key("x").float(1.5).key("y").float(64.0).key("z").float(2.5).end();
    let spec2 = w1.into_vec().unwrap_or_default();
    let npc = hud::npc_create(&spec2, Some(clicked));
    let mut w3 = Writer::new();
    w3.begin_map().key("world").string("nowhere").key("name").string("Nobody").key("x").float(0.0).key("y").float(0.0).key("z").float(0.0).end();
    let spec4 = w3.into_vec().unwrap_or_default();
    hud::npc_create(&spec4, None);
    let mut w5 = Writer::new();
    w5.begin_map().key("name").string("<aqua>Guide II").key("x").float(3.5).key("y").float(65.0).key("z").float(4.5).key("yaw").float(90.0).key("pitch").float(-15.0).key("looks_at_players").boolean(true).key("health").float(18.0).key("held_slot").int(0).key("flags").int(0).key("pose").int(0).key("attributes").begin_map().key("max_health").float(30.0).end().key("equipment").begin_map().key("0").begin_map().key("item").string("minecraft:diamond_sword").key("count").int(1).end().end().end();
    let spec6 = w5.into_vec().unwrap_or_default();
    hud::npc_set(npc, &spec6);
    let mut w7 = Writer::new();
    w7.begin_map().key("attributes").begin_map().key("no_such_attribute").float(1.0).end().end();
    let spec8 = w7.into_vec().unwrap_or_default();
    hud::npc_set(npc, &spec8);
    let mut buf9 = [0u8; 768];
    hud::npc_get(npc, &mut buf9);
    let mut buf10 = [0u8; 768];
    hud::npc_item(npc, 0, &mut buf10);
    let mut buf11 = [0u8; 768];
    hud::npc_item(npc, 1, &mut buf11);
    let mut out12 = 0.0f64;
    hud::npc_attribute(npc, "max_health", &mut out12);
    let mut out13 = 0.0f64;
    hud::npc_attribute(npc, "no_such_attribute", &mut out13);
    hud::npc_animate(npc, 0);
    let entity = entity_of(npc);
    hud::tab_entry(player, entity, 0);
    hud::tab_entry(player, 424242, 0);
    G_NPC.store(npc, Ordering::Relaxed);
    basalt_server::commands::reply(ctx, "npcs done", &Args::new());
}

fn npc_end(ctx: Ctx) {
    hud::npc_remove(G_NPC.load(Ordering::Relaxed));
    hud::npc_animate(G_NPC.load(Ordering::Relaxed), 0);
    basalt_server::commands::reply(ctx, "npc end done", &Args::new());
}

fn tab(ctx: Ctx) {
    let source = basalt_server::commands::source_name(ctx).unwrap_or_default();
    let player = basalt_server::players::by_name(&source);
    let mut w1 = Writer::new();
    w1.begin_map().key("tab_header").string("<green>Top").key("tab_footer").string("<gray>Bottom").key("list_name").string("<gold>Steve").key("listed").boolean(true).key("list_order").int(3).key("score").int(5).key("waypoint_style").string("default").key("waypoint_color").int(16711680).end();
    let spec2 = w1.into_vec().unwrap_or_default();
    hud::player_set(player, &spec2);
    let mut buf3 = [0u8; 768];
    hud::player_get(player, &mut buf3);
    let mut w4 = Writer::new();
    w4.begin_map().key("not_a_property").int(1).end();
    let spec5 = w4.into_vec().unwrap_or_default();
    hud::player_set(player, &spec5);
    let mut w6 = Writer::new();
    w6.begin_map().key("listed").string("yes").end();
    let spec7 = w6.into_vec().unwrap_or_default();
    hud::player_set(player, &spec7);
    let mut w8 = Writer::new();
    w8.begin_map().key("tab_header").null().key("tab_footer").null().key("list_name").null().end();
    let spec9 = w8.into_vec().unwrap_or_default();
    hud::player_set(player, &spec9);
    basalt_server::commands::reply(ctx, "tab done", &Args::new());
}

fn surfaces(ctx: Ctx) {
    let mut w1 = Writer::new();
    w1.begin_map().key("title").string("<gold>Static").key("lines").begin_list().string("<white>one").end().end();
    let spec2 = w1.into_vec().unwrap_or_default();
    let claim = hud::surface_claim("sidebar", 5, &spec2, Some(render));
    let mut w3 = Writer::new();
    w3.begin_map().key("title").string("<gold>Static").key("lines").begin_list().string("<white>one").end().end();
    let spec4 = w3.into_vec().unwrap_or_default();
    hud::surface_claim("nowhere", 1, &spec4, None);
    let mut w5 = Writer::new();
    w5.begin_map().key("title").string("<gold>Changed").key("lines").begin_list().string("<white>two").end().end();
    let spec6 = w5.into_vec().unwrap_or_default();
    hud::surface_update(claim, &spec6);
    let mut w7 = Writer::new();
    w7.begin_map().key("title").string("<gold>Changed").key("lines").begin_list().string("<white>two").end().end();
    let spec8 = w7.into_vec().unwrap_or_default();
    hud::surface_answer(ctx, &spec8);
    G_CLAIM.store(claim, Ordering::Relaxed);
    basalt_server::commands::reply(ctx, "surfaces done", &Args::new());
}

fn surface_end(ctx: Ctx) {
    hud::surface_release(G_CLAIM.load(Ordering::Relaxed));
    hud::surface_release(G_CLAIM.load(Ordering::Relaxed));
    let mut w1 = Writer::new();
    w1.begin_map().key("title").string("<gold>Late").end();
    let spec2 = w1.into_vec().unwrap_or_default();
    hud::surface_update(G_CLAIM.load(Ordering::Relaxed), &spec2);
    basalt_server::commands::reply(ctx, "surface end done", &Args::new());
}

pub fn register() {
    basalt_server::commands::register("hud board", "", board);
    basalt_server::commands::register("hud bars", "", bars);
    basalt_server::commands::register("hud holograms", "", holograms);
    basalt_server::commands::register("hud npcs", "", npcs);
    basalt_server::commands::register("hud npc-end", "", npc_end);
    basalt_server::commands::register("hud tab", "", tab);
    basalt_server::commands::register("hud surfaces", "", surfaces);
    basalt_server::commands::register("hud surface-end", "", surface_end);
}
