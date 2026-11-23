package org.basaltmc.server.example.events;

import org.basaltmc.server.core.Args;
import org.basaltmc.server.example.coins.Wallet;

import static org.basaltmc.server.config.Settings.configI64;
import static org.basaltmc.server.core.Priority.NORMAL;
import static org.basaltmc.server.data.PlayerStore.dataGetBool;
import static org.basaltmc.server.data.PlayerStore.dataSetBool;
import static org.basaltmc.server.events.EventBus.eventPlayer;
import static org.basaltmc.server.events.EventBus.on;
import static org.basaltmc.server.menus.InventoryMenu.placeholder;
import static org.basaltmc.server.menus.InventoryMenu.placeholderPlayer;
import static org.basaltmc.server.menus.InventoryMenu.placeholderResult;
import static org.basaltmc.server.players.Directory.actionBar;
import static org.basaltmc.server.players.Directory.playerName;
import static org.basaltmc.server.players.Directory.playersOnline;
import static org.basaltmc.server.players.Directory.send;
import static org.basaltmc.server.tasks.Scheduler.every;

public final class PlayerListeners {
    private PlayerListeners() {}

    private static void onJoin(int ctx) {
        int player = eventPlayer(ctx);
        if (player == 0) {
            return;
        }
        String name = playerName(player);
        if (!dataGetBool(player, "welcomed", false)) {
            dataSetBool(player, "welcomed", true);
            long bonus = configI64("welcome-bonus", 50);
            Wallet.add(player, bonus);
            send(player, "@welcome", new Args().add("name", name).add("amount", String.valueOf(bonus)));
        } else {
            send(player, "@welcome-back",
                    new Args().add("name", name).add("amount", String.valueOf(Wallet.get(player))));
        }
    }

    private static void payout(int ctx) {
        long amount = configI64("payout-amount", 5);
        int[] online = playersOnline();
        for (int i = 0; i < online.length; i++) {
            long balance = Wallet.add(online[i], amount);
            actionBar(online[i], "@payout",
                    new Args().add("amount", String.valueOf(amount)).add("balance", String.valueOf(balance)));
        }
    }

    private static void coinsPlaceholder(int ctx) {
        int player = placeholderPlayer(ctx);
        placeholderResult(ctx, player == 0 ? "0" : String.valueOf(Wallet.get(player)));
    }

    public static void register() {
        on("player.join", NORMAL, PlayerListeners::onJoin);
        // Delayed by its own period, so a payout does not stack with the welcome bonus.
        int period = (int) configI64("payout-period", 1200);
        every(period, period, PlayerListeners::payout);
        placeholder("coins", PlayerListeners::coinsPlaceholder);
    }
}
