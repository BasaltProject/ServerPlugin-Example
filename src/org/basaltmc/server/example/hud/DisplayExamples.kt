package org.basaltmc.server.example.hud

import org.basaltmc.server.commands.command
import org.basaltmc.server.commands.commandReply
import org.basaltmc.server.commands.commandSourceName
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.hud.bossbarAttach
import org.basaltmc.server.hud.bossbarCreate
import org.basaltmc.server.hud.bossbarGet
import org.basaltmc.server.hud.bossbarKept
import org.basaltmc.server.hud.bossbarPlayerAdd
import org.basaltmc.server.hud.bossbarPlayerRemove
import org.basaltmc.server.hud.bossbarPlayers
import org.basaltmc.server.hud.bossbarRemove
import org.basaltmc.server.hud.bossbarUpdate
import org.basaltmc.server.hud.displayGet
import org.basaltmc.server.hud.displaySet
import org.basaltmc.server.hud.hologramCreate
import org.basaltmc.server.hud.hologramGet
import org.basaltmc.server.hud.hologramRemove
import org.basaltmc.server.hud.hologramSet
import org.basaltmc.server.hud.npcAnimate
import org.basaltmc.server.hud.npcAttribute
import org.basaltmc.server.hud.npcCreate
import org.basaltmc.server.hud.npcGet
import org.basaltmc.server.hud.npcItem
import org.basaltmc.server.hud.npcRemove
import org.basaltmc.server.hud.npcSet
import org.basaltmc.server.hud.objectiveAdd
import org.basaltmc.server.hud.objectiveRemove
import org.basaltmc.server.hud.playerGet
import org.basaltmc.server.hud.playerSet
import org.basaltmc.server.hud.scoreGet
import org.basaltmc.server.hud.scoreReset
import org.basaltmc.server.hud.scoreSet
import org.basaltmc.server.hud.scoreboardCreate
import org.basaltmc.server.hud.scoreboardList
import org.basaltmc.server.hud.scoreboardMain
import org.basaltmc.server.hud.scoreboardRelease
import org.basaltmc.server.hud.scoreboardShow
import org.basaltmc.server.hud.scoreboardShown
import org.basaltmc.server.hud.surfaceAnswer
import org.basaltmc.server.hud.surfaceClaim
import org.basaltmc.server.hud.surfaceRelease
import org.basaltmc.server.hud.surfaceUpdate
import org.basaltmc.server.hud.tabEntry
import org.basaltmc.server.hud.teamAdd
import org.basaltmc.server.hud.teamJoin
import org.basaltmc.server.hud.teamLeave
import org.basaltmc.server.hud.teamOf
import org.basaltmc.server.hud.teamRemove
import org.basaltmc.server.hud.teamSet
import org.basaltmc.server.log.logInfo
import org.basaltmc.server.players.playerByName
import org.basaltmc.server.players.playerName
import org.basaltmc.server.values.Value
import org.basaltmc.server.values.ValueWriter

object DisplayExamples {
    private var gNpc = 0
    private var gClaim = 0

    private fun entityOf(npc: Int): Int {
        val raw = ByteArray(768)
        val length = npcGet(npc, raw)
        if (length <= 0 || length > raw.size) return 0
        val value = Value.open(raw.copyOf(length)) ?: return 0
        return (value.find("id")?.asLong() ?: 0L).toInt()
    }

    private fun clicked(ctx: Ctx) {
        logInfo("npc clicked")
    }

    private fun render(ctx: Ctx) {
        val spec1 = ValueWriter().beginMap().key("title").string("<gold>Example").key("lines").beginList().string("<white>Hello %player%").string("<gray>second line").end().end().finish() ?: ByteArray(0)
        surfaceAnswer(ctx, spec1)
    }

