#include "coins/coins.hpp"

namespace coins {

long long get(basalt::server::core::Player player) { return basalt::server::data::data_get_i64(player, "coins", 0); }

long long add(basalt::server::core::Player player, long long amount) {
    const long long balance = get(player) + amount;
    basalt::server::data::data_set_i64(player, "coins", balance);
    return balance;
}

bool take(basalt::server::core::Player player, long long amount) {
    const long long balance = get(player);
    if (balance < amount) {
        return false;
    }
    basalt::server::data::data_set_i64(player, "coins", balance - amount);
    return true;
}

} // namespace coins
