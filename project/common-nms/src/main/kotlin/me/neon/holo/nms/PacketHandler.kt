package me.neon.holo.nms

import me.neon.holo.nms.agent.EntityType
import me.neon.holo.nms.minecraft.*
import me.neon.libs.taboolib.nms.DataSerializer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.Location
import taboolib.library.xseries.parseToItemStack
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy
import java.util.UUID

/**
 * NeonLibs
 * me.neon.libs.nms
 *
 * @author 老廖
 * @since 2024/4/27 8:48
 */
object PacketHandler {

    private val group = "${PacketHandler::class.java.`package`.name}.minecraft"

    /**
     * 盔甲架meta索引号
     */
    private val armorStandIndex by lazy { arrayOf(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11, 10900 to 10).firstOrNull {
        MinecraftVersion.majorLegacy >= it.first }?.second ?: -1
    }

    /**
     * 物品生物 meta 索引号
     */
    private val itemStackIndex by lazy { arrayOf(11700 to 8, 11300 to 7, 11000 to 6, 10900 to 5).firstOrNull {
        MinecraftVersion.majorLegacy >= it.first }?.second ?: -1
    }

    /** 单位生成接口 **/
    val nmsEntitySpawnHandler = nmsProxy<EntitySpawnHandler>("$group.EntitySpawnHandlerImpl")

    /** 生物设置选项接口 **/
    val nmsEntityOperatorHandler = nmsProxy<EntityOperatorHandler>("$group.EntityOperatorHandlerImpl")

    /** 工具类杂项接口 **/
    val nmsSundryHandler = nmsProxy<NMSSundryHandler>("$group.NMSSundryHandlerImpl")

    fun getNMSEntityType(type: EntityType): Any {
        return nmsSundryHandler.adaptNMSEntityType(type)
    }
    
    fun sendEntityDestroy(players: List<Player>, entityId: Int) {
        nmsEntityOperatorHandler.destroyEntity(players, entityId)
    }

    fun sendEntityName(players: List<Player>, entityId: Int, displayName: String, isCustomNameVisible: Boolean) {
        val chat = nmsSundryHandler.createChatBaseComponent(2, displayName)
        val visible = nmsSundryHandler.createBooleanMeta(3, isCustomNameVisible)
        nmsEntityOperatorHandler.updateEntityMetadata(players, entityId, chat, visible)
    }

    fun sendSpawnArmorStand(players: List<Player>, entityId: Int, uuid: UUID, location: Location) {
        nmsEntitySpawnHandler.spawnEntityLiving(players, EntityType.ARMOR_STAND, entityId, uuid, location)
    }

    fun sendSpawnItemEntity(players: List<Player>, entityId: Int, uuid: UUID, location: Location, itemStack: ItemStack) {
        nmsEntitySpawnHandler.spawnEntity(players, EntityType.DROPPED_ITEM, entityId, uuid, location)
        sendItemEntityMeta(players, entityId, false, itemStack)
    }

    fun sendItemEntityMeta(players: List<Player>, entityId: Int, glow: Boolean, itemStack: ItemStack) {
        var entity = 0
        if (glow) entity += 0x40.toByte()
        val entityByte = nmsSundryHandler.createByteMeta(0, entity.toByte())
        // noGravity
        val a2 = nmsSundryHandler.createBooleanMeta(5, true)
        val a3 = nmsSundryHandler.createItemStackMeta(itemStackIndex, itemStack)
        nmsEntityOperatorHandler.updateEntityMetadata(players, entityId, entityByte, a2, a3)
    }

    fun sendArmorStandMeta(players: List<Player>, entityId: Int,
        isInvisible: Boolean,
        isGlowing: Boolean,
        isSmall: Boolean,
        hasArms: Boolean,
        noBasePlate: Boolean,
        isMarker: Boolean
    ) {
        var entity = 0
        var armorStand = 0
        if (isInvisible) entity += 0x20.toByte()
        if (isGlowing) entity += 0x40.toByte()
        if (isSmall) armorStand += 0x01.toByte()
        if (hasArms) armorStand += 0x04.toByte()
        if (noBasePlate) armorStand += 0x08.toByte()
        if (isMarker) armorStand += 0x10.toByte()
        val entityByte = nmsSundryHandler.createByteMeta(0, entity.toByte())
        val armorStandByte = nmsSundryHandler.createByteMeta(armorStandIndex, armorStand.toByte())
        nmsEntityOperatorHandler.updateEntityMetadata(players, entityId, entityByte, armorStandByte)
    }

    /**
     * 补充 taboolib 的序列化没有对 meta 进行支持
     */
    fun writeNMSMetadata(dataSerializer: DataSerializer, vararg metadata: Any) {
        nmsSundryHandler.adaptWriteMetadata(dataSerializer, metadata.toList())
    }


}