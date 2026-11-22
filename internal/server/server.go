package server

import (
	bsCommands "basaltmc.org/sdk/server/commands"
	bsCore "basaltmc.org/sdk/server/core"
	bsLog "basaltmc.org/sdk/server/log"
	bsPlayers "basaltmc.org/sdk/server/players"
	bsServer "basaltmc.org/sdk/server/server"
	bsValues "basaltmc.org/sdk/server/values"
)

import (
	"strconv"
)

func Say(ctx bsCore.Ctx, text string) {
	bsCommands.CommandReply(ctx, "@server-line", bsCore.NewArgs().Add("text", text))
}

func SayNumber(ctx bsCore.Ctx, label string, value int64) {
	Say(ctx, label+" "+strconv.FormatInt(value, 10))
}

func NoteNumber(label string, value int64) {
	bsLog.LogInfo(label + " " + strconv.FormatInt(value, 10))
}

func Flag(value bool) int64 {
	if value {
		return 1
	}
	return 0
}

func Field(v bsValues.Value, keys ...string) bsValues.Value {
	for _, key := range keys {
		v, _ = v.Find(key)
	}
	return v
}

func IntOr(v bsValues.Value, def int64) int64 {
	if n, ok := v.AsInt(); ok {
		return n
	}
	return def
}

func FloatOr(v bsValues.Value, def float64) float64 {
	if f, ok := v.AsFloat(); ok {
		return f
	}
	if n, ok := v.AsInt(); ok {
		return float64(n)
	}
	return def
}

func BoolOr(v bsValues.Value, def bool) bool {
	if b, ok := v.AsBool(); ok {
		return b
	}
	return def
}

func SourcePlayer(ctx bsCore.Ctx) bsCore.Player {
	source, _ := bsCommands.CommandSourceName(ctx)
	return bsPlayers.PlayerByName(source)
}

func Permissions(ctx bsCore.Ctx) {
	player := SourcePlayer(ctx)
	SayNumber(ctx, "has example.vip", int64(bsServer.PermissionHas(player, "example.vip")))
	SayNumber(ctx, "console has example.vip", int64(bsServer.PermissionCheck("console", "example.vip")))
	SayNumber(ctx, "grant", int64(bsServer.PermissionGrant(player, "example.vip")))
	SayNumber(ctx, "has example.vip", int64(bsServer.PermissionHas(player, "example.vip")))
	SayNumber(ctx, "drop", int64(bsServer.PermissionDrop(player, "example.vip")))
	SayNumber(ctx, "has example.vip", int64(bsServer.PermissionHas(player, "example.vip")))
	meta, _ := bsServer.PermissionMeta("Steve", "prefix")
	Say(ctx, meta)
	SayNumber(ctx, "unknown subject", int64(bsServer.PermissionCheck("Nobody", "example.vip")))
}

func VipProvider(ctx bsCore.Ctx) {
	node, _ := bsServer.PermissionQuery(ctx, "node")
	kind, _ := bsServer.PermissionQuery(ctx, "kind")
	if len(kind) > 0 && kind[0] == 'c' {
		answer := int32(-1)
		if len(node) > 8 && node[8] == 'v' {
			answer = 1
		}
		bsServer.PermissionAnswer(ctx, answer)
	} else {
		bsServer.PermissionAnswerMeta(ctx, "VIP")
	}
}

func Broadcast(ctx bsCore.Ctx) {
	args := bsCore.NewArgs().Add("text", "hello everyone")
	SayNumber(ctx, "reached", int64(bsServer.Broadcast("@server-line", args, bsServer.BroadcastPlayers|bsServer.BroadcastConsole)))
	SayNumber(ctx, "console only", int64(bsServer.Broadcast("plain text", nil, bsServer.BroadcastConsole)))
	SayNumber(ctx, "nobody", int64(bsServer.Broadcast("x", nil, 0)))
	filled, _ := bsServer.PlaceholderFill("%player_name% is here", SourcePlayer(ctx))
	Say(ctx, filled)
}

func Info(ctx bsCore.Ctx) {
	reply, status := bsServer.ServerInfo()
	SayNumber(ctx, "info", int64(status))
	SayNumber(ctx, "online", IntOr(Field(reply, "online"), -1))
	software, _ := Field(reply, "software").AsString()
	Say(ctx, "software "+strconv.Itoa(len(software)))
	SayNumber(ctx, "plugins", int64(Field(reply, "plugins").Count()))
	SayNumber(ctx, "lane", IntOr(Field(reply, "plugin", "lane"), -1))
}

