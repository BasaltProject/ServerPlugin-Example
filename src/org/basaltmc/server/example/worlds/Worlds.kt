package org.basaltmc.server.example.worlds


import org.basaltmc.server.core.Ctx

object Worlds {
    private fun kl(length: Int, capacity: Int): Int = if (length > 0 && length <= capacity) length else 0

    private fun done(ctx: Ctx) {
        val out = ByteArray(16)
        org.basaltmc.server.worlds.worldValueResult(ctx, out)
    }

    private fun later2(ctx: Ctx) {
        var w2 = 0
        run {
            val d20000 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("name").string("tour").end().finish() ?: ByteArray(0)
            org.basaltmc.server.worlds.worldCreate(d20000)
        }
        run {
            org.basaltmc.server.worlds.worldUnload("nope", 0)
        }
        run {
            org.basaltmc.server.worlds.worldUnload("tour", 0)
        }
        run {
            org.basaltmc.server.worlds.worldDelete("tour")
        }
        if (w2 < 0) org.basaltmc.server.log.logDebug("unreachable")
    }

    private fun later(ctx: Ctx) {
        var w2 = 0
        run {
            val d10000 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("name").string("tour").end().finish() ?: ByteArray(0)
            w2 = org.basaltmc.server.worlds.worldCreate(d10000)
        }
        run {
            org.basaltmc.server.worlds.worldAt(w2, 0.5, 0.5, ::later2)
        }
        if (w2 < 0) org.basaltmc.server.log.logDebug("unreachable")
    }

