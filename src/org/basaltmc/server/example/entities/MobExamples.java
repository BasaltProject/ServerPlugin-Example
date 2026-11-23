package org.basaltmc.server.example.entities;

import java.util.Arrays;
import org.basaltmc.server.commands.Registry;
import org.basaltmc.server.entities.EntityManager;
import org.basaltmc.server.values.Documents;

import static org.basaltmc.server.commands.Sender.name;
import static org.basaltmc.server.commands.Sender.reply;
import static org.basaltmc.server.players.Directory.playerByName;

public final class MobExamples {
    private MobExamples() {}

    private static void tour(int ctx) {
        int player = playerByName(name(ctx));
        int me = EntityManager.ofPlayer(player);
        EntityManager.player(me);
        EntityManager.alive(me);
        EntityManager.id(me);
        double[] buf1 = new double[3];
        EntityManager.position(me, buf1);
        byte[] spec2 = new Documents.Writer().beginMap().key("yaw").number(90.0).key("pitch").number(10.0).end().finish();
        EntityManager.teleport(me, 10.5, 64.0, -3.5, spec2);
        byte[] spec3 = new Documents.Writer().beginMap().key("world").string("nowhere").end().finish();
        EntityManager.teleport(me, 1.0, 2.0, 3.0, spec3);
        double[] buf4 = new double[2];
        EntityManager.rotation(me, buf4);
        EntityManager.setRotation(me, 45.0, 5.0);
        byte[] buf5 = new byte[256];
        EntityManager.get(me, "x", buf5);
        byte[] buf6 = new byte[256];
        EntityManager.get(me, "nope", buf6);
        byte[] buf7 = new byte[256];
        EntityManager.get(me, "uuid", buf7);
        byte[] buf8 = new byte[1024];
        EntityManager.properties(me, buf8);
        byte[] buf9 = new byte[1024];
        EntityManager.operations(me, buf9);
        byte[] buf10 = new byte[2048];
        EntityManager.types(buf10);
        byte[] buf11 = new byte[512];
        EntityManager.typeAttributes("minecraft:pig", buf11);
        byte[] buf12 = new byte[512];
        EntityManager.typeAttributes("minecraft:nothing", buf12);
        byte[] spec13 = new Documents.Writer().beginMap().key("yaw").number(90.0).end().finish();
        int pig = EntityManager.spawn("world", "minecraft:pig", 1.0, 64.0, 2.0, spec13);
        EntityManager.spawn("world", "minecraft:nothing", 0.0, 0.0, 0.0, new byte[0]);
        int zombie = EntityManager.spawn("world", "minecraft:zombie", 3.0, 64.0, 2.0, new byte[0]);
        byte[] spec14 = new Documents.Writer().beginMap().key("reason").string("veto").end().finish();
        EntityManager.spawn("world", "minecraft:pig", 0.0, 0.0, 0.0, spec14);
        EntityManager.spawn("nowhere", "minecraft:pig", 0.0, 0.0, 0.0, new byte[0]);
        int pig_id = EntityManager.id(pig);
        EntityManager.byId("world", pig_id);
        EntityManager.byId("world", 9999);
        EntityManager.byUuid("00000000-0000-4000-8000-000000000001");
        EntityManager.byUuid("not-a-uuid");
        byte[] spec15 = new Documents.Writer().beginMap().key("type").string("minecraft:pig").end().finish();
        int[] buf16 = new int[8];
        EntityManager.query("world", spec15, buf16);
        byte[] spec17 = new Documents.Writer().beginMap().key("x").number(1.0).key("y").number(64.0).key("z").number(2.0).key("radius").number(5.0).key("limit").integer(10L).end().finish();
        int[] buf18 = new int[8];
        EntityManager.query("world", spec17, buf18);
        EntityManager.setVelocity(pig, 0.0, 0.5, 0.0);
        double[] buf19 = new double[3];
        EntityManager.velocity(pig, buf19);
        double[] buf20 = new double[3];
        EntityManager.velocity(me, buf20);
        byte[] spec21 = new Documents.Writer().bool(true).finish();
        EntityManager.set(pig, "glowing", spec21);
        byte[] buf22 = new byte[64];
        EntityManager.get(pig, "glowing", buf22);
        byte[] spec23 = new Documents.Writer().integer(1L).finish();
        EntityManager.set(pig, "vehicle", spec23);
        byte[] spec24 = new Documents.Writer().bool(true).finish();
        EntityManager.set(pig, "nope", spec24);
        byte[] spec25 = new Documents.Writer().beginList().number(3.0).end().finish();
        byte[] buf26 = new byte[64];
        EntityManager.call(pig, "damage", spec25, buf26);
        byte[] spec27 = new Documents.Writer().beginList().string("minecraft:speed").integer(200L).integer(1L).end().finish();
        byte[] buf28 = new byte[64];
        EntityManager.call(pig, "add_effect", spec27, buf28);
        byte[] buf29 = new byte[512];
        EntityManager.call(pig, "effects", new byte[0], buf29);
        byte[] spec30 = new Documents.Writer().beginList().string("example:wander").integer(3L).string("move").end().finish();
        byte[] buf31 = new byte[64];
        EntityManager.call(pig, "add_goal", spec30, buf31);
        byte[] spec32 = new Documents.Writer().beginList().string("example:wander").end().finish();
        byte[] buf33 = new byte[64];
        EntityManager.call(pig, "has_goal", spec32, buf33);
        byte[] buf34 = new byte[512];
        EntityManager.call(pig, "goals", new byte[0], buf34);
        byte[] spec35 = new Documents.Writer().beginList().number(5.0).number(64.0).number(5.0).end().finish();
        byte[] buf36 = new byte[512];
        EntityManager.call(pig, "find_path", spec35, buf36);
        byte[] buf37 = new byte[64];
        EntityManager.call(pig, "bogus", new byte[0], buf37);
        byte[] snap = new byte[4096];
        int saved = EntityManager.snapshot(pig, snap);
        EntityManager.restore(pig, Arrays.copyOf(snap, saved));
        int clone = EntityManager.spawnNbt("world", Arrays.copyOf(snap, saved), "command");
        byte[] spec39 = new Documents.Writer().beginMap().key("id").string("minecraft:diamond").key("count").integer(2L).end().finish();
        int drop = EntityManager.dropItem("world", spec39, 1.0, 65.0, 2.0, 0.0, 0.2, 0.0);
        byte[] spec40 = new Documents.Writer().beginMap().key("count").integer(1L).end().finish();
        EntityManager.dropItem("world", spec40, 1.0, 65.0, 2.0, 0.0, 0.0, 0.0);
        byte[] spec41 = new Documents.Writer().beginMap().key("limits").beginMap().key("monster").integer(20L).end().key("allow_animals").bool(false).end().finish();
        byte[] buf42 = new byte[512];
        EntityManager.spawnRules("world", spec41, buf42);
        byte[] spec43 = new Documents.Writer().beginMap().key("limits").beginMap().key("monster").integer(-1L).end().key("allow_animals").bool(true).end().finish();
        byte[] buf44 = new byte[512];
        EntityManager.spawnRules("world", spec43, buf44);
        byte[] spec45 = new Documents.Writer().beginMap().key("distance").number(10.0).end().finish();
        byte[] buf46 = new byte[64];
        EntityManager.mechanic("combat.fall_damage", spec45, buf46);
        byte[] spec47 = new Documents.Writer().beginMap().key("tick_count").number(10.0).end().finish();
        byte[] buf48 = new byte[64];
        EntityManager.mechanic("physics.projectile_margin", spec47, buf48);
        byte[] spec49 = new Documents.Writer().beginMap().end().finish();
        byte[] buf50 = new byte[64];
        EntityManager.mechanic("nope", spec49, buf50);
        EntityManager.remove(pig);
        EntityManager.alive(pig);
        double[] buf51 = new double[3];
        EntityManager.position(pig, buf51);
        EntityManager.remove(pig);
        EntityManager.removeUuid("world", "00000000-0000-4000-8000-0000000000ff");
        EntityManager.removeUuid("world", "nope");
        EntityManager.remove(zombie);
        EntityManager.remove(clone);
        EntityManager.remove(drop);
        EntityManager.release(me);
        EntityManager.release(me);
        reply(ctx, "tour done");
    }

    private static void bad(int ctx) {
        EntityManager.alive(999);
        EntityManager.id(0);
        EntityManager.player(-5);
        EntityManager.byUuid("");
        EntityManager.eventEntity(ctx);
        EntityManager.aiAnswer(ctx, 1);
        double[] buf1 = new double[3];
        EntityManager.position(7, buf1);
        byte[] spec2 = new Documents.Writer().beginMap().end().finish();
        byte[] buf3 = new byte[16];
        EntityManager.mechanic("", spec2, buf3);
        reply(ctx, "bad done");
    }

    public static void register() {
        Registry.register("entities tour", "", MobExamples::tour);
        Registry.register("entities bad", "", MobExamples::bad);
    }
}
