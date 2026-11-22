package org.basaltmc.server.example.commands;

import org.basaltmc.server.example.shop.Shop;
import org.basaltmc.server.example.coins.Coins;
import static org.basaltmc.server.commands.Commands.command;
import static org.basaltmc.server.commands.Commands.commandArgI64;
import static org.basaltmc.server.commands.Commands.commandArgStr;
import static org.basaltmc.server.commands.Commands.commandIsConsole;
import static org.basaltmc.server.commands.Commands.commandReply;
import static org.basaltmc.server.commands.Commands.commandSourceName;
import static org.basaltmc.server.data.Data.storeTop;
import static org.basaltmc.server.menus.Menus.menuOpen;
import static org.basaltmc.server.players.SharedPlayers.playerByName;
import static org.basaltmc.server.players.SharedPlayers.playerName;
import org.basaltmc.server.core.Args;
import org.basaltmc.server.data.Data.TopEntry;
import java.util.List;

public final class Commands {
    private Commands() {}

    private static void balance(int ctx) {
        String target = commandArgStr(ctx, "target");
        if (target != null) {
            int player = playerByName(target);
            if (player == 0) {
                commandReply(ctx, "@balance-usage");
                return;
            }
            commandReply(ctx, "@balance-other",
                    new Args().add("name", playerName(player)).add("amount", String.valueOf(Coins.get(player))));
            return;
        }
        if (commandIsConsole(ctx)) {
            commandReply(ctx, "@balance-usage");
            return;
        }
        int player = playerByName(commandSourceName(ctx));
        commandReply(ctx, "@balance-self", new Args().add("amount", String.valueOf(Coins.get(player))));
    }

    private static void top(int ctx) {
        Long given = commandArgI64(ctx, "limit");
        long limit = given == null ? 10 : given;
        List<TopEntry> rows = storeTop("coins", (int) limit);
        if (rows.isEmpty()) {
            commandReply(ctx, "@top-empty");
            return;
        }
        commandReply(ctx, "@top-header");
        long rank = 1;
        for (TopEntry row : rows) {
            commandReply(ctx, "@top-row", new Args().add("rank", String.valueOf(rank++)).add("name", row.name)
                    .add("amount", String.valueOf(row.value)));
        }
    }

    private static void openShop(int ctx) {
        if (commandIsConsole(ctx)) {
            commandReply(ctx, "@shop-players-only");
            return;
        }
        menuOpen(Shop.menu(), playerByName(commandSourceName(ctx)));
    }

    public static void register() {
        command("coins [target: word]", "", Commands::balance);
        command("coins top [limit: int 1..25]", "", Commands::top);
        command("shop", "example.shop", Commands::openShop);
    }
}
