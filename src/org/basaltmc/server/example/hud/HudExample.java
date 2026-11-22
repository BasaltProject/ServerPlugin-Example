package org.basaltmc.server.example.hud;

import static org.basaltmc.server.commands.Commands.command;
import static org.basaltmc.server.commands.Commands.commandReply;
import static org.basaltmc.server.commands.Commands.commandSourceName;
import static org.basaltmc.server.log.Log.logInfo;
import static org.basaltmc.server.players.SharedPlayers.playerByName;
import static org.basaltmc.server.players.SharedPlayers.playerName;
import org.basaltmc.server.hud.Hud;
import org.basaltmc.server.values.Values;

public final class HudExample {
    private HudExample() {}

    private static int gNpc;
    private static int gClaim;

    private static int entityOf(int npc) {
        byte[] raw = new byte[768];
        int length = Hud.npcGet(npc, raw);
        if (length <= 0 || length > raw.length) {
            return 0;
        }
        byte[] document = new byte[length];
        System.arraycopy(raw, 0, document, 0, length);
        Values.Value value = Values.Value.open(document);
        return value == null ? 0 : (int) value.find("id").asLong(0);
    }

    private static void clicked(int ctx) {
        logInfo("npc clicked");
    }

    private static void render(int ctx) {
        byte[] spec1 = new Values.Writer().beginMap().key("title").string("<gold>Example").key("lines").beginList().string("<white>Hello %player%").string("<gray>second line").end().end().finish();
        Hud.surfaceAnswer(ctx, spec1);
    }

    private static void board(int ctx) {
        int player = playerByName(commandSourceName(ctx));
        int mainBoard = Hud.scoreboardMain();
        int own = Hud.scoreboardCreate();
        byte[] spec1 = new Values.Writer().beginMap().key("criterion").string("dummy").key("title").string("<gold>Kills").key("hearts").bool(false).end().finish();
        Hud.objectiveAdd(own, "kills", spec1);
        byte[] spec2 = new Values.Writer().beginMap().key("criterion").string("no_such_criterion").end().finish();
        Hud.objectiveAdd(own, "broken", spec2);
        byte[] buf3 = new byte[256];
        Hud.scoreboardList(own, "objectives", buf3);
        Hud.displaySet(own, "sidebar", "kills");
        byte[] buf4 = new byte[256];
        Hud.displayGet(own, "sidebar", buf4);
        Hud.displaySet(own, "sidebar", "missing");
        Hud.displaySet(own, "nowhere", "kills");
        Hud.scoreSet(own, "Steve", "kills", 7);
        Hud.scoreSet(own, "Alex", "kills", 3);
        int[] out5 = new int[1];
        Hud.scoreGet(own, "Steve", "kills", out5);
        byte[] buf6 = new byte[256];
        Hud.scoreboardList(own, "entries", buf6);
        Hud.scoreReset(own, "Steve", "kills");
        int[] out7 = new int[1];
        Hud.scoreGet(own, "Steve", "kills", out7);
        Hud.teamAdd(own, "red");
        Hud.teamAdd(own, "red");
        byte[] spec8 = new Values.Writer().beginMap().key("display_name").string("<red>Red").key("prefix").string("[R] ").key("suffix").string("!").key("color").string("red").key("friendly_fire").bool(false).key("see_invisible").bool(true).key("name_tags").string("hideForOtherTeams").key("death_messages").string("never").key("collision").string("pushOtherTeams").end().finish();
        Hud.teamSet(own, "red", spec8);
        byte[] spec9 = new Values.Writer().beginMap().key("color").string("blue").end().finish();
        Hud.teamSet(own, "blue", spec9);
        Hud.teamJoin(own, "red", "Steve");
        Hud.teamJoin(own, "gone", "Steve");
        byte[] buf10 = new byte[256];
        Hud.teamOf(own, "Steve", buf10);
        byte[] buf11 = new byte[256];
        Hud.scoreboardList(own, "teams", buf11);
        byte[] buf12 = new byte[256];
        Hud.scoreboardList(own, "bogus", buf12);
        Hud.teamLeave(own, "red", "Steve");
        Hud.teamLeave(own, "red", "Steve");
        byte[] buf13 = new byte[256];
        Hud.teamOf(own, "Steve", buf13);
        Hud.teamRemove(own, "red");
        Hud.teamRemove(own, "red");
        Hud.scoreboardShow(player, own);
        Hud.scoreboardShown(player);
        Hud.scoreboardShow(player, mainBoard);
        Hud.scoreboardShown(player);
        Hud.objectiveRemove(own, "kills");
        Hud.objectiveRemove(own, "kills");
        Hud.scoreboardRelease(own);
        Hud.scoreboardRelease(own);
        Hud.scoreboardRelease(mainBoard);
        commandReply(ctx, "board done");
    }

