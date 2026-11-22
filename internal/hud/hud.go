package hud

import (
	bsCommands "basaltmc.org/sdk/server/commands"
	bsCore "basaltmc.org/sdk/server/core"
	bsHud "basaltmc.org/sdk/server/hud"
	bsLog "basaltmc.org/sdk/server/log"
	bsPlayers "basaltmc.org/sdk/server/players"
	bsValues "basaltmc.org/sdk/server/values"
)

var gNpc int32
var gClaim int32

func HudEntityOf(npc int32) int32 {
	var raw [768]byte
	length := bsHud.HudNpcGet(npc, raw[:])
	if length <= 0 || int(length) > len(raw) {
		return 0
	}
	value, ok := bsValues.OpenValue(raw[:length])
	if !ok {
		return 0
	}
	id, ok := value.Find("id")
	if !ok {
		return 0
	}
	entity, _ := id.AsInt()
	return int32(entity)
}

func HudClicked(_ bsCore.Ctx) {
	bsLog.LogInfo("npc clicked")
}

func HudRender(ctx bsCore.Ctx) {
	w1 := bsValues.NewValueWriter()
	w1.BeginMap().Key("title").String("<gold>Example").Key("lines").BeginList().String("<white>Hello %player%").String("<gray>second line").End().End()
	spec2, _ := w1.Finish()
	bsHud.HudSurfaceAnswer(ctx, spec2)
}

func HudBoard(ctx bsCore.Ctx) {
	source, _ := bsCommands.CommandSourceName(ctx)
	player := bsPlayers.PlayerByName(source)
	mainBoard := bsHud.HudScoreboardMain()
	own := bsHud.HudScoreboardCreate()
	w1 := bsValues.NewValueWriter()
	w1.BeginMap().Key("criterion").String("dummy").Key("title").String("<gold>Kills").Key("hearts").Bool(false).End()
	spec2, _ := w1.Finish()
	bsHud.HudObjectiveAdd(own, "kills", spec2)
	w3 := bsValues.NewValueWriter()
	w3.BeginMap().Key("criterion").String("no_such_criterion").End()
	spec4, _ := w3.Finish()
	bsHud.HudObjectiveAdd(own, "broken", spec4)
	var buf5 [256]byte
	bsHud.HudScoreboardList(own, "objectives", buf5[:])
	bsHud.HudDisplaySet(own, "sidebar", "kills")
	var buf6 [256]byte
	bsHud.HudDisplayGet(own, "sidebar", buf6[:])
	bsHud.HudDisplaySet(own, "sidebar", "missing")
	bsHud.HudDisplaySet(own, "nowhere", "kills")
	bsHud.HudScoreSet(own, "Steve", "kills", 7)
	bsHud.HudScoreSet(own, "Alex", "kills", 3)
	var out7 int32
	bsHud.HudScoreGet(own, "Steve", "kills", &out7)
	var buf8 [256]byte
	bsHud.HudScoreboardList(own, "entries", buf8[:])
	bsHud.HudScoreReset(own, "Steve", "kills")
	var out9 int32
	bsHud.HudScoreGet(own, "Steve", "kills", &out9)
	bsHud.HudTeamAdd(own, "red")
	bsHud.HudTeamAdd(own, "red")
	w10 := bsValues.NewValueWriter()
	w10.BeginMap().Key("display_name").String("<red>Red").Key("prefix").String("[R] ").Key("suffix").String("!").Key("color").String("red").Key("friendly_fire").Bool(false).Key("see_invisible").Bool(true).Key("name_tags").String("hideForOtherTeams").Key("death_messages").String("never").Key("collision").String("pushOtherTeams").End()
	spec11, _ := w10.Finish()
	bsHud.HudTeamSet(own, "red", spec11)
	w12 := bsValues.NewValueWriter()
	w12.BeginMap().Key("color").String("blue").End()
	spec13, _ := w12.Finish()
	bsHud.HudTeamSet(own, "blue", spec13)
	bsHud.HudTeamJoin(own, "red", "Steve")
	bsHud.HudTeamJoin(own, "gone", "Steve")
	var buf14 [256]byte
	bsHud.HudTeamOf(own, "Steve", buf14[:])
	var buf15 [256]byte
	bsHud.HudScoreboardList(own, "teams", buf15[:])
	var buf16 [256]byte
	bsHud.HudScoreboardList(own, "bogus", buf16[:])
	bsHud.HudTeamLeave(own, "red", "Steve")
	bsHud.HudTeamLeave(own, "red", "Steve")
	var buf17 [256]byte
	bsHud.HudTeamOf(own, "Steve", buf17[:])
	bsHud.HudTeamRemove(own, "red")
	bsHud.HudTeamRemove(own, "red")
	bsHud.HudScoreboardShow(player, own)
	bsHud.HudScoreboardShown(player)
	bsHud.HudScoreboardShow(player, mainBoard)
	bsHud.HudScoreboardShown(player)
	bsHud.HudObjectiveRemove(own, "kills")
	bsHud.HudObjectiveRemove(own, "kills")
	bsHud.HudScoreboardRelease(own)
	bsHud.HudScoreboardRelease(own)
	bsHud.HudScoreboardRelease(mainBoard)
	bsCommands.CommandReply(ctx, "board done", nil)
}

