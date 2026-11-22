#pragma once

#include <basalt/server.hpp>

namespace coins {

long long get(basalt::server::core::Player player);
long long add(basalt::server::core::Player player, long long amount);
bool take(basalt::server::core::Player player, long long amount);

} // namespace coins
