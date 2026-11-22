package org.basaltmc.server.example.events


import org.basaltmc.server.core.Ctx
import org.basaltmc.server.values.ValueWriter

object AreaEvents {
    private const val VALUE_BYTES = 512
    private const val LIST_BYTES = 8192

    private fun doc(build: ValueWriter.() -> Unit): ByteArray {
        val writer = ValueWriter()
        writer.build()
        return writer.finish() ?: ByteArray(0)
    }

    private fun onForm(ctx: Ctx) {
        val answer = org.basaltmc.server.events.completionValue(ctx, VALUE_BYTES) ?: return
        org.basaltmc.server.log.logInfo("form button ${answer.find("button")?.asLong() ?: -1L}")
    }

    private fun onDialog(ctx: Ctx) {
        val answer = org.basaltmc.server.events.completionValue(ctx, VALUE_BYTES) ?: return
        org.basaltmc.server.log.logInfo("dialog id ${answer.find("id")?.asString() ?: ""}")
    }

    private fun greet(ctx: Ctx) {
        val player = org.basaltmc.server.events.eventPlayer(ctx)
        org.basaltmc.server.events.eventGet(ctx, "world", VALUE_BYTES)
        org.basaltmc.server.events.eventFields(ctx, VALUE_BYTES)

        org.basaltmc.server.events.eventDescribe("player.chat", VALUE_BYTES)
        org.basaltmc.server.events.eventDescribe("no.such.event", VALUE_BYTES)
        org.basaltmc.server.events.eventNames(LIST_BYTES)

        org.basaltmc.server.events.playerByUuid(org.basaltmc.server.events.playerUuid(player) ?: "")
        org.basaltmc.server.events.playerByUuid("00000000-0000-0000-0000-999999999999")
        org.basaltmc.server.events.playerUuid(9999)

        org.basaltmc.server.events.playerGet(player, "food", VALUE_BYTES)
        org.basaltmc.server.events.playerGet(player, "no_such_property", VALUE_BYTES)
        val food = doc { int(18) }
        org.basaltmc.server.events.playerSet(player, "food", food)
        org.basaltmc.server.events.playerSet(player, "name", food)
        org.basaltmc.server.events.playerDescribe(LIST_BYTES)
        val none = doc { beginList().end() }
        org.basaltmc.server.events.playerCall(player, "reset_idle", none, VALUE_BYTES)
        org.basaltmc.server.events.playerCall(player, "no_such_operation", none, VALUE_BYTES)

        org.basaltmc.server.events.playerHealth(player)
        org.basaltmc.server.events.playerSetHealth(player, 10.5)
        org.basaltmc.server.events.playerSetHealth(player, -1.0)
        org.basaltmc.server.events.playerGamemode(player)
        org.basaltmc.server.events.playerSetGamemode(player, "creative")
        org.basaltmc.server.events.playerSetGamemode(player, "bogus")
        org.basaltmc.server.events.playerTeleport(player, "", 10.5, 64.0, -3.25, 90.0f, 15.0f)
        org.basaltmc.server.events.playerTeleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0f, 0.0f)
        org.basaltmc.server.events.playerTitle(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10)
        org.basaltmc.server.events.playerTitle(player, "<gold>Welcome", "", -1, 40, 10)
        org.basaltmc.server.events.playerEffect(player, "minecraft:speed", 200, 1)
        org.basaltmc.server.events.playerEffect(player, "no_such_effect", 200, 1)

        val trade = doc { beginMap().key("entity").int(1).end() }
        org.basaltmc.server.events.playerOpenInventory(player, "trades", trade)
        org.basaltmc.server.events.playerOpenInventory(player, "no_such_window", trade)
        org.basaltmc.server.events.playerCloseWindow(player)