func HudBars(ctx bsCore.Ctx) {
	source, _ := bsCommands.CommandSourceName(ctx)
	player := bsPlayers.PlayerByName(source)
	w1 := bsValues.NewValueWriter()
	w1.BeginMap().Key("title").String("<green>Wave 1").Key("color").String("green").Key("style").String("notched_10").Key("progress").Float(0.5).Key("darken_sky").Bool(false).Key("boss_music").Bool(false).Key("fog").Bool(false).Key("visible").Bool(true).Key("key").String("example:wave").End()
	spec2, _ := w1.Finish()
	bar := bsHud.HudBossbarCreate(spec2)
	w3 := bsValues.NewValueWriter()
	w3.BeginMap().Key("title").String("<green>Wave 1").Key("key").String("example:wave").End()
	spec4, _ := w3.Finish()
	bsHud.HudBossbarCreate(spec4)
	var buf5 [512]byte
	bsHud.HudBossbarKept(buf5[:])
	var buf6 [512]byte
	bsHud.HudBossbarGet(bar, buf6[:])
	attached := bsHud.HudBossbarAttach("example:wave")
	bsHud.HudBossbarAttach("example:none")
	bsHud.HudBossbarPlayerAdd(bar, player)
	bsHud.HudBossbarPlayerAdd(bar, player)
	var buf7 [512]byte
	bsHud.HudBossbarPlayers(bar, buf7[:])
	w8 := bsValues.NewValueWriter()
	w8.BeginMap().Key("title").String("<red>Wave 2").Key("color").String("red").Key("progress").Float(0.25).End()
	spec9, _ := w8.Finish()
	bsHud.HudBossbarUpdate(bar, spec9)
	w10 := bsValues.NewValueWriter()
	w10.BeginMap().Key("progress").String("half").End()
	spec11, _ := w10.Finish()
	bsHud.HudBossbarUpdate(bar, spec11)
	bsHud.HudBossbarPlayerRemove(bar, player)
	bsHud.HudBossbarPlayerRemove(bar, player)
	bsHud.HudBossbarRemove(bar)
	w12 := bsValues.NewValueWriter()
	w12.BeginMap().Key("title").String("<red>Late").End()
	spec13, _ := w12.Finish()
	bsHud.HudBossbarUpdate(attached, spec13)
	bsHud.HudBossbarRemove(bar)
	bsCommands.CommandReply(ctx, "bars done", nil)
}

