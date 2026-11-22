use basalt_server::core::Player;

pub fn get(player: Player) -> i64 {
    basalt_server::data::get_i64(player, "coins", 0)
}

pub fn add(player: Player, amount: i64) -> i64 {
    let balance = get(player) + amount;
    basalt_server::data::set_i64(player, "coins", balance);
    balance
}

pub fn take(player: Player, amount: i64) -> bool {
    let balance = get(player);
    if balance < amount {
        return false;
    }
    basalt_server::data::set_i64(player, "coins", balance - amount);
    true
}
