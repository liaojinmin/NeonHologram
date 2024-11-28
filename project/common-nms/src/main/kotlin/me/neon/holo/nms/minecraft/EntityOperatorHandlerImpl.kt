package me.neon.holo.nms.minecraft

import me.neon.holo.nms.PacketHandler
import me.neon.holo.nms.agent.*

import me.neon.holo.nms.n120.NMS120
import me.neon.libs.taboolib.nms.DataSerializer
import me.neon.libs.taboolib.nms.dataSerializerBuilder
import org.bukkit.entity.Player

import org.bukkit.inventory.EquipmentSlot

import taboolib.common.util.Location
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.sendPacket

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2024/11/27 15:23
 */
class EntityOperatorHandlerImpl: EntityOperatorHandler {

    private val isUniversal = MinecraftVersion.isUniversal
    private val major = MinecraftVersion.major
    private val majorLegacy = MinecraftVersion.majorLegacy

    private fun sendPacket(player: List<Player>, packet: Any) {
        player.forEach { it.sendPacket(packet) }
    }

    override fun destroyEntity(player: List<Player>, entityId: Int) {
        sendPacket(player, NMSPacketPlayOutEntityDestroy(entityId))
    }

    override fun teleportEntity(player: List<Player>, entityId: Int, location: Location, onGround: Boolean) {
        // 计算视角
        val yaw = (location.yaw * 256 / 360).toInt().toByte()
        val pitch = (location.pitch * 256 / 360).toInt().toByte()
        val dataSerializer: DataSerializer = dataSerializerBuilder {
            writeVarInt(entityId)
            writeDouble(location.x)
            writeDouble(location.y)
            writeDouble(location.z)
            writeByte(yaw)
            writeByte(pitch)
            writeBoolean(onGround)
        }
        // 版本判断
        val packet: Any = when (major) {
            // 1.9, 1.10, 1.11, 1.12, 1.13, 1.14, 1.15, 1.16
            in 1..8 -> NMS9PacketPlayOutEntityTeleport().also {
                it.a(dataSerializer.build() as NMS9PacketDataSerializer)
            }
            // 1.17, 1.18, 1.19
            // 使用带有 DataSerializer 的构造函数生成数据包
            9, 10, 11, 12 -> NMSPacketPlayOutEntityTeleport(dataSerializer.build() as NMSPacketDataSerializer)
            // 不支持
            else -> error("Unsupported version.")
        }
        sendPacket(player, packet)
    }

    override fun updateEntityMetadata(player: List<Player>, entityId: Int, vararg metadata: Any) {
        // 1.19.3 变更为 record 类型，因此无法兼容之前的写法
        if (majorLegacy >= 11903) {
            sendPacket(player, NMS120.INSTANCE.createEntityMetadata(entityId, *metadata))
            return
        }
        val dataSerializer = dataSerializerBuilder {
            writeVarInt(entityId)
            PacketHandler.writeNMSMetadata(this, *metadata)
        }
        if (isUniversal) {
            sendPacket(player, NMSPacketPlayOutEntityMetadata(dataSerializer.build() as NMSPacketDataSerializer))
        } else {
            sendPacket(player, NMS16PacketPlayOutEntityMetadata().also {
                it.a(dataSerializer.build() as NMS16PacketDataSerializer)
            })
        }
    }

    override fun sendMount(player: List<Player>, entityId: Int, mount: IntArray) {
        val dataSerializer = dataSerializerBuilder {
            writeVarInt(entityId)
            writeVarIntArray(mount)
        }
        if (isUniversal) {
            sendPacket(player, NMSPacketPlayOutMount(dataSerializer.build() as NMSPacketDataSerializer))
        } else {
            sendPacket(player, NMS16PacketPlayOutMount().also {
                it.a(dataSerializer.build() as NMS16PacketDataSerializer)
            })
        }
    }

    private fun EquipmentSlot.toNMSEnumItemSlot(): NMSEnumItemSlot {
        return when (this) {
            EquipmentSlot.HAND -> NMSEnumItemSlot.MAINHAND
            EquipmentSlot.OFF_HAND -> NMSEnumItemSlot.OFFHAND
            EquipmentSlot.FEET -> NMSEnumItemSlot.FEET
            EquipmentSlot.LEGS -> NMSEnumItemSlot.LEGS
            EquipmentSlot.CHEST -> NMSEnumItemSlot.CHEST
            EquipmentSlot.HEAD -> NMSEnumItemSlot.HEAD
            else -> error("Unknown EquipmentSlot: $this")
        }
    }

    private fun EquipmentSlot.toNMS13EnumItemSlot(): NMS13EnumItemSlot {
        return when (this) {
            EquipmentSlot.HAND -> NMS13EnumItemSlot.MAINHAND
            EquipmentSlot.OFF_HAND -> NMS13EnumItemSlot.OFFHAND
            EquipmentSlot.FEET -> NMS13EnumItemSlot.FEET
            EquipmentSlot.LEGS -> NMS13EnumItemSlot.LEGS
            EquipmentSlot.CHEST -> NMS13EnumItemSlot.CHEST
            EquipmentSlot.HEAD -> NMS13EnumItemSlot.HEAD
            else -> error("Unknown EquipmentSlot: $this")
        }
    }
}