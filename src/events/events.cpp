#include "events/events.hpp"

#include "coins/coins.hpp"
#include "common/text.hpp"

namespace events {

namespace {

void on_join(basalt::server::core::Ctx ctx) {
    const basalt::server::core::Player player = basalt::server::events::event_player(ctx);
    if (player == 0) {
        return;
    }
    const auto name = basalt::server::players::player_name(player);
    basalt::server::core::Args args;
    if (!basalt::server::data::data_get_bool(player, "welcomed", false)) {
        basalt::server::data::data_set_bool(player, "welcomed", true);
        const long long bonus = basalt::server::config::config_i64("welcome-bonus", 50);
        coins::add(player, bonus);
        args.add("name", name.c_str()).add("amount", Buffer().add(bonus).c_str());
        basalt::server::players::send(player, "@welcome", &args);
    } else {
        args.add("name", name.c_str()).add("amount", Buffer().add(coins::get(player)).c_str());
        basalt::server::players::send(player, "@welcome-back", &args);
    }
}

void payout(basalt::server::core::Ctx) {
    const long long amount = basalt::server::config::config_i64("payout-amount", 5);
    basalt::server::core::Player online[64];
    const int count = basalt::server::players::players_online(online, 64);
    for (int i = 0; i < count && i < 64; ++i) {
        const long long balance = coins::add(online[i], amount);
        basalt::server::core::Args args;
        args.add("amount", Buffer().add(amount).c_str()).add("balance", Buffer().add(balance).c_str());
        basalt::server::players::action_bar(online[i], "@payout", &args);
    }
}

void coins_placeholder(basalt::server::core::Ctx ctx) {
    const basalt::server::core::Player player = basalt::server::menus::placeholder_player(ctx);
    basalt::server::menus::placeholder_result(ctx, player == 0 ? "0" : Buffer().add(coins::get(player)).c_str());
}

} // namespace

void register_all() {
    basalt::server::events::on("player.join", basalt::server::core::kNormal, on_join);
    // Delayed by its own period, so a payout does not stack with the welcome bonus.
    const int period = static_cast<int>(basalt::server::config::config_i64("payout-period", 1200));
    basalt::server::tasks::every(period, period, payout);
    basalt::server::menus::placeholder("coins", coins_placeholder);
}

} // namespace events
