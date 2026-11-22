package org.basaltmc.server.example.items


import org.basaltmc.server.core.Args
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.items.Inventory
import org.basaltmc.server.values.ValueWriter

object ItemCommands {
    private const val DOCUMENT = 2048

    private var menu = 0

    private fun sourcePlayer(ctx: Ctx): Int = org.basaltmc.server.players.playerByName(org.basaltmc.server.commands.commandSourceName(ctx) ?: "")

    private fun say(ctx: Ctx, label: String, value: Long) {
        org.basaltmc.server.commands.commandReply(ctx, "@items-line", Args().add("text", "$label $value"))
    }

    private fun say(ctx: Ctx, label: String, value: Int) = say(ctx, label, value.toLong())

    private fun countArg(ctx: Ctx): Long = org.basaltmc.server.commands.commandArgI64(ctx, "count") ?: 1L

    private fun give(ctx: Ctx) {
        val id = org.basaltmc.server.commands.commandArgStr(ctx, "id") ?: ""
        val inventory = Inventory.ofPlayer(sourcePlayer(ctx))
        val count = countArg(ctx).toInt()
        say(ctx, "left over", inventory.give(id, count, "<gold>Gift"))
        inventory.release()
    }

    private fun take(ctx: Ctx) {
        val id = org.basaltmc.server.commands.commandArgStr(ctx, "id") ?: ""
        val inventory = Inventory.ofPlayer(sourcePlayer(ctx))
        val count = countArg(ctx).toInt()
        say(ctx, "taken", inventory.take(id, count))
        inventory.release()
    }

    private fun count(ctx: Ctx) {
        val id = org.basaltmc.server.commands.commandArgStr(ctx, "id") ?: ""
        val inventory = Inventory.ofPlayer(sourcePlayer(ctx))
        say(ctx, "count", inventory.count(id))
        say(ctx, "size", inventory.size())
        inventory.release()
    }

    private fun slot(ctx: Ctx) {
        val index = org.basaltmc.server.commands.commandArgI64(ctx, "slot") ?: 0L
        val inventory = Inventory.ofPlayer(sourcePlayer(ctx))
        say(ctx, "in slot", inventory.slotCount(index.toInt()))
        say(ctx, "bad slot", inventory.slotCount(99))
        inventory.release()
    }

    private fun hand(ctx: Ctx) {
        val inventory = Inventory.ofPlayer(sourcePlayer(ctx))
        say(ctx, "held", inventory.heldSlot())
        say(ctx, "set held", inventory.setHeldSlot(4))
        say(ctx, "held", inventory.heldSlot())
        say(ctx, "contents bytes", inventory.contents(DOCUMENT))
        inventory.release()
    }

    private fun chest(ctx: Ctx) {
        val block = Inventory.ofBlock("world", 10, 64, 10)
        say(ctx, "chest", block.size())
        say(ctx, "bread", block.count("minecraft:bread"))
        say(ctx, "left over", block.give("minecraft:apple", 3))
        say(ctx, "apples", block.count("minecraft:apple"))
        say(ctx, "taken", block.take("minecraft:bread", 2))
        val none = Inventory.ofBlock("world", 0, 0, 0)
        say(ctx, "no block", none.handle)
        none.release()
        val hopper = Inventory.ofBlock("world", 11, 64, 10)
        say(ctx, "hopper", hopper.size())
        hopper.release()
        block.release()
    }

    private fun ender(ctx: Ctx) {
        val inventory = Inventory.ofEnder(sourcePlayer(ctx))
        say(ctx, "put", inventory.put(0, "minecraft:diamond", 5))
        say(ctx, "diamonds", inventory.count("minecraft:diamond"))
        say(ctx, "size", inventory.size())
        inventory.release()
    }

    private fun cooldown(ctx: Ctx) {
        val id = org.basaltmc.server.commands.commandArgStr(ctx, "id") ?: ""
        val ticks = org.basaltmc.server.commands.commandArgI64(ctx, "ticks") ?: 0L
        val player = sourcePlayer(ctx)
        say(ctx, "set", org.basaltmc.server.items.itemSetCooldown(player, id, ticks.toInt()))
        say(ctx, "left", org.basaltmc.server.items.itemCooldown(player, id))
    }

    private fun registry(ctx: Ctx) {
        val kind = org.basaltmc.server.commands.commandArgStr(ctx, "kind") ?: ""
        val key = org.basaltmc.server.commands.commandArgStr(ctx, "key") ?: ""
        say(ctx, "bytes", org.basaltmc.server.items.registryGet(kind, key, DOCUMENT))
    }

    private fun level(ctx: Ctx) {
        val value = org.basaltmc.server.commands.commandArgI64(ctx, "level") ?: 1L
        val spec = ValueWriter(128).beginMap().key("type").string("linear").key("base").float(1.0)
            .key("per_level_above_first").float(0.5).end().finish()
        val number = org.basaltmc.server.items.enchantmentLevelValue(spec, value.toInt())
        say(ctx, "value x100", (number * 100.0).toLong())
    }

