package org.basaltmc.server.example.entities

import org.basaltmc.server.commands.command
import org.basaltmc.server.commands.commandReply
import org.basaltmc.server.commands.commandSourceName
import org.basaltmc.server.core.Ctx
import org.basaltmc.server.entities.entityAiAnswer
import org.basaltmc.server.entities.entityAlive
import org.basaltmc.server.entities.entityById
import org.basaltmc.server.entities.entityByUuid
import org.basaltmc.server.entities.entityCall
import org.basaltmc.server.entities.entityDropItem
import org.basaltmc.server.entities.entityEventEntity
import org.basaltmc.server.entities.entityGet
import org.basaltmc.server.entities.entityId
import org.basaltmc.server.entities.entityMechanic
import org.basaltmc.server.entities.entityOfPlayer
import org.basaltmc.server.entities.entityOperations
import org.basaltmc.server.entities.entityPlayer
import org.basaltmc.server.entities.entityPosition
import org.basaltmc.server.entities.entityProperties
import org.basaltmc.server.entities.entityQuery
import org.basaltmc.server.entities.entityRelease
import org.basaltmc.server.entities.entityRemove
import org.basaltmc.server.entities.entityRemoveUuid
import org.basaltmc.server.entities.entityRestore
import org.basaltmc.server.entities.entityRotation
import org.basaltmc.server.entities.entitySet
import org.basaltmc.server.entities.entitySetRotation
import org.basaltmc.server.entities.entitySetVelocity
import org.basaltmc.server.entities.entitySnapshot
import org.basaltmc.server.entities.entitySpawn
import org.basaltmc.server.entities.entitySpawnNbt
import org.basaltmc.server.entities.entitySpawnRules
import org.basaltmc.server.entities.entityTeleport
import org.basaltmc.server.entities.entityTypeAttributes
import org.basaltmc.server.entities.entityTypes
import org.basaltmc.server.entities.entityVelocity
import org.basaltmc.server.players.playerByName
import org.basaltmc.server.values.ValueWriter