    private static void bars(int ctx) {
        int player = playerByName(commandSourceName(ctx));
        byte[] spec1 = new Values.Writer().beginMap().key("title").string("<green>Wave 1").key("color").string("green").key("style").string("notched_10").key("progress").number(0.5).key("darken_sky").bool(false).key("boss_music").bool(false).key("fog").bool(false).key("visible").bool(true).key("key").string("example:wave").end().finish();
        int bar = Hud.bossbarCreate(spec1);
        byte[] spec2 = new Values.Writer().beginMap().key("title").string("<green>Wave 1").key("key").string("example:wave").end().finish();
        Hud.bossbarCreate(spec2);
        byte[] buf3 = new byte[512];
        Hud.bossbarKept(buf3);
        byte[] buf4 = new byte[512];
        Hud.bossbarGet(bar, buf4);
        int attached = Hud.bossbarAttach("example:wave");
        Hud.bossbarAttach("example:none");
        Hud.bossbarPlayerAdd(bar, player);
        Hud.bossbarPlayerAdd(bar, player);
        byte[] buf5 = new byte[512];
        Hud.bossbarPlayers(bar, buf5);
        byte[] spec6 = new Values.Writer().beginMap().key("title").string("<red>Wave 2").key("color").string("red").key("progress").number(0.25).end().finish();
        Hud.bossbarUpdate(bar, spec6);
        byte[] spec7 = new Values.Writer().beginMap().key("progress").string("half").end().finish();
        Hud.bossbarUpdate(bar, spec7);
        Hud.bossbarPlayerRemove(bar, player);
        Hud.bossbarPlayerRemove(bar, player);
        Hud.bossbarRemove(bar);
        byte[] spec8 = new Values.Writer().beginMap().key("title").string("<red>Late").end().finish();
        Hud.bossbarUpdate(attached, spec8);
        Hud.bossbarRemove(bar);
        commandReply(ctx, "bars done");
    }

    private static void holograms(int ctx) {
        int player = playerByName(commandSourceName(ctx));
        String name = playerName(player);
        int holo = Hud.hologramCreate("world", "<yellow>Hello", 0.5, 64.0, 0.5);
        Hud.hologramCreate("nowhere", "text", 0.0, 0.0, 0.0);
        byte[] spec1 = new Values.Writer().beginMap().key("text").string("<yellow>Moved").key("x").number(2.5).key("y").number(65.0).key("z").number(-1.5).end().finish();
        Hud.hologramSet(holo, spec1);
        byte[] spec2 = new Values.Writer().beginMap().key("x").number(1.0).end().finish();
        Hud.hologramSet(holo, spec2);
        byte[] spec3 = new Values.Writer().beginMap().key("audience").beginList().string(name).end().end().finish();
        Hud.hologramSet(holo, spec3);
        byte[] buf4 = new byte[512];
        Hud.hologramGet(holo, buf4);
        byte[] spec5 = new Values.Writer().beginMap().key("audience").nul().end().finish();
        Hud.hologramSet(holo, spec5);
        Hud.hologramRemove(holo);
        Hud.hologramRemove(holo);
        commandReply(ctx, "holograms done");
    }

