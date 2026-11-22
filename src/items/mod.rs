use basalt_server::items::{self, Inventory, Item};
use basalt_server::items::raw as raw;
use basalt_server::values::{Value, Writer};
use basalt_server::core::{Args, Ctx, Player};

const DOCUMENT: usize = 2048;

thread_local! {
    static MENU: std::cell::Cell<i32> = const { std::cell::Cell::new(0) };
}

fn source_player(ctx: Ctx) -> Player {
    basalt_server::players::by_name(&basalt_server::commands::source_name(ctx).unwrap_or_default())
}

fn say(ctx: Ctx, label: &str, value: i64) {
    basalt_server::commands::reply(ctx, "@items-line", &Args::new().add("text", &format!("{label} {value}")));
}

fn count_arg(ctx: Ctx) -> i32 {
    basalt_server::commands::arg_i64(ctx, "count").unwrap_or(1) as i32
}

fn finished(writer: &Writer) -> Vec<u8> {
    writer.finish().map(|bytes| bytes.to_vec()).unwrap_or_default()
}

fn len(bytes: &[u8]) -> i32 {
    bytes.len() as i32
}

fn give(ctx: Ctx) {
    let id = basalt_server::commands::arg_str(ctx, "id").unwrap_or_default();
    let inventory = Inventory::of_player(source_player(ctx));
    say(ctx, "left over", inventory.give(&id, count_arg(ctx), Some("<gold>Gift")) as i64);
}

fn take(ctx: Ctx) {
    let id = basalt_server::commands::arg_str(ctx, "id").unwrap_or_default();
    let inventory = Inventory::of_player(source_player(ctx));
    say(ctx, "taken", inventory.take(&id, count_arg(ctx)) as i64);
}

fn count(ctx: Ctx) {
    let id = basalt_server::commands::arg_str(ctx, "id").unwrap_or_default();
    let inventory = Inventory::of_player(source_player(ctx));
    say(ctx, "count", inventory.count(&id) as i64);
    say(ctx, "size", inventory.size() as i64);
}

fn slot(ctx: Ctx) {
    let index = basalt_server::commands::arg_i64(ctx, "slot").unwrap_or(0) as i32;
    let inventory = Inventory::of_player(source_player(ctx));
    say(ctx, "in slot", inventory.slot_count(index) as i64);
    say(ctx, "bad slot", inventory.slot_count(99) as i64);
}

fn hand(ctx: Ctx) {
    let inventory = Inventory::of_player(source_player(ctx));
    say(ctx, "held", inventory.held_slot() as i64);
    say(ctx, "set held", inventory.set_held_slot(4) as i64);
    say(ctx, "held", inventory.held_slot() as i64);
    let mut contents = [0u8; DOCUMENT];
    say(ctx, "contents bytes", inventory.contents(&mut contents) as i64);
}

fn chest(ctx: Ctx) {
    let block = Inventory::of_block("world", 10, 64, 10);
    say(ctx, "chest", block.size() as i64);
    say(ctx, "bread", block.count("minecraft:bread") as i64);
    say(ctx, "left over", block.give("minecraft:apple", 3, None) as i64);
    say(ctx, "apples", block.count("minecraft:apple") as i64);
    say(ctx, "taken", block.take("minecraft:bread", 2) as i64);
    say(ctx, "no block", Inventory::of_block("world", 0, 0, 0).handle() as i64);
    say(ctx, "hopper", Inventory::of_block("world", 11, 64, 10).size() as i64);
}

fn ender(ctx: Ctx) {
    let inventory = Inventory::of_ender(source_player(ctx));
    say(ctx, "put", inventory.put(0, "minecraft:diamond", 5) as i64);
    say(ctx, "diamonds", inventory.count("minecraft:diamond") as i64);
    say(ctx, "size", inventory.size() as i64);
}

fn cooldown(ctx: Ctx) {
    let id = basalt_server::commands::arg_str(ctx, "id").unwrap_or_default();
    let ticks = basalt_server::commands::arg_i64(ctx, "ticks").unwrap_or(0) as i32;
    let player = source_player(ctx);
    say(ctx, "set", items::item_set_cooldown(player, &id, ticks) as i64);
    say(ctx, "left", items::item_cooldown(player, &id) as i64);
}

