using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkCommands = global::Basalt.Server.Commands.Commands;
using SdkHud = global::Basalt.Server.Hud.Hud;
using SdkLogger = global::Basalt.Server.Log.Logger;
using SdkPlayers = global::Basalt.Server.Players.Players;

namespace Basalt.Server.Example.Hud;

public static class HudExample
{
    static int gNpc;
    static int gClaim;

    static int EntityOf(int npc)
    {
        byte[] raw = new byte[768];
        int length = SdkHud.NpcGet(npc, raw);
        if (length <= 0 || length > raw.Length)
        {
            return 0;
        }
        byte[] document = new byte[length];
        System.Array.Copy(raw, document, length);
        if (!Value.TryOpen(document, out Value value) || !value.TryFind("id", out Value id) || !id.TryInt(out long entity))
        {
            return 0;
        }
        return (int)entity;
    }

    static void Clicked(int ctx)
    {
        SdkLogger.LogInfo("npc clicked");
    }

    static void Render(int ctx)
    {
        byte[] spec1 = new ValueWriter().BeginMap().Key("title").String("<gold>Example").Key("lines").BeginList().String("<white>Hello %player%").String("<gray>second line").End().End().Finish();
        SdkHud.SurfaceAnswer(ctx, spec1);
    }

    static void Board(int ctx)
    {
        int player = SdkPlayers.PlayerByName(SdkCommands.CommandSourceName(ctx) ?? "");
        int mainBoard = SdkHud.ScoreboardMain();
        int own = SdkHud.ScoreboardCreate();
        byte[] spec1 = new ValueWriter().BeginMap().Key("criterion").String("dummy").Key("title").String("<gold>Kills").Key("hearts").Bool(false).End().Finish();
        SdkHud.ObjectiveAdd(own, "kills", spec1);
        byte[] spec2 = new ValueWriter().BeginMap().Key("criterion").String("no_such_criterion").End().Finish();
        SdkHud.ObjectiveAdd(own, "broken", spec2);
        byte[] buf3 = new byte[256];
        SdkHud.ScoreboardList(own, "objectives", buf3);
        SdkHud.DisplaySet(own, "sidebar", "kills");
        byte[] buf4 = new byte[256];
        SdkHud.DisplayGet(own, "sidebar", buf4);
        SdkHud.DisplaySet(own, "sidebar", "missing");
        SdkHud.DisplaySet(own, "nowhere", "kills");
        SdkHud.ScoreSet(own, "Steve", "kills", 7);
        SdkHud.ScoreSet(own, "Alex", "kills", 3);
        SdkHud.ScoreGet(own, "Steve", "kills", out int out5);
        byte[] buf6 = new byte[256];
        SdkHud.ScoreboardList(own, "entries", buf6);
        SdkHud.ScoreReset(own, "Steve", "kills");
        SdkHud.ScoreGet(own, "Steve", "kills", out int out7);
        SdkHud.TeamAdd(own, "red");
        SdkHud.TeamAdd(own, "red");
        byte[] spec8 = new ValueWriter().BeginMap().Key("display_name").String("<red>Red").Key("prefix").String("[R] ").Key("suffix").String("!").Key("color").String("red").Key("friendly_fire").Bool(false).Key("see_invisible").Bool(true).Key("name_tags").String("hideForOtherTeams").Key("death_messages").String("never").Key("collision").String("pushOtherTeams").End().Finish();
        SdkHud.TeamSet(own, "red", spec8);
        byte[] spec9 = new ValueWriter().BeginMap().Key("color").String("blue").End().Finish();
        SdkHud.TeamSet(own, "blue", spec9);
        SdkHud.TeamJoin(own, "red", "Steve");
        SdkHud.TeamJoin(own, "gone", "Steve");
        byte[] buf10 = new byte[256];
        SdkHud.TeamOf(own, "Steve", buf10);
        byte[] buf11 = new byte[256];
        SdkHud.ScoreboardList(own, "teams", buf11);
        byte[] buf12 = new byte[256];
        SdkHud.ScoreboardList(own, "bogus", buf12);
        SdkHud.TeamLeave(own, "red", "Steve");
        SdkHud.TeamLeave(own, "red", "Steve");
        byte[] buf13 = new byte[256];
        SdkHud.TeamOf(own, "Steve", buf13);
        SdkHud.TeamRemove(own, "red");
        SdkHud.TeamRemove(own, "red");
        SdkHud.ScoreboardShow(player, own);
        SdkHud.ScoreboardShown(player);
        SdkHud.ScoreboardShow(player, mainBoard);
        SdkHud.ScoreboardShown(player);
        SdkHud.ObjectiveRemove(own, "kills");
        SdkHud.ObjectiveRemove(own, "kills");
        SdkHud.ScoreboardRelease(own);
        SdkHud.ScoreboardRelease(own);
        SdkHud.ScoreboardRelease(mainBoard);
        SdkCommands.CommandReply(ctx, "board done");
    }

