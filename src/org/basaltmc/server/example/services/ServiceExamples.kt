package org.basaltmc.server.example.services

import org.basaltmc.server.commands.command
import org.basaltmc.server.commands.commandReply
import org.basaltmc.server.commands.commandSourceName
import org.basaltmc.server.core.Args
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.core.Player
import org.basaltmc.server.log.logInfo
import org.basaltmc.server.players.playerByName
import org.basaltmc.server.services.BROADCAST_CONSOLE
import org.basaltmc.server.services.BROADCAST_PLAYERS
import org.basaltmc.server.services.PROVIDE_CHECK
import org.basaltmc.server.services.PROVIDE_META
import org.basaltmc.server.services.broadcast as servicesBroadcast
import org.basaltmc.server.services.command as registerCommandWithCompletion
import org.basaltmc.server.services.commandAlias
import org.basaltmc.server.services.commandArgBool
import org.basaltmc.server.services.commandArgF64
import org.basaltmc.server.services.commandRun
import org.basaltmc.server.services.commandUnregister
import org.basaltmc.server.services.completeAdd
import org.basaltmc.server.services.completeInput
import org.basaltmc.server.services.configValue
import org.basaltmc.server.services.dataRead
import org.basaltmc.server.services.dataWrite
import org.basaltmc.server.services.fileDelete
import org.basaltmc.server.services.fileExists
import org.basaltmc.server.services.fileList
import org.basaltmc.server.services.fileRead
import org.basaltmc.server.services.fileStat
import org.basaltmc.server.services.fileWrite
import org.basaltmc.server.services.permissionAnswer
import org.basaltmc.server.services.permissionAnswerMeta
import org.basaltmc.server.services.permissionCheck
import org.basaltmc.server.services.permissionDrop
import org.basaltmc.server.services.permissionGrant
import org.basaltmc.server.services.permissionHas
import org.basaltmc.server.services.permissionMeta
import org.basaltmc.server.services.permissionProvide
import org.basaltmc.server.services.permissionQuery
import org.basaltmc.server.services.placeholderFill
import org.basaltmc.server.services.profileLookup
import org.basaltmc.server.services.serverCall
import org.basaltmc.server.services.serverInfo
import org.basaltmc.server.services.serviceCall
import org.basaltmc.server.services.serviceHas
import org.basaltmc.server.services.serviceRegister
import org.basaltmc.server.services.serviceReply
import org.basaltmc.server.services.storeGet
import org.basaltmc.server.services.storeSet
import org.basaltmc.server.services.taskAt
import org.basaltmc.server.services.taskWait
import org.basaltmc.server.services.valueResult
import org.basaltmc.server.values.ValueWriter

object ServiceExamples {
    private var waits = 0

    private fun say(ctx: Ctx, text: String) {
        commandReply(ctx, "@server-line", Args().add("text", text))
    }

    private fun sayNumber(ctx: Ctx, label: String, value: Long) = say(ctx, "$label $value")

    private fun sayNumber(ctx: Ctx, label: String, value: Int) = say(ctx, "$label $value")

    private fun noteNumber(label: String, value: Long) = logInfo("$label $value")

    private fun sourcePlayer(ctx: Ctx): Player = playerByName(commandSourceName(ctx) ?: "")

    private fun flag(value: Boolean) = if (value) 1 else 0

    private fun permissions(ctx: Ctx) {
        val player = sourcePlayer(ctx)
        sayNumber(ctx, "has example.vip", permissionHas(player, "example.vip"))
        sayNumber(ctx, "console has example.vip", permissionCheck("console", "example.vip"))
        sayNumber(ctx, "grant", permissionGrant(player, "example.vip"))
        sayNumber(ctx, "has example.vip", permissionHas(player, "example.vip"))
        sayNumber(ctx, "drop", permissionDrop(player, "example.vip"))
        sayNumber(ctx, "has example.vip", permissionHas(player, "example.vip"))
        say(ctx, permissionMeta("Steve", "prefix") ?: "")
        sayNumber(ctx, "unknown subject", permissionCheck("Nobody", "example.vip"))
    }

    private fun vipProvider(ctx: Ctx) {
        val node = permissionQuery(ctx, "node") ?: ""
        val kind = permissionQuery(ctx, "kind") ?: ""
        if (kind.startsWith("c")) {
            permissionAnswer(ctx, if (node.getOrNull(8) == 'v') 1 else -1)
        } else {
            permissionAnswerMeta(ctx, "VIP")
        }
    }

