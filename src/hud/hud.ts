import * as bsCommands from "basalt-server/commands";
import * as bsCore from "basalt-server/core";
import * as bsHud from "basalt-server/hud";
import * as bsLog from "basalt-server/log";
import * as bsPlayers from "basalt-server/players";
import * as bsValues from "basalt-server/values";

let gNpc: i32 = 0;
let gClaim: i32 = 0;

function entityOf(npc: i32): i32 {
  const raw = new Uint8Array(768);
  const length = bsHud.hudNpcGet(npc, raw);
  if (length <= 0 || length > raw.length) return 0;
  const value = bsValues.Value.open(raw.slice(0, length));
  if (value === null) return 0;
  const id = value.find("id");
  return id === null ? 0 : <i32>id.toInt(0);
}

function clicked(ctx: bsCore.Ctx): void {
  bsLog.logInfo("npc clicked");
}

function render(ctx: bsCore.Ctx): void {
  const spec1 = new bsValues.ValueWriter().beginMap().key("title").string("<gold>Example").key("lines").beginList().string("<white>Hello %player%").string("<gray>second line").end().end().finish()!;
  bsHud.hudSurfaceAnswer(ctx, spec1);
}

function board(ctx: bsCore.Ctx): void {
  const player = bsPlayers.playerByName(bsCommands.commandSourceName(ctx)!);
  const mainBoard = bsHud.hudScoreboardMain();
  const own = bsHud.hudScoreboardCreate();
  const spec1 = new bsValues.ValueWriter().beginMap().key("criterion").string("dummy").key("title").string("<gold>Kills").key("hearts").boolean(false).end().finish()!;
  bsHud.hudObjectiveAdd(own, "kills", spec1);
  const spec2 = new bsValues.ValueWriter().beginMap().key("criterion").string("no_such_criterion").end().finish()!;
  bsHud.hudObjectiveAdd(own, "broken", spec2);
  const buf3 = new Uint8Array(256);
  bsHud.hudScoreboardList(own, "objectives", buf3);
  bsHud.hudDisplaySet(own, "sidebar", "kills");
  const buf4 = new Uint8Array(256);
  bsHud.hudDisplayGet(own, "sidebar", buf4);
  bsHud.hudDisplaySet(own, "sidebar", "missing");
  bsHud.hudDisplaySet(own, "nowhere", "kills");
  bsHud.hudScoreSet(own, "Steve", "kills", 7);
  bsHud.hudScoreSet(own, "Alex", "kills", 3);
  const out5 = new Int32Array(1);
  bsHud.hudScoreGet(own, "Steve", "kills", out5);
  const buf6 = new Uint8Array(256);
  bsHud.hudScoreboardList(own, "entries", buf6);
  bsHud.hudScoreReset(own, "Steve", "kills");
  const out7 = new Int32Array(1);
  bsHud.hudScoreGet(own, "Steve", "kills", out7);
  bsHud.hudTeamAdd(own, "red");
  bsHud.hudTeamAdd(own, "red");
  const spec8 = new bsValues.ValueWriter().beginMap().key("display_name").string("<red>Red").key("prefix").string("[R] ").key("suffix").string("!").key("color").string("red").key("friendly_fire").boolean(false).key("see_invisible").boolean(true).key("name_tags").string("hideForOtherTeams").key("death_messages").string("never").key("collision").string("pushOtherTeams").end().finish()!;
  bsHud.hudTeamSet(own, "red", spec8);
  const spec9 = new bsValues.ValueWriter().beginMap().key("color").string("blue").end().finish()!;
  bsHud.hudTeamSet(own, "blue", spec9);
  bsHud.hudTeamJoin(own, "red", "Steve");
  bsHud.hudTeamJoin(own, "gone", "Steve");
  const buf10 = new Uint8Array(256);
  bsHud.hudTeamOf(own, "Steve", buf10);
  const buf11 = new Uint8Array(256);
  bsHud.hudScoreboardList(own, "teams", buf11);
  const buf12 = new Uint8Array(256);
  bsHud.hudScoreboardList(own, "bogus", buf12);
  bsHud.hudTeamLeave(own, "red", "Steve");
  bsHud.hudTeamLeave(own, "red", "Steve");
  const buf13 = new Uint8Array(256);
  bsHud.hudTeamOf(own, "Steve", buf13);
  bsHud.hudTeamRemove(own, "red");
  bsHud.hudTeamRemove(own, "red");
  bsHud.hudScoreboardShow(player, own);
  bsHud.hudScoreboardShown(player);
  bsHud.hudScoreboardShow(player, mainBoard);
  bsHud.hudScoreboardShown(player);
  bsHud.hudObjectiveRemove(own, "kills");
  bsHud.hudObjectiveRemove(own, "kills");
  bsHud.hudScoreboardRelease(own);
  bsHud.hudScoreboardRelease(own);
  bsHud.hudScoreboardRelease(mainBoard);
  bsCommands.commandReply(ctx, "board done");
}