    static void Bars(int ctx)
    {
        int player = SdkPlayers.PlayerByName(SdkCommands.CommandSourceName(ctx) ?? "");
        byte[] spec1 = new ValueWriter().BeginMap().Key("title").String("<green>Wave 1").Key("color").String("green").Key("style").String("notched_10").Key("progress").Float(0.5).Key("darken_sky").Bool(false).Key("boss_music").Bool(false).Key("fog").Bool(false).Key("visible").Bool(true).Key("key").String("example:wave").End().Finish();
        int bar = SdkHud.BossbarCreate(spec1);
        byte[] spec2 = new ValueWriter().BeginMap().Key("title").String("<green>Wave 1").Key("key").String("example:wave").End().Finish();
        SdkHud.BossbarCreate(spec2);
        byte[] buf3 = new byte[512];
        SdkHud.BossbarKept(buf3);
        byte[] buf4 = new byte[512];
        SdkHud.BossbarGet(bar, buf4);
        int attached = SdkHud.BossbarAttach("example:wave");
        SdkHud.BossbarAttach("example:none");
        SdkHud.BossbarPlayerAdd(bar, player);
        SdkHud.BossbarPlayerAdd(bar, player);
        byte[] buf5 = new byte[512];
        SdkHud.BossbarPlayers(bar, buf5);
        byte[] spec6 = new ValueWriter().BeginMap().Key("title").String("<red>Wave 2").Key("color").String("red").Key("progress").Float(0.25).End().Finish();
        SdkHud.BossbarUpdate(bar, spec6);
        byte[] spec7 = new ValueWriter().BeginMap().Key("progress").String("half").End().Finish();
        SdkHud.BossbarUpdate(bar, spec7);
        SdkHud.BossbarPlayerRemove(bar, player);
        SdkHud.BossbarPlayerRemove(bar, player);
        SdkHud.BossbarRemove(bar);
        byte[] spec8 = new ValueWriter().BeginMap().Key("title").String("<red>Late").End().Finish();
        SdkHud.BossbarUpdate(attached, spec8);
        SdkHud.BossbarRemove(bar);
        SdkCommands.CommandReply(ctx, "bars done");
    }

    static void Holograms(int ctx)
    {
        int player = SdkPlayers.PlayerByName(SdkCommands.CommandSourceName(ctx) ?? "");
        string name = SdkPlayers.PlayerName(player) ?? "";
        int holo = SdkHud.HologramCreate("world", "<yellow>Hello", 0.5, 64.0, 0.5);
        SdkHud.HologramCreate("nowhere", "text", 0.0, 0.0, 0.0);
        byte[] spec1 = new ValueWriter().BeginMap().Key("text").String("<yellow>Moved").Key("x").Float(2.5).Key("y").Float(65.0).Key("z").Float(-1.5).End().Finish();
        SdkHud.HologramSet(holo, spec1);
        byte[] spec2 = new ValueWriter().BeginMap().Key("x").Float(1.0).End().Finish();
        SdkHud.HologramSet(holo, spec2);
        byte[] spec3 = new ValueWriter().BeginMap().Key("audience").BeginList().String(name).End().End().Finish();
        SdkHud.HologramSet(holo, spec3);
        byte[] buf4 = new byte[512];
        SdkHud.HologramGet(holo, buf4);
        byte[] spec5 = new ValueWriter().BeginMap().Key("audience").Null().End().Finish();
        SdkHud.HologramSet(holo, spec5);
        SdkHud.HologramRemove(holo);
        SdkHud.HologramRemove(holo);
        SdkCommands.CommandReply(ctx, "holograms done");
    }

