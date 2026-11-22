#include "commands/commands.hpp"
#include "common/text.hpp"
#include "entities/entities.hpp"
#include "events/area_events.hpp"
#include "events/events.hpp"
#include "hud/hud.hpp"
#include "items/items.hpp"
#include "packets/packets.hpp"
#include "server/server.hpp"
#include "shop/shop.hpp"
#include "worlds/worlds.hpp"

namespace {

void ready(basalt::server::core::Ctx) {
    const Buffer line = Buffer()
                            .add("ready: ")
                            .add(basalt::server::config::config_i64("payout-amount", 5))
                            .add(" coins every ")
                            .add(basalt::server::config::config_i64("payout-period", 1200))
                            .add(" ticks");
    basalt::server::log::log_info(line.c_str());
}

} // namespace

void plugin_init() {
    shop::build();
    events::register_all();
    commands::register_all();
    area_events::register_all();
    server_area::register_all();
    worlds::register_all();
    hud::register_all();
    items::register_all();
    entities::register_all();
    basalt::server::events::on("plugin.enable", basalt::server::core::kNormal, ready);
    packets::register_all();
}
