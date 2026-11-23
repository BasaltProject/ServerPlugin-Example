package org.basaltmc.server.example.shop;

import org.basaltmc.server.core.Args;
import org.basaltmc.server.core.Handler;
import org.basaltmc.server.example.coins.Wallet;

import static org.basaltmc.server.config.Settings.configI64;
import static org.basaltmc.server.menus.InventoryMenu.menuClickPlayer;
import static org.basaltmc.server.menus.InventoryMenu.menuCreate;
import static org.basaltmc.server.menus.InventoryMenu.menuSet;
import static org.basaltmc.server.players.Directory.particle;
import static org.basaltmc.server.players.Directory.send;
import static org.basaltmc.server.players.Directory.sound;
import static org.basaltmc.server.text.Translations.translate;

public final class Storefront {
    private static final int SPARKLES_SLOT = 11;
    private static final int FIREWORKS_SLOT = 15;

    private static int menu = 0;

    private Storefront() {}

    private static boolean buy(int player, String key, long price) {
        if (Wallet.take(player, price)) {
            send(player, "@shop-bought", new Args().add("item", translate(key)).add("price", String.valueOf(price)));
            return true;
        }
        send(player, "@shop-cant-afford",
                new Args().add("price", String.valueOf(price)).add("amount", String.valueOf(Wallet.get(player))));
        return false;
    }

    private static void buySparkles(int ctx) {
        int player = menuClickPlayer(ctx);
        if (buy(player, "shop-item-sparkles", configI64("shop.sparkles-price", 100))) {
            particle(player, "minecraft:totem_of_undying", 30, 0.4);
        }
    }

    private static void buyFireworks(int ctx) {
        int player = menuClickPlayer(ctx);
        if (buy(player, "shop-item-fireworks", configI64("shop.fireworks-price", 150))) {
            sound(player, "minecraft:entity.firework_rocket.launch", 1.0F, 1.0F);
        }
    }

    private static void addItem(int slot, String item, String nameKey, String loreKey, long price, Handler onClick) {
        String name = translate(nameKey);
        String lore = translate(loreKey, new Args().add("price", String.valueOf(price)));
        menuSet(menu, slot, item, name, lore, onClick);
    }

    public static void build() {
        menu = menuCreate(translate("shop-title"), 3);
        addItem(SPARKLES_SLOT, "minecraft:blaze_powder", "shop-item-sparkles", "shop-item-sparkles-lore",
                configI64("shop.sparkles-price", 100), Storefront::buySparkles);
        addItem(FIREWORKS_SLOT, "minecraft:firework_rocket", "shop-item-fireworks", "shop-item-fireworks-lore",
                configI64("shop.fireworks-price", 150), Storefront::buyFireworks);
    }

    public static int menu() {
        return menu;
    }
}