    private static void npcs(int ctx) {
        int player = playerByName(commandSourceName(ctx));
        byte[] spec1 = new Values.Writer().beginMap().key("world").string("world").key("name").string("<aqua>Guide").key("x").number(1.5).key("y").number(64.0).key("z").number(2.5).end().finish();
        int npc = Hud.npcCreate(spec1, HudExample::clicked);
        byte[] spec2 = new Values.Writer().beginMap().key("world").string("nowhere").key("name").string("Nobody").key("x").number(0.0).key("y").number(0.0).key("z").number(0.0).end().finish();
        Hud.npcCreate(spec2, null);
        byte[] spec3 = new Values.Writer().beginMap().key("name").string("<aqua>Guide II").key("x").number(3.5).key("y").number(65.0).key("z").number(4.5).key("yaw").number(90.0).key("pitch").number(-15.0).key("looks_at_players").bool(true).key("health").number(18.0).key("held_slot").integer(0).key("flags").integer(0).key("pose").integer(0).key("attributes").beginMap().key("max_health").number(30.0).end().key("equipment").beginMap().key("0").beginMap().key("item").string("minecraft:diamond_sword").key("count").integer(1).end().end().end().finish();
        Hud.npcSet(npc, spec3);
        byte[] spec4 = new Values.Writer().beginMap().key("attributes").beginMap().key("no_such_attribute").number(1.0).end().end().finish();
        Hud.npcSet(npc, spec4);
        byte[] buf5 = new byte[768];
        Hud.npcGet(npc, buf5);
        byte[] buf6 = new byte[768];
        Hud.npcItem(npc, 0, buf6);
        byte[] buf7 = new byte[768];
        Hud.npcItem(npc, 1, buf7);
        double[] out8 = new double[1];
        Hud.npcAttribute(npc, "max_health", out8);
        double[] out9 = new double[1];
        Hud.npcAttribute(npc, "no_such_attribute", out9);
        Hud.npcAnimate(npc, 0);
        int entity = entityOf(npc);
        Hud.tabEntry(player, entity, 0);
        Hud.tabEntry(player, 424242, 0);
        gNpc = npc;
        commandReply(ctx, "npcs done");
    }

    private static void npcEnd(int ctx) {
        Hud.npcRemove(gNpc);
        Hud.npcAnimate(gNpc, 0);
        commandReply(ctx, "npc end done");
    }

    private static void tab(int ctx) {
        int player = playerByName(commandSourceName(ctx));
        byte[] spec1 = new Values.Writer().beginMap().key("tab_header").string("<green>Top").key("tab_footer").string("<gray>Bottom").key("list_name").string("<gold>Steve").key("listed").bool(true).key("list_order").integer(3).key("score").integer(5).key("waypoint_style").string("default").key("waypoint_color").integer(16711680).end().finish();
        Hud.playerSet(player, spec1);
        byte[] buf2 = new byte[768];
        Hud.playerGet(player, buf2);
        byte[] spec3 = new Values.Writer().beginMap().key("not_a_property").integer(1).end().finish();
        Hud.playerSet(player, spec3);
        byte[] spec4 = new Values.Writer().beginMap().key("listed").string("yes").end().finish();
        Hud.playerSet(player, spec4);
        byte[] spec5 = new Values.Writer().beginMap().key("tab_header").nul().key("tab_footer").nul().key("list_name").nul().end().finish();
        Hud.playerSet(player, spec5);
        commandReply(ctx, "tab done");
    }

    private static void surfaces(int ctx) {
        byte[] spec1 = new Values.Writer().beginMap().key("title").string("<gold>Static").key("lines").beginList().string("<white>one").end().end().finish();
        int claim = Hud.surfaceClaim("sidebar", 5, spec1, HudExample::render);
        byte[] spec2 = new Values.Writer().beginMap().key("title").string("<gold>Static").key("lines").beginList().string("<white>one").end().end().finish();
        Hud.surfaceClaim("nowhere", 1, spec2, null);
        byte[] spec3 = new Values.Writer().beginMap().key("title").string("<gold>Changed").key("lines").beginList().string("<white>two").end().end().finish();
        Hud.surfaceUpdate(claim, spec3);
        byte[] spec4 = new Values.Writer().beginMap().key("title").string("<gold>Changed").key("lines").beginList().string("<white>two").end().end().finish();
        Hud.surfaceAnswer(ctx, spec4);
        gClaim = claim;
        commandReply(ctx, "surfaces done");
    }

    private static void surfaceEnd(int ctx) {
        Hud.surfaceRelease(gClaim);
        Hud.surfaceRelease(gClaim);
        byte[] spec1 = new Values.Writer().beginMap().key("title").string("<gold>Late").end().finish();
        Hud.surfaceUpdate(gClaim, spec1);
        commandReply(ctx, "surface end done");
    }

    public static void register() {
        command("hud board", "", HudExample::board);
        command("hud bars", "", HudExample::bars);
        command("hud holograms", "", HudExample::holograms);
        command("hud npcs", "", HudExample::npcs);
        command("hud npc-end", "", HudExample::npcEnd);
        command("hud tab", "", HudExample::tab);
        command("hud surfaces", "", HudExample::surfaces);
        command("hud surface-end", "", HudExample::surfaceEnd);
    }
}
