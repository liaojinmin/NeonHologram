package me.neon.holo.conf

import me.neon.holo.hologram.ComponentAction
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer

/**
 * GeekHologram
 * me.geek.holo.bukkit.player
 *
 * @author 老廖
 * @since 2023/9/24 21:00
 */
data class Group(
    /**
     * 优先级
     */
    val priority: Int = 1,
    /**
     * 权限
     */
    val permission: String = "hologram.player.default",
    /**
     * 花费条件
     */
    val costCondition: CostCondition = CostCondition(),
    /**
     * 视距
     */
    val visibleByDistance: Double = 16.0,
    /**
     * 全息个数
     */
    val haveAmount: Int = 10,
    /**
     * 单个全息行数
     */
    val haveLineAmount: Int = 5,
    /**
     * 单行字符数量
     */
    val haveCharLength: Int = 32,
    /**
     * 允许创建的数量
     */
    val haveWorldName: List<String> = emptyList(),
    /**
     * 允许使用的变量
     */
    val variable: List<String> = emptyList()
) {
    data class CostCondition(
        var condition: String = "",
        var allow: String = "",
        var deny: String = ""
    ) {
        fun eval(player: Player): Boolean {
            if (condition.isEmpty()) return true
            val p = adaptPlayer(player)
            if (ComponentAction.eval(p, condition).get() as? Boolean == true) {
                if (allow.isNotEmpty()) {
                    ComponentAction.eval(p, allow)
                }
                return true
            } else {
                if (deny.isNotEmpty()) {
                    ComponentAction.eval(p, deny)
                }
            }
            return false
        }
    }
}
