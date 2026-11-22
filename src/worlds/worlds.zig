const basalt = @import("basalt_server");
const worlds = basalt.worlds;
const values = basalt.values;

fn kl(length: i32, capacity: i32) usize {
    return if (length > 0 and length <= capacity) @intCast(length) else 0;
}

fn done(ctx: basalt.core.Ctx) void {
    var out: [16]u8 = @splat(0);
    _ = worlds.worldValueResult(ctx, &out);
}

fn later2(_: basalt.core.Ctx) void {
    var w2: i32 = 0;
    {
        var dbuf20000: [512]u8 = undefined;
        var d20000 = values.Writer.init(&dbuf20000);
        d20000.beginMap();
        d20000.key("name");
        d20000.string("tour");
        d20000.end();
        const d20000d = d20000.finish() catch dbuf20000[0..0];
        _ = worlds.worldCreate(d20000d);
    }
    {
        _ = worlds.worldUnload("nope", 0);
    }
    {
        _ = worlds.worldUnload("tour", 0);
    }
    {
        _ = worlds.worldDelete("tour");
    }
    _ = &w2;
}

fn later(_: basalt.core.Ctx) void {
    var w2: i32 = 0;
    {
        var dbuf10000: [512]u8 = undefined;
        var d10000 = values.Writer.init(&dbuf10000);
        d10000.beginMap();
        d10000.key("name");
        d10000.string("tour");
        d10000.end();
        const d10000d = d10000.finish() catch dbuf10000[0..0];
        w2 = worlds.worldCreate(d10000d);
    }
    {
        _ = worlds.worldAt(w2, 0.5, 0.5, later2);
    }
    _ = &w2;
}

