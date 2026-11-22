#include "hud.hpp"

#include <basalt.hpp>

namespace hud {

namespace {

using Spec = basalt::ValueBuffer<640>;

int g_npc = 0;
int g_claim = 0;

int entity_of(int npc) {
    char out[768];
    const int length = basalt::hud::npc_get(npc, out, sizeof out);
    basalt::Value value;
    if (length <= 0 || length > static_cast<int>(sizeof out) || !value.open(out, length)) {
        return 0;
    }
    return static_cast<int>(value.find("id").i64_or(0));
}

void clicked(basalt::Ctx) {
    basalt::log_info("npc clicked");
}

void render(basalt::Ctx ctx) {
    Spec spec1;
    spec1.map().key("title").string("<gold>Example").key("lines").list().string("<white>Hello %player%").string("<gray>second line").end().end();
    basalt::hud::surface_answer(ctx, spec1.data(), spec1.finish());
}

void board(basalt::Ctx ctx) {
    const auto source = basalt::command_source_name(ctx);
    const basalt::Player player = basalt::player_by_name(source.c_str());
    const int main_board = basalt::hud::scoreboard_main();
    const int own = basalt::hud::scoreboard_create();
    Spec spec1;
    spec1.map().key("criterion").string("dummy").key("title").string("<gold>Kills").key("hearts").boolean(false).end();
    basalt::hud::objective_add(own, "kills", spec1.data(), spec1.finish());
    Spec spec2;
    spec2.map().key("criterion").string("no_such_criterion").end();
    basalt::hud::objective_add(own, "broken", spec2.data(), spec2.finish());
    char buf3[256];
    basalt::hud::scoreboard_list(own, "objectives", buf3, sizeof buf3);
    basalt::hud::display_set(own, "sidebar", "kills");
    char buf4[256];
    basalt::hud::display_get(own, "sidebar", buf4, sizeof buf4);
    basalt::hud::display_set(own, "sidebar", "missing");
    basalt::hud::display_set(own, "nowhere", "kills");
    basalt::hud::score_set(own, "Steve", "kills", 7);
    basalt::hud::score_set(own, "Alex", "kills", 3);
    int out5 = 0;
    basalt::hud::score_get(own, "Steve", "kills", &out5);
    char buf6[256];
    basalt::hud::scoreboard_list(own, "entries", buf6, sizeof buf6);
    basalt::hud::score_reset(own, "Steve", "kills");
    int out7 = 0;
    basalt::hud::score_get(own, "Steve", "kills", &out7);
    basalt::hud::team_add(own, "red");
    basalt::hud::team_add(own, "red");
    Spec spec8;
    spec8.map().key("display_name").string("<red>Red").key("prefix").string("[R] ").key("suffix").string("!").key("color").string("red").key("friendly_fire").boolean(false).key("see_invisible").boolean(true).key("name_tags").string("hideForOtherTeams").key("death_messages").string("never").key("collision").string("pushOtherTeams").end();
    basalt::hud::team_set(own, "red", spec8.data(), spec8.finish());
    Spec spec9;
    spec9.map().key("color").string("blue").end();
    basalt::hud::team_set(own, "blue", spec9.data(), spec9.finish());
    basalt::hud::team_join(own, "red", "Steve");
    basalt::hud::team_join(own, "gone", "Steve");
    char buf10[256];
    basalt::hud::team_of(own, "Steve", buf10, sizeof buf10);
    char buf11[256];
    basalt::hud::scoreboard_list(own, "teams", buf11, sizeof buf11);
    char buf12[256];
    basalt::hud::scoreboard_list(own, "bogus", buf12, sizeof buf12);
    basalt::hud::team_leave(own, "red", "Steve");
    basalt::hud::team_leave(own, "red", "Steve");
    char buf13[256];
    basalt::hud::team_of(own, "Steve", buf13, sizeof buf13);
    basalt::hud::team_remove(own, "red");
    basalt::hud::team_remove(own, "red");
    basalt::hud::scoreboard_show(player, own);
    basalt::hud::scoreboard_shown(player);
    basalt::hud::scoreboard_show(player, main_board);
    basalt::hud::scoreboard_shown(player);
    basalt::hud::objective_remove(own, "kills");
    basalt::hud::objective_remove(own, "kills");
    basalt::hud::scoreboard_release(own);
    basalt::hud::scoreboard_release(own);
    basalt::hud::scoreboard_release(main_board);
    basalt::command_reply(ctx, "board done");
}

void bars(basalt::Ctx ctx) {
    const auto source = basalt::command_source_name(ctx);
    const basalt::Player player = basalt::player_by_name(source.c_str());
    Spec spec1;
    spec1.map().key("title").string("<green>Wave 1").key("color").string("green").key("style").string("notched_10").key("progress").number(0.5).key("darken_sky").boolean(false).key("boss_music").boolean(false).key("fog").boolean(false).key("visible").boolean(true).key("key").string("example:wave").end();
    const int bar = basalt::hud::bossbar_create(spec1.data(), spec1.finish());
    Spec spec2;
    spec2.map().key("title").string("<green>Wave 1").key("key").string("example:wave").end();
    basalt::hud::bossbar_create(spec2.data(), spec2.finish());
    char buf3[512];
    basalt::hud::bossbar_kept(buf3, sizeof buf3);
    char buf4[512];
    basalt::hud::bossbar_get(bar, buf4, sizeof buf4);
    const int attached = basalt::hud::bossbar_attach("example:wave");
    basalt::hud::bossbar_attach("example:none");
    basalt::hud::bossbar_player_add(bar, player);
    basalt::hud::bossbar_player_add(bar, player);
    char buf5[512];
    basalt::hud::bossbar_players(bar, buf5, sizeof buf5);
    Spec spec6;
    spec6.map().key("title").string("<red>Wave 2").key("color").string("red").key("progress").number(0.25).end();
    basalt::hud::bossbar_update(bar, spec6.data(), spec6.finish());
    Spec spec7;
    spec7.map().key("progress").string("half").end();
    basalt::hud::bossbar_update(bar, spec7.data(), spec7.finish());
    basalt::hud::bossbar_player_remove(bar, player);
    basalt::hud::bossbar_player_remove(bar, player);
    basalt::hud::bossbar_remove(bar);
    Spec spec8;
    spec8.map().key("title").string("<red>Late").end();
    basalt::hud::bossbar_update(attached, spec8.data(), spec8.finish());
    basalt::hud::bossbar_remove(bar);
    basalt::command_reply(ctx, "bars done");
}

void holograms(basalt::Ctx ctx) {
    const auto source = basalt::command_source_name(ctx);
    const basalt::Player player = basalt::player_by_name(source.c_str());
    const auto name = basalt::player_name(player);
    const int holo = basalt::hud::hologram_create("world", "<yellow>Hello", 0.5, 64.0, 0.5);
    basalt::hud::hologram_create("nowhere", "text", 0.0, 0.0, 0.0);
    Spec spec1;
    spec1.map().key("text").string("<yellow>Moved").key("x").number(2.5).key("y").number(65.0).key("z").number(-1.5).end();
    basalt::hud::hologram_set(holo, spec1.data(), spec1.finish());
    Spec spec2;
    spec2.map().key("x").number(1.0).end();
    basalt::hud::hologram_set(holo, spec2.data(), spec2.finish());
    Spec spec3;
    spec3.map().key("audience").list().string(name.c_str()).end().end();
    basalt::hud::hologram_set(holo, spec3.data(), spec3.finish());
    char buf4[512];
    basalt::hud::hologram_get(holo, buf4, sizeof buf4);
    Spec spec5;
    spec5.map().key("audience").null().end();
    basalt::hud::hologram_set(holo, spec5.data(), spec5.finish());
    basalt::hud::hologram_remove(holo);
    basalt::hud::hologram_remove(holo);
    basalt::command_reply(ctx, "holograms done");
}

void npcs(basalt::Ctx ctx) {
    const auto source = basalt::command_source_name(ctx);
    const basalt::Player player = basalt::player_by_name(source.c_str());
    Spec spec1;
    spec1.map().key("world").string("world").key("name").string("<aqua>Guide").key("x").number(1.5).key("y").number(64.0).key("z").number(2.5).end();
    const int npc = basalt::hud::npc_create(spec1.data(), spec1.finish(), clicked);
    Spec spec2;
    spec2.map().key("world").string("nowhere").key("name").string("Nobody").key("x").number(0.0).key("y").number(0.0).key("z").number(0.0).end();
    basalt::hud::npc_create(spec2.data(), spec2.finish(), nullptr);
    Spec spec3;
    spec3.map().key("name").string("<aqua>Guide II").key("x").number(3.5).key("y").number(65.0).key("z").number(4.5).key("yaw").number(90.0).key("pitch").number(-15.0).key("looks_at_players").boolean(true).key("health").number(18.0).key("held_slot").integer(0).key("flags").integer(0).key("pose").integer(0).key("attributes").map().key("max_health").number(30.0).end().key("equipment").map().key("0").map().key("item").string("minecraft:diamond_sword").key("count").integer(1).end().end().end();
    basalt::hud::npc_set(npc, spec3.data(), spec3.finish());
    Spec spec4;
    spec4.map().key("attributes").map().key("no_such_attribute").number(1.0).end().end();
    basalt::hud::npc_set(npc, spec4.data(), spec4.finish());
    char buf5[768];
    basalt::hud::npc_get(npc, buf5, sizeof buf5);
    char buf6[768];
    basalt::hud::npc_item(npc, 0, buf6, sizeof buf6);
    char buf7[768];
    basalt::hud::npc_item(npc, 1, buf7, sizeof buf7);
    double out8 = 0.0;
    basalt::hud::npc_attribute(npc, "max_health", &out8);
    double out9 = 0.0;
    basalt::hud::npc_attribute(npc, "no_such_attribute", &out9);
    basalt::hud::npc_animate(npc, 0);
    const int entity = entity_of(npc);
    basalt::hud::tab_entry(player, entity, 0);
    basalt::hud::tab_entry(player, 424242, 0);
    g_npc = npc;
    basalt::command_reply(ctx, "npcs done");
}

void npc_end(basalt::Ctx ctx) {
    basalt::hud::npc_remove(g_npc);
    basalt::hud::npc_animate(g_npc, 0);
    basalt::command_reply(ctx, "npc end done");
}

void tab(basalt::Ctx ctx) {
    const auto source = basalt::command_source_name(ctx);
    const basalt::Player player = basalt::player_by_name(source.c_str());
    Spec spec1;
    spec1.map().key("tab_header").string("<green>Top").key("tab_footer").string("<gray>Bottom").key("list_name").string("<gold>Steve").key("listed").boolean(true).key("list_order").integer(3).key("score").integer(5).key("waypoint_style").string("default").key("waypoint_color").integer(16711680).end();
    basalt::hud::player_set(player, spec1.data(), spec1.finish());
    char buf2[768];
    basalt::hud::player_get(player, buf2, sizeof buf2);
    Spec spec3;
    spec3.map().key("not_a_property").integer(1).end();
    basalt::hud::player_set(player, spec3.data(), spec3.finish());
    Spec spec4;
    spec4.map().key("listed").string("yes").end();
    basalt::hud::player_set(player, spec4.data(), spec4.finish());
    Spec spec5;
    spec5.map().key("tab_header").null().key("tab_footer").null().key("list_name").null().end();
    basalt::hud::player_set(player, spec5.data(), spec5.finish());
    basalt::command_reply(ctx, "tab done");
}

void surfaces(basalt::Ctx ctx) {
    Spec spec1;
    spec1.map().key("title").string("<gold>Static").key("lines").list().string("<white>one").end().end();
    const int claim = basalt::hud::surface_claim("sidebar", 5, spec1.data(), spec1.finish(), render);
    Spec spec2;
    spec2.map().key("title").string("<gold>Static").key("lines").list().string("<white>one").end().end();
    basalt::hud::surface_claim("nowhere", 1, spec2.data(), spec2.finish(), nullptr);
    Spec spec3;
    spec3.map().key("title").string("<gold>Changed").key("lines").list().string("<white>two").end().end();
    basalt::hud::surface_update(claim, spec3.data(), spec3.finish());
    Spec spec4;
    spec4.map().key("title").string("<gold>Changed").key("lines").list().string("<white>two").end().end();
    basalt::hud::surface_answer(ctx, spec4.data(), spec4.finish());
    g_claim = claim;
    basalt::command_reply(ctx, "surfaces done");
}

void surface_end(basalt::Ctx ctx) {
    basalt::hud::surface_release(g_claim);
    basalt::hud::surface_release(g_claim);
    Spec spec1;
    spec1.map().key("title").string("<gold>Late").end();
    basalt::hud::surface_update(g_claim, spec1.data(), spec1.finish());
    basalt::command_reply(ctx, "surface end done");
}

} // namespace

void register_all() {
    basalt::command("hud board", "", board);
    basalt::command("hud bars", "", bars);
    basalt::command("hud holograms", "", holograms);
    basalt::command("hud npcs", "", npcs);
    basalt::command("hud npc-end", "", npc_end);
    basalt::command("hud tab", "", tab);
    basalt::command("hud surfaces", "", surfaces);
    basalt::command("hud surface-end", "", surface_end);
}

} // namespace hud
