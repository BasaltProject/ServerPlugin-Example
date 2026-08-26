-- Built once, at load: menu() is a window this plugin owns, reused by everyone who opens it.

local coins = require("coins")

local M = {}

local SPARKLES_SLOT = 11
local FIREWORKS_SLOT = 15

local function buy(player, key, price, apply)
    if coins.take(player, price) then
        player:send("@shop-bought", { item = t(key), price = price })
        apply(player)
    else
        player:send("@shop-cant-afford", { price = price, amount = coins.get(player) })
    end
end

function M.build()
    local shop = menu{ title = "@shop-title", rows = 3 }

    shop:set(SPARKLES_SLOT, {
        id = "minecraft:blaze_powder",
        name = t("shop-item-sparkles"),
        lore = { t("shop-item-sparkles-lore", { price = config.shop.sparkles_price }) },
    }, function(e)
        buy(e.player, "shop-item-sparkles", config.shop.sparkles_price, function(player)
            player:particle("minecraft:totem_of_undying", 30, 0.4)
        end)
    end)

    shop:set(FIREWORKS_SLOT, {
        id = "minecraft:firework_rocket",
        name = t("shop-item-fireworks"),
        lore = { t("shop-item-fireworks-lore", { price = config.shop.fireworks_price }) },
    }, function(e)
        buy(e.player, "shop-item-fireworks", config.shop.fireworks_price, function(player)
            player:sound("minecraft:entity.firework_rocket.launch", 1, 1)
        end)
    end)

    return shop
end

return M
