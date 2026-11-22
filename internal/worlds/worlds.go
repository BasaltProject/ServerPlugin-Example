package worlds

import (
	bsCommands "basaltmc.org/sdk/server/commands"
	bsCore "basaltmc.org/sdk/server/core"
	bsPlayers "basaltmc.org/sdk/server/players"
	bsValues "basaltmc.org/sdk/server/values"
	bsWorlds "basaltmc.org/sdk/server/worlds"
)

func Kl(length, capacity int32) int32 {
	if length > 0 && length <= capacity {
		return length
	}
	return 0
}

func Done(ctx bsCore.Ctx) {
	out := make([]byte, 16)
	bsWorlds.WorldValueResult(int32(ctx), out)
}

func Later2(bsCore.Ctx) {
	var w2 int32
	{
		d20000, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("name").String("tour").End().Finish()
		bsWorlds.WorldCreate(d20000)
	}
	{
		bsWorlds.WorldUnload("nope", 0)
	}
	{
		bsWorlds.WorldUnload("tour", 0)
	}
	{
		bsWorlds.WorldDelete("tour")
	}
	_ = w2
}

func WorldsLater(bsCore.Ctx) {
	var w2 int32
	{
		d10000, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("name").String("tour").End().Finish()
		w2 = bsWorlds.WorldCreate(d10000)
	}
	{
		bsWorlds.WorldAt(w2, 0.5, 0.5, Later2)
	}
	_ = w2
}

