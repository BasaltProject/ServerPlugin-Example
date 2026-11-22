import * as bsConfig from "basalt-server/config";
import * as bsCore from "basalt-server/core";
import * as bsMenus from "basalt-server/menus";
import * as bsPlayers from "basalt-server/players";
import * as bsText from "basalt-server/text";
import * as coins from "../coins/coins";

const SPARKLES_SLOT: i32 = 11;
const FIREWORKS_SLOT: i32 = 15;

let menu: i32 = 0;

export function menuHandle(): i32 {
  return menu;
}

function buy(player: bsCore.Player, key: string, price: i64): bool {
  if (coins.take(player, price)) {
    const item = bsText.translate(key);
    bsPlayers.send(player, "@shop-bought", new bsCore.Args().add("item", item ? item : "").add("price", price.toString()));
    return true;
  }
  bsPlayers.send(player, "@shop-cant-afford", new bsCore.Args().add("price", price.toString()).add("amount", coins.get(player).toString()));
  return false;
}

function buySparkles(ctx: bsCore.Ctx): void {
  const player = bsMenus.menuClickPlayer(ctx);
  if (buy(player, "shop-item-sparkles", bsConfig.configI64("shop.sparkles-price", 100))) {
    bsPlayers.particle(player, "minecraft:totem_of_undying", 30, 0.4);
  }
}

function buyFireworks(ctx: bsCore.Ctx): void {
  const player = bsMenus.menuClickPlayer(ctx);
  if (buy(player, "shop-item-fireworks", bsConfig.configI64("shop.fireworks-price", 150))) {
    bsPlayers.sound(player, "minecraft:entity.firework_rocket.launch", 1.0, 1.0);
  }
}

function addItem(slot: i32, item: string, nameKey: string, loreKey: string, price: i64, onClick: bsCore.Handler): void {
  const name = bsText.translate(nameKey);
  const lore = bsText.translate(loreKey, new bsCore.Args().add("price", price.toString()));
  bsMenus.menuSet(menu, slot, item, name ? name : "", lore ? lore : "", onClick);
}

export function build(): void {
  const title = bsText.translate("shop-title");
  menu = bsMenus.menuCreate(title ? title : "", 3);
  addItem(SPARKLES_SLOT, "minecraft:blaze_powder", "shop-item-sparkles", "shop-item-sparkles-lore", bsConfig.configI64("shop.sparkles-price", 100), buySparkles);
  addItem(FIREWORKS_SLOT, "minecraft:firework_rocket", "shop-item-fireworks", "shop-item-fireworks-lore", bsConfig.configI64("shop.fireworks-price", 150), buyFireworks);
}
