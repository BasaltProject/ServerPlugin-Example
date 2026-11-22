import * as bsCore from "basalt-server/core";
import * as bsData from "basalt-server/data";

export function get(player: bsCore.Player): i64 {
  return bsData.dataGetI64(player, "coins", 0);
}

export function add(player: bsCore.Player, amount: i64): i64 {
  const balance = get(player) + amount;
  bsData.dataSetI64(player, "coins", balance);
  return balance;
}

export function take(player: bsCore.Player, amount: i64): bool {
  const balance = get(player);
  if (balance < amount) return false;
  bsData.dataSetI64(player, "coins", balance - amount);
  return true;
}
