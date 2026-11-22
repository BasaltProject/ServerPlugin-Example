package org.basaltmc.server.example.worlds;


public final class Worlds {
    private Worlds() {}

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
        org.basaltmc.server.worlds.Worlds.worldValueResult(ctx, out);
    }

    private static void later2(int ctx) {
        int w2 = 0;
        {
            byte[] d20000 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("name").string("tour").end().finish();
            org.basaltmc.server.worlds.Worlds.worldCreate(d20000);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldUnload("nope", 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldUnload("tour", 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldDelete("tour");
        }
        if (w2 < 0) {
            org.basaltmc.server.log.Log.logDebug("unreachable");
        }
    }

    private static void later(int ctx) {
        int w2 = 0;
        {
            byte[] d10000 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("name").string("tour").end().finish();
            w2 = org.basaltmc.server.worlds.Worlds.worldCreate(d10000);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldAt(w2, 0.5, 0.5, Worlds::later2);
        }
        if (w2 < 0) {
            org.basaltmc.server.log.Log.logDebug("unreachable");
        }
    }

    private static void tour(int ctx) {
        org.basaltmc.server.worlds.Worlds.chunkCoord(-17);
        org.basaltmc.server.worlds.Worlds.blockDistance(0, 0, 0, 3, 4, 0);
        int[] pos = {1, 2, 3};
        org.basaltmc.server.worlds.Worlds.posOffset(pos, 1, 1, 1);
        int w = 0, p = org.basaltmc.server.players.SharedPlayers.playerByName("Steve"), w2 = 0, rq = 0, nz = 0;
        byte[] s1 = new byte[2048];
        int s1n = 0;
        {
            w = org.basaltmc.server.worlds.Worlds.worldByName("world");
        }
        {
            org.basaltmc.server.worlds.Worlds.worldByName("nope");
        }
        {
            org.basaltmc.server.worlds.Worlds.worldDefault();
        }
        {
            int[] o30 = new int[8];
            org.basaltmc.server.worlds.Worlds.worldList(o30);
        }
        {
            byte[] o41 = new byte[128];
            org.basaltmc.server.worlds.Worlds.worldName(w, o41);
        }
        {
            byte[] o51 = new byte[128];
            org.basaltmc.server.worlds.Worlds.worldName(999, o51);
        }
        {
            byte[] o62 = new byte[128];
            org.basaltmc.server.worlds.Worlds.worldGet(w, "time", o62);
        }
        {
            byte[] o72 = new byte[128];
            org.basaltmc.server.worlds.Worlds.worldGet(w, "nope", o72);
        }
        {
            byte[] d82 = new org.basaltmc.server.values.Values.Writer(512).integer(1000).finish();
            org.basaltmc.server.worlds.Worlds.worldSet(w, "day_time", d82);
        }
        {
            byte[] d92 = new org.basaltmc.server.values.Values.Writer(512).integer(1).finish();
            org.basaltmc.server.worlds.Worlds.worldSet(w, "nope", d92);
        }
        {
            byte[] o102 = new byte[128];
            org.basaltmc.server.worlds.Worlds.worldRuleGet(w, "doDaylightCycle", o102);
        }
        {
            byte[] o112 = new byte[128];
            org.basaltmc.server.worlds.Worlds.worldRuleGet(w, "nope", o112);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldRuleSet(w, "doDaylightCycle", "false");
        }
        {
            rq = org.basaltmc.server.worlds.Worlds.worldAt(w, 1.5, 1.5, Worlds::done);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldRequestCancel(rq);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldAt(w, 2.5, 2.5, Worlds::done);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldBlockSet(w, 0, 64, 0, "minecraft:stone", 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldBlockSet(w, 0, 64, 0, "nope:block", 0);
        }
        {
            byte[] o185 = new byte[128];
            org.basaltmc.server.worlds.Worlds.worldBlockGet(w, 0, 64, 0, 0, o185);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldStateGet(w, 0, 64, 0, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldStateSet(w, 1, 64, 0, 3, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldStateGet(999, 0, 64, 0, 0);
        }
        {
            int[] o222 = new int[2];
            org.basaltmc.server.worlds.Worlds.worldStatesRead(w, new int[] {0, 64, 0, 1, 64, 0}, o222, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldStatesWrite(w, new int[] {2, 64, 0, 3, 64, 0}, new int[] {5, 5}, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldFill(w, 2, 64, 2, 5, 65, 5, 5, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldFill(999, 0, 0, 0, 1, 1, 1, 1, 0);
        }
        {
            int[] o265 = new int[4096];
            org.basaltmc.server.worlds.Worlds.worldSectionRead(w, 0, 4, 0, 0, o265);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldSectionWrite(w, 0, 4, 0, new int[4096], 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldSectionWrite(w, 0, 200, 0, new int[4096], 0);
        }
        {
            int[] o294 = new int[8];
            org.basaltmc.server.worlds.Worlds.worldChunkRead(w, 0, 0, 0, o294);
        }
        {
            int[] o306 = new int[4096];
            org.basaltmc.server.worlds.Worlds.worldChunkApply(w, 0, 0, new int[] {4}, new int[4096], 0, o306);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldChangesApply(w, new int[] {0, 64, 0}, new int[] {5}, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldBlocksSettle(w, new int[] {0, 64, 0}, new int[] {0}, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldBlocksNotify(w, new int[] {0, 64, 0}, new int[] {1, 64, 0});
        }
        {
            org.basaltmc.server.worlds.Worlds.worldBlocksReshape(w, new int[] {0, 64, 0}, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldStateValid(w, 0, 64, 0, 1);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldTickSchedule(w, 0, 64, 0, 5, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldTickPending(w, 0, 64, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldStateSet(w, 3, 64, 0, 6, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldBlockEntityEdit(w, 1, 3, 64, 0, new byte[] {10, 0, 0, 0});
        }
        {
            byte[] o404 = new byte[256];
            org.basaltmc.server.worlds.Worlds.worldBlockEntityGet(w, 3, 64, 0, o404);
        }
        {
            byte[] o414 = new byte[256];
            org.basaltmc.server.worlds.Worlds.worldBlockEntityGet(w, 9, 64, 9, o414);
        }
        {
            int[] o423 = new int[8];
            org.basaltmc.server.worlds.Worlds.worldBlockEntities(w, 0, 0, o423);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldDataSet(w, 0, 0, 0, 0, new byte[] {10, 0, 0, 0});
        }
        {
            byte[] o445 = new byte[256];
            org.basaltmc.server.worlds.Worlds.worldDataGet(w, 0, 0, 0, 0, o445);
        }
        {
            byte[] o455 = new byte[256];
            org.basaltmc.server.worlds.Worlds.worldDataGet(w, 1, 9, 0, 9, o455);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldChunkStatus(w, 0, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldChunkStatus(w, 9, 9);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldChunkLoad(w, 3, 3, 8);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldChunkUnload(w, 3, 3);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldChunkRequest(w, 4, 4, 8, 0, Worlds::done);
        }
        {
            int[] o511 = new int[16];
            org.basaltmc.server.worlds.Worlds.worldChunksLoaded(w, o511);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldChunkHold(w, "tour", new int[] {4, 4});
        }
        {
            int[] o532 = new int[8];
            org.basaltmc.server.worlds.Worlds.worldChunkHeld(w, "tour", o532);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldChunkRelease(w, "tour", new int[] {4, 4});
        }
        {
            org.basaltmc.server.worlds.Worlds.worldChunksResend(w, new int[] {0, 0});
        }
        {
            org.basaltmc.server.worlds.Worlds.worldStoredPut(w, 7, 7, new byte[] {10, 0, 0, 0});
        }
        {
            byte[] o573 = new byte[256];
            org.basaltmc.server.worlds.Worlds.worldStoredGet(w, 7, 7, o573);
        }
        {
            byte[] o583 = new byte[256];
            org.basaltmc.server.worlds.Worlds.worldStoredGet(w, 8, 8, o583);
        }
        {
            int[] o593 = new int[8];
            org.basaltmc.server.worlds.Worlds.worldStoredList(w, 0, 0, o593);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldLightAt(w, 0, 64, 0, 0);
        }
        {
            byte[] o616 = new byte[2048];
            org.basaltmc.server.worlds.Worlds.worldLightRead(w, 0, 4, 0, 1, 1, o616);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldLightWrite(w, 0, 4, 0, 1, new byte[2048]);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldLightWrite(w, 0, 4, 0, 1, new byte[] {10, 0, 0, 0});
        }
        {
            org.basaltmc.server.worlds.Worlds.worldLightCheck(w, new int[] {0, 0}, Worlds::done);
        }
        {
            byte[] o654 = new byte[128];
            org.basaltmc.server.worlds.Worlds.worldBiomeGet(w, 0, 64, 0, o654);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldBiomeSet(w, 0, 64, 0, "minecraft:desert");
        }
        {
            org.basaltmc.server.worlds.Worlds.worldBiomeSet(w, 0, 64, 0, "nope:biome");
        }
        {
            int[] o683 = new int[8];
            org.basaltmc.server.worlds.Worlds.worldBiomesRead(w, 0, 0, o683);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldBiomesWrite(w, 0, 0, new int[] {1, 1, 1, 1});
        }
        {
            org.basaltmc.server.worlds.Worlds.worldSound(w, "minecraft:block.note_block.bell", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0L);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldSound(w, "", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0L);
        }
        {
            byte[] d721 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("type").string("minecraft:flame").key("x").number(0.5).key("y").number(65.0).key("z").number(0.5).key("count").integer(3).end().finish();
            org.basaltmc.server.worlds.Worlds.worldParticle(w, d721);
        }
        {
            byte[] d731 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("count").integer(1).end().finish();
            org.basaltmc.server.worlds.Worlds.worldParticle(w, d731);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldLevelEvent(w, 1000, 0, 64, 0, 0, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldBlockEvent(w, 0, 64, 0, 1, 2);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldExplode(w, 0.5, 64.0, 0.5, 0.0f, 0, 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldLightning(w, 0.5, 64.0, 0.5);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldGameEvent(w, "minecraft:block_change", 0.5, 64.0, 0.5, "");
        }
        {
            org.basaltmc.server.worlds.Worlds.worldGameEvent(w, "", 0.5, 64.0, 0.5, "");
        }
        {
            byte[] d802 = new org.basaltmc.server.values.Values.Writer(512).beginList().string("minecraft:block.note_block.bell").string("block").number(0.5).number(64.0).number(0.5).number(1.0).number(1.0).integer(0).end().finish();
            byte[] o803 = new byte[64];
            org.basaltmc.server.worlds.Worlds.worldPlayerEffect(p, "sound_at", d802, o803);
        }
        {
            byte[] d812 = new org.basaltmc.server.values.Values.Writer(512).beginList().end().finish();
            byte[] o813 = new byte[64];
            org.basaltmc.server.worlds.Worlds.worldPlayerEffect(p, "nope", d812, o813);
        }
        {
            byte[] o821 = new byte[512];
            org.basaltmc.server.worlds.Worlds.worldBorderGet(w, o821);
        }
        {
            byte[] d831 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("size").number(100.0).end().finish();
            org.basaltmc.server.worlds.Worlds.worldBorderSet(w, d831);
        }
        {
            byte[] d841 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("size").number(-1.0).key("seconds").number(-1.0).end().finish();
            org.basaltmc.server.worlds.Worlds.worldBorderSet(w, d841);
        }
        {
            byte[] d852 = new org.basaltmc.server.values.Values.Writer(512).beginMap().end().finish();
            byte[] o853 = new byte[1024];
            org.basaltmc.server.worlds.Worlds.worldCall(w, "ops", d852, o853);
        }
        {
            byte[] d862 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("x").integer(2).key("z").integer(2).end().finish();
            byte[] o863 = new byte[256];
            org.basaltmc.server.worlds.Worlds.worldCall(w, "highest", d862, o863);
        }
        {
            byte[] d872 = new org.basaltmc.server.values.Values.Writer(512).beginMap().end().finish();
            byte[] o873 = new byte[256];
            org.basaltmc.server.worlds.Worlds.worldCall(w, "nope", d872, o873);
        }
        {
            s1n = org.basaltmc.server.worlds.Worlds.structureCapture(w, 0, 64, 0, 3, 2, 3, 0, s1);
        }
        {
            org.basaltmc.server.worlds.Worlds.structurePut("tour:a", cut(s1, kl(s1n, 2048)), 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.structurePut("tour:b", new byte[] {10, 0, 0, 0}, 0);
        }
        {
            byte[] o910 = new byte[256];
            org.basaltmc.server.worlds.Worlds.structureList(o910);
        }
        {
            byte[] o921 = new byte[256];
            org.basaltmc.server.worlds.Worlds.structureGet("tour:a", o921);
        }
        {
            byte[] o931 = new byte[256];
            org.basaltmc.server.worlds.Worlds.structureGet("tour:zzz", o931);
        }
        {
            byte[] o941 = new byte[128];
            org.basaltmc.server.worlds.Worlds.structureFile("tour:a", o941);
        }
        {
            byte[] d951 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("x").integer(0).key("y").integer(64).key("z").integer(0).end().finish();
            byte[] o952 = new byte[1024];
            org.basaltmc.server.worlds.Worlds.structureScan(cut(s1, kl(s1n, 2048)), d951, o952);
        }
        {
            byte[] d961 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("world").string("world").key("x").integer(8).key("y").integer(64).key("z").integer(8).end().finish();
            org.basaltmc.server.worlds.Worlds.structurePlace(cut(s1, kl(s1n, 2048)), d961);
        }
        {
            org.basaltmc.server.worlds.Worlds.structureRemove("tour:a", 0);
        }
        {
            org.basaltmc.server.worlds.Worlds.structureRemove("tour:a", 0);
        }
        {
            byte[] d990 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("op").string("shape").key("settings").string("minecraft:overworld").key("seed").integer(0).end().finish();
            byte[] o991 = new byte[256];
            org.basaltmc.server.worlds.Worlds.noiseQuery(d990, o991);
        }
        {
            byte[] d1000 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("op").string("nope").end().finish();
            byte[] o1001 = new byte[256];
            org.basaltmc.server.worlds.Worlds.noiseQuery(d1000, o1001);
        }
        {
            byte[] d1010 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("id").string("minecraft:temperature").key("seed").integer(0).end().finish();
            nz = org.basaltmc.server.worlds.Worlds.noiseCreate(d1010);
        }
        {
            double[] f1024 = new double[1];
            org.basaltmc.server.worlds.Worlds.noiseSample(nz, 0.0, 0.0, 0.0, f1024);
        }
        {
            byte[] o1031 = new byte[128];
            org.basaltmc.server.worlds.Worlds.noiseInfo(nz, o1031);
        }
        {
            org.basaltmc.server.worlds.Worlds.noiseRelease(nz);
        }
        {
            org.basaltmc.server.worlds.Worlds.noiseRelease(nz);
        }
        {
            org.basaltmc.server.worlds.Worlds.blockStateId("minecraft:stone");
        }
        {
            org.basaltmc.server.worlds.Worlds.blockStateId("nope:block");
        }
        {
            byte[] o1081 = new byte[128];
            org.basaltmc.server.worlds.Worlds.blockStateText(1, o1081);
        }
        {
            byte[] d1091 = new org.basaltmc.server.values.Values.Writer(512).beginMap().end().finish();
            byte[] o1092 = new byte[1024];
            org.basaltmc.server.worlds.Worlds.worldsData("tables", d1091, o1092);
        }
        {
            byte[] d1101 = new org.basaltmc.server.values.Values.Writer(512).beginMap().end().finish();
            byte[] o1102 = new byte[512];
            org.basaltmc.server.worlds.Worlds.worldsData("blocks", d1101, o1102);
        }
        {
            byte[] d1111 = new org.basaltmc.server.values.Values.Writer(512).beginMap().end().finish();
            byte[] o1112 = new byte[64];
            org.basaltmc.server.worlds.Worlds.worldsData("nope", d1111, o1112);
        }
        {
            byte[] d1121 = new org.basaltmc.server.values.Values.Writer(512).beginMap().end().finish();
            byte[] o1122 = new byte[128];
            org.basaltmc.server.worlds.Worlds.datafixCall("types", d1121, o1122);
        }
        {
            byte[] d1131 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("type").string("chunk").end().finish();
            byte[] o1132 = new byte[64];
            org.basaltmc.server.worlds.Worlds.datafixCall("supports", d1131, o1132);
        }
        {
            byte[] d1141 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("type").string("chunk").end().finish();
            byte[] o1142 = new byte[256];
            org.basaltmc.server.worlds.Worlds.datafixCall("steps", d1141, o1142);
        }
        {
            byte[] d1151 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("type").string("chunk").key("from").integer(100).key("to").integer(200).end().finish();
            byte[] o1152 = new byte[64];
            org.basaltmc.server.worlds.Worlds.datafixCall("missing", d1151, o1152);
        }
        {
            byte[] d1161 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("id").integer(16).end().finish();
            byte[] o1162 = new byte[128];
            org.basaltmc.server.worlds.Worlds.datafixCall("legacy_state", d1161, o1162);
        }
        {
            byte[] d1171 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("id").integer(16).end().finish();
            byte[] o1172 = new byte[128];
            org.basaltmc.server.worlds.Worlds.datafixCall("modern_state", d1171, o1172);
        }
        {
            byte[] d1181 = new org.basaltmc.server.values.Values.Writer(512).beginMap().key("type").string("chunk").key("nbt").bytes(new byte[] {10, 0, 0, 0}).key("from").integer(100).key("to").integer(200).end().finish();
            byte[] o1182 = new byte[128];
            org.basaltmc.server.worlds.Worlds.datafixCall("upgrade", d1181, o1182);
        }
        {
            byte[] d1191 = new org.basaltmc.server.values.Values.Writer(512).beginMap().end().finish();
            byte[] o1192 = new byte[64];
            org.basaltmc.server.worlds.Worlds.datafixCall("nope", d1191, o1192);
        }
        {
            org.basaltmc.server.worlds.Worlds.worldAt(w, 3.5, 3.5, Worlds::later);
        }
    }

    public static void register() {
        org.basaltmc.server.commands.Commands.command("worlds", "", Worlds::tour);
    }
}
