package org.basaltmc.server.example.hud;

import org.basaltmc.server.commands.Registry;
import org.basaltmc.server.hud.Displays;
import org.basaltmc.server.values.Documents;

import static org.basaltmc.server.commands.Sender.name;
import static org.basaltmc.server.commands.Sender.reply;
import static org.basaltmc.server.log.Logger.logInfo;
import static org.basaltmc.server.players.Directory.playerByName;
import static org.basaltmc.server.players.Directory.playerName;

public final class DisplayExamples {
    private DisplayExamples() {}

    private static int gNpc;
    private static int gClaim;

    private static int entityOf(int npc) {
        byte[] raw = new byte[768];
        int length = Displays.npcGet(npc, raw);
        if (length <= 0 || length > raw.length) {
            return 0;
        }
        byte[] document = new byte[length];
        System.arraycopy(raw, 0, document, 0, length);
        Documents.Value value = Documents.Value.open(document);
        return value == null ? 0 : (int) value.find("id").asLong(0);
    }

    private static void clicked(int ctx) {
        logInfo("npc clicked");
    }

    private static void render(int ctx) {
        byte[] spec1 = new Documents.Writer().beginMap().key("title").string("<gold>Example").key("lines").beginList().string("<white>Hello %player%").string("<gray>second line").end().end().finish();
        Displays.surfaceAnswer(ctx, spec1);
    }

    private static void board(int ctx) {
        int player = playerByName(name(ctx));
        int mainBoard = Displays.scoreboardMain();
        int own = Displays.scoreboardCreate();
        byte[] spec1 = new Documents.Writer().beginMap().key("criterion").string("dummy").key("title").string("<gold>Kills").key("hearts").bool(false).end().finish();
        Displays.objectiveAdd(own, "kills", spec1);
        byte[] spec2 = new Documents.Writer().beginMap().key("criterion").string("no_such_criterion").end().finish();
        Displays.objectiveAdd(own, "broken", spec2);
        byte[] buf3 = new byte[256];
        Displays.scoreboardList(own, "objectives", buf3);
        Displays.displaySet(own, "sidebar", "kills");
        byte[] buf4 = new byte[256];
        Displays.displayGet(own, "sidebar", buf4);
        Displays.displaySet(own, "sidebar", "missing");
        Displays.displaySet(own, "nowhere", "kills");
        Displays.scoreSet(own, "Steve", "kills", 7);
        Displays.scoreSet(own, "Alex", "kills", 3);
        int[] out5 = new int[1];
        Displays.scoreGet(own, "Steve", "kills", out5);
        byte[] buf6 = new byte[256];
        Displays.scoreboardList(own, "entries", buf6);
        Displays.scoreReset(own, "Steve", "kills");
        int[] out7 = new int[1];
        Displays.scoreGet(own, "Steve", "kills", out7);
        Displays.teamAdd(own, "red");
        Displays.teamAdd(own, "red");
        byte[] spec8 = new Documents.Writer().beginMap().key("display_name").string("<red>Red").key("prefix").string("[R] ").key("suffix").string("!").key("color").string("red").key("friendly_fire").bool(false).key("see_invisible").bool(true).key("name_tags").string("hideForOtherTeams").key("death_messages").string("never").key("collision").string("pushOtherTeams").end().finish();
        Displays.teamSet(own, "red", spec8);
        byte[] spec9 = new Documents.Writer().beginMap().key("color").string("blue").end().finish();
        Displays.teamSet(own, "blue", spec9);
        Displays.teamJoin(own, "red", "Steve");
        Displays.teamJoin(own, "gone", "Steve");
        byte[] buf10 = new byte[256];
        Displays.teamOf(own, "Steve", buf10);
        byte[] buf11 = new byte[256];
        Displays.scoreboardList(own, "teams", buf11);
        byte[] buf12 = new byte[256];
        Displays.scoreboardList(own, "bogus", buf12);
        Displays.teamLeave(own, "red", "Steve");
        Displays.teamLeave(own, "red", "Steve");
        byte[] buf13 = new byte[256];
        Displays.teamOf(own, "Steve", buf13);
        Displays.teamRemove(own, "red");
        Displays.teamRemove(own, "red");
        Displays.scoreboardShow(player, own);
        Displays.scoreboardShown(player);
        Displays.scoreboardShow(player, mainBoard);
        Displays.scoreboardShown(player);
        Displays.objectiveRemove(own, "kills");
        Displays.objectiveRemove(own, "kills");
        Displays.scoreboardRelease(own);
        Displays.scoreboardRelease(own);
        Displays.scoreboardRelease(mainBoard);
        reply(ctx, "board done");
    }

