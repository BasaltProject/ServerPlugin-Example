package packets

import (
	bsCommands "basaltmc.org/sdk/server/commands"
	bsCore "basaltmc.org/sdk/server/core"
	bsLog "basaltmc.org/sdk/server/log"
	bsPackets "basaltmc.org/sdk/server/packets"
	bsPlayers "basaltmc.org/sdk/server/players"
	bsValues "basaltmc.org/sdk/server/values"
)

const (
	channelName = "example:chan"
	capacity    = 1024
)

var (
	commandListener int32
	lastCtx         bsCore.Ctx
)

func PacketsSource(ctx bsCore.Ctx) bsCore.Player {
	name, _ := bsCommands.CommandSourceName(ctx)
	return bsPlayers.PlayerByName(name)
}

func OnChannel(ctx bsCore.Ctx) {
	info := make([]byte, capacity)
	body := make([]byte, capacity)
	bsPackets.PacketCtxInfo(ctx, info)
	size := bsPackets.PacketCtxBody(ctx, body)
	bsPackets.ChannelSend(bsPackets.PacketCtxPlayer(ctx), channelName, body[:size])
	bsPackets.PacketCtxCancel(ctx)
}

func OnChat(ctx bsCore.Ctx) {
	lastCtx = ctx
	body := make([]byte, capacity)
	size := bsPackets.PacketCtxBody(ctx, body)
	bsPackets.PacketCtxPlayer(ctx)
	if size > 0 && body[0] == '!' {
		bsPackets.PacketCtxCancel(ctx)
		return
	}
	body[size] = '#'
	bsPackets.PacketCtxSetBody(ctx, body[:size+1])
}

func OnWatch(ctx bsCore.Ctx) {
	info := make([]byte, capacity)
	size := bsPackets.PacketCtxInfo(ctx, info)
	if root, ok := bsValues.OpenValue(info[:size]); ok {
		if field, ok := root.Find("name"); ok {
			if name, ok := field.AsString(); ok {
				bsLog.LogInfo(name)
			}
		}
	}
	bsPackets.PacketCtxCancel(ctx)
}

func OnCommandPacket(ctx bsCore.Ctx) {
	bsPackets.PacketCtxSend(ctx, "clientbound.system_chat", []byte("ok"), false)
	bsPackets.PacketCtxReceive(ctx, "serverbound.chat_message", []byte("hi"), true)
	bsPackets.PacketCtxSend(ctx, "serverbound.chat_message", []byte("no"), false)
}

func PacketsInfo() {
	out := make([]byte, capacity)
	bsPackets.PacketNames("serverbound", out)
	bsPackets.PacketNames("", out)
	bsPackets.PacketLookup("serverbound.chat_message", out)
	bsPackets.PacketLookup("clientbound.play.*", out)
	bsPackets.PacketLookup("bogus.name", out)
	bsPackets.PacketPlayerActions(out)
}

func PacketsSend(player bsCore.Player) {
	bsPackets.PacketSend(player, "clientbound.set_title_text", []byte("hi"), false)
	bsPackets.PacketSend(player, "serverbound.chat_message", []byte("x"), false)
	bsPackets.PacketSend(player, "clientbound.system_chat", []byte("again"), true)
}

func PacketsReceive(player bsCore.Player) {
	bsPackets.PacketReceive(player, "serverbound.chat_command", []byte("/help"), false)
	bsPackets.PacketReceive(player, "clientbound.set_title_text", []byte("x"), false)
}

func PacketsChannel(player bsCore.Player) {
	bsPackets.ChannelSend(player, channelName, []byte("ping"))
	bsPackets.ChannelSend(0, channelName, []byte("ping"))
}

func PacketsAction(player bsCore.Player) {
	held, _ := bsValues.NewValueWriter().BeginList().Int(3).End().Finish()
	bsPackets.PacketPlayerReceive(player, "held_slot", held)
	payload, _ := bsValues.NewValueWriter().BeginList().String(channelName).String("pong").End().Finish()
	bsPackets.PacketPlayerReceive(player, "custom_payload", payload)
	bsPackets.PacketPlayerReceive(player, "bogus", held)
}

func PacketsStale() {
	out := make([]byte, capacity)
	bsPackets.PacketCtxCancel(lastCtx)
	bsPackets.PacketCtxInfo(9999, out)
}

func PacketsUnlisten() {
	bsPackets.PacketUnlisten(commandListener)
	bsPackets.PacketUnlisten(commandListener)
	bsPackets.PacketUnlisten(0)
}

func OnPacketsCommand(ctx bsCore.Ctx) {
	word, _ := bsCommands.CommandArgStr(ctx, "action")
	player := PacketsSource(ctx)
	switch word {
	case "info":
		PacketsInfo()
	case "send":
		PacketsSend(player)
	case "recv":
		PacketsReceive(player)
	case "chan":
		PacketsChannel(player)
	case "action":
		PacketsAction(player)
	case "stale":
		PacketsStale()
	case "unlisten":
		PacketsUnlisten()
	}
}

func Register() {
	bsPackets.ChannelListen(channelName, OnChannel)
	bsPackets.PacketListen("serverbound.chat_message", "first", false, OnChat)
	bsPackets.PacketListen("clientbound.*", "", true, OnWatch)
	bsPackets.PacketListen("serverbound.play.*", "late", true, OnWatch)
	commandListener = bsPackets.PacketListen("serverbound.chat_command", "", false, OnCommandPacket)
	bsPackets.PacketListen("nope", "", false, OnWatch)
	bsCommands.Command("pkt <action: word>", "", OnPacketsCommand)
}
