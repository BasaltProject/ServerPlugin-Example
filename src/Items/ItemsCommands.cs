using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkCommands = global::Basalt.Server.Commands.Commands;
using SdkItems = global::Basalt.Server.Items.Items;
using SdkLogger = global::Basalt.Server.Log.Logger;
using SdkPlayers = global::Basalt.Server.Players.Players;

namespace Basalt.Server.Example.Items;

public static class ItemsCommands
{
    const int Document = 2048;

    static int menu;

    static int SourcePlayer(int ctx) => SdkPlayers.PlayerByName(SdkCommands.CommandSourceName(ctx) ?? "");

    static void Say(int ctx, string label, long value) => SayText(ctx, label + " " + value.ToString());

    static void SayText(int ctx, string text)
    {
        Args args = new();
        args.Add("text", text);
        SdkCommands.CommandReply(ctx, "@items-line", args);
    }

    static long CountArg(int ctx) => SdkCommands.CommandArgI64(ctx, "count") ?? 1;

    static void Give(int ctx)
    {
        string id = SdkCommands.CommandArgStr(ctx, "id");
        Inventory inventory = Inventory.OfPlayer(SourcePlayer(ctx));
        Say(ctx, "left over", inventory.Give(id, (int)CountArg(ctx), "<gold>Gift"));
        inventory.Release();
    }

    static void Take(int ctx)
    {
        string id = SdkCommands.CommandArgStr(ctx, "id");
        Inventory inventory = Inventory.OfPlayer(SourcePlayer(ctx));
        Say(ctx, "taken", inventory.Take(id, (int)CountArg(ctx)));
        inventory.Release();
    }

    static void Count(int ctx)
    {
        string id = SdkCommands.CommandArgStr(ctx, "id");
        Inventory inventory = Inventory.OfPlayer(SourcePlayer(ctx));
        Say(ctx, "count", inventory.Count(id));
        Say(ctx, "size", inventory.Size());
        inventory.Release();
    }

    static void Slot(int ctx)
    {
        long index = SdkCommands.CommandArgI64(ctx, "slot") ?? 0;
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
        string id = SdkCommands.CommandArgStr(ctx, "id");
        long ticks = SdkCommands.CommandArgI64(ctx, "ticks") ?? 0;
        int player = SourcePlayer(ctx);
        Say(ctx, "set", SdkItems.ItemSetCooldown(player, id, (int)ticks));
        Say(ctx, "left", SdkItems.ItemCooldown(player, id));
    }

    static void Registry(int ctx)
    {
        string kind = SdkCommands.CommandArgStr(ctx, "kind");
        string key = SdkCommands.CommandArgStr(ctx, "key");
        Say(ctx, "bytes", SdkItems.RegistryGet(kind, key, new byte[Document]));
    }

    static void Level(int ctx)
    {
        long value = SdkCommands.CommandArgI64(ctx, "level") ?? 1;
        ValueWriter spec = new(128);
        spec.BeginMap().Key("type").String("linear").Key("base").Float(1.0).Key("per_level_above_first").Float(0.5).End();
        double number = SdkItems.EnchantmentLevelValue(spec.Finish(), (int)value);
        Say(ctx, "value x100", (long)(number * 100.0));
    }

    static void Recipes(int ctx)
    {
        string json = "{\"type\":\"minecraft:crafting_shapeless\",\"ingredients\":[\"minecraft:dirt\"]," +
                      "\"result\":{\"id\":\"minecraft:diamond\",\"count\":2}}";
        Say(ctx, "added", SdkItems.RecipeAdd("example:dirt_gems", json));
        Say(ctx, "again", SdkItems.RecipeAdd("example:dirt_gems", json));
        Say(ctx, "has", SdkItems.RecipeKnown("example:dirt_gems") ? 1 : 0);
        byte[] output = new byte[Document];
        Say(ctx, "ids bytes", SdkItems.RecipeIds(output));
        Say(ctx, "get bytes", SdkItems.RecipeGet("example:dirt_gems", output));
        Say(ctx, "removed", SdkItems.RecipeRemove("example:dirt_gems"));
        Say(ctx, "removed again", SdkItems.RecipeRemove("example:dirt_gems"));
    }

