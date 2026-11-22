using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkCommands = global::Basalt.Server.Commands.Commands;
using SdkLogger = global::Basalt.Server.Log.Logger;
using SdkPlayers = global::Basalt.Server.Players.Players;
using SdkWorlds = global::Basalt.Server.Worlds.Worlds;

namespace Basalt.Server.Example.Worlds;

public static class WorldsTour
{
    static int Kl(int length, int capacity) => length > 0 && length <= capacity ? length : 0;

    static byte[] Cut(byte[] data, int length)
    {
        byte[] result = new byte[length];
        System.Array.Copy(data, result, length);
        return result;
    }

    static void Done(int ctx)
    {
        byte[] output = new byte[16];
        SdkWorlds.WorldValueResult(ctx, output);
    }

    static void Later2(int ctx)
    {
        int w2 = 0;
        {
            byte[] d20000 = new ValueWriter(512).BeginMap().Key("name").String("tour").End().Finish();
            SdkWorlds.WorldCreate(d20000);
        }
        {
            SdkWorlds.WorldUnload("nope", 0);
        }
        {
            SdkWorlds.WorldUnload("tour", 0);
        }
        {
            SdkWorlds.WorldDelete("tour");
        }
        if (w2 < 0)
        {
            SdkLogger.LogDebug("unreachable");
        }
    }

    static void Later(int ctx)
    {
        int w2 = 0;
        {
            byte[] d10000 = new ValueWriter(512).BeginMap().Key("name").String("tour").End().Finish();
            w2 = SdkWorlds.WorldCreate(d10000);
        }
        {
            SdkWorlds.WorldAt(w2, 0.5, 0.5, Later2);
        }
        if (w2 < 0)
        {
            SdkLogger.LogDebug("unreachable");
        }
    }