func HudHolograms(ctx bsCore.Ctx) {
	source, _ := bsCommands.CommandSourceName(ctx)
	player := bsPlayers.PlayerByName(source)
	name, _ := bsPlayers.PlayerName(player)
	holo := bsHud.HudHologramCreate("world", "<yellow>Hello", 0.5, 64.0, 0.5)
	bsHud.HudHologramCreate("nowhere", "text", 0.0, 0.0, 0.0)
	w1 := bsValues.NewValueWriter()
	w1.BeginMap().Key("text").String("<yellow>Moved").Key("x").Float(2.5).Key("y").Float(65.0).Key("z").Float(-1.5).End()
	spec2, _ := w1.Finish()
	bsHud.HudHologramSet(holo, spec2)
	w3 := bsValues.NewValueWriter()
	w3.BeginMap().Key("x").Float(1.0).End()
	spec4, _ := w3.Finish()
	bsHud.HudHologramSet(holo, spec4)
	w5 := bsValues.NewValueWriter()
	w5.BeginMap().Key("audience").BeginList().String(name).End().End()
	spec6, _ := w5.Finish()
	bsHud.HudHologramSet(holo, spec6)
	var buf7 [512]byte
	bsHud.HudHologramGet(holo, buf7[:])
	w8 := bsValues.NewValueWriter()
	w8.BeginMap().Key("audience").Null().End()
	spec9, _ := w8.Finish()
	bsHud.HudHologramSet(holo, spec9)
	bsHud.HudHologramRemove(holo)
	bsHud.HudHologramRemove(holo)
	bsCommands.CommandReply(ctx, "holograms done", nil)
}

func HudNpcs(ctx bsCore.Ctx) {
	source, _ := bsCommands.CommandSourceName(ctx)
	player := bsPlayers.PlayerByName(source)
	w1 := bsValues.NewValueWriter()
	w1.BeginMap().Key("world").String("world").Key("name").String("<aqua>Guide").Key("x").Float(1.5).Key("y").Float(64.0).Key("z").Float(2.5).End()
	spec2, _ := w1.Finish()
	npc := bsHud.HudNpcCreate(spec2, HudClicked)
	w3 := bsValues.NewValueWriter()
	w3.BeginMap().Key("world").String("nowhere").Key("name").String("Nobody").Key("x").Float(0.0).Key("y").Float(0.0).Key("z").Float(0.0).End()
	spec4, _ := w3.Finish()
	bsHud.HudNpcCreate(spec4, nil)
	w5 := bsValues.NewValueWriter()
	w5.BeginMap().Key("name").String("<aqua>Guide II").Key("x").Float(3.5).Key("y").Float(65.0).Key("z").Float(4.5).Key("yaw").Float(90.0).Key("pitch").Float(-15.0).Key("looks_at_players").Bool(true).Key("health").Float(18.0).Key("held_slot").Int(0).Key("flags").Int(0).Key("pose").Int(0).Key("attributes").BeginMap().Key("max_health").Float(30.0).End().Key("equipment").BeginMap().Key("0").BeginMap().Key("item").String("minecraft:diamond_sword").Key("count").Int(1).End().End().End()
	spec6, _ := w5.Finish()
	bsHud.HudNpcSet(npc, spec6)
	w7 := bsValues.NewValueWriter()
	w7.BeginMap().Key("attributes").BeginMap().Key("no_such_attribute").Float(1.0).End().End()
	spec8, _ := w7.Finish()
	bsHud.HudNpcSet(npc, spec8)
	var buf9 [768]byte
	bsHud.HudNpcGet(npc, buf9[:])
	var buf10 [768]byte
	bsHud.HudNpcItem(npc, 0, buf10[:])
	var buf11 [768]byte
	bsHud.HudNpcItem(npc, 1, buf11[:])
	var out12 float64
	bsHud.HudNpcAttribute(npc, "max_health", &out12)
	var out13 float64
	bsHud.HudNpcAttribute(npc, "no_such_attribute", &out13)
	bsHud.HudNpcAnimate(npc, 0)
	entity := HudEntityOf(npc)
	bsHud.HudTabEntry(player, entity, 0)
	bsHud.HudTabEntry(player, 424242, 0)
	gNpc = npc
	bsCommands.CommandReply(ctx, "npcs done", nil)
}

