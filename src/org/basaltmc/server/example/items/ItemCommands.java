package org.basaltmc.server.example.items;

import static org.basaltmc.server.commands.Commands.command;
import static org.basaltmc.server.commands.Commands.commandArgI64;
import static org.basaltmc.server.commands.Commands.commandArgStr;
import static org.basaltmc.server.commands.Commands.commandReply;
import static org.basaltmc.server.commands.Commands.commandSourceName;
import static org.basaltmc.server.log.Log.logInfo;
import static org.basaltmc.server.players.SharedPlayers.playerByName;
import org.basaltmc.server.core.Args;
import org.basaltmc.server.items.Items;
import org.basaltmc.server.items.Items.Inventory;
import org.basaltmc.server.values.Values;
import org.basaltmc.server.values.Values.Writer;

public final class ItemCommands {
    private ItemCommands() {}

    private static final int DOCUMENT = 2048;

    private static int menu;

    private static int sourcePlayer(int ctx) {
        String source = commandSourceName(ctx);
        return playerByName(source == null ? "" : source);
    }

    private static void say(int ctx, String label, long value) {
        commandReply(ctx, "@items-line", new Args().add("text", label + " " + value));
    }

    private static long countArg(int ctx) {
        Long given = commandArgI64(ctx, "count");
        return given == null ? 1 : given;
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }

    private static void give(int ctx) {
        String id = text(commandArgStr(ctx, "id"));
        Inventory inventory = Inventory.ofPlayer(sourcePlayer(ctx));
        int count = (int) countArg(ctx);
        say(ctx, "left over", inventory.give(id, count, "<gold>Gift"));
        inventory.release();
    }

    private static void take(int ctx) {
        String id = text(commandArgStr(ctx, "id"));
        Inventory inventory = Inventory.ofPlayer(sourcePlayer(ctx));
        int count = (int) countArg(ctx);
        say(ctx, "taken", inventory.take(id, count));
        inventory.release();
    }

    private static void count(int ctx) {
        String id = text(commandArgStr(ctx, "id"));
        Inventory inventory = Inventory.ofPlayer(sourcePlayer(ctx));
        say(ctx, "count", inventory.count(id));
        say(ctx, "size", inventory.size());
        inventory.release();
    }

    private static void slot(int ctx) {
        Long given = commandArgI64(ctx, "slot");
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
        String id = text(commandArgStr(ctx, "id"));
        Long given = commandArgI64(ctx, "ticks");
        long ticks = given == null ? 0 : given;
        int player = sourcePlayer(ctx);
        say(ctx, "set", Items.itemSetCooldown(player, id, (int) ticks));
        say(ctx, "left", Items.itemCooldown(player, id));
    }

    private static void registry(int ctx) {
        String kind = text(commandArgStr(ctx, "kind"));
        String key = text(commandArgStr(ctx, "key"));
        say(ctx, "bytes", Items.registryGet(kind, key, DOCUMENT));
    }

    private static void level(int ctx) {
        Long given = commandArgI64(ctx, "level");
        long value = given == null ? 1 : given;
        byte[] spec = new Writer(128).beginMap().key("type").string("linear").key("base").number(1.0)
                .key("per_level_above_first").number(0.5).end().finish();
        double number = Items.enchantmentLevelValue(spec, (int) value);
        say(ctx, "value x100", (long) (number * 100.0));
    }

    private static void recipes(int ctx) {
        String json = "{\"type\":\"minecraft:crafting_shapeless\",\"ingredients\":[\"minecraft:dirt\"],"
                + "\"result\":{\"id\":\"minecraft:diamond\",\"count\":2}}";
        say(ctx, "added", Items.recipeAdd("example:dirt_gems", json));
        say(ctx, "again", Items.recipeAdd("example:dirt_gems", json));
        say(ctx, "has", Items.recipeKnown("example:dirt_gems") ? 1 : 0);
        say(ctx, "ids bytes", Items.recipeIds(DOCUMENT));
        say(ctx, "get bytes", Items.recipeGet("example:dirt_gems", DOCUMENT));
        say(ctx, "removed", Items.recipeRemove("example:dirt_gems"));
        say(ctx, "removed again", Items.recipeRemove("example:dirt_gems"));
    }