    private fun broadcast(ctx: Ctx) {
        sayNumber(
            ctx, "reached",
            servicesBroadcast("@server-line", Args().add("text", "hello everyone"), BROADCAST_PLAYERS or BROADCAST_CONSOLE)
        )
        sayNumber(ctx, "console only", servicesBroadcast("plain text", null, BROADCAST_CONSOLE))
        sayNumber(ctx, "nobody", servicesBroadcast("x", null, 0))
        say(ctx, placeholderFill("%player_name% is here", sourcePlayer(ctx)) ?: "")
    }

    private fun info(ctx: Ctx) {
        val reply = serverInfo()
        sayNumber(ctx, "info", reply.error)
        val value = reply.value()
        sayNumber(ctx, "online", value?.find("online")?.asLong() ?: -1L)
        say(ctx, "software ${value?.find("software")?.asStringBytes()?.size ?: 0}")
        sayNumber(ctx, "plugins", value?.find("plugins")?.count() ?: -1)
        sayNumber(ctx, "lane", value?.find("plugin")?.find("lane")?.asLong() ?: -1L)
    }

    private fun admin(ctx: Ctx) {
        val key = ValueWriter().beginMap().key("key").string("motd").end()
        var reply = serverCall("settings.get", key)
        sayNumber(ctx, "settings.get", reply.error)
        say(ctx, "motd length ${reply.value()?.asStringBytes()?.size ?: 0}")
        reply = serverCall("tick.rate")
        sayNumber(ctx, "tick.rate", reply.error)
        sayNumber(ctx, "rate", (reply.value()?.asDouble() ?: -1.0).toLong())
        reply = serverCall("ops")
        sayNumber(ctx, "ops", reply.error)
        sayNumber(ctx, "op count", reply.value()?.count() ?: -1)
        sayNumber(ctx, "unknown op", serverCall("nope.nope").error)
        sayNumber(ctx, "datapacks", serverCall("datapacks.list").error)
    }

    private fun files(ctx: Ctx) {
        sayNumber(ctx, "write", fileWrite("notes/a.txt", "hello"))
        sayNumber(ctx, "exists", flag(fileExists("notes/a.txt")))
        val text = fileRead("notes/a.txt", 63)
        say(ctx, if (text.error == 0) text.bytes.decodeToString() else "")
        val listing = fileList("notes")
        sayNumber(ctx, "list", listing.error)
        sayNumber(ctx, "entries", listing.value()?.count() ?: -1)
        val stat = fileStat("notes/a.txt")
        sayNumber(ctx, "stat", stat.error)
        sayNumber(ctx, "size", stat.value()?.find("size")?.asLong() ?: -1L)
        sayNumber(ctx, "outside", fileWrite("secret/a.txt", "x"))
        sayNumber(ctx, "escape", fileWrite("notes/../a.txt", "x"))
        val readme = fileRead("data/readme.txt", 32)
        sayNumber(ctx, "packaged", if (readme.error == 0) readme.bytes.size else readme.error)
        sayNumber(ctx, "delete", fileDelete("notes/a.txt"))
        sayNumber(ctx, "gone", flag(fileExists("notes/a.txt")))
    }

    private fun store(ctx: Ctx) {
        sayNumber(ctx, "set", storeSet("visits", 3L))
        var reply = storeGet("visits")
        sayNumber(ctx, "get", reply.error)
        sayNumber(ctx, "visits", reply.value()?.asLong() ?: -1L)
        sayNumber(ctx, "missing", storeGet("nothing").error)
        val doc = ValueWriter().beginMap().key("kills").int(7).key("tags").beginList().string("a").string("b").end().end()
        sayNumber(ctx, "write", dataWrite("stats", doc))
        reply = dataRead("stats")
        sayNumber(ctx, "read", reply.error)
        sayNumber(ctx, "kills", reply.value()?.find("kills")?.asLong() ?: -1L)
        sayNumber(ctx, "tags", reply.value()?.find("tags")?.count() ?: -1)
        reply = configValue("shop")
        sayNumber(ctx, "config", reply.error)
        sayNumber(ctx, "price", reply.value()?.find("sparkles-price")?.asLong() ?: -1L)
        sayNumber(ctx, "dashed", configValue("payout_period").error)
    }