    private fun recipes(ctx: Ctx) {
        val json = "{\"type\":\"minecraft:crafting_shapeless\",\"ingredients\":[\"minecraft:dirt\"]," +
            "\"result\":{\"id\":\"minecraft:diamond\",\"count\":2}}"
        say(ctx, "added", org.basaltmc.server.items.recipeAdd("example:dirt_gems", json))
        say(ctx, "again", org.basaltmc.server.items.recipeAdd("example:dirt_gems", json))
        say(ctx, "has", if (org.basaltmc.server.items.recipeKnown("example:dirt_gems")) 1 else 0)
        say(ctx, "ids bytes", org.basaltmc.server.items.recipeIds(DOCUMENT))
        say(ctx, "get bytes", org.basaltmc.server.items.recipeGet("example:dirt_gems", DOCUMENT))
        say(ctx, "removed", org.basaltmc.server.items.recipeRemove("example:dirt_gems"))
        say(ctx, "removed again", org.basaltmc.server.items.recipeRemove("example:dirt_gems"))
    }

    private fun book(ctx: Ctx) {
        val player = sourcePlayer(ctx)
        val ids = ValueWriter(128).beginList().string("minecraft:oak_planks").string("minecraft:stick").end().finish()
        say(ctx, "learned", org.basaltmc.server.items.recipeBookSet(player, ids, true))
        say(ctx, "knows", org.basaltmc.server.items.recipeBookHas(player, "minecraft:stick"))
        say(ctx, "book bytes", org.basaltmc.server.items.recipeBookGet(player, DOCUMENT))
        say(ctx, "forgot", org.basaltmc.server.items.recipeBookSet(player, ids, false))
        say(ctx, "knows", org.basaltmc.server.items.recipeBookHas(player, "minecraft:stick"))
        say(ctx, "resent", org.basaltmc.server.items.recipeBookResend(player))
    }

    private fun craft(ctx: Ctx) {
        val input = ValueWriter(512).beginMap().key("width").int(1).key("height").int(1).key("items").beginList()
            .beginMap().key("id").string("minecraft:oak_log").key("count").int(1).end().end().end().finish()
        val answer = org.basaltmc.server.items.recipeStation("crafting", input, DOCUMENT)
        say(ctx, "status", answer.error)
        say(ctx, "result count", answer.value()?.find("result")?.find("count")?.asLong() ?: -1L)
        say(ctx, "unknown", org.basaltmc.server.items.recipeStation("grinder", input, DOCUMENT).error)
    }

    private fun clickDiamond(ctx: Ctx) {
        val root = org.basaltmc.server.items.containerMenuClickInfo(ctx, 256)
        if (root != null) org.basaltmc.server.log.logInfo("clicked slot ${root.find("slot")?.asLong() ?: -1L}")
        org.basaltmc.server.items.containerMenuClickResult(ctx, org.basaltmc.server.items.itemStack("minecraft:emerald", 1))
        org.basaltmc.server.log.logInfo("cancel ${org.basaltmc.server.items.containerMenuClickCancel(ctx, true)}")
    }

    private fun clickRefuse(ctx: Ctx) {
        org.basaltmc.server.log.logInfo("uncancel ${org.basaltmc.server.items.containerMenuClickCancel(ctx, false)}")
        org.basaltmc.server.items.containerMenuClickResult(ctx, byteArrayOf(0))
    }

    private fun menu(ctx: Ctx) {
        val player = sourcePlayer(ctx)
        if (menu == 0) {
            val spec = ValueWriter(128).beginMap().key("title").string("<gold>Items menu").key("rows").int(1).end().finish()
            menu = org.basaltmc.server.items.containerMenuCreate(spec)
        }
        say(ctx, "menu", if (menu > 0) 1 else 0)
        say(ctx, "slot 2", org.basaltmc.server.items.menuSlot(menu, 2, "minecraft:diamond", 1, "<aqua>Click me", ::clickDiamond))
        say(ctx, "slot 4", org.basaltmc.server.items.menuSlot(menu, 4, "minecraft:barrier", 1, "<red>Refuse", ::clickRefuse))
        say(ctx, "slot 99", org.basaltmc.server.items.menuSlot(menu, 99, "minecraft:diamond", 1, null, null))
        say(ctx, "open", org.basaltmc.server.items.containerMenuOpen(menu, player))
    }

    private fun closeMenu(ctx: Ctx) {
        say(ctx, "close", org.basaltmc.server.items.containerMenuClose(menu, sourcePlayer(ctx)))
        say(ctx, "close again", org.basaltmc.server.items.containerMenuClose(menu, sourcePlayer(ctx)))
    }

