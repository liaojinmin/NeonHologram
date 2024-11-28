package me.neon.holo.nms.minecraft

import me.neon.holo.nms.agent.EntityType
import org.bukkit.entity.Player
import taboolib.common.util.Location
import java.util.*

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2024/11/27 15:23
 */
interface EntitySpawnHandler {

    /**
     * 生成数据包实体
     *
     * @param player 数据包接收人
     * @param entityType 实体类型
     * @param entityId 实体序号
     * @param uuid 实体 UUID
     * @param location 生成坐标
     * @param data 特殊数据
     */
    fun spawnEntity(player: Player, entityType: EntityType, entityId: Int, uuid: UUID, location: Location, data: Int = 0) {
        return spawnEntity(listOf(player), entityType, entityId, uuid, location, data)
    }
    fun spawnEntity(player: List<Player>, entityType: EntityType, entityId: Int, uuid: UUID, location: Location, data: Int = 0)

    /**
     * 在 1.18 及以下版本生成 EntityLiving 类型的数据包实体，在 1.19 版本中被 [spawnEntity] 取代。
     *
     * 在 1.19 及以上版本调用时会产生异常。
     *
     * @param player 数据包接收人
     * @param entityType 实体类型
     * @param entityId 实体序号
     * @param uuid 实体 UUID
     * @param location 实体坐标
     */
    fun spawnEntityLiving(player: Player, entityType: EntityType, entityId: Int, uuid: UUID, location: Location) {
        return spawnEntityLiving(listOf(player), entityType, entityId, uuid, location)
    }
    fun spawnEntityLiving(player: List<Player>, entityType: EntityType, entityId: Int, uuid: UUID, location: Location)

}