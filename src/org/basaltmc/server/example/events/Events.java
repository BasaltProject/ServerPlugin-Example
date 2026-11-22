package org.basaltmc.server.example.events;

import org.basaltmc.server.example.coins.Coins;
import static org.basaltmc.server.config.Config.configI64;
import static org.basaltmc.server.core.Priority.NORMAL;
import static org.basaltmc.server.data.SharedData.dataGetBool;
import static org.basaltmc.server.data.SharedData.dataSetBool;
import static org.basaltmc.server.events.Events.eventPlayer;
import static org.basaltmc.server.events.Events.on;
import static org.basaltmc.server.menus.Menus.placeholder;
import static org.basaltmc.server.menus.Menus.placeholderPlayer;
import static org.basaltmc.server.menus.Menus.placeholderResult;
import static org.basaltmc.server.players.SharedPlayers.actionBar;
import static org.basaltmc.server.players.SharedPlayers.playerName;
import static org.basaltmc.server.players.SharedPlayers.playersOnline;
import static org.basaltmc.server.players.SharedPlayers.send;
import static org.basaltmc.server.tasks.Tasks.every;
import org.basaltmc.server.core.Args;

public final class Events {
    private Events() {}

    private static void onJoin(int ctx) {
        int player = eventPlayer(ctx);
        if (player == 0) {
            return;
        }
        String name = playerName(player);
        if (!dataGetBool(player, "welcomed", false)) {
            dataSetBool(player, "welcomed", true);
            long bonus = configI64("welcome-bonus", 50);
            Coins.add(player, bonus);
            send(player, "@welcome", new Args().add("name", name).add("amount", String.valueOf(bonus)));
        } else {
            send(player, "@welcome-back",
                    new Args().add("name", name).add("amount", String.valueOf(Coins.get(player))));
        }
    }

    private static void payout(int ctx) {
        long amount = configI64("payout-amount", 5);
        int[] online = playersOnline();
        for (int i = 0; i < online.length && i < 64; i++) {
            long balance = Coins.add(online[i], amount);
            actionBar(online[i], "@payout",
                    new Args().add("amount", String.valueOf(amount)).add("balance", String.valueOf(balance)));
        }
    }

    private static void coinsPlaceholder(int ctx) {
        int player = placeholderPlayer(ctx);
        placeholderResult(ctx, player == 0 ? "0" : String.valueOf(Coins.get(player)));
    }

    public static void register() {
        on("player.join", NORMAL, Events::onJoin);
        // Delayed by its own period, so a payout does not stack with the welcome bonus.
        int period = (int) configI64("payout-period", 1200);
        every(period, period, Events::payout);
        placeholder("coins", Events::coinsPlaceholder);
    }
}
