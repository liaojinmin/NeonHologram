package me.neon.holo.nms.minecraft

import me.neon.holo.nms.agent.EntityType
import me.neon.holo.nms.packet.PacketArmorStandMeta
import me.neon.holo.nms.packet.PacketEntityName
import org.bukkit.inventory.ItemStack

/**
 * @作者: 老廖
 * @时间: 2023/6/1 16:55
 * @包: me.geek.cos.common.nms.minecraft
 */
interface NMSSundryHandler {


    /**
     * 解析NMS生物种类ID
     */
    fun adaptNMSEntityType(type: EntityType): Any

    /**
     * 解析盔甲架属性
     */
    fun adaptArmorStandMeta(packetArmorStandMeta: PacketArmorStandMeta): Array<Any>

    /**
     * 解析物品Meta
     */
    fun adaptItemStackMeta(glow: Boolean, item: ItemStack): Array<Any>


    /**
     * 解析生物名称属性
     */
    fun adaptEntityName(packetEntityName: PacketEntityName): Array<Any>


    /**
     * 使用 CraftChatMessage 将字符串转换为 IChatBaseComponent 类型
     */
    fun craftChatMessageFromString(message: String): Any

}