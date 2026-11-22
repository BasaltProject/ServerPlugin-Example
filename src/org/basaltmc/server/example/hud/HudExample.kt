package org.basaltmc.server.example.hud


import org.basaltmc.server.core.Ctx

object HudExample {
    private var gNpc = 0
    private var gClaim = 0

    private fun entityOf(npc: Int): Int {
        val raw = ByteArray(768)
        val length = org.basaltmc.server.hud.npcGet(npc, raw)
        if (length <= 0 || length > raw.size) return 0
        val value = org.basaltmc.server.values.Value.open(raw.copyOf(length)) ?: return 0
        return (value.find("id")?.asLong() ?: 0L).toInt()
    }

    private fun clicked(ctx: Ctx) {
        org.basaltmc.server.log.logInfo("npc clicked")
    }

    private fun render(ctx: Ctx) {
        val spec1 = org.basaltmc.server.values.ValueWriter().beginMap().key("title").string("<gold>Example").key("lines").beginList().string("<white>Hello %player%").string("<gray>second line").end().end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.surfaceAnswer(ctx, spec1)
    }

    private fun board(ctx: Ctx) {
        val player = org.basaltmc.server.players.playerByName(org.basaltmc.server.commands.commandSourceName(ctx) ?: "")
        val mainBoard = org.basaltmc.server.hud.scoreboardMain()
        val own = org.basaltmc.server.hud.scoreboardCreate()
        val spec1 = org.basaltmc.server.values.ValueWriter().beginMap().key("criterion").string("dummy").key("title").string("<gold>Kills").key("hearts").bool(false).end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.objectiveAdd(own, "kills", spec1)
        val spec2 = org.basaltmc.server.values.ValueWriter().beginMap().key("criterion").string("no_such_criterion").end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.objectiveAdd(own, "broken", spec2)
        val buf3 = ByteArray(256)
        org.basaltmc.server.hud.scoreboardList(own, "objectives", buf3)
        org.basaltmc.server.hud.displaySet(own, "sidebar", "kills")
        val buf4 = ByteArray(256)
        org.basaltmc.server.hud.displayGet(own, "sidebar", buf4)
        org.basaltmc.server.hud.displaySet(own, "sidebar", "missing")
        org.basaltmc.server.hud.displaySet(own, "nowhere", "kills")
        org.basaltmc.server.hud.scoreSet(own, "Steve", "kills", 7)
        org.basaltmc.server.hud.scoreSet(own, "Alex", "kills", 3)
        val out5 = IntArray(1)
        org.basaltmc.server.hud.scoreGet(own, "Steve", "kills", out5)
        val buf6 = ByteArray(256)
        org.basaltmc.server.hud.scoreboardList(own, "entries", buf6)
        org.basaltmc.server.hud.scoreReset(own, "Steve", "kills")
        val out7 = IntArray(1)
        org.basaltmc.server.hud.scoreGet(own, "Steve", "kills", out7)
        org.basaltmc.server.hud.teamAdd(own, "red")
        org.basaltmc.server.hud.teamAdd(own, "red")
        val spec8 = org.basaltmc.server.values.ValueWriter().beginMap().key("display_name").string("<red>Red").key("prefix").string("[R] ").key("suffix").string("!").key("color").string("red").key("friendly_fire").bool(false).key("see_invisible").bool(true).key("name_tags").string("hideForOtherTeams").key("death_messages").string("never").key("collision").string("pushOtherTeams").end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.teamSet(own, "red", spec8)
        val spec9 = org.basaltmc.server.values.ValueWriter().beginMap().key("color").string("blue").end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.teamSet(own, "blue", spec9)
        org.basaltmc.server.hud.teamJoin(own, "red", "Steve")
        org.basaltmc.server.hud.teamJoin(own, "gone", "Steve")
        val buf10 = ByteArray(256)
        org.basaltmc.server.hud.teamOf(own, "Steve", buf10)
        val buf11 = ByteArray(256)
        org.basaltmc.server.hud.scoreboardList(own, "teams", buf11)
        val buf12 = ByteArray(256)
        org.basaltmc.server.hud.scoreboardList(own, "bogus", buf12)
        org.basaltmc.server.hud.teamLeave(own, "red", "Steve")
        org.basaltmc.server.hud.teamLeave(own, "red", "Steve")
        val buf13 = ByteArray(256)
        org.basaltmc.server.hud.teamOf(own, "Steve", buf13)
        org.basaltmc.server.hud.teamRemove(own, "red")
        org.basaltmc.server.hud.teamRemove(own, "red")
        org.basaltmc.server.hud.scoreboardShow(player, own)
        org.basaltmc.server.hud.scoreboardShown(player)
        org.basaltmc.server.hud.scoreboardShow(player, mainBoard)
        org.basaltmc.server.hud.scoreboardShown(player)
        org.basaltmc.server.hud.objectiveRemove(own, "kills")
        org.basaltmc.server.hud.objectiveRemove(own, "kills")
        org.basaltmc.server.hud.scoreboardRelease(own)
        org.basaltmc.server.hud.scoreboardRelease(own)
        org.basaltmc.server.hud.scoreboardRelease(mainBoard)
        org.basaltmc.server.commands.commandReply(ctx, "board done")
    }

