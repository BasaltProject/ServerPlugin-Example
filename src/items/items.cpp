#include "items/items.hpp"

#include "common/text.hpp"

namespace items {

namespace {

constexpr int kDocument = 2048;

int g_menu = 0;

basalt::server::core::Player source_player(basalt::server::core::Ctx ctx) {
    const auto source = basalt::server::commands::command_source_name(ctx);
    return basalt::server::players::player_by_name(source.c_str());
}

void say(basalt::server::core::Ctx ctx, const char* label, long long value) {
    basalt::server::core::Args args;
    args.add("text", Buffer().add(label).add(" ").add(value).c_str());
    basalt::server::commands::command_reply(ctx, "@items-line", &args);
}

void say_text(basalt::server::core::Ctx ctx, const char* text) {
    basalt::server::core::Args args;
    args.add("text", text);
    basalt::server::commands::command_reply(ctx, "@items-line", &args);
}

long long count_arg(basalt::server::core::Ctx ctx) {
    long long count = 1;
    basalt::server::commands::command_arg_i64(ctx, "count", &count);
    return count;
}

void give(basalt::server::core::Ctx ctx) {
    const auto id = basalt::server::commands::command_arg_str(ctx, "id");
    const auto inventory = basalt::server::items::Inventory::of_player(source_player(ctx));
    say(ctx, "left over", inventory.give(id.c_str(), static_cast<int>(count_arg(ctx)), "<gold>Gift"));
}

void take(basalt::server::core::Ctx ctx) {
    const auto id = basalt::server::commands::command_arg_str(ctx, "id");
    const auto inventory = basalt::server::items::Inventory::of_player(source_player(ctx));
    say(ctx, "taken", inventory.take(id.c_str(), static_cast<int>(count_arg(ctx))));
}

void count(basalt::server::core::Ctx ctx) {
    const auto id = basalt::server::commands::command_arg_str(ctx, "id");
    const auto inventory = basalt::server::items::Inventory::of_player(source_player(ctx));
    say(ctx, "count", inventory.count(id.c_str()));
    say(ctx, "size", inventory.size());
}

void slot(basalt::server::core::Ctx ctx) {
    long long index = 0;
    basalt::server::commands::command_arg_i64(ctx, "slot", &index);
    const auto inventory = basalt::server::items::Inventory::of_player(source_player(ctx));
    say(ctx, "in slot", inventory.slot_count(static_cast<int>(index)));
    say(ctx, "bad slot", inventory.slot_count(99));
}

void hand(basalt::server::core::Ctx ctx) {
    const auto inventory = basalt::server::items::Inventory::of_player(source_player(ctx));
    say(ctx, "held", inventory.held_slot());
    say(ctx, "set held", inventory.set_held_slot(4));
    say(ctx, "held", inventory.held_slot());
    char contents[kDocument];
    say(ctx, "contents bytes", inventory.contents(contents, sizeof contents));
}

void chest(basalt::server::core::Ctx ctx) {
    const auto block = basalt::server::items::Inventory::of_block("world", 10, 64, 10);
    say(ctx, "chest", block.size());
    say(ctx, "bread", block.count("minecraft:bread"));
    say(ctx, "left over", block.give("minecraft:apple", 3));
    say(ctx, "apples", block.count("minecraft:apple"));
    say(ctx, "taken", block.take("minecraft:bread", 2));
    say(ctx, "no block", basalt::server::items::Inventory::of_block("world", 0, 0, 0).handle());
    say(ctx, "hopper", basalt::server::items::Inventory::of_block("world", 11, 64, 10).size());
}

void ender(basalt::server::core::Ctx ctx) {
    const auto inventory = basalt::server::items::Inventory::of_ender(source_player(ctx));
    say(ctx, "put", inventory.put(0, "minecraft:diamond", 5));
    say(ctx, "diamonds", inventory.count("minecraft:diamond"));
    say(ctx, "size", inventory.size());
}

void cooldown(basalt::server::core::Ctx ctx) {
    const auto id = basalt::server::commands::command_arg_str(ctx, "id");
    long long ticks = 0;
    basalt::server::commands::command_arg_i64(ctx, "ticks", &ticks);
    const basalt::server::core::Player player = source_player(ctx);
    say(ctx, "set", basalt::server::items::item_set_cooldown(player, id.c_str(), static_cast<int>(ticks)));
    say(ctx, "left", basalt::server::items::item_cooldown(player, id.c_str()));
}

void registry(basalt::server::core::Ctx ctx) {
    const auto kind = basalt::server::commands::command_arg_str(ctx, "kind");
    const auto key = basalt::server::commands::command_arg_str(ctx, "key");
    char out[kDocument];
    say(ctx, "bytes", bs_registry(kind.c_str(), key.c_str(), out, sizeof out));
}

void level(basalt::server::core::Ctx ctx) {
    long long value = 1;
    basalt::server::commands::command_arg_i64(ctx, "level", &value);
    basalt::server::values::ValueBuffer<128> spec;
    spec.map().key("type").string("linear").key("base").number(1.0).key("per_level_above_first").number(0.5).end();
    const double number = basalt_enchantment_level_value(spec.data(), spec.finish(), static_cast<int>(value));
    say(ctx, "value x100", static_cast<long long>(number * 100.0));
}

void recipes(basalt::server::core::Ctx ctx) {
    const char* json = "{\"type\":\"minecraft:crafting_shapeless\",\"ingredients\":[\"minecraft:dirt\"],"
                       "\"result\":{\"id\":\"minecraft:diamond\",\"count\":2}}";
    say(ctx, "added", basalt_recipe_add("example:dirt_gems", 17, json, bs_text_len(json)));
    say(ctx, "again", basalt_recipe_add("example:dirt_gems", 17, json, bs_text_len(json)));
    say(ctx, "has", basalt::server::items::recipe_known("example:dirt_gems") ? 1 : 0);
    char out[kDocument];
    say(ctx, "ids bytes", basalt_recipe_ids(out, sizeof out));
    say(ctx, "get bytes", basalt_recipe_get("example:dirt_gems", 17, out, sizeof out));
    say(ctx, "removed", basalt_recipe_remove("example:dirt_gems", 17));
    say(ctx, "removed again", basalt_recipe_remove("example:dirt_gems", 17));
}

void book(basalt::server::core::Ctx ctx) {
    const basalt::server::core::Player player = source_player(ctx);
    basalt::server::values::ValueBuffer<128> ids;
    ids.list().string("minecraft:oak_planks").string("minecraft:stick").end();
    say(ctx, "learned", basalt_recipe_book_set(player, ids.data(), ids.finish(), 1));
    say(ctx, "knows", basalt_recipe_book_has(player, "minecraft:stick", 15));
    char out[kDocument];
    say(ctx, "book bytes", basalt_recipe_book_get(player, out, sizeof out));
    say(ctx, "forgot", basalt_recipe_book_set(player, ids.data(), ids.finish(), 0));
    say(ctx, "knows", basalt_recipe_book_has(player, "minecraft:stick", 15));
    say(ctx, "resent", basalt_recipe_book_resend(player));
}

void craft(basalt::server::core::Ctx ctx) {
    basalt::server::values::ValueBuffer<512> input;
    input.map().key("width").integer(1).key("height").integer(1).key("items").list();
    input.map().key("id").string("minecraft:oak_log").key("count").integer(1).end();
    input.end().end();
    char out[kDocument];
    char spare[kDocument];
    char* result = nullptr;
    basalt::server::values::Value answer;
    const int status = basalt::server::values::call_value(
        [&](char* buffer, int cap) {
            return basalt_recipe_station("crafting", 8, input.data(), input.finish(), buffer, cap);
        },
        out, sizeof out, spare, sizeof spare, answer);
    say(ctx, "status", status);
    say(ctx, "result count", answer.find("result").find("count").i64_or(-1));
    say(ctx, "unknown", basalt_recipe_station("grinder", 7, input.data(), input.finish(), out, sizeof out));
}

void click_diamond(basalt::server::core::Ctx ctx) {
    char info[256];
    const int length = basalt_container_menu_click_info(ctx, info, sizeof info);
    basalt::server::values::Value root;
    if (length > 0 && length <= static_cast<int>(sizeof info) && root.open(info, length)) {
        basalt::server::log::log_info(Buffer().add("clicked slot ").add(root.find("slot").i64_or(-1)).c_str());
    }
    const basalt::server::items::Item replacement("minecraft:emerald", 1);
    basalt_container_menu_click_result(ctx, replacement.data(), replacement.length());
    basalt::server::log::log_info(Buffer().add("cancel ").add(basalt_container_menu_click_cancel(ctx, 1)).c_str());
}

void click_refuse(basalt::server::core::Ctx ctx) {
    basalt::server::log::log_info(Buffer().add("uncancel ").add(basalt_container_menu_click_cancel(ctx, 0)).c_str());
    basalt_container_menu_click_result(ctx, "\x00", 1);
}

void menu(basalt::server::core::Ctx ctx) {
    const basalt::server::core::Player player = source_player(ctx);
    if (g_menu == 0) {
        basalt::server::values::ValueBuffer<128> spec;
        spec.map().key("title").string("<gold>Items menu").key("rows").integer(1).end();
        g_menu = basalt_container_menu_create(spec.data(), spec.finish());
    }
    say(ctx, "menu", g_menu > 0 ? 1 : 0);
    say(ctx, "slot 2",
        basalt::server::items::menu_slot(g_menu, 2, "minecraft:diamond", 1, "<aqua>Click me", click_diamond));
    say(ctx, "slot 4",
        basalt::server::items::menu_slot(g_menu, 4, "minecraft:barrier", 1, "<red>Refuse", click_refuse));
    say(ctx, "slot 99", basalt::server::items::menu_slot(g_menu, 99, "minecraft:diamond", 1, nullptr, nullptr));
    say(ctx, "open", basalt_container_menu_open(g_menu, player));
}

void close_menu(basalt::server::core::Ctx ctx) {
    say(ctx, "close", basalt_container_menu_close(g_menu, source_player(ctx)));
    say(ctx, "close again", basalt_container_menu_close(g_menu, source_player(ctx)));
}

void window(basalt::server::core::Ctx ctx) {
    const basalt::server::core::Player player = source_player(ctx);
    basalt::server::values::ValueBuffer<512> spec;
    spec.map().key("kind").string("hopper").key("title").string("<green>Sorter").key("items").list();
    spec.map().key("id").string("minecraft:coal").key("count").integer(7).end();
    spec.end().end();
    say(ctx, "container", basalt_window_open(player, spec.data(), spec.finish()));
    char out[kDocument];
    say(ctx, "info bytes", basalt_window_info(player, out, sizeof out));
    basalt::server::values::ValueBuffer<128> update;
    update.map().key("title").string("<red>Sorted").end();
    say(ctx, "update", basalt_window_update(player, update.data(), update.finish()));
    const auto inventory = basalt::server::items::Inventory::of_window(player);
    say(ctx, "window size", inventory.size());
    say(ctx, "coal", inventory.count("minecraft:coal"));
    say(ctx, "close", basalt_window_close(player));
    say(ctx, "close again", basalt_window_close(player));
}

void merchant(basalt::server::core::Ctx ctx) {
    basalt::server::values::ValueBuffer<1024> spec;
    spec.map().key("title").string("<gold>Trader").key("offers").list();
    spec.map().key("buy").map().key("id").string("minecraft:emerald").key("count").integer(2).end();
    spec.key("sell").map().key("id").string("minecraft:bread").key("count").integer(6).end();
    spec.key("max_uses").integer(8).key("xp").integer(3).key("price_multiplier").number(0.05).end();
    spec.end().end();
    say(ctx, "merchant", basalt_merchant_open(source_player(ctx), spec.data(), spec.finish()));
    basalt::server::values::ValueBuffer<64> bad;
    bad.map().end();
    say(ctx, "bad merchant", basalt_merchant_open(source_player(ctx), bad.data(), bad.finish()));
}

void loot(basalt::server::core::Ctx ctx) {
    char out[kDocument];
    say(ctx, "tables bytes", basalt_loot_tables(out, sizeof out));
    basalt::server::values::ValueBuffer<128> roll;
    roll.map().key("seed").integer(3).key("luck").number(1.5).end();
    say(ctx, "zombie bytes",
        basalt_loot_roll("minecraft:entities/zombie", 25, roll.data(), roll.finish(), out, sizeof out));
    say(ctx, "no table", basalt_loot_roll("minecraft:nothing", 17, nullptr, 0, out, sizeof out));
    const char* table = "{\"type\":\"minecraft:chest\",\"pools\":[]}";
    say(ctx, "json bytes", basalt_loot_roll_json(table, bs_text_len(table), nullptr, 0, out, sizeof out));
    say(ctx, "bad json", basalt_loot_roll_json("nope", 4, nullptr, 0, out, sizeof out));
    say(ctx, "number bytes", basalt_loot_number("7", 1, 1, 0, out, sizeof out));
    basalt::server::values::ValueBuffer<512> apply;
    apply.map().key("functions").string("{\"function\":\"minecraft:set_count\"}").key("item").map();
    apply.key("id").string("minecraft:bread").key("count").integer(2).end().end();
    say(ctx, "apply bytes", basalt_loot_apply(apply.data(), apply.finish(), out, sizeof out));
    basalt::server::values::ValueBuffer<512> enchant;
    enchant.map().key("item").map().key("id").string("minecraft:diamond_sword").end().key("levels").integer(30).end();
    say(ctx, "enchant bytes", basalt_loot_enchant(enchant.data(), enchant.finish(), out, sizeof out));
    say(ctx, "trades bytes", basalt_trades_roll("minecraft:farmer", 16, 2, nullptr, 0, out, sizeof out));
    say(ctx, "no profession", basalt_trades_roll("minecraft:nothing", 17, 2, nullptr, 0, out, sizeof out));
}

} // namespace

void register_all() {
    basalt::server::commands::command("items give <id: word> [count: int 1..64]", "", give);
    basalt::server::commands::command("items take <id: word> [count: int 1..64]", "", take);
    basalt::server::commands::command("items count <id: word>", "", count);
    basalt::server::commands::command("items slot <slot: int 0..40>", "", slot);
    basalt::server::commands::command("items hand", "", hand);
    basalt::server::commands::command("items chest", "", chest);
    basalt::server::commands::command("items ender", "", ender);
    basalt::server::commands::command("items cooldown <id: word> <ticks: int 0..1000>", "", cooldown);
    basalt::server::commands::command("items registry <kind: word> <key: word>", "", registry);
    basalt::server::commands::command("items level <level: int 1..10>", "", level);
    basalt::server::commands::command("items recipes", "", recipes);
    basalt::server::commands::command("items book", "", book);
    basalt::server::commands::command("items craft", "", craft);
    basalt::server::commands::command("items menu", "", menu);
    basalt::server::commands::command("items closemenu", "", close_menu);
    basalt::server::commands::command("items window", "", window);
    basalt::server::commands::command("items merchant", "", merchant);
    basalt::server::commands::command("items loot", "", loot);
}

} // namespace items
