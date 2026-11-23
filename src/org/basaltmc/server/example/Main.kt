package org.basaltmc.server.example

import org.basaltmc.server.config.configI64
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.core.Priority
import org.basaltmc.server.events.on
import org.basaltmc.server.example.commands.CoinCommands
import org.basaltmc.server.example.entities.MobExamples
import org.basaltmc.server.example.events.EventExamples
import org.basaltmc.server.example.events.PlayerListeners
import org.basaltmc.server.example.hud.DisplayExamples
import org.basaltmc.server.example.items.InventoryCommands
import org.basaltmc.server.example.packets.ChannelExamples
import org.basaltmc.server.example.services.ServiceExamples
import org.basaltmc.server.example.shop.Storefront
import org.basaltmc.server.example.worlds.WorldTour
import org.basaltmc.server.log.logInfo

private fun ready(ctx: Ctx) {
    val amount = configI64("payout-amount", 5)
    val period = configI64("payout-period", 1200)
    logInfo("ready: $amount coins every $period ticks")
}

fun init() {
    Storefront.build()
    PlayerListeners.register()
    CoinCommands.register()
    EventExamples.register()
    ServiceExamples.register()
    WorldTour.register()
    DisplayExamples.register()
    InventoryCommands.register()
    MobExamples.register()
    on("plugin.enable", Priority.NORMAL, ::ready)
    ChannelExamples.register()
}
