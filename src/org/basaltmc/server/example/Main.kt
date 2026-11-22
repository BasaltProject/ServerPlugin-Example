package org.basaltmc.server.example

import org.basaltmc.server.example.shop.Shop
import org.basaltmc.server.example.commands.Commands
import org.basaltmc.server.example.events.Events
import org.basaltmc.server.example.events.AreaEvents
import org.basaltmc.server.example.server.ServerArea
import org.basaltmc.server.example.worlds.Worlds
import org.basaltmc.server.example.hud.HudExample
import org.basaltmc.server.example.items.ItemCommands
import org.basaltmc.server.example.entities.EntitiesExample
import org.basaltmc.server.example.packets.PacketsExample
import org.basaltmc.server.core.Ctx

private fun ready(ctx: Ctx) {
    val amount = org.basaltmc.server.config.configI64("payout-amount", 5)
    val period = org.basaltmc.server.config.configI64("payout-period", 1200)
    org.basaltmc.server.log.logInfo("ready: $amount coins every $period ticks")
}

fun init() {
    Shop.build()
    Events.register()
    Commands.register()
    AreaEvents.register()
    ServerArea.register()
    Worlds.register()
    HudExample.register()
    ItemCommands.register()
    EntitiesExample.register()
    org.basaltmc.server.events.on("plugin.enable", org.basaltmc.server.core.Priority.NORMAL, ::ready)
    PacketsExample.register()
}
