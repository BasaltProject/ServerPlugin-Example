using Basalt.Server.Commands;
using Basalt.Server.Core;
using Basalt.Server.Data;
using Basalt.Server.Example.Coins;
using Basalt.Server.Example.Shop;
using Basalt.Server.Items;
using Basalt.Server.Menus;
using Basalt.Server.Players;
using Basalt.Server.Values;

namespace Basalt.Server.Example.Commands;

public static class CoinCommands
{
    static void Balance(int ctx)
    {
        string target = Arguments.Str(ctx, "target");
        if (target != null)
        {
            int other = Directory.PlayerByName(target);
            if (other == 0)
            {
                Sender.Reply(ctx, "@balance-usage");
                return;
            }
            string name = Directory.PlayerName(other) ?? "";
            Args otherArgs = new();
            otherArgs.Add("name", name).Add("amount", Wallet.Get(other).ToString());
            Sender.Reply(ctx, "@balance-other", otherArgs);
            return;
        }
        if (Sender.IsConsole(ctx))
        {
            Sender.Reply(ctx, "@balance-usage");
            return;
        }
        string source = Sender.Name(ctx) ?? "";
        int player = Directory.PlayerByName(source);
        Args args = new();
        args.Add("amount", Wallet.Get(player).ToString());
        Sender.Reply(ctx, "@balance-self", args);
    }

    static void Top(int ctx)
    {
        long limit = Arguments.I64(ctx, "limit") ?? 10;
        var rows = PlayerStore.StoreTop("coins", (int)limit);
        if (rows.Count == 0)
        {
            Sender.Reply(ctx, "@top-empty");
            return;
        }
        Sender.Reply(ctx, "@top-header");
        long rank = 1;
        foreach (var row in rows)
        {
            Args args = new();
            args.Add("rank", rank.ToString()).Add("name", row.Name).Add("amount", row.Value.ToString());
            Sender.Reply(ctx, "@top-row", args);
            rank++;
        }
    }

    static void OpenShop(int ctx)
    {
        if (Sender.IsConsole(ctx))
        {
            Sender.Reply(ctx, "@shop-players-only");
            return;
        }
        string source = Sender.Name(ctx) ?? "";
        InventoryMenu.MenuOpen(Storefront.Menu, Directory.PlayerByName(source));
    }

    public static void RegisterAll()
    {
        Registry.Register("coins [target: word]", "", Balance);
        Registry.Register("coins top [limit: int 1..25]", "", Top);
        Registry.Register("shop", "example.shop", OpenShop);
    }
}