function bars(ctx: bsCore.Ctx): void {
  const player = bsPlayers.playerByName(bsCommands.commandSourceName(ctx)!);
  const spec1 = new bsValues.ValueWriter().beginMap().key("title").string("<green>Wave 1").key("color").string("green").key("style").string("notched_10").key("progress").float(0.5).key("darken_sky").boolean(false).key("boss_music").boolean(false).key("fog").boolean(false).key("visible").boolean(true).key("key").string("example:wave").end().finish()!;
  const bar = bsHud.hudBossbarCreate(spec1);
  const spec2 = new bsValues.ValueWriter().beginMap().key("title").string("<green>Wave 1").key("key").string("example:wave").end().finish()!;
  bsHud.hudBossbarCreate(spec2);
  const buf3 = new Uint8Array(512);
  bsHud.hudBossbarKept(buf3);
  const buf4 = new Uint8Array(512);
  bsHud.hudBossbarGet(bar, buf4);
  const attached = bsHud.hudBossbarAttach("example:wave");
  bsHud.hudBossbarAttach("example:none");
  bsHud.hudBossbarPlayerAdd(bar, player);
  bsHud.hudBossbarPlayerAdd(bar, player);
  const buf5 = new Uint8Array(512);
  bsHud.hudBossbarPlayers(bar, buf5);
  const spec6 = new bsValues.ValueWriter().beginMap().key("title").string("<red>Wave 2").key("color").string("red").key("progress").float(0.25).end().finish()!;
  bsHud.hudBossbarUpdate(bar, spec6);
  const spec7 = new bsValues.ValueWriter().beginMap().key("progress").string("half").end().finish()!;
  bsHud.hudBossbarUpdate(bar, spec7);
  bsHud.hudBossbarPlayerRemove(bar, player);
  bsHud.hudBossbarPlayerRemove(bar, player);
  bsHud.hudBossbarRemove(bar);
  const spec8 = new bsValues.ValueWriter().beginMap().key("title").string("<red>Late").end().finish()!;
  bsHud.hudBossbarUpdate(attached, spec8);
  bsHud.hudBossbarRemove(bar);
  bsCommands.commandReply(ctx, "bars done");
}

function holograms(ctx: bsCore.Ctx): void {
  const player = bsPlayers.playerByName(bsCommands.commandSourceName(ctx)!);
  const name = bsPlayers.playerName(player)!;
  const holo = bsHud.hudHologramCreate("world", "<yellow>Hello", 0.5, 64.0, 0.5);
  bsHud.hudHologramCreate("nowhere", "text", 0.0, 0.0, 0.0);
  const spec1 = new bsValues.ValueWriter().beginMap().key("text").string("<yellow>Moved").key("x").float(2.5).key("y").float(65.0).key("z").float(-1.5).end().finish()!;
  bsHud.hudHologramSet(holo, spec1);
  const spec2 = new bsValues.ValueWriter().beginMap().key("x").float(1.0).end().finish()!;
  bsHud.hudHologramSet(holo, spec2);
  const spec3 = new bsValues.ValueWriter().beginMap().key("audience").beginList().string(name).end().end().finish()!;
  bsHud.hudHologramSet(holo, spec3);
  const buf4 = new Uint8Array(512);
  bsHud.hudHologramGet(holo, buf4);
  const spec5 = new bsValues.ValueWriter().beginMap().key("audience").nullValue().end().finish()!;
  bsHud.hudHologramSet(holo, spec5);
  bsHud.hudHologramRemove(holo);
  bsHud.hudHologramRemove(holo);
  bsCommands.commandReply(ctx, "holograms done");
}