    private fun board(ctx: Ctx) {
        val player = playerByName(commandSourceName(ctx) ?: "")
        val mainBoard = scoreboardMain()
        val own = scoreboardCreate()
        val spec1 = ValueWriter().beginMap().key("criterion").string("dummy").key("title").string("<gold>Kills").key("hearts").bool(false).end().finish() ?: ByteArray(0)
        objectiveAdd(own, "kills", spec1)
        val spec2 = ValueWriter().beginMap().key("criterion").string("no_such_criterion").end().finish() ?: ByteArray(0)
        objectiveAdd(own, "broken", spec2)
        val buf3 = ByteArray(256)
        scoreboardList(own, "objectives", buf3)
        displaySet(own, "sidebar", "kills")
        val buf4 = ByteArray(256)
        displayGet(own, "sidebar", buf4)
        displaySet(own, "sidebar", "missing")
        displaySet(own, "nowhere", "kills")
        scoreSet(own, "Steve", "kills", 7)
        scoreSet(own, "Alex", "kills", 3)
        val out5 = IntArray(1)
        scoreGet(own, "Steve", "kills", out5)
        val buf6 = ByteArray(256)
        scoreboardList(own, "entries", buf6)
        scoreReset(own, "Steve", "kills")
        val out7 = IntArray(1)
        scoreGet(own, "Steve", "kills", out7)
        teamAdd(own, "red")
        teamAdd(own, "red")
        val spec8 = ValueWriter().beginMap().key("display_name").string("<red>Red").key("prefix").string("[R] ").key("suffix").string("!").key("color").string("red").key("friendly_fire").bool(false).key("see_invisible").bool(true).key("name_tags").string("hideForOtherTeams").key("death_messages").string("never").key("collision").string("pushOtherTeams").end().finish() ?: ByteArray(0)
        teamSet(own, "red", spec8)
        val spec9 = ValueWriter().beginMap().key("color").string("blue").end().finish() ?: ByteArray(0)
        teamSet(own, "blue", spec9)
        teamJoin(own, "red", "Steve")
        teamJoin(own, "gone", "Steve")
        val buf10 = ByteArray(256)
        teamOf(own, "Steve", buf10)
        val buf11 = ByteArray(256)
        scoreboardList(own, "teams", buf11)
        val buf12 = ByteArray(256)
        scoreboardList(own, "bogus", buf12)
        teamLeave(own, "red", "Steve")
        teamLeave(own, "red", "Steve")
        val buf13 = ByteArray(256)
        teamOf(own, "Steve", buf13)
        teamRemove(own, "red")
        teamRemove(own, "red")
        scoreboardShow(player, own)
        scoreboardShown(player)
        scoreboardShow(player, mainBoard)
        scoreboardShown(player)
        objectiveRemove(own, "kills")
        objectiveRemove(own, "kills")
        scoreboardRelease(own)
        scoreboardRelease(own)
        scoreboardRelease(mainBoard)
        commandReply(ctx, "board done")
    }

    private fun bars(ctx: Ctx) {
        val player = playerByName(commandSourceName(ctx) ?: "")
        val spec1 = ValueWriter().beginMap().key("title").string("<green>Wave 1").key("color").string("green").key("style").string("notched_10").key("progress").float(0.5).key("darken_sky").bool(false).key("boss_music").bool(false).key("fog").bool(false).key("visible").bool(true).key("key").string("example:wave").end().finish() ?: ByteArray(0)
        val bar = bossbarCreate(spec1)
        val spec2 = ValueWriter().beginMap().key("title").string("<green>Wave 1").key("key").string("example:wave").end().finish() ?: ByteArray(0)
        bossbarCreate(spec2)
        val buf3 = ByteArray(512)
        bossbarKept(buf3)
        val buf4 = ByteArray(512)
        bossbarGet(bar, buf4)
        val attached = bossbarAttach("example:wave")
        bossbarAttach("example:none")
        bossbarPlayerAdd(bar, player)
        bossbarPlayerAdd(bar, player)
        val buf5 = ByteArray(512)
        bossbarPlayers(bar, buf5)
        val spec6 = ValueWriter().beginMap().key("title").string("<red>Wave 2").key("color").string("red").key("progress").float(0.25).end().finish() ?: ByteArray(0)
        bossbarUpdate(bar, spec6)
        val spec7 = ValueWriter().beginMap().key("progress").string("half").end().finish() ?: ByteArray(0)
        bossbarUpdate(bar, spec7)
        bossbarPlayerRemove(bar, player)
        bossbarPlayerRemove(bar, player)
        bossbarRemove(bar)
        val spec8 = ValueWriter().beginMap().key("title").string("<red>Late").end().finish() ?: ByteArray(0)
        bossbarUpdate(attached, spec8)
        bossbarRemove(bar)
        commandReply(ctx, "bars done")
    }

    private fun holograms(ctx: Ctx) {
        val player = playerByName(commandSourceName(ctx) ?: "")
        val name = playerName(player) ?: ""
        val holo = hologramCreate("world", "<yellow>Hello", 0.5, 64.0, 0.5)
        hologramCreate("nowhere", "text", 0.0, 0.0, 0.0)
        val spec1 = ValueWriter().beginMap().key("text").string("<yellow>Moved").key("x").float(2.5).key("y").float(65.0).key("z").float(-1.5).end().finish() ?: ByteArray(0)
        hologramSet(holo, spec1)
        val spec2 = ValueWriter().beginMap().key("x").float(1.0).end().finish() ?: ByteArray(0)
        hologramSet(holo, spec2)
        val spec3 = ValueWriter().beginMap().key("audience").beginList().string(name).end().end().finish() ?: ByteArray(0)
        hologramSet(holo, spec3)
        val buf4 = ByteArray(512)
        hologramGet(holo, buf4)
        val spec5 = ValueWriter().beginMap().key("audience").nul().end().finish() ?: ByteArray(0)
        hologramSet(holo, spec5)
        hologramRemove(holo)
        hologramRemove(holo)
        commandReply(ctx, "holograms done")
    }

