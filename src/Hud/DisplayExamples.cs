using Basalt.Server.Commands;
using Basalt.Server.Core;
using Basalt.Server.Hud;
using Basalt.Server.Items;
using Basalt.Server.Log;
using Basalt.Server.Players;
using Basalt.Server.Values;
using System;

namespace Basalt.Server.Example.Hud;

public static class DisplayExamples
{
    static int gNpc;
    static int gClaim;

    static int EntityOf(int npc)
    {
        byte[] raw = new byte[768];
        int length = Displays.NpcGet(npc, raw);
        if (length <= 0 || length > raw.Length)
        {
            return 0;
        }
        byte[] document = new byte[length];
        Array.Copy(raw, document, length);
        if (!Value.TryOpen(document, out Value value) || !value.TryFind("id", out Value id) || !id.TryInt(out long entity))
        {
            return 0;
        }
        return (int)entity;
    }

    static void Clicked(int ctx)
    {
        Logger.LogInfo("npc clicked");
    }

    static void Render(int ctx)
    {
        byte[] spec1 = new ValueWriter().BeginMap().Key("title").String("<gold>Example").Key("lines").BeginList().String("<white>Hello %player%").String("<gray>second line").End().End().Finish();
        Displays.SurfaceAnswer(ctx, spec1);
    }

    static void Board(int ctx)
    {
        int player = Directory.PlayerByName(Sender.Name(ctx) ?? "");
        int mainBoard = Displays.ScoreboardMain();
        int own = Displays.ScoreboardCreate();
        byte[] spec1 = new ValueWriter().BeginMap().Key("criterion").String("dummy").Key("title").String("<gold>Kills").Key("hearts").Bool(false).End().Finish();
        Displays.ObjectiveAdd(own, "kills", spec1);
        byte[] spec2 = new ValueWriter().BeginMap().Key("criterion").String("no_such_criterion").End().Finish();
        Displays.ObjectiveAdd(own, "broken", spec2);
        byte[] buf3 = new byte[256];
        Displays.ScoreboardList(own, "objectives", buf3);
        Displays.DisplaySet(own, "sidebar", "kills");
        byte[] buf4 = new byte[256];
        Displays.DisplayGet(own, "sidebar", buf4);
        Displays.DisplaySet(own, "sidebar", "missing");
        Displays.DisplaySet(own, "nowhere", "kills");
        Displays.ScoreSet(own, "Steve", "kills", 7);
        Displays.ScoreSet(own, "Alex", "kills", 3);
        Displays.ScoreGet(own, "Steve", "kills", out int out5);
        byte[] buf6 = new byte[256];
        Displays.ScoreboardList(own, "entries", buf6);
        Displays.ScoreReset(own, "Steve", "kills");
        Displays.ScoreGet(own, "Steve", "kills", out int out7);
        Displays.TeamAdd(own, "red");
        Displays.TeamAdd(own, "red");
        byte[] spec8 = new ValueWriter().BeginMap().Key("display_name").String("<red>Red").Key("prefix").String("[R] ").Key("suffix").String("!").Key("color").String("red").Key("friendly_fire").Bool(false).Key("see_invisible").Bool(true).Key("name_tags").String("hideForOtherTeams").Key("death_messages").String("never").Key("collision").String("pushOtherTeams").End().Finish();
        Displays.TeamSet(own, "red", spec8);
        byte[] spec9 = new ValueWriter().BeginMap().Key("color").String("blue").End().Finish();
        Displays.TeamSet(own, "blue", spec9);
        Displays.TeamJoin(own, "red", "Steve");
        Displays.TeamJoin(own, "gone", "Steve");
        byte[] buf10 = new byte[256];
        Displays.TeamOf(own, "Steve", buf10);
        byte[] buf11 = new byte[256];
        Displays.ScoreboardList(own, "teams", buf11);
        byte[] buf12 = new byte[256];
        Displays.ScoreboardList(own, "bogus", buf12);
        Displays.TeamLeave(own, "red", "Steve");
        Displays.TeamLeave(own, "red", "Steve");
        byte[] buf13 = new byte[256];
        Displays.TeamOf(own, "Steve", buf13);
        Displays.TeamRemove(own, "red");
        Displays.TeamRemove(own, "red");
        Displays.ScoreboardShow(player, own);
        Displays.ScoreboardShown(player);
        Displays.ScoreboardShow(player, mainBoard);
        Displays.ScoreboardShown(player);
        Displays.ObjectiveRemove(own, "kills");
        Displays.ObjectiveRemove(own, "kills");
        Displays.ScoreboardRelease(own);
        Displays.ScoreboardRelease(own);
        Displays.ScoreboardRelease(mainBoard);
        Sender.Reply(ctx, "board done");
    }

