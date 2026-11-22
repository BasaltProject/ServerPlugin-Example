import * as bsCore from "basalt-server/core";
import * as bsEvents from "basalt-server/events";
import * as bsLog from "basalt-server/log";
import * as bsValues from "basalt-server/values";

const VALUE_BYTES: i32 = 512;
const LIST_BYTES: i32 = 8192;

function doc(writer: bsValues.ValueWriter): Uint8Array {
  const out = writer.finish();
  return out === null ? new Uint8Array(0) : out;
}

function onForm(ctx: bsCore.Ctx): void {
  const answer = bsEvents.completionValue(ctx, VALUE_BYTES);
  if (answer === null) return;
  const button = answer.find("button");
  let number: i64 = -1;
  if (button !== null) number = button.toInt(-1);
  bsLog.logInfo("form button " + number.toString());
}

function onDialog(ctx: bsCore.Ctx): void {
  const answer = bsEvents.completionValue(ctx, VALUE_BYTES);
  if (answer === null) return;
  const id = answer.find("id");
  let text = "";
  if (id !== null) {
    const found = id.toStr();
    if (found !== null) text = found;
  }
  bsLog.logInfo("dialog id " + text);
}

function greet(ctx: bsCore.Ctx): void {
  const player = bsEvents.eventPlayer(ctx);
  bsEvents.eventGet(ctx, "world", VALUE_BYTES);
  bsEvents.eventFields(ctx, VALUE_BYTES);

  bsEvents.eventDescribe("player.chat", VALUE_BYTES);
  bsEvents.eventDescribe("no.such.event", VALUE_BYTES);
  bsEvents.eventNames(LIST_BYTES);

  const uuid = bsEvents.playerUuid(player);
  bsEvents.playerByUuid(uuid === null ? "" : uuid);
  bsEvents.playerByUuid("00000000-0000-0000-0000-999999999999");
  bsEvents.playerUuid(9999);

  bsEvents.playerGet(player, "food", VALUE_BYTES);
  bsEvents.playerGet(player, "no_such_property", VALUE_BYTES);
  const food = doc(new bsValues.ValueWriter().int(18));
  bsEvents.playerSet(player, "food", food);
  bsEvents.playerSet(player, "name", food);
  bsEvents.playerDescribe(LIST_BYTES);
  const none = doc(new bsValues.ValueWriter().beginList().end());
  bsEvents.playerCall(player, "reset_idle", none, VALUE_BYTES);
  bsEvents.playerCall(player, "no_such_operation", none, VALUE_BYTES);

  bsEvents.playerHealth(player);
  bsEvents.playerSetHealth(player, 10.5);
  bsEvents.playerSetHealth(player, -1.0);
  bsEvents.playerGamemode(player);
  bsEvents.playerSetGamemode(player, "creative");
  bsEvents.playerSetGamemode(player, "bogus");
  bsEvents.playerTeleport(player, "", 10.5, 64.0, -3.25, 90.0, 15.0);
  bsEvents.playerTeleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0, 0.0);
  bsEvents.playerTitle(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10);
  bsEvents.playerTitle(player, "<gold>Welcome", "", -1, 40, 10);
  bsEvents.playerEffect(player, "minecraft:speed", 200, 1);
  bsEvents.playerEffect(player, "no_such_effect", 200, 1);

  const trade = doc(new bsValues.ValueWriter().beginMap().key("entity").int(1).end());
  bsEvents.playerOpenInventory(player, "trades", trade);
  bsEvents.playerOpenInventory(player, "no_such_window", trade);
  bsEvents.playerCloseWindow(player);

  const builder = new bsValues.ValueWriter().beginMap().key("title").string("Pick one").key("buttons").beginList().string("Yes").string("No").end();
  builder.key("fields").beginList().beginMap().key("key").string("name").key("type").string("text");
  const form = doc(builder.key("label").string("Name").end().end().end());
  bsEvents.formOpen(player, form, onForm);
  bsEvents.formOpen(player, doc(new bsValues.ValueWriter().beginMap().key("title").int(1).end()), onForm);
  bsEvents.formClose(player);
  bsEvents.formOpen(player, form, onForm);
  bsEvents.dialogShow(player, "minecraft:confirm", onDialog);
  bsEvents.dialogClear(player);
  bsEvents.dialogShow(player, "minecraft:confirm", onDialog);
  bsEvents.playerKick(9999, "bye");
}

function chat(ctx: bsCore.Ctx): void {
  bsEvents.eventName(ctx);
  let text = "";
  const message = bsEvents.eventGet(ctx, "message", VALUE_BYTES);
  if (message !== null) {
    const found = message.toStr();
    if (found !== null) text = found;
  }
  bsEvents.eventGet(ctx, "no_such_field", VALUE_BYTES);
  bsEvents.eventCancelled(ctx);
  bsEvents.eventIsPost(ctx);
  bsEvents.eventSet(ctx, "player_name", doc(new bsValues.ValueWriter().string("x")));
  if (text.length == 6 && text.charCodeAt(0) == 99) {
    bsEvents.eventCancelWith(ctx, "<red>Not allowed</red>");
    return;
  }
  if (text.length == 3 && text.charCodeAt(0) == 98) {
    bsEvents.playerKick(bsEvents.eventPlayer(ctx), "<red>Bye</red>");
    return;
  }
  if (text.length == 4 && text.charCodeAt(0) == 112) {
    const payload = doc(new bsValues.ValueWriter().beginMap().key("from").string("chat").end());
    bsEvents.eventEmit("example:pong", payload);
    bsEvents.eventEmit("nocolon", payload);
  }
  bsEvents.eventSet(ctx, "message", doc(new bsValues.ValueWriter().string(text + "!")));
  bsEvents.eventFields(ctx, VALUE_BYTES);
}

function chatPost(ctx: bsCore.Ctx): void {
  const post = bsEvents.eventIsPost(ctx) == 1;
  const cancelled = bsEvents.eventCancelled(ctx) == 1;
  bsEvents.eventSet(ctx, "message", doc(new bsValues.ValueWriter().string("late")));
  bsEvents.eventCancelWith(ctx, "<red>Too late</red>");
  const reason = bsEvents.eventCancelMessage(ctx);
  bsLog.logInfo((post ? "post " : "decision ") + (cancelled ? "cancelled " : "delivered ") + (reason === null ? "no message" : reason));
}

function ping(ctx: bsCore.Ctx): void {
  const payload = bsEvents.eventPayload(ctx, VALUE_BYTES);
  let level: i64 = 0;
  if (payload !== null) {
    const found = payload.find("level");
    if (found !== null) level = found.toInt(0);
  }
  bsLog.logInfo("ping level " + level.toString());
}

export function register(): void {
  bsEvents.eventOnEx("player.chat", bsCore.NORMAL, 0, null, chat);
  bsEvents.eventOnEx("player.chat", bsCore.HIGHEST, bsEvents.EVENT_POST | bsEvents.EVENT_CANCELLED, null, chatPost);
  bsEvents.eventOnEx("player.join", bsCore.LOW, 0, doc(new bsValues.ValueWriter().beginMap().key("world").string("world").end()), greet);
  bsEvents.eventOnEx("example:ping", bsCore.NORMAL, 0, null, ping);
  bsEvents.eventOnEx("no.such.event", bsCore.NORMAL, 0, null, ping);
}
