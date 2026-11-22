mod area_events;
mod coins;
mod commands;
mod entities;
mod events;
mod server;
mod hud;
mod packets;
mod items;
mod shop;
mod worlds;

fn ready(_: basalt_server::core::Ctx) {
    let amount = basalt_server::config::i64("payout-amount", 5);
    let period = basalt_server::config::i64("payout-period", 1200);
    basalt_server::log::info(&format!("ready: {amount} coins every {period} ticks"));
}

pub fn init() {
    shop::build();
    events::register();
    commands::register();
    area_events::register();
    server::register();
    worlds::register();
    hud::register();
    items::register();
    entities::register();
    basalt_server::events::on("plugin.enable", basalt_server::core::NORMAL, ready);
    packets::register();
}
