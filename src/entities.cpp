#include "entities.hpp"

#include <basalt.hpp>

namespace entities {

namespace {

using Spec = basalt::ValueBuffer<640>;

void tour(basalt::Ctx ctx) {
    const auto source = basalt::command_source_name(ctx);
    const basalt::Player player = basalt::player_by_name(source.c_str());
    const int me = basalt::entities::of_player(player);
    basalt::entities::player(me);
    basalt::entities::alive(me);
    basalt::entities::id(me);
    double buf1[3];
    basalt::entities::position(me, buf1);
    Spec spec2;
    spec2.map().key("yaw").number(90.0).key("pitch").number(10.0).end();
    basalt::entities::teleport(me, 10.5, 64.0, -3.5, spec2.data(), spec2.finish());
    Spec spec3;
    spec3.map().key("world").string("nowhere").end();
    basalt::entities::teleport(me, 1.0, 2.0, 3.0, spec3.data(), spec3.finish());
    double buf4[2];
    basalt::entities::rotation(me, buf4);
    basalt::entities::set_rotation(me, 45.0, 5.0);
    char buf5[256];
    basalt::entities::get(me, "x", buf5, sizeof buf5);
    char buf6[256];
    basalt::entities::get(me, "nope", buf6, sizeof buf6);
    char buf7[256];
    basalt::entities::get(me, "uuid", buf7, sizeof buf7);
    char buf8[1024];
    basalt::entities::properties(me, buf8, sizeof buf8);
    char buf9[1024];
    basalt::entities::operations(me, buf9, sizeof buf9);
    char buf10[2048];
    basalt::entities::types(buf10, sizeof buf10);
    char buf11[512];
    basalt::entities::type_attributes("minecraft:pig", buf11, sizeof buf11);
    char buf12[512];
    basalt::entities::type_attributes("minecraft:nothing", buf12, sizeof buf12);
    Spec spec13;
    spec13.map().key("yaw").number(90.0).end();
    const int pig = basalt::entities::spawn("world", "minecraft:pig", 1.0, 64.0, 2.0, spec13.data(), spec13.finish());
    basalt::entities::spawn("world", "minecraft:nothing", 0.0, 0.0, 0.0, nullptr, 0);
    const int zombie = basalt::entities::spawn("world", "minecraft:zombie", 3.0, 64.0, 2.0, nullptr, 0);
    Spec spec14;
    spec14.map().key("reason").string("veto").end();
    basalt::entities::spawn("world", "minecraft:pig", 0.0, 0.0, 0.0, spec14.data(), spec14.finish());
    basalt::entities::spawn("nowhere", "minecraft:pig", 0.0, 0.0, 0.0, nullptr, 0);
    const int pig_id = basalt::entities::id(pig);
    basalt::entities::by_id("world", pig_id);
    basalt::entities::by_id("world", 9999);
    basalt::entities::by_uuid("00000000-0000-4000-8000-000000000001");
    basalt::entities::by_uuid("not-a-uuid");
    Spec spec15;
    spec15.map().key("type").string("minecraft:pig").end();
    int buf16[8];
    basalt::entities::query("world", spec15.data(), spec15.finish(), buf16, 8);
    Spec spec17;
    spec17.map().key("x").number(1.0).key("y").number(64.0).key("z").number(2.0).key("radius").number(5.0).key("limit").integer(10).end();
    int buf18[8];
    basalt::entities::query("world", spec17.data(), spec17.finish(), buf18, 8);
    basalt::entities::set_velocity(pig, 0.0, 0.5, 0.0);
    double buf19[3];
    basalt::entities::velocity(pig, buf19);
    double buf20[3];
    basalt::entities::velocity(me, buf20);
    Spec spec21;
    spec21.boolean(true);
    basalt::entities::set(pig, "glowing", spec21.data(), spec21.finish());
    char buf22[64];
    basalt::entities::get(pig, "glowing", buf22, sizeof buf22);
    Spec spec23;
    spec23.integer(1);
    basalt::entities::set(pig, "vehicle", spec23.data(), spec23.finish());
    Spec spec24;
    spec24.boolean(true);
    basalt::entities::set(pig, "nope", spec24.data(), spec24.finish());
    Spec spec25;
    spec25.list().number(3.0).end();
    char buf26[64];
    basalt::entities::call(pig, "damage", spec25.data(), spec25.finish(), buf26, sizeof buf26);
    Spec spec27;
    spec27.list().string("minecraft:speed").integer(200).integer(1).end();
    char buf28[64];
    basalt::entities::call(pig, "add_effect", spec27.data(), spec27.finish(), buf28, sizeof buf28);
    char buf29[512];
    basalt::entities::call(pig, "effects", nullptr, 0, buf29, sizeof buf29);
    Spec spec30;
    spec30.list().string("example:wander").integer(3).string("MOVE").end();
    char buf31[64];
    basalt::entities::call(pig, "add_goal", spec30.data(), spec30.finish(), buf31, sizeof buf31);
    Spec spec32;
    spec32.list().string("example:wander").end();
    char buf33[64];
    basalt::entities::call(pig, "has_goal", spec32.data(), spec32.finish(), buf33, sizeof buf33);
    char buf34[512];
    basalt::entities::call(pig, "goals", nullptr, 0, buf34, sizeof buf34);
    Spec spec35;
    spec35.list().number(5.0).number(64.0).number(5.0).end();
    char buf36[512];
    basalt::entities::call(pig, "find_path", spec35.data(), spec35.finish(), buf36, sizeof buf36);
    char buf37[64];
    basalt::entities::call(pig, "bogus", nullptr, 0, buf37, sizeof buf37);
    char snap[4096];
    const int saved = basalt::entities::snapshot(pig, snap, sizeof snap);
    basalt::entities::restore(pig, snap, saved);
    const int clone = basalt::entities::spawn_nbt("world", snap, saved, "command");
    Spec spec39;
    spec39.map().key("id").string("minecraft:diamond").key("count").integer(2).end();
    const int drop = basalt::entities::drop_item("world", spec39.data(), spec39.finish(), 1.0, 65.0, 2.0, 0.0, 0.2, 0.0);
    Spec spec40;
    spec40.map().key("count").integer(1).end();
    basalt::entities::drop_item("world", spec40.data(), spec40.finish(), 1.0, 65.0, 2.0, 0.0, 0.0, 0.0);
    Spec spec41;
    spec41.map().key("limits").map().key("monster").integer(20).end().key("allow_animals").boolean(false).end();
    char buf42[512];
    basalt::entities::spawn_rules("world", spec41.data(), spec41.finish(), buf42, sizeof buf42);
    Spec spec43;
    spec43.map().key("limits").map().key("monster").integer(-1).end().key("allow_animals").boolean(true).end();
    char buf44[512];
    basalt::entities::spawn_rules("world", spec43.data(), spec43.finish(), buf44, sizeof buf44);
    Spec spec45;
    spec45.map().key("distance").number(10.0).end();
    char buf46[64];
    basalt::entities::mechanic("combat.fall_damage", spec45.data(), spec45.finish(), buf46, sizeof buf46);
    Spec spec47;
    spec47.map().key("tick_count").number(10.0).end();
    char buf48[64];
    basalt::entities::mechanic("physics.projectile_margin", spec47.data(), spec47.finish(), buf48, sizeof buf48);
    Spec spec49;
    spec49.map().end();
    char buf50[64];
    basalt::entities::mechanic("nope", spec49.data(), spec49.finish(), buf50, sizeof buf50);
    basalt::entities::remove(pig);
    basalt::entities::alive(pig);
    double buf51[3];
    basalt::entities::position(pig, buf51);
    basalt::entities::remove(pig);
    basalt::entities::remove_uuid("world", "00000000-0000-4000-8000-0000000000ff");
    basalt::entities::remove_uuid("world", "nope");
    basalt::entities::remove(zombie);
    basalt::entities::remove(clone);
    basalt::entities::remove(drop);
    basalt::entities::release(me);
    basalt::entities::release(me);
    basalt::command_reply(ctx, "tour done");
}

void bad(basalt::Ctx ctx) {
    basalt::entities::alive(999);
    basalt::entities::id(0);
    basalt::entities::player(-5);
    basalt::entities::by_uuid("");
    basalt::entities::event_entity(ctx);
    basalt::entities::ai_answer(ctx, 1);
    double buf1[3];
    basalt::entities::position(7, buf1);
    Spec spec2;
    spec2.map().end();
    char buf3[16];
    basalt::entities::mechanic("", spec2.data(), spec2.finish(), buf3, sizeof buf3);
    basalt::command_reply(ctx, "bad done");
}

} // namespace

void register_all() {
    basalt::command("entities tour", "", tour);
    basalt::command("entities bad", "", bad);
}

} // namespace entities
