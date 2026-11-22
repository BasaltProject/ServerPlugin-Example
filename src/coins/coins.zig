const basalt = @import("basalt_server");

pub fn get(player: basalt.core.Player) i64 {
    return basalt.data.getI64(player, "coins", 0);
}

pub fn add(player: basalt.core.Player, amount: i64) i64 {
    const balance = get(player) + amount;
    _ = basalt.data.setI64(player, "coins", balance);
    return balance;
}

pub fn take(player: basalt.core.Player, amount: i64) bool {
    const balance = get(player);
    if (balance < amount) return false;
    _ = basalt.data.setI64(player, "coins", balance - amount);
    return true;
}
