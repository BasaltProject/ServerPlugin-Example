#include "events.hpp"

#include "coins.hpp"
#include "text.hpp"

namespace events {

namespace {

void on_join(basalt::Ctx ctx) {
    const basalt::Player player = basalt::event_player(ctx);
    if (player == 0) {
        return;
    }
    const auto name = basalt::player_name(player);
    basalt::Args args;
    if (!basalt::data_get_bool(player, "welcomed", false)) {
        basalt::data_set_bool(player, "welcomed", true);
        const long long bonus = basalt::config_i64("welcome-bonus", 50);
        coins::add(player, bonus);
        args.add("name", name.c_str()).add("amount", Buffer().add(bonus).c_str());
        basalt::send(player, "@welcome", &args);
    } else {
        args.add("name", name.c_str()).add("amount", Buffer().add(coins::get(player)).c_str());
        basalt::send(player, "@welcome-back", &args);
    }
}

void payout(basalt::Ctx) {
    const long long amount = basalt::config_i64("payout-amount", 5);
    basalt::Player online[64];
    const int count = basalt::players_online(online, 64);
    for (int i = 0; i < count && i < 64; ++i) {
        const long long balance = coins::add(online[i], amount);
        basalt::Args args;
        args.add("amount", Buffer().add(amount).c_str()).add("balance", Buffer().add(balance).c_str());
        basalt::action_bar(online[i], "@payout", &args);
    }
}

void coins_placeholder(basalt::Ctx ctx) {
    const basalt::Player player = basalt::placeholder_player(ctx);
    basalt::placeholder_result(ctx, player == 0 ? "0" : Buffer().add(coins::get(player)).c_str());
}

} // namespace

void register_all() {
    basalt::on("player.join", basalt::kNormal, on_join);
    // Delayed by its own period, so a payout does not stack with the welcome bonus.
    const int period = static_cast<int>(basalt::config_i64("payout-period", 1200));
    basalt::every(period, period, payout);
    basalt::placeholder("coins", coins_placeholder);
}

} // namespace events
