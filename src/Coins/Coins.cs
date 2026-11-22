using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkData = global::Basalt.Server.Data.Data;

namespace Basalt.Server.Example.Coins;

public static class Coins
{
    public static long Get(int player) => SdkData.DataGetI64(player, "coins", 0);

    public static long Add(int player, long amount)
    {
        long balance = Get(player) + amount;
        SdkData.DataSetI64(player, "coins", balance);
        return balance;
    }

    public static bool Take(int player, long amount)
    {
        long balance = Get(player);
        if (balance < amount)
        {
            return false;
        }
        SdkData.DataSetI64(player, "coins", balance - amount);
        return true;
    }
}