func HudNpcEnd(ctx bsCore.Ctx) {
	bsHud.HudNpcRemove(gNpc)
	bsHud.HudNpcAnimate(gNpc, 0)
	bsCommands.CommandReply(ctx, "npc end done", nil)
}

func HudTab(ctx bsCore.Ctx) {
	source, _ := bsCommands.CommandSourceName(ctx)
	player := bsPlayers.PlayerByName(source)
	w1 := bsValues.NewValueWriter()
	w1.BeginMap().Key("tab_header").String("<green>Top").Key("tab_footer").String("<gray>Bottom").Key("list_name").String("<gold>Steve").Key("listed").Bool(true).Key("list_order").Int(3).Key("score").Int(5).Key("waypoint_style").String("default").Key("waypoint_color").Int(16711680).End()
	spec2, _ := w1.Finish()
	bsHud.HudPlayerSet(player, spec2)
	var buf3 [768]byte
	bsHud.HudPlayerGet(player, buf3[:])
	w4 := bsValues.NewValueWriter()
	w4.BeginMap().Key("not_a_property").Int(1).End()
	spec5, _ := w4.Finish()
	bsHud.HudPlayerSet(player, spec5)
	w6 := bsValues.NewValueWriter()
	w6.BeginMap().Key("listed").String("yes").End()
	spec7, _ := w6.Finish()
	bsHud.HudPlayerSet(player, spec7)
	w8 := bsValues.NewValueWriter()
	w8.BeginMap().Key("tab_header").Null().Key("tab_footer").Null().Key("list_name").Null().End()
	spec9, _ := w8.Finish()
	bsHud.HudPlayerSet(player, spec9)
	bsCommands.CommandReply(ctx, "tab done", nil)
}

func HudSurfaces(ctx bsCore.Ctx) {
	w1 := bsValues.NewValueWriter()
	w1.BeginMap().Key("title").String("<gold>Static").Key("lines").BeginList().String("<white>one").End().End()
	spec2, _ := w1.Finish()
	claim := bsHud.HudSurfaceClaim("sidebar", 5, spec2, HudRender)
	w3 := bsValues.NewValueWriter()
	w3.BeginMap().Key("title").String("<gold>Static").Key("lines").BeginList().String("<white>one").End().End()
	spec4, _ := w3.Finish()
	bsHud.HudSurfaceClaim("nowhere", 1, spec4, nil)
	w5 := bsValues.NewValueWriter()
	w5.BeginMap().Key("title").String("<gold>Changed").Key("lines").BeginList().String("<white>two").End().End()
	spec6, _ := w5.Finish()
	bsHud.HudSurfaceUpdate(claim, spec6)
	w7 := bsValues.NewValueWriter()
	w7.BeginMap().Key("title").String("<gold>Changed").Key("lines").BeginList().String("<white>two").End().End()
	spec8, _ := w7.Finish()
	bsHud.HudSurfaceAnswer(ctx, spec8)
	gClaim = claim
	bsCommands.CommandReply(ctx, "surfaces done", nil)
}

func HudSurfaceEnd(ctx bsCore.Ctx) {
	bsHud.HudSurfaceRelease(gClaim)
	bsHud.HudSurfaceRelease(gClaim)
	w1 := bsValues.NewValueWriter()
	w1.BeginMap().Key("title").String("<gold>Late").End()
	spec2, _ := w1.Finish()
	bsHud.HudSurfaceUpdate(gClaim, spec2)
	bsCommands.CommandReply(ctx, "surface end done", nil)
}

func Register() {
	bsCommands.Command("hud board", "", HudBoard)
	bsCommands.Command("hud bars", "", HudBars)
	bsCommands.Command("hud holograms", "", HudHolograms)
	bsCommands.Command("hud npcs", "", HudNpcs)
	bsCommands.Command("hud npc-end", "", HudNpcEnd)
	bsCommands.Command("hud tab", "", HudTab)
	bsCommands.Command("hud surfaces", "", HudSurfaces)
	bsCommands.Command("hud surface-end", "", HudSurfaceEnd)
}