fn registry(ctx: Ctx) {
    let kind = basalt_server::commands::arg_str(ctx, "kind").unwrap_or_default();
    let key = basalt_server::commands::arg_str(ctx, "key").unwrap_or_default();
    let mut out = [0u8; DOCUMENT];
    say(ctx, "bytes", items::registry_get(&kind, &key, &mut out) as i64);
}

fn level(ctx: Ctx) {
    let value = basalt_server::commands::arg_i64(ctx, "level").unwrap_or(1) as i32;
    let mut spec = Writer::with_limit(128);
    spec.begin_map().key("type").string("linear").key("base").float(1.0).key("per_level_above_first").float(0.5).end();
    let bytes = finished(&spec);
    let number = unsafe { raw::enchantment_level_value(bytes.as_ptr(), len(&bytes), value) };
    say(ctx, "value x100", (number * 100.0) as i64);
}

fn recipes(ctx: Ctx) {
    let json = "{\"type\":\"minecraft:crafting_shapeless\",\"ingredients\":[\"minecraft:dirt\"],\"result\":{\"id\":\"minecraft:diamond\",\"count\":2}}";
    let id = "example:dirt_gems";
    let mut out = [0u8; DOCUMENT];
    let cap = DOCUMENT as i32;
    unsafe {
        say(ctx, "added", raw::recipe_add(id.as_ptr(), 17, json.as_ptr(), json.len() as i32) as i64);
        say(ctx, "again", raw::recipe_add(id.as_ptr(), 17, json.as_ptr(), json.len() as i32) as i64);
        say(ctx, "has", items::recipe_known(id) as i64);
        say(ctx, "ids bytes", raw::recipe_ids(out.as_mut_ptr(), cap) as i64);
        say(ctx, "get bytes", raw::recipe_get(id.as_ptr(), 17, out.as_mut_ptr(), cap) as i64);
        say(ctx, "removed", raw::recipe_remove(id.as_ptr(), 17) as i64);
        say(ctx, "removed again", raw::recipe_remove(id.as_ptr(), 17) as i64);
    }
}

fn book(ctx: Ctx) {
    let player = source_player(ctx);
    let mut ids = Writer::with_limit(128);
    ids.begin_list().string("minecraft:oak_planks").string("minecraft:stick").end();
    let bytes = finished(&ids);
    let stick = "minecraft:stick";
    let mut out = [0u8; DOCUMENT];
    unsafe {
        say(ctx, "learned", raw::recipe_book_set(player, bytes.as_ptr(), len(&bytes), 1) as i64);
        say(ctx, "knows", raw::recipe_book_has(player, stick.as_ptr(), 15) as i64);
        say(ctx, "book bytes", raw::recipe_book_get(player, out.as_mut_ptr(), DOCUMENT as i32) as i64);
        say(ctx, "forgot", raw::recipe_book_set(player, bytes.as_ptr(), len(&bytes), 0) as i64);
        say(ctx, "knows", raw::recipe_book_has(player, stick.as_ptr(), 15) as i64);
        say(ctx, "resent", raw::recipe_book_resend(player) as i64);
    }
}

fn craft(ctx: Ctx) {
    let mut input = Writer::with_limit(512);
    input.begin_map().key("width").int(1).key("height").int(1).key("items").begin_list();
    input.begin_map().key("id").string("minecraft:oak_log").key("count").int(1).end();
    input.end().end();
    let bytes = finished(&input);
    let answer = basalt_server::values::call_value(DOCUMENT, |out, cap| unsafe {
        raw::recipe_station("crafting".as_ptr(), 8, bytes.as_ptr(), len(&bytes), out, cap)
    });
    say(ctx, "status", if answer.is_ok() { 0 } else { -3 });
    let result_count = answer
        .as_ref()
        .ok()
        .and_then(|owned| owned.root().find("result").and_then(|result| result.find("count")).and_then(|c| c.as_i64()))
        .unwrap_or(-1);
    say(ctx, "result count", result_count);
    let mut out = [0u8; DOCUMENT];
    let unknown = unsafe {
        raw::recipe_station("grinder".as_ptr(), 7, bytes.as_ptr(), len(&bytes), out.as_mut_ptr(), DOCUMENT as i32)
    };
    say(ctx, "unknown", unknown as i64);
}

