package me.neon.holo.nms.data

import taboolib.common.util.unsafeLazy
import taboolib.module.nms.nmsProxy

/**
 * Adyeshach
 * ink.ptms.adyeshach.api.dataserializer.DataSerializerFactory
 *
 * @author 坏黑
 * @since 2022/12/12 23:04
 */
interface DataSerializerFactory {

    fun newSerializer(): DataSerializer

    companion object {

        @JvmStatic
        val instance by unsafeLazy { nmsProxy<DataSerializerFactory>() }
    }
}