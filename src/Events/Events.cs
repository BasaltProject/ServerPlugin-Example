using ExampleCoins = global::Basalt.Server.Example.Coins.Coins;
using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkConfig = global::Basalt.Server.Config.Config;
using SdkData = global::Basalt.Server.Data.Data;
using SdkEvents = global::Basalt.Server.Events.Events;
using SdkMenus = global::Basalt.Server.Menus.Menus;
using SdkPlayers = global::Basalt.Server.Players.Players;
using SdkTasks = global::Basalt.Server.Tasks.Tasks;

namespace Basalt.Server.Example.Events;

public static class Events
{
    static void OnJoin(int ctx)
    {
        int player = SdkEvents.EventPlayer(ctx);
        if (player == 0)
        {
            return;
        }
        string name = SdkPlayers.PlayerName(player) ?? "";
        Args args = new();
        if (!SdkData.DataGetBool(player, "welcomed", false))
        {
            SdkData.DataSetBool(player, "welcomed", true);
            long bonus = SdkConfig.ConfigI64("welcome-bonus", 50);
            ExampleCoins.Add(player, bonus);
            args.Add("name", name).Add("amount", bonus.ToString());
            SdkPlayers.Send(player, "@welcome", args);
        }
        else
        {
            args.Add("name", name).Add("amount", ExampleCoins.Get(player).ToString());
            SdkPlayers.Send(player, "@welcome-back", args);
        }
    }

    static void Payout(int ctx)
    {
        long amount = SdkConfig.ConfigI64("payout-amount", 5);
        int[] online = SdkPlayers.PlayersOnline();
        for (int i = 0; i < online.Length && i < 64; i++)
        {
            long balance = ExampleCoins.Add(online[i], amount);
            Args args = new();
            args.Add("amount", amount.ToString()).Add("balance", balance.ToString());
            SdkPlayers.ActionBar(online[i], "@payout", args);
        }
    }

    static void CoinsPlaceholder(int ctx)
    {
        int player = SdkMenus.PlaceholderPlayer(ctx);
        SdkMenus.PlaceholderResult(ctx, player == 0 ? "0" : ExampleCoins.Get(player).ToString());
    }

    public static void RegisterAll()
    {
        SdkEvents.On("player.join", Priority.Normal, OnJoin);
        // Delayed by its own period, so a payout does not stack with the welcome bonus.
        int period = (int)SdkConfig.ConfigI64("payout-period", 1200);
        SdkTasks.Every(period, period, Payout);
        SdkMenus.Placeholder("coins", CoinsPlaceholder);
    }
}