fn click_diamond(ctx: Ctx) {
    let mut info = [0u8; 256];
    let length = unsafe { raw::container_menu_click_info(ctx, info.as_mut_ptr(), 256) };
    if length > 0 && length <= 256 {
        if let Some(root) = Value::open(&info[..length as usize]) {
            let slot = root.find("slot").and_then(|found| found.as_i64()).unwrap_or(-1);
            basalt_server::log::info(&format!("clicked slot {slot}"));
        }
    }
    let replacement = Item::new("minecraft:emerald", 1, None);
    unsafe {
        raw::container_menu_click_result(ctx, replacement.bytes().as_ptr(), len(replacement.bytes()));
        basalt_server::log::info(&format!("cancel {}", raw::container_menu_click_cancel(ctx, 1)));
    }
}

fn click_refuse(ctx: Ctx) {
    unsafe {
        basalt_server::log::info(&format!("uncancel {}", raw::container_menu_click_cancel(ctx, 0)));
        raw::container_menu_click_result(ctx, [0u8].as_ptr(), 1);
    }
}

fn menu(ctx: Ctx) {
    let player = source_player(ctx);
    if MENU.get() == 0 {
        let mut spec = Writer::with_limit(128);
        spec.begin_map().key("title").string("<gold>Items menu").key("rows").int(1).end();
        let bytes = finished(&spec);
        MENU.set(unsafe { raw::container_menu_create(bytes.as_ptr(), len(&bytes)) });
    }
    let menu = MENU.get();
    say(ctx, "menu", (menu > 0) as i64);
    say(ctx, "slot 2", items::menu_slot(menu, 2, "minecraft:diamond", 1, Some("<aqua>Click me"), Some(Box::new(click_diamond))) as i64);
    say(ctx, "slot 4", items::menu_slot(menu, 4, "minecraft:barrier", 1, Some("<red>Refuse"), Some(Box::new(click_refuse))) as i64);
    say(ctx, "slot 99", items::menu_slot(menu, 99, "minecraft:diamond", 1, None, None) as i64);
    say(ctx, "open", unsafe { raw::container_menu_open(menu, player) } as i64);
}

fn close_menu(ctx: Ctx) {
    let menu = MENU.get();
    say(ctx, "close", unsafe { raw::container_menu_close(menu, source_player(ctx)) } as i64);
    say(ctx, "close again", unsafe { raw::container_menu_close(menu, source_player(ctx)) } as i64);
}

fn window(ctx: Ctx) {
    let player = source_player(ctx);
    let mut spec = Writer::with_limit(512);
    spec.begin_map().key("kind").string("hopper").key("title").string("<green>Sorter").key("items").begin_list();
    spec.begin_map().key("id").string("minecraft:coal").key("count").int(7).end();
    spec.end().end();
    let bytes = finished(&spec);
    let mut out = [0u8; DOCUMENT];
    let mut update = Writer::with_limit(128);
    update.begin_map().key("title").string("<red>Sorted").end();
    let update_bytes = finished(&update);
    unsafe {
        say(ctx, "container", raw::window_open(player, bytes.as_ptr(), len(&bytes)) as i64);
        say(ctx, "info bytes", raw::window_info(player, out.as_mut_ptr(), DOCUMENT as i32) as i64);
        say(ctx, "update", raw::window_update(player, update_bytes.as_ptr(), len(&update_bytes)) as i64);
    }
    let inventory = Inventory::of_window(player);
    say(ctx, "window size", inventory.size() as i64);
    say(ctx, "coal", inventory.count("minecraft:coal") as i64);
    unsafe {
        say(ctx, "close", raw::window_close(player) as i64);
        say(ctx, "close again", raw::window_close(player) as i64);
    }
}

