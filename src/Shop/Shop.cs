using ExampleCoins = global::Basalt.Server.Example.Coins.Coins;
using Basalt.Server.Core;
using Basalt.Server.Items;
using Basalt.Server.Values;
using SdkConfig = global::Basalt.Server.Config.Config;
using SdkMenus = global::Basalt.Server.Menus.Menus;
using SdkPlayers = global::Basalt.Server.Players.Players;
using SdkText = global::Basalt.Server.Text.Text;

namespace Basalt.Server.Example.Shop;

public static class Shop
{
    const int SparklesSlot = 11;
    const int FireworksSlot = 15;

    static int menu;

    public static int Menu => menu;

    static bool Buy(int player, string key, long price)
    {
        Args args = new();
        if (ExampleCoins.Take(player, price))
        {
            string item = SdkText.Translate(key) ?? "";
            args.Add("item", item).Add("price", price.ToString());
            SdkPlayers.Send(player, "@shop-bought", args);
            return true;
        }
        args.Add("price", price.ToString()).Add("amount", ExampleCoins.Get(player).ToString());
        SdkPlayers.Send(player, "@shop-cant-afford", args);
        return false;
    }

    static void BuySparkles(int ctx)
    {
        int player = SdkMenus.MenuClickPlayer(ctx);
        if (Buy(player, "shop-item-sparkles", SdkConfig.ConfigI64("shop.sparkles-price", 100)))
        {
            SdkPlayers.Particle(player, "minecraft:totem_of_undying", 30, 0.4);
        }
    }

    static void BuyFireworks(int ctx)
    {
        int player = SdkMenus.MenuClickPlayer(ctx);
        if (Buy(player, "shop-item-fireworks", SdkConfig.ConfigI64("shop.fireworks-price", 150)))
        {
            SdkPlayers.Sound(player, "minecraft:entity.firework_rocket.launch", 1.0F, 1.0F);
        }
    }

    static void AddItem(int slot, string item, string nameKey, string loreKey, long price, Handler onClick)
    {
        string name = SdkText.Translate(nameKey) ?? "";
        Args args = new();
        args.Add("price", price.ToString());
        string lore = SdkText.Translate(loreKey, args) ?? "";
        SdkMenus.MenuSet(menu, slot, item, name, lore, onClick);
    }

    public static void Build()
    {
        string title = SdkText.Translate("shop-title") ?? "";
        menu = SdkMenus.MenuCreate(title, 3);
        AddItem(SparklesSlot, "minecraft:blaze_powder", "shop-item-sparkles", "shop-item-sparkles-lore",
                SdkConfig.ConfigI64("shop.sparkles-price", 100), BuySparkles);
        AddItem(FireworksSlot, "minecraft:firework_rocket", "shop-item-fireworks", "shop-item-fireworks-lore",
                SdkConfig.ConfigI64("shop.fireworks-price", 150), BuyFireworks);
    }
}
