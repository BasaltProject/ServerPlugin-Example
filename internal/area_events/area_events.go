package area_events

import (
	bsCore "basaltmc.org/sdk/server/core"
	bsEvents "basaltmc.org/sdk/server/events"
	bsLog "basaltmc.org/sdk/server/log"
	bsValues "basaltmc.org/sdk/server/values"
)

import (
	"strconv"
)

const valueBytes = 512
const listBytes = 8192

func Doc(build func(w *bsValues.ValueWriter)) []byte {
	w := bsValues.NewValueWriter()
	build(w)
	out, _ := w.Finish()
	return out
}

func OnForm(ctx bsCore.Ctx) {
	answer, err := bsEvents.CompletionValue(ctx, valueBytes)
	if err != nil {
		return
	}
	button := int64(-1)
	if b, ok := answer.Find("button"); ok {
		if n, ok := b.AsInt(); ok {
			button = n
		}
	}
	bsLog.LogInfo("form button " + strconv.FormatInt(button, 10))
}

func OnDialog(ctx bsCore.Ctx) {
	answer, err := bsEvents.CompletionValue(ctx, valueBytes)
	if err != nil {
		return
	}
	id := ""
	if i, ok := answer.Find("id"); ok {
		id, _ = i.AsString()
	}
	bsLog.LogInfo("dialog id " + id)
}

func Greet(ctx bsCore.Ctx) {
	player := bsEvents.EventPlayer(ctx)
	bsEvents.EventGet(ctx, "world", valueBytes)
	bsEvents.EventFields(ctx, valueBytes)

	bsEvents.EventDescribe("player.chat", valueBytes)
	bsEvents.EventDescribe("no.such.event", valueBytes)
	bsEvents.EventNames(listBytes)

	uuid, _ := bsEvents.PlayerUuid(player)
	bsEvents.PlayerByUuid(uuid)
	bsEvents.PlayerByUuid("00000000-0000-0000-0000-999999999999")
	bsEvents.PlayerUuid(9999)

	bsEvents.PlayerGet(player, "food", valueBytes)
	bsEvents.PlayerGet(player, "no_such_property", valueBytes)
	food := Doc(func(w *bsValues.ValueWriter) { w.Int(18) })
	bsEvents.PlayerSet(player, "food", food)
	bsEvents.PlayerSet(player, "name", food)
	bsEvents.PlayerDescribe(listBytes)
	none := Doc(func(w *bsValues.ValueWriter) { w.BeginList().End() })
	bsEvents.PlayerCall(player, "reset_idle", none, valueBytes)
	bsEvents.PlayerCall(player, "no_such_operation", none, valueBytes)

	bsEvents.PlayerHealth(player)
	bsEvents.PlayerSetHealth(player, 10.5)
	bsEvents.PlayerSetHealth(player, -1.0)
	bsEvents.PlayerGamemode(player)
	bsEvents.PlayerSetGamemode(player, "creative")
	bsEvents.PlayerSetGamemode(player, "bogus")
	bsEvents.PlayerTeleport(player, "", 10.5, 64.0, -3.25, 90.0, 15.0)
	bsEvents.PlayerTeleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0, 0.0)
	bsEvents.PlayerTitle(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10, nil)
	bsEvents.PlayerTitle(player, "<gold>Welcome", "", -1, 40, 10, nil)
	bsEvents.PlayerEffect(player, "minecraft:speed", 200, 1)
	bsEvents.PlayerEffect(player, "no_such_effect", 200, 1)

	trade := Doc(func(w *bsValues.ValueWriter) { w.BeginMap().Key("entity").Int(1).End() })
	bsEvents.PlayerOpenInventory(player, "trades", trade)
	bsEvents.PlayerOpenInventory(player, "no_such_window", trade)
	bsEvents.PlayerCloseWindow(player)

	form := Doc(func(w *bsValues.ValueWriter) {
		w.BeginMap().Key("title").String("Pick one").Key("buttons").BeginList().String("Yes").String("No").End()
		w.Key("fields").BeginList().BeginMap().Key("key").String("name").Key("type").String("text")
		w.Key("label").String("Name").End().End().End()
	})
	bsEvents.FormOpen(player, form, OnForm)
	broken := Doc(func(w *bsValues.ValueWriter) { w.BeginMap().Key("title").Int(1).End() })
	bsEvents.FormOpen(player, broken, OnForm)
	bsEvents.FormClose(player)
	bsEvents.FormOpen(player, form, OnForm)
	bsEvents.DialogShow(player, "minecraft:confirm", OnDialog)
	bsEvents.DialogClear(player)
	bsEvents.DialogShow(player, "minecraft:confirm", OnDialog)
	bsEvents.PlayerKick(9999, "bye", nil)
}