    static void Npcs(int ctx)
    {
        int player = SdkPlayers.PlayerByName(SdkCommands.CommandSourceName(ctx) ?? "");
        byte[] spec1 = new ValueWriter().BeginMap().Key("world").String("world").Key("name").String("<aqua>Guide").Key("x").Float(1.5).Key("y").Float(64.0).Key("z").Float(2.5).End().Finish();
        int npc = SdkHud.NpcCreate(spec1, Clicked);
        byte[] spec2 = new ValueWriter().BeginMap().Key("world").String("nowhere").Key("name").String("Nobody").Key("x").Float(0.0).Key("y").Float(0.0).Key("z").Float(0.0).End().Finish();
        SdkHud.NpcCreate(spec2, null);
        byte[] spec3 = new ValueWriter().BeginMap().Key("name").String("<aqua>Guide II").Key("x").Float(3.5).Key("y").Float(65.0).Key("z").Float(4.5).Key("yaw").Float(90.0).Key("pitch").Float(-15.0).Key("looks_at_players").Bool(true).Key("health").Float(18.0).Key("held_slot").Int(0).Key("flags").Int(0).Key("pose").Int(0).Key("attributes").BeginMap().Key("max_health").Float(30.0).End().Key("equipment").BeginMap().Key("0").BeginMap().Key("item").String("minecraft:diamond_sword").Key("count").Int(1).End().End().End().Finish();
        SdkHud.NpcSet(npc, spec3);
        byte[] spec4 = new ValueWriter().BeginMap().Key("attributes").BeginMap().Key("no_such_attribute").Float(1.0).End().End().Finish();
        SdkHud.NpcSet(npc, spec4);
        byte[] buf5 = new byte[768];
        SdkHud.NpcGet(npc, buf5);
        byte[] buf6 = new byte[768];
        SdkHud.NpcItem(npc, 0, buf6);
        byte[] buf7 = new byte[768];
        SdkHud.NpcItem(npc, 1, buf7);
        SdkHud.NpcAttribute(npc, "max_health", out double out8);
        SdkHud.NpcAttribute(npc, "no_such_attribute", out double out9);
        SdkHud.NpcAnimate(npc, 0);
        int entity = EntityOf(npc);
        SdkHud.TabEntry(player, entity, 0);
        SdkHud.TabEntry(player, 424242, 0);
        gNpc = npc;
        SdkCommands.CommandReply(ctx, "npcs done");
    }

    static void NpcEnd(int ctx)
    {
        SdkHud.NpcRemove(gNpc);
        SdkHud.NpcAnimate(gNpc, 0);
        SdkCommands.CommandReply(ctx, "npc end done");
    }

    static void Tab(int ctx)
    {
        int player = SdkPlayers.PlayerByName(SdkCommands.CommandSourceName(ctx) ?? "");
        byte[] spec1 = new ValueWriter().BeginMap().Key("tab_header").String("<green>Top").Key("tab_footer").String("<gray>Bottom").Key("list_name").String("<gold>Steve").Key("listed").Bool(true).Key("list_order").Int(3).Key("score").Int(5).Key("waypoint_style").String("default").Key("waypoint_color").Int(16711680).End().Finish();
        SdkHud.PlayerSet(player, spec1);
        byte[] buf2 = new byte[768];
        SdkHud.PlayerGet(player, buf2);
        byte[] spec3 = new ValueWriter().BeginMap().Key("not_a_property").Int(1).End().Finish();
        SdkHud.PlayerSet(player, spec3);
        byte[] spec4 = new ValueWriter().BeginMap().Key("listed").String("yes").End().Finish();
        SdkHud.PlayerSet(player, spec4);
        byte[] spec5 = new ValueWriter().BeginMap().Key("tab_header").Null().Key("tab_footer").Null().Key("list_name").Null().End().Finish();
        SdkHud.PlayerSet(player, spec5);
        SdkCommands.CommandReply(ctx, "tab done");
    }

    static void Surfaces(int ctx)
    {
        byte[] spec1 = new ValueWriter().BeginMap().Key("title").String("<gold>Static").Key("lines").BeginList().String("<white>one").End().End().Finish();
        int claim = SdkHud.SurfaceClaim("sidebar", 5, spec1, Render);
        byte[] spec2 = new ValueWriter().BeginMap().Key("title").String("<gold>Static").Key("lines").BeginList().String("<white>one").End().End().Finish();
        SdkHud.SurfaceClaim("nowhere", 1, spec2, null);
        byte[] spec3 = new ValueWriter().BeginMap().Key("title").String("<gold>Changed").Key("lines").BeginList().String("<white>two").End().End().Finish();
        SdkHud.SurfaceUpdate(claim, spec3);
        byte[] spec4 = new ValueWriter().BeginMap().Key("title").String("<gold>Changed").Key("lines").BeginList().String("<white>two").End().End().Finish();
        SdkHud.SurfaceAnswer(ctx, spec4);
        gClaim = claim;
        SdkCommands.CommandReply(ctx, "surfaces done");
    }

    static void SurfaceEnd(int ctx)
    {
        SdkHud.SurfaceRelease(gClaim);
        SdkHud.SurfaceRelease(gClaim);
        byte[] spec1 = new ValueWriter().BeginMap().Key("title").String("<gold>Late").End().Finish();
        SdkHud.SurfaceUpdate(gClaim, spec1);
        SdkCommands.CommandReply(ctx, "surface end done");
    }

    public static void RegisterAll()
    {
        SdkCommands.Command("hud board", "", Board);
        SdkCommands.Command("hud bars", "", Bars);
        SdkCommands.Command("hud holograms", "", Holograms);
        SdkCommands.Command("hud npcs", "", Npcs);
        SdkCommands.Command("hud npc-end", "", NpcEnd);
        SdkCommands.Command("hud tab", "", Tab);
        SdkCommands.Command("hud surfaces", "", Surfaces);
        SdkCommands.Command("hud surface-end", "", SurfaceEnd);
    }
}