    static void Bars(int ctx)
    {
        int player = Directory.PlayerByName(Sender.Name(ctx) ?? "");
        byte[] spec1 = new ValueWriter().BeginMap().Key("title").String("<green>Wave 1").Key("color").String("green").Key("style").String("notched_10").Key("progress").Float(0.5).Key("darken_sky").Bool(false).Key("boss_music").Bool(false).Key("fog").Bool(false).Key("visible").Bool(true).Key("key").String("example:wave").End().Finish();
        int bar = Displays.BossbarCreate(spec1);
        byte[] spec2 = new ValueWriter().BeginMap().Key("title").String("<green>Wave 1").Key("key").String("example:wave").End().Finish();
        Displays.BossbarCreate(spec2);
        byte[] buf3 = new byte[512];
        Displays.BossbarKept(buf3);
        byte[] buf4 = new byte[512];
        Displays.BossbarGet(bar, buf4);
        int attached = Displays.BossbarAttach("example:wave");
        Displays.BossbarAttach("example:none");
        Displays.BossbarPlayerAdd(bar, player);
        Displays.BossbarPlayerAdd(bar, player);
        byte[] buf5 = new byte[512];
        Displays.BossbarPlayers(bar, buf5);
        byte[] spec6 = new ValueWriter().BeginMap().Key("title").String("<red>Wave 2").Key("color").String("red").Key("progress").Float(0.25).End().Finish();
        Displays.BossbarUpdate(bar, spec6);
        byte[] spec7 = new ValueWriter().BeginMap().Key("progress").String("half").End().Finish();
        Displays.BossbarUpdate(bar, spec7);
        Displays.BossbarPlayerRemove(bar, player);
        Displays.BossbarPlayerRemove(bar, player);
        Displays.BossbarRemove(bar);
        byte[] spec8 = new ValueWriter().BeginMap().Key("title").String("<red>Late").End().Finish();
        Displays.BossbarUpdate(attached, spec8);
        Displays.BossbarRemove(bar);
        Sender.Reply(ctx, "bars done");
    }

    static void Holograms(int ctx)
    {
        int player = Directory.PlayerByName(Sender.Name(ctx) ?? "");
        string name = Directory.PlayerName(player) ?? "";
        int holo = Displays.HologramCreate("world", "<yellow>Hello", 0.5, 64.0, 0.5);
        Displays.HologramCreate("nowhere", "text", 0.0, 0.0, 0.0);
        byte[] spec1 = new ValueWriter().BeginMap().Key("text").String("<yellow>Moved").Key("x").Float(2.5).Key("y").Float(65.0).Key("z").Float(-1.5).End().Finish();
        Displays.HologramSet(holo, spec1);
        byte[] spec2 = new ValueWriter().BeginMap().Key("x").Float(1.0).End().Finish();
        Displays.HologramSet(holo, spec2);
        byte[] spec3 = new ValueWriter().BeginMap().Key("audience").BeginList().String(name).End().End().Finish();
        Displays.HologramSet(holo, spec3);
        byte[] buf4 = new byte[512];
        Displays.HologramGet(holo, buf4);
        byte[] spec5 = new ValueWriter().BeginMap().Key("audience").Null().End().Finish();
        Displays.HologramSet(holo, spec5);
        Displays.HologramRemove(holo);
        Displays.HologramRemove(holo);
        Sender.Reply(ctx, "holograms done");
    }

