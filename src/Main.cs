using ExamplePacketsExample = global::Basalt.Server.Example.Packets.PacketsExample;
using ExampleEntitiesExample = global::Basalt.Server.Example.Entities.EntitiesExample;
using ExampleItemsCommands = global::Basalt.Server.Example.Items.ItemsCommands;
using ExampleHudExample = global::Basalt.Server.Example.Hud.HudExample;
using ExampleWorldsTour = global::Basalt.Server.Example.Worlds.WorldsTour;
using ExampleServerExample = global::Basalt.Server.Example.Server.ServerExample;
using ExampleAreaEvents = global::Basalt.Server.Example.Events.AreaEvents;
using ExampleEvents = global::Basalt.Server.Example.Events.Events;
using ExampleCommands = global::Basalt.Server.Example.Commands.Commands;
using ExampleShop = global::Basalt.Server.Example.Shop.Shop;
using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkConfig = global::Basalt.Server.Config.Config;
using SdkEvents = global::Basalt.Server.Events.Events;
using SdkLogger = global::Basalt.Server.Log.Logger;

namespace Basalt.Server.Example;

public static class Plugin
{
    static void Ready(int ctx)
    {
        long amount = SdkConfig.ConfigI64("payout-amount", 5);
        long period = SdkConfig.ConfigI64("payout-period", 1200);
        SdkLogger.LogInfo("ready: " + amount.ToString() + " coins every " + period.ToString() + " ticks");
    }

    public static void Init()
    {
        ExampleShop.Build();
        ExampleEvents.RegisterAll();
        ExampleCommands.RegisterAll();
        ExampleAreaEvents.Register();
        ExampleServerExample.RegisterAll();
        ExampleWorldsTour.Register();
        ExampleHudExample.RegisterAll();
        ExampleItemsCommands.RegisterAll();
        ExampleEntitiesExample.RegisterAll();
        SdkEvents.On("plugin.enable", Priority.Normal, Ready);
        ExamplePacketsExample.Register();
    }
}
