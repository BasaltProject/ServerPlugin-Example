package org.basaltmc.server.example.coins;

import static org.basaltmc.server.data.SharedData.dataGetI64;
import static org.basaltmc.server.data.SharedData.dataSetI64;
public final class Coins {
    private Coins() {}

    public static long get(int player) {
        return dataGetI64(player, "coins", 0);
    }

    public static long add(int player, long amount) {
        long balance = get(player) + amount;
        dataSetI64(player, "coins", balance);
        return balance;
    }

    public static boolean take(int player, long amount) {
        long balance = get(player);
        if (balance < amount) {
            return false;
        }
        dataSetI64(player, "coins", balance - amount);
        return true;
    }
}