func Admin(ctx bsCore.Ctx) {
	key, _ := bsValues.NewValueWriter().BeginMap().Key("key").String("motd").End().Finish()
	reply, status := bsServer.ServerCall("settings.get", key)
	SayNumber(ctx, "settings.get", int64(status))
	motd, _ := reply.AsString()
	SayNumber(ctx, "motd length", int64(len(motd)))
	reply, status = bsServer.ServerCall("tick.rate", nil)
	SayNumber(ctx, "tick.rate", int64(status))
	SayNumber(ctx, "rate", int64(FloatOr(reply, -1)))
	reply, status = bsServer.ServerCall("ops", nil)
	SayNumber(ctx, "ops", int64(status))
	SayNumber(ctx, "op count", int64(reply.Count()))
	_, status = bsServer.ServerCall("nope.nope", nil)
	SayNumber(ctx, "unknown op", int64(status))
	_, status = bsServer.ServerCall("datapacks.list", nil)
	SayNumber(ctx, "datapacks", int64(status))
}

func Files(ctx bsCore.Ctx) {
	SayNumber(ctx, "write", int64(bsServer.FileWrite("notes/a.txt", "hello")))
	SayNumber(ctx, "exists", Flag(bsServer.FileExists("notes/a.txt")))
	var text [64]byte
	size := bsServer.FileRead("notes/a.txt", text[:len(text)-1])
	if size < 0 {
		size = 0
	}
	Say(ctx, string(text[:size]))
	listing, status := bsServer.FileList("notes")
	SayNumber(ctx, "list", int64(status))
	SayNumber(ctx, "entries", int64(listing.Count()))
	stat, status := bsServer.FileStat("notes/a.txt")
	SayNumber(ctx, "stat", int64(status))
	SayNumber(ctx, "size", IntOr(Field(stat, "size"), -1))
	SayNumber(ctx, "outside", int64(bsServer.FileWrite("secret/a.txt", "x")))
	SayNumber(ctx, "escape", int64(bsServer.FileWrite("notes/../a.txt", "x")))
	var readme [32]byte
	SayNumber(ctx, "packaged", int64(bsServer.FileRead("data/readme.txt", readme[:])))
	SayNumber(ctx, "delete", int64(bsServer.FileDelete("notes/a.txt", false)))
	SayNumber(ctx, "gone", Flag(bsServer.FileExists("notes/a.txt")))
}

func Store(ctx bsCore.Ctx) {
	SayNumber(ctx, "set", int64(bsServer.StoreSetInt("visits", 3)))
	reply, status := bsServer.StoreGet("visits")
	SayNumber(ctx, "get", int64(status))
	SayNumber(ctx, "visits", IntOr(reply, -1))
	_, status = bsServer.StoreGet("nothing")
	SayNumber(ctx, "missing", int64(status))
	doc, _ := bsValues.NewValueWriter().BeginMap().Key("kills").Int(7).Key("tags").BeginList().String("a").String("b").End().End().Finish()
	SayNumber(ctx, "write", int64(bsServer.DataWrite("stats", doc)))
	reply, status = bsServer.DataRead("stats")
	SayNumber(ctx, "read", int64(status))
	SayNumber(ctx, "kills", IntOr(Field(reply, "kills"), -1))
	SayNumber(ctx, "tags", int64(Field(reply, "tags").Count()))
	reply, status = bsServer.ConfigValue("shop")
	SayNumber(ctx, "config", int64(status))
	SayNumber(ctx, "price", IntOr(Field(reply, "sparkles-price"), -1))
	_, status = bsServer.ConfigValue("payout_period")
	SayNumber(ctx, "dashed", int64(status))
}

func EchoService(ctx bsCore.Ctx) {
	call, _ := bsServer.ServerResult(ctx)
	answer, _ := bsValues.NewValueWriter().BeginMap().Key("echo").Int(IntOr(Field(call, "args", "n"), 0) * 2).End().Finish()
	bsServer.ServiceReply(ctx, answer)
}

func EchoDone(ctx bsCore.Ctx) {
	reply, _ := bsServer.ServerResult(ctx)
	NoteNumber("ok", Flag(BoolOr(Field(reply, "ok"), false)))
	NoteNumber("echo", IntOr(Field(reply, "value", "echo"), -1))
}

