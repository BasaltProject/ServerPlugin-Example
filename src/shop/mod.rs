use super::coins;
use basalt_server::core::{Args, Ctx, Handler, Player};
use std::sync::atomic::{AtomicI32, Ordering};

const SPARKLES_SLOT: i32 = 11;
const FIREWORKS_SLOT: i32 = 15;

static MENU: AtomicI32 = AtomicI32::new(0);

pub fn menu() -> i32 {
    MENU.load(Ordering::Relaxed)
}

fn buy(player: Player, key: &str, price: i64) -> bool {
    if coins::take(player, price) {
        let item = basalt_server::text::translate(key, &Args::new()).unwrap_or_default();
        let args = Args::new().add("item", &item).add("price", &price.to_string());
        basalt_server::players::send(player, "@shop-bought", &args);
        return true;
    }
    let args = Args::new().add("price", &price.to_string()).add("amount", &coins::get(player).to_string());
    basalt_server::players::send(player, "@shop-cant-afford", &args);
    false
}

fn buy_sparkles(ctx: Ctx) {
    let player = basalt_server::menus::menu_click_player(ctx);
    if buy(player, "shop-item-sparkles", basalt_server::config::i64("shop.sparkles-price", 100)) {
        basalt_server::players::particle(player, "minecraft:totem_of_undying", 30, 0.4);
    }
}

fn buy_fireworks(ctx: Ctx) {
    let player = basalt_server::menus::menu_click_player(ctx);
    if buy(player, "shop-item-fireworks", basalt_server::config::i64("shop.fireworks-price", 150)) {
        basalt_server::players::sound(player, "minecraft:entity.firework_rocket.launch", 1.0, 1.0);
    }
}

fn add_item(slot: i32, item: &str, name_key: &str, lore_key: &str, price: i64, on_click: fn(Ctx)) {
    let name = basalt_server::text::translate(name_key, &Args::new()).unwrap_or_default();
    let lore = basalt_server::text::translate(lore_key, &Args::new().add("price", &price.to_string())).unwrap_or_default();
    let on_click: Handler = Box::new(on_click);
    basalt_server::menus::menu_set(menu(), slot, item, &name, &lore, on_click);
}

pub fn build() {
    let title = basalt_server::text::translate("shop-title", &Args::new()).unwrap_or_default();
    MENU.store(basalt_server::menus::menu_create(&title, 3), Ordering::Relaxed);
    let sparkles = basalt_server::config::i64("shop.sparkles-price", 100);
    add_item(SPARKLES_SLOT, "minecraft:blaze_powder", "shop-item-sparkles", "shop-item-sparkles-lore", sparkles, buy_sparkles);
    let fireworks = basalt_server::config::i64("shop.fireworks-price", 150);
    add_item(FIREWORKS_SLOT, "minecraft:firework_rocket", "shop-item-fireworks", "shop-item-fireworks-lore", fireworks, buy_fireworks);
}
