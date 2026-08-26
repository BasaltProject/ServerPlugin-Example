-- Wires the modules together; registers nothing itself.

local coins = require("coins")
local shop = require("shop")
local events = require("events")
local commands = require("commands")

local shop_menu = shop.build()

events.register(coins)
commands.register(coins, shop_menu)

on("plugin.enable", function()
    log.info("ready: " .. config.payout_amount .. " coins every " .. config.payout_period .. " ticks")
end)
