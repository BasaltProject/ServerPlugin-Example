#include "area_events.hpp"
#include "commands.hpp"
#include "entities.hpp"
#include "events.hpp"
#include "server.hpp"
#include "hud.hpp"
#include "packets.hpp"
#include "items.hpp"
#include "shop.hpp"
#include "text.hpp"
#include "worlds.hpp"

namespace {

void ready(basalt::Ctx) {
    const Buffer line = Buffer()
                            .add("ready: ")
                            .add(basalt::config_i64("payout-amount", 5))
                            .add(" coins every ")
                            .add(basalt::config_i64("payout-period", 1200))
                            .add(" ticks");
    basalt::log_info(line.c_str());
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
    basalt::on("plugin.enable", basalt::kNormal, ready);
    packets::register_all();
}
