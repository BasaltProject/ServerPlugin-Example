using Basalt.Server.Commands;
using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Log;
using Basalt.Server.Players;
using Basalt.Server.Values;
using System;

namespace Basalt.Server.Example.Items;

public static class InventoryCommands
{
    const int Document = 2048;

    static int menu;

    static int SourcePlayer(int ctx) => Directory.PlayerByName(Sender.Name(ctx) ?? "");

    static void Say(int ctx, string label, long value) => SayText(ctx, label + " " + value.ToString());

    static void SayText(int ctx, string text)
    {
        Args args = new();
        args.Add("text", text);
        Sender.Reply(ctx, "@items-line", args);
    }

    static long CountArg(int ctx) => Arguments.I64(ctx, "count") ?? 1;

    static void Give(int ctx)
    {
        string id = Arguments.Str(ctx, "id");
        Inventory inventory = Inventory.OfPlayer(SourcePlayer(ctx));
        Say(ctx, "left over", inventory.Give(id, (int)CountArg(ctx), "<gold>Gift"));
        inventory.Release();
    }

    static void Take(int ctx)
    {
        string id = Arguments.Str(ctx, "id");
        Inventory inventory = Inventory.OfPlayer(SourcePlayer(ctx));
        Say(ctx, "taken", inventory.Take(id, (int)CountArg(ctx)));
        inventory.Release();
    }

    static void Count(int ctx)
    {
        string id = Arguments.Str(ctx, "id");
        Inventory inventory = Inventory.OfPlayer(SourcePlayer(ctx));
        Say(ctx, "count", inventory.Count(id));
        Say(ctx, "size", inventory.Size());
        inventory.Release();
    }

    static void Slot(int ctx)
    {
        long index = Arguments.I64(ctx, "slot") ?? 0;
        Inventory inventory = Inventory.OfPlayer(SourcePlayer(ctx));
        Say(ctx, "in slot", inventory.SlotCount((int)index));
        Say(ctx, "bad slot", inventory.SlotCount(99));
        inventory.Release();
    }

    static void Hand(int ctx)
    {
        Inventory inventory = Inventory.OfPlayer(SourcePlayer(ctx));
        Say(ctx, "held", inventory.HeldSlot());
        Say(ctx, "set held", inventory.SetHeldSlot(4));
        Say(ctx, "held", inventory.HeldSlot());
        Say(ctx, "contents bytes", inventory.Contents(new byte[Document]));
        inventory.Release();
    }

    static void Chest(int ctx)
    {
        Inventory block = Inventory.OfBlock("world", 10, 64, 10);
        Say(ctx, "chest", block.Size());
        Say(ctx, "bread", block.Count("minecraft:bread"));
        Say(ctx, "left over", block.Give("minecraft:apple", 3));
        Say(ctx, "apples", block.Count("minecraft:apple"));
        Say(ctx, "taken", block.Take("minecraft:bread", 2));
        Inventory none = Inventory.OfBlock("world", 0, 0, 0);
        Say(ctx, "no block", none.Handle);
        none.Release();
        Inventory hopper = Inventory.OfBlock("world", 11, 64, 10);
        Say(ctx, "hopper", hopper.Size());
        hopper.Release();
        block.Release();
    }

    static void Ender(int ctx)
    {
        Inventory inventory = Inventory.OfEnder(SourcePlayer(ctx));
        Say(ctx, "put", inventory.Put(0, "minecraft:diamond", 5));
        Say(ctx, "diamonds", inventory.Count("minecraft:diamond"));
        Say(ctx, "size", inventory.Size());
        inventory.Release();
    }

    static void Cooldown(int ctx)
    {
        string id = Arguments.Str(ctx, "id");
        long ticks = Arguments.I64(ctx, "ticks") ?? 0;
        int player = SourcePlayer(ctx);
        Say(ctx, "set", Catalog.ItemSetCooldown(player, id, (int)ticks));
        Say(ctx, "left", Catalog.ItemCooldown(player, id));
    }

    static void ShowCatalog(int ctx)
    {
        string kind = Arguments.Str(ctx, "kind");
        string key = Arguments.Str(ctx, "key");
        Say(ctx, "bytes", Catalog.RegistryGet(kind, key, new byte[Document]));
    }

    static void Level(int ctx)
    {
        long value = Arguments.I64(ctx, "level") ?? 1;
        ValueWriter spec = new(128);
        spec.BeginMap().Key("type").String("linear").Key("base").Float(1.0).Key("per_level_above_first").Float(0.5).End();
        double number = Catalog.EnchantmentLevelValue(spec.Finish(), (int)value);
        Say(ctx, "value x100", (long)(number * 100.0));
    }

