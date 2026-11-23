package org.basaltmc.server.example.worlds;

import org.basaltmc.server.commands.Registry;
import org.basaltmc.server.log.Logger;
import org.basaltmc.server.players.Directory;
import org.basaltmc.server.values.Documents.Writer;
import org.basaltmc.server.worlds.WorldManager;

public final class WorldTour {
    private WorldTour() {}

    private static int kl(int length, int capacity) {
        return length > 0 && length <= capacity ? length : 0;
    }

    private static byte[] cut(byte[] data, int length) {
        byte[] out = new byte[length];
        System.arraycopy(data, 0, out, 0, length);
        return out;
    }

    private static void done(int ctx) {
        byte[] out = new byte[16];
        WorldManager.worldValueResult(ctx, out);
    }

    private static void later2(int ctx) {
        int w2 = 0;
        {
            byte[] d20000 = new Writer(512).beginMap().key("name").string("tour").end().finish();
            WorldManager.worldCreate(d20000);
        }
        {
            WorldManager.worldUnload("nope", 0);
        }
        {
            WorldManager.worldUnload("tour", 0);
        }
        {
            WorldManager.worldDelete("tour");
        }
        if (w2 < 0) {
            Logger.logDebug("unreachable");
        }
    }

    private static void later(int ctx) {
        int w2 = 0;
        {
            byte[] d10000 = new Writer(512).beginMap().key("name").string("tour").end().finish();
            w2 = WorldManager.worldCreate(d10000);
        }
        {
            WorldManager.worldAt(w2, 0.5, 0.5, WorldTour::later2);
        }
        if (w2 < 0) {
            Logger.logDebug("unreachable");
        }
    }

