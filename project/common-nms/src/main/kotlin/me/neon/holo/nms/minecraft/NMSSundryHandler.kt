package me.neon.holo.nms.minecraft

import me.neon.holo.nms.agent.EntityType
import me.neon.libs.taboolib.nms.DataSerializer
import org.bukkit.inventory.ItemStack

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2024/11/27 15:23
 */
interface NMSSundryHandler {

    fun adaptWriteMetadata(dataSerializer: DataSerializer, meta: List<Any>): DataSerializer

    /**
     * 解析NMS生物种类ID
     */
    fun adaptNMSEntityType(type: EntityType): Any

    fun createChatBaseComponent(index: Int, rawMessage: String?): Any

    fun createItemStackMeta(index: Int, itemStack: ItemStack): Any

    fun createStringMeta(index: Int, value: String): Any

    fun createBooleanMeta(index: Int, value: Boolean): Any

    fun createByteMeta(index: Int, value: Byte): Any

    /**
     * 使用 CraftChatMessage 将字符串转换为 IChatBaseComponent 类型
     */
    fun craftChatMessageFromString(message: String): Any

}