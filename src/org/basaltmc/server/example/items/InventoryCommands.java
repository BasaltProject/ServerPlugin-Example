package org.basaltmc.server.example.items;

import org.basaltmc.server.commands.Registry;
import org.basaltmc.server.core.Args;
import org.basaltmc.server.items.Catalog;
import org.basaltmc.server.items.Catalog.Inventory;
import org.basaltmc.server.values.Documents;
import org.basaltmc.server.values.Documents.Writer;

import static org.basaltmc.server.commands.Arguments.i64;
import static org.basaltmc.server.commands.Arguments.str;
import static org.basaltmc.server.commands.Sender.name;
import static org.basaltmc.server.commands.Sender.reply;
import static org.basaltmc.server.log.Logger.logInfo;
import static org.basaltmc.server.players.Directory.playerByName;

public final class InventoryCommands {
    private InventoryCommands() {}

    private static final int DOCUMENT = 2048;

    private static int menu;

    private static int sourcePlayer(int ctx) {
        String source = name(ctx);
        return playerByName(source == null ? "" : source);
    }

    private static void say(int ctx, String label, long value) {
        reply(ctx, "@items-line", new Args().add("text", label + " " + value));
    }

    private static long countArg(int ctx) {
        Long given = i64(ctx, "count");
        return given == null ? 1 : given;
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }

    private static void give(int ctx) {
        String id = text(str(ctx, "id"));
        Inventory inventory = Inventory.ofPlayer(sourcePlayer(ctx));
        int count = (int) countArg(ctx);
        say(ctx, "left over", inventory.give(id, count, "<gold>Gift"));
        inventory.release();
    }

    private static void take(int ctx) {
        String id = text(str(ctx, "id"));
        Inventory inventory = Inventory.ofPlayer(sourcePlayer(ctx));
        int count = (int) countArg(ctx);
        say(ctx, "taken", inventory.take(id, count));
        inventory.release();
    }

    private static void count(int ctx) {
        String id = text(str(ctx, "id"));
        Inventory inventory = Inventory.ofPlayer(sourcePlayer(ctx));
        say(ctx, "count", inventory.count(id));
        say(ctx, "size", inventory.size());
        inventory.release();
    }

    private static void slot(int ctx) {
        Long given = i64(ctx, "slot");
        long index = given == null ? 0 : given;
        Inventory inventory = Inventory.ofPlayer(sourcePlayer(ctx));
        say(ctx, "in slot", inventory.slotCount((int) index));
        say(ctx, "bad slot", inventory.slotCount(99));
        inventory.release();
    }

    private static void hand(int ctx) {
        Inventory inventory = Inventory.ofPlayer(sourcePlayer(ctx));
        say(ctx, "held", inventory.heldSlot());
        say(ctx, "set held", inventory.setHeldSlot(4));
        say(ctx, "held", inventory.heldSlot());
        say(ctx, "contents bytes", inventory.contents(DOCUMENT));
        inventory.release();
    }

    private static void chest(int ctx) {
        Inventory block = Inventory.ofBlock("world", 10, 64, 10);
        say(ctx, "chest", block.size());
        say(ctx, "bread", block.count("minecraft:bread"));
        say(ctx, "left over", block.give("minecraft:apple", 3, null));
        say(ctx, "apples", block.count("minecraft:apple"));
        say(ctx, "taken", block.take("minecraft:bread", 2));
        Inventory none = Inventory.ofBlock("world", 0, 0, 0);
        say(ctx, "no block", none.handle());
        none.release();
        Inventory hopper = Inventory.ofBlock("world", 11, 64, 10);
        say(ctx, "hopper", hopper.size());
        hopper.release();
        block.release();
    }

    private static void ender(int ctx) {
        Inventory inventory = Inventory.ofEnder(sourcePlayer(ctx));
        say(ctx, "put", inventory.put(0, "minecraft:diamond", 5));
        say(ctx, "diamonds", inventory.count("minecraft:diamond"));
        say(ctx, "size", inventory.size());
        inventory.release();
    }

    private static void cooldown(int ctx) {
        String id = text(str(ctx, "id"));
        Long given = i64(ctx, "ticks");
        long ticks = given == null ? 0 : given;
        int player = sourcePlayer(ctx);
        say(ctx, "set", Catalog.itemSetCooldown(player, id, (int) ticks));
        say(ctx, "left", Catalog.itemCooldown(player, id));
    }

    private static void registry(int ctx) {
        String kind = text(str(ctx, "kind"));
        String key = text(str(ctx, "key"));
        say(ctx, "bytes", Catalog.registryGet(kind, key, DOCUMENT));
    }

