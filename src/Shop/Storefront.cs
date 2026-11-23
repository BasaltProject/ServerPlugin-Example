using Basalt.Server.Config;
using Basalt.Server.Core;
using Basalt.Server.Example.Coins;
using Basalt.Server.Items;
using Basalt.Server.Menus;
using Basalt.Server.Players;
using Basalt.Server.Text;
using Basalt.Server.Values;

namespace Basalt.Server.Example.Shop;

public static class Storefront
{
    const int SparklesSlot = 11;
    const int FireworksSlot = 15;

    static int menu;

    public static int Menu => menu;

    static bool Buy(int player, string key, long price)
    {
        Args args = new();
        if (Wallet.Take(player, price))
        {
            string item = Translations.Translate(key) ?? "";
            args.Add("item", item).Add("price", price.ToString());
            Directory.Send(player, "@shop-bought", args);
            return true;
        }
        args.Add("price", price.ToString()).Add("amount", Wallet.Get(player).ToString());
        Directory.Send(player, "@shop-cant-afford", args);
        return false;
    }

    static void BuySparkles(int ctx)
    {
        int player = InventoryMenu.MenuClickPlayer(ctx);
        if (Buy(player, "shop-item-sparkles", Settings.ConfigI64("shop.sparkles-price", 100)))
        {
            Directory.Particle(player, "minecraft:totem_of_undying", 30, 0.4);
        }
    }

    static void BuyFireworks(int ctx)
    {
        int player = InventoryMenu.MenuClickPlayer(ctx);
        if (Buy(player, "shop-item-fireworks", Settings.ConfigI64("shop.fireworks-price", 150)))
        {
            Directory.Sound(player, "minecraft:entity.firework_rocket.launch", 1.0F, 1.0F);
        }
    }

    static void AddItem(int slot, string item, string nameKey, string loreKey, long price, Handler onClick)
    {
        string name = Translations.Translate(nameKey) ?? "";
        Args args = new();
        args.Add("price", price.ToString());
        string lore = Translations.Translate(loreKey, args) ?? "";
        InventoryMenu.MenuSet(menu, slot, item, name, lore, onClick);
    }

    public static void Build()
    {
        string title = Translations.Translate("shop-title") ?? "";
        menu = InventoryMenu.MenuCreate(title, 3);
        AddItem(SparklesSlot, "minecraft:blaze_powder", "shop-item-sparkles", "shop-item-sparkles-lore",
                Settings.ConfigI64("shop.sparkles-price", 100), BuySparkles);
        AddItem(FireworksSlot, "minecraft:firework_rocket", "shop-item-fireworks", "shop-item-fireworks-lore",
                Settings.ConfigI64("shop.fireworks-price", 150), BuyFireworks);
    }
}