    static void Recipes(int ctx)
    {
        string json = "{\"type\":\"minecraft:crafting_shapeless\",\"ingredients\":[\"minecraft:dirt\"]," +
                      "\"result\":{\"id\":\"minecraft:diamond\",\"count\":2}}";
        Say(ctx, "added", Catalog.RecipeAdd("example:dirt_gems", json));
        Say(ctx, "again", Catalog.RecipeAdd("example:dirt_gems", json));
        Say(ctx, "has", Catalog.RecipeKnown("example:dirt_gems") ? 1 : 0);
        byte[] output = new byte[Document];
        Say(ctx, "ids bytes", Catalog.RecipeIds(output));
        Say(ctx, "get bytes", Catalog.RecipeGet("example:dirt_gems", output));
        Say(ctx, "removed", Catalog.RecipeRemove("example:dirt_gems"));
        Say(ctx, "removed again", Catalog.RecipeRemove("example:dirt_gems"));
    }

    static void Book(int ctx)
    {
        int player = SourcePlayer(ctx);
        ValueWriter list = new(128);
        list.BeginList().String("minecraft:oak_planks").String("minecraft:stick").End();
        byte[] ids = list.Finish();
        Say(ctx, "learned", Catalog.RecipeBookSet(player, ids, true));
        Say(ctx, "knows", Catalog.RecipeBookHas(player, "minecraft:stick"));
        Say(ctx, "book bytes", Catalog.RecipeBookGet(player, new byte[Document]));
        Say(ctx, "forgot", Catalog.RecipeBookSet(player, ids, false));
        Say(ctx, "knows", Catalog.RecipeBookHas(player, "minecraft:stick"));
        Say(ctx, "resent", Catalog.RecipeBookResend(player));
    }

    static unsafe void Craft(int ctx)
    {
        ValueWriter w = new(512);
        w.BeginMap().Key("width").Int(1).Key("height").Int(1).Key("items").BeginList();
        w.BeginMap().Key("id").String("minecraft:oak_log").Key("count").Int(1).End();
        w.End().End();
        byte[] input = w.Finish();
        OutResult answer = ValueCall.Out(Document, (output, cap) =>
            Catalog.RecipeStation("crafting", input, output, cap));
        Say(ctx, "status", answer.Error);
        long count = -1;
        if (answer.TryValue(out Value root)) root.Find("result").Find("count").TryInt(out count);
        Say(ctx, "result count", count);
        Say(ctx, "unknown", Catalog.RecipeStation("grinder", input, new byte[Document]));
    }

    static void ClickDiamond(int ctx)
    {
        byte[] info = new byte[256];
        int length = Catalog.ContainerMenuClickInfo(ctx, info);
        if (length > 0 && length <= info.Length)
        {
            byte[] doc = new byte[length];
            Array.Copy(info, doc, length);
            if (Value.TryOpen(doc, out Value root))
            {
                long slot = -1;
                root.Find("slot").TryInt(out slot);
                Logger.LogInfo("clicked slot " + slot.ToString());
            }
        }
        Catalog.ContainerMenuClickResult(ctx, new Item("minecraft:emerald", 1));
        Logger.LogInfo("cancel " + Catalog.ContainerMenuClickCancel(ctx, true).ToString());
    }

    static void ClickRefuse(int ctx)
    {
        Logger.LogInfo("uncancel " + Catalog.ContainerMenuClickCancel(ctx, false).ToString());
        Catalog.ContainerMenuClickResultNull(ctx);
    }

    static void Menu(int ctx)
    {
        int player = SourcePlayer(ctx);
        if (menu == 0)
        {
            ValueWriter spec = new(128);
            spec.BeginMap().Key("title").String("<gold>Items menu").Key("rows").Int(1).End();
            menu = Catalog.ContainerMenuCreate(spec.Finish());
        }
        Say(ctx, "menu", menu > 0 ? 1 : 0);
        Say(ctx, "slot 2", Catalog.MenuSlot(menu, 2, "minecraft:diamond", 1, "<aqua>Click me", ClickDiamond));
        Say(ctx, "slot 4", Catalog.MenuSlot(menu, 4, "minecraft:barrier", 1, "<red>Refuse", ClickRefuse));
        Say(ctx, "slot 99", Catalog.MenuSlot(menu, 99, "minecraft:diamond", 1, null, null));
        Say(ctx, "open", Catalog.ContainerMenuOpen(menu, player));
    }

    static void CloseMenu(int ctx)
    {
        Say(ctx, "close", Catalog.ContainerMenuClose(menu, SourcePlayer(ctx)));
        Say(ctx, "close again", Catalog.ContainerMenuClose(menu, SourcePlayer(ctx)));
    }