    private static void level(int ctx) {
        Long given = i64(ctx, "level");
        long value = given == null ? 1 : given;
        byte[] spec = new Writer(128).beginMap().key("type").string("linear").key("base").number(1.0)
                .key("per_level_above_first").number(0.5).end().finish();
        double number = Catalog.enchantmentLevelValue(spec, (int) value);
        say(ctx, "value x100", (long) (number * 100.0));
    }

    private static void recipes(int ctx) {
        String json = "{\"type\":\"minecraft:crafting_shapeless\",\"ingredients\":[\"minecraft:dirt\"],"
                + "\"result\":{\"id\":\"minecraft:diamond\",\"count\":2}}";
        say(ctx, "added", Catalog.recipeAdd("example:dirt_gems", json));
        say(ctx, "again", Catalog.recipeAdd("example:dirt_gems", json));
        say(ctx, "has", Catalog.recipeKnown("example:dirt_gems") ? 1 : 0);
        say(ctx, "ids bytes", Catalog.recipeIds(DOCUMENT));
        say(ctx, "get bytes", Catalog.recipeGet("example:dirt_gems", DOCUMENT));
        say(ctx, "removed", Catalog.recipeRemove("example:dirt_gems"));
        say(ctx, "removed again", Catalog.recipeRemove("example:dirt_gems"));
    }

    private static void book(int ctx) {
        int player = sourcePlayer(ctx);
        byte[] ids = new Writer(128).beginList().string("minecraft:oak_planks").string("minecraft:stick").end()
                .finish();
        say(ctx, "learned", Catalog.recipeBookSet(player, ids, true));
        say(ctx, "knows", Catalog.recipeBookHas(player, "minecraft:stick"));
        say(ctx, "book bytes", Catalog.recipeBookGet(player, DOCUMENT));
        say(ctx, "forgot", Catalog.recipeBookSet(player, ids, false));
        say(ctx, "knows", Catalog.recipeBookHas(player, "minecraft:stick"));
        say(ctx, "resent", Catalog.recipeBookResend(player));
    }

    private static void craft(int ctx) {
        byte[] input = new Writer(512).beginMap().key("width").integer(1).key("height").integer(1).key("items")
                .beginList().beginMap().key("id").string("minecraft:oak_log").key("count").integer(1).end().end()
                .end().finish();
        Documents.OutResult[] answer = new Documents.OutResult[1];
        int status = Catalog.recipeStation("crafting", input, DOCUMENT, answer);
        say(ctx, "status", status);
        Documents.Value root = answer[0].value();
        Documents.Value count = root == null ? null : root.find("result");
        count = count == null ? null : count.find("count");
        say(ctx, "result count", count == null ? -1 : count.asLong(-1));
        say(ctx, "unknown", Catalog.recipeStation("grinder", input, DOCUMENT, null));
    }

    private static void clickDiamond(int ctx) {
        Documents.Value root = Catalog.containerMenuClickInfo(ctx, 256);
        if (root != null) {
            Documents.Value slot = root.find("slot");
            logInfo("clicked slot " + (slot == null ? -1 : slot.asLong(-1)));
        }
        Catalog.containerMenuClickResult(ctx, Catalog.item("minecraft:emerald", 1, null));
        logInfo("cancel " + Catalog.containerMenuClickCancel(ctx, true));
    }

    private static void clickRefuse(int ctx) {
        logInfo("uncancel " + Catalog.containerMenuClickCancel(ctx, false));
        Catalog.containerMenuClickResult(ctx, new byte[] {0});
    }

    private static void menu(int ctx) {
        int player = sourcePlayer(ctx);
        if (menu == 0) {
            byte[] spec = new Writer(128).beginMap().key("title").string("<gold>Items menu").key("rows").integer(1)
                    .end().finish();
            menu = Catalog.containerMenuCreate(spec);
        }
        say(ctx, "menu", menu > 0 ? 1 : 0);
        say(ctx, "slot 2", Catalog.menuSlot(menu, 2, "minecraft:diamond", 1, "<aqua>Click me",
                InventoryCommands::clickDiamond));
        say(ctx, "slot 4", Catalog.menuSlot(menu, 4, "minecraft:barrier", 1, "<red>Refuse",
                InventoryCommands::clickRefuse));
        say(ctx, "slot 99", Catalog.menuSlot(menu, 99, "minecraft:diamond", 1, null, null));
        say(ctx, "open", Catalog.containerMenuOpen(menu, player));
    }

    private static void closeMenu(int ctx) {
        say(ctx, "close", Catalog.containerMenuClose(menu, sourcePlayer(ctx)));
        say(ctx, "close again", Catalog.containerMenuClose(menu, sourcePlayer(ctx)));
    }

