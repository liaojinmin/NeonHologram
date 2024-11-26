package me.neon.holo.nms

import me.neon.holo.nms.minecraft.*

import taboolib.module.nms.MinecraftVersion

import taboolib.module.nms.nmsProxy



object Packet {

    private val group = "${Packet::class.java.`package`.name}.minecraft"


    /**
     * 盔甲架meta索引号
     */
    internal val armorStandIndex by lazy { arrayOf(11700 to 15, 11500 to 14, 11400 to 13, 11000 to 11, 10900 to 10).firstOrNull {
        MinecraftVersion.majorLegacy >= it.first }?.second ?: -1
    }

    /**
     * 物品生物meta索引号
     */
    internal val itemStackIndex by lazy { arrayOf(11700 to 8, 11300 to 7, 11000 to 6, 10900 to 5).firstOrNull {
        MinecraftVersion.majorLegacy >= it.first }?.second ?: -1
    }


    /** 单位生成接口 **/
    val nmsEntitySpawnHandler = nmsProxy<EntitySpawnHandler>("$group.EntitySpawnHandlerImpl")


    /** 生物设置选项接口 **/
    val nmsEntityOperatorHandler = nmsProxy<EntityOperatorHandler>("$group.EntityOperatorHandlerImpl")


    /** 工具类杂项接口 **/
    val nmsSundryHandler = nmsProxy<NMSSundryHandler>("$group.NMSSundryHandlerImpl")





}