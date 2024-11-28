package me.neon.libs.taboolib.nms

import taboolib.common.util.unsafeLazy
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy

/**
 * Adyeshach
 * taboolib.module.nms.DataSerializerFactory
 *
 * @author 坏黑
 * @since 2022/12/12 23:04
 */
interface DataSerializerFactory {

    fun newSerializer(): DataSerializer

    companion object {

        val instance by unsafeLazy {
            if (MinecraftVersion.majorLegacy >= 12005) {
                nmsProxy<DataSerializerFactory>("{name}12005")
            } else {
                nmsProxy<DataSerializerFactory>("{name}Legacy")
            }
        }
    }
}

/**
 * 创建一个 [DataSerializer]
 */
fun dataSerializerBuilder(builder: DataSerializer.() -> Unit = {}): DataSerializer {
    return DataSerializerFactory.instance.newSerializer().also(builder)
}