fn merchant(ctx: Ctx) {
    let mut spec = Writer::with_limit(1024);
    spec.begin_map().key("title").string("<gold>Trader").key("offers").begin_list();
    spec.begin_map().key("buy").begin_map().key("id").string("minecraft:emerald").key("count").int(2).end();
    spec.key("sell").begin_map().key("id").string("minecraft:bread").key("count").int(6).end();
    spec.key("max_uses").int(8).key("xp").int(3).key("price_multiplier").float(0.05).end();
    spec.end().end();
    let bytes = finished(&spec);
    say(ctx, "merchant", unsafe { raw::merchant_open(source_player(ctx), bytes.as_ptr(), len(&bytes)) } as i64);
    let mut bad = Writer::with_limit(64);
    bad.begin_map().end();
    let bad_bytes = finished(&bad);
    say(ctx, "bad merchant", unsafe { raw::merchant_open(source_player(ctx), bad_bytes.as_ptr(), len(&bad_bytes)) } as i64);
}

fn loot(ctx: Ctx) {
    let mut out = [0u8; DOCUMENT];
    let o = out.as_mut_ptr();
    let cap = DOCUMENT as i32;
    let none = std::ptr::null();
    let mut roll = Writer::with_limit(128);
    roll.begin_map().key("seed").int(3).key("luck").float(1.5).end();
    let roll_bytes = finished(&roll);
    let table = "{\"type\":\"minecraft:chest\",\"pools\":[]}";
    let mut apply = Writer::with_limit(512);
    apply.begin_map().key("functions").string("{\"function\":\"minecraft:set_count\"}").key("item").begin_map();
    apply.key("id").string("minecraft:bread").key("count").int(2).end().end();
    let apply_bytes = finished(&apply);
    let mut enchant = Writer::with_limit(512);
    enchant.begin_map().key("item").begin_map().key("id").string("minecraft:diamond_sword").end().key("levels").int(30).end();
    let enchant_bytes = finished(&enchant);
    unsafe {
        say(ctx, "tables bytes", raw::loot_tables(o, cap) as i64);
        say(ctx, "zombie bytes", raw::loot_roll("minecraft:entities/zombie".as_ptr(), 25, roll_bytes.as_ptr(), len(&roll_bytes), o, cap) as i64);
        say(ctx, "no table", raw::loot_roll("minecraft:nothing".as_ptr(), 17, none, 0, o, cap) as i64);
        say(ctx, "json bytes", raw::loot_roll_json(table.as_ptr(), table.len() as i32, none, 0, o, cap) as i64);
        say(ctx, "bad json", raw::loot_roll_json("nope".as_ptr(), 4, none, 0, o, cap) as i64);
        say(ctx, "number bytes", raw::loot_number("7".as_ptr(), 1, 1, 0, o, cap) as i64);
        say(ctx, "apply bytes", raw::loot_apply(apply_bytes.as_ptr(), len(&apply_bytes), o, cap) as i64);
        say(ctx, "enchant bytes", raw::loot_enchant(enchant_bytes.as_ptr(), len(&enchant_bytes), o, cap) as i64);
        say(ctx, "trades bytes", raw::trades_roll("minecraft:farmer".as_ptr(), 16, 2, none, 0, o, cap) as i64);
        say(ctx, "no profession", raw::trades_roll("minecraft:nothing".as_ptr(), 17, 2, none, 0, o, cap) as i64);
    }
}

pub fn register() {
    basalt_server::commands::register("items give <id: word> [count: int 1..64]", "", give);
    basalt_server::commands::register("items take <id: word> [count: int 1..64]", "", take);
    basalt_server::commands::register("items count <id: word>", "", count);
    basalt_server::commands::register("items slot <slot: int 0..40>", "", slot);
    basalt_server::commands::register("items hand", "", hand);
    basalt_server::commands::register("items chest", "", chest);
    basalt_server::commands::register("items ender", "", ender);
    basalt_server::commands::register("items cooldown <id: word> <ticks: int 0..1000>", "", cooldown);
    basalt_server::commands::register("items registry <kind: word> <key: word>", "", registry);
    basalt_server::commands::register("items level <level: int 1..10>", "", level);
    basalt_server::commands::register("items recipes", "", recipes);
    basalt_server::commands::register("items book", "", book);
    basalt_server::commands::register("items craft", "", craft);
    basalt_server::commands::register("items menu", "", menu);
    basalt_server::commands::register("items closemenu", "", close_menu);
    basalt_server::commands::register("items window", "", window);
    basalt_server::commands::register("items merchant", "", merchant);
    basalt_server::commands::register("items loot", "", loot);
}