    private fun bars(ctx: Ctx) {
        val player = org.basaltmc.server.players.playerByName(org.basaltmc.server.commands.commandSourceName(ctx) ?: "")
        val spec1 = org.basaltmc.server.values.ValueWriter().beginMap().key("title").string("<green>Wave 1").key("color").string("green").key("style").string("notched_10").key("progress").float(0.5).key("darken_sky").bool(false).key("boss_music").bool(false).key("fog").bool(false).key("visible").bool(true).key("key").string("example:wave").end().finish() ?: ByteArray(0)
        val bar = org.basaltmc.server.hud.bossbarCreate(spec1)
        val spec2 = org.basaltmc.server.values.ValueWriter().beginMap().key("title").string("<green>Wave 1").key("key").string("example:wave").end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.bossbarCreate(spec2)
        val buf3 = ByteArray(512)
        org.basaltmc.server.hud.bossbarKept(buf3)
        val buf4 = ByteArray(512)
        org.basaltmc.server.hud.bossbarGet(bar, buf4)
        val attached = org.basaltmc.server.hud.bossbarAttach("example:wave")
        org.basaltmc.server.hud.bossbarAttach("example:none")
        org.basaltmc.server.hud.bossbarPlayerAdd(bar, player)
        org.basaltmc.server.hud.bossbarPlayerAdd(bar, player)
        val buf5 = ByteArray(512)
        org.basaltmc.server.hud.bossbarPlayers(bar, buf5)
        val spec6 = org.basaltmc.server.values.ValueWriter().beginMap().key("title").string("<red>Wave 2").key("color").string("red").key("progress").float(0.25).end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.bossbarUpdate(bar, spec6)
        val spec7 = org.basaltmc.server.values.ValueWriter().beginMap().key("progress").string("half").end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.bossbarUpdate(bar, spec7)
        org.basaltmc.server.hud.bossbarPlayerRemove(bar, player)
        org.basaltmc.server.hud.bossbarPlayerRemove(bar, player)
        org.basaltmc.server.hud.bossbarRemove(bar)
        val spec8 = org.basaltmc.server.values.ValueWriter().beginMap().key("title").string("<red>Late").end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.bossbarUpdate(attached, spec8)
        org.basaltmc.server.hud.bossbarRemove(bar)
        org.basaltmc.server.commands.commandReply(ctx, "bars done")
    }

    private fun holograms(ctx: Ctx) {
        val player = org.basaltmc.server.players.playerByName(org.basaltmc.server.commands.commandSourceName(ctx) ?: "")
        val name = org.basaltmc.server.players.playerName(player) ?: ""
        val holo = org.basaltmc.server.hud.hologramCreate("world", "<yellow>Hello", 0.5, 64.0, 0.5)
        org.basaltmc.server.hud.hologramCreate("nowhere", "text", 0.0, 0.0, 0.0)
        val spec1 = org.basaltmc.server.values.ValueWriter().beginMap().key("text").string("<yellow>Moved").key("x").float(2.5).key("y").float(65.0).key("z").float(-1.5).end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.hologramSet(holo, spec1)
        val spec2 = org.basaltmc.server.values.ValueWriter().beginMap().key("x").float(1.0).end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.hologramSet(holo, spec2)
        val spec3 = org.basaltmc.server.values.ValueWriter().beginMap().key("audience").beginList().string(name).end().end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.hologramSet(holo, spec3)
        val buf4 = ByteArray(512)
        org.basaltmc.server.hud.hologramGet(holo, buf4)
        val spec5 = org.basaltmc.server.values.ValueWriter().beginMap().key("audience").nul().end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.hologramSet(holo, spec5)
        org.basaltmc.server.hud.hologramRemove(holo)
        org.basaltmc.server.hud.hologramRemove(holo)
        org.basaltmc.server.commands.commandReply(ctx, "holograms done")
    }

