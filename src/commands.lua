-- A balance to check, a leaderboard from store.top, and the shop.

local M = {}

function M.register(coins, shop_menu)
    cmd("coins [target: word]", function(src, target)
        if target then
            local player = players.by_name(target)
            if not player then
                src:send("@balance-usage")
                return
            end
            src:send("@balance-other", { name = player.name, amount = coins.get(player) })
            return
        end
        if src.is_console then
            src:send("@balance-usage")
            return
        end
        src:send("@balance-self", { amount = coins.get(players.by_name(src.name)) })
    end)

    cmd("coins top [limit: int 1..25]", function(src, limit)
        local rows = store.top("coins", limit or 10)
        if #rows == 0 then
            src:send("@top-empty")
            return
        end
        src:send("@top-header")
        for i, row in ipairs(rows) do
            src:send("@top-row", { rank = i, name = row.name, amount = row.value })
        end
    end)

    cmd("shop", function(src)
        if src.is_console then
            src:send("@shop-players-only")
            return
        end
        shop_menu:open(players.by_name(src.name))
    end, { permission = "example.shop" })
end

return M