function npcs(ctx: bsCore.Ctx): void {
  const player = bsPlayers.playerByName(bsCommands.commandSourceName(ctx)!);
  const spec1 = new bsValues.ValueWriter().beginMap().key("world").string("world").key("name").string("<aqua>Guide").key("x").float(1.5).key("y").float(64.0).key("z").float(2.5).end().finish()!;
  const npc = bsHud.hudNpcCreate(spec1, clicked);
  const spec2 = new bsValues.ValueWriter().beginMap().key("world").string("nowhere").key("name").string("Nobody").key("x").float(0.0).key("y").float(0.0).key("z").float(0.0).end().finish()!;
  bsHud.hudNpcCreate(spec2, null);
  const spec3 = new bsValues.ValueWriter().beginMap().key("name").string("<aqua>Guide II").key("x").float(3.5).key("y").float(65.0).key("z").float(4.5).key("yaw").float(90.0).key("pitch").float(-15.0).key("looks_at_players").boolean(true).key("health").float(18.0).key("held_slot").int(0).key("flags").int(0).key("pose").int(0).key("attributes").beginMap().key("max_health").float(30.0).end().key("equipment").beginMap().key("0").beginMap().key("item").string("minecraft:diamond_sword").key("count").int(1).end().end().end().finish()!;
  bsHud.hudNpcSet(npc, spec3);
  const spec4 = new bsValues.ValueWriter().beginMap().key("attributes").beginMap().key("no_such_attribute").float(1.0).end().end().finish()!;
  bsHud.hudNpcSet(npc, spec4);
  const buf5 = new Uint8Array(768);
  bsHud.hudNpcGet(npc, buf5);
  const buf6 = new Uint8Array(768);
  bsHud.hudNpcItem(npc, 0, buf6);
  const buf7 = new Uint8Array(768);
  bsHud.hudNpcItem(npc, 1, buf7);
  const out8 = new Float64Array(1);
  bsHud.hudNpcAttribute(npc, "max_health", out8);
  const out9 = new Float64Array(1);
  bsHud.hudNpcAttribute(npc, "no_such_attribute", out9);
  bsHud.hudNpcAnimate(npc, 0);
  const entity = entityOf(npc);
  bsHud.hudTabEntry(player, entity, 0);
  bsHud.hudTabEntry(player, 424242, 0);
  gNpc = npc;
  bsCommands.commandReply(ctx, "npcs done");
}

function npcEnd(ctx: bsCore.Ctx): void {
  bsHud.hudNpcRemove(gNpc);
  bsHud.hudNpcAnimate(gNpc, 0);
  bsCommands.commandReply(ctx, "npc end done");
}

function tab(ctx: bsCore.Ctx): void {
  const player = bsPlayers.playerByName(bsCommands.commandSourceName(ctx)!);
  const spec1 = new bsValues.ValueWriter().beginMap().key("tab_header").string("<green>Top").key("tab_footer").string("<gray>Bottom").key("list_name").string("<gold>Steve").key("listed").boolean(true).key("list_order").int(3).key("score").int(5).key("waypoint_style").string("default").key("waypoint_color").int(16711680).end().finish()!;
  bsHud.hudPlayerSet(player, spec1);
  const buf2 = new Uint8Array(768);
  bsHud.hudPlayerGet(player, buf2);
  const spec3 = new bsValues.ValueWriter().beginMap().key("not_a_property").int(1).end().finish()!;
  bsHud.hudPlayerSet(player, spec3);
  const spec4 = new bsValues.ValueWriter().beginMap().key("listed").string("yes").end().finish()!;
  bsHud.hudPlayerSet(player, spec4);
  const spec5 = new bsValues.ValueWriter().beginMap().key("tab_header").nullValue().key("tab_footer").nullValue().key("list_name").nullValue().end().finish()!;
  bsHud.hudPlayerSet(player, spec5);
  bsCommands.commandReply(ctx, "tab done");
}

function surfaces(ctx: bsCore.Ctx): void {
  const spec1 = new bsValues.ValueWriter().beginMap().key("title").string("<gold>Static").key("lines").beginList().string("<white>one").end().end().finish()!;
  const claim = bsHud.hudSurfaceClaim("sidebar", 5, spec1, render);
  const spec2 = new bsValues.ValueWriter().beginMap().key("title").string("<gold>Static").key("lines").beginList().string("<white>one").end().end().finish()!;
  bsHud.hudSurfaceClaim("nowhere", 1, spec2, null);
  const spec3 = new bsValues.ValueWriter().beginMap().key("title").string("<gold>Changed").key("lines").beginList().string("<white>two").end().end().finish()!;
  bsHud.hudSurfaceUpdate(claim, spec3);
  const spec4 = new bsValues.ValueWriter().beginMap().key("title").string("<gold>Changed").key("lines").beginList().string("<white>two").end().end().finish()!;
  bsHud.hudSurfaceAnswer(ctx, spec4);
  gClaim = claim;
  bsCommands.commandReply(ctx, "surfaces done");
}

function surfaceEnd(ctx: bsCore.Ctx): void {
  bsHud.hudSurfaceRelease(gClaim);
  bsHud.hudSurfaceRelease(gClaim);
  const spec1 = new bsValues.ValueWriter().beginMap().key("title").string("<gold>Late").end().finish()!;
  bsHud.hudSurfaceUpdate(gClaim, spec1);
  bsCommands.commandReply(ctx, "surface end done");
}

export function register(): void {
  bsCommands.command("hud board", "", board);
  bsCommands.command("hud bars", "", bars);
  bsCommands.command("hud holograms", "", holograms);
  bsCommands.command("hud npcs", "", npcs);
  bsCommands.command("hud npc-end", "", npcEnd);
  bsCommands.command("hud tab", "", tab);
  bsCommands.command("hud surfaces", "", surfaces);
  bsCommands.command("hud surface-end", "", surfaceEnd);
}
