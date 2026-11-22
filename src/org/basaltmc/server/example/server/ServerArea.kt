package org.basaltmc.server.example.server


import org.basaltmc.server.core.Args
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.core.Player
import org.basaltmc.server.values.ValueWriter

object ServerArea {
    private var waits = 0

    private fun say(ctx: Ctx, text: String) {
        org.basaltmc.server.commands.commandReply(ctx, "@server-line", Args().add("text", text))
    }

    private fun sayNumber(ctx: Ctx, label: String, value: Long) = say(ctx, "$label $value")

    private fun sayNumber(ctx: Ctx, label: String, value: Int) = say(ctx, "$label $value")

    private fun noteNumber(label: String, value: Long) = org.basaltmc.server.log.logInfo("$label $value")

    private fun sourcePlayer(ctx: Ctx): Player = org.basaltmc.server.players.playerByName(org.basaltmc.server.commands.commandSourceName(ctx) ?: "")

    private fun flag(value: Boolean) = if (value) 1 else 0

    private fun permissions(ctx: Ctx) {
        val player = sourcePlayer(ctx)
        sayNumber(ctx, "has example.vip", org.basaltmc.server.server.permissionHas(player, "example.vip"))
        sayNumber(ctx, "console has example.vip", org.basaltmc.server.server.permissionCheck("console", "example.vip"))
        sayNumber(ctx, "grant", org.basaltmc.server.server.permissionGrant(player, "example.vip"))
        sayNumber(ctx, "has example.vip", org.basaltmc.server.server.permissionHas(player, "example.vip"))
        sayNumber(ctx, "drop", org.basaltmc.server.server.permissionDrop(player, "example.vip"))
        sayNumber(ctx, "has example.vip", org.basaltmc.server.server.permissionHas(player, "example.vip"))
        say(ctx, org.basaltmc.server.server.permissionMeta("Steve", "prefix") ?: "")
        sayNumber(ctx, "unknown subject", org.basaltmc.server.server.permissionCheck("Nobody", "example.vip"))
    }

    private fun vipProvider(ctx: Ctx) {
        val node = org.basaltmc.server.server.permissionQuery(ctx, "node") ?: ""
        val kind = org.basaltmc.server.server.permissionQuery(ctx, "kind") ?: ""
        if (kind.startsWith("c")) {
            org.basaltmc.server.server.permissionAnswer(ctx, if (node.getOrNull(8) == 'v') 1 else -1)
        } else {
            org.basaltmc.server.server.permissionAnswerMeta(ctx, "VIP")
        }
    }

    private fun broadcast(ctx: Ctx) {
        sayNumber(
            ctx, "reached",
            org.basaltmc.server.server.broadcast("@server-line", Args().add("text", "hello everyone"), org.basaltmc.server.server.BROADCAST_PLAYERS or org.basaltmc.server.server.BROADCAST_CONSOLE)
        )
        sayNumber(ctx, "console only", org.basaltmc.server.server.broadcast("plain text", null, org.basaltmc.server.server.BROADCAST_CONSOLE))
        sayNumber(ctx, "nobody", org.basaltmc.server.server.broadcast("x", null, 0))
        say(ctx, org.basaltmc.server.server.placeholderFill("%player_name% is here", sourcePlayer(ctx)) ?: "")
    }

    private fun info(ctx: Ctx) {
        val reply = org.basaltmc.server.server.serverInfo()
        sayNumber(ctx, "info", reply.error)
        val value = reply.value()
        sayNumber(ctx, "online", value?.find("online")?.asLong() ?: -1L)
        say(ctx, "software ${value?.find("software")?.asStringBytes()?.size ?: 0}")
        sayNumber(ctx, "plugins", value?.find("plugins")?.count() ?: -1)
        sayNumber(ctx, "lane", value?.find("plugin")?.find("lane")?.asLong() ?: -1L)
    }

    private fun admin(ctx: Ctx) {
        val key = ValueWriter().beginMap().key("key").string("motd").end()
        var reply = org.basaltmc.server.server.serverCall("settings.get", key)
        sayNumber(ctx, "settings.get", reply.error)
        say(ctx, "motd length ${reply.value()?.asStringBytes()?.size ?: 0}")
        reply = org.basaltmc.server.server.serverCall("tick.rate")
        sayNumber(ctx, "tick.rate", reply.error)
        sayNumber(ctx, "rate", (reply.value()?.asDouble() ?: -1.0).toLong())
        reply = org.basaltmc.server.server.serverCall("ops")
        sayNumber(ctx, "ops", reply.error)
        sayNumber(ctx, "op count", reply.value()?.count() ?: -1)
        sayNumber(ctx, "unknown op", org.basaltmc.server.server.serverCall("nope.nope").error)
        sayNumber(ctx, "datapacks", org.basaltmc.server.server.serverCall("datapacks.list").error)
    }

