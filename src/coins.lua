-- A balance per player, kept in players.db under this plugin's namespace.

local M = {}

function M.get(player)
    return player.data.coins or 0
end

function M.add(player, amount)
    local balance = M.get(player) + amount
    player.data.coins = balance
    return balance
end

-- Answers whether it could take `amount`; the balance only moves on success.
function M.take(player, amount)
    local balance = M.get(player)
    if balance < amount then
        return false
    end
    player.data.coins = balance - amount
    return true
end

return M