fn tour(_: basalt.core.Ctx) void {
    _ = worlds.chunkCoord(-17);
    _ = worlds.blockDistance(0, 0, 0, 3, 4, 0);
    var pos = [3]i32{ 1, 2, 3 };
    worlds.posOffset(&pos, 1, 1, 1);
    var w: i32 = 0;
    var p: i32 = basalt.players.byName("Steve");
    var w2: i32 = 0;
    var rq: i32 = 0;
    var nz: i32 = 0;
    var s1: [2048]u8 = @splat(0);
    var s1n: i32 = 0;
    {
        w = worlds.worldByName("world");
    }
    {
        _ = worlds.worldByName("nope");
    }
    {
        _ = worlds.worldDefault();
    }
    {
        var o30: [8]i32 = @splat(0);
        _ = worlds.worldList(&o30);
    }
    {
        var o41: [128]u8 = @splat(0);
        _ = worlds.worldName(w, &o41);
    }
    {
        var o51: [128]u8 = @splat(0);
        _ = worlds.worldName(999, &o51);
    }
    {
        var o62: [128]u8 = @splat(0);
        _ = worlds.worldGet(w, "time", &o62);
    }
    {
        var o72: [128]u8 = @splat(0);
        _ = worlds.worldGet(w, "nope", &o72);
    }
    {
        var dbuf82: [512]u8 = undefined;
        var d82 = values.Writer.init(&dbuf82);
        d82.int(1000);
        const d82d = d82.finish() catch dbuf82[0..0];
        _ = worlds.worldSet(w, "day_time", d82d);
    }
    {
        var dbuf92: [512]u8 = undefined;
        var d92 = values.Writer.init(&dbuf92);
        d92.int(1);
        const d92d = d92.finish() catch dbuf92[0..0];
        _ = worlds.worldSet(w, "nope", d92d);
    }
    {
        var o102: [128]u8 = @splat(0);
        _ = worlds.worldRuleGet(w, "doDaylightCycle", &o102);
    }
    {
        var o112: [128]u8 = @splat(0);
        _ = worlds.worldRuleGet(w, "nope", &o112);
    }
    {
        _ = worlds.worldRuleSet(w, "doDaylightCycle", "false");
    }
    {
        rq = worlds.worldAt(w, 1.5, 1.5, done);
    }
    {
        _ = worlds.worldRequestCancel(rq);
    }
    {
        _ = worlds.worldAt(w, 2.5, 2.5, done);
    }
    {
        _ = worlds.worldBlockSet(w, 0, 64, 0, "minecraft:stone", 0);
    }
    {
        _ = worlds.worldBlockSet(w, 0, 64, 0, "nope:block", 0);
    }
    {
        var o185: [128]u8 = @splat(0);
        _ = worlds.worldBlockGet(w, 0, 64, 0, 0, &o185);
    }
    {
        _ = worlds.worldStateGet(w, 0, 64, 0, 0);
    }
    {
        _ = worlds.worldStateSet(w, 1, 64, 0, 3, 0);
    }
    {
        _ = worlds.worldStateGet(999, 0, 64, 0, 0);
    }
    {
        var o222: [2]i32 = @splat(0);
        _ = worlds.worldStatesRead(w, &[_]i32{0, 64, 0, 1, 64, 0}, &o222, 0);
    }
    {
        _ = worlds.worldStatesWrite(w, &[_]i32{2, 64, 0, 3, 64, 0}, &[_]i32{5, 5}, 0);
    }
    {
        _ = worlds.worldFill(w, 2, 64, 2, 5, 65, 5, 5, 0);
    }
    {
        _ = worlds.worldFill(999, 0, 0, 0, 1, 1, 1, 1, 0);
    }
    {
        var o265: [4096]i32 = @splat(0);
        _ = worlds.worldSectionRead(w, 0, 4, 0, 0, &o265);
    }
    {
        const ia274: [4096]i32 = @splat(0);
        _ = worlds.worldSectionWrite(w, 0, 4, 0, &ia274, 0);
    }
    {
        const ia284: [4096]i32 = @splat(0);
        _ = worlds.worldSectionWrite(w, 0, 200, 0, &ia284, 0);
    }
    {
        var o294: [8]i32 = @splat(0);
        _ = worlds.worldChunkRead(w, 0, 0, 0, &o294);
    }
    {
        const ia304: [4096]i32 = @splat(0);
        var o306: [4096]i32 = @splat(0);
        _ = worlds.worldChunkApply(w, 0, 0, &[_]i32{4}, &ia304, 0, &o306);
    }
    {
        _ = worlds.worldChangesApply(w, &[_]i32{0, 64, 0}, &[_]i32{5}, 0);
    }
    {
        _ = worlds.worldBlocksSettle(w, &[_]i32{0, 64, 0}, &[_]i32{0}, 0);
    }
    {
        _ = worlds.worldBlocksNotify(w, &[_]i32{0, 64, 0}, &[_]i32{1, 64, 0});
    }
    {
        _ = worlds.worldBlocksReshape(w, &[_]i32{0, 64, 0}, 0);
    }
    {
        _ = worlds.worldStateValid(w, 0, 64, 0, 1);
    }
    {
        _ = worlds.worldTickSchedule(w, 0, 64, 0, 5, 0);
    }
    {
        _ = worlds.worldTickPending(w, 0, 64, 0);
    }
    {
        _ = worlds.worldStateSet(w, 3, 64, 0, 6, 0);
    }
    {
        _ = worlds.worldBlockEntityEdit(w, 1, 3, 64, 0, &[_]u8{10, 0, 0, 0});
    }
    {
        var o404: [256]u8 = @splat(0);
        _ = worlds.worldBlockEntityGet(w, 3, 64, 0, &o404);
    }
    {
        var o414: [256]u8 = @splat(0);
        _ = worlds.worldBlockEntityGet(w, 9, 64, 9, &o414);
    }
    {
        var o423: [8]i32 = @splat(0);
        _ = worlds.worldBlockEntities(w, 0, 0, &o423);
    }
    {
        _ = worlds.worldDataSet(w, 0, 0, 0, 0, &[_]u8{10, 0, 0, 0});
    }
    {
        var o445: [256]u8 = @splat(0);
        _ = worlds.worldDataGet(w, 0, 0, 0, 0, &o445);
    }
    {
        var o455: [256]u8 = @splat(0);
        _ = worlds.worldDataGet(w, 1, 9, 0, 9, &o455);
    }
    {
        _ = worlds.worldChunkStatus(w, 0, 0);
    }
    {
        _ = worlds.worldChunkStatus(w, 9, 9);
    }
    {
        _ = worlds.worldChunkLoad(w, 3, 3, 8);
    }
    {
        _ = worlds.worldChunkUnload(w, 3, 3);
    }
    {
        _ = worlds.worldChunkRequest(w, 4, 4, 8, 0, done);
    }
    {
        var o511: [16]i32 = @splat(0);
        _ = worlds.worldChunksLoaded(w, &o511);
    }
    {
        _ = worlds.worldChunkHold(w, "tour", &[_]i32{4, 4});
    }
    {
        var o532: [8]i32 = @splat(0);
        _ = worlds.worldChunkHeld(w, "tour", &o532);
    }
    {
        _ = worlds.worldChunkRelease(w, "tour", &[_]i32{4, 4});
    }
    {
        _ = worlds.worldChunksResend(w, &[_]i32{0, 0});
    }
    {
        _ = worlds.worldStoredPut(w, 7, 7, &[_]u8{10, 0, 0, 0});
    }
    {
        var o573: [256]u8 = @splat(0);
        _ = worlds.worldStoredGet(w, 7, 7, &o573);
    }
    {
        var o583: [256]u8 = @splat(0);
        _ = worlds.worldStoredGet(w, 8, 8, &o583);
    }
    {
        var o593: [8]i32 = @splat(0);
        _ = worlds.worldStoredList(w, 0, 0, &o593);
    }
    {
        _ = worlds.worldLightAt(w, 0, 64, 0, 0);
    }
    {
        var o616: [2048]u8 = @splat(0);
        _ = worlds.worldLightRead(w, 0, 4, 0, 1, 1, &o616);
    }
    {
        const z625: [2048]u8 = @splat(0);
        _ = worlds.worldLightWrite(w, 0, 4, 0, 1, &z625);
    }
    {
        _ = worlds.worldLightWrite(w, 0, 4, 0, 1, &[_]u8{10, 0, 0, 0});
    }
    {
        _ = worlds.worldLightCheck(w, &[_]i32{0, 0}, done);
    }
    {
        var o654: [128]u8 = @splat(0);
        _ = worlds.worldBiomeGet(w, 0, 64, 0, &o654);
    }
    {
        _ = worlds.worldBiomeSet(w, 0, 64, 0, "minecraft:desert");
    }
    {
        _ = worlds.worldBiomeSet(w, 0, 64, 0, "nope:biome");
    }
    {
        var o683: [8]i32 = @splat(0);
        _ = worlds.worldBiomesRead(w, 0, 0, &o683);
    }
    {
        _ = worlds.worldBiomesWrite(w, 0, 0, &[_]i32{1, 1, 1, 1});
    }
    {
        _ = worlds.worldSound(w, "minecraft:block.note_block.bell", "block", 0.5, 64.0, 0.5, 1.0, 1.0, 0);
    }
    {
        _ = worlds.worldSound(w, "", "block", 0.5, 64.0, 0.5, 1.0, 1.0, 0);
    }
    {
        var dbuf721: [512]u8 = undefined;
        var d721 = values.Writer.init(&dbuf721);
        d721.beginMap();
        d721.key("type");
        d721.string("minecraft:flame");
        d721.key("x");
        d721.float(0.5);
        d721.key("y");
        d721.float(65.0);
        d721.key("z");
        d721.float(0.5);
        d721.key("count");
        d721.int(3);
        d721.end();
        const d721d = d721.finish() catch dbuf721[0..0];
        _ = worlds.worldParticle(w, d721d);
    }
    {
        var dbuf731: [512]u8 = undefined;
        var d731 = values.Writer.init(&dbuf731);
        d731.beginMap();
        d731.key("count");
        d731.int(1);
        d731.end();
        const d731d = d731.finish() catch dbuf731[0..0];
        _ = worlds.worldParticle(w, d731d);
    }
    {
        _ = worlds.worldLevelEvent(w, 1000, 0, 64, 0, 0, 0);
    }
    {
        _ = worlds.worldBlockEvent(w, 0, 64, 0, 1, 2);
    }
    {
        _ = worlds.worldExplode(w, 0.5, 64.0, 0.5, 0.0, 0, 0);
    }
    {
        _ = worlds.worldLightning(w, 0.5, 64.0, 0.5);
    }
    {
        _ = worlds.worldGameEvent(w, "minecraft:block_change", 0.5, 64.0, 0.5, "");
    }
    {
        _ = worlds.worldGameEvent(w, "", 0.5, 64.0, 0.5, "");
    }
    {
        var dbuf802: [512]u8 = undefined;
        var d802 = values.Writer.init(&dbuf802);
        d802.beginList();
        d802.string("minecraft:block.note_block.bell");
        d802.string("block");
        d802.float(0.5);
        d802.float(64.0);
        d802.float(0.5);
        d802.float(1.0);
        d802.float(1.0);
        d802.int(0);
        d802.end();
        const d802d = d802.finish() catch dbuf802[0..0];
        var o803: [64]u8 = @splat(0);
        _ = worlds.worldPlayerEffect(p, "sound_at", d802d, &o803);
    }
    {
        var dbuf812: [512]u8 = undefined;
        var d812 = values.Writer.init(&dbuf812);
        d812.beginList();
        d812.end();
        const d812d = d812.finish() catch dbuf812[0..0];
        var o813: [64]u8 = @splat(0);
        _ = worlds.worldPlayerEffect(p, "nope", d812d, &o813);
    }
    {
        var o821: [512]u8 = @splat(0);
        _ = worlds.worldBorderGet(w, &o821);
    }
    {
        var dbuf831: [512]u8 = undefined;
        var d831 = values.Writer.init(&dbuf831);
        d831.beginMap();
        d831.key("size");
        d831.float(100.0);
        d831.end();
        const d831d = d831.finish() catch dbuf831[0..0];
        _ = worlds.worldBorderSet(w, d831d);
    }
    {
        var dbuf841: [512]u8 = undefined;
        var d841 = values.Writer.init(&dbuf841);
        d841.beginMap();
        d841.key("size");
        d841.float(-1.0);
        d841.key("seconds");
        d841.float(-1.0);
        d841.end();
        const d841d = d841.finish() catch dbuf841[0..0];
        _ = worlds.worldBorderSet(w, d841d);
    }
    {
        var dbuf852: [512]u8 = undefined;
        var d852 = values.Writer.init(&dbuf852);
        d852.beginMap();
        d852.end();
        const d852d = d852.finish() catch dbuf852[0..0];
        var o853: [1024]u8 = @splat(0);
        _ = worlds.worldCall(w, "ops", d852d, &o853);
    }
    {
        var dbuf862: [512]u8 = undefined;
        var d862 = values.Writer.init(&dbuf862);
        d862.beginMap();
        d862.key("x");
        d862.int(2);
        d862.key("z");
        d862.int(2);
        d862.end();
        const d862d = d862.finish() catch dbuf862[0..0];
        var o863: [256]u8 = @splat(0);
        _ = worlds.worldCall(w, "highest", d862d, &o863);
    }
    {
        var dbuf872: [512]u8 = undefined;
        var d872 = values.Writer.init(&dbuf872);
        d872.beginMap();
        d872.end();
        const d872d = d872.finish() catch dbuf872[0..0];
        var o873: [256]u8 = @splat(0);
        _ = worlds.worldCall(w, "nope", d872d, &o873);
    }
    {
        s1n = worlds.structureCapture(w, 0, 64, 0, 3, 2, 3, 0, &s1);
    }
    {
        _ = worlds.structurePut("tour:a", s1[0..kl(s1n, 2048)], 0);
    }
    {
        _ = worlds.structurePut("tour:b", &[_]u8{10, 0, 0, 0}, 0);
    }
    {
        var o910: [256]u8 = @splat(0);
        _ = worlds.structureList(&o910);
    }
    {
        var o921: [256]u8 = @splat(0);
        _ = worlds.structureGet("tour:a", &o921);
    }
    {
        var o931: [256]u8 = @splat(0);
        _ = worlds.structureGet("tour:zzz", &o931);
    }
    {
        var o941: [128]u8 = @splat(0);
        _ = worlds.structureFile("tour:a", &o941);
    }
    {
        var dbuf951: [512]u8 = undefined;
        var d951 = values.Writer.init(&dbuf951);
        d951.beginMap();
        d951.key("x");
        d951.int(0);
        d951.key("y");
        d951.int(64);
        d951.key("z");
        d951.int(0);
        d951.end();
        const d951d = d951.finish() catch dbuf951[0..0];
        var o952: [1024]u8 = @splat(0);
        _ = worlds.structureScan(s1[0..kl(s1n, 2048)], d951d, &o952);
    }
    {
        var dbuf961: [512]u8 = undefined;
        var d961 = values.Writer.init(&dbuf961);
        d961.beginMap();
        d961.key("world");
        d961.string("world");
        d961.key("x");
        d961.int(8);
        d961.key("y");
        d961.int(64);
        d961.key("z");
        d961.int(8);
        d961.end();
        const d961d = d961.finish() catch dbuf961[0..0];
        _ = worlds.structurePlace(s1[0..kl(s1n, 2048)], d961d);
    }
    {
        _ = worlds.structureRemove("tour:a", 0);
    }
    {
        _ = worlds.structureRemove("tour:a", 0);
    }
    {
        var dbuf990: [512]u8 = undefined;
        var d990 = values.Writer.init(&dbuf990);
        d990.beginMap();
        d990.key("op");
        d990.string("shape");
        d990.key("settings");
        d990.string("minecraft:overworld");
        d990.key("seed");
        d990.int(0);
        d990.end();
        const d990d = d990.finish() catch dbuf990[0..0];
        var o991: [256]u8 = @splat(0);
        _ = worlds.noiseQuery(d990d, &o991);
    }
    {
        var dbuf1000: [512]u8 = undefined;
        var d1000 = values.Writer.init(&dbuf1000);
        d1000.beginMap();
        d1000.key("op");
        d1000.string("nope");
        d1000.end();
        const d1000d = d1000.finish() catch dbuf1000[0..0];
        var o1001: [256]u8 = @splat(0);
        _ = worlds.noiseQuery(d1000d, &o1001);
    }
    {
        var dbuf1010: [512]u8 = undefined;
        var d1010 = values.Writer.init(&dbuf1010);
        d1010.beginMap();
        d1010.key("id");
        d1010.string("minecraft:temperature");
        d1010.key("seed");
        d1010.int(0);
        d1010.end();
        const d1010d = d1010.finish() catch dbuf1010[0..0];
        nz = worlds.noiseCreate(d1010d);
    }
    {
        var fl1024: f64 = 0;
        _ = worlds.noiseSample(nz, 0.0, 0.0, 0.0, &fl1024);
    }
    {
        var o1031: [128]u8 = @splat(0);
        _ = worlds.noiseInfo(nz, &o1031);
    }
    {
        _ = worlds.noiseRelease(nz);
    }
    {
        _ = worlds.noiseRelease(nz);
    }
    {
        _ = worlds.blockStateId("minecraft:stone");
    }
    {
        _ = worlds.blockStateId("nope:block");
    }
    {
        var o1081: [128]u8 = @splat(0);
        _ = worlds.blockStateText(1, &o1081);
    }
    {
        var dbuf1091: [512]u8 = undefined;
        var d1091 = values.Writer.init(&dbuf1091);
        d1091.beginMap();
        d1091.end();
        const d1091d = d1091.finish() catch dbuf1091[0..0];
        var o1092: [1024]u8 = @splat(0);
        _ = worlds.worldsData("tables", d1091d, &o1092);
    }
    {
        var dbuf1101: [512]u8 = undefined;
        var d1101 = values.Writer.init(&dbuf1101);
        d1101.beginMap();
        d1101.end();
        const d1101d = d1101.finish() catch dbuf1101[0..0];
        var o1102: [512]u8 = @splat(0);
        _ = worlds.worldsData("blocks", d1101d, &o1102);
    }
    {
        var dbuf1111: [512]u8 = undefined;
        var d1111 = values.Writer.init(&dbuf1111);
        d1111.beginMap();
        d1111.end();
        const d1111d = d1111.finish() catch dbuf1111[0..0];
        var o1112: [64]u8 = @splat(0);
        _ = worlds.worldsData("nope", d1111d, &o1112);
    }
    {
        var dbuf1121: [512]u8 = undefined;
        var d1121 = values.Writer.init(&dbuf1121);
        d1121.beginMap();
        d1121.end();
        const d1121d = d1121.finish() catch dbuf1121[0..0];
        var o1122: [128]u8 = @splat(0);
        _ = worlds.datafixCall("types", d1121d, &o1122);
    }
    {
        var dbuf1131: [512]u8 = undefined;
        var d1131 = values.Writer.init(&dbuf1131);
        d1131.beginMap();
        d1131.key("type");
        d1131.string("chunk");
        d1131.end();
        const d1131d = d1131.finish() catch dbuf1131[0..0];
        var o1132: [64]u8 = @splat(0);
        _ = worlds.datafixCall("supports", d1131d, &o1132);
    }
    {
        var dbuf1141: [512]u8 = undefined;
        var d1141 = values.Writer.init(&dbuf1141);
        d1141.beginMap();
        d1141.key("type");
        d1141.string("chunk");
        d1141.end();
        const d1141d = d1141.finish() catch dbuf1141[0..0];
        var o1142: [256]u8 = @splat(0);
        _ = worlds.datafixCall("steps", d1141d, &o1142);
    }
    {
        var dbuf1151: [512]u8 = undefined;
        var d1151 = values.Writer.init(&dbuf1151);
        d1151.beginMap();
        d1151.key("type");
        d1151.string("chunk");
        d1151.key("from");
        d1151.int(100);
        d1151.key("to");
        d1151.int(200);
        d1151.end();
        const d1151d = d1151.finish() catch dbuf1151[0..0];
        var o1152: [64]u8 = @splat(0);
        _ = worlds.datafixCall("missing", d1151d, &o1152);
    }
    {
        var dbuf1161: [512]u8 = undefined;
        var d1161 = values.Writer.init(&dbuf1161);
        d1161.beginMap();
        d1161.key("id");
        d1161.int(16);
        d1161.end();
        const d1161d = d1161.finish() catch dbuf1161[0..0];
        var o1162: [128]u8 = @splat(0);
        _ = worlds.datafixCall("legacy_state", d1161d, &o1162);
    }
    {
        var dbuf1171: [512]u8 = undefined;
        var d1171 = values.Writer.init(&dbuf1171);
        d1171.beginMap();
        d1171.key("id");
        d1171.int(16);
        d1171.end();
        const d1171d = d1171.finish() catch dbuf1171[0..0];
        var o1172: [128]u8 = @splat(0);
        _ = worlds.datafixCall("modern_state", d1171d, &o1172);
    }
    {
        var dbuf1181: [512]u8 = undefined;
        var d1181 = values.Writer.init(&dbuf1181);
        d1181.beginMap();
        d1181.key("type");
        d1181.string("chunk");
        d1181.key("nbt");
        d1181.bytes(&[_]u8{10, 0, 0, 0});
        d1181.key("from");
        d1181.int(100);
        d1181.key("to");
        d1181.int(200);
        d1181.end();
        const d1181d = d1181.finish() catch dbuf1181[0..0];
        var o1182: [128]u8 = @splat(0);
        _ = worlds.datafixCall("upgrade", d1181d, &o1182);
    }
    {
        var dbuf1191: [512]u8 = undefined;
        var d1191 = values.Writer.init(&dbuf1191);
        d1191.beginMap();
        d1191.end();
        const d1191d = d1191.finish() catch dbuf1191[0..0];
        var o1192: [64]u8 = @splat(0);
        _ = worlds.datafixCall("nope", d1191d, &o1192);
    }
    {
        _ = worlds.worldAt(w, 3.5, 3.5, later);
    }
    _ = &w;
    _ = &p;
    _ = &w2;
    _ = &rq;
    _ = &nz;
    _ = &s1n;
    _ = &s1;
}

pub fn register() void {
    _ = basalt.commands.register("worlds", "", tour);
}