    private static void bars(int ctx) {
        int player = playerByName(name(ctx));
        byte[] spec1 = new Documents.Writer().beginMap().key("title").string("<green>Wave 1").key("color").string("green").key("style").string("notched_10").key("progress").number(0.5).key("darken_sky").bool(false).key("boss_music").bool(false).key("fog").bool(false).key("visible").bool(true).key("key").string("example:wave").end().finish();
        int bar = Displays.bossbarCreate(spec1);
        byte[] spec2 = new Documents.Writer().beginMap().key("title").string("<green>Wave 1").key("key").string("example:wave").end().finish();
        Displays.bossbarCreate(spec2);
        byte[] buf3 = new byte[512];
        Displays.bossbarKept(buf3);
        byte[] buf4 = new byte[512];
        Displays.bossbarGet(bar, buf4);
        int attached = Displays.bossbarAttach("example:wave");
        Displays.bossbarAttach("example:none");
        Displays.bossbarPlayerAdd(bar, player);
        Displays.bossbarPlayerAdd(bar, player);
        byte[] buf5 = new byte[512];
        Displays.bossbarPlayers(bar, buf5);
        byte[] spec6 = new Documents.Writer().beginMap().key("title").string("<red>Wave 2").key("color").string("red").key("progress").number(0.25).end().finish();
        Displays.bossbarUpdate(bar, spec6);
        byte[] spec7 = new Documents.Writer().beginMap().key("progress").string("half").end().finish();
        Displays.bossbarUpdate(bar, spec7);
        Displays.bossbarPlayerRemove(bar, player);
        Displays.bossbarPlayerRemove(bar, player);
        Displays.bossbarRemove(bar);
        byte[] spec8 = new Documents.Writer().beginMap().key("title").string("<red>Late").end().finish();
        Displays.bossbarUpdate(attached, spec8);
        Displays.bossbarRemove(bar);
        reply(ctx, "bars done");
    }

    private static void holograms(int ctx) {
        int player = playerByName(name(ctx));
        String name = playerName(player);
        int holo = Displays.hologramCreate("world", "<yellow>Hello", 0.5, 64.0, 0.5);
        Displays.hologramCreate("nowhere", "text", 0.0, 0.0, 0.0);
        byte[] spec1 = new Documents.Writer().beginMap().key("text").string("<yellow>Moved").key("x").number(2.5).key("y").number(65.0).key("z").number(-1.5).end().finish();
        Displays.hologramSet(holo, spec1);
        byte[] spec2 = new Documents.Writer().beginMap().key("x").number(1.0).end().finish();
        Displays.hologramSet(holo, spec2);
        byte[] spec3 = new Documents.Writer().beginMap().key("audience").beginList().string(name).end().end().finish();
        Displays.hologramSet(holo, spec3);
        byte[] buf4 = new byte[512];
        Displays.hologramGet(holo, buf4);
        byte[] spec5 = new Documents.Writer().beginMap().key("audience").nul().end().finish();
        Displays.hologramSet(holo, spec5);
        Displays.hologramRemove(holo);
        Displays.hologramRemove(holo);
        reply(ctx, "holograms done");
    }

