package me.neon.holo.nms.n120


import net.minecraft.core.IRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
import net.minecraft.network.syncher.DataWatcher

import net.minecraft.world.entity.EntityTypes

/**
 * @作者: 老廖
 * @时间: 2023/6/17 18:15
 * @包: me.geek.cos.common.nms.n120
 */
class NMS120Impl : NMS120() {

    override fun entityTypeGetId(any: Any): Int {
        val ir = BuiltInRegistries.ENTITY_TYPE as IRegistry<EntityTypes<*>>
        return ir.getId(any as EntityTypes<*>)
    }

    override fun createEntityMetadata(entityId: Int, vararg metadata: Any): Any {
        return PacketPlayOutEntityMetadata(entityId, metadata.map { (it as DataWatcher.Item<*>).value() })
    }

}