    private fun tour(ctx: Ctx) {
        org.basaltmc.server.worlds.chunkCoord(-17)
        org.basaltmc.server.worlds.blockDistance(0, 0, 0, 3, 4, 0)
        val pos = intArrayOf(1, 2, 3)
        org.basaltmc.server.worlds.posOffset(pos, 1, 1, 1)
        var w = 0
        val p = org.basaltmc.server.players.playerByName("Steve")
        var w2 = 0
        var rq = 0
        var nz = 0
        val s1 = ByteArray(2048)
        var s1n = 0
        run {
            w = org.basaltmc.server.worlds.worldByName("world")
        }
        run {
            org.basaltmc.server.worlds.worldByName("nope")
        }
        run {
            org.basaltmc.server.worlds.worldDefault()
        }
        run {
            val o30 = IntArray(8)
            org.basaltmc.server.worlds.worldList(o30)
        }
        run {
            val o41 = ByteArray(128)
            org.basaltmc.server.worlds.worldName(w, o41)
        }
        run {
            val o51 = ByteArray(128)
            org.basaltmc.server.worlds.worldName(999, o51)
        }
        run {
            val o62 = ByteArray(128)
            org.basaltmc.server.worlds.worldGet(w, "time", o62)
        }
        run {
            val o72 = ByteArray(128)
            org.basaltmc.server.worlds.worldGet(w, "nope", o72)
        }
        run {
            val d82 = org.basaltmc.server.values.ValueWriter(512).int(1000L).finish() ?: ByteArray(0)
            org.basaltmc.server.worlds.worldSet(w, "day_time", d82)
        }
        run {
            val d92 = org.basaltmc.server.values.ValueWriter(512).int(1L).finish() ?: ByteArray(0)
            org.basaltmc.server.worlds.worldSet(w, "nope", d92)
        }
        run {
            val o102 = ByteArray(128)
            org.basaltmc.server.worlds.worldRuleGet(w, "doDaylightCycle", o102)
        }
        run {
            val o112 = ByteArray(128)
            org.basaltmc.server.worlds.worldRuleGet(w, "nope", o112)
        }
        run {
            org.basaltmc.server.worlds.worldRuleSet(w, "doDaylightCycle", "false")
        }
        run {
            rq = org.basaltmc.server.worlds.worldAt(w, 1.5, 1.5, ::done)
        }
        run {
            org.basaltmc.server.worlds.worldRequestCancel(rq)
        }
        run {
            org.basaltmc.server.worlds.worldAt(w, 2.5, 2.5, ::done)
        }
        run {
            org.basaltmc.server.worlds.worldBlockSet(w, 0, 64, 0, "minecraft:stone", 0)
        }
        run {
            org.basaltmc.server.worlds.worldBlockSet(w, 0, 64, 0, "nope:block", 0)
        }
        run {
            val o185 = ByteArray(128)
            org.basaltmc.server.worlds.worldBlockGet(w, 0, 64, 0, 0, o185)
        }
        run {
            org.basaltmc.server.worlds.worldStateGet(w, 0, 64, 0, 0)
        }
        run {
            org.basaltmc.server.worlds.worldStateSet(w, 1, 64, 0, 3, 0)
        }
        run {
            org.basaltmc.server.worlds.worldStateGet(999, 0, 64, 0, 0)
        }
        run {
            val o222 = IntArray(2)
            org.basaltmc.server.worlds.worldStatesRead(w, intArrayOf(0, 64, 0, 1, 64, 0), o222, 0)
        }
        run {
            org.basaltmc.server.worlds.worldStatesWrite(w, intArrayOf(2, 64, 0, 3, 64, 0), intArrayOf(5, 5), 0)
        }
        run {
            org.basaltmc.server.worlds.worldFill(w, 2, 64, 2, 5, 65, 5, 5, 0)
        }
        run {
            org.basaltmc.server.worlds.worldFill(999, 0, 0, 0, 1, 1, 1, 1, 0)
        }
        run {
            val o265 = IntArray(4096)
            org.basaltmc.server.worlds.worldSectionRead(w, 0, 4, 0, 0, o265)
        }
        run {
            org.basaltmc.server.worlds.worldSectionWrite(w, 0, 4, 0, IntArray(4096), 0)
        }
        run {
            org.basaltmc.server.worlds.worldSectionWrite(w, 0, 200, 0, IntArray(4096), 0)
        }
        run {
            val o294 = IntArray(8)
            org.basaltmc.server.worlds.worldChunkRead(w, 0, 0, 0, o294)
        }
        run {
            val o306 = IntArray(4096)
            org.basaltmc.server.worlds.worldChunkApply(w, 0, 0, intArrayOf(4), IntArray(4096), 0, o306)
        }
        run {
            org.basaltmc.server.worlds.worldChangesApply(w, intArrayOf(0, 64, 0), intArrayOf(5), 0)
        }
        run {
            org.basaltmc.server.worlds.worldBlocksSettle(w, intArrayOf(0, 64, 0), intArrayOf(0), 0)
        }
        run {
            org.basaltmc.server.worlds.worldBlocksNotify(w, intArrayOf(0, 64, 0), intArrayOf(1, 64, 0))
        }
        run {
            org.basaltmc.server.worlds.worldBlocksReshape(w, intArrayOf(0, 64, 0), 0)
        }
        run {
            org.basaltmc.server.worlds.worldStateValid(w, 0, 64, 0, 1)
        }
        run {
            org.basaltmc.server.worlds.worldTickSchedule(w, 0, 64, 0, 5, 0)
        }
        run {
            org.basaltmc.server.worlds.worldTickPending(w, 0, 64, 0)
        }
        run {
            org.basaltmc.server.worlds.worldStateSet(w, 3, 64, 0, 6, 0)
        }
        run {
            org.basaltmc.server.worlds.worldBlockEntityEdit(w, 1, 3, 64, 0, byteArrayOf(10, 0, 0, 0))
        }
        run {
            val o404 = ByteArray(256)
            org.basaltmc.server.worlds.worldBlockEntityGet(w, 3, 64, 0, o404)
        }
        run {
            val o414 = ByteArray(256)
            org.basaltmc.server.worlds.worldBlockEntityGet(w, 9, 64, 9, o414)
        }
        run {
            val o423 = IntArray(8)
            org.basaltmc.server.worlds.worldBlockEntities(w, 0, 0, o423)
        }
        run {
            org.basaltmc.server.worlds.worldDataSet(w, 0, 0, 0, 0, byteArrayOf(10, 0, 0, 0))
        }
        run {
            val o445 = ByteArray(256)
            org.basaltmc.server.worlds.worldDataGet(w, 0, 0, 0, 0, o445)
        }
        run {
            val o455 = ByteArray(256)
            org.basaltmc.server.worlds.worldDataGet(w, 1, 9, 0, 9, o455)
        }
        run {
            org.basaltmc.server.worlds.worldChunkStatus(w, 0, 0)
        }
        run {
            org.basaltmc.server.worlds.worldChunkStatus(w, 9, 9)
        }
        run {
            org.basaltmc.server.worlds.worldChunkLoad(w, 3, 3, 8)
        }
        run {
            org.basaltmc.server.worlds.worldChunkUnload(w, 3, 3)
        }
        run {
            org.basaltmc.server.worlds.worldChunkRequest(w, 4, 4, 8, 0, ::done)
        }
        run {
            val o511 = IntArray(16)
            org.basaltmc.server.worlds.worldChunksLoaded(w, o511)
        }
        run {
            org.basaltmc.server.worlds.worldChunkHold(w, "tour", intArrayOf(4, 4))
        }
        run {
            val o532 = IntArray(8)
            org.basaltmc.server.worlds.worldChunkHeld(w, "tour", o532)
        }
        run {
            org.basaltmc.server.worlds.worldChunkRelease(w, "tour", intArrayOf(4, 4))
        }
        run {
            org.basaltmc.server.worlds.worldChunksResend(w, intArrayOf(0, 0))
        }
        run {
            org.basaltmc.server.worlds.worldStoredPut(w, 7, 7, byteArrayOf(10, 0, 0, 0))
        }
        run {
            val o573 = ByteArray(256)
            org.basaltmc.server.worlds.worldStoredGet(w, 7, 7, o573)
        }
        run {
            val o583 = ByteArray(256)
            org.basaltmc.server.worlds.worldStoredGet(w, 8, 8, o583)
        }
        run {
            val o593 = IntArray(8)
            org.basaltmc.server.worlds.worldStoredList(w, 0, 0, o593)
        }
        run {
            org.basaltmc.server.worlds.worldLightAt(w, 0, 64, 0, 0)
        }
        run {
            val o616 = ByteArray(2048)
            org.basaltmc.server.worlds.worldLightRead(w, 0, 4, 0, 1, 1, o616)
        }
        run {
            org.basaltmc.server.worlds.worldLightWrite(w, 0, 4, 0, 1, ByteArray(2048))
        }
        run {
            org.basaltmc.server.worlds.worldLightWrite(w, 0, 4, 0, 1, byteArrayOf(10, 0, 0, 0))
        }
        run {
            org.basaltmc.server.worlds.worldLightCheck(w, intArrayOf(0, 0), ::done)
        }
        run {
            val o654 = ByteArray(128)
            org.basaltmc.server.worlds.worldBiomeGet(w, 0, 64, 0, o654)
        }
        run {
            org.basaltmc.server.worlds.worldBiomeSet(w, 0, 64, 0, "minecraft:desert")
        }
        run {
            org.basaltmc.server.worlds.worldBiomeSet(w, 0, 64, 0, "nope:biome")
        }
        run {
            val o683 = IntArray(8)
            org.basaltmc.server.worlds.worldBiomesRead(w, 0, 0, o683)
        }
        run {
            org.basaltmc.server.worlds.worldBiomesWrite(w, 0, 0, intArrayOf(1, 1, 1, 1))
        }
        run {
            org.basaltmc.server.worlds.worldSound(w, "minecraft:block.note_block.bell", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0L)
        }
        run {
            org.basaltmc.server.worlds.worldSound(w, "", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0L)
        }
        run {
            val d721 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("type").string("minecraft:flame").key("x").float(0.5).key("y").float(65.0).key("z").float(0.5).key("count").int(3L).end().finish() ?: ByteArray(0)
            org.basaltmc.server.worlds.worldParticle(w, d721)
        }
        run {
            val d731 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("count").int(1L).end().finish() ?: ByteArray(0)
            org.basaltmc.server.worlds.worldParticle(w, d731)
        }
        run {
            org.basaltmc.server.worlds.worldLevelEvent(w, 1000, 0, 64, 0, 0, 0)
        }
        run {
            org.basaltmc.server.worlds.worldBlockEvent(w, 0, 64, 0, 1, 2)
        }
        run {
            org.basaltmc.server.worlds.worldExplode(w, 0.5, 64.0, 0.5, 0.0f, 0, 0)
        }
        run {
            org.basaltmc.server.worlds.worldLightning(w, 0.5, 64.0, 0.5)
        }
        run {
            org.basaltmc.server.worlds.worldGameEvent(w, "minecraft:block_change", 0.5, 64.0, 0.5, "")
        }
        run {
            org.basaltmc.server.worlds.worldGameEvent(w, "", 0.5, 64.0, 0.5, "")
        }
        run {
            val d802 = org.basaltmc.server.values.ValueWriter(512).beginList().string("minecraft:block.note_block.bell").string("block").float(0.5).float(64.0).float(0.5).float(1.0).float(1.0).int(0L).end().finish() ?: ByteArray(0)
            val o803 = ByteArray(64)
            org.basaltmc.server.worlds.worldPlayerEffect(p, "sound_at", d802, o803)
        }
        run {
            val d812 = org.basaltmc.server.values.ValueWriter(512).beginList().end().finish() ?: ByteArray(0)
            val o813 = ByteArray(64)
            org.basaltmc.server.worlds.worldPlayerEffect(p, "nope", d812, o813)
        }
        run {
            val o821 = ByteArray(512)
            org.basaltmc.server.worlds.worldBorderGet(w, o821)
        }
        run {
            val d831 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("size").float(100.0).end().finish() ?: ByteArray(0)
            org.basaltmc.server.worlds.worldBorderSet(w, d831)
        }
        run {
            val d841 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("size").float(-1.0).key("seconds").float(-1.0).end().finish() ?: ByteArray(0)
            org.basaltmc.server.worlds.worldBorderSet(w, d841)
        }
        run {
            val d852 = org.basaltmc.server.values.ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o853 = ByteArray(1024)
            org.basaltmc.server.worlds.worldCall(w, "ops", d852, o853)
        }
        run {
            val d862 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("x").int(2L).key("z").int(2L).end().finish() ?: ByteArray(0)
            val o863 = ByteArray(256)
            org.basaltmc.server.worlds.worldCall(w, "highest", d862, o863)
        }
        run {
            val d872 = org.basaltmc.server.values.ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o873 = ByteArray(256)
            org.basaltmc.server.worlds.worldCall(w, "nope", d872, o873)
        }
        run {
            s1n = org.basaltmc.server.worlds.structureCapture(w, 0, 64, 0, 3, 2, 3, 0, s1)
        }
        run {
            org.basaltmc.server.worlds.structurePut("tour:a", s1.copyOf(kl(s1n, 2048)), 0)
        }
        run {
            org.basaltmc.server.worlds.structurePut("tour:b", byteArrayOf(10, 0, 0, 0), 0)
        }
        run {
            val o910 = ByteArray(256)
            org.basaltmc.server.worlds.structureList(o910)
        }
        run {
            val o921 = ByteArray(256)
            org.basaltmc.server.worlds.structureGet("tour:a", o921)
        }
        run {
            val o931 = ByteArray(256)
            org.basaltmc.server.worlds.structureGet("tour:zzz", o931)
        }
        run {
            val o941 = ByteArray(128)
            org.basaltmc.server.worlds.structureFile("tour:a", o941)
        }
        run {
            val d951 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("x").int(0L).key("y").int(64L).key("z").int(0L).end().finish() ?: ByteArray(0)
            val o952 = ByteArray(1024)
            org.basaltmc.server.worlds.structureScan(s1.copyOf(kl(s1n, 2048)), d951, o952)
        }
        run {
            val d961 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("world").string("world").key("x").int(8L).key("y").int(64L).key("z").int(8L).end().finish() ?: ByteArray(0)
            org.basaltmc.server.worlds.structurePlace(s1.copyOf(kl(s1n, 2048)), d961)
        }
        run {
            org.basaltmc.server.worlds.structureRemove("tour:a", 0)
        }
        run {
            org.basaltmc.server.worlds.structureRemove("tour:a", 0)
        }
        run {
            val d990 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("op").string("shape").key("settings").string("minecraft:overworld").key("seed").int(0L).end().finish() ?: ByteArray(0)
            val o991 = ByteArray(256)
            org.basaltmc.server.worlds.noiseQuery(d990, o991)
        }
        run {
            val d1000 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("op").string("nope").end().finish() ?: ByteArray(0)
            val o1001 = ByteArray(256)
            org.basaltmc.server.worlds.noiseQuery(d1000, o1001)
        }
        run {
            val d1010 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("id").string("minecraft:temperature").key("seed").int(0L).end().finish() ?: ByteArray(0)
            nz = org.basaltmc.server.worlds.noiseCreate(d1010)
        }
        run {
            val f1024 = DoubleArray(1)
            org.basaltmc.server.worlds.noiseSample(nz, 0.0, 0.0, 0.0, f1024)
        }
        run {
            val o1031 = ByteArray(128)
            org.basaltmc.server.worlds.noiseInfo(nz, o1031)
        }
        run {
            org.basaltmc.server.worlds.noiseRelease(nz)
        }
        run {
            org.basaltmc.server.worlds.noiseRelease(nz)
        }
        run {
            org.basaltmc.server.worlds.blockStateId("minecraft:stone")
        }
        run {
            org.basaltmc.server.worlds.blockStateId("nope:block")
        }
        run {
            val o1081 = ByteArray(128)
            org.basaltmc.server.worlds.blockStateText(1, o1081)
        }
        run {
            val d1091 = org.basaltmc.server.values.ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o1092 = ByteArray(1024)
            org.basaltmc.server.worlds.worldsData("tables", d1091, o1092)
        }
        run {
            val d1101 = org.basaltmc.server.values.ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o1102 = ByteArray(512)
            org.basaltmc.server.worlds.worldsData("blocks", d1101, o1102)
        }
        run {
            val d1111 = org.basaltmc.server.values.ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o1112 = ByteArray(64)
            org.basaltmc.server.worlds.worldsData("nope", d1111, o1112)
        }
        run {
            val d1121 = org.basaltmc.server.values.ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o1122 = ByteArray(128)
            org.basaltmc.server.worlds.datafixCall("types", d1121, o1122)
        }
        run {
            val d1131 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("type").string("chunk").end().finish() ?: ByteArray(0)
            val o1132 = ByteArray(64)
            org.basaltmc.server.worlds.datafixCall("supports", d1131, o1132)
        }
        run {
            val d1141 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("type").string("chunk").end().finish() ?: ByteArray(0)
            val o1142 = ByteArray(256)
            org.basaltmc.server.worlds.datafixCall("steps", d1141, o1142)
        }
        run {
            val d1151 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("type").string("chunk").key("from").int(100L).key("to").int(200L).end().finish() ?: ByteArray(0)
            val o1152 = ByteArray(64)
            org.basaltmc.server.worlds.datafixCall("missing", d1151, o1152)
        }
        run {
            val d1161 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("id").int(16L).end().finish() ?: ByteArray(0)
            val o1162 = ByteArray(128)
            org.basaltmc.server.worlds.datafixCall("legacy_state", d1161, o1162)
        }
        run {
            val d1171 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("id").int(16L).end().finish() ?: ByteArray(0)
            val o1172 = ByteArray(128)
            org.basaltmc.server.worlds.datafixCall("modern_state", d1171, o1172)
        }
        run {
            val d1181 = org.basaltmc.server.values.ValueWriter(512).beginMap().key("type").string("chunk").key("nbt").bytes(byteArrayOf(10, 0, 0, 0)).key("from").int(100L).key("to").int(200L).end().finish() ?: ByteArray(0)
            val o1182 = ByteArray(128)
            org.basaltmc.server.worlds.datafixCall("upgrade", d1181, o1182)
        }
        run {
            val d1191 = org.basaltmc.server.values.ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o1192 = ByteArray(64)
            org.basaltmc.server.worlds.datafixCall("nope", d1191, o1192)
        }
        run {
            org.basaltmc.server.worlds.worldAt(w, 3.5, 3.5, ::later)
        }
        if (w + p + w2 + rq + nz < 0) org.basaltmc.server.log.logDebug("unreachable")
    }

    fun register() {
        org.basaltmc.server.commands.command("worlds", "", ::tour)
    }
}