func Chat(ctx bsCore.Ctx) {
	bsEvents.EventName(ctx)
	text := ""
	if v, err := bsEvents.EventGet(ctx, "message", valueBytes); err == nil {
		text, _ = v.AsString()
	}
	bsEvents.EventGet(ctx, "no_such_field", valueBytes)
	bsEvents.EventCancelled(ctx)
	bsEvents.EventIsPost(ctx)
	bsEvents.EventSet(ctx, "player_name", Doc(func(w *bsValues.ValueWriter) { w.String("x") }))
	if len(text) == 6 && text[0] == 'c' {
		bsEvents.EventCancelWith(ctx, "<red>Not allowed</red>", nil)
		return
	}
	if len(text) == 3 && text[0] == 'b' {
		bsEvents.PlayerKick(bsEvents.EventPlayer(ctx), "<red>Bye</red>", nil)
		return
	}
	if len(text) == 4 && text[0] == 'p' {
		payload := Doc(func(w *bsValues.ValueWriter) { w.BeginMap().Key("from").String("chat").End() })
		bsEvents.EventEmit("example:pong", payload)
		bsEvents.EventEmit("nocolon", payload)
	}
	bsEvents.EventSet(ctx, "message", Doc(func(w *bsValues.ValueWriter) { w.String(text + "!") }))
	bsEvents.EventFields(ctx, valueBytes)
}

func ChatPost(ctx bsCore.Ctx) {
	post := bsEvents.EventIsPost(ctx) == 1
	cancelled := bsEvents.EventCancelled(ctx) == 1
	bsEvents.EventSet(ctx, "message", Doc(func(w *bsValues.ValueWriter) { w.String("late") }))
	bsEvents.EventCancelWith(ctx, "<red>Too late</red>", nil)
	reason, ok := bsEvents.EventCancelMessage(ctx)
	if !ok {
		reason = "no message"
	}
	line := "decision "
	if post {
		line = "post "
	}
	if cancelled {
		line += "cancelled "
	} else {
		line += "delivered "
	}
	bsLog.LogInfo(line + reason)
}

func Ping(ctx bsCore.Ctx) {
	level := int64(0)
	if payload, err := bsEvents.EventPayload(ctx, valueBytes); err == nil {
		if l, ok := payload.Find("level"); ok {
			level, _ = l.AsInt()
		}
	}
	bsLog.LogInfo("ping level " + strconv.FormatInt(level, 10))
}

func Register() {
	bsEvents.EventOnEx("player.chat", bsCore.Normal, 0, nil, Chat)
	bsEvents.EventOnEx("player.chat", bsCore.Highest, bsEvents.EventFlagPost|bsEvents.EventFlagCancelled, nil, ChatPost)
	filters := Doc(func(w *bsValues.ValueWriter) { w.BeginMap().Key("world").String("world").End() })
	bsEvents.EventOnEx("player.join", bsCore.Low, 0, filters, Greet)
	bsEvents.EventOnEx("example:ping", bsCore.Normal, 0, nil, Ping)
	bsEvents.EventOnEx("no.such.event", bsCore.Normal, 0, nil, Ping)
}