    private static void npcs(int ctx) {
        int player = playerByName(name(ctx));
        byte[] spec1 = new Documents.Writer().beginMap().key("world").string("world").key("name").string("<aqua>Guide").key("x").number(1.5).key("y").number(64.0).key("z").number(2.5).end().finish();
        int npc = Displays.npcCreate(spec1, DisplayExamples::clicked);
        byte[] spec2 = new Documents.Writer().beginMap().key("world").string("nowhere").key("name").string("Nobody").key("x").number(0.0).key("y").number(0.0).key("z").number(0.0).end().finish();
        Displays.npcCreate(spec2, null);
        byte[] spec3 = new Documents.Writer().beginMap().key("name").string("<aqua>Guide II").key("x").number(3.5).key("y").number(65.0).key("z").number(4.5).key("yaw").number(90.0).key("pitch").number(-15.0).key("looks_at_players").bool(true).key("health").number(18.0).key("held_slot").integer(0).key("flags").integer(0).key("pose").integer(0).key("attributes").beginMap().key("max_health").number(30.0).end().key("equipment").beginMap().key("0").beginMap().key("item").string("minecraft:diamond_sword").key("count").integer(1).end().end().end().finish();
        Displays.npcSet(npc, spec3);
        byte[] spec4 = new Documents.Writer().beginMap().key("attributes").beginMap().key("no_such_attribute").number(1.0).end().end().finish();
        Displays.npcSet(npc, spec4);
        byte[] buf5 = new byte[768];
        Displays.npcGet(npc, buf5);
        byte[] buf6 = new byte[768];
        Displays.npcItem(npc, 0, buf6);
        byte[] buf7 = new byte[768];
        Displays.npcItem(npc, 1, buf7);
        double[] out8 = new double[1];
        Displays.npcAttribute(npc, "max_health", out8);
        double[] out9 = new double[1];
        Displays.npcAttribute(npc, "no_such_attribute", out9);
        Displays.npcAnimate(npc, 0);
        int entity = entityOf(npc);
        Displays.tabEntry(player, entity, 0);
        Displays.tabEntry(player, 424242, 0);
        gNpc = npc;
        reply(ctx, "npcs done");
    }

    private static void npcEnd(int ctx) {
        Displays.npcRemove(gNpc);
        Displays.npcAnimate(gNpc, 0);
        reply(ctx, "npc end done");
    }

    private static void tab(int ctx) {
        int player = playerByName(name(ctx));
        byte[] spec1 = new Documents.Writer().beginMap().key("tab_header").string("<green>Top").key("tab_footer").string("<gray>Bottom").key("list_name").string("<gold>Steve").key("listed").bool(true).key("list_order").integer(3).key("score").integer(5).key("waypoint_style").string("default").key("waypoint_color").integer(16711680).end().finish();
        Displays.playerSet(player, spec1);
        byte[] buf2 = new byte[768];
        Displays.playerGet(player, buf2);
        byte[] spec3 = new Documents.Writer().beginMap().key("not_a_property").integer(1).end().finish();
        Displays.playerSet(player, spec3);
        byte[] spec4 = new Documents.Writer().beginMap().key("listed").string("yes").end().finish();
        Displays.playerSet(player, spec4);
        byte[] spec5 = new Documents.Writer().beginMap().key("tab_header").nul().key("tab_footer").nul().key("list_name").nul().end().finish();
        Displays.playerSet(player, spec5);
        reply(ctx, "tab done");
    }

    private static void surfaces(int ctx) {
        byte[] spec1 = new Documents.Writer().beginMap().key("title").string("<gold>Static").key("lines").beginList().string("<white>one").end().end().finish();
        int claim = Displays.surfaceClaim("sidebar", 5, spec1, DisplayExamples::render);
        byte[] spec2 = new Documents.Writer().beginMap().key("title").string("<gold>Static").key("lines").beginList().string("<white>one").end().end().finish();
        Displays.surfaceClaim("nowhere", 1, spec2, null);
        byte[] spec3 = new Documents.Writer().beginMap().key("title").string("<gold>Changed").key("lines").beginList().string("<white>two").end().end().finish();
        Displays.surfaceUpdate(claim, spec3);
        byte[] spec4 = new Documents.Writer().beginMap().key("title").string("<gold>Changed").key("lines").beginList().string("<white>two").end().end().finish();
        Displays.surfaceAnswer(ctx, spec4);
        gClaim = claim;
        reply(ctx, "surfaces done");
    }

    private static void surfaceEnd(int ctx) {
        Displays.surfaceRelease(gClaim);
        Displays.surfaceRelease(gClaim);
        byte[] spec1 = new Documents.Writer().beginMap().key("title").string("<gold>Late").end().finish();
        Displays.surfaceUpdate(gClaim, spec1);
        reply(ctx, "surface end done");
    }

    public static void register() {
        Registry.register("hud board", "", DisplayExamples::board);
        Registry.register("hud bars", "", DisplayExamples::bars);
        Registry.register("hud holograms", "", DisplayExamples::holograms);
        Registry.register("hud npcs", "", DisplayExamples::npcs);
        Registry.register("hud npc-end", "", DisplayExamples::npcEnd);
        Registry.register("hud tab", "", DisplayExamples::tab);
        Registry.register("hud surfaces", "", DisplayExamples::surfaces);
        Registry.register("hud surface-end", "", DisplayExamples::surfaceEnd);
    }
}
