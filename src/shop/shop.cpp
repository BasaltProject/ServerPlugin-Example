#include "shop/shop.hpp"

#include "coins/coins.hpp"
#include "common/text.hpp"

namespace shop {

namespace {

constexpr int kSparklesSlot = 11;
constexpr int kFireworksSlot = 15;

int g_menu = 0;

bool buy(basalt::server::core::Player player, const char* key, long long price) {
    basalt::server::core::Args args;
    if (coins::take(player, price)) {
        const auto item = basalt::server::text::translate(key);
        args.add("item", item.c_str()).add("price", Buffer().add(price).c_str());
        basalt::server::players::send(player, "@shop-bought", &args);
        return true;
    }
    args.add("price", Buffer().add(price).c_str()).add("amount", Buffer().add(coins::get(player)).c_str());
    basalt::server::players::send(player, "@shop-cant-afford", &args);
    return false;
}

void buy_sparkles(basalt::server::core::Ctx ctx) {
    const basalt::server::core::Player player = basalt::server::menus::menu_click_player(ctx);
    if (buy(player, "shop-item-sparkles", basalt::server::config::config_i64("shop.sparkles-price", 100))) {
        basalt::server::players::particle(player, "minecraft:totem_of_undying", 30, 0.4);
    }
}

void buy_fireworks(basalt::server::core::Ctx ctx) {
    const basalt::server::core::Player player = basalt::server::menus::menu_click_player(ctx);
    if (buy(player, "shop-item-fireworks", basalt::server::config::config_i64("shop.fireworks-price", 150))) {
        basalt::server::players::sound(player, "minecraft:entity.firework_rocket.launch", 1.0F, 1.0F);
    }
}

void add_item(int slot, const char* item, const char* name_key, const char* lore_key, long long price,
              basalt::server::core::Handler on_click) {
    const auto name = basalt::server::text::translate(name_key);
    basalt::server::core::Args args;
    args.add("price", Buffer().add(price).c_str());
    const auto lore = basalt::server::text::translate(lore_key, &args);
    basalt::server::menus::menu_set(g_menu, slot, item, name.c_str(), lore.c_str(), on_click);
}

} // namespace

void build() {
    const auto title = basalt::server::text::translate("shop-title");
    g_menu = basalt::server::menus::menu_create(title.c_str(), 3);
    add_item(kSparklesSlot, "minecraft:blaze_powder", "shop-item-sparkles", "shop-item-sparkles-lore",
             basalt::server::config::config_i64("shop.sparkles-price", 100), buy_sparkles);
    add_item(kFireworksSlot, "minecraft:firework_rocket", "shop-item-fireworks", "shop-item-fireworks-lore",
             basalt::server::config::config_i64("shop.fireworks-price", 150), buy_fireworks);
}

int menu() { return g_menu; }

} // namespace shop