    private static void book(int ctx) {
        int player = sourcePlayer(ctx);
        byte[] ids = new Writer(128).beginList().string("minecraft:oak_planks").string("minecraft:stick").end()
                .finish();
        say(ctx, "learned", Items.recipeBookSet(player, ids, true));
        say(ctx, "knows", Items.recipeBookHas(player, "minecraft:stick"));
        say(ctx, "book bytes", Items.recipeBookGet(player, DOCUMENT));
        say(ctx, "forgot", Items.recipeBookSet(player, ids, false));
        say(ctx, "knows", Items.recipeBookHas(player, "minecraft:stick"));
        say(ctx, "resent", Items.recipeBookResend(player));
    }

    private static void craft(int ctx) {
        byte[] input = new Writer(512).beginMap().key("width").integer(1).key("height").integer(1).key("items")
                .beginList().beginMap().key("id").string("minecraft:oak_log").key("count").integer(1).end().end()
                .end().finish();
        Values.OutResult[] answer = new Values.OutResult[1];
        int status = Items.recipeStation("crafting", input, DOCUMENT, answer);
        say(ctx, "status", status);
        Values.Value root = answer[0].value();
        Values.Value count = root == null ? null : root.find("result");
        count = count == null ? null : count.find("count");
        say(ctx, "result count", count == null ? -1 : count.asLong(-1));
        say(ctx, "unknown", Items.recipeStation("grinder", input, DOCUMENT, null));
    }

    private static void clickDiamond(int ctx) {
        Values.Value root = Items.containerMenuClickInfo(ctx, 256);
        if (root != null) {
            Values.Value slot = root.find("slot");
            logInfo("clicked slot " + (slot == null ? -1 : slot.asLong(-1)));
        }
        Items.containerMenuClickResult(ctx, Items.item("minecraft:emerald", 1, null));
        logInfo("cancel " + Items.containerMenuClickCancel(ctx, true));
    }

    private static void clickRefuse(int ctx) {
        logInfo("uncancel " + Items.containerMenuClickCancel(ctx, false));
        Items.containerMenuClickResult(ctx, new byte[] {0});
    }

    private static void menu(int ctx) {
        int player = sourcePlayer(ctx);
        if (menu == 0) {
            byte[] spec = new Writer(128).beginMap().key("title").string("<gold>Items menu").key("rows").integer(1)
                    .end().finish();
            menu = Items.containerMenuCreate(spec);
        }
        say(ctx, "menu", menu > 0 ? 1 : 0);
        say(ctx, "slot 2", Items.menuSlot(menu, 2, "minecraft:diamond", 1, "<aqua>Click me",
                ItemCommands::clickDiamond));
        say(ctx, "slot 4", Items.menuSlot(menu, 4, "minecraft:barrier", 1, "<red>Refuse",
                ItemCommands::clickRefuse));
        say(ctx, "slot 99", Items.menuSlot(menu, 99, "minecraft:diamond", 1, null, null));
        say(ctx, "open", Items.containerMenuOpen(menu, player));
    }

    private static void closeMenu(int ctx) {
        say(ctx, "close", Items.containerMenuClose(menu, sourcePlayer(ctx)));
        say(ctx, "close again", Items.containerMenuClose(menu, sourcePlayer(ctx)));
    }

    private static void window(int ctx) {
        int player = sourcePlayer(ctx);
        byte[] spec = new Writer(512).beginMap().key("kind").string("hopper").key("title").string("<green>Sorter")
                .key("items").beginList().beginMap().key("id").string("minecraft:coal").key("count").integer(7).end()
                .end().end().finish();
        say(ctx, "container", Items.windowOpen(player, spec));
        say(ctx, "info bytes", Items.windowInfo(player, DOCUMENT));
        byte[] update = new Writer(128).beginMap().key("title").string("<red>Sorted").end().finish();
        say(ctx, "update", Items.windowUpdate(player, update));
        Inventory inventory = Inventory.ofWindow(player);
        say(ctx, "window size", inventory.size());
        say(ctx, "coal", inventory.count("minecraft:coal"));
        say(ctx, "close", Items.windowClose(player));
        say(ctx, "close again", Items.windowClose(player));
        inventory.release();
    }