    private fun npcs(ctx: Ctx) {
        val player = playerByName(commandSourceName(ctx) ?: "")
        val spec1 = ValueWriter().beginMap().key("world").string("world").key("name").string("<aqua>Guide").key("x").float(1.5).key("y").float(64.0).key("z").float(2.5).end().finish() ?: ByteArray(0)
        val npc = npcCreate(spec1, ::clicked)
        val spec2 = ValueWriter().beginMap().key("world").string("nowhere").key("name").string("Nobody").key("x").float(0.0).key("y").float(0.0).key("z").float(0.0).end().finish() ?: ByteArray(0)
        npcCreate(spec2, null)
        val spec3 = ValueWriter().beginMap().key("name").string("<aqua>Guide II").key("x").float(3.5).key("y").float(65.0).key("z").float(4.5).key("yaw").float(90.0).key("pitch").float(-15.0).key("looks_at_players").bool(true).key("health").float(18.0).key("held_slot").int(0).key("flags").int(0).key("pose").int(0).key("attributes").beginMap().key("max_health").float(30.0).end().key("equipment").beginMap().key("0").beginMap().key("item").string("minecraft:diamond_sword").key("count").int(1).end().end().end().finish() ?: ByteArray(0)
        npcSet(npc, spec3)
        val spec4 = ValueWriter().beginMap().key("attributes").beginMap().key("no_such_attribute").float(1.0).end().end().finish() ?: ByteArray(0)
        npcSet(npc, spec4)
        val buf5 = ByteArray(768)
        npcGet(npc, buf5)
        val buf6 = ByteArray(768)
        npcItem(npc, 0, buf6)
        val buf7 = ByteArray(768)
        npcItem(npc, 1, buf7)
        val out8 = DoubleArray(1)
        npcAttribute(npc, "max_health", out8)
        val out9 = DoubleArray(1)
        npcAttribute(npc, "no_such_attribute", out9)
        npcAnimate(npc, 0)
        val entity = entityOf(npc)
        tabEntry(player, entity, 0)
        tabEntry(player, 424242, 0)
        gNpc = npc
        commandReply(ctx, "npcs done")
    }

    private fun npcEnd(ctx: Ctx) {
        npcRemove(gNpc)
        npcAnimate(gNpc, 0)
        commandReply(ctx, "npc end done")
    }

    private fun tab(ctx: Ctx) {
        val player = playerByName(commandSourceName(ctx) ?: "")
        val spec1 = ValueWriter().beginMap().key("tab_header").string("<green>Top").key("tab_footer").string("<gray>Bottom").key("list_name").string("<gold>Steve").key("listed").bool(true).key("list_order").int(3).key("score").int(5).key("waypoint_style").string("default").key("waypoint_color").int(16711680).end().finish() ?: ByteArray(0)
        playerSet(player, spec1)
        val buf2 = ByteArray(768)
        playerGet(player, buf2)
        val spec3 = ValueWriter().beginMap().key("not_a_property").int(1).end().finish() ?: ByteArray(0)
        playerSet(player, spec3)
        val spec4 = ValueWriter().beginMap().key("listed").string("yes").end().finish() ?: ByteArray(0)
        playerSet(player, spec4)
        val spec5 = ValueWriter().beginMap().key("tab_header").nul().key("tab_footer").nul().key("list_name").nul().end().finish() ?: ByteArray(0)
        playerSet(player, spec5)
        commandReply(ctx, "tab done")
    }

    private fun surfaces(ctx: Ctx) {
        val spec1 = ValueWriter().beginMap().key("title").string("<gold>Static").key("lines").beginList().string("<white>one").end().end().finish() ?: ByteArray(0)
        val claim = surfaceClaim("sidebar", 5, spec1, ::render)
        val spec2 = ValueWriter().beginMap().key("title").string("<gold>Static").key("lines").beginList().string("<white>one").end().end().finish() ?: ByteArray(0)
        surfaceClaim("nowhere", 1, spec2, null)
        val spec3 = ValueWriter().beginMap().key("title").string("<gold>Changed").key("lines").beginList().string("<white>two").end().end().finish() ?: ByteArray(0)
        surfaceUpdate(claim, spec3)
        val spec4 = ValueWriter().beginMap().key("title").string("<gold>Changed").key("lines").beginList().string("<white>two").end().end().finish() ?: ByteArray(0)
        surfaceAnswer(ctx, spec4)
        gClaim = claim
        commandReply(ctx, "surfaces done")
    }

    private fun surfaceEnd(ctx: Ctx) {
        surfaceRelease(gClaim)
        surfaceRelease(gClaim)
        val spec1 = ValueWriter().beginMap().key("title").string("<gold>Late").end().finish() ?: ByteArray(0)
        surfaceUpdate(gClaim, spec1)
        commandReply(ctx, "surface end done")
    }

    fun register() {
        command("hud board", "", ::board)
        command("hud bars", "", ::bars)
        command("hud holograms", "", ::holograms)
        command("hud npcs", "", ::npcs)
        command("hud npc-end", "", ::npcEnd)
        command("hud tab", "", ::tab)
        command("hud surfaces", "", ::surfaces)
        command("hud surface-end", "", ::surfaceEnd)
    }
}
