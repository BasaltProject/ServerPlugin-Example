import * as bsCommands from "basalt-server/commands";
import * as bsCore from "basalt-server/core";
import * as bsLog from "basalt-server/log";
import * as bsPlayers from "basalt-server/players";
import * as bsValues from "basalt-server/values";
import * as bsWorlds from "basalt-server/worlds";

function kl(length: i32, capacity: i32): i32 {
  return length > 0 && length <= capacity ? length : 0;
}

function bytesOf(values: i32[]): Uint8Array {
  const out = new Uint8Array(values.length);
  for (let i = 0; i < values.length; i++) out[i] = <u8>values[i];
  return out;
}

function i32s(values: i32[]): Int32Array {
  const out = new Int32Array(values.length);
  for (let i = 0; i < values.length; i++) out[i] = values[i];
  return out;
}

function done(ctx: bsCore.Ctx): void {
  bsWorlds.worldValueResult(ctx, new Uint8Array(16));
}

function later2(ctx: bsCore.Ctx): void {
  let w2: i32 = 0;
  {
    const d20000 = new bsValues.ValueWriter(512).beginMap().key("name").string("tour").end().finish()!;
    bsWorlds.worldCreate(d20000);
  }
  {
    bsWorlds.worldUnload("nope", 0);
  }
  {
    bsWorlds.worldUnload("tour", 0);
  }
  {
    bsWorlds.worldDelete("tour");
  }
  if (w2 < 0) bsLog.logDebug("unreachable");
}

function later(ctx: bsCore.Ctx): void {
  let w2: i32 = 0;
  {
    const d10000 = new bsValues.ValueWriter(512).beginMap().key("name").string("tour").end().finish()!;
    w2 = bsWorlds.worldCreate(d10000);
  }
  {
    bsWorlds.worldAt(w2, 0.5, 0.5, later2);
  }
  if (w2 < 0) bsLog.logDebug("unreachable");
}

