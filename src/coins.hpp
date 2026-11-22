#pragma once

#include <basalt.hpp>

namespace coins {

long long get(basalt::Player player);
long long add(basalt::Player player, long long amount);
bool take(basalt::Player player, long long amount);

} // namespace coins