    static void Book(int ctx)
    {
        int player = SourcePlayer(ctx);
        ValueWriter list = new(128);
        list.BeginList().String("minecraft:oak_planks").String("minecraft:stick").End();
        byte[] ids = list.Finish();
        Say(ctx, "learned", SdkItems.RecipeBookSet(player, ids, true));
        Say(ctx, "knows", SdkItems.RecipeBookHas(player, "minecraft:stick"));
        Say(ctx, "book bytes", SdkItems.RecipeBookGet(player, new byte[Document]));
        Say(ctx, "forgot", SdkItems.RecipeBookSet(player, ids, false));
        Say(ctx, "knows", SdkItems.RecipeBookHas(player, "minecraft:stick"));
        Say(ctx, "resent", SdkItems.RecipeBookResend(player));
    }

    static unsafe void Craft(int ctx)
    {
        ValueWriter w = new(512);
        w.BeginMap().Key("width").Int(1).Key("height").Int(1).Key("items").BeginList();
        w.BeginMap().Key("id").String("minecraft:oak_log").Key("count").Int(1).End();
        w.End().End();
        byte[] input = w.Finish();
        OutResult answer = ValueCall.Out(Document, (output, cap) =>
            SdkItems.RecipeStation("crafting", input, output, cap));
        Say(ctx, "status", answer.Error);
        long count = -1;
        if (answer.TryValue(out Value root)) root.Find("result").Find("count").TryInt(out count);
        Say(ctx, "result count", count);
        Say(ctx, "unknown", SdkItems.RecipeStation("grinder", input, new byte[Document]));
    }

    static void ClickDiamond(int ctx)
    {
        byte[] info = new byte[256];
        int length = SdkItems.ContainerMenuClickInfo(ctx, info);
        if (length > 0 && length <= info.Length)
        {
            byte[] doc = new byte[length];
            System.Array.Copy(info, doc, length);
            if (Value.TryOpen(doc, out Value root))
            {
                long slot = -1;
                root.Find("slot").TryInt(out slot);
                SdkLogger.LogInfo("clicked slot " + slot.ToString());
            }
        }
        SdkItems.ContainerMenuClickResult(ctx, new Item("minecraft:emerald", 1));
        SdkLogger.LogInfo("cancel " + SdkItems.ContainerMenuClickCancel(ctx, true).ToString());
    }

    static void ClickRefuse(int ctx)
    {
        SdkLogger.LogInfo("uncancel " + SdkItems.ContainerMenuClickCancel(ctx, false).ToString());
        SdkItems.ContainerMenuClickResultNull(ctx);
    }

    static void Menu(int ctx)
    {
        int player = SourcePlayer(ctx);
        if (menu == 0)
        {
            ValueWriter spec = new(128);
            spec.BeginMap().Key("title").String("<gold>Items menu").Key("rows").Int(1).End();
            menu = SdkItems.ContainerMenuCreate(spec.Finish());
        }
        Say(ctx, "menu", menu > 0 ? 1 : 0);
        Say(ctx, "slot 2", SdkItems.MenuSlot(menu, 2, "minecraft:diamond", 1, "<aqua>Click me", ClickDiamond));
        Say(ctx, "slot 4", SdkItems.MenuSlot(menu, 4, "minecraft:barrier", 1, "<red>Refuse", ClickRefuse));
        Say(ctx, "slot 99", SdkItems.MenuSlot(menu, 99, "minecraft:diamond", 1, null, null));
        Say(ctx, "open", SdkItems.ContainerMenuOpen(menu, player));
    }

    static void CloseMenu(int ctx)
    {
        Say(ctx, "close", SdkItems.ContainerMenuClose(menu, SourcePlayer(ctx)));
        Say(ctx, "close again", SdkItems.ContainerMenuClose(menu, SourcePlayer(ctx)));
    }