    static void Window(int ctx)
    {
        int player = SourcePlayer(ctx);
        ValueWriter spec = new(512);
        spec.BeginMap().Key("kind").String("hopper").Key("title").String("<green>Sorter").Key("items").BeginList();
        spec.BeginMap().Key("id").String("minecraft:coal").Key("count").Int(7).End();
        spec.End().End();
        Say(ctx, "container", Catalog.WindowOpen(player, spec.Finish()));
        Say(ctx, "info bytes", Catalog.WindowInfo(player, new byte[Document]));
        ValueWriter update = new(128);
        update.BeginMap().Key("title").String("<red>Sorted").End();
        Say(ctx, "update", Catalog.WindowUpdate(player, update.Finish()));
        Inventory inventory = Inventory.OfWindow(player);
        Say(ctx, "window size", inventory.Size());
        Say(ctx, "coal", inventory.Count("minecraft:coal"));
        Say(ctx, "close", Catalog.WindowClose(player));
        Say(ctx, "close again", Catalog.WindowClose(player));
        inventory.Release();
    }

    static void Merchant(int ctx)
    {
        ValueWriter spec = new(1024);
        spec.BeginMap().Key("title").String("<gold>Trader").Key("offers").BeginList();
        spec.BeginMap().Key("buy").BeginMap().Key("id").String("minecraft:emerald").Key("count").Int(2).End();
        spec.Key("sell").BeginMap().Key("id").String("minecraft:bread").Key("count").Int(6).End();
        spec.Key("max_uses").Int(8).Key("xp").Int(3).Key("price_multiplier").Float(0.05).End();
        spec.End().End();
        Say(ctx, "merchant", Catalog.MerchantOpen(SourcePlayer(ctx), spec.Finish()));
        ValueWriter bad = new(64);
        bad.BeginMap().End();
        Say(ctx, "bad merchant", Catalog.MerchantOpen(SourcePlayer(ctx), bad.Finish()));
    }

    static void Loot(int ctx)
    {
        byte[] output = new byte[Document];
        Say(ctx, "tables bytes", Catalog.LootTables(output));
        ValueWriter roll = new(128);
        roll.BeginMap().Key("seed").Int(3).Key("luck").Float(1.5).End();
        Say(ctx, "zombie bytes", Catalog.LootRoll("minecraft:entities/zombie", roll.Finish(), output));
        Say(ctx, "no table", Catalog.LootRoll("minecraft:nothing", null, output));
        string table = "{\"type\":\"minecraft:chest\",\"pools\":[]}";
        Say(ctx, "json bytes", Catalog.LootRollJson(table, null, output));
        Say(ctx, "bad json", Catalog.LootRollJson("nope", null, output));
        Say(ctx, "number bytes", Catalog.LootNumber("7", true, 0, output));
        ValueWriter apply = new(512);
        apply.BeginMap().Key("functions").String("{\"function\":\"minecraft:set_count\"}").Key("item").BeginMap();
        apply.Key("id").String("minecraft:bread").Key("count").Int(2).End().End();
        Say(ctx, "apply bytes", Catalog.LootApply(apply.Finish(), output));
        ValueWriter enchant = new(512);
        enchant.BeginMap().Key("item").BeginMap().Key("id").String("minecraft:diamond_sword").End().Key("levels").Int(30).End();
        Say(ctx, "enchant bytes", Catalog.LootEnchant(enchant.Finish(), output));
        Say(ctx, "trades bytes", Catalog.TradesRoll("minecraft:farmer", 2, null, output));
        Say(ctx, "no profession", Catalog.TradesRoll("minecraft:nothing", 2, null, output));
    }

    public static void RegisterAll()
    {
        Registry.Register("items give <id: word> [count: int 1..64]", "", Give);
        Registry.Register("items take <id: word> [count: int 1..64]", "", Take);
        Registry.Register("items count <id: word>", "", Count);
        Registry.Register("items slot <slot: int 0..40>", "", Slot);
        Registry.Register("items hand", "", Hand);
        Registry.Register("items chest", "", Chest);
        Registry.Register("items ender", "", Ender);
        Registry.Register("items cooldown <id: word> <ticks: int 0..1000>", "", Cooldown);
        Registry.Register("items registry <kind: word> <key: word>", "", ShowCatalog);
        Registry.Register("items level <level: int 1..10>", "", Level);
        Registry.Register("items recipes", "", Recipes);
        Registry.Register("items book", "", Book);
        Registry.Register("items craft", "", Craft);
        Registry.Register("items menu", "", Menu);
        Registry.Register("items closemenu", "", CloseMenu);
        Registry.Register("items window", "", Window);
        Registry.Register("items merchant", "", Merchant);
        Registry.Register("items loot", "", Loot);
    }
}
