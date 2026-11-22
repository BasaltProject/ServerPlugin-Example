import * as bsConfig from "basalt-server/config";
import * as bsCore from "basalt-server/core";
import * as bsEvents from "basalt-server/events";
import * as bsLog from "basalt-server/log";
import * as areaEvents from "./events/area_events";
import * as commands from "./commands/commands";
import * as entities from "./entities/entities";
import * as events from "./events/events";
import * as server from "./server/server";
import * as hud from "./hud/hud";
import * as packets from "./packets/packets";
import * as items from "./items/items";
import * as shop from "./shop/shop";
import * as worlds from "./worlds/worlds";

function ready(ctx: bsCore.Ctx): void {
  const amount = bsConfig.configI64("payout-amount", 5);
  const period = bsConfig.configI64("payout-period", 1200);
  bsLog.logInfo(`ready: ${amount} coins every ${period} ticks`);
}

export function init(): void {
  shop.build();
  events.register();
  commands.register();
  areaEvents.register();
  server.register();
  worlds.register();
  hud.register();
  items.register();
  entities.register();
  bsEvents.on("plugin.enable", bsCore.NORMAL, ready);
  packets.register();
}