func Tour(bsCore.Ctx) {
	bsWorlds.ChunkCoord(-17)
	bsWorlds.BlockDistance(0, 0, 0, 3, 4, 0)
	pos := [3]int32{1, 2, 3}
	bsWorlds.PosOffset(&pos, 1, 1, 1)
	var w, w2, rq, nz int32
	p := int32(bsPlayers.PlayerByName("Steve"))
	s1 := make([]byte, 2048)
	var s1n int32
	{
		w = bsWorlds.WorldByName("world")
	}
	{
		bsWorlds.WorldByName("nope")
	}
	{
		bsWorlds.WorldDefault()
	}
	{
		o30 := make([]int32, 8)
		bsWorlds.WorldList(o30)
	}
	{
		o41 := make([]byte, 128)
		bsWorlds.WorldName(w, o41)
	}
	{
		o51 := make([]byte, 128)
		bsWorlds.WorldName(999, o51)
	}
	{
		o62 := make([]byte, 128)
		bsWorlds.WorldGet(w, "time", o62)
	}
	{
		o72 := make([]byte, 128)
		bsWorlds.WorldGet(w, "nope", o72)
	}
	{
		d82, _ := bsValues.NewValueWriterLimit(512).Int(1000).Finish()
		bsWorlds.WorldSet(w, "day_time", d82)
	}
	{
		d92, _ := bsValues.NewValueWriterLimit(512).Int(1).Finish()
		bsWorlds.WorldSet(w, "nope", d92)
	}
	{
		o102 := make([]byte, 128)
		bsWorlds.WorldRuleGet(w, "doDaylightCycle", o102)
	}
	{
		o112 := make([]byte, 128)
		bsWorlds.WorldRuleGet(w, "nope", o112)
	}
	{
		bsWorlds.WorldRuleSet(w, "doDaylightCycle", "false")
	}
	{
		rq = bsWorlds.WorldAt(w, 1.5, 1.5, Done)
	}
	{
		bsWorlds.WorldRequestCancel(rq)
	}
	{
		bsWorlds.WorldAt(w, 2.5, 2.5, Done)
	}
	{
		bsWorlds.WorldBlockSet(w, 0, 64, 0, "minecraft:stone", 0)
	}
	{
		bsWorlds.WorldBlockSet(w, 0, 64, 0, "nope:block", 0)
	}
	{
		o185 := make([]byte, 128)
		bsWorlds.WorldBlockGet(w, 0, 64, 0, 0, o185)
	}
	{
		bsWorlds.WorldStateGet(w, 0, 64, 0, 0)
	}
	{
		bsWorlds.WorldStateSet(w, 1, 64, 0, 3, 0)
	}
	{
		bsWorlds.WorldStateGet(999, 0, 64, 0, 0)
	}
	{
		o222 := make([]int32, 2)
		bsWorlds.WorldStatesRead(w, []int32{0, 64, 0, 1, 64, 0}, o222, 0)
	}
	{
		bsWorlds.WorldStatesWrite(w, []int32{2, 64, 0, 3, 64, 0}, []int32{5, 5}, 0)
	}
	{
		bsWorlds.WorldFill(w, 2, 64, 2, 5, 65, 5, 5, 0)
	}
	{
		bsWorlds.WorldFill(999, 0, 0, 0, 1, 1, 1, 1, 0)
	}
	{
		o265 := make([]int32, 4096)
		bsWorlds.WorldSectionRead(w, 0, 4, 0, 0, o265)
	}
	{
		bsWorlds.WorldSectionWrite(w, 0, 4, 0, make([]int32, 4096), 0)
	}
	{
		bsWorlds.WorldSectionWrite(w, 0, 200, 0, make([]int32, 4096), 0)
	}
	{
		o294 := make([]int32, 8)
		bsWorlds.WorldChunkRead(w, 0, 0, 0, o294)
	}
	{
		o306 := make([]int32, 4096)
		bsWorlds.WorldChunkApply(w, 0, 0, []int32{4}, make([]int32, 4096), 0, o306)
	}
	{
		bsWorlds.WorldChangesApply(w, []int32{0, 64, 0}, []int32{5}, 0)
	}
	{
		bsWorlds.WorldBlocksSettle(w, []int32{0, 64, 0}, []int32{0}, 0)
	}
	{
		bsWorlds.WorldBlocksNotify(w, []int32{0, 64, 0}, []int32{1, 64, 0})
	}
	{
		bsWorlds.WorldBlocksReshape(w, []int32{0, 64, 0}, 0)
	}
	{
		bsWorlds.WorldStateValid(w, 0, 64, 0, 1)
	}
	{
		bsWorlds.WorldTickSchedule(w, 0, 64, 0, 5, 0)
	}
	{
		bsWorlds.WorldTickPending(w, 0, 64, 0)
	}
	{
		bsWorlds.WorldStateSet(w, 3, 64, 0, 6, 0)
	}
	{
		bsWorlds.WorldBlockEntityEdit(w, 1, 3, 64, 0, []byte{10, 0, 0, 0})
	}
	{
		o404 := make([]byte, 256)
		bsWorlds.WorldBlockEntityGet(w, 3, 64, 0, o404)
	}
	{
		o414 := make([]byte, 256)
		bsWorlds.WorldBlockEntityGet(w, 9, 64, 9, o414)
	}
	{
		o423 := make([]int32, 8)
		bsWorlds.WorldBlockEntities(w, 0, 0, o423)
	}
	{
		bsWorlds.WorldDataSet(w, 0, 0, 0, 0, []byte{10, 0, 0, 0})
	}
	{
		o445 := make([]byte, 256)
		bsWorlds.WorldDataGet(w, 0, 0, 0, 0, o445)
	}
	{
		o455 := make([]byte, 256)
		bsWorlds.WorldDataGet(w, 1, 9, 0, 9, o455)
	}
	{
		bsWorlds.WorldChunkStatus(w, 0, 0)
	}
	{
		bsWorlds.WorldChunkStatus(w, 9, 9)
	}
	{
		bsWorlds.WorldChunkLoad(w, 3, 3, 8)
	}
	{
		bsWorlds.WorldChunkUnload(w, 3, 3)
	}
	{
		bsWorlds.WorldChunkRequest(w, 4, 4, 8, 0, Done)
	}
	{
		o511 := make([]int32, 16)
		bsWorlds.WorldChunksLoaded(w, o511)
	}
	{
		bsWorlds.WorldChunkHold(w, "tour", []int32{4, 4})
	}
	{
		o532 := make([]int32, 8)
		bsWorlds.WorldChunkHeld(w, "tour", o532)
	}
	{
		bsWorlds.WorldChunkRelease(w, "tour", []int32{4, 4})
	}
	{
		bsWorlds.WorldChunksResend(w, []int32{0, 0})
	}
	{
		bsWorlds.WorldStoredPut(w, 7, 7, []byte{10, 0, 0, 0})
	}
	{
		o573 := make([]byte, 256)
		bsWorlds.WorldStoredGet(w, 7, 7, o573)
	}
	{
		o583 := make([]byte, 256)
		bsWorlds.WorldStoredGet(w, 8, 8, o583)
	}
	{
		o593 := make([]int32, 8)
		bsWorlds.WorldStoredList(w, 0, 0, o593)
	}
	{
		bsWorlds.WorldLightAt(w, 0, 64, 0, 0)
	}
	{
		o616 := make([]byte, 2048)
		bsWorlds.WorldLightRead(w, 0, 4, 0, 1, 1, o616)
	}
	{
		bsWorlds.WorldLightWrite(w, 0, 4, 0, 1, make([]byte, 2048))
	}
	{
		bsWorlds.WorldLightWrite(w, 0, 4, 0, 1, []byte{10, 0, 0, 0})
	}
	{
		bsWorlds.WorldLightCheck(w, []int32{0, 0}, Done)
	}
	{
		o654 := make([]byte, 128)
		bsWorlds.WorldBiomeGet(w, 0, 64, 0, o654)
	}
	{
		bsWorlds.WorldBiomeSet(w, 0, 64, 0, "minecraft:desert")
	}
	{
		bsWorlds.WorldBiomeSet(w, 0, 64, 0, "nope:biome")
	}
	{
		o683 := make([]int32, 8)
		bsWorlds.WorldBiomesRead(w, 0, 0, o683)
	}
	{
		bsWorlds.WorldBiomesWrite(w, 0, 0, []int32{1, 1, 1, 1})
	}
	{
		bsWorlds.WorldSound(w, "minecraft:block.note_block.bell", "block", 0.5, 64.0, 0.5, 1.0, 1.0, 0)
	}
	{
		bsWorlds.WorldSound(w, "", "block", 0.5, 64.0, 0.5, 1.0, 1.0, 0)
	}
	{
		d721, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("type").String("minecraft:flame").Key("x").Float(0.5).Key("y").Float(65.0).Key("z").Float(0.5).Key("count").Int(3).End().Finish()
		bsWorlds.WorldParticle(w, d721)
	}
	{
		d731, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("count").Int(1).End().Finish()
		bsWorlds.WorldParticle(w, d731)
	}
	{
		bsWorlds.WorldLevelEvent(w, 1000, 0, 64, 0, 0, 0)
	}
	{
		bsWorlds.WorldBlockEvent(w, 0, 64, 0, 1, 2)
	}
	{
		bsWorlds.WorldExplode(w, 0.5, 64.0, 0.5, 0.0, 0, 0)
	}
	{
		bsWorlds.WorldLightning(w, 0.5, 64.0, 0.5)
	}
	{
		bsWorlds.WorldGameEvent(w, "minecraft:block_change", 0.5, 64.0, 0.5, "")
	}
	{
		bsWorlds.WorldGameEvent(w, "", 0.5, 64.0, 0.5, "")
	}
	{
		d802, _ := bsValues.NewValueWriterLimit(512).BeginList().String("minecraft:block.note_block.bell").String("block").Float(0.5).Float(64.0).Float(0.5).Float(1.0).Float(1.0).Int(0).End().Finish()
		o803 := make([]byte, 64)
		bsWorlds.WorldPlayerEffect(p, "sound_at", d802, o803)
	}
	{
		d812, _ := bsValues.NewValueWriterLimit(512).BeginList().End().Finish()
		o813 := make([]byte, 64)
		bsWorlds.WorldPlayerEffect(p, "nope", d812, o813)
	}
	{
		o821 := make([]byte, 512)
		bsWorlds.WorldBorderGet(w, o821)
	}
	{
		d831, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("size").Float(100.0).End().Finish()
		bsWorlds.WorldBorderSet(w, d831)
	}
	{
		d841, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("size").Float(-1.0).Key("seconds").Float(-1.0).End().Finish()
		bsWorlds.WorldBorderSet(w, d841)
	}
	{
		d852, _ := bsValues.NewValueWriterLimit(512).BeginMap().End().Finish()
		o853 := make([]byte, 1024)
		bsWorlds.WorldCall(w, "ops", d852, o853)
	}
	{
		d862, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("x").Int(2).Key("z").Int(2).End().Finish()
		o863 := make([]byte, 256)
		bsWorlds.WorldCall(w, "highest", d862, o863)
	}
	{
		d872, _ := bsValues.NewValueWriterLimit(512).BeginMap().End().Finish()
		o873 := make([]byte, 256)
		bsWorlds.WorldCall(w, "nope", d872, o873)
	}
	{
		s1n = bsWorlds.StructureCapture(w, 0, 64, 0, 3, 2, 3, 0, s1)
	}
	{
		bsWorlds.StructurePut("tour:a", s1[:Kl(s1n, 2048)], 0)
	}
	{
		bsWorlds.StructurePut("tour:b", []byte{10, 0, 0, 0}, 0)
	}
	{
		o910 := make([]byte, 256)
		bsWorlds.StructureList(o910)
	}
	{
		o921 := make([]byte, 256)
		bsWorlds.StructureGet("tour:a", o921)
	}
	{
		o931 := make([]byte, 256)
		bsWorlds.StructureGet("tour:zzz", o931)
	}
	{
		o941 := make([]byte, 128)
		bsWorlds.StructureFile("tour:a", o941)
	}
	{
		d951, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("x").Int(0).Key("y").Int(64).Key("z").Int(0).End().Finish()
		o952 := make([]byte, 1024)
		bsWorlds.StructureScan(s1[:Kl(s1n, 2048)], d951, o952)
	}
	{
		d961, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("world").String("world").Key("x").Int(8).Key("y").Int(64).Key("z").Int(8).End().Finish()
		bsWorlds.StructurePlace(s1[:Kl(s1n, 2048)], d961)
	}
	{
		bsWorlds.StructureRemove("tour:a", 0)
	}
	{
		bsWorlds.StructureRemove("tour:a", 0)
	}
	{
		d990, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("op").String("shape").Key("settings").String("minecraft:overworld").Key("seed").Int(0).End().Finish()
		o991 := make([]byte, 256)
		bsWorlds.NoiseQuery(d990, o991)
	}
	{
		d1000, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("op").String("nope").End().Finish()
		o1001 := make([]byte, 256)
		bsWorlds.NoiseQuery(d1000, o1001)
	}
	{
		d1010, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("id").String("minecraft:temperature").Key("seed").Int(0).End().Finish()
		nz = bsWorlds.NoiseCreate(d1010)
	}
	{
		var f1024 float64
		bsWorlds.NoiseSample(nz, 0.0, 0.0, 0.0, &f1024)
	}
	{
		o1031 := make([]byte, 128)
		bsWorlds.NoiseInfo(nz, o1031)
	}
	{
		bsWorlds.NoiseRelease(nz)
	}
	{
		bsWorlds.NoiseRelease(nz)
	}
	{
		bsWorlds.BlockStateId("minecraft:stone")
	}
	{
		bsWorlds.BlockStateId("nope:block")
	}
	{
		o1081 := make([]byte, 128)
		bsWorlds.BlockStateText(1, o1081)
	}
	{
		d1091, _ := bsValues.NewValueWriterLimit(512).BeginMap().End().Finish()
		o1092 := make([]byte, 1024)
		bsWorlds.WorldsData("tables", d1091, o1092)
	}
	{
		d1101, _ := bsValues.NewValueWriterLimit(512).BeginMap().End().Finish()
		o1102 := make([]byte, 512)
		bsWorlds.WorldsData("blocks", d1101, o1102)
	}
	{
		d1111, _ := bsValues.NewValueWriterLimit(512).BeginMap().End().Finish()
		o1112 := make([]byte, 64)
		bsWorlds.WorldsData("nope", d1111, o1112)
	}
	{
		d1121, _ := bsValues.NewValueWriterLimit(512).BeginMap().End().Finish()
		o1122 := make([]byte, 128)
		bsWorlds.DatafixCall("types", d1121, o1122)
	}
	{
		d1131, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("type").String("chunk").End().Finish()
		o1132 := make([]byte, 64)
		bsWorlds.DatafixCall("supports", d1131, o1132)
	}
	{
		d1141, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("type").String("chunk").End().Finish()
		o1142 := make([]byte, 256)
		bsWorlds.DatafixCall("steps", d1141, o1142)
	}
	{
		d1151, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("type").String("chunk").Key("from").Int(100).Key("to").Int(200).End().Finish()
		o1152 := make([]byte, 64)
		bsWorlds.DatafixCall("missing", d1151, o1152)
	}
	{
		d1161, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("id").Int(16).End().Finish()
		o1162 := make([]byte, 128)
		bsWorlds.DatafixCall("legacy_state", d1161, o1162)
	}
	{
		d1171, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("id").Int(16).End().Finish()
		o1172 := make([]byte, 128)
		bsWorlds.DatafixCall("modern_state", d1171, o1172)
	}
	{
		d1181, _ := bsValues.NewValueWriterLimit(512).BeginMap().Key("type").String("chunk").Key("nbt").Bytes([]byte{10, 0, 0, 0}).Key("from").Int(100).Key("to").Int(200).End().Finish()
		o1182 := make([]byte, 128)
		bsWorlds.DatafixCall("upgrade", d1181, o1182)
	}
	{
		d1191, _ := bsValues.NewValueWriterLimit(512).BeginMap().End().Finish()
		o1192 := make([]byte, 64)
		bsWorlds.DatafixCall("nope", d1191, o1192)
	}
	{
		bsWorlds.WorldAt(w, 3.5, 3.5, WorldsLater)
	}
	_, _, _, _, _ = w, p, w2, rq, nz
	_ = s1n
}

func Register() { bsCommands.Command("worlds", "", Tour) }
