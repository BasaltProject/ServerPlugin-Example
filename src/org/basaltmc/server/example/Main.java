package org.basaltmc.server.example;

import org.basaltmc.server.example.commands.CoinCommands;
import org.basaltmc.server.example.entities.MobExamples;
import org.basaltmc.server.example.events.EventExamples;
import org.basaltmc.server.example.events.PlayerListeners;
import org.basaltmc.server.example.hud.DisplayExamples;
import org.basaltmc.server.example.items.InventoryCommands;
import org.basaltmc.server.example.packets.ChannelExamples;
import org.basaltmc.server.example.services.ServiceExamples;
import org.basaltmc.server.example.shop.Storefront;
import org.basaltmc.server.example.worlds.WorldTour;

import static org.basaltmc.server.config.Settings.configI64;
import static org.basaltmc.server.core.Priority.NORMAL;
import static org.basaltmc.server.events.EventBus.on;
import static org.basaltmc.server.log.Logger.logInfo;

public final class Main {
    private Main() {}

    private static void ready(int ctx) {
        logInfo("ready: " + configI64("payout-amount", 5) + " coins every " + configI64("payout-period", 1200)
                + " ticks");
    }

    public static void init() {
        Storefront.build();
        PlayerListeners.register();
        CoinCommands.register();
        EventExamples.register();
        ServiceExamples.register();
        WorldTour.register();
        DisplayExamples.register();
        InventoryCommands.register();
        MobExamples.register();
        on("plugin.enable", NORMAL, Main::ready);
        ChannelExamples.register();
    }
}