    static void Npcs(int ctx)
    {
        int player = Directory.PlayerByName(Sender.Name(ctx) ?? "");
        byte[] spec1 = new ValueWriter().BeginMap().Key("world").String("world").Key("name").String("<aqua>Guide").Key("x").Float(1.5).Key("y").Float(64.0).Key("z").Float(2.5).End().Finish();
        int npc = Displays.NpcCreate(spec1, Clicked);
        byte[] spec2 = new ValueWriter().BeginMap().Key("world").String("nowhere").Key("name").String("Nobody").Key("x").Float(0.0).Key("y").Float(0.0).Key("z").Float(0.0).End().Finish();
        Displays.NpcCreate(spec2, null);
        byte[] spec3 = new ValueWriter().BeginMap().Key("name").String("<aqua>Guide II").Key("x").Float(3.5).Key("y").Float(65.0).Key("z").Float(4.5).Key("yaw").Float(90.0).Key("pitch").Float(-15.0).Key("looks_at_players").Bool(true).Key("health").Float(18.0).Key("held_slot").Int(0).Key("flags").Int(0).Key("pose").Int(0).Key("attributes").BeginMap().Key("max_health").Float(30.0).End().Key("equipment").BeginMap().Key("0").BeginMap().Key("item").String("minecraft:diamond_sword").Key("count").Int(1).End().End().End().Finish();
        Displays.NpcSet(npc, spec3);
        byte[] spec4 = new ValueWriter().BeginMap().Key("attributes").BeginMap().Key("no_such_attribute").Float(1.0).End().End().Finish();
        Displays.NpcSet(npc, spec4);
        byte[] buf5 = new byte[768];
        Displays.NpcGet(npc, buf5);
        byte[] buf6 = new byte[768];
        Displays.NpcItem(npc, 0, buf6);
        byte[] buf7 = new byte[768];
        Displays.NpcItem(npc, 1, buf7);
        Displays.NpcAttribute(npc, "max_health", out double out8);
        Displays.NpcAttribute(npc, "no_such_attribute", out double out9);
        Displays.NpcAnimate(npc, 0);
        int entity = EntityOf(npc);
        Displays.TabEntry(player, entity, 0);
        Displays.TabEntry(player, 424242, 0);
        gNpc = npc;
        Sender.Reply(ctx, "npcs done");
    }

    static void NpcEnd(int ctx)
    {
        Displays.NpcRemove(gNpc);
        Displays.NpcAnimate(gNpc, 0);
        Sender.Reply(ctx, "npc end done");
    }

    static void Tab(int ctx)
    {
        int player = Directory.PlayerByName(Sender.Name(ctx) ?? "");
        byte[] spec1 = new ValueWriter().BeginMap().Key("tab_header").String("<green>Top").Key("tab_footer").String("<gray>Bottom").Key("list_name").String("<gold>Steve").Key("listed").Bool(true).Key("list_order").Int(3).Key("score").Int(5).Key("waypoint_style").String("default").Key("waypoint_color").Int(16711680).End().Finish();
        Displays.PlayerSet(player, spec1);
        byte[] buf2 = new byte[768];
        Displays.PlayerGet(player, buf2);
        byte[] spec3 = new ValueWriter().BeginMap().Key("not_a_property").Int(1).End().Finish();
        Displays.PlayerSet(player, spec3);
        byte[] spec4 = new ValueWriter().BeginMap().Key("listed").String("yes").End().Finish();
        Displays.PlayerSet(player, spec4);
        byte[] spec5 = new ValueWriter().BeginMap().Key("tab_header").Null().Key("tab_footer").Null().Key("list_name").Null().End().Finish();
        Displays.PlayerSet(player, spec5);
        Sender.Reply(ctx, "tab done");
    }

    static void Surfaces(int ctx)
    {
        byte[] spec1 = new ValueWriter().BeginMap().Key("title").String("<gold>Static").Key("lines").BeginList().String("<white>one").End().End().Finish();
        int claim = Displays.SurfaceClaim("sidebar", 5, spec1, Render);
        byte[] spec2 = new ValueWriter().BeginMap().Key("title").String("<gold>Static").Key("lines").BeginList().String("<white>one").End().End().Finish();
        Displays.SurfaceClaim("nowhere", 1, spec2, null);
        byte[] spec3 = new ValueWriter().BeginMap().Key("title").String("<gold>Changed").Key("lines").BeginList().String("<white>two").End().End().Finish();
        Displays.SurfaceUpdate(claim, spec3);
        byte[] spec4 = new ValueWriter().BeginMap().Key("title").String("<gold>Changed").Key("lines").BeginList().String("<white>two").End().End().Finish();
        Displays.SurfaceAnswer(ctx, spec4);
        gClaim = claim;
        Sender.Reply(ctx, "surfaces done");
    }

    static void SurfaceEnd(int ctx)
    {
        Displays.SurfaceRelease(gClaim);
        Displays.SurfaceRelease(gClaim);
        byte[] spec1 = new ValueWriter().BeginMap().Key("title").String("<gold>Late").End().Finish();
        Displays.SurfaceUpdate(gClaim, spec1);
        Sender.Reply(ctx, "surface end done");
    }

    public static void RegisterAll()
    {
        Registry.Register("hud board", "", Board);
        Registry.Register("hud bars", "", Bars);
        Registry.Register("hud holograms", "", Holograms);
        Registry.Register("hud npcs", "", Npcs);
        Registry.Register("hud npc-end", "", NpcEnd);
        Registry.Register("hud tab", "", Tab);
        Registry.Register("hud surfaces", "", Surfaces);
        Registry.Register("hud surface-end", "", SurfaceEnd);
    }
}