    private static void merchant(int ctx) {
        byte[] spec = new Writer(1024).beginMap().key("title").string("<gold>Trader").key("offers").beginList()
                .beginMap().key("buy").beginMap().key("id").string("minecraft:emerald").key("count").integer(2).end()
                .key("sell").beginMap().key("id").string("minecraft:bread").key("count").integer(6).end()
                .key("max_uses").integer(8).key("xp").integer(3).key("price_multiplier").number(0.05).end()
                .end().end().finish();
        say(ctx, "merchant", Items.merchantOpen(sourcePlayer(ctx), spec));
        byte[] bad = new Writer(64).beginMap().end().finish();
        say(ctx, "bad merchant", Items.merchantOpen(sourcePlayer(ctx), bad));
    }

    private static void loot(int ctx) {
        say(ctx, "tables bytes", Items.lootTables(DOCUMENT));
        byte[] roll = new Writer(128).beginMap().key("seed").integer(3).key("luck").number(1.5).end().finish();
        say(ctx, "zombie bytes", Items.lootRoll("minecraft:entities/zombie", roll, DOCUMENT));
        say(ctx, "no table", Items.lootRoll("minecraft:nothing", null, DOCUMENT));
        say(ctx, "json bytes", Items.lootRollJson("{\"type\":\"minecraft:chest\",\"pools\":[]}", null, DOCUMENT));
        say(ctx, "bad json", Items.lootRollJson("nope", null, DOCUMENT));
        say(ctx, "number bytes", Items.lootNumber("7", true, 0, DOCUMENT));
        byte[] apply = new Writer(512).beginMap().key("functions").string("{\"function\":\"minecraft:set_count\"}")
                .key("item").beginMap().key("id").string("minecraft:bread").key("count").integer(2).end().end()
                .finish();
        say(ctx, "apply bytes", Items.lootApply(apply, DOCUMENT));
        byte[] enchant = new Writer(512).beginMap().key("item").beginMap().key("id").string("minecraft:diamond_sword")
                .end().key("levels").integer(30).end().finish();
        say(ctx, "enchant bytes", Items.lootEnchant(enchant, DOCUMENT));
        say(ctx, "trades bytes", Items.tradesRoll("minecraft:farmer", 2, null, DOCUMENT));
        say(ctx, "no profession", Items.tradesRoll("minecraft:nothing", 2, null, DOCUMENT));
    }

    public static void register() {
        command("items give <id: word> [count: int 1..64]", "", ItemCommands::give);
        command("items take <id: word> [count: int 1..64]", "", ItemCommands::take);
        command("items count <id: word>", "", ItemCommands::count);
        command("items slot <slot: int 0..40>", "", ItemCommands::slot);
        command("items hand", "", ItemCommands::hand);
        command("items chest", "", ItemCommands::chest);
        command("items ender", "", ItemCommands::ender);
        command("items cooldown <id: word> <ticks: int 0..1000>", "", ItemCommands::cooldown);
        command("items registry <kind: word> <key: word>", "", ItemCommands::registry);
        command("items level <level: int 1..10>", "", ItemCommands::level);
        command("items recipes", "", ItemCommands::recipes);
        command("items book", "", ItemCommands::book);
        command("items craft", "", ItemCommands::craft);
        command("items menu", "", ItemCommands::menu);
        command("items closemenu", "", ItemCommands::closeMenu);
        command("items window", "", ItemCommands::window);
        command("items merchant", "", ItemCommands::merchant);
        command("items loot", "", ItemCommands::loot);
    }
}