    static void Window(int ctx)
    {
        int player = SourcePlayer(ctx);
        ValueWriter spec = new(512);
        spec.BeginMap().Key("kind").String("hopper").Key("title").String("<green>Sorter").Key("items").BeginList();
        spec.BeginMap().Key("id").String("minecraft:coal").Key("count").Int(7).End();
        spec.End().End();
        Say(ctx, "container", SdkItems.WindowOpen(player, spec.Finish()));
        Say(ctx, "info bytes", SdkItems.WindowInfo(player, new byte[Document]));
        ValueWriter update = new(128);
        update.BeginMap().Key("title").String("<red>Sorted").End();
        Say(ctx, "update", SdkItems.WindowUpdate(player, update.Finish()));
        Inventory inventory = Inventory.OfWindow(player);
        Say(ctx, "window size", inventory.Size());
        Say(ctx, "coal", inventory.Count("minecraft:coal"));
        Say(ctx, "close", SdkItems.WindowClose(player));
        Say(ctx, "close again", SdkItems.WindowClose(player));
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
        Say(ctx, "merchant", SdkItems.MerchantOpen(SourcePlayer(ctx), spec.Finish()));
        ValueWriter bad = new(64);
        bad.BeginMap().End();
        Say(ctx, "bad merchant", SdkItems.MerchantOpen(SourcePlayer(ctx), bad.Finish()));
    }

    static void Loot(int ctx)
    {
        byte[] output = new byte[Document];
        Say(ctx, "tables bytes", SdkItems.LootTables(output));
        ValueWriter roll = new(128);
        roll.BeginMap().Key("seed").Int(3).Key("luck").Float(1.5).End();
        Say(ctx, "zombie bytes", SdkItems.LootRoll("minecraft:entities/zombie", roll.Finish(), output));
        Say(ctx, "no table", SdkItems.LootRoll("minecraft:nothing", null, output));
        string table = "{\"type\":\"minecraft:chest\",\"pools\":[]}";
        Say(ctx, "json bytes", SdkItems.LootRollJson(table, null, output));
        Say(ctx, "bad json", SdkItems.LootRollJson("nope", null, output));
        Say(ctx, "number bytes", SdkItems.LootNumber("7", true, 0, output));
        ValueWriter apply = new(512);
        apply.BeginMap().Key("functions").String("{\"function\":\"minecraft:set_count\"}").Key("item").BeginMap();
        apply.Key("id").String("minecraft:bread").Key("count").Int(2).End().End();
        Say(ctx, "apply bytes", SdkItems.LootApply(apply.Finish(), output));
        ValueWriter enchant = new(512);
        enchant.BeginMap().Key("item").BeginMap().Key("id").String("minecraft:diamond_sword").End().Key("levels").Int(30).End();
        Say(ctx, "enchant bytes", SdkItems.LootEnchant(enchant.Finish(), output));
        Say(ctx, "trades bytes", SdkItems.TradesRoll("minecraft:farmer", 2, null, output));
        Say(ctx, "no profession", SdkItems.TradesRoll("minecraft:nothing", 2, null, output));
    }

    public static void RegisterAll()
    {
        SdkCommands.Command("items give <id: word> [count: int 1..64]", "", Give);
        SdkCommands.Command("items take <id: word> [count: int 1..64]", "", Take);
        SdkCommands.Command("items count <id: word>", "", Count);
        SdkCommands.Command("items slot <slot: int 0..40>", "", Slot);
        SdkCommands.Command("items hand", "", Hand);
        SdkCommands.Command("items chest", "", Chest);
        SdkCommands.Command("items ender", "", Ender);
        SdkCommands.Command("items cooldown <id: word> <ticks: int 0..1000>", "", Cooldown);
        SdkCommands.Command("items registry <kind: word> <key: word>", "", Registry);
        SdkCommands.Command("items level <level: int 1..10>", "", Level);
        SdkCommands.Command("items recipes", "", Recipes);
        SdkCommands.Command("items book", "", Book);
        SdkCommands.Command("items craft", "", Craft);
        SdkCommands.Command("items menu", "", Menu);
        SdkCommands.Command("items closemenu", "", CloseMenu);
        SdkCommands.Command("items window", "", Window);
        SdkCommands.Command("items merchant", "", Merchant);
        SdkCommands.Command("items loot", "", Loot);
    }
}
