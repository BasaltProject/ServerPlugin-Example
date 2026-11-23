using Basalt.Server.Config;
using Basalt.Server.Core;
using Basalt.Server.Data;
using Basalt.Server.Events;
using Basalt.Server.Example.Coins;
using Basalt.Server.Items;
using Basalt.Server.Menus;
using Basalt.Server.Players;
using Basalt.Server.Tasks;
using Basalt.Server.Values;

namespace Basalt.Server.Example.Events;

public static class PlayerListeners
{
    static void OnJoin(int ctx)
    {
        int player = EventBus.EventPlayer(ctx);
        if (player == 0)
        {
            return;
        }
        string name = Directory.PlayerName(player) ?? "";
        Args args = new();
        if (!PlayerStore.DataGetBool(player, "welcomed", false))
        {
            PlayerStore.DataSetBool(player, "welcomed", true);
            long bonus = Settings.ConfigI64("welcome-bonus", 50);
            Wallet.Add(player, bonus);
            args.Add("name", name).Add("amount", bonus.ToString());
            Directory.Send(player, "@welcome", args);
        }
        else
        {
            args.Add("name", name).Add("amount", Wallet.Get(player).ToString());
            Directory.Send(player, "@welcome-back", args);
        }
    }

    static void Payout(int ctx)
    {
        long amount = Settings.ConfigI64("payout-amount", 5);
        int[] online = Directory.PlayersOnline();
        for (int i = 0; i < online.Length; i++)
        {
            long balance = Wallet.Add(online[i], amount);
            Args args = new();
            args.Add("amount", amount.ToString()).Add("balance", balance.ToString());
            Directory.ActionBar(online[i], "@payout", args);
        }
    }

    static void CoinsPlaceholder(int ctx)
    {
        int player = InventoryMenu.PlaceholderPlayer(ctx);
        InventoryMenu.PlaceholderResult(ctx, player == 0 ? "0" : Wallet.Get(player).ToString());
    }

    public static void RegisterAll()
    {
        EventBus.On("player.join", Priority.Normal, OnJoin);
        // Delayed by its own period, so a payout does not stack with the welcome bonus.
        int period = (int)Settings.ConfigI64("payout-period", 1200);
        Scheduler.Every(period, period, Payout);
        InventoryMenu.Placeholder("coins", CoinsPlaceholder);
    }
}