    private fun files(ctx: Ctx) {
        sayNumber(ctx, "write", org.basaltmc.server.server.fileWrite("notes/a.txt", "hello"))
        sayNumber(ctx, "exists", flag(org.basaltmc.server.server.fileExists("notes/a.txt")))
        val text = org.basaltmc.server.server.fileRead("notes/a.txt", 63)
        say(ctx, if (text.error == 0) text.bytes.decodeToString() else "")
        val listing = org.basaltmc.server.server.fileList("notes")
        sayNumber(ctx, "list", listing.error)
        sayNumber(ctx, "entries", listing.value()?.count() ?: -1)
        val stat = org.basaltmc.server.server.fileStat("notes/a.txt")
        sayNumber(ctx, "stat", stat.error)
        sayNumber(ctx, "size", stat.value()?.find("size")?.asLong() ?: -1L)
        sayNumber(ctx, "outside", org.basaltmc.server.server.fileWrite("secret/a.txt", "x"))
        sayNumber(ctx, "escape", org.basaltmc.server.server.fileWrite("notes/../a.txt", "x"))
        val readme = org.basaltmc.server.server.fileRead("data/readme.txt", 32)
        sayNumber(ctx, "packaged", if (readme.error == 0) readme.bytes.size else readme.error)
        sayNumber(ctx, "delete", org.basaltmc.server.server.fileDelete("notes/a.txt"))
        sayNumber(ctx, "gone", flag(org.basaltmc.server.server.fileExists("notes/a.txt")))
    }

    private fun store(ctx: Ctx) {
        sayNumber(ctx, "set", org.basaltmc.server.server.storeSet("visits", 3L))
        var reply = org.basaltmc.server.server.storeGet("visits")
        sayNumber(ctx, "get", reply.error)
        sayNumber(ctx, "visits", reply.value()?.asLong() ?: -1L)
        sayNumber(ctx, "missing", org.basaltmc.server.server.storeGet("nothing").error)
        val doc = ValueWriter().beginMap().key("kills").int(7).key("tags").beginList().string("a").string("b").end().end()
        sayNumber(ctx, "write", org.basaltmc.server.server.dataWrite("stats", doc))
        reply = org.basaltmc.server.server.dataRead("stats")
        sayNumber(ctx, "read", reply.error)
        sayNumber(ctx, "kills", reply.value()?.find("kills")?.asLong() ?: -1L)
        sayNumber(ctx, "tags", reply.value()?.find("tags")?.count() ?: -1)
        reply = org.basaltmc.server.server.configValue("shop")
        sayNumber(ctx, "config", reply.error)
        sayNumber(ctx, "price", reply.value()?.find("sparkles-price")?.asLong() ?: -1L)
        sayNumber(ctx, "dashed", org.basaltmc.server.server.configValue("payout_period").error)
    }

    private fun echoService(ctx: Ctx) {
        val call = org.basaltmc.server.server.valueResult(ctx).value()
        val n = call?.find("args")?.find("n")?.asLong() ?: 0L
        org.basaltmc.server.server.serviceReply(ctx, ValueWriter().beginMap().key("echo").int(n * 2).end())
    }

    private fun echoDone(ctx: Ctx) {
        val reply = org.basaltmc.server.server.valueResult(ctx).value()
        noteNumber("ok", flag(reply?.find("ok")?.asBool() ?: false).toLong())
        noteNumber("echo", reply?.find("value")?.find("echo")?.asLong() ?: -1L)
    }

    private fun missingDone(ctx: Ctx) {
        val reply = org.basaltmc.server.server.valueResult(ctx).value()
        noteNumber("late ok", flag(reply?.find("ok")?.asBool() ?: true).toLong())
    }

