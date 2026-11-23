package org.basaltmc.server.example.packets

import org.basaltmc.server.commands.command
import org.basaltmc.server.commands.commandArgStr
import org.basaltmc.server.commands.commandSourceName
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.core.Player
import org.basaltmc.server.log.logInfo
import org.basaltmc.server.packets.channelListen
import org.basaltmc.server.packets.channelSend
import org.basaltmc.server.packets.packetCtxBody
import org.basaltmc.server.packets.packetCtxCancel
import org.basaltmc.server.packets.packetCtxInfo
import org.basaltmc.server.packets.packetCtxPlayer
import org.basaltmc.server.packets.packetCtxReceive
import org.basaltmc.server.packets.packetCtxSend
import org.basaltmc.server.packets.packetCtxSetBody
import org.basaltmc.server.packets.packetListen
import org.basaltmc.server.packets.packetLookup
import org.basaltmc.server.packets.packetNames
import org.basaltmc.server.packets.packetPlayerActions
import org.basaltmc.server.packets.packetPlayerReceive
import org.basaltmc.server.packets.packetReceive
import org.basaltmc.server.packets.packetSend
import org.basaltmc.server.packets.packetUnlisten
import org.basaltmc.server.players.playerByName
import org.basaltmc.server.values.ValueWriter

object ChannelExamples {
    private const val CHANNEL = "example:chan"

    private var commandListener = 0
    private var lastCtx = 0

    private fun bytes(text: String) = text.encodeToByteArray()

    private fun source(ctx: Ctx): Player = playerByName(commandSourceName(ctx) ?: "")

    private fun onChannel(ctx: Ctx) {
        packetCtxInfo(ctx)
        val body = packetCtxBody(ctx).bytes
        channelSend(packetCtxPlayer(ctx), CHANNEL, body)
        packetCtxCancel(ctx)
    }

    private fun onChat(ctx: Ctx) {
        lastCtx = ctx
        val body = packetCtxBody(ctx).bytes
        packetCtxPlayer(ctx)
        if (body.isNotEmpty() && body[0] == '!'.code.toByte()) {
            packetCtxCancel(ctx)
            return
        }
        packetCtxSetBody(ctx, body + '#'.code.toByte())
    }

    private fun onWatch(ctx: Ctx) {
        val name = packetCtxInfo(ctx).value()?.find("name")?.asString()
        if (name != null) logInfo(name)
        packetCtxCancel(ctx)
    }

    private fun onCommandPacket(ctx: Ctx) {
        packetCtxSend(ctx, "clientbound.system_chat", bytes("ok"), false)
        packetCtxReceive(ctx, "serverbound.chat_message", bytes("hi"), true)
        packetCtxSend(ctx, "serverbound.chat_message", bytes("no"), false)
    }

    private fun info() {
        packetNames("serverbound")
        packetNames("")
        packetLookup("serverbound.chat_message")
        packetLookup("clientbound.play.*")
        packetLookup("bogus.name")
        packetPlayerActions()
    }

    private fun send(player: Player) {
        packetSend(player, "clientbound.set_title_text", bytes("hi"), false)
        packetSend(player, "serverbound.chat_message", bytes("x"), false)
        packetSend(player, "clientbound.system_chat", bytes("again"), true)
    }

    private fun receive(player: Player) {
        packetReceive(player, "serverbound.chat_command", bytes("/help"), false)
        packetReceive(player, "clientbound.set_title_text", bytes("x"), false)
    }

    private fun channel(player: Player) {
        channelSend(player, CHANNEL, bytes("ping"))
        channelSend(0, CHANNEL, bytes("ping"))
    }

    private fun action(player: Player) {
        val held = ValueWriter().beginList().int(3).end().finish() ?: return
        packetPlayerReceive(player, "held_slot", held)
        val payload = ValueWriter().beginList().string(CHANNEL).string("pong").end().finish() ?: return
        packetPlayerReceive(player, "custom_payload", payload)
        packetPlayerReceive(player, "bogus", held)
    }

    private fun stale() {
        packetCtxCancel(lastCtx)
        packetCtxInfo(9999)
    }

    private fun unlisten() {
        packetUnlisten(commandListener)
        packetUnlisten(commandListener)
        packetUnlisten(0)
    }

    private fun onCommand(ctx: Ctx) {
        val word = commandArgStr(ctx, "action") ?: ""
        val player = source(ctx)
        when (word) {
            "info" -> info()
            "send" -> send(player)
            "recv" -> receive(player)
            "chan" -> channel(player)
            "action" -> action(player)
            "stale" -> stale()
            "unlisten" -> unlisten()
        }
    }

    fun register() {
        channelListen(CHANNEL, ::onChannel)
        packetListen("serverbound.chat_message", "first", false, ::onChat)
        packetListen("clientbound.*", null, true, ::onWatch)
        packetListen("serverbound.play.*", "late", true, ::onWatch)
        commandListener = packetListen("serverbound.chat_command", null, false, ::onCommandPacket)
        packetListen("nope", null, false, ::onWatch)
        command("pkt <action: word>", "", ::onCommand)
    }
}