    private fun echoService(ctx: Ctx) {
        val call = valueResult(ctx).value()
        val n = call?.find("args")?.find("n")?.asLong() ?: 0L
        serviceReply(ctx, ValueWriter().beginMap().key("echo").int(n * 2).end())
    }

    private fun echoDone(ctx: Ctx) {
        val reply = valueResult(ctx).value()
        noteNumber("ok", flag(reply?.find("ok")?.asBool() ?: false).toLong())
        noteNumber("echo", reply?.find("value")?.find("echo")?.asLong() ?: -1L)
    }

    private fun missingDone(ctx: Ctx) {
        val reply = valueResult(ctx).value()
        noteNumber("late ok", flag(reply?.find("ok")?.asBool() ?: true).toLong())
    }

    private fun service(ctx: Ctx) {
        sayNumber(ctx, "has", flag(serviceHas("example.echo")))
        sayNumber(ctx, "has other", flag(serviceHas("example.other")))
        val args = ValueWriter().beginMap().key("n").int(21).end()
        sayNumber(ctx, "call", flag(serviceCall("example.echo", args, 0, ::echoDone) > 0))
        sayNumber(ctx, "absent", serviceCall("example.other", args, 0, ::missingDone))
    }

    private fun waited(ctx: Ctx) = noteNumber("waited", (++waits).toLong())

    private fun placed(ctx: Ctx) = noteNumber("placed", (++waits).toLong())

    private fun looked(ctx: Ctx) {
        val reply = valueResult(ctx).value()
        val length = reply?.find("uuid")?.asStringBytes()?.size ?: 0
        noteNumber("profile", if (reply == null || reply.isNull) 0L else length.toLong())
    }

    private fun later(ctx: Ctx) {
        sayNumber(ctx, "wait", flag(taskWait(20, ::waited) > 0))
        sayNumber(ctx, "at", flag(taskAt("world", 0.0, 0.0, ::placed) > 0))
        sayNumber(ctx, "at unknown", taskAt("elsewhere", 0.0, 0.0, ::placed))
        sayNumber(ctx, "lookup", flag(profileLookup("Notch", ::looked) > 0))
    }

    private fun dynamic(ctx: Ctx) {
        say(ctx, "dynamic ran")
        val amount = commandArgF64(ctx, "amount") ?: 0.0
        val loud = commandArgBool(ctx, "loud") ?: false
        sayNumber(ctx, "amount", (amount * 10).toLong())
        sayNumber(ctx, "loud", flag(loud))
    }

    private fun dynamicComplete(ctx: Ctx) {
        completeAdd(ctx, "alpha")
        completeAdd(ctx, "beta")
        val typed = completeInput(ctx) ?: ""
        if (typed.isNotEmpty()) completeAdd(ctx, typed)
    }

    private fun manage(ctx: Ctx) {
        val spec = "dyn [amount: float] [loud: bool]"
        sayNumber(ctx, "register", registerCommandWithCompletion(spec, "", ::dynamic, ::dynamicComplete))
        sayNumber(ctx, "again", registerCommandWithCompletion(spec, "", ::dynamic))
        sayNumber(ctx, "alias", commandAlias("dyn", "dy"))
        sayNumber(ctx, "alias missing", commandAlias("nothing", "no"))
        sayNumber(ctx, "run", commandRun("srv hello", 0))
        sayNumber(ctx, "run as player", commandRun("srv hello", sourcePlayer(ctx)))
    }

    private fun unmanage(ctx: Ctx) {
        sayNumber(ctx, "unregister", commandUnregister("dyn"))
        sayNumber(ctx, "unregister again", commandUnregister("dyn"))
    }

    private fun hello(ctx: Ctx) = say(ctx, "hello from the command")

    fun register() {
        permissionProvide(10, PROVIDE_CHECK or PROVIDE_META, ::vipProvider)
        serviceRegister("example.echo", ::echoService)
        command("srv hello", "", ::hello)
        command("srv perms", "", ::permissions)
        command("srv broadcast", "", ::broadcast)
        command("srv info", "", ::info)
        command("srv admin", "", ::admin)
        command("srv files", "", ::files)
        command("srv store", "", ::store)
        command("srv service", "", ::service)
        command("srv later", "", ::later)
        command("srv manage", "", ::manage)
        command("srv unmanage", "", ::unmanage)
    }
}
