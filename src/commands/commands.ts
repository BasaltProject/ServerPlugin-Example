import * as bsCommands from "basalt-server/commands";
import * as bsCore from "basalt-server/core";
import * as bsData from "basalt-server/data";
import * as bsMenus from "basalt-server/menus";
import * as bsPlayers from "basalt-server/players";
import * as coins from "../coins/coins";
import * as shop from "../shop/shop";

function balance(ctx: bsCore.Ctx): void {
  const target = bsCommands.commandArgStr(ctx, "target");
  if (target) {
    const player = bsPlayers.playerByName(target);
    if (player == 0) {
      bsCommands.commandReply(ctx, "@balance-usage");
      return;
    }
    const found = bsPlayers.playerName(player);
    bsCommands.commandReply(ctx, "@balance-other", new bsCore.Args().add("name", found ? found : "").add("amount", coins.get(player).toString()));
    return;
  }
  if (bsCommands.commandIsConsole(ctx)) {
    bsCommands.commandReply(ctx, "@balance-usage");
    return;
  }
  const source = bsCommands.commandSourceName(ctx);
  const player = bsPlayers.playerByName(source ? source : "");
  bsCommands.commandReply(ctx, "@balance-self", new bsCore.Args().add("amount", coins.get(player).toString()));
}

function top(ctx: bsCore.Ctx): void {
  const limit = bsCommands.commandArgI64(ctx, "limit", 10);
  const rows = bsData.storeTop("coins", <i32>limit);
  if (rows.length == 0) {
    bsCommands.commandReply(ctx, "@top-empty");
    return;
  }
  bsCommands.commandReply(ctx, "@top-header");
  for (let i = 0; i < rows.length; i++) {
    bsCommands.commandReply(ctx, "@top-row", new bsCore.Args().add("rank", (i + 1).toString()).add("name", rows[i].name).add("amount", rows[i].value.toString()));
  }
}

function openShop(ctx: bsCore.Ctx): void {
  if (bsCommands.commandIsConsole(ctx)) {
    bsCommands.commandReply(ctx, "@shop-players-only");
    return;
  }
  const source = bsCommands.commandSourceName(ctx);
  bsMenus.menuOpen(shop.menuHandle(), bsPlayers.playerByName(source ? source : ""));
}

export function register(): void {
  bsCommands.command("coins [target: word]", "", balance);
  bsCommands.command("coins top [limit: int 1..25]", "", top);
  bsCommands.command("shop", "example.shop", openShop);
}
