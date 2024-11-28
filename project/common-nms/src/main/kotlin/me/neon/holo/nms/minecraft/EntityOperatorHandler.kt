package me.neon.holo.nms.minecraft

import org.bukkit.entity.Player
import taboolib.common.util.Location

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2024/11/27 15:23
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