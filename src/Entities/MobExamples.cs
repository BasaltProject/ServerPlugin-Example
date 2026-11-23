using Basalt.Server.Commands;
using Basalt.Server.Core;
using Basalt.Server.Entities;
using Basalt.Server.Items;
using Basalt.Server.Players;
using Basalt.Server.Values;
using System;

namespace Basalt.Server.Example.Entities;

public static class MobExamples
{
    static byte[] Cut(byte[] data, int length)
    {
        byte[] result = new byte[length];
        Array.Copy(data, result, length);
        return result;
    }

    static void Tour(int ctx)
    {
        int player = Directory.PlayerByName(Sender.Name(ctx) ?? "");
        int me = EntityManager.OfPlayer(player);
        EntityManager.Player(me);
        EntityManager.Alive(me);
        EntityManager.Id(me);
        double[] buf1 = new double[3];
        EntityManager.Position(me, buf1);
        byte[] spec2 = new ValueWriter().BeginMap().Key("yaw").Float(90.0).Key("pitch").Float(10.0).End().Finish();
        EntityManager.Teleport(me, 10.5, 64.0, -3.5, spec2);
        byte[] spec3 = new ValueWriter().BeginMap().Key("world").String("nowhere").End().Finish();
        EntityManager.Teleport(me, 1.0, 2.0, 3.0, spec3);
        double[] buf4 = new double[2];
        EntityManager.Rotation(me, buf4);
        EntityManager.SetRotation(me, 45.0, 5.0);
        byte[] buf5 = new byte[256];
        EntityManager.Get(me, "x", buf5);
        byte[] buf6 = new byte[256];
        EntityManager.Get(me, "nope", buf6);
        byte[] buf7 = new byte[256];
        EntityManager.Get(me, "uuid", buf7);
        byte[] buf8 = new byte[1024];
        EntityManager.Properties(me, buf8);
        byte[] buf9 = new byte[1024];
        EntityManager.Operations(me, buf9);
        byte[] buf10 = new byte[2048];
        EntityManager.Types(buf10);
        byte[] buf11 = new byte[512];
        EntityManager.TypeAttributes("minecraft:pig", buf11);
        byte[] buf12 = new byte[512];
        EntityManager.TypeAttributes("minecraft:nothing", buf12);
        byte[] spec13 = new ValueWriter().BeginMap().Key("yaw").Float(90.0).End().Finish();
        int pig = EntityManager.Spawn("world", "minecraft:pig", 1.0, 64.0, 2.0, spec13);
        EntityManager.Spawn("world", "minecraft:nothing", 0.0, 0.0, 0.0, new byte[0]);
        int zombie = EntityManager.Spawn("world", "minecraft:zombie", 3.0, 64.0, 2.0, new byte[0]);
        byte[] spec14 = new ValueWriter().BeginMap().Key("reason").String("veto").End().Finish();
        EntityManager.Spawn("world", "minecraft:pig", 0.0, 0.0, 0.0, spec14);
        EntityManager.Spawn("nowhere", "minecraft:pig", 0.0, 0.0, 0.0, new byte[0]);
        int pig_id = EntityManager.Id(pig);
        EntityManager.ById("world", pig_id);
        EntityManager.ById("world", 9999);
        EntityManager.ByUuid("00000000-0000-4000-8000-000000000001");
        EntityManager.ByUuid("not-a-uuid");
        byte[] spec15 = new ValueWriter().BeginMap().Key("type").String("minecraft:pig").End().Finish();
        int[] buf16 = new int[8];
        EntityManager.Query("world", spec15, buf16);
        byte[] spec17 = new ValueWriter().BeginMap().Key("x").Float(1.0).Key("y").Float(64.0).Key("z").Float(2.0).Key("radius").Float(5.0).Key("limit").Int(10).End().Finish();
        int[] buf18 = new int[8];
        EntityManager.Query("world", spec17, buf18);
        EntityManager.SetVelocity(pig, 0.0, 0.5, 0.0);
        double[] buf19 = new double[3];
        EntityManager.Velocity(pig, buf19);
        double[] buf20 = new double[3];
        EntityManager.Velocity(me, buf20);
        byte[] spec21 = new ValueWriter().Bool(true).Finish();
        EntityManager.Set(pig, "glowing", spec21);
        byte[] buf22 = new byte[64];
        EntityManager.Get(pig, "glowing", buf22);
        byte[] spec23 = new ValueWriter().Int(1).Finish();
        EntityManager.Set(pig, "vehicle", spec23);
        byte[] spec24 = new ValueWriter().Bool(true).Finish();
        EntityManager.Set(pig, "nope", spec24);
        byte[] spec25 = new ValueWriter().BeginList().Float(3.0).End().Finish();
        byte[] buf26 = new byte[64];
        EntityManager.Call(pig, "damage", spec25, buf26);
        byte[] spec27 = new ValueWriter().BeginList().String("minecraft:speed").Int(200).Int(1).End().Finish();
        byte[] buf28 = new byte[64];
        EntityManager.Call(pig, "add_effect", spec27, buf28);
        byte[] buf29 = new byte[512];
        EntityManager.Call(pig, "effects", new byte[0], buf29);
        byte[] spec30 = new ValueWriter().BeginList().String("example:wander").Int(3).String("move").End().Finish();
        byte[] buf31 = new byte[64];
        EntityManager.Call(pig, "add_goal", spec30, buf31);
        byte[] spec32 = new ValueWriter().BeginList().String("example:wander").End().Finish();
        byte[] buf33 = new byte[64];
        EntityManager.Call(pig, "has_goal", spec32, buf33);
        byte[] buf34 = new byte[512];
        EntityManager.Call(pig, "goals", new byte[0], buf34);
        byte[] spec35 = new ValueWriter().BeginList().Float(5.0).Float(64.0).Float(5.0).End().Finish();
        byte[] buf36 = new byte[512];
        EntityManager.Call(pig, "find_path", spec35, buf36);
        byte[] buf37 = new byte[64];
        EntityManager.Call(pig, "bogus", new byte[0], buf37);
        byte[] snap = new byte[4096];
        int saved = EntityManager.Snapshot(pig, snap);
        EntityManager.Restore(pig, Cut(snap, saved));
        int clone = EntityManager.SpawnNbt("world", Cut(snap, saved), "command");
        byte[] spec39 = new ValueWriter().BeginMap().Key("id").String("minecraft:diamond").Key("count").Int(2).End().Finish();
        int drop = EntityManager.DropItem("world", spec39, 1.0, 65.0, 2.0, 0.0, 0.2, 0.0);
        byte[] spec40 = new ValueWriter().BeginMap().Key("count").Int(1).End().Finish();
        EntityManager.DropItem("world", spec40, 1.0, 65.0, 2.0, 0.0, 0.0, 0.0);
        byte[] spec41 = new ValueWriter().BeginMap().Key("limits").BeginMap().Key("monster").Int(20).End().Key("allow_animals").Bool(false).End().Finish();
        byte[] buf42 = new byte[512];
        EntityManager.SpawnRules("world", spec41, buf42);
        byte[] spec43 = new ValueWriter().BeginMap().Key("limits").BeginMap().Key("monster").Int(-1).End().Key("allow_animals").Bool(true).End().Finish();
        byte[] buf44 = new byte[512];
        EntityManager.SpawnRules("world", spec43, buf44);
        byte[] spec45 = new ValueWriter().BeginMap().Key("distance").Float(10.0).End().Finish();
        byte[] buf46 = new byte[64];
        EntityManager.Mechanic("combat.fall_damage", spec45, buf46);
        byte[] spec47 = new ValueWriter().BeginMap().Key("tick_count").Float(10.0).End().Finish();
        byte[] buf48 = new byte[64];
        EntityManager.Mechanic("physics.projectile_margin", spec47, buf48);
        byte[] spec49 = new ValueWriter().BeginMap().End().Finish();
        byte[] buf50 = new byte[64];
        EntityManager.Mechanic("nope", spec49, buf50);
        EntityManager.Remove(pig);
        EntityManager.Alive(pig);
        double[] buf51 = new double[3];
        EntityManager.Position(pig, buf51);
        EntityManager.Remove(pig);
        EntityManager.RemoveUuid("world", "00000000-0000-4000-8000-0000000000ff");
        EntityManager.RemoveUuid("world", "nope");
        EntityManager.Remove(zombie);
        EntityManager.Remove(clone);
        EntityManager.Remove(drop);
        EntityManager.Release(me);
        EntityManager.Release(me);
        Sender.Reply(ctx, "tour done");
    }

    static void Bad(int ctx)
    {
        EntityManager.Alive(999);
        EntityManager.Id(0);
        EntityManager.Player(-5);
        EntityManager.ByUuid("");
        EntityManager.EventEntity(ctx);
        EntityManager.AiAnswer(ctx, 1);
        double[] buf1 = new double[3];
        EntityManager.Position(7, buf1);
        byte[] spec2 = new ValueWriter().BeginMap().End().Finish();
        byte[] buf3 = new byte[16];
        EntityManager.Mechanic("", spec2, buf3);
        Sender.Reply(ctx, "bad done");
    }

    public static void RegisterAll()
    {
        Registry.Register("entities tour", "", Tour);
        Registry.Register("entities bad", "", Bad);
    }
}
