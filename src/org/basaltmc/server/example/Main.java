package org.basaltmc.server.example;

import org.basaltmc.server.example.packets.PacketsExample;
import org.basaltmc.server.example.entities.EntitiesExample;
import org.basaltmc.server.example.items.ItemCommands;
import org.basaltmc.server.example.hud.HudExample;
import org.basaltmc.server.example.worlds.Worlds;
import org.basaltmc.server.example.server.ServerTour;
import org.basaltmc.server.example.events.AreaEvents;
import org.basaltmc.server.example.events.Events;
import org.basaltmc.server.example.commands.Commands;
import org.basaltmc.server.example.shop.Shop;
import static org.basaltmc.server.config.Config.configI64;
import static org.basaltmc.server.core.Priority.NORMAL;
import static org.basaltmc.server.events.Events.on;
import static org.basaltmc.server.log.Log.logInfo;
public final class Main {
    private Main() {}

    private static void ready(int ctx) {
        logInfo("ready: " + configI64("payout-amount", 5) + " coins every " + configI64("payout-period", 1200)
                + " ticks");
    }

    public static void init() {
        Shop.build();
        Events.register();
        Commands.register();
        AreaEvents.register();
        ServerTour.register();
        Worlds.register();
        HudExample.register();
        ItemCommands.register();
        EntitiesExample.register();
        on("plugin.enable", NORMAL, Main::ready);
        PacketsExample.register();
    }
}
