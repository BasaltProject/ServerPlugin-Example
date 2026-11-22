import * as bsConfig from "basalt-server/config";
import * as bsCore from "basalt-server/core";
import * as bsData from "basalt-server/data";
import * as bsEvents from "basalt-server/events";
import * as bsMenus from "basalt-server/menus";
import * as bsPlayers from "basalt-server/players";
import * as bsTasks from "basalt-server/tasks";
import * as coins from "../coins/coins";

function onJoin(ctx: bsCore.Ctx): void {
  const player = bsEvents.eventPlayer(ctx);
  if (player == 0) return;
  const found = bsPlayers.playerName(player);
  const name = found ? found : "";
  if (!bsData.dataGetBool(player, "welcomed", false)) {
    bsData.dataSetBool(player, "welcomed", true);
    const bonus = bsConfig.configI64("welcome-bonus", 50);
    coins.add(player, bonus);
    bsPlayers.send(player, "@welcome", new bsCore.Args().add("name", name).add("amount", bonus.toString()));
  } else {
    bsPlayers.send(player, "@welcome-back", new bsCore.Args().add("name", name).add("amount", coins.get(player).toString()));
  }
}

function payout(ctx: bsCore.Ctx): void {
  const amount = bsConfig.configI64("payout-amount", 5);
  const online = bsPlayers.playersOnline();
  for (let i = 0; i < online.length; i++) {
    const balance = coins.add(online[i], amount);
    bsPlayers.actionBar(online[i], "@payout", new bsCore.Args().add("amount", amount.toString()).add("balance", balance.toString()));
  }
}

function coinsPlaceholder(ctx: bsCore.Ctx): void {
  const player = bsMenus.placeholderPlayer(ctx);
  bsMenus.placeholderResult(ctx, player == 0 ? "0" : coins.get(player).toString());
}

export function register(): void {
  bsEvents.on("player.join", bsCore.NORMAL, onJoin);
  // Delayed by its own period, so a payout does not stack with the welcome bonus.
  const period = <i32>bsConfig.configI64("payout-period", 1200);
  bsTasks.every(period, period, payout);
  bsMenus.placeholder("coins", coinsPlaceholder);
}
