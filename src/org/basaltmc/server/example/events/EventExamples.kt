package org.basaltmc.server.example.events

import org.basaltmc.server.core.Ctx
import org.basaltmc.server.core.Priority
import org.basaltmc.server.events.EVENT_CANCELLED
import org.basaltmc.server.events.EVENT_POST
import org.basaltmc.server.events.completionValue
import org.basaltmc.server.events.dialogClear
import org.basaltmc.server.events.dialogShow
import org.basaltmc.server.events.eventCancelMessage
import org.basaltmc.server.events.eventCancelWith
import org.basaltmc.server.events.eventCancelled
import org.basaltmc.server.events.eventDescribe
import org.basaltmc.server.events.eventEmit
import org.basaltmc.server.events.eventFields
import org.basaltmc.server.events.eventGet
import org.basaltmc.server.events.eventIsPost
import org.basaltmc.server.events.eventName
import org.basaltmc.server.events.eventNames
import org.basaltmc.server.events.eventOnEx
import org.basaltmc.server.events.eventPayload
import org.basaltmc.server.events.eventPlayer
import org.basaltmc.server.events.eventSet
import org.basaltmc.server.events.formClose
import org.basaltmc.server.events.formOpen
import org.basaltmc.server.events.playerByUuid
import org.basaltmc.server.events.playerCall
import org.basaltmc.server.events.playerCloseWindow
import org.basaltmc.server.events.playerDescribe
import org.basaltmc.server.events.playerEffect
import org.basaltmc.server.events.playerGamemode
import org.basaltmc.server.events.playerGet
import org.basaltmc.server.events.playerHealth
import org.basaltmc.server.events.playerKick
import org.basaltmc.server.events.playerOpenInventory
import org.basaltmc.server.events.playerSet
import org.basaltmc.server.events.playerSetGamemode
import org.basaltmc.server.events.playerSetHealth
import org.basaltmc.server.events.playerTeleport
import org.basaltmc.server.events.playerTitle
import org.basaltmc.server.events.playerUuid
import org.basaltmc.server.log.logInfo
import org.basaltmc.server.values.ValueWriter

object EventExamples {
    private const val VALUE_BYTES = 512
    private const val LIST_BYTES = 8192

    private fun doc(build: ValueWriter.() -> Unit): ByteArray {
        val writer = ValueWriter()
        writer.build()
        return writer.finish() ?: ByteArray(0)
    }

    private fun onForm(ctx: Ctx) {
        val answer = completionValue(ctx, VALUE_BYTES) ?: return
        logInfo("form button ${answer.find("button")?.asLong() ?: -1L}")
    }

    private fun onDialog(ctx: Ctx) {
        val answer = completionValue(ctx, VALUE_BYTES) ?: return
        logInfo("dialog id ${answer.find("id")?.asString() ?: ""}")
    }

    private fun greet(ctx: Ctx) {
        val player = eventPlayer(ctx)
        eventGet(ctx, "world", VALUE_BYTES)
        eventFields(ctx, VALUE_BYTES)

        eventDescribe("player.chat", VALUE_BYTES)
        eventDescribe("no.such.event", VALUE_BYTES)
        eventNames(LIST_BYTES)

        playerByUuid(playerUuid(player) ?: "")
        playerByUuid("00000000-0000-0000-0000-999999999999")
        playerUuid(9999)

        playerGet(player, "food", VALUE_BYTES)
        playerGet(player, "no_such_property", VALUE_BYTES)
        val food = doc { int(18) }
        playerSet(player, "food", food)
        playerSet(player, "name", food)
        playerDescribe(LIST_BYTES)
        val none = doc { beginList().end() }
        playerCall(player, "reset_idle", none, VALUE_BYTES)
        playerCall(player, "no_such_operation", none, VALUE_BYTES)

        playerHealth(player)
        playerSetHealth(player, 10.5)
        playerSetHealth(player, -1.0)
        playerGamemode(player)
        playerSetGamemode(player, "creative")
        playerSetGamemode(player, "bogus")
        playerTeleport(player, "", 10.5, 64.0, -3.25, 90.0f, 15.0f)
        playerTeleport(player, "no_such_world", 0.0, 64.0, 0.0, 0.0f, 0.0f)
        playerTitle(player, "<gold>Welcome", "<gray>enjoy", 10, 40, 10)
        playerTitle(player, "<gold>Welcome", "", -1, 40, 10)
        playerEffect(player, "minecraft:speed", 200, 1)
        playerEffect(player, "no_such_effect", 200, 1)

        val trade = doc { beginMap().key("entity").int(1).end() }
        playerOpenInventory(player, "trades", trade)
        playerOpenInventory(player, "no_such_window", trade)
        playerCloseWindow(player)

        val form = doc {
            beginMap().key("title").string("Pick one").key("buttons").beginList().string("Yes").string("No").end()
            key("fields").beginList().beginMap().key("key").string("name").key("type").string("text")
            key("label").string("Name").end().end().end()
        }
        formOpen(player, form, ::onForm)
        formOpen(player, doc { beginMap().key("title").int(1).end() }, ::onForm)
        formClose(player)
        formOpen(player, form, ::onForm)
        dialogShow(player, "minecraft:confirm", ::onDialog)
        dialogClear(player)
        dialogShow(player, "minecraft:confirm", ::onDialog)
        playerKick(9999, "bye")
    }

    private fun chat(ctx: Ctx) {
        eventName(ctx)
        val text = eventGet(ctx, "message", VALUE_BYTES)?.asString() ?: ""
        eventGet(ctx, "no_such_field", VALUE_BYTES)
        eventCancelled(ctx)
        eventIsPost(ctx)
        eventSet(ctx, "player_name", doc { string("x") })
        if (text.length == 6 && text[0] == 'c') {
            eventCancelWith(ctx, "<red>Not allowed</red>")
            return
        }
        if (text.length == 3 && text[0] == 'b') {
            playerKick(eventPlayer(ctx), "<red>Bye</red>")
            return
        }
        if (text.length == 4 && text[0] == 'p') {
            val payload = doc { beginMap().key("from").string("chat").end() }
            eventEmit("example:pong", payload)
            eventEmit("nocolon", payload)
        }
        eventSet(ctx, "message", doc { string("$text!") })
        eventFields(ctx, VALUE_BYTES)
    }

    private fun chatPost(ctx: Ctx) {
        val post = eventIsPost(ctx) == 1
        val cancelled = eventCancelled(ctx) == 1
        eventSet(ctx, "message", doc { string("late") })
        eventCancelWith(ctx, "<red>Too late</red>")
        val reason = eventCancelMessage(ctx) ?: "no message"
        logInfo((if (post) "post " else "decision ") + (if (cancelled) "cancelled " else "delivered ") + reason)
    }

    private fun ping(ctx: Ctx) {
        val level = eventPayload(ctx, VALUE_BYTES)?.find("level")?.asLong() ?: 0L
        logInfo("ping level $level")
    }

    fun register() {
        eventOnEx("player.chat", Priority.NORMAL, 0, null, ::chat)
        eventOnEx("player.chat", Priority.HIGHEST, EVENT_POST or EVENT_CANCELLED, null, ::chatPost)
        eventOnEx("player.join", Priority.LOW, 0, doc { beginMap().key("world").string("world").end() }, ::greet)
        eventOnEx("example:ping", Priority.NORMAL, 0, null, ::ping)
        eventOnEx("no.such.event", Priority.NORMAL, 0, null, ::ping)
    }
}
