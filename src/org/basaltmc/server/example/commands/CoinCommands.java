package org.basaltmc.server.example.commands;

import java.util.List;
import org.basaltmc.server.commands.Registry;
import org.basaltmc.server.core.Args;
import org.basaltmc.server.data.PlayerStore.TopEntry;
import org.basaltmc.server.example.coins.Wallet;
import org.basaltmc.server.example.shop.Storefront;

import static org.basaltmc.server.commands.Arguments.i64;
import static org.basaltmc.server.commands.Arguments.str;
import static org.basaltmc.server.commands.Sender.isConsole;
import static org.basaltmc.server.commands.Sender.name;
import static org.basaltmc.server.commands.Sender.reply;
import static org.basaltmc.server.data.PlayerStore.storeTop;
import static org.basaltmc.server.menus.InventoryMenu.menuOpen;
import static org.basaltmc.server.players.Directory.playerByName;
import static org.basaltmc.server.players.Directory.playerName;

public final class CoinCommands {
    private CoinCommands() {}

    private static void balance(int ctx) {
        String target = str(ctx, "target");
        if (target != null) {
            int player = playerByName(target);
            if (player == 0) {
                reply(ctx, "@balance-usage");
                return;
            }
            reply(ctx, "@balance-other",
                    new Args().add("name", playerName(player)).add("amount", String.valueOf(Wallet.get(player))));
            return;
        }
        if (isConsole(ctx)) {
            reply(ctx, "@balance-usage");
            return;
        }
        int player = playerByName(name(ctx));
        reply(ctx, "@balance-self", new Args().add("amount", String.valueOf(Wallet.get(player))));
    }

    private static void top(int ctx) {
        Long given = i64(ctx, "limit");
        long limit = given == null ? 10 : given;
        List<TopEntry> rows = storeTop("coins", (int) limit);
        if (rows.isEmpty()) {
            reply(ctx, "@top-empty");
            return;
        }
        reply(ctx, "@top-header");
        long rank = 1;
        for (TopEntry row : rows) {
            reply(ctx, "@top-row", new Args().add("rank", String.valueOf(rank++)).add("name", row.name)
                    .add("amount", String.valueOf(row.value)));
        }
    }

    private static void openShop(int ctx) {
        if (isConsole(ctx)) {
            reply(ctx, "@shop-players-only");
            return;
        }
        menuOpen(Storefront.menu(), playerByName(name(ctx)));
    }

    public static void register() {
        Registry.register("coins [target: word]", "", CoinCommands::balance);
        Registry.register("coins top [limit: int 1..25]", "", CoinCommands::top);
        Registry.register("shop", "example.shop", CoinCommands::openShop);
    }
}