    private static void window(int ctx) {
        int player = sourcePlayer(ctx);
        byte[] spec = new Writer(512).beginMap().key("kind").string("hopper").key("title").string("<green>Sorter")
                .key("items").beginList().beginMap().key("id").string("minecraft:coal").key("count").integer(7).end()
                .end().end().finish();
        say(ctx, "container", Catalog.windowOpen(player, spec));
        say(ctx, "info bytes", Catalog.windowInfo(player, DOCUMENT));
        byte[] update = new Writer(128).beginMap().key("title").string("<red>Sorted").end().finish();
        say(ctx, "update", Catalog.windowUpdate(player, update));
        Inventory inventory = Inventory.ofWindow(player);
        say(ctx, "window size", inventory.size());
        say(ctx, "coal", inventory.count("minecraft:coal"));
        say(ctx, "close", Catalog.windowClose(player));
        say(ctx, "close again", Catalog.windowClose(player));
        inventory.release();
    }

    private static void merchant(int ctx) {
        byte[] spec = new Writer(1024).beginMap().key("title").string("<gold>Trader").key("offers").beginList()
                .beginMap().key("buy").beginMap().key("id").string("minecraft:emerald").key("count").integer(2).end()
                .key("sell").beginMap().key("id").string("minecraft:bread").key("count").integer(6).end()
                .key("max_uses").integer(8).key("xp").integer(3).key("price_multiplier").number(0.05).end()
                .end().end().finish();
        say(ctx, "merchant", Catalog.merchantOpen(sourcePlayer(ctx), spec));
        byte[] bad = new Writer(64).beginMap().end().finish();
        say(ctx, "bad merchant", Catalog.merchantOpen(sourcePlayer(ctx), bad));
    }

    private static void loot(int ctx) {
        say(ctx, "tables bytes", Catalog.lootTables(DOCUMENT));
        byte[] roll = new Writer(128).beginMap().key("seed").integer(3).key("luck").number(1.5).end().finish();
        say(ctx, "zombie bytes", Catalog.lootRoll("minecraft:entities/zombie", roll, DOCUMENT));
        say(ctx, "no table", Catalog.lootRoll("minecraft:nothing", null, DOCUMENT));
        say(ctx, "json bytes", Catalog.lootRollJson("{\"type\":\"minecraft:chest\",\"pools\":[]}", null, DOCUMENT));
        say(ctx, "bad json", Catalog.lootRollJson("nope", null, DOCUMENT));
        say(ctx, "number bytes", Catalog.lootNumber("7", true, 0, DOCUMENT));
        byte[] apply = new Writer(512).beginMap().key("functions").string("{\"function\":\"minecraft:set_count\"}")
                .key("item").beginMap().key("id").string("minecraft:bread").key("count").integer(2).end().end()
                .finish();
        say(ctx, "apply bytes", Catalog.lootApply(apply, DOCUMENT));
        byte[] enchant = new Writer(512).beginMap().key("item").beginMap().key("id").string("minecraft:diamond_sword")
                .end().key("levels").integer(30).end().finish();
        say(ctx, "enchant bytes", Catalog.lootEnchant(enchant, DOCUMENT));
        say(ctx, "trades bytes", Catalog.tradesRoll("minecraft:farmer", 2, null, DOCUMENT));
        say(ctx, "no profession", Catalog.tradesRoll("minecraft:nothing", 2, null, DOCUMENT));
    }

    public static void register() {
        Registry.register("items give <id: word> [count: int 1..64]", "", InventoryCommands::give);
        Registry.register("items take <id: word> [count: int 1..64]", "", InventoryCommands::take);
        Registry.register("items count <id: word>", "", InventoryCommands::count);
        Registry.register("items slot <slot: int 0..40>", "", InventoryCommands::slot);
        Registry.register("items hand", "", InventoryCommands::hand);
        Registry.register("items chest", "", InventoryCommands::chest);
        Registry.register("items ender", "", InventoryCommands::ender);
        Registry.register("items cooldown <id: word> <ticks: int 0..1000>", "", InventoryCommands::cooldown);
        Registry.register("items registry <kind: word> <key: word>", "", InventoryCommands::registry);
        Registry.register("items level <level: int 1..10>", "", InventoryCommands::level);
        Registry.register("items recipes", "", InventoryCommands::recipes);
        Registry.register("items book", "", InventoryCommands::book);
        Registry.register("items craft", "", InventoryCommands::craft);
        Registry.register("items menu", "", InventoryCommands::menu);
        Registry.register("items closemenu", "", InventoryCommands::closeMenu);
        Registry.register("items window", "", InventoryCommands::window);
        Registry.register("items merchant", "", InventoryCommands::merchant);
        Registry.register("items loot", "", InventoryCommands::loot);
    }
}
