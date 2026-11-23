package org.basaltmc.server.example.worlds

import org.basaltmc.server.commands.command
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.log.logDebug
import org.basaltmc.server.players.playerByName
import org.basaltmc.server.values.ValueWriter
import org.basaltmc.server.worlds.blockDistance
import org.basaltmc.server.worlds.blockStateId
import org.basaltmc.server.worlds.blockStateText
import org.basaltmc.server.worlds.chunkCoord
import org.basaltmc.server.worlds.datafixCall
import org.basaltmc.server.worlds.noiseCreate
import org.basaltmc.server.worlds.noiseInfo
import org.basaltmc.server.worlds.noiseQuery
import org.basaltmc.server.worlds.noiseRelease
import org.basaltmc.server.worlds.noiseSample
import org.basaltmc.server.worlds.posOffset
import org.basaltmc.server.worlds.structureCapture
import org.basaltmc.server.worlds.structureFile
import org.basaltmc.server.worlds.structureGet
import org.basaltmc.server.worlds.structureList
import org.basaltmc.server.worlds.structurePlace
import org.basaltmc.server.worlds.structurePut
import org.basaltmc.server.worlds.structureRemove
import org.basaltmc.server.worlds.structureScan
import org.basaltmc.server.worlds.worldAt
import org.basaltmc.server.worlds.worldBiomeGet
import org.basaltmc.server.worlds.worldBiomeSet
import org.basaltmc.server.worlds.worldBiomesRead
import org.basaltmc.server.worlds.worldBiomesWrite
import org.basaltmc.server.worlds.worldBlockEntities
import org.basaltmc.server.worlds.worldBlockEntityEdit
import org.basaltmc.server.worlds.worldBlockEntityGet
import org.basaltmc.server.worlds.worldBlockEvent
import org.basaltmc.server.worlds.worldBlockGet
import org.basaltmc.server.worlds.worldBlockSet
import org.basaltmc.server.worlds.worldBlocksNotify
import org.basaltmc.server.worlds.worldBlocksReshape
import org.basaltmc.server.worlds.worldBlocksSettle
import org.basaltmc.server.worlds.worldBorderGet
import org.basaltmc.server.worlds.worldBorderSet
import org.basaltmc.server.worlds.worldByName
import org.basaltmc.server.worlds.worldCall
import org.basaltmc.server.worlds.worldChangesApply
import org.basaltmc.server.worlds.worldChunkApply
import org.basaltmc.server.worlds.worldChunkHeld
import org.basaltmc.server.worlds.worldChunkHold
import org.basaltmc.server.worlds.worldChunkLoad
import org.basaltmc.server.worlds.worldChunkRead
import org.basaltmc.server.worlds.worldChunkRelease
import org.basaltmc.server.worlds.worldChunkRequest
import org.basaltmc.server.worlds.worldChunkStatus
import org.basaltmc.server.worlds.worldChunkUnload
import org.basaltmc.server.worlds.worldChunksLoaded
import org.basaltmc.server.worlds.worldChunksResend
import org.basaltmc.server.worlds.worldCreate
import org.basaltmc.server.worlds.worldDataGet
import org.basaltmc.server.worlds.worldDataSet
import org.basaltmc.server.worlds.worldDefault
import org.basaltmc.server.worlds.worldDelete
import org.basaltmc.server.worlds.worldExplode
import org.basaltmc.server.worlds.worldFill
import org.basaltmc.server.worlds.worldGameEvent
import org.basaltmc.server.worlds.worldGet
import org.basaltmc.server.worlds.worldLevelEvent
import org.basaltmc.server.worlds.worldLightAt
import org.basaltmc.server.worlds.worldLightCheck
import org.basaltmc.server.worlds.worldLightRead
import org.basaltmc.server.worlds.worldLightWrite
import org.basaltmc.server.worlds.worldLightning
import org.basaltmc.server.worlds.worldList
import org.basaltmc.server.worlds.worldName
import org.basaltmc.server.worlds.worldParticle
import org.basaltmc.server.worlds.worldPlayerEffect
import org.basaltmc.server.worlds.worldRequestCancel
import org.basaltmc.server.worlds.worldRuleGet
import org.basaltmc.server.worlds.worldRuleSet
import org.basaltmc.server.worlds.worldSectionRead
import org.basaltmc.server.worlds.worldSectionWrite
import org.basaltmc.server.worlds.worldSet
import org.basaltmc.server.worlds.worldSound
import org.basaltmc.server.worlds.worldStateGet
import org.basaltmc.server.worlds.worldStateSet
import org.basaltmc.server.worlds.worldStateValid
import org.basaltmc.server.worlds.worldStatesRead
import org.basaltmc.server.worlds.worldStatesWrite
import org.basaltmc.server.worlds.worldStoredGet
import org.basaltmc.server.worlds.worldStoredList
import org.basaltmc.server.worlds.worldStoredPut
import org.basaltmc.server.worlds.worldTickPending
import org.basaltmc.server.worlds.worldTickSchedule
import org.basaltmc.server.worlds.worldUnload
import org.basaltmc.server.worlds.worldValueResult
import org.basaltmc.server.worlds.worldsData