    private fun npcs(ctx: Ctx) {
        val player = org.basaltmc.server.players.playerByName(org.basaltmc.server.commands.commandSourceName(ctx) ?: "")
        val spec1 = org.basaltmc.server.values.ValueWriter().beginMap().key("world").string("world").key("name").string("<aqua>Guide").key("x").float(1.5).key("y").float(64.0).key("z").float(2.5).end().finish() ?: ByteArray(0)
        val npc = org.basaltmc.server.hud.npcCreate(spec1, ::clicked)
        val spec2 = org.basaltmc.server.values.ValueWriter().beginMap().key("world").string("nowhere").key("name").string("Nobody").key("x").float(0.0).key("y").float(0.0).key("z").float(0.0).end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.npcCreate(spec2, null)
        val spec3 = org.basaltmc.server.values.ValueWriter().beginMap().key("name").string("<aqua>Guide II").key("x").float(3.5).key("y").float(65.0).key("z").float(4.5).key("yaw").float(90.0).key("pitch").float(-15.0).key("looks_at_players").bool(true).key("health").float(18.0).key("held_slot").int(0).key("flags").int(0).key("pose").int(0).key("attributes").beginMap().key("max_health").float(30.0).end().key("equipment").beginMap().key("0").beginMap().key("item").string("minecraft:diamond_sword").key("count").int(1).end().end().end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.npcSet(npc, spec3)
        val spec4 = org.basaltmc.server.values.ValueWriter().beginMap().key("attributes").beginMap().key("no_such_attribute").float(1.0).end().end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.npcSet(npc, spec4)
        val buf5 = ByteArray(768)
        org.basaltmc.server.hud.npcGet(npc, buf5)
        val buf6 = ByteArray(768)
        org.basaltmc.server.hud.npcItem(npc, 0, buf6)
        val buf7 = ByteArray(768)
        org.basaltmc.server.hud.npcItem(npc, 1, buf7)
        val out8 = DoubleArray(1)
        org.basaltmc.server.hud.npcAttribute(npc, "max_health", out8)
        val out9 = DoubleArray(1)
        org.basaltmc.server.hud.npcAttribute(npc, "no_such_attribute", out9)
        org.basaltmc.server.hud.npcAnimate(npc, 0)
        val entity = entityOf(npc)
        org.basaltmc.server.hud.tabEntry(player, entity, 0)
        org.basaltmc.server.hud.tabEntry(player, 424242, 0)
        gNpc = npc
        org.basaltmc.server.commands.commandReply(ctx, "npcs done")
    }

    private fun npcEnd(ctx: Ctx) {
        org.basaltmc.server.hud.npcRemove(gNpc)
        org.basaltmc.server.hud.npcAnimate(gNpc, 0)
        org.basaltmc.server.commands.commandReply(ctx, "npc end done")
    }

    private fun tab(ctx: Ctx) {
        val player = org.basaltmc.server.players.playerByName(org.basaltmc.server.commands.commandSourceName(ctx) ?: "")
        val spec1 = org.basaltmc.server.values.ValueWriter().beginMap().key("tab_header").string("<green>Top").key("tab_footer").string("<gray>Bottom").key("list_name").string("<gold>Steve").key("listed").bool(true).key("list_order").int(3).key("score").int(5).key("waypoint_style").string("default").key("waypoint_color").int(16711680).end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.playerSet(player, spec1)
        val buf2 = ByteArray(768)
        org.basaltmc.server.hud.playerGet(player, buf2)
        val spec3 = org.basaltmc.server.values.ValueWriter().beginMap().key("not_a_property").int(1).end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.playerSet(player, spec3)
        val spec4 = org.basaltmc.server.values.ValueWriter().beginMap().key("listed").string("yes").end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.playerSet(player, spec4)
        val spec5 = org.basaltmc.server.values.ValueWriter().beginMap().key("tab_header").nul().key("tab_footer").nul().key("list_name").nul().end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.playerSet(player, spec5)
        org.basaltmc.server.commands.commandReply(ctx, "tab done")
    }

    private fun surfaces(ctx: Ctx) {
        val spec1 = org.basaltmc.server.values.ValueWriter().beginMap().key("title").string("<gold>Static").key("lines").beginList().string("<white>one").end().end().finish() ?: ByteArray(0)
        val claim = org.basaltmc.server.hud.surfaceClaim("sidebar", 5, spec1, ::render)
        val spec2 = org.basaltmc.server.values.ValueWriter().beginMap().key("title").string("<gold>Static").key("lines").beginList().string("<white>one").end().end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.surfaceClaim("nowhere", 1, spec2, null)
        val spec3 = org.basaltmc.server.values.ValueWriter().beginMap().key("title").string("<gold>Changed").key("lines").beginList().string("<white>two").end().end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.surfaceUpdate(claim, spec3)
        val spec4 = org.basaltmc.server.values.ValueWriter().beginMap().key("title").string("<gold>Changed").key("lines").beginList().string("<white>two").end().end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.surfaceAnswer(ctx, spec4)
        gClaim = claim
        org.basaltmc.server.commands.commandReply(ctx, "surfaces done")
    }

    private fun surfaceEnd(ctx: Ctx) {
        org.basaltmc.server.hud.surfaceRelease(gClaim)
        org.basaltmc.server.hud.surfaceRelease(gClaim)
        val spec1 = org.basaltmc.server.values.ValueWriter().beginMap().key("title").string("<gold>Late").end().finish() ?: ByteArray(0)
        org.basaltmc.server.hud.surfaceUpdate(gClaim, spec1)
        org.basaltmc.server.commands.commandReply(ctx, "surface end done")
    }

    fun register() {
        org.basaltmc.server.commands.command("hud board", "", ::board)
        org.basaltmc.server.commands.command("hud bars", "", ::bars)
        org.basaltmc.server.commands.command("hud holograms", "", ::holograms)
        org.basaltmc.server.commands.command("hud npcs", "", ::npcs)
        org.basaltmc.server.commands.command("hud npc-end", "", ::npcEnd)
        org.basaltmc.server.commands.command("hud tab", "", ::tab)
        org.basaltmc.server.commands.command("hud surfaces", "", ::surfaces)
        org.basaltmc.server.commands.command("hud surface-end", "", ::surfaceEnd)
    }
}
