package me.neon.holo.nms.minecraft

import me.neon.holo.nms.agent.*
import me.neon.libs.taboolib.nms.DataSerializer
import net.minecraft.server.v1_9_R2.DataWatcher
import org.bukkit.inventory.ItemStack
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.nms.MinecraftVersion
import java.util.*
import kotlin.collections.ArrayList

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2024/11/27 15:23
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

    @Suppress("UNCHECKED_CAST")
    override fun adaptWriteMetadata(dataSerializer: DataSerializer, meta: List<Any>): DataSerializer {
        DataWatcher.a(meta as List<DataWatcher.Item<*>>, dataSerializer.build() as net.minecraft.server.v1_9_R2.PacketDataSerializer)
        return dataSerializer
    }

    override fun craftChatMessageFromString(message: String): Any {
        return CraftChatMessage19.fromString(message)[0]
    }

    override fun createChatBaseComponent(index: Int, rawMessage: String?): Any {
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

    override fun createItemStackMeta(index: Int, itemStack: ItemStack): Any {
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

    override fun createStringMeta(index: Int, value: String): Any {
        return if (MinecraftVersion.majorLegacy >= 11900) {
                NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.STRING), value)
            } else {
                NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.d), value)
            }

    }

    override fun createByteMeta(index: Int, value: Byte): Any {
        return if (MinecraftVersion.majorLegacy >= 11900) {
            NMSDataWatcherItem(NMSDataWatcherObject(index, NMSDataWatcherRegistry.BYTE), value)
        } else {
            NMS16DataWatcherItem(NMS16DataWatcherObject(index, NMS16DataWatcherRegistry.a), value)
        }
    }

    override fun createBooleanMeta(index: Int, value: Boolean): Any {
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