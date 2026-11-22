#include "coins.hpp"

namespace coins {

long long get(basalt::Player player) { return basalt::data_get_i64(player, "coins", 0); }

long long add(basalt::Player player, long long amount) {
    const long long balance = get(player) + amount;
    basalt::data_set_i64(player, "coins", balance);
    return balance;
}

bool take(basalt::Player player, long long amount) {
    const long long balance = get(player);
    if (balance < amount) {
        return false;
    }
    basalt::data_set_i64(player, "coins", balance - amount);
    return true;
}

} // namespace coins