    private static void tour(int ctx) {
        WorldManager.chunkCoord(-17);
        WorldManager.blockDistance(0, 0, 0, 3, 4, 0);
        int[] pos = {1, 2, 3};
        WorldManager.posOffset(pos, 1, 1, 1);
        int w = 0, p = Directory.playerByName("Steve"), w2 = 0, rq = 0, nz = 0;
        byte[] s1 = new byte[2048];
        int s1n = 0;
        {
            w = WorldManager.worldByName("world");
        }
        {
            WorldManager.worldByName("nope");
        }
        {
            WorldManager.worldDefault();
        }
        {
            int[] o30 = new int[8];
            WorldManager.worldList(o30);
        }
        {
            byte[] o41 = new byte[128];
            WorldManager.worldName(w, o41);
        }
        {
            byte[] o51 = new byte[128];
            WorldManager.worldName(999, o51);
        }
        {
            byte[] o62 = new byte[128];
            WorldManager.worldGet(w, "time", o62);
        }
        {
            byte[] o72 = new byte[128];
            WorldManager.worldGet(w, "nope", o72);
        }
        {
            byte[] d82 = new Writer(512).integer(1000).finish();
            WorldManager.worldSet(w, "day_time", d82);
        }
        {
            byte[] d92 = new Writer(512).integer(1).finish();
            WorldManager.worldSet(w, "nope", d92);
        }
        {
            byte[] o102 = new byte[128];
            WorldManager.worldRuleGet(w, "doDaylightCycle", o102);
        }
        {
            byte[] o112 = new byte[128];
            WorldManager.worldRuleGet(w, "nope", o112);
        }
        {
            WorldManager.worldRuleSet(w, "doDaylightCycle", "false");
        }
        {
            rq = WorldManager.worldAt(w, 1.5, 1.5, WorldTour::done);
        }
        {
            WorldManager.worldRequestCancel(rq);
        }
        {
            WorldManager.worldAt(w, 2.5, 2.5, WorldTour::done);
        }
        {
            WorldManager.worldBlockSet(w, 0, 64, 0, "minecraft:stone", 0);
        }
        {
            WorldManager.worldBlockSet(w, 0, 64, 0, "nope:block", 0);
        }
        {
            byte[] o185 = new byte[128];
            WorldManager.worldBlockGet(w, 0, 64, 0, 0, o185);
        }
        {
            WorldManager.worldStateGet(w, 0, 64, 0, 0);
        }
        {
            WorldManager.worldStateSet(w, 1, 64, 0, 3, 0);
        }
        {
            WorldManager.worldStateGet(999, 0, 64, 0, 0);
        }
        {
            int[] o222 = new int[2];
            WorldManager.worldStatesRead(w, new int[] {0, 64, 0, 1, 64, 0}, o222, 0);
        }
        {
            WorldManager.worldStatesWrite(w, new int[] {2, 64, 0, 3, 64, 0}, new int[] {5, 5}, 0);
        }
        {
            WorldManager.worldFill(w, 2, 64, 2, 5, 65, 5, 5, 0);
        }
        {
            WorldManager.worldFill(999, 0, 0, 0, 1, 1, 1, 1, 0);
        }
        {
            int[] o265 = new int[4096];
            WorldManager.worldSectionRead(w, 0, 4, 0, 0, o265);
        }
        {
            WorldManager.worldSectionWrite(w, 0, 4, 0, new int[4096], 0);
        }
        {
            WorldManager.worldSectionWrite(w, 0, 200, 0, new int[4096], 0);
        }
        {
            int[] o294 = new int[8];
            WorldManager.worldChunkRead(w, 0, 0, 0, o294);
        }
        {
            int[] o306 = new int[4096];
            WorldManager.worldChunkApply(w, 0, 0, new int[] {4}, new int[4096], 0, o306);
        }
        {
            WorldManager.worldChangesApply(w, new int[] {0, 64, 0}, new int[] {5}, 0);
        }
        {
            WorldManager.worldBlocksSettle(w, new int[] {0, 64, 0}, new int[] {0}, 0);
        }
        {
            WorldManager.worldBlocksNotify(w, new int[] {0, 64, 0}, new int[] {1, 64, 0});
        }
        {
            WorldManager.worldBlocksReshape(w, new int[] {0, 64, 0}, 0);
        }
        {
            WorldManager.worldStateValid(w, 0, 64, 0, 1);
        }
        {
            WorldManager.worldTickSchedule(w, 0, 64, 0, 5, 0);
        }
        {
            WorldManager.worldTickPending(w, 0, 64, 0);
        }
        {
            WorldManager.worldStateSet(w, 3, 64, 0, 6, 0);
        }
        {
            WorldManager.worldBlockEntityEdit(w, 1, 3, 64, 0, new byte[] {10, 0, 0, 0});
        }
        {
            byte[] o404 = new byte[256];
            WorldManager.worldBlockEntityGet(w, 3, 64, 0, o404);
        }
        {
            byte[] o414 = new byte[256];
            WorldManager.worldBlockEntityGet(w, 9, 64, 9, o414);
        }
        {
            int[] o423 = new int[8];
            WorldManager.worldBlockEntities(w, 0, 0, o423);
        }
        {
            WorldManager.worldDataSet(w, 0, 0, 0, 0, new byte[] {10, 0, 0, 0});
        }
        {
            byte[] o445 = new byte[256];
            WorldManager.worldDataGet(w, 0, 0, 0, 0, o445);
        }
        {
            byte[] o455 = new byte[256];
            WorldManager.worldDataGet(w, 1, 9, 0, 9, o455);
        }
        {
            WorldManager.worldChunkStatus(w, 0, 0);
        }
        {
            WorldManager.worldChunkStatus(w, 9, 9);
        }
        {
            WorldManager.worldChunkLoad(w, 3, 3, 8);
        }
        {
            WorldManager.worldChunkUnload(w, 3, 3);
        }
        {
            WorldManager.worldChunkRequest(w, 4, 4, 8, 0, WorldTour::done);
        }
        {
            int[] o511 = new int[16];
            WorldManager.worldChunksLoaded(w, o511);
        }
        {
            WorldManager.worldChunkHold(w, "tour", new int[] {4, 4});
        }
        {
            int[] o532 = new int[8];
            WorldManager.worldChunkHeld(w, "tour", o532);
        }
        {
            WorldManager.worldChunkRelease(w, "tour", new int[] {4, 4});
        }
        {
            WorldManager.worldChunksResend(w, new int[] {0, 0});
        }
        {
            WorldManager.worldStoredPut(w, 7, 7, new byte[] {10, 0, 0, 0});
        }
        {
            byte[] o573 = new byte[256];
            WorldManager.worldStoredGet(w, 7, 7, o573);
        }
        {
            byte[] o583 = new byte[256];
            WorldManager.worldStoredGet(w, 8, 8, o583);
        }
        {
            int[] o593 = new int[8];
            WorldManager.worldStoredList(w, 0, 0, o593);
        }
        {
            WorldManager.worldLightAt(w, 0, 64, 0, 0);
        }
        {
            byte[] o616 = new byte[2048];
            WorldManager.worldLightRead(w, 0, 4, 0, 1, 1, o616);
        }
        {
            WorldManager.worldLightWrite(w, 0, 4, 0, 1, new byte[2048]);
        }
        {
            WorldManager.worldLightWrite(w, 0, 4, 0, 1, new byte[] {10, 0, 0, 0});
        }
        {
            WorldManager.worldLightCheck(w, new int[] {0, 0}, WorldTour::done);
        }
        {
            byte[] o654 = new byte[128];
            WorldManager.worldBiomeGet(w, 0, 64, 0, o654);
        }
        {
            WorldManager.worldBiomeSet(w, 0, 64, 0, "minecraft:desert");
        }
        {
            WorldManager.worldBiomeSet(w, 0, 64, 0, "nope:biome");
        }
        {
            int[] o683 = new int[8];
            WorldManager.worldBiomesRead(w, 0, 0, o683);
        }
        {
            WorldManager.worldBiomesWrite(w, 0, 0, new int[] {1, 1, 1, 1});
        }
        {
            WorldManager.worldSound(w, "minecraft:block.note_block.bell", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0L);
        }
        {
            WorldManager.worldSound(w, "", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0L);
        }
        {
            byte[] d721 = new Writer(512).beginMap().key("type").string("minecraft:flame").key("x").number(0.5).key("y").number(65.0).key("z").number(0.5).key("count").integer(3).end().finish();
            WorldManager.worldParticle(w, d721);
        }
        {
            byte[] d731 = new Writer(512).beginMap().key("count").integer(1).end().finish();
            WorldManager.worldParticle(w, d731);
        }
        {
            WorldManager.worldLevelEvent(w, 1000, 0, 64, 0, 0, 0);
        }
        {
            WorldManager.worldBlockEvent(w, 0, 64, 0, 1, 2);
        }
        {
            WorldManager.worldExplode(w, 0.5, 64.0, 0.5, 0.0f, 0, 0);
        }
        {
            WorldManager.worldLightning(w, 0.5, 64.0, 0.5);
        }
        {
            WorldManager.worldGameEvent(w, "minecraft:block_change", 0.5, 64.0, 0.5, "");
        }
        {
            WorldManager.worldGameEvent(w, "", 0.5, 64.0, 0.5, "");
        }
        {
            byte[] d802 = new Writer(512).beginList().string("minecraft:block.note_block.bell").string("block").number(0.5).number(64.0).number(0.5).number(1.0).number(1.0).integer(0).end().finish();
            byte[] o803 = new byte[64];
            WorldManager.worldPlayerEffect(p, "sound_at", d802, o803);
        }
        {
            byte[] d812 = new Writer(512).beginList().end().finish();
            byte[] o813 = new byte[64];
            WorldManager.worldPlayerEffect(p, "nope", d812, o813);
        }
        {
            byte[] o821 = new byte[512];
            WorldManager.worldBorderGet(w, o821);
        }
        {
            byte[] d831 = new Writer(512).beginMap().key("size").number(100.0).end().finish();
            WorldManager.worldBorderSet(w, d831);
        }
        {
            byte[] d841 = new Writer(512).beginMap().key("size").number(-1.0).key("seconds").number(-1.0).end().finish();
            WorldManager.worldBorderSet(w, d841);
        }
        {
            byte[] d852 = new Writer(512).beginMap().end().finish();
            byte[] o853 = new byte[1024];
            WorldManager.worldCall(w, "ops", d852, o853);
        }
        {
            byte[] d862 = new Writer(512).beginMap().key("x").integer(2).key("z").integer(2).end().finish();
            byte[] o863 = new byte[256];
            WorldManager.worldCall(w, "highest", d862, o863);
        }
        {
            byte[] d872 = new Writer(512).beginMap().end().finish();
            byte[] o873 = new byte[256];
            WorldManager.worldCall(w, "nope", d872, o873);
        }
        {
            s1n = WorldManager.structureCapture(w, 0, 64, 0, 3, 2, 3, 0, s1);
        }
        {
            WorldManager.structurePut("tour:a", cut(s1, kl(s1n, 2048)), 0);
        }
        {
            WorldManager.structurePut("tour:b", new byte[] {10, 0, 0, 0}, 0);
        }
        {
            byte[] o910 = new byte[256];
            WorldManager.structureList(o910);
        }
        {
            byte[] o921 = new byte[256];
            WorldManager.structureGet("tour:a", o921);
        }
        {
            byte[] o931 = new byte[256];
            WorldManager.structureGet("tour:zzz", o931);
        }
        {
            byte[] o941 = new byte[128];
            WorldManager.structureFile("tour:a", o941);
        }
        {
            byte[] d951 = new Writer(512).beginMap().key("x").integer(0).key("y").integer(64).key("z").integer(0).end().finish();
            byte[] o952 = new byte[1024];
            WorldManager.structureScan(cut(s1, kl(s1n, 2048)), d951, o952);
        }
        {
            byte[] d961 = new Writer(512).beginMap().key("world").string("world").key("x").integer(8).key("y").integer(64).key("z").integer(8).end().finish();
            WorldManager.structurePlace(cut(s1, kl(s1n, 2048)), d961);
        }
        {
            WorldManager.structureRemove("tour:a", 0);
        }
        {
            WorldManager.structureRemove("tour:a", 0);
        }
        {
            byte[] d990 = new Writer(512).beginMap().key("op").string("shape").key("settings").string("minecraft:overworld").key("seed").integer(0).end().finish();
            byte[] o991 = new byte[256];
            WorldManager.noiseQuery(d990, o991);
        }
        {
            byte[] d1000 = new Writer(512).beginMap().key("op").string("nope").end().finish();
            byte[] o1001 = new byte[256];
            WorldManager.noiseQuery(d1000, o1001);
        }
        {
            byte[] d1010 = new Writer(512).beginMap().key("id").string("minecraft:temperature").key("seed").integer(0).end().finish();
            nz = WorldManager.noiseCreate(d1010);
        }
        {
            double[] f1024 = new double[1];
            WorldManager.noiseSample(nz, 0.0, 0.0, 0.0, f1024);
        }
        {
            byte[] o1031 = new byte[128];
            WorldManager.noiseInfo(nz, o1031);
        }
        {
            WorldManager.noiseRelease(nz);
        }
        {
            WorldManager.noiseRelease(nz);
        }
        {
            WorldManager.blockStateId("minecraft:stone");
        }
        {
            WorldManager.blockStateId("nope:block");
        }
        {
            byte[] o1081 = new byte[128];
            WorldManager.blockStateText(1, o1081);
        }
        {
            byte[] d1091 = new Writer(512).beginMap().end().finish();
            byte[] o1092 = new byte[1024];
            WorldManager.worldsData("tables", d1091, o1092);
        }
        {
            byte[] d1101 = new Writer(512).beginMap().end().finish();
            byte[] o1102 = new byte[512];
            WorldManager.worldsData("blocks", d1101, o1102);
        }
        {
            byte[] d1111 = new Writer(512).beginMap().end().finish();
            byte[] o1112 = new byte[64];
            WorldManager.worldsData("nope", d1111, o1112);
        }
        {
            byte[] d1121 = new Writer(512).beginMap().end().finish();
            byte[] o1122 = new byte[128];
            WorldManager.datafixCall("types", d1121, o1122);
        }
        {
            byte[] d1131 = new Writer(512).beginMap().key("type").string("chunk").end().finish();
            byte[] o1132 = new byte[64];
            WorldManager.datafixCall("supports", d1131, o1132);
        }
        {
            byte[] d1141 = new Writer(512).beginMap().key("type").string("chunk").end().finish();
            byte[] o1142 = new byte[256];
            WorldManager.datafixCall("steps", d1141, o1142);
        }
        {
            byte[] d1151 = new Writer(512).beginMap().key("type").string("chunk").key("from").integer(100).key("to").integer(200).end().finish();
            byte[] o1152 = new byte[64];
            WorldManager.datafixCall("missing", d1151, o1152);
        }
        {
            byte[] d1161 = new Writer(512).beginMap().key("id").integer(16).end().finish();
            byte[] o1162 = new byte[128];
            WorldManager.datafixCall("legacy_state", d1161, o1162);
        }
        {
            byte[] d1171 = new Writer(512).beginMap().key("id").integer(16).end().finish();
            byte[] o1172 = new byte[128];
            WorldManager.datafixCall("modern_state", d1171, o1172);
        }
        {
            byte[] d1181 = new Writer(512).beginMap().key("type").string("chunk").key("nbt").bytes(new byte[] {10, 0, 0, 0}).key("from").integer(100).key("to").integer(200).end().finish();
            byte[] o1182 = new byte[128];
            WorldManager.datafixCall("upgrade", d1181, o1182);
        }
        {
            byte[] d1191 = new Writer(512).beginMap().end().finish();
            byte[] o1192 = new byte[64];
            WorldManager.datafixCall("nope", d1191, o1192);
        }
        {
            WorldManager.worldAt(w, 3.5, 3.5, WorldTour::later);
        }
    }

    public static void register() {
        Registry.register("worlds", "", WorldTour::tour);
    }
}