    static void Tour(int ctx)
    {
        SdkWorlds.ChunkCoord(-17);
        SdkWorlds.BlockDistance(0, 0, 0, 3, 4, 0);
        int[] pos = { 1, 2, 3 };
        SdkWorlds.PosOffset(pos, 1, 1, 1);
        int w = 0, p = SdkPlayers.PlayerByName("Steve"), w2 = 0, rq = 0, nz = 0;
        byte[] s1 = new byte[2048];
        int s1n = 0;
        {
            w = SdkWorlds.WorldByName("world");
        }
        {
            SdkWorlds.WorldByName("nope");
        }
        {
            SdkWorlds.WorldDefault();
        }
        {
            int[] o30 = new int[8];
            SdkWorlds.WorldList(o30);
        }
        {
            byte[] o41 = new byte[128];
            SdkWorlds.WorldName(w, o41);
        }
        {
            byte[] o51 = new byte[128];
            SdkWorlds.WorldName(999, o51);
        }
        {
            byte[] o62 = new byte[128];
            SdkWorlds.WorldGet(w, "time", o62);
        }
        {
            byte[] o72 = new byte[128];
            SdkWorlds.WorldGet(w, "nope", o72);
        }
        {
            byte[] d82 = new ValueWriter(512).Int(1000).Finish();
            SdkWorlds.WorldSet(w, "day_time", d82);
        }
        {
            byte[] d92 = new ValueWriter(512).Int(1).Finish();
            SdkWorlds.WorldSet(w, "nope", d92);
        }
        {
            byte[] o102 = new byte[128];
            SdkWorlds.WorldRuleGet(w, "doDaylightCycle", o102);
        }
        {
            byte[] o112 = new byte[128];
            SdkWorlds.WorldRuleGet(w, "nope", o112);
        }
        {
            SdkWorlds.WorldRuleSet(w, "doDaylightCycle", "false");
        }
        {
            rq = SdkWorlds.WorldAt(w, 1.5, 1.5, Done);
        }
        {
            SdkWorlds.WorldRequestCancel(rq);
        }
        {
            SdkWorlds.WorldAt(w, 2.5, 2.5, Done);
        }
        {
            SdkWorlds.WorldBlockSet(w, 0, 64, 0, "minecraft:stone", 0);
        }
        {
            SdkWorlds.WorldBlockSet(w, 0, 64, 0, "nope:block", 0);
        }
        {
            byte[] o185 = new byte[128];
            SdkWorlds.WorldBlockGet(w, 0, 64, 0, 0, o185);
        }
        {
            SdkWorlds.WorldStateGet(w, 0, 64, 0, 0);
        }
        {
            SdkWorlds.WorldStateSet(w, 1, 64, 0, 3, 0);
        }
        {
            SdkWorlds.WorldStateGet(999, 0, 64, 0, 0);
        }
        {
            int[] o222 = new int[2];
            SdkWorlds.WorldStatesRead(w, new int[] {0, 64, 0, 1, 64, 0}, o222, 0);
        }
        {
            SdkWorlds.WorldStatesWrite(w, new int[] {2, 64, 0, 3, 64, 0}, new int[] {5, 5}, 0);
        }
        {
            SdkWorlds.WorldFill(w, 2, 64, 2, 5, 65, 5, 5, 0);
        }
        {
            SdkWorlds.WorldFill(999, 0, 0, 0, 1, 1, 1, 1, 0);
        }
        {
            int[] o265 = new int[4096];
            SdkWorlds.WorldSectionRead(w, 0, 4, 0, 0, o265);
        }
        {
            SdkWorlds.WorldSectionWrite(w, 0, 4, 0, new int[4096], 0);
        }
        {
            SdkWorlds.WorldSectionWrite(w, 0, 200, 0, new int[4096], 0);
        }
        {
            int[] o294 = new int[8];
            SdkWorlds.WorldChunkRead(w, 0, 0, 0, o294);
        }
        {
            int[] o306 = new int[4096];
            SdkWorlds.WorldChunkApply(w, 0, 0, new int[] {4}, new int[4096], 0, o306);
        }
        {
            SdkWorlds.WorldChangesApply(w, new int[] {0, 64, 0}, new int[] {5}, 0);
        }
        {
            SdkWorlds.WorldBlocksSettle(w, new int[] {0, 64, 0}, new int[] {0}, 0);
        }
        {
            SdkWorlds.WorldBlocksNotify(w, new int[] {0, 64, 0}, new int[] {1, 64, 0});
        }
        {
            SdkWorlds.WorldBlocksReshape(w, new int[] {0, 64, 0}, 0);
        }
        {
            SdkWorlds.WorldStateValid(w, 0, 64, 0, 1);
        }
        {
            SdkWorlds.WorldTickSchedule(w, 0, 64, 0, 5, 0);
        }
        {
            SdkWorlds.WorldTickPending(w, 0, 64, 0);
        }
        {
            SdkWorlds.WorldStateSet(w, 3, 64, 0, 6, 0);
        }
        {
            SdkWorlds.WorldBlockEntityEdit(w, 1, 3, 64, 0, new byte[] {10, 0, 0, 0});
        }
        {
            byte[] o404 = new byte[256];
            SdkWorlds.WorldBlockEntityGet(w, 3, 64, 0, o404);
        }
        {
            byte[] o414 = new byte[256];
            SdkWorlds.WorldBlockEntityGet(w, 9, 64, 9, o414);
        }
        {
            int[] o423 = new int[8];
            SdkWorlds.WorldBlockEntities(w, 0, 0, o423);
        }
        {
            SdkWorlds.WorldDataSet(w, 0, 0, 0, 0, new byte[] {10, 0, 0, 0});
        }
        {
            byte[] o445 = new byte[256];
            SdkWorlds.WorldDataGet(w, 0, 0, 0, 0, o445);
        }
        {
            byte[] o455 = new byte[256];
            SdkWorlds.WorldDataGet(w, 1, 9, 0, 9, o455);
        }
        {
            SdkWorlds.WorldChunkStatus(w, 0, 0);
        }
        {
            SdkWorlds.WorldChunkStatus(w, 9, 9);
        }
        {
            SdkWorlds.WorldChunkLoad(w, 3, 3, 8);
        }
        {
            SdkWorlds.WorldChunkUnload(w, 3, 3);
        }
        {
            SdkWorlds.WorldChunkRequest(w, 4, 4, 8, 0, Done);
        }
        {
            int[] o511 = new int[16];
            SdkWorlds.WorldChunksLoaded(w, o511);
        }
        {
            SdkWorlds.WorldChunkHold(w, "tour", new int[] {4, 4});
        }
        {
            int[] o532 = new int[8];
            SdkWorlds.WorldChunkHeld(w, "tour", o532);
        }
        {
            SdkWorlds.WorldChunkRelease(w, "tour", new int[] {4, 4});
        }
        {
            SdkWorlds.WorldChunksResend(w, new int[] {0, 0});
        }
        {
            SdkWorlds.WorldStoredPut(w, 7, 7, new byte[] {10, 0, 0, 0});
        }
        {
            byte[] o573 = new byte[256];
            SdkWorlds.WorldStoredGet(w, 7, 7, o573);
        }
        {
            byte[] o583 = new byte[256];
            SdkWorlds.WorldStoredGet(w, 8, 8, o583);
        }
        {
            int[] o593 = new int[8];
            SdkWorlds.WorldStoredList(w, 0, 0, o593);
        }
        {
            SdkWorlds.WorldLightAt(w, 0, 64, 0, 0);
        }
        {
            byte[] o616 = new byte[2048];
            SdkWorlds.WorldLightRead(w, 0, 4, 0, 1, 1, o616);
        }
        {
            SdkWorlds.WorldLightWrite(w, 0, 4, 0, 1, new byte[2048]);
        }
        {
            SdkWorlds.WorldLightWrite(w, 0, 4, 0, 1, new byte[] {10, 0, 0, 0});
        }
        {
            SdkWorlds.WorldLightCheck(w, new int[] {0, 0}, Done);
        }
        {
            byte[] o654 = new byte[128];
            SdkWorlds.WorldBiomeGet(w, 0, 64, 0, o654);
        }
        {
            SdkWorlds.WorldBiomeSet(w, 0, 64, 0, "minecraft:desert");
        }
        {
            SdkWorlds.WorldBiomeSet(w, 0, 64, 0, "nope:biome");
        }
        {
            int[] o683 = new int[8];
            SdkWorlds.WorldBiomesRead(w, 0, 0, o683);
        }
        {
            SdkWorlds.WorldBiomesWrite(w, 0, 0, new int[] {1, 1, 1, 1});
        }
        {
            SdkWorlds.WorldSound(w, "minecraft:block.note_block.bell", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0L);
        }
        {
            SdkWorlds.WorldSound(w, "", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0L);
        }
        {
            byte[] d721 = new ValueWriter(512).BeginMap().Key("type").String("minecraft:flame").Key("x").Float(0.5).Key("y").Float(65.0).Key("z").Float(0.5).Key("count").Int(3).End().Finish();
            SdkWorlds.WorldParticle(w, d721);
        }
        {
            byte[] d731 = new ValueWriter(512).BeginMap().Key("count").Int(1).End().Finish();
            SdkWorlds.WorldParticle(w, d731);
        }
        {
            SdkWorlds.WorldLevelEvent(w, 1000, 0, 64, 0, 0, 0);
        }
        {
            SdkWorlds.WorldBlockEvent(w, 0, 64, 0, 1, 2);
        }
        {
            SdkWorlds.WorldExplode(w, 0.5, 64.0, 0.5, 0.0f, 0, 0);
        }
        {
            SdkWorlds.WorldLightning(w, 0.5, 64.0, 0.5);
        }
        {
            SdkWorlds.WorldGameEvent(w, "minecraft:block_change", 0.5, 64.0, 0.5, "");
        }
        {
            SdkWorlds.WorldGameEvent(w, "", 0.5, 64.0, 0.5, "");
        }
        {
            byte[] d802 = new ValueWriter(512).BeginList().String("minecraft:block.note_block.bell").String("block").Float(0.5).Float(64.0).Float(0.5).Float(1.0).Float(1.0).Int(0).End().Finish();
            byte[] o803 = new byte[64];
            SdkWorlds.WorldPlayerEffect(p, "sound_at", d802, o803);
        }
        {
            byte[] d812 = new ValueWriter(512).BeginList().End().Finish();
            byte[] o813 = new byte[64];
            SdkWorlds.WorldPlayerEffect(p, "nope", d812, o813);
        }
        {
            byte[] o821 = new byte[512];
            SdkWorlds.WorldBorderGet(w, o821);
        }
        {
            byte[] d831 = new ValueWriter(512).BeginMap().Key("size").Float(100.0).End().Finish();
            SdkWorlds.WorldBorderSet(w, d831);
        }
        {
            byte[] d841 = new ValueWriter(512).BeginMap().Key("size").Float(-1.0).Key("seconds").Float(-1.0).End().Finish();
            SdkWorlds.WorldBorderSet(w, d841);
        }
        {
            byte[] d852 = new ValueWriter(512).BeginMap().End().Finish();
            byte[] o853 = new byte[1024];
            SdkWorlds.WorldCall(w, "ops", d852, o853);
        }
        {
            byte[] d862 = new ValueWriter(512).BeginMap().Key("x").Int(2).Key("z").Int(2).End().Finish();
            byte[] o863 = new byte[256];
            SdkWorlds.WorldCall(w, "highest", d862, o863);
        }
        {
            byte[] d872 = new ValueWriter(512).BeginMap().End().Finish();
            byte[] o873 = new byte[256];
            SdkWorlds.WorldCall(w, "nope", d872, o873);
        }
        {
            s1n = SdkWorlds.StructureCapture(w, 0, 64, 0, 3, 2, 3, 0, s1);
        }
        {
            SdkWorlds.StructurePut("tour:a", Cut(s1, Kl(s1n, 2048)), 0);
        }
        {
            SdkWorlds.StructurePut("tour:b", new byte[] {10, 0, 0, 0}, 0);
        }
        {
            byte[] o910 = new byte[256];
            SdkWorlds.StructureList(o910);
        }
        {
            byte[] o921 = new byte[256];
            SdkWorlds.StructureGet("tour:a", o921);
        }
        {
            byte[] o931 = new byte[256];
            SdkWorlds.StructureGet("tour:zzz", o931);
        }
        {
            byte[] o941 = new byte[128];
            SdkWorlds.StructureFile("tour:a", o941);
        }
        {
            byte[] d951 = new ValueWriter(512).BeginMap().Key("x").Int(0).Key("y").Int(64).Key("z").Int(0).End().Finish();
            byte[] o952 = new byte[1024];
            SdkWorlds.StructureScan(Cut(s1, Kl(s1n, 2048)), d951, o952);
        }
        {
            byte[] d961 = new ValueWriter(512).BeginMap().Key("world").String("world").Key("x").Int(8).Key("y").Int(64).Key("z").Int(8).End().Finish();
            SdkWorlds.StructurePlace(Cut(s1, Kl(s1n, 2048)), d961);
        }
        {
            SdkWorlds.StructureRemove("tour:a", 0);
        }
        {
            SdkWorlds.StructureRemove("tour:a", 0);
        }
        {
            byte[] d990 = new ValueWriter(512).BeginMap().Key("op").String("shape").Key("settings").String("minecraft:overworld").Key("seed").Int(0).End().Finish();
            byte[] o991 = new byte[256];
            SdkWorlds.NoiseQuery(d990, o991);
        }
        {
            byte[] d1000 = new ValueWriter(512).BeginMap().Key("op").String("nope").End().Finish();
            byte[] o1001 = new byte[256];
            SdkWorlds.NoiseQuery(d1000, o1001);
        }
        {
            byte[] d1010 = new ValueWriter(512).BeginMap().Key("id").String("minecraft:temperature").Key("seed").Int(0).End().Finish();
            nz = SdkWorlds.NoiseCreate(d1010);
        }
        {
            double[] f1024 = new double[1];
            SdkWorlds.NoiseSample(nz, 0.0, 0.0, 0.0, f1024);
        }
        {
            byte[] o1031 = new byte[128];
            SdkWorlds.NoiseInfo(nz, o1031);
        }
        {
            SdkWorlds.NoiseRelease(nz);
        }
        {
            SdkWorlds.NoiseRelease(nz);
        }
        {
            SdkWorlds.BlockStateId("minecraft:stone");
        }
        {
            SdkWorlds.BlockStateId("nope:block");
        }
        {
            byte[] o1081 = new byte[128];
            SdkWorlds.BlockStateText(1, o1081);
        }
        {
            byte[] d1091 = new ValueWriter(512).BeginMap().End().Finish();
            byte[] o1092 = new byte[1024];
            SdkWorlds.WorldsData("tables", d1091, o1092);
        }
        {
            byte[] d1101 = new ValueWriter(512).BeginMap().End().Finish();
            byte[] o1102 = new byte[512];
            SdkWorlds.WorldsData("blocks", d1101, o1102);
        }
        {
            byte[] d1111 = new ValueWriter(512).BeginMap().End().Finish();
            byte[] o1112 = new byte[64];
            SdkWorlds.WorldsData("nope", d1111, o1112);
        }
        {
            byte[] d1121 = new ValueWriter(512).BeginMap().End().Finish();
            byte[] o1122 = new byte[128];
            SdkWorlds.DatafixCall("types", d1121, o1122);
        }
        {
            byte[] d1131 = new ValueWriter(512).BeginMap().Key("type").String("chunk").End().Finish();
            byte[] o1132 = new byte[64];
            SdkWorlds.DatafixCall("supports", d1131, o1132);
        }
        {
            byte[] d1141 = new ValueWriter(512).BeginMap().Key("type").String("chunk").End().Finish();
            byte[] o1142 = new byte[256];
            SdkWorlds.DatafixCall("steps", d1141, o1142);
        }
        {
            byte[] d1151 = new ValueWriter(512).BeginMap().Key("type").String("chunk").Key("from").Int(100).Key("to").Int(200).End().Finish();
            byte[] o1152 = new byte[64];
            SdkWorlds.DatafixCall("missing", d1151, o1152);
        }
        {
            byte[] d1161 = new ValueWriter(512).BeginMap().Key("id").Int(16).End().Finish();
            byte[] o1162 = new byte[128];
            SdkWorlds.DatafixCall("legacy_state", d1161, o1162);
        }
        {
            byte[] d1171 = new ValueWriter(512).BeginMap().Key("id").Int(16).End().Finish();
            byte[] o1172 = new byte[128];
            SdkWorlds.DatafixCall("modern_state", d1171, o1172);
        }
        {
            byte[] d1181 = new ValueWriter(512).BeginMap().Key("type").String("chunk").Key("nbt").Bytes(new byte[] {10, 0, 0, 0}).Key("from").Int(100).Key("to").Int(200).End().Finish();
            byte[] o1182 = new byte[128];
            SdkWorlds.DatafixCall("upgrade", d1181, o1182);
        }
        {
            byte[] d1191 = new ValueWriter(512).BeginMap().End().Finish();
            byte[] o1192 = new byte[64];
            SdkWorlds.DatafixCall("nope", d1191, o1192);
        }
        {
            SdkWorlds.WorldAt(w, 3.5, 3.5, Later);
        }
        if (w + p + w2 + rq + nz < 0)
        {
            SdkLogger.LogDebug("unreachable");
        }
    }

    public static void Register() => SdkCommands.Command("worlds", "", Tour);
}
