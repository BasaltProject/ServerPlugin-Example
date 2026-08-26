-- Join message, recurring payout, and the placeholder that exposes a balance.

local M = {}

function M.register(coins)
    on("player.join", function(e)
        local player = e.player
        if player == nil then
            return
        end
        if not player.data.welcomed then
            player.data.welcomed = true
            coins.add(player, config.welcome_bonus)
            player:send("@welcome", { name = player.name, amount = config.welcome_bonus })
        else
            player:send("@welcome-back", { name = player.name, amount = coins.get(player) })
        end
    end)

    -- Delayed by its own period, so a payout does not stack with the welcome bonus.
    every(config.payout_period, config.payout_period, function()
        for _, player in ipairs(players.online()) do
            local balance = coins.add(player, config.payout_amount)
            player:action_bar("@payout", { amount = config.payout_amount, balance = balance })
        end
    end)

    placeholder("coins", function(p)
        if p == nil then
            return "0"
        end
        return tostring(coins.get(p))
    end)
end

return M
