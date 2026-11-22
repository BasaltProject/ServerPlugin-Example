using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkCommands = global::Basalt.Server.Commands.Commands;
using SdkEntities = global::Basalt.Server.Entities.Entities;
using SdkPlayers = global::Basalt.Server.Players.Players;

namespace Basalt.Server.Example.Entities;

public static class EntitiesExample
{
    static byte[] Cut(byte[] data, int length)
    {
        byte[] result = new byte[length];
        System.Array.Copy(data, result, length);
        return result;
    }

    static void Tour(int ctx)
    {
        int player = SdkPlayers.PlayerByName(SdkCommands.CommandSourceName(ctx) ?? "");
        int me = SdkEntities.OfPlayer(player);
        SdkEntities.Player(me);
        SdkEntities.Alive(me);
        SdkEntities.Id(me);
        double[] buf1 = new double[3];
        SdkEntities.Position(me, buf1);
        byte[] spec2 = new ValueWriter().BeginMap().Key("yaw").Float(90.0).Key("pitch").Float(10.0).End().Finish();
        SdkEntities.Teleport(me, 10.5, 64.0, -3.5, spec2);
        byte[] spec3 = new ValueWriter().BeginMap().Key("world").String("nowhere").End().Finish();
        SdkEntities.Teleport(me, 1.0, 2.0, 3.0, spec3);
        double[] buf4 = new double[2];
        SdkEntities.Rotation(me, buf4);
        SdkEntities.SetRotation(me, 45.0, 5.0);
        byte[] buf5 = new byte[256];
        SdkEntities.Get(me, "x", buf5);
        byte[] buf6 = new byte[256];
        SdkEntities.Get(me, "nope", buf6);
        byte[] buf7 = new byte[256];
        SdkEntities.Get(me, "uuid", buf7);
        byte[] buf8 = new byte[1024];
        SdkEntities.Properties(me, buf8);
        byte[] buf9 = new byte[1024];
        SdkEntities.Operations(me, buf9);
        byte[] buf10 = new byte[2048];
        SdkEntities.Types(buf10);
        byte[] buf11 = new byte[512];
        SdkEntities.TypeAttributes("minecraft:pig", buf11);
        byte[] buf12 = new byte[512];
        SdkEntities.TypeAttributes("minecraft:nothing", buf12);
        byte[] spec13 = new ValueWriter().BeginMap().Key("yaw").Float(90.0).End().Finish();
        int pig = SdkEntities.Spawn("world", "minecraft:pig", 1.0, 64.0, 2.0, spec13);
        SdkEntities.Spawn("world", "minecraft:nothing", 0.0, 0.0, 0.0, new byte[0]);
        int zombie = SdkEntities.Spawn("world", "minecraft:zombie", 3.0, 64.0, 2.0, new byte[0]);
        byte[] spec14 = new ValueWriter().BeginMap().Key("reason").String("veto").End().Finish();
        SdkEntities.Spawn("world", "minecraft:pig", 0.0, 0.0, 0.0, spec14);
        SdkEntities.Spawn("nowhere", "minecraft:pig", 0.0, 0.0, 0.0, new byte[0]);
        int pig_id = SdkEntities.Id(pig);
        SdkEntities.ById("world", pig_id);
        SdkEntities.ById("world", 9999);
        SdkEntities.ByUuid("00000000-0000-4000-8000-000000000001");
        SdkEntities.ByUuid("not-a-uuid");
        byte[] spec15 = new ValueWriter().BeginMap().Key("type").String("minecraft:pig").End().Finish();
        int[] buf16 = new int[8];
        SdkEntities.Query("world", spec15, buf16);
        byte[] spec17 = new ValueWriter().BeginMap().Key("x").Float(1.0).Key("y").Float(64.0).Key("z").Float(2.0).Key("radius").Float(5.0).Key("limit").Int(10).End().Finish();
        int[] buf18 = new int[8];
        SdkEntities.Query("world", spec17, buf18);
        SdkEntities.SetVelocity(pig, 0.0, 0.5, 0.0);
        double[] buf19 = new double[3];
        SdkEntities.Velocity(pig, buf19);
        double[] buf20 = new double[3];
        SdkEntities.Velocity(me, buf20);
        byte[] spec21 = new ValueWriter().Bool(true).Finish();
        SdkEntities.Set(pig, "glowing", spec21);
        byte[] buf22 = new byte[64];
        SdkEntities.Get(pig, "glowing", buf22);
        byte[] spec23 = new ValueWriter().Int(1).Finish();
        SdkEntities.Set(pig, "vehicle", spec23);
        byte[] spec24 = new ValueWriter().Bool(true).Finish();
        SdkEntities.Set(pig, "nope", spec24);
        byte[] spec25 = new ValueWriter().BeginList().Float(3.0).End().Finish();
        byte[] buf26 = new byte[64];
        SdkEntities.Call(pig, "damage", spec25, buf26);
        byte[] spec27 = new ValueWriter().BeginList().String("minecraft:speed").Int(200).Int(1).End().Finish();
        byte[] buf28 = new byte[64];
        SdkEntities.Call(pig, "add_effect", spec27, buf28);
        byte[] buf29 = new byte[512];
        SdkEntities.Call(pig, "effects", new byte[0], buf29);
        byte[] spec30 = new ValueWriter().BeginList().String("example:wander").Int(3).String("MOVE").End().Finish();
        byte[] buf31 = new byte[64];
        SdkEntities.Call(pig, "add_goal", spec30, buf31);
        byte[] spec32 = new ValueWriter().BeginList().String("example:wander").End().Finish();
        byte[] buf33 = new byte[64];
        SdkEntities.Call(pig, "has_goal", spec32, buf33);
        byte[] buf34 = new byte[512];
        SdkEntities.Call(pig, "goals", new byte[0], buf34);
        byte[] spec35 = new ValueWriter().BeginList().Float(5.0).Float(64.0).Float(5.0).End().Finish();
        byte[] buf36 = new byte[512];
        SdkEntities.Call(pig, "find_path", spec35, buf36);
        byte[] buf37 = new byte[64];
        SdkEntities.Call(pig, "bogus", new byte[0], buf37);
        byte[] snap = new byte[4096];
        int saved = SdkEntities.Snapshot(pig, snap);
        SdkEntities.Restore(pig, Cut(snap, saved));
        int clone = SdkEntities.SpawnNbt("world", Cut(snap, saved), "command");
        byte[] spec39 = new ValueWriter().BeginMap().Key("id").String("minecraft:diamond").Key("count").Int(2).End().Finish();
        int drop = SdkEntities.DropItem("world", spec39, 1.0, 65.0, 2.0, 0.0, 0.2, 0.0);
        byte[] spec40 = new ValueWriter().BeginMap().Key("count").Int(1).End().Finish();
        SdkEntities.DropItem("world", spec40, 1.0, 65.0, 2.0, 0.0, 0.0, 0.0);
        byte[] spec41 = new ValueWriter().BeginMap().Key("limits").BeginMap().Key("monster").Int(20).End().Key("allow_animals").Bool(false).End().Finish();
        byte[] buf42 = new byte[512];
        SdkEntities.SpawnRules("world", spec41, buf42);
        byte[] spec43 = new ValueWriter().BeginMap().Key("limits").BeginMap().Key("monster").Int(-1).End().Key("allow_animals").Bool(true).End().Finish();
        byte[] buf44 = new byte[512];
        SdkEntities.SpawnRules("world", spec43, buf44);
        byte[] spec45 = new ValueWriter().BeginMap().Key("distance").Float(10.0).End().Finish();
        byte[] buf46 = new byte[64];
        SdkEntities.Mechanic("combat.fall_damage", spec45, buf46);
        byte[] spec47 = new ValueWriter().BeginMap().Key("tick_count").Float(10.0).End().Finish();
        byte[] buf48 = new byte[64];
        SdkEntities.Mechanic("physics.projectile_margin", spec47, buf48);
        byte[] spec49 = new ValueWriter().BeginMap().End().Finish();
        byte[] buf50 = new byte[64];
        SdkEntities.Mechanic("nope", spec49, buf50);
        SdkEntities.Remove(pig);
        SdkEntities.Alive(pig);
        double[] buf51 = new double[3];
        SdkEntities.Position(pig, buf51);
        SdkEntities.Remove(pig);
        SdkEntities.RemoveUuid("world", "00000000-0000-4000-8000-0000000000ff");
        SdkEntities.RemoveUuid("world", "nope");
        SdkEntities.Remove(zombie);
        SdkEntities.Remove(clone);
        SdkEntities.Remove(drop);
        SdkEntities.Release(me);
        SdkEntities.Release(me);
        SdkCommands.CommandReply(ctx, "tour done");
    }

    static void Bad(int ctx)
    {
        SdkEntities.Alive(999);
        SdkEntities.Id(0);
        SdkEntities.Player(-5);
        SdkEntities.ByUuid("");
        SdkEntities.EventEntity(ctx);
        SdkEntities.AiAnswer(ctx, 1);
        double[] buf1 = new double[3];
        SdkEntities.Position(7, buf1);
        byte[] spec2 = new ValueWriter().BeginMap().End().Finish();
        byte[] buf3 = new byte[16];
        SdkEntities.Mechanic("", spec2, buf3);
        SdkCommands.CommandReply(ctx, "bad done");
    }

    public static void RegisterAll()
    {
        SdkCommands.Command("entities tour", "", Tour);
        SdkCommands.Command("entities bad", "", Bad);
    }
}
