using Basalt.Server.Config;
using Basalt.Server.Core;
using Basalt.Server.Events;
using Basalt.Server.Example.Commands;
using Basalt.Server.Example.Entities;
using Basalt.Server.Example.Events;
using Basalt.Server.Example.Hud;
using Basalt.Server.Example.Items;
using Basalt.Server.Example.Packets;
using Basalt.Server.Example.Services;
using Basalt.Server.Example.Shop;
using Basalt.Server.Example.Worlds;
using Basalt.Server.Items;
using Basalt.Server.Log;
using Basalt.Server.Values;

namespace Basalt.Server.Example;

public static class Plugin
{
    static void Ready(int ctx)
    {
        long amount = Settings.ConfigI64("payout-amount", 5);
        long period = Settings.ConfigI64("payout-period", 1200);
        Logger.LogInfo("ready: " + amount.ToString() + " coins every " + period.ToString() + " ticks");
    }

    public static void Init()
    {
        Storefront.Build();
        PlayerListeners.RegisterAll();
        CoinCommands.RegisterAll();
        EventExamples.Register();
        ServiceExamples.RegisterAll();
        WorldTour.Register();
        DisplayExamples.RegisterAll();
        InventoryCommands.RegisterAll();
        MobExamples.RegisterAll();
        EventBus.On("plugin.enable", Priority.Normal, Ready);
        ChannelExamples.Register();
    }
}
