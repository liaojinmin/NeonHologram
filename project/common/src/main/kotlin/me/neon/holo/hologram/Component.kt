package me.neon.holo.hologram

import me.neon.holo.HologramAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.util.Location

import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

/**
 * @作者: 老廖
 * @时间: 2023/5/19 5:02
 * @包: me.geek.pet.common.armor
 */
abstract class Component {

    /**
     * 生成载体
     */
    abstract fun spawn(view: Player, location: Location): Component

    /**
     * 摧毁
     */
    abstract fun destroy(view: Player)

    /**
     * 载体更新任务
     */
    abstract fun tick(view: List<Player> = getVisiblePlayers(), location: Location? = null)

    /**
     * 行高偏移
     */
    abstract var highSet: Double

    /**
     * 横向偏移
     */
    abstract var wideSet: Double

    /**
     * 更新间隔
     */
    abstract var update: Int

    /**
     * 显示名称
     */
    abstract var displayName: String


    /**
     * 包生物ID
     */
    val entityId by lazy { ComponentHandler.nextIndex() }

    val uuid: UUID = UUID.randomUUID()

    val viewPlayer = CopyOnWriteArraySet<String>()

    var selfLocation: Location = Location("", 0.0, 0.0, 0.0)

    /**
     * 计时器
     */
    private var ticks: Int = 0

    /**
     * 点击动作
     */
    open fun click(player: Player, action: ComponentAction, isMainHand: Boolean) {}

    /**
     * 检查是否允许运行
     */
    fun have(): Boolean {
        if (update <= 0) return false
        return if (ticks >= update) {
            ticks = 0
            true
        } else {
            // 对标载体管理的运行间隔 1 Tick
            ticks+=1
            false
        }
    }

    fun isView(player: Player): Boolean {
        return viewPlayer.contains(player.name)
    }

    fun removeView(view: Player) {
        viewPlayer.remove(view.name)
    }

    fun destroyAll() {
        getVisiblePlayers().forEach { destroy(it) }
    }

    private fun getVisiblePlayers(): List<Player> {
        if (viewPlayer.isEmpty()) return emptyList()
        return viewPlayer.mapNotNull { Bukkit.getPlayerExact(it) }
    }

    companion object {

        fun of(string: String): Component {
            return HologramAPI.hologramManager.parseComponent(string)
        }

    }


}