func MissingDone(ctx bsCore.Ctx) {
	reply, _ := bsServer.ServerResult(ctx)
	NoteNumber("late ok", Flag(BoolOr(Field(reply, "ok"), true)))
}

func Service(ctx bsCore.Ctx) {
	SayNumber(ctx, "has", Flag(bsServer.ServiceHas("example.echo")))
	SayNumber(ctx, "has other", Flag(bsServer.ServiceHas("example.other")))
	args, _ := bsValues.NewValueWriter().BeginMap().Key("n").Int(21).End().Finish()
	SayNumber(ctx, "call", Flag(bsServer.ServiceCall("example.echo", args, 0, EchoDone) > 0))
	SayNumber(ctx, "absent", int64(bsServer.ServiceCall("example.other", args, 0, MissingDone)))
}

var waits int64

func Waited(bsCore.Ctx) {
	waits++
	NoteNumber("waited", waits)
}

func Placed(bsCore.Ctx) {
	waits++
	NoteNumber("placed", waits)
}

func Looked(ctx bsCore.Ctx) {
	reply, _ := bsServer.ServerResult(ctx)
	uuid, _ := Field(reply, "uuid").AsString()
	length := int64(len(uuid))
	if reply.IsNull() {
		length = 0
	}
	NoteNumber("profile", length)
}

func Later(ctx bsCore.Ctx) {
	SayNumber(ctx, "wait", Flag(bsServer.Wait(20, Waited) > 0))
	SayNumber(ctx, "at", Flag(bsServer.At("world", 0, 0, Placed) > 0))
	SayNumber(ctx, "at unknown", int64(bsServer.At("elsewhere", 0, 0, Placed)))
	SayNumber(ctx, "lookup", Flag(bsServer.ProfileLookup("Notch", Looked) > 0))
}

func Dynamic(ctx bsCore.Ctx) {
	bsCommands.CommandReply(ctx, "@server-line", bsCore.NewArgs().Add("text", "dynamic ran"))
	amount, _ := bsServer.CommandArgF64(ctx, "amount")
	loud, _ := bsServer.CommandArgBool(ctx, "loud")
	SayNumber(ctx, "amount", int64(amount*10))
	SayNumber(ctx, "loud", Flag(loud))
}

func DynamicComplete(ctx bsCore.Ctx) {
	bsServer.CompleteAdd(ctx, "alpha")
	bsServer.CompleteAdd(ctx, "beta")
	if typed, _ := bsServer.CompleteInput(ctx); len(typed) > 0 {
		bsServer.CompleteAdd(ctx, typed)
	}
}

func Manage(ctx bsCore.Ctx) {
	const spec = "dyn [amount: float] [loud: bool]"
	SayNumber(ctx, "register", int64(bsServer.CommandEx(spec, "", Dynamic, DynamicComplete)))
	SayNumber(ctx, "again", int64(bsServer.CommandEx(spec, "", Dynamic, nil)))
	SayNumber(ctx, "alias", int64(bsServer.CommandAlias("dyn", "dy")))
	SayNumber(ctx, "alias missing", int64(bsServer.CommandAlias("nothing", "no")))
	SayNumber(ctx, "run", int64(bsServer.CommandRun("srv hello", 0)))
	SayNumber(ctx, "run as player", int64(bsServer.CommandRun("srv hello", SourcePlayer(ctx))))
}

func Unmanage(ctx bsCore.Ctx) {
	SayNumber(ctx, "unregister", int64(bsServer.CommandUnregister("dyn")))
	SayNumber(ctx, "unregister again", int64(bsServer.CommandUnregister("dyn")))
}

func Hello(ctx bsCore.Ctx) { Say(ctx, "hello from the command") }

func Register() {
	bsServer.PermissionProvide(10, bsServer.ProvideCheck|bsServer.ProvideMeta, VipProvider)
	bsServer.ServiceRegister("example.echo", EchoService)
	bsCommands.Command("srv hello", "", Hello)
	bsCommands.Command("srv perms", "", Permissions)
	bsCommands.Command("srv broadcast", "", Broadcast)
	bsCommands.Command("srv info", "", Info)
	bsCommands.Command("srv admin", "", Admin)
	bsCommands.Command("srv files", "", Files)
	bsCommands.Command("srv store", "", Store)
	bsCommands.Command("srv service", "", Service)
	bsCommands.Command("srv later", "", Later)
	bsCommands.Command("srv manage", "", Manage)
	bsCommands.Command("srv unmanage", "", Unmanage)
}