    private fun service(ctx: Ctx) {
        sayNumber(ctx, "has", flag(org.basaltmc.server.server.serviceHas("example.echo")))
        sayNumber(ctx, "has other", flag(org.basaltmc.server.server.serviceHas("example.other")))
        val args = ValueWriter().beginMap().key("n").int(21).end()
        sayNumber(ctx, "call", flag(org.basaltmc.server.server.serviceCall("example.echo", args, 0, ::echoDone) > 0))
        sayNumber(ctx, "absent", org.basaltmc.server.server.serviceCall("example.other", args, 0, ::missingDone))
    }

    private fun waited(ctx: Ctx) = noteNumber("waited", (++waits).toLong())

    private fun placed(ctx: Ctx) = noteNumber("placed", (++waits).toLong())

    private fun looked(ctx: Ctx) {
        val reply = org.basaltmc.server.server.valueResult(ctx).value()
        val length = reply?.find("uuid")?.asStringBytes()?.size ?: 0
        noteNumber("profile", if (reply == null || reply.isNull) 0L else length.toLong())
    }

    private fun later(ctx: Ctx) {
        sayNumber(ctx, "wait", flag(org.basaltmc.server.server.taskWait(20, ::waited) > 0))
        sayNumber(ctx, "at", flag(org.basaltmc.server.server.taskAt("world", 0.0, 0.0, ::placed) > 0))
        sayNumber(ctx, "at unknown", org.basaltmc.server.server.taskAt("elsewhere", 0.0, 0.0, ::placed))
        sayNumber(ctx, "lookup", flag(org.basaltmc.server.server.profileLookup("Notch", ::looked) > 0))
    }

    private fun dynamic(ctx: Ctx) {
        say(ctx, "dynamic ran")
        val amount = org.basaltmc.server.server.commandArgF64(ctx, "amount") ?: 0.0
        val loud = org.basaltmc.server.server.commandArgBool(ctx, "loud") ?: false
        sayNumber(ctx, "amount", (amount * 10).toLong())
        sayNumber(ctx, "loud", flag(loud))
    }

    private fun dynamicComplete(ctx: Ctx) {
        org.basaltmc.server.server.completeAdd(ctx, "alpha")
        org.basaltmc.server.server.completeAdd(ctx, "beta")
        val typed = org.basaltmc.server.server.completeInput(ctx) ?: ""
        if (typed.isNotEmpty()) org.basaltmc.server.server.completeAdd(ctx, typed)
    }

    private fun manage(ctx: Ctx) {
        val spec = "dyn [amount: float] [loud: bool]"
        sayNumber(ctx, "register", org.basaltmc.server.server.command(spec, "", ::dynamic, ::dynamicComplete))
        sayNumber(ctx, "again", org.basaltmc.server.server.command(spec, "", ::dynamic))
        sayNumber(ctx, "alias", org.basaltmc.server.server.commandAlias("dyn", "dy"))
        sayNumber(ctx, "alias missing", org.basaltmc.server.server.commandAlias("nothing", "no"))
        sayNumber(ctx, "run", org.basaltmc.server.server.commandRun("srv hello", 0))
        sayNumber(ctx, "run as player", org.basaltmc.server.server.commandRun("srv hello", sourcePlayer(ctx)))
    }

    private fun unmanage(ctx: Ctx) {
        sayNumber(ctx, "unregister", org.basaltmc.server.server.commandUnregister("dyn"))
        sayNumber(ctx, "unregister again", org.basaltmc.server.server.commandUnregister("dyn"))
    }

    private fun hello(ctx: Ctx) = say(ctx, "hello from the command")

    fun register() {
        org.basaltmc.server.server.permissionProvide(10, org.basaltmc.server.server.PROVIDE_CHECK or org.basaltmc.server.server.PROVIDE_META, ::vipProvider)
        org.basaltmc.server.server.serviceRegister("example.echo", ::echoService)
        org.basaltmc.server.commands.command("srv hello", "", ::hello)
        org.basaltmc.server.commands.command("srv perms", "", ::permissions)
        org.basaltmc.server.commands.command("srv broadcast", "", ::broadcast)
        org.basaltmc.server.commands.command("srv info", "", ::info)
        org.basaltmc.server.commands.command("srv admin", "", ::admin)
        org.basaltmc.server.commands.command("srv files", "", ::files)
        org.basaltmc.server.commands.command("srv store", "", ::store)
        org.basaltmc.server.commands.command("srv service", "", ::service)
        org.basaltmc.server.commands.command("srv later", "", ::later)
        org.basaltmc.server.commands.command("srv manage", "", ::manage)
        org.basaltmc.server.commands.command("srv unmanage", "", ::unmanage)
    }
}
