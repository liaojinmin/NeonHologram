package me.neon.holo.nms.minecraft

import me.neon.holo.nms.agent.EntityType
import org.bukkit.entity.Player
import taboolib.common.util.Location
import java.util.*

/**
 * @作者: 老廖
 * @时间: 2023/5/31 15:23
 * @包: me.geek.cos.common.nms.minecraft
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
    fun spawnEntity(player: Player, entityType: EntityType, entityId: Int, uuid: UUID, location: Location, data: Int = 0)

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
    fun spawnEntityLiving(player: Player, entityType: EntityType, entityId: Int, uuid: UUID, location: Location)

}