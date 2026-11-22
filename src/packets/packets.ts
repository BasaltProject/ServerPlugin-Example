import * as bsCommands from "basalt-server/commands";
import * as bsCore from "basalt-server/core";
import * as bsLog from "basalt-server/log";
import * as bsPackets from "basalt-server/packets";
import * as bsPlayers from "basalt-server/players";
import * as bsValues from "basalt-server/values";

const CHANNEL = "example:chan";

let commandListener: i32 = 0;
let lastCtx: i32 = 0;

function bytes(text: string): Uint8Array {
  return Uint8Array.wrap(String.UTF8.encode(text));
}

function source(ctx: bsCore.Ctx): bsCore.Player {
  const name = bsCommands.commandSourceName(ctx);
  return bsPlayers.playerByName(name ? name : "");
}

function onChannel(ctx: bsCore.Ctx): void {
  bsPackets.packetCtxInfo(ctx);
  const body = bsPackets.packetCtxBody(ctx).bytes;
  bsPackets.channelSend(bsPackets.packetCtxPlayer(ctx), CHANNEL, body);
  bsPackets.packetCtxCancel(ctx);
}

function onChat(ctx: bsCore.Ctx): void {
  lastCtx = ctx;
  const body = bsPackets.packetCtxBody(ctx).bytes;
  bsPackets.packetCtxPlayer(ctx);
  if (body.length > 0 && body[0] == 0x21) {
    bsPackets.packetCtxCancel(ctx);
    return;
  }
  const grown = new Uint8Array(body.length + 1);
  grown.set(body);
  grown[body.length] = 0x23;
  bsPackets.packetCtxSetBody(ctx, grown);
}

function onWatch(ctx: bsCore.Ctx): void {
  const root = bsPackets.packetCtxInfo(ctx).value();
  if (root) {
    const field = root.find("name");
    const name = field ? field.toStr() : null;
    if (name) bsLog.logInfo(name);
  }
  bsPackets.packetCtxCancel(ctx);
}

function onCommandPacket(ctx: bsCore.Ctx): void {
  bsPackets.packetCtxSend(ctx, "clientbound.system_chat", bytes("ok"), false);
  bsPackets.packetCtxReceive(ctx, "serverbound.chat_message", bytes("hi"), true);
  bsPackets.packetCtxSend(ctx, "serverbound.chat_message", bytes("no"), false);
}

function info(): void {
  bsPackets.packetNames("serverbound");
  bsPackets.packetNames("");
  bsPackets.packetLookup("serverbound.chat_message");
  bsPackets.packetLookup("clientbound.play.*");
  bsPackets.packetLookup("bogus.name");
  bsPackets.packetPlayerActions();
}

function send(player: bsCore.Player): void {
  bsPackets.packetSend(player, "clientbound.set_title_text", bytes("hi"), false);
  bsPackets.packetSend(player, "serverbound.chat_message", bytes("x"), false);
  bsPackets.packetSend(player, "clientbound.system_chat", bytes("again"), true);
}

function receive(player: bsCore.Player): void {
  bsPackets.packetReceive(player, "serverbound.chat_command", bytes("/help"), false);
  bsPackets.packetReceive(player, "clientbound.set_title_text", bytes("x"), false);
}

function channel(player: bsCore.Player): void {
  bsPackets.channelSend(player, CHANNEL, bytes("ping"));
  bsPackets.channelSend(0, CHANNEL, bytes("ping"));
}

function action(player: bsCore.Player): void {
  const held = new bsValues.ValueWriter().beginList().int(3).end().finish();
  if (held === null) return;
  bsPackets.packetPlayerReceive(player, "held_slot", held);
  const payload = new bsValues.ValueWriter().beginList().string(CHANNEL).string("pong").end().finish();
  if (payload === null) return;
  bsPackets.packetPlayerReceive(player, "custom_payload", payload);
  bsPackets.packetPlayerReceive(player, "bogus", held);
}

function stale(): void {
  bsPackets.packetCtxCancel(lastCtx);
  bsPackets.packetCtxInfo(9999);
}

function unlisten(): void {
  bsPackets.packetUnlisten(commandListener);
  bsPackets.packetUnlisten(commandListener);
  bsPackets.packetUnlisten(0);
}

function onCommand(ctx: bsCore.Ctx): void {
  const found = bsCommands.commandArgStr(ctx, "action");
  const word = found ? found : "";
  const player = source(ctx);
  if (word == "info") info();
  else if (word == "send") send(player);
  else if (word == "recv") receive(player);
  else if (word == "chan") channel(player);
  else if (word == "action") action(player);
  else if (word == "stale") stale();
  else if (word == "unlisten") unlisten();
}

export function register(): void {
  bsPackets.channelListen(CHANNEL, onChannel);
  bsPackets.packetListen("serverbound.chat_message", "first", false, onChat);
  bsPackets.packetListen("clientbound.*", null, true, onWatch);
  bsPackets.packetListen("serverbound.play.*", "late", true, onWatch);
  commandListener = bsPackets.packetListen("serverbound.chat_command", null, false, onCommandPacket);
  bsPackets.packetListen("nope", null, false, onWatch);
  bsCommands.command("pkt <action: word>", "", onCommand);
}
