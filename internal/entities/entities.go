package entities

import (
	bsCommands "basaltmc.org/sdk/server/commands"
	bsCore "basaltmc.org/sdk/server/core"
	bsEntities "basaltmc.org/sdk/server/entities"
	bsPlayers "basaltmc.org/sdk/server/players"
	bsValues "basaltmc.org/sdk/server/values"
)

func EntitiesTour(ctx bsCore.Ctx) {
	source, _ := bsCommands.CommandSourceName(ctx)
	player := bsPlayers.PlayerByName(source)
	me := bsEntities.EntityOfPlayer(player)
	bsEntities.EntityPlayer(me)
	bsEntities.EntityAlive(me)
	bsEntities.EntityId(me)
	var buf1 [3]float64
	bsEntities.EntityPosition(me, &buf1)
	w2 := bsValues.NewValueWriter()
	w2.BeginMap().Key("yaw").Float(90.0).Key("pitch").Float(10.0).End()
	spec2, _ := w2.Finish()
	bsEntities.EntityTeleport(me, 10.5, 64.0, -3.5, spec2)
	w3 := bsValues.NewValueWriter()
	w3.BeginMap().Key("world").String("nowhere").End()
	spec3, _ := w3.Finish()
	bsEntities.EntityTeleport(me, 1.0, 2.0, 3.0, spec3)
	var buf4 [2]float64
	bsEntities.EntityRotation(me, &buf4)
	bsEntities.EntitySetRotation(me, 45.0, 5.0)
	var buf5 [256]byte
	bsEntities.EntityGet(me, "x", buf5[:])
	var buf6 [256]byte
	bsEntities.EntityGet(me, "nope", buf6[:])
	var buf7 [256]byte
	bsEntities.EntityGet(me, "uuid", buf7[:])
	var buf8 [1024]byte
	bsEntities.EntityProperties(me, buf8[:])
	var buf9 [1024]byte
	bsEntities.EntityOperations(me, buf9[:])
	var buf10 [2048]byte
	bsEntities.EntityTypes(buf10[:])
	var buf11 [512]byte
	bsEntities.EntityTypeAttributes("minecraft:pig", buf11[:])
	var buf12 [512]byte
	bsEntities.EntityTypeAttributes("minecraft:nothing", buf12[:])
	w13 := bsValues.NewValueWriter()
	w13.BeginMap().Key("yaw").Float(90.0).End()
	spec13, _ := w13.Finish()
	pig := bsEntities.EntitySpawn("world", "minecraft:pig", 1.0, 64.0, 2.0, spec13)
	bsEntities.EntitySpawn("world", "minecraft:nothing", 0.0, 0.0, 0.0, nil)
	zombie := bsEntities.EntitySpawn("world", "minecraft:zombie", 3.0, 64.0, 2.0, nil)
	w14 := bsValues.NewValueWriter()
	w14.BeginMap().Key("reason").String("veto").End()
	spec14, _ := w14.Finish()
	bsEntities.EntitySpawn("world", "minecraft:pig", 0.0, 0.0, 0.0, spec14)
	bsEntities.EntitySpawn("nowhere", "minecraft:pig", 0.0, 0.0, 0.0, nil)
	pig_id := bsEntities.EntityId(pig)
	bsEntities.EntityById("world", pig_id)
	bsEntities.EntityById("world", 9999)
	bsEntities.EntityByUuid("00000000-0000-4000-8000-000000000001")
	bsEntities.EntityByUuid("not-a-uuid")
	w15 := bsValues.NewValueWriter()
	w15.BeginMap().Key("type").String("minecraft:pig").End()
	spec15, _ := w15.Finish()
	var buf16 [8]int32
	bsEntities.EntityQuery("world", spec15, buf16[:])
	w17 := bsValues.NewValueWriter()
	w17.BeginMap().Key("x").Float(1.0).Key("y").Float(64.0).Key("z").Float(2.0).Key("radius").Float(5.0).Key("limit").Int(10).End()
	spec17, _ := w17.Finish()
	var buf18 [8]int32
	bsEntities.EntityQuery("world", spec17, buf18[:])
	bsEntities.EntitySetVelocity(pig, 0.0, 0.5, 0.0)
	var buf19 [3]float64
	bsEntities.EntityVelocity(pig, &buf19)
	var buf20 [3]float64
	bsEntities.EntityVelocity(me, &buf20)
	w21 := bsValues.NewValueWriter()
	w21.Bool(true)
	spec21, _ := w21.Finish()
	bsEntities.EntitySet(pig, "glowing", spec21)
	var buf22 [64]byte
	bsEntities.EntityGet(pig, "glowing", buf22[:])
	w23 := bsValues.NewValueWriter()
	w23.Int(1)
	spec23, _ := w23.Finish()
	bsEntities.EntitySet(pig, "vehicle", spec23)
	w24 := bsValues.NewValueWriter()
	w24.Bool(true)
	spec24, _ := w24.Finish()
	bsEntities.EntitySet(pig, "nope", spec24)
	w25 := bsValues.NewValueWriter()
	w25.BeginList().Float(3.0).End()
	spec25, _ := w25.Finish()
	var buf26 [64]byte
	bsEntities.EntityCall(pig, "damage", spec25, buf26[:])
	w27 := bsValues.NewValueWriter()
	w27.BeginList().String("minecraft:speed").Int(200).Int(1).End()
	spec27, _ := w27.Finish()
	var buf28 [64]byte
	bsEntities.EntityCall(pig, "add_effect", spec27, buf28[:])
	var buf29 [512]byte
	bsEntities.EntityCall(pig, "effects", nil, buf29[:])
	w30 := bsValues.NewValueWriter()
	w30.BeginList().String("example:wander").Int(3).String("MOVE").End()
	spec30, _ := w30.Finish()
	var buf31 [64]byte
	bsEntities.EntityCall(pig, "add_goal", spec30, buf31[:])
	w32 := bsValues.NewValueWriter()
	w32.BeginList().String("example:wander").End()
	spec32, _ := w32.Finish()
	var buf33 [64]byte
	bsEntities.EntityCall(pig, "has_goal", spec32, buf33[:])
	var buf34 [512]byte
	bsEntities.EntityCall(pig, "goals", nil, buf34[:])
	w35 := bsValues.NewValueWriter()
	w35.BeginList().Float(5.0).Float(64.0).Float(5.0).End()
	spec35, _ := w35.Finish()
	var buf36 [512]byte
	bsEntities.EntityCall(pig, "find_path", spec35, buf36[:])
	var buf37 [64]byte
	bsEntities.EntityCall(pig, "bogus", nil, buf37[:])
	var snap [4096]byte
	saved := bsEntities.EntitySnapshot(pig, snap[:])
	bsEntities.EntityRestore(pig, snap[:saved])
	clone := bsEntities.EntitySpawnNbt("world", snap[:saved], "command")
	w39 := bsValues.NewValueWriter()
	w39.BeginMap().Key("id").String("minecraft:diamond").Key("count").Int(2).End()
	spec39, _ := w39.Finish()
	drop := bsEntities.EntityDropItem("world", spec39, 1.0, 65.0, 2.0, 0.0, 0.2, 0.0)
	w40 := bsValues.NewValueWriter()
	w40.BeginMap().Key("count").Int(1).End()
	spec40, _ := w40.Finish()
	bsEntities.EntityDropItem("world", spec40, 1.0, 65.0, 2.0, 0.0, 0.0, 0.0)
	w41 := bsValues.NewValueWriter()
	w41.BeginMap().Key("limits").BeginMap().Key("monster").Int(20).End().Key("allow_animals").Bool(false).End()
	spec41, _ := w41.Finish()
	var buf42 [512]byte
	bsEntities.EntitySpawnRules("world", spec41, buf42[:])
	w43 := bsValues.NewValueWriter()
	w43.BeginMap().Key("limits").BeginMap().Key("monster").Int(-1).End().Key("allow_animals").Bool(true).End()
	spec43, _ := w43.Finish()
	var buf44 [512]byte
	bsEntities.EntitySpawnRules("world", spec43, buf44[:])
	w45 := bsValues.NewValueWriter()
	w45.BeginMap().Key("distance").Float(10.0).End()
	spec45, _ := w45.Finish()
	var buf46 [64]byte
	bsEntities.EntityMechanic("combat.fall_damage", spec45, buf46[:])
	w47 := bsValues.NewValueWriter()
	w47.BeginMap().Key("tick_count").Float(10.0).End()
	spec47, _ := w47.Finish()
	var buf48 [64]byte
	bsEntities.EntityMechanic("physics.projectile_margin", spec47, buf48[:])
	w49 := bsValues.NewValueWriter()
	w49.BeginMap().End()
	spec49, _ := w49.Finish()
	var buf50 [64]byte
	bsEntities.EntityMechanic("nope", spec49, buf50[:])
	bsEntities.EntityRemove(pig)
	bsEntities.EntityAlive(pig)
	var buf51 [3]float64
	bsEntities.EntityPosition(pig, &buf51)
	bsEntities.EntityRemove(pig)
	bsEntities.EntityRemoveUuid("world", "00000000-0000-4000-8000-0000000000ff")
	bsEntities.EntityRemoveUuid("world", "nope")
	bsEntities.EntityRemove(zombie)
	bsEntities.EntityRemove(clone)
	bsEntities.EntityRemove(drop)
	bsEntities.EntityRelease(me)
	bsEntities.EntityRelease(me)
	bsCommands.CommandReply(ctx, "tour done", nil)
}

func EntitiesBad(ctx bsCore.Ctx) {
	bsEntities.EntityAlive(999)
	bsEntities.EntityId(0)
	bsEntities.EntityPlayer(-5)
	bsEntities.EntityByUuid("")
	bsEntities.EntityEventEntity(ctx)
	bsEntities.EntityAiAnswer(ctx, 1)
	var buf1 [3]float64
	bsEntities.EntityPosition(7, &buf1)
	w2 := bsValues.NewValueWriter()
	w2.BeginMap().End()
	spec2, _ := w2.Finish()
	var buf3 [16]byte
	bsEntities.EntityMechanic("", spec2, buf3[:])
	bsCommands.CommandReply(ctx, "bad done", nil)
}

func Register() {
	bsCommands.Command("entities tour", "", EntitiesTour)
	bsCommands.Command("entities bad", "", EntitiesBad)
}