function tour(ctx: bsCore.Ctx): void {
  bsWorlds.chunkCoord(-17);
  bsWorlds.blockDistance(0, 0, 0, 3, 4, 0);
  const pos = i32s([1, 2, 3]);
  bsWorlds.posOffset(pos, 1, 1, 1);
  let w: i32 = 0;
  const p: i32 = bsPlayers.playerByName("Steve");
  let w2: i32 = 0;
  let rq: i32 = 0;
  let nz: i32 = 0;
  const s1 = new Uint8Array(2048);
  let s1n: i32 = 0;
  {
    w = bsWorlds.worldByName("world");
  }
  {
    bsWorlds.worldByName("nope");
  }
  {
    bsWorlds.worldDefault();
  }
  {
    const o30 = new Int32Array(8);
    bsWorlds.worldList(o30);
  }
  {
    const o41 = new Uint8Array(128);
    bsWorlds.worldName(w, o41);
  }
  {
    const o51 = new Uint8Array(128);
    bsWorlds.worldName(999, o51);
  }
  {
    const o62 = new Uint8Array(128);
    bsWorlds.worldGet(w, "time", o62);
  }
  {
    const o72 = new Uint8Array(128);
    bsWorlds.worldGet(w, "nope", o72);
  }
  {
    const d82 = new bsValues.ValueWriter(512).int(1000).finish()!;
    bsWorlds.worldSet(w, "day_time", d82);
  }
  {
    const d92 = new bsValues.ValueWriter(512).int(1).finish()!;
    bsWorlds.worldSet(w, "nope", d92);
  }
  {
    const o102 = new Uint8Array(128);
    bsWorlds.worldRuleGet(w, "doDaylightCycle", o102);
  }
  {
    const o112 = new Uint8Array(128);
    bsWorlds.worldRuleGet(w, "nope", o112);
  }
  {
    bsWorlds.worldRuleSet(w, "doDaylightCycle", "false");
  }
  {
    rq = bsWorlds.worldAt(w, 1.5, 1.5, done);
  }
  {
    bsWorlds.worldRequestCancel(rq);
  }
  {
    bsWorlds.worldAt(w, 2.5, 2.5, done);
  }
  {
    bsWorlds.worldBlockSet(w, 0, 64, 0, "minecraft:stone", 0);
  }
  {
    bsWorlds.worldBlockSet(w, 0, 64, 0, "nope:block", 0);
  }
  {
    const o185 = new Uint8Array(128);
    bsWorlds.worldBlockGet(w, 0, 64, 0, 0, o185);
  }
  {
    bsWorlds.worldStateGet(w, 0, 64, 0, 0);
  }
  {
    bsWorlds.worldStateSet(w, 1, 64, 0, 3, 0);
  }
  {
    bsWorlds.worldStateGet(999, 0, 64, 0, 0);
  }
  {
    const o222 = new Int32Array(2);
    bsWorlds.worldStatesRead(w, i32s([0, 64, 0, 1, 64, 0]), o222, 0);
  }
  {
    bsWorlds.worldStatesWrite(w, i32s([2, 64, 0, 3, 64, 0]), i32s([5, 5]), 0);
  }
  {
    bsWorlds.worldFill(w, 2, 64, 2, 5, 65, 5, 5, 0);
  }
  {
    bsWorlds.worldFill(999, 0, 0, 0, 1, 1, 1, 1, 0);
  }
  {
    const o265 = new Int32Array(4096);
    bsWorlds.worldSectionRead(w, 0, 4, 0, 0, o265);
  }
  {
    bsWorlds.worldSectionWrite(w, 0, 4, 0, new Int32Array(4096), 0);
  }
  {
    bsWorlds.worldSectionWrite(w, 0, 200, 0, new Int32Array(4096), 0);
  }
  {
    const o294 = new Int32Array(8);
    bsWorlds.worldChunkRead(w, 0, 0, 0, o294);
  }
  {
    const o306 = new Int32Array(4096);
    bsWorlds.worldChunkApply(w, 0, 0, i32s([4]), new Int32Array(4096), 0, o306);
  }
  {
    bsWorlds.worldChangesApply(w, i32s([0, 64, 0]), i32s([5]), 0);
  }
  {
    bsWorlds.worldBlocksSettle(w, i32s([0, 64, 0]), i32s([0]), 0);
  }
  {
    bsWorlds.worldBlocksNotify(w, i32s([0, 64, 0]), i32s([1, 64, 0]));
  }
  {
    bsWorlds.worldBlocksReshape(w, i32s([0, 64, 0]), 0);
  }
  {
    bsWorlds.worldStateValid(w, 0, 64, 0, 1);
  }
  {
    bsWorlds.worldTickSchedule(w, 0, 64, 0, 5, 0);
  }
  {
    bsWorlds.worldTickPending(w, 0, 64, 0);
  }
  {
    bsWorlds.worldStateSet(w, 3, 64, 0, 6, 0);
  }
  {
    bsWorlds.worldBlockEntityEdit(w, 1, 3, 64, 0, bytesOf([10, 0, 0, 0]));
  }
  {
    const o404 = new Uint8Array(256);
    bsWorlds.worldBlockEntityGet(w, 3, 64, 0, o404);
  }
  {
    const o414 = new Uint8Array(256);
    bsWorlds.worldBlockEntityGet(w, 9, 64, 9, o414);
  }
  {
    const o423 = new Int32Array(8);
    bsWorlds.worldBlockEntities(w, 0, 0, o423);
  }
  {
    bsWorlds.worldDataSet(w, 0, 0, 0, 0, bytesOf([10, 0, 0, 0]));
  }
  {
    const o445 = new Uint8Array(256);
    bsWorlds.worldDataGet(w, 0, 0, 0, 0, o445);
  }
  {
    const o455 = new Uint8Array(256);
    bsWorlds.worldDataGet(w, 1, 9, 0, 9, o455);
  }
  {
    bsWorlds.worldChunkStatus(w, 0, 0);
  }
  {
    bsWorlds.worldChunkStatus(w, 9, 9);
  }
  {
    bsWorlds.worldChunkLoad(w, 3, 3, 8);
  }
  {
    bsWorlds.worldChunkUnload(w, 3, 3);
  }
  {
    bsWorlds.worldChunkRequest(w, 4, 4, 8, 0, done);
  }
  {
    const o511 = new Int32Array(16);
    bsWorlds.worldChunksLoaded(w, o511);
  }
  {
    bsWorlds.worldChunkHold(w, "tour", i32s([4, 4]));
  }
  {
    const o532 = new Int32Array(8);
    bsWorlds.worldChunkHeld(w, "tour", o532);
  }
  {
    bsWorlds.worldChunkRelease(w, "tour", i32s([4, 4]));
  }
  {
    bsWorlds.worldChunksResend(w, i32s([0, 0]));
  }
  {
    bsWorlds.worldStoredPut(w, 7, 7, bytesOf([10, 0, 0, 0]));
  }
  {
    const o573 = new Uint8Array(256);
    bsWorlds.worldStoredGet(w, 7, 7, o573);
  }
  {
    const o583 = new Uint8Array(256);
    bsWorlds.worldStoredGet(w, 8, 8, o583);
  }
  {
    const o593 = new Int32Array(8);
    bsWorlds.worldStoredList(w, 0, 0, o593);
  }
  {
    bsWorlds.worldLightAt(w, 0, 64, 0, 0);
  }
  {
    const o616 = new Uint8Array(2048);
    bsWorlds.worldLightRead(w, 0, 4, 0, 1, 1, o616);
  }
  {
    bsWorlds.worldLightWrite(w, 0, 4, 0, 1, new Uint8Array(2048));
  }
  {
    bsWorlds.worldLightWrite(w, 0, 4, 0, 1, bytesOf([10, 0, 0, 0]));
  }
  {
    bsWorlds.worldLightCheck(w, i32s([0, 0]), done);
  }
  {
    const o654 = new Uint8Array(128);
    bsWorlds.worldBiomeGet(w, 0, 64, 0, o654);
  }
  {
    bsWorlds.worldBiomeSet(w, 0, 64, 0, "minecraft:desert");
  }
  {
    bsWorlds.worldBiomeSet(w, 0, 64, 0, "nope:biome");
  }
  {
    const o683 = new Int32Array(8);
    bsWorlds.worldBiomesRead(w, 0, 0, o683);
  }
  {
    bsWorlds.worldBiomesWrite(w, 0, 0, i32s([1, 1, 1, 1]));
  }
  {
    bsWorlds.worldSound(w, "minecraft:block.note_block.bell", "block", 0.5, 64.0, 0.5, 1.0, 1.0, 0);
  }
  {
    bsWorlds.worldSound(w, "", "block", 0.5, 64.0, 0.5, 1.0, 1.0, 0);
  }
  {
    const d721 = new bsValues.ValueWriter(512).beginMap().key("type").string("minecraft:flame").key("x").float(0.5).key("y").float(65.0).key("z").float(0.5).key("count").int(3).end().finish()!;
    bsWorlds.worldParticle(w, d721);
  }
  {
    const d731 = new bsValues.ValueWriter(512).beginMap().key("count").int(1).end().finish()!;
    bsWorlds.worldParticle(w, d731);
  }
  {
    bsWorlds.worldLevelEvent(w, 1000, 0, 64, 0, 0, 0);
  }
  {
    bsWorlds.worldBlockEvent(w, 0, 64, 0, 1, 2);
  }
  {
    bsWorlds.worldExplode(w, 0.5, 64.0, 0.5, 0.0, 0, 0);
  }
  {
    bsWorlds.worldLightning(w, 0.5, 64.0, 0.5);
  }
  {
    bsWorlds.worldGameEvent(w, "minecraft:block_change", 0.5, 64.0, 0.5, "");
  }
  {
    bsWorlds.worldGameEvent(w, "", 0.5, 64.0, 0.5, "");
  }
  {
    const d802 = new bsValues.ValueWriter(512).beginList().string("minecraft:block.note_block.bell").string("block").float(0.5).float(64.0).float(0.5).float(1.0).float(1.0).int(0).end().finish()!;
    const o803 = new Uint8Array(64);
    bsWorlds.worldPlayerEffect(p, "sound_at", d802, o803);
  }
  {
    const d812 = new bsValues.ValueWriter(512).beginList().end().finish()!;
    const o813 = new Uint8Array(64);
    bsWorlds.worldPlayerEffect(p, "nope", d812, o813);
  }
  {
    const o821 = new Uint8Array(512);
    bsWorlds.worldBorderGet(w, o821);
  }
  {
    const d831 = new bsValues.ValueWriter(512).beginMap().key("size").float(100.0).end().finish()!;
    bsWorlds.worldBorderSet(w, d831);
  }
  {
    const d841 = new bsValues.ValueWriter(512).beginMap().key("size").float(-1.0).key("seconds").float(-1.0).end().finish()!;
    bsWorlds.worldBorderSet(w, d841);
  }
  {
    const d852 = new bsValues.ValueWriter(512).beginMap().end().finish()!;
    const o853 = new Uint8Array(1024);
    bsWorlds.worldCall(w, "ops", d852, o853);
  }
  {
    const d862 = new bsValues.ValueWriter(512).beginMap().key("x").int(2).key("z").int(2).end().finish()!;
    const o863 = new Uint8Array(256);
    bsWorlds.worldCall(w, "highest", d862, o863);
  }
  {
    const d872 = new bsValues.ValueWriter(512).beginMap().end().finish()!;
    const o873 = new Uint8Array(256);
    bsWorlds.worldCall(w, "nope", d872, o873);
  }
  {
    s1n = bsWorlds.structureCapture(w, 0, 64, 0, 3, 2, 3, 0, s1);
  }
  {
    bsWorlds.structurePut("tour:a", s1.slice(0, kl(s1n, 2048)), 0);
  }
  {
    bsWorlds.structurePut("tour:b", bytesOf([10, 0, 0, 0]), 0);
  }
  {
    const o910 = new Uint8Array(256);
    bsWorlds.structureList(o910);
  }
  {
    const o921 = new Uint8Array(256);
    bsWorlds.structureGet("tour:a", o921);
  }
  {
    const o931 = new Uint8Array(256);
    bsWorlds.structureGet("tour:zzz", o931);
  }
  {
    const o941 = new Uint8Array(128);
    bsWorlds.structureFile("tour:a", o941);
  }
  {
    const d951 = new bsValues.ValueWriter(512).beginMap().key("x").int(0).key("y").int(64).key("z").int(0).end().finish()!;
    const o952 = new Uint8Array(1024);
    bsWorlds.structureScan(s1.slice(0, kl(s1n, 2048)), d951, o952);
  }
  {
    const d961 = new bsValues.ValueWriter(512).beginMap().key("world").string("world").key("x").int(8).key("y").int(64).key("z").int(8).end().finish()!;
    bsWorlds.structurePlace(s1.slice(0, kl(s1n, 2048)), d961);
  }
  {
    bsWorlds.structureRemove("tour:a", 0);
  }
  {
    bsWorlds.structureRemove("tour:a", 0);
  }
  {
    const d990 = new bsValues.ValueWriter(512).beginMap().key("op").string("shape").key("settings").string("minecraft:overworld").key("seed").int(0).end().finish()!;
    const o991 = new Uint8Array(256);
    bsWorlds.noiseQuery(d990, o991);
  }
  {
    const d1000 = new bsValues.ValueWriter(512).beginMap().key("op").string("nope").end().finish()!;
    const o1001 = new Uint8Array(256);
    bsWorlds.noiseQuery(d1000, o1001);
  }
  {
    const d1010 = new bsValues.ValueWriter(512).beginMap().key("id").string("minecraft:temperature").key("seed").int(0).end().finish()!;
    nz = bsWorlds.noiseCreate(d1010);
  }
  {
    const f1024 = new Float64Array(1);
    bsWorlds.noiseSample(nz, 0.0, 0.0, 0.0, f1024);
  }
  {
    const o1031 = new Uint8Array(128);
    bsWorlds.noiseInfo(nz, o1031);
  }
  {
    bsWorlds.noiseRelease(nz);
  }
  {
    bsWorlds.noiseRelease(nz);
  }
  {
    bsWorlds.blockStateId("minecraft:stone");
  }
  {
    bsWorlds.blockStateId("nope:block");
  }
  {
    const o1081 = new Uint8Array(128);
    bsWorlds.blockStateText(1, o1081);
  }
  {
    const d1091 = new bsValues.ValueWriter(512).beginMap().end().finish()!;
    const o1092 = new Uint8Array(1024);
    bsWorlds.worldsData("tables", d1091, o1092);
  }
  {
    const d1101 = new bsValues.ValueWriter(512).beginMap().end().finish()!;
    const o1102 = new Uint8Array(512);
    bsWorlds.worldsData("blocks", d1101, o1102);
  }
  {
    const d1111 = new bsValues.ValueWriter(512).beginMap().end().finish()!;
    const o1112 = new Uint8Array(64);
    bsWorlds.worldsData("nope", d1111, o1112);
  }
  {
    const d1121 = new bsValues.ValueWriter(512).beginMap().end().finish()!;
    const o1122 = new Uint8Array(128);
    bsWorlds.datafixCall("types", d1121, o1122);
  }
  {
    const d1131 = new bsValues.ValueWriter(512).beginMap().key("type").string("chunk").end().finish()!;
    const o1132 = new Uint8Array(64);
    bsWorlds.datafixCall("supports", d1131, o1132);
  }
  {
    const d1141 = new bsValues.ValueWriter(512).beginMap().key("type").string("chunk").end().finish()!;
    const o1142 = new Uint8Array(256);
    bsWorlds.datafixCall("steps", d1141, o1142);
  }
  {
    const d1151 = new bsValues.ValueWriter(512).beginMap().key("type").string("chunk").key("from").int(100).key("to").int(200).end().finish()!;
    const o1152 = new Uint8Array(64);
    bsWorlds.datafixCall("missing", d1151, o1152);
  }
  {
    const d1161 = new bsValues.ValueWriter(512).beginMap().key("id").int(16).end().finish()!;
    const o1162 = new Uint8Array(128);
    bsWorlds.datafixCall("legacy_state", d1161, o1162);
  }
  {
    const d1171 = new bsValues.ValueWriter(512).beginMap().key("id").int(16).end().finish()!;
    const o1172 = new Uint8Array(128);
    bsWorlds.datafixCall("modern_state", d1171, o1172);
  }
  {
    const d1181 = new bsValues.ValueWriter(512).beginMap().key("type").string("chunk").key("nbt").bytes(bytesOf([10, 0, 0, 0])).key("from").int(100).key("to").int(200).end().finish()!;
    const o1182 = new Uint8Array(128);
    bsWorlds.datafixCall("upgrade", d1181, o1182);
  }
  {
    const d1191 = new bsValues.ValueWriter(512).beginMap().end().finish()!;
    const o1192 = new Uint8Array(64);
    bsWorlds.datafixCall("nope", d1191, o1192);
  }
  {
    bsWorlds.worldAt(w, 3.5, 3.5, later);
  }
  if (w + p + w2 + rq + nz < 0) bsLog.logDebug("unreachable");
}

export function register(): void {
  bsCommands.command("worlds", "", tour);
}
