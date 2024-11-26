package me.neon.holo.nms.minecraft

import me.neon.holo.nms.Packet
import me.neon.holo.nms.agent.EntityType
import me.neon.holo.nms.packet.PacketArmorStandMeta
import me.neon.holo.nms.packet.PacketEntityName
import me.neon.holo.nms.data.*

import org.bukkit.inventory.ItemStack

import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.nms.MinecraftVersion
import java.util.*
import kotlin.collections.ArrayList

/**
 * @作者: 老廖
 * @时间: 2023/6/1 16:55
 * @包: me.geek.holo.nms.minecraft
 */
class NMSSundryHandlerImpl: NMSSundryHandler {
    /**
     * 老版本格式
     */
    private val majorLegacy = MinecraftVersion.majorLegacy

    private val major = MinecraftVersion.major

    /**
     * 在 1.13↓ 使用的是数字ID ，以上为种类 ,故同时提供种类名称，以及数字ID
     */
    override fun adaptNMSEntityType(type: EntityType): Any {
        // 1.13以下
        if (major < 5) {
            return type.id
        }
        val names = ArrayList<String>()
        names +=  if (type != EntityType.DROPPED_ITEM) type.name.uppercase() else "ITEM"
        names += type.id.toString()
        names.forEach {
            kotlin.runCatching {
                return NMS16EntityTypes::class.java.getProperty<Any>(it, isStatic = true)!!
            }
        }
        error("不支持的类型 $type $names")
    }

    override fun adaptArmorStandMeta(packetArmorStandMeta: PacketArmorStandMeta): Array<Any> {
        var entity = 0
        var armorstand = 0
        if (packetArmorStandMeta.isInvisible) entity += 0x20.toByte()
        if (packetArmorStandMeta.isGlowing) entity += 0x40.toByte()
        if (packetArmorStandMeta.isSmall) armorstand += 0x01.toByte()
        if (packetArmorStandMeta.hasArms) armorstand += 0x04.toByte()
        if (packetArmorStandMeta.noBasePlate) armorstand += 0x08.toByte()
        if (packetArmorStandMeta.isMarker) armorstand += 0x10.toByte()
        return arrayOf(createByteMeta(0, entity.toByte()),
            createByteMeta(Packet.armorStandIndex, armorstand.toByte()))
    }


    override fun adaptItemStackMeta(glow: Boolean, item: ItemStack): Array<Any> {
        var entity = 0
        if (glow) entity += 0x40.toByte()
        return arrayOf(
            createByteMeta(0, entity.toByte()),
            // noGravity
            createBooleanMeta(5, true),
            createItemStackMeta(Packet.itemStackIndex, item)
        )
    }

    override fun adaptEntityName(packetEntityName: PacketEntityName): Array<Any> {
        return arrayOf(getMetaEntityChatBaseComponent(2, packetEntityName.name), createBooleanMeta(3, packetEntityName.isCustomNameVisible))
    }


    override fun craftChatMessageFromString(message: String): Any {
        return CraftChatMessage19.fromString(message)[0]
    }

    private fun getMetaEntityChatBaseComponent(index: Int, rawMessage: String?): Any {
        return when {
            majorLegacy >= 11900 -> {
                NMSDataWatcherItem(
                    NMSDataWatcherObject(index, NMSDataWatcherRegistry.OPTIONAL_COMPONENT),
                    Optional.ofNullable(if (rawMessage == null) null else craftChatMessageFromString(rawMessage) as NMSIChatBaseComponent)
                )
            }
            majorLegacy >= 11300 -> {
                NMS16DataWatcherItem(
                    NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.f),
                    Optional.ofNullable(if (rawMessage == null) null else craftChatMessageFromString(rawMessage) as NMS16IChatBaseComponent)
                )
            }
            else -> {
                NMS12DataWatcherItem(NMS12DataWatcherObject(index, NMS12DataWatcherRegistry.d), rawMessage ?: "")
            }
        }
    }


    private fun createItemStackMeta(index: Int, itemStack: ItemStack): Any {
        return when {
            majorLegacy >= 11900 -> {
                NMSDataWatcherItem(
                    NMSDataWatcherObject(index, NMSDataWatcherRegistry.ITEM_STACK),
                    CraftItemStack19.asNMSCopy(itemStack)
                )
            }
            majorLegacy >= 11300 -> {
                NMS16DataWatcherItem(
                    NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.g),
                    CraftItemStack16.asNMSCopy(itemStack)
                )
            }
            majorLegacy >= 11200 -> {
                NMS12DataWatcherItem(
                    NMS12DataWatcherObject(index, NMS12DataWatcherRegistry.f),
                    CraftItemStack12.asNMSCopy(itemStack)
                )
            }
            else -> {
                NMS9DataWatcherItem(
                    NMS9DataWatcherObject(index, NMS9DataWatcherRegistry.f),
                    com.google.common.base.Optional.fromNullable(CraftItemStack9.asNMSCopy(itemStack))
                )
            }
        }
    }

    private fun createStringMeta(index: Int, value: String): Any {
        return if (MinecraftVersion.majorLegacy >= 11900) {
                NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.STRING), value)
            } else {
                NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.d), value)
            }

    }

    private fun createByteMeta(index: Int, value: Byte): Any {
        return if (MinecraftVersion.majorLegacy >= 11900) {
            NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.BYTE), value)
        } else {
            NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.a), value)
        }
    }

    private fun createBooleanMeta(index: Int, value: Boolean): Any {
        return when {
            MinecraftVersion.majorLegacy >= 11900 -> {
                NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.BOOLEAN), value)
            }
            MinecraftVersion.majorLegacy >= 11300 -> {
                NMS13DataWatcherItem(NMS13DataWatcherObject(index, NMS13DataWatcherRegistry.i), value)
            }
            else -> {
                NMS11DataWatcherItem(NMS11DataWatcherObject(index, NMS11DataWatcherRegistry.h), value)
            }
        }
    }


}