object WorldTour {
    private fun kl(length: Int, capacity: Int): Int = if (length > 0 && length <= capacity) length else 0

    private fun done(ctx: Ctx) {
        val out = ByteArray(16)
        worldValueResult(ctx, out)
    }

    private fun later2(ctx: Ctx) {
        var w2 = 0
        run {
            val d20000 = ValueWriter(512).beginMap().key("name").string("tour").end().finish() ?: ByteArray(0)
            worldCreate(d20000)
        }
        run {
            worldUnload("nope", 0)
        }
        run {
            worldUnload("tour", 0)
        }
        run {
            worldDelete("tour")
        }
        if (w2 < 0) logDebug("unreachable")
    }

    private fun later(ctx: Ctx) {
        var w2 = 0
        run {
            val d10000 = ValueWriter(512).beginMap().key("name").string("tour").end().finish() ?: ByteArray(0)
            w2 = worldCreate(d10000)
        }
        run {
            worldAt(w2, 0.5, 0.5, ::later2)
        }
        if (w2 < 0) logDebug("unreachable")
    }

    private fun tour(ctx: Ctx) {
        chunkCoord(-17)
        blockDistance(0, 0, 0, 3, 4, 0)
        val pos = intArrayOf(1, 2, 3)
        posOffset(pos, 1, 1, 1)
        var w = 0
        val p = playerByName("Steve")
        var w2 = 0
        var rq = 0
        var nz = 0
        val s1 = ByteArray(2048)
        var s1n = 0
        run {
            w = worldByName("world")
        }
        run {
            worldByName("nope")
        }
        run {
            worldDefault()
        }
        run {
            val o30 = IntArray(8)
            worldList(o30)
        }
        run {
            val o41 = ByteArray(128)
            worldName(w, o41)
        }
        run {
            val o51 = ByteArray(128)
            worldName(999, o51)
        }
        run {
            val o62 = ByteArray(128)
            worldGet(w, "time", o62)
        }
        run {
            val o72 = ByteArray(128)
            worldGet(w, "nope", o72)
        }
        run {
            val d82 = ValueWriter(512).int(1000L).finish() ?: ByteArray(0)
            worldSet(w, "day_time", d82)
        }
        run {
            val d92 = ValueWriter(512).int(1L).finish() ?: ByteArray(0)
            worldSet(w, "nope", d92)
        }
        run {
            val o102 = ByteArray(128)
            worldRuleGet(w, "doDaylightCycle", o102)
        }
        run {
            val o112 = ByteArray(128)
            worldRuleGet(w, "nope", o112)
        }
        run {
            worldRuleSet(w, "doDaylightCycle", "false")
        }
        run {
            rq = worldAt(w, 1.5, 1.5, ::done)
        }
        run {
            worldRequestCancel(rq)
        }
        run {
            worldAt(w, 2.5, 2.5, ::done)
        }
        run {
            worldBlockSet(w, 0, 64, 0, "minecraft:stone", 0)
        }
        run {
            worldBlockSet(w, 0, 64, 0, "nope:block", 0)
        }
        run {
            val o185 = ByteArray(128)
            worldBlockGet(w, 0, 64, 0, 0, o185)
        }
        run {
            worldStateGet(w, 0, 64, 0, 0)
        }
        run {
            worldStateSet(w, 1, 64, 0, 3, 0)
        }
        run {
            worldStateGet(999, 0, 64, 0, 0)
        }
        run {
            val o222 = IntArray(2)
            worldStatesRead(w, intArrayOf(0, 64, 0, 1, 64, 0), o222, 0)
        }
        run {
            worldStatesWrite(w, intArrayOf(2, 64, 0, 3, 64, 0), intArrayOf(5, 5), 0)
        }
        run {
            worldFill(w, 2, 64, 2, 5, 65, 5, 5, 0)
        }
        run {
            worldFill(999, 0, 0, 0, 1, 1, 1, 1, 0)
        }
        run {
            val o265 = IntArray(4096)
            worldSectionRead(w, 0, 4, 0, 0, o265)
        }
        run {
            worldSectionWrite(w, 0, 4, 0, IntArray(4096), 0)
        }
        run {
            worldSectionWrite(w, 0, 200, 0, IntArray(4096), 0)
        }
        run {
            val o294 = IntArray(8)
            worldChunkRead(w, 0, 0, 0, o294)
        }
        run {
            val o306 = IntArray(4096)
            worldChunkApply(w, 0, 0, intArrayOf(4), IntArray(4096), 0, o306)
        }
        run {
            worldChangesApply(w, intArrayOf(0, 64, 0), intArrayOf(5), 0)
        }
        run {
            worldBlocksSettle(w, intArrayOf(0, 64, 0), intArrayOf(0), 0)
        }
        run {
            worldBlocksNotify(w, intArrayOf(0, 64, 0), intArrayOf(1, 64, 0))
        }
        run {
            worldBlocksReshape(w, intArrayOf(0, 64, 0), 0)
        }
        run {
            worldStateValid(w, 0, 64, 0, 1)
        }
        run {
            worldTickSchedule(w, 0, 64, 0, 5, 0)
        }
        run {
            worldTickPending(w, 0, 64, 0)
        }
        run {
            worldStateSet(w, 3, 64, 0, 6, 0)
        }
        run {
            worldBlockEntityEdit(w, 1, 3, 64, 0, byteArrayOf(10, 0, 0, 0))
        }
        run {
            val o404 = ByteArray(256)
            worldBlockEntityGet(w, 3, 64, 0, o404)
        }
        run {
            val o414 = ByteArray(256)
            worldBlockEntityGet(w, 9, 64, 9, o414)
        }
        run {
            val o423 = IntArray(8)
            worldBlockEntities(w, 0, 0, o423)
        }
        run {
            worldDataSet(w, 0, 0, 0, 0, byteArrayOf(10, 0, 0, 0))
        }
        run {
            val o445 = ByteArray(256)
            worldDataGet(w, 0, 0, 0, 0, o445)
        }
        run {
            val o455 = ByteArray(256)
            worldDataGet(w, 1, 9, 0, 9, o455)
        }
        run {
            worldChunkStatus(w, 0, 0)
        }
        run {
            worldChunkStatus(w, 9, 9)
        }
        run {
            worldChunkLoad(w, 3, 3, 8)
        }
        run {
            worldChunkUnload(w, 3, 3)
        }
        run {
            worldChunkRequest(w, 4, 4, 8, 0, ::done)
        }
        run {
            val o511 = IntArray(16)
            worldChunksLoaded(w, o511)
        }
        run {
            worldChunkHold(w, "tour", intArrayOf(4, 4))
        }
        run {
            val o532 = IntArray(8)
            worldChunkHeld(w, "tour", o532)
        }
        run {
            worldChunkRelease(w, "tour", intArrayOf(4, 4))
        }
        run {
            worldChunksResend(w, intArrayOf(0, 0))
        }
        run {
            worldStoredPut(w, 7, 7, byteArrayOf(10, 0, 0, 0))
        }
        run {
            val o573 = ByteArray(256)
            worldStoredGet(w, 7, 7, o573)
        }
        run {
            val o583 = ByteArray(256)
            worldStoredGet(w, 8, 8, o583)
        }
        run {
            val o593 = IntArray(8)
            worldStoredList(w, 0, 0, o593)
        }
        run {
            worldLightAt(w, 0, 64, 0, 0)
        }
        run {
            val o616 = ByteArray(2048)
            worldLightRead(w, 0, 4, 0, 1, 1, o616)
        }
        run {
            worldLightWrite(w, 0, 4, 0, 1, ByteArray(2048))
        }
        run {
            worldLightWrite(w, 0, 4, 0, 1, byteArrayOf(10, 0, 0, 0))
        }
        run {
            worldLightCheck(w, intArrayOf(0, 0), ::done)
        }
        run {
            val o654 = ByteArray(128)
            worldBiomeGet(w, 0, 64, 0, o654)
        }
        run {
            worldBiomeSet(w, 0, 64, 0, "minecraft:desert")
        }
        run {
            worldBiomeSet(w, 0, 64, 0, "nope:biome")
        }
        run {
            val o683 = IntArray(8)
            worldBiomesRead(w, 0, 0, o683)
        }
        run {
            worldBiomesWrite(w, 0, 0, intArrayOf(1, 1, 1, 1))
        }
        run {
            worldSound(w, "minecraft:block.note_block.bell", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0L)
        }
        run {
            worldSound(w, "", "block", 0.5, 64.0, 0.5, 1.0f, 1.0f, 0L)
        }
        run {
            val d721 = ValueWriter(512).beginMap().key("type").string("minecraft:flame").key("x").float(0.5).key("y").float(65.0).key("z").float(0.5).key("count").int(3L).end().finish() ?: ByteArray(0)
            worldParticle(w, d721)
        }
        run {
            val d731 = ValueWriter(512).beginMap().key("count").int(1L).end().finish() ?: ByteArray(0)
            worldParticle(w, d731)
        }
        run {
            worldLevelEvent(w, 1000, 0, 64, 0, 0, 0)
        }
        run {
            worldBlockEvent(w, 0, 64, 0, 1, 2)
        }
        run {
            worldExplode(w, 0.5, 64.0, 0.5, 0.0f, 0, 0)
        }
        run {
            worldLightning(w, 0.5, 64.0, 0.5)
        }
        run {
            worldGameEvent(w, "minecraft:block_change", 0.5, 64.0, 0.5, "")
        }
        run {
            worldGameEvent(w, "", 0.5, 64.0, 0.5, "")
        }
        run {
            val d802 = ValueWriter(512).beginList().string("minecraft:block.note_block.bell").string("block").float(0.5).float(64.0).float(0.5).float(1.0).float(1.0).int(0L).end().finish() ?: ByteArray(0)
            val o803 = ByteArray(64)
            worldPlayerEffect(p, "sound_at", d802, o803)
        }
        run {
            val d812 = ValueWriter(512).beginList().end().finish() ?: ByteArray(0)
            val o813 = ByteArray(64)
            worldPlayerEffect(p, "nope", d812, o813)
        }
        run {
            val o821 = ByteArray(512)
            worldBorderGet(w, o821)
        }
        run {
            val d831 = ValueWriter(512).beginMap().key("size").float(100.0).end().finish() ?: ByteArray(0)
            worldBorderSet(w, d831)
        }
        run {
            val d841 = ValueWriter(512).beginMap().key("size").float(-1.0).key("seconds").float(-1.0).end().finish() ?: ByteArray(0)
            worldBorderSet(w, d841)
        }
        run {
            val d852 = ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o853 = ByteArray(1024)
            worldCall(w, "ops", d852, o853)
        }
        run {
            val d862 = ValueWriter(512).beginMap().key("x").int(2L).key("z").int(2L).end().finish() ?: ByteArray(0)
            val o863 = ByteArray(256)
            worldCall(w, "highest", d862, o863)
        }
        run {
            val d872 = ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o873 = ByteArray(256)
            worldCall(w, "nope", d872, o873)
        }
        run {
            s1n = structureCapture(w, 0, 64, 0, 3, 2, 3, 0, s1)
        }
        run {
            structurePut("tour:a", s1.copyOf(kl(s1n, 2048)), 0)
        }
        run {
            structurePut("tour:b", byteArrayOf(10, 0, 0, 0), 0)
        }
        run {
            val o910 = ByteArray(256)
            structureList(o910)
        }
        run {
            val o921 = ByteArray(256)
            structureGet("tour:a", o921)
        }
        run {
            val o931 = ByteArray(256)
            structureGet("tour:zzz", o931)
        }
        run {
            val o941 = ByteArray(128)
            structureFile("tour:a", o941)
        }
        run {
            val d951 = ValueWriter(512).beginMap().key("x").int(0L).key("y").int(64L).key("z").int(0L).end().finish() ?: ByteArray(0)
            val o952 = ByteArray(1024)
            structureScan(s1.copyOf(kl(s1n, 2048)), d951, o952)
        }
        run {
            val d961 = ValueWriter(512).beginMap().key("world").string("world").key("x").int(8L).key("y").int(64L).key("z").int(8L).end().finish() ?: ByteArray(0)
            structurePlace(s1.copyOf(kl(s1n, 2048)), d961)
        }
        run {
            structureRemove("tour:a", 0)
        }
        run {
            structureRemove("tour:a", 0)
        }
        run {
            val d990 = ValueWriter(512).beginMap().key("op").string("shape").key("settings").string("minecraft:overworld").key("seed").int(0L).end().finish() ?: ByteArray(0)
            val o991 = ByteArray(256)
            noiseQuery(d990, o991)
        }
        run {
            val d1000 = ValueWriter(512).beginMap().key("op").string("nope").end().finish() ?: ByteArray(0)
            val o1001 = ByteArray(256)
            noiseQuery(d1000, o1001)
        }
        run {
            val d1010 = ValueWriter(512).beginMap().key("id").string("minecraft:temperature").key("seed").int(0L).end().finish() ?: ByteArray(0)
            nz = noiseCreate(d1010)
        }
        run {
            val f1024 = DoubleArray(1)
            noiseSample(nz, 0.0, 0.0, 0.0, f1024)
        }
        run {
            val o1031 = ByteArray(128)
            noiseInfo(nz, o1031)
        }
        run {
            noiseRelease(nz)
        }
        run {
            noiseRelease(nz)
        }
        run {
            blockStateId("minecraft:stone")
        }
        run {
            blockStateId("nope:block")
        }
        run {
            val o1081 = ByteArray(128)
            blockStateText(1, o1081)
        }
        run {
            val d1091 = ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o1092 = ByteArray(1024)
            worldsData("tables", d1091, o1092)
        }
        run {
            val d1101 = ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o1102 = ByteArray(512)
            worldsData("blocks", d1101, o1102)
        }
        run {
            val d1111 = ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o1112 = ByteArray(64)
            worldsData("nope", d1111, o1112)
        }
        run {
            val d1121 = ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o1122 = ByteArray(128)
            datafixCall("types", d1121, o1122)
        }
        run {
            val d1131 = ValueWriter(512).beginMap().key("type").string("chunk").end().finish() ?: ByteArray(0)
            val o1132 = ByteArray(64)
            datafixCall("supports", d1131, o1132)
        }
        run {
            val d1141 = ValueWriter(512).beginMap().key("type").string("chunk").end().finish() ?: ByteArray(0)
            val o1142 = ByteArray(256)
            datafixCall("steps", d1141, o1142)
        }
        run {
            val d1151 = ValueWriter(512).beginMap().key("type").string("chunk").key("from").int(100L).key("to").int(200L).end().finish() ?: ByteArray(0)
            val o1152 = ByteArray(64)
            datafixCall("missing", d1151, o1152)
        }
        run {
            val d1161 = ValueWriter(512).beginMap().key("id").int(16L).end().finish() ?: ByteArray(0)
            val o1162 = ByteArray(128)
            datafixCall("legacy_state", d1161, o1162)
        }
        run {
            val d1171 = ValueWriter(512).beginMap().key("id").int(16L).end().finish() ?: ByteArray(0)
            val o1172 = ByteArray(128)
            datafixCall("modern_state", d1171, o1172)
        }
        run {
            val d1181 = ValueWriter(512).beginMap().key("type").string("chunk").key("nbt").bytes(byteArrayOf(10, 0, 0, 0)).key("from").int(100L).key("to").int(200L).end().finish() ?: ByteArray(0)
            val o1182 = ByteArray(128)
            datafixCall("upgrade", d1181, o1182)
        }
        run {
            val d1191 = ValueWriter(512).beginMap().end().finish() ?: ByteArray(0)
            val o1192 = ByteArray(64)
            datafixCall("nope", d1191, o1192)
        }
        run {
            worldAt(w, 3.5, 3.5, ::later)
        }
        if (w + p + w2 + rq + nz < 0) logDebug("unreachable")
    }

    fun register() {
        command("worlds", "", ::tour)
    }
}