        val form = doc {
            beginMap().key("title").string("Pick one").key("buttons").beginList().string("Yes").string("No").end()
            key("fields").beginList().beginMap().key("key").string("name").key("type").string("text")
            key("label").string("Name").end().end().end()
        }
        org.basaltmc.server.events.formOpen(player, form, ::onForm)
        org.basaltmc.server.events.formOpen(player, doc { beginMap().key("title").int(1).end() }, ::onForm)
        org.basaltmc.server.events.formClose(player)
        org.basaltmc.server.events.formOpen(player, form, ::onForm)
        org.basaltmc.server.events.dialogShow(player, "minecraft:confirm", ::onDialog)
        org.basaltmc.server.events.dialogClear(player)
        org.basaltmc.server.events.dialogShow(player, "minecraft:confirm", ::onDialog)
        org.basaltmc.server.events.playerKick(9999, "bye")
    }

    private fun chat(ctx: Ctx) {
        org.basaltmc.server.events.eventName(ctx)
        val text = org.basaltmc.server.events.eventGet(ctx, "message", VALUE_BYTES)?.asString() ?: ""
        org.basaltmc.server.events.eventGet(ctx, "no_such_field", VALUE_BYTES)
        org.basaltmc.server.events.eventCancelled(ctx)
        org.basaltmc.server.events.eventIsPost(ctx)
        org.basaltmc.server.events.eventSet(ctx, "player_name", doc { string("x") })
        if (text.length == 6 && text[0] == 'c') {
            org.basaltmc.server.events.eventCancelWith(ctx, "<red>Not allowed</red>")
            return
        }
        if (text.length == 3 && text[0] == 'b') {
            org.basaltmc.server.events.playerKick(org.basaltmc.server.events.eventPlayer(ctx), "<red>Bye</red>")
            return
        }
        if (text.length == 4 && text[0] == 'p') {
            val payload = doc { beginMap().key("from").string("chat").end() }
            org.basaltmc.server.events.eventEmit("example:pong", payload)
            org.basaltmc.server.events.eventEmit("nocolon", payload)
        }
        org.basaltmc.server.events.eventSet(ctx, "message", doc { string("$text!") })
        org.basaltmc.server.events.eventFields(ctx, VALUE_BYTES)
    }

    private fun chatPost(ctx: Ctx) {
        val post = org.basaltmc.server.events.eventIsPost(ctx) == 1
        val cancelled = org.basaltmc.server.events.eventCancelled(ctx) == 1
        org.basaltmc.server.events.eventSet(ctx, "message", doc { string("late") })
        org.basaltmc.server.events.eventCancelWith(ctx, "<red>Too late</red>")
        val reason = org.basaltmc.server.events.eventCancelMessage(ctx) ?: "no message"
        org.basaltmc.server.log.logInfo((if (post) "post " else "decision ") + (if (cancelled) "cancelled " else "delivered ") + reason)
    }

    private fun ping(ctx: Ctx) {
        val level = org.basaltmc.server.events.eventPayload(ctx, VALUE_BYTES)?.find("level")?.asLong() ?: 0L
        org.basaltmc.server.log.logInfo("ping level $level")
    }

    fun register() {
        org.basaltmc.server.events.eventOnEx("player.chat", org.basaltmc.server.core.Priority.NORMAL, 0, null, ::chat)
        org.basaltmc.server.events.eventOnEx("player.chat", org.basaltmc.server.core.Priority.HIGHEST, org.basaltmc.server.events.EVENT_POST or org.basaltmc.server.events.EVENT_CANCELLED, null, ::chatPost)
        org.basaltmc.server.events.eventOnEx("player.join", org.basaltmc.server.core.Priority.LOW, 0, doc { beginMap().key("world").string("world").end() }, ::greet)
        org.basaltmc.server.events.eventOnEx("example:ping", org.basaltmc.server.core.Priority.NORMAL, 0, null, ::ping)
        org.basaltmc.server.events.eventOnEx("no.such.event", org.basaltmc.server.core.Priority.NORMAL, 0, null, ::ping)
    }
}
