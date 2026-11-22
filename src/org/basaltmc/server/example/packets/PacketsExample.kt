package org.basaltmc.server.example.packets


import org.basaltmc.server.core.Ctx
import org.basaltmc.server.core.Player
import org.basaltmc.server.values.ValueWriter

object PacketsExample {
    private const val CHANNEL = "example:chan"

    private var commandListener = 0
    private var lastCtx = 0

    private fun bytes(text: String) = text.encodeToByteArray()

    private fun source(ctx: Ctx): Player = org.basaltmc.server.players.playerByName(org.basaltmc.server.commands.commandSourceName(ctx) ?: "")

    private fun onChannel(ctx: Ctx) {
        org.basaltmc.server.packets.packetCtxInfo(ctx)
        val body = org.basaltmc.server.packets.packetCtxBody(ctx).bytes
        org.basaltmc.server.packets.channelSend(org.basaltmc.server.packets.packetCtxPlayer(ctx), CHANNEL, body)
        org.basaltmc.server.packets.packetCtxCancel(ctx)
    }

    private fun onChat(ctx: Ctx) {
        lastCtx = ctx
        val body = org.basaltmc.server.packets.packetCtxBody(ctx).bytes
        org.basaltmc.server.packets.packetCtxPlayer(ctx)
        if (body.isNotEmpty() && body[0] == '!'.code.toByte()) {
            org.basaltmc.server.packets.packetCtxCancel(ctx)
            return
        }
        org.basaltmc.server.packets.packetCtxSetBody(ctx, body + '#'.code.toByte())
    }

    private fun onWatch(ctx: Ctx) {
        val name = org.basaltmc.server.packets.packetCtxInfo(ctx).value()?.find("name")?.asString()
        if (name != null) org.basaltmc.server.log.logInfo(name)
        org.basaltmc.server.packets.packetCtxCancel(ctx)
    }

    private fun onCommandPacket(ctx: Ctx) {
        org.basaltmc.server.packets.packetCtxSend(ctx, "clientbound.system_chat", bytes("ok"), false)
        org.basaltmc.server.packets.packetCtxReceive(ctx, "serverbound.chat_message", bytes("hi"), true)
        org.basaltmc.server.packets.packetCtxSend(ctx, "serverbound.chat_message", bytes("no"), false)
    }

    private fun info() {
        org.basaltmc.server.packets.packetNames("serverbound")
        org.basaltmc.server.packets.packetNames("")
        org.basaltmc.server.packets.packetLookup("serverbound.chat_message")
        org.basaltmc.server.packets.packetLookup("clientbound.play.*")
        org.basaltmc.server.packets.packetLookup("bogus.name")
        org.basaltmc.server.packets.packetPlayerActions()
    }

    private fun send(player: Player) {
        org.basaltmc.server.packets.packetSend(player, "clientbound.set_title_text", bytes("hi"), false)
        org.basaltmc.server.packets.packetSend(player, "serverbound.chat_message", bytes("x"), false)
        org.basaltmc.server.packets.packetSend(player, "clientbound.system_chat", bytes("again"), true)
    }

    private fun receive(player: Player) {
        org.basaltmc.server.packets.packetReceive(player, "serverbound.chat_command", bytes("/help"), false)
        org.basaltmc.server.packets.packetReceive(player, "clientbound.set_title_text", bytes("x"), false)
    }

    private fun channel(player: Player) {
        org.basaltmc.server.packets.channelSend(player, CHANNEL, bytes("ping"))
        org.basaltmc.server.packets.channelSend(0, CHANNEL, bytes("ping"))
    }

    private fun action(player: Player) {
        val held = ValueWriter().beginList().int(3).end().finish() ?: return
        org.basaltmc.server.packets.packetPlayerReceive(player, "held_slot", held)
        val payload = ValueWriter().beginList().string(CHANNEL).string("pong").end().finish() ?: return
        org.basaltmc.server.packets.packetPlayerReceive(player, "custom_payload", payload)
        org.basaltmc.server.packets.packetPlayerReceive(player, "bogus", held)
    }

    private fun stale() {
        org.basaltmc.server.packets.packetCtxCancel(lastCtx)
        org.basaltmc.server.packets.packetCtxInfo(9999)
    }

    private fun unlisten() {
        org.basaltmc.server.packets.packetUnlisten(commandListener)
        org.basaltmc.server.packets.packetUnlisten(commandListener)
        org.basaltmc.server.packets.packetUnlisten(0)
    }

    private fun onCommand(ctx: Ctx) {
        val word = org.basaltmc.server.commands.commandArgStr(ctx, "action") ?: ""
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
        org.basaltmc.server.packets.channelListen(CHANNEL, ::onChannel)
        org.basaltmc.server.packets.packetListen("serverbound.chat_message", "first", false, ::onChat)
        org.basaltmc.server.packets.packetListen("clientbound.*", null, true, ::onWatch)
        org.basaltmc.server.packets.packetListen("serverbound.play.*", "late", true, ::onWatch)
        commandListener = org.basaltmc.server.packets.packetListen("serverbound.chat_command", null, false, ::onCommandPacket)
        org.basaltmc.server.packets.packetListen("nope", null, false, ::onWatch)
        org.basaltmc.server.commands.command("pkt <action: word>", "", ::onCommand)
    }
}
