using Basalt.Server.Core;
using Basalt.Server.Data;
using Basalt.Server.Items;
using Basalt.Server.Values;

namespace Basalt.Server.Example.Coins;

public static class Wallet
{
    public static long Get(int player) => PlayerStore.DataGetI64(player, "coins", 0);

    public static long Add(int player, long amount)
    {
        long balance = Get(player) + amount;
        PlayerStore.DataSetI64(player, "coins", balance);
        return balance;
    }

    public static bool Take(int player, long amount)
    {
        long balance = Get(player);
        if (balance < amount)
        {
            return false;
        }
        PlayerStore.DataSetI64(player, "coins", balance - amount);
        return true;
    }
}
