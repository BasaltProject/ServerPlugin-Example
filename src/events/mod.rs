use super::coins;
use basalt_server::core::{Args, Ctx};

fn on_join(ctx: Ctx) {
    let player = basalt_server::events::player(ctx);
    if player == 0 {
        return;
    }
    let name = basalt_server::players::name(player).unwrap_or_default();
    if !basalt_server::data::get_bool(player, "welcomed", false) {
        basalt_server::data::set_bool(player, "welcomed", true);
        let bonus = basalt_server::config::i64("welcome-bonus", 50);
        coins::add(player, bonus);
        let args = Args::new().add("name", &name).add("amount", &bonus.to_string());
        basalt_server::players::send(player, "@welcome", &args);
    } else {
        let args = Args::new().add("name", &name).add("amount", &coins::get(player).to_string());
        basalt_server::players::send(player, "@welcome-back", &args);
    }
}

fn payout(_: Ctx) {
    let amount = basalt_server::config::i64("payout-amount", 5);
    for player in basalt_server::players::online() {
        let balance = coins::add(player, amount);
        let args = Args::new().add("amount", &amount.to_string()).add("balance", &balance.to_string());
        basalt_server::players::action_bar(player, "@payout", &args);
    }
}

fn coins_placeholder(ctx: Ctx) {
    let player = basalt_server::menus::placeholder_player(ctx);
    let text = if player == 0 { "0".to_string() } else { coins::get(player).to_string() };
    basalt_server::menus::placeholder_result(ctx, &text);
}

pub fn register() {
    basalt_server::events::on("player.join", basalt_server::core::NORMAL, on_join);
    // Delayed by its own period, so a payout does not stack with the welcome bonus.
    let period = basalt_server::config::i64("payout-period", 1200) as i32;
    basalt_server::tasks::every(period, period, payout);
    basalt_server::menus::placeholder("coins", coins_placeholder);
}
