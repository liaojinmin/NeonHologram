package me.neon.holo.nms.minecraft

import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.util.Location

/**
 * @作者: 老廖
 * @时间: 2023/6/17 18:12
 * @包: me.geek.cos.common.nms.minecraft
 */
interface EntityOperatorHandler {

    /**
     * 移除数据包实体
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     */
    fun destroyEntity(player: List<Player>, entityId: Int)
    fun destroyEntity(player: Player, entityId: Int) {
        destroyEntity(listOf(player), entityId)
    }

    /**
     * 传送数据包实体到另一个位置
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param location 传送位置
     * @param onGround 是否在地面上
     */
    fun teleportEntity(player: List<Player>, entityId: Int, location: Location, onGround: Boolean = false)
    fun teleportEntity(player: Player, entityId: Int, location: Location, onGround: Boolean = false) {
        teleportEntity(listOf(player), entityId, location, onGround)
    }

    /**
     * 更新数据包实体的装备信息
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param slot 装备槽
     * @param itemStack 物品对象
     */
    fun updateEquipment(player: List<Player>, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack)
    fun updateEquipment(player: Player, entityId: Int, slot: EquipmentSlot, itemStack: ItemStack) {
        updateEquipment(listOf(player), entityId, slot, itemStack)
    }

    /**
     * 更新数据包实体的装备信息
     *
     * @param player 数据包接收人
     * @param entityId 实体序号
     * @param equipment 装备信息
     */
    fun updateEquipment(player: List<Player>, entityId: Int, equipment: Map<EquipmentSlot, ItemStack>)
    fun updateEquipment(player: Player, entityId: Int, equipment: Map<EquipmentSlot, ItemStack>) {
        updateEquipment(listOf(player), entityId, equipment)
    }

    /**
    * 更新数据包骑乘状态
    */
    fun sendMount(player: List<Player>, entityId: Int, mount: IntArray)
    fun sendMount(player: Player, entityId: Int, mount: IntArray) {
        sendMount(listOf(player), entityId, mount)
    }


    /**
     * 更新数据包 MetaData
     */
    fun updateEntityMetadata(player: List<Player>, entityId: Int, vararg metadata: Any)
    fun updateEntityMetadata(player: Player, entityId: Int, vararg metadata: Any) {
        updateEntityMetadata(listOf(player), entityId, *metadata)
    }



}