    private fun window(ctx: Ctx) {
        val player = sourcePlayer(ctx)
        val spec = ValueWriter(512).beginMap().key("kind").string("hopper").key("title").string("<green>Sorter")
            .key("items").beginList().beginMap().key("id").string("minecraft:coal").key("count").int(7).end().end().end()
            .finish()
        say(ctx, "container", org.basaltmc.server.items.windowOpen(player, spec))
        say(ctx, "info bytes", org.basaltmc.server.items.windowInfo(player, DOCUMENT))
        val update = ValueWriter(128).beginMap().key("title").string("<red>Sorted").end().finish()
        say(ctx, "update", org.basaltmc.server.items.windowUpdate(player, update))
        val inventory = Inventory.ofWindow(player)
        say(ctx, "window size", inventory.size())
        say(ctx, "coal", inventory.count("minecraft:coal"))
        say(ctx, "close", org.basaltmc.server.items.windowClose(player))
        say(ctx, "close again", org.basaltmc.server.items.windowClose(player))
        inventory.release()
    }

    private fun merchant(ctx: Ctx) {
        val spec = ValueWriter(1024).beginMap().key("title").string("<gold>Trader").key("offers").beginList()
            .beginMap().key("buy").beginMap().key("id").string("minecraft:emerald").key("count").int(2).end()
            .key("sell").beginMap().key("id").string("minecraft:bread").key("count").int(6).end()
            .key("max_uses").int(8).key("xp").int(3).key("price_multiplier").float(0.05).end().end().end().finish()
        say(ctx, "merchant", org.basaltmc.server.items.merchantOpen(sourcePlayer(ctx), spec))
        val bad = ValueWriter(64).beginMap().end().finish()
        say(ctx, "bad merchant", org.basaltmc.server.items.merchantOpen(sourcePlayer(ctx), bad))
    }

    private fun loot(ctx: Ctx) {
        say(ctx, "tables bytes", org.basaltmc.server.items.lootTables(DOCUMENT))
        val roll = ValueWriter(128).beginMap().key("seed").int(3).key("luck").float(1.5).end().finish()
        say(ctx, "zombie bytes", org.basaltmc.server.items.lootRoll("minecraft:entities/zombie", roll, DOCUMENT))
        say(ctx, "no table", org.basaltmc.server.items.lootRoll("minecraft:nothing", null, DOCUMENT))
        say(ctx, "json bytes", org.basaltmc.server.items.lootRollJson("{\"type\":\"minecraft:chest\",\"pools\":[]}", null, DOCUMENT))
        say(ctx, "bad json", org.basaltmc.server.items.lootRollJson("nope", null, DOCUMENT))
        say(ctx, "number bytes", org.basaltmc.server.items.lootNumber("7", true, 0, DOCUMENT))
        val apply = ValueWriter(512).beginMap().key("functions").string("{\"function\":\"minecraft:set_count\"}")
            .key("item").beginMap().key("id").string("minecraft:bread").key("count").int(2).end().end().finish()
        say(ctx, "apply bytes", org.basaltmc.server.items.lootApply(apply, DOCUMENT))
        val enchant = ValueWriter(512).beginMap().key("item").beginMap().key("id").string("minecraft:diamond_sword")
            .end().key("levels").int(30).end().finish()
        say(ctx, "enchant bytes", org.basaltmc.server.items.lootEnchant(enchant, DOCUMENT))
        say(ctx, "trades bytes", org.basaltmc.server.items.tradesRoll("minecraft:farmer", 2, null, DOCUMENT))
        say(ctx, "no profession", org.basaltmc.server.items.tradesRoll("minecraft:nothing", 2, null, DOCUMENT))
    }

    fun register() {
        org.basaltmc.server.commands.command("items give <id: word> [count: int 1..64]", "", ::give)
        org.basaltmc.server.commands.command("items take <id: word> [count: int 1..64]", "", ::take)
        org.basaltmc.server.commands.command("items count <id: word>", "", ::count)
        org.basaltmc.server.commands.command("items slot <slot: int 0..40>", "", ::slot)
        org.basaltmc.server.commands.command("items hand", "", ::hand)
        org.basaltmc.server.commands.command("items chest", "", ::chest)
        org.basaltmc.server.commands.command("items ender", "", ::ender)
        org.basaltmc.server.commands.command("items cooldown <id: word> <ticks: int 0..1000>", "", ::cooldown)
        org.basaltmc.server.commands.command("items registry <kind: word> <key: word>", "", ::registry)
        org.basaltmc.server.commands.command("items level <level: int 1..10>", "", ::level)
        org.basaltmc.server.commands.command("items recipes", "", ::recipes)
        org.basaltmc.server.commands.command("items book", "", ::book)
        org.basaltmc.server.commands.command("items craft", "", ::craft)
        org.basaltmc.server.commands.command("items menu", "", ::menu)
        org.basaltmc.server.commands.command("items closemenu", "", ::closeMenu)
        org.basaltmc.server.commands.command("items window", "", ::window)
        org.basaltmc.server.commands.command("items merchant", "", ::merchant)
        org.basaltmc.server.commands.command("items loot", "", ::loot)
    }
}
