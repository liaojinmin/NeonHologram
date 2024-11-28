package me.neon.holo.nms.n120

import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy

/**
 * @作者: 老廖
 * @时间: 2023/6/17 18:15
 * @包: me.geek.cos.common.nms.n
 */
abstract class NMS120 {

    abstract fun entityTypeGetId(any: Any): Int

    abstract fun createEntityMetadata(entityId: Int, vararg metadata: Any): Any


    companion object {

        val INSTANCE by unsafeLazy {
            nmsProxy<NMS120>()
        }
    }

}