package me.neon.holo.nms.minecraft

import com.mojang.datafixers.util.Pair

import me.neon.holo.nms.data.createDataSerializer
import me.neon.holo.nms.data.*
import me.neon.holo.nms.n120.NMS120
import org.bukkit.entity.Player

import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.ProxyPlayer

import taboolib.common.util.Location
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.sendPacket
import taboolib.platform.type.BukkitPlayer

/**
 * @作者: 老廖
 * @时间: 2023/6/17 18:15
 * @包: me.geek.cos.common.nms.minecraft
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
        // 版本判断
        val packet: Any = when (major) {
            // 1.9, 1.10, 1.11, 1.12, 1.13, 1.14, 1.15, 1.16
            in 1..8 -> NMS9PacketPlayOutEntityTeleport().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeDouble(location.x)
                    writeDouble(location.y)
                    writeDouble(location.z)
                    // 在传送包下，yaw 与 pitch的读取顺序颠倒
                    writeByte(yaw)
                    writeByte(pitch)
                    writeBoolean(onGround)
                }.toNMS() as NMS9PacketDataSerializer)
            }
            // 1.17, 1.18, 1.19
            // 使用带有 DataSerializer 的构造函数生成数据包
            9, 10, 11, 12 -> NMSPacketPlayOutEntityTeleport(createDataSerializer {
                writeVarInt(entityId)
                writeDouble(location.x)
                writeDouble(location.y)
                writeDouble(location.z)
                writeByte(yaw)
                writeByte(pitch)
                writeBoolean(onGround)
            }.toNMS() as NMSPacketDataSerializer)
            // 不支持
            else -> error("Unsupported version.")
        }
        sendPacket(player, packet)
    }

    override fun updateEquipment(player: List<Player>, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        updateEquipment(player, entityId, mapOf(slot to itemStack))
    }

    override fun updateEquipment(player: List<Player>, entityId: Int, equipment: Map<EquipmentSlot, ItemStack>) {
        when {
            // 从 1.16 开始每个包支持多个物品
            majorLegacy >= 11600 -> {
                val items = equipment.map { Pair(it.key.toNMSEnumItemSlot(), CraftItemStack19.asNMSCopy(it.value)) }
                sendPacket(player, NMSPacketPlayOutEntityEquipment(entityId, items))
            }
            // 低版本
            else -> {
                equipment.forEach { (k, v) ->
                    sendPacket(player, NMS13PacketPlayOutEntityEquipment(entityId, k.toNMS13EnumItemSlot(), CraftItemStack13.asNMSCopy(v)))
                }
            }
        }
    }

    override fun updateEntityMetadata(player: List<Player>, entityId: Int, vararg metadata: Any) {
        // 1.19.3 变更为 record 类型，因此无法兼容之前的写法
        if (majorLegacy >= 11903) {
            sendPacket(player, NMS120.INSTANCE.createEntityMetadata(entityId, *metadata))

        } else if (isUniversal) {
            sendPacket(player, NMSPacketPlayOutEntityMetadata(createDataSerializer {
                writeVarInt(entityId)
                writeMetadata(metadata.toList())
            }.toNMS() as NMSPacketDataSerializer))
        } else {
            sendPacket(player, NMS16PacketPlayOutEntityMetadata().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeMetadata(metadata.toList())
                }.toNMS() as NMS16PacketDataSerializer)
            })
        }
    }

    override fun sendMount(player: List<Player>, entityId: Int, mount: IntArray) {
        if (isUniversal) {
            sendPacket(player, NMSPacketPlayOutMount(createDataSerializer {
                writeVarInt(entityId)
                writeVarIntArray(mount)
            }.toNMS() as NMSPacketDataSerializer))
        } else {
            sendPacket(player, NMS16PacketPlayOutMount().also {
                it.a(createDataSerializer {
                    writeVarInt(entityId)
                    writeVarIntArray(mount)
                }.toNMS() as NMS16PacketDataSerializer)
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