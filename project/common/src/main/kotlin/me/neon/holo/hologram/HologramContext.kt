package me.neon.holo.hologram

import me.neon.holo.HologramAPI
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitPlayer


/**
 * NeonHologram
 * me.geek.holo.hologram
 *
 * @author 老廖
 * @since 2023/11/23 10:34
 */
class HologramContext(
    /**
     * 全息的原始文本
     */
    var originContext: MutableList<String>,
    /**
     * 全息的动作组
     */
    val action: Map<ComponentAction, String>
) {

    val carrierContext: MutableList<Component> = originContext.map {
        HologramAPI.hologramManager.parseComponent(it)
    }.toMutableList()

    fun evalAction(player: Player, hologram: Hologram, type: ComponentAction) {
    //    println("动作大小: ${action.size}")
        action[ComponentAction.ALL]?.let {
         //   println("动作 ComponentAction.ALL")
            ComponentAction.eval(BukkitPlayer(player), it, "@Hologram" to hologram)
        }
        action[type]?.let {
           // println("动作 $type")
            ComponentAction.eval(BukkitPlayer(player), it, "@Hologram" to hologram)
        }
    }

    fun registerAll(hologram: Hologram) {
        carrierContext.forEach {
            ComponentHandler.addComponent(it, hologram)
        }
    }

    fun unRegisterAll() {
        carrierContext.forEach {
            ComponentHandler.delComponent(it)
        }
    }
}