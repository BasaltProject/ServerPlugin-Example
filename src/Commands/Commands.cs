using ExampleShop = global::Basalt.Server.Example.Shop.Shop;
using ExampleCoins = global::Basalt.Server.Example.Coins.Coins;
using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkCommands = global::Basalt.Server.Commands.Commands;
using SdkData = global::Basalt.Server.Data.Data;
using SdkMenus = global::Basalt.Server.Menus.Menus;
using SdkPlayers = global::Basalt.Server.Players.Players;

namespace Basalt.Server.Example.Commands;

public static class Commands
{
    static void Balance(int ctx)
    {
        string target = SdkCommands.CommandArgStr(ctx, "target");
        if (target != null)
        {
            int other = SdkPlayers.PlayerByName(target);
            if (other == 0)
            {
                SdkCommands.CommandReply(ctx, "@balance-usage");
                return;
            }
            string name = SdkPlayers.PlayerName(other) ?? "";
            Args otherArgs = new();
            otherArgs.Add("name", name).Add("amount", ExampleCoins.Get(other).ToString());
            SdkCommands.CommandReply(ctx, "@balance-other", otherArgs);
            return;
        }
        if (SdkCommands.CommandIsConsole(ctx))
        {
            SdkCommands.CommandReply(ctx, "@balance-usage");
            return;
        }
        string source = SdkCommands.CommandSourceName(ctx) ?? "";
        int player = SdkPlayers.PlayerByName(source);
        Args args = new();
        args.Add("amount", ExampleCoins.Get(player).ToString());
        SdkCommands.CommandReply(ctx, "@balance-self", args);
    }

    static void Top(int ctx)
    {
        long limit = SdkCommands.CommandArgI64(ctx, "limit") ?? 10;
        var rows = SdkData.StoreTop("coins", (int)limit);
        if (rows.Count == 0)
        {
            SdkCommands.CommandReply(ctx, "@top-empty");
            return;
        }
        SdkCommands.CommandReply(ctx, "@top-header");
        long rank = 1;
        foreach (var row in rows)
        {
            Args args = new();
            args.Add("rank", rank.ToString()).Add("name", row.Name).Add("amount", row.Value.ToString());
            SdkCommands.CommandReply(ctx, "@top-row", args);
            rank++;
        }
    }

    static void OpenShop(int ctx)
    {
        if (SdkCommands.CommandIsConsole(ctx))
        {
            SdkCommands.CommandReply(ctx, "@shop-players-only");
            return;
        }
        string source = SdkCommands.CommandSourceName(ctx) ?? "";
        SdkMenus.MenuOpen(ExampleShop.Menu, SdkPlayers.PlayerByName(source));
    }

    public static void RegisterAll()
    {
        SdkCommands.Command("coins [target: word]", "", Balance);
        SdkCommands.Command("coins top [limit: int 1..25]", "", Top);
        SdkCommands.Command("shop", "example.shop", OpenShop);
    }
}