object MobExamples {
    private fun tour(ctx: Ctx) {
        val player = playerByName(commandSourceName(ctx) ?: "")
        val me = entityOfPlayer(player)
        entityPlayer(me)
        entityAlive(me)
        entityId(me)
        val buf1 = DoubleArray(3)
        entityPosition(me, buf1)
        val spec2 = ValueWriter().beginMap().key("yaw").float(90.0).key("pitch").float(10.0).end().finish() ?: ByteArray(0)
        entityTeleport(me, 10.5, 64.0, -3.5, spec2)
        val spec3 = ValueWriter().beginMap().key("world").string("nowhere").end().finish() ?: ByteArray(0)
        entityTeleport(me, 1.0, 2.0, 3.0, spec3)
        val buf4 = DoubleArray(2)
        entityRotation(me, buf4)
        entitySetRotation(me, 45.0, 5.0)
        val buf5 = ByteArray(256)
        entityGet(me, "x", buf5)
        val buf6 = ByteArray(256)
        entityGet(me, "nope", buf6)
        val buf7 = ByteArray(256)
        entityGet(me, "uuid", buf7)
        val buf8 = ByteArray(1024)
        entityProperties(me, buf8)
        val buf9 = ByteArray(1024)
        entityOperations(me, buf9)
        val buf10 = ByteArray(2048)
        entityTypes(buf10)
        val buf11 = ByteArray(512)
        entityTypeAttributes("minecraft:pig", buf11)
        val buf12 = ByteArray(512)
        entityTypeAttributes("minecraft:nothing", buf12)
        val spec13 = ValueWriter().beginMap().key("yaw").float(90.0).end().finish() ?: ByteArray(0)
        val pig = entitySpawn("world", "minecraft:pig", 1.0, 64.0, 2.0, spec13)
        entitySpawn("world", "minecraft:nothing", 0.0, 0.0, 0.0, ByteArray(0))
        val zombie = entitySpawn("world", "minecraft:zombie", 3.0, 64.0, 2.0, ByteArray(0))
        val spec14 = ValueWriter().beginMap().key("reason").string("veto").end().finish() ?: ByteArray(0)
        entitySpawn("world", "minecraft:pig", 0.0, 0.0, 0.0, spec14)
        entitySpawn("nowhere", "minecraft:pig", 0.0, 0.0, 0.0, ByteArray(0))
        val pig_id = entityId(pig)
        entityById("world", pig_id)
        entityById("world", 9999)
        entityByUuid("00000000-0000-4000-8000-000000000001")
        entityByUuid("not-a-uuid")
        val spec15 = ValueWriter().beginMap().key("type").string("minecraft:pig").end().finish() ?: ByteArray(0)
        val buf16 = IntArray(8)
        entityQuery("world", spec15, buf16)
        val spec17 = ValueWriter().beginMap().key("x").float(1.0).key("y").float(64.0).key("z").float(2.0).key("radius").float(5.0).key("limit").int(10L).end().finish() ?: ByteArray(0)
        val buf18 = IntArray(8)
        entityQuery("world", spec17, buf18)
        entitySetVelocity(pig, 0.0, 0.5, 0.0)
        val buf19 = DoubleArray(3)
        entityVelocity(pig, buf19)
        val buf20 = DoubleArray(3)
        entityVelocity(me, buf20)
        val spec21 = ValueWriter().bool(true).finish() ?: ByteArray(0)
        entitySet(pig, "glowing", spec21)
        val buf22 = ByteArray(64)
        entityGet(pig, "glowing", buf22)
        val spec23 = ValueWriter().int(1L).finish() ?: ByteArray(0)
        entitySet(pig, "vehicle", spec23)
        val spec24 = ValueWriter().bool(true).finish() ?: ByteArray(0)
        entitySet(pig, "nope", spec24)
        val spec25 = ValueWriter().beginList().float(3.0).end().finish() ?: ByteArray(0)
        val buf26 = ByteArray(64)
        entityCall(pig, "damage", spec25, buf26)
        val spec27 = ValueWriter().beginList().string("minecraft:speed").int(200L).int(1L).end().finish() ?: ByteArray(0)
        val buf28 = ByteArray(64)
        entityCall(pig, "add_effect", spec27, buf28)
        val buf29 = ByteArray(512)
        entityCall(pig, "effects", ByteArray(0), buf29)
        val spec30 = ValueWriter().beginList().string("example:wander").int(3L).string("move").end().finish() ?: ByteArray(0)
        val buf31 = ByteArray(64)
        entityCall(pig, "add_goal", spec30, buf31)
        val spec32 = ValueWriter().beginList().string("example:wander").end().finish() ?: ByteArray(0)
        val buf33 = ByteArray(64)
        entityCall(pig, "has_goal", spec32, buf33)
        val buf34 = ByteArray(512)
        entityCall(pig, "goals", ByteArray(0), buf34)
        val spec35 = ValueWriter().beginList().float(5.0).float(64.0).float(5.0).end().finish() ?: ByteArray(0)
        val buf36 = ByteArray(512)
        entityCall(pig, "find_path", spec35, buf36)
        val buf37 = ByteArray(64)
        entityCall(pig, "bogus", ByteArray(0), buf37)
        val snap = ByteArray(4096)
        val saved = entitySnapshot(pig, snap)
        entityRestore(pig, snap.copyOf(saved))
        val clone = entitySpawnNbt("world", snap.copyOf(saved), "command")
        val spec39 = ValueWriter().beginMap().key("id").string("minecraft:diamond").key("count").int(2L).end().finish() ?: ByteArray(0)
        val drop = entityDropItem("world", spec39, 1.0, 65.0, 2.0, 0.0, 0.2, 0.0)
        val spec40 = ValueWriter().beginMap().key("count").int(1L).end().finish() ?: ByteArray(0)
        entityDropItem("world", spec40, 1.0, 65.0, 2.0, 0.0, 0.0, 0.0)
        val spec41 = ValueWriter().beginMap().key("limits").beginMap().key("monster").int(20L).end().key("allow_animals").bool(false).end().finish() ?: ByteArray(0)
        val buf42 = ByteArray(512)
        entitySpawnRules("world", spec41, buf42)
        val spec43 = ValueWriter().beginMap().key("limits").beginMap().key("monster").int(-1L).end().key("allow_animals").bool(true).end().finish() ?: ByteArray(0)
        val buf44 = ByteArray(512)
        entitySpawnRules("world", spec43, buf44)
        val spec45 = ValueWriter().beginMap().key("distance").float(10.0).end().finish() ?: ByteArray(0)
        val buf46 = ByteArray(64)
        entityMechanic("combat.fall_damage", spec45, buf46)
        val spec47 = ValueWriter().beginMap().key("tick_count").float(10.0).end().finish() ?: ByteArray(0)
        val buf48 = ByteArray(64)
        entityMechanic("physics.projectile_margin", spec47, buf48)
        val spec49 = ValueWriter().beginMap().end().finish() ?: ByteArray(0)
        val buf50 = ByteArray(64)
        entityMechanic("nope", spec49, buf50)
        entityRemove(pig)
        entityAlive(pig)
        val buf51 = DoubleArray(3)
        entityPosition(pig, buf51)
        entityRemove(pig)
        entityRemoveUuid("world", "00000000-0000-4000-8000-0000000000ff")
        entityRemoveUuid("world", "nope")
        entityRemove(zombie)
        entityRemove(clone)
        entityRemove(drop)
        entityRelease(me)
        entityRelease(me)
        commandReply(ctx, "tour done")
    }

    private fun bad(ctx: Ctx) {
        entityAlive(999)
        entityId(0)
        entityPlayer(-5)
        entityByUuid("")
        entityEventEntity(ctx)
        entityAiAnswer(ctx, 1)
        val buf1 = DoubleArray(3)
        entityPosition(7, buf1)
        val spec2 = ValueWriter().beginMap().end().finish() ?: ByteArray(0)
        val buf3 = ByteArray(16)
        entityMechanic("", spec2, buf3)
        commandReply(ctx, "bad done")
    }

    fun register() {
        command("entities tour", "", ::tour)
        command("entities bad", "", ::bad)
    }
}
