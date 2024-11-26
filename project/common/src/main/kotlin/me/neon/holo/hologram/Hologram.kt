package me.neon.holo.hologram

import me.neon.holo.conf.Option
import me.neon.holo.utils.saveToFile
import me.neon.holo.utils.yaml
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.Location
import java.io.File

/**
 * NeonHologram
 * me.geek.holo.hologram
 *
 * @author 老廖
 * @since 2023/9/23 14:59
 */
abstract class Hologram(
    val option: Option
) {

    abstract var owner: String

    abstract val uniqueId: String

    abstract val loaderPath: String

    abstract val hologram: ComponentHandler

    abstract fun spawn(): Hologram

    abstract fun delete(): Boolean

    abstract fun append(name: String, page: Int = 0): Hologram

    abstract fun subtract(line: Int, page: Int = 0): Hologram

    /**
     * key = 页数
     * value = 上下文
     */
    val hologramContext: MutableMap<Int, HologramContext> = mutableMapOf()

    open fun onClick(player: Player, action: ComponentAction, isMainHand: Boolean) {
        if (hologramContext.isEmpty() || !isMainHand) return
        hologramContext[0]?.evalAction(player, this, action)
    }

    /**
     * 如果这个客户端ID属于本实例则返回
     */
    fun claimComponent(entityID: Int): Boolean {
        if (hologram.publicComponent.any { it.entityId == entityID }) {
            return true
        } else {
            // 好傻逼的认领 TODO("待改进")
            val p = hologram.privateComponent.entries.iterator()
            while (p.hasNext()) {
                val v = p.next().value
                if (v.any { it.entityId == entityID }) {
                    return true
                }
            }
        }
        return false
    }

    fun move(newLocation: Location): Boolean {
        newLocation.world?.let {
            hologram.lock()
            submitAsync {
                val location = option.location
                hologram.publicDestroyAll()
                hologram.privateDestroyAll()
                location.setWorld(it)
                location.x = newLocation.x
                location.y = newLocation.y - 1
                location.z = newLocation.z
                hologram.unlock()
                save(File(loaderPath))
            }
        } ?: return false
        return true
    }

    fun save(file: File) {
        yaml {
            "hologram" to {
                "owner" to owner
                "uniqueId" to uniqueId
                "option" to {
                    val location = option.location
                    "location" to "${location.world};${location.x};${location.y};${location.z}"
                    "visibleByDistance" to option.visibleByDistance
                    "visibleCondition" to option.visibleCondition
                    "lineSpacing" to option.lineSpacing
                    "isScroll" to option.isScroll
                }
                if (hologramContext.isNotEmpty()) {
                    val list = mutableListOf<ConfigurationSection>()
                    hologramContext.values.forEach {
                        list.add(
                            yaml {
                                "text" to it.originContext

                                if (it.action.isNotEmpty()) {
                                    "action" to {
                                        it.action[ComponentAction.ALL]?.let {
                                            "all" to it
                                        }
                                        it.action[ComponentAction.LEFT_CLICK]?.let {
                                            "left" to it
                                        }
                                        it.action[ComponentAction.RIGHT_CLICK]?.let {
                                            "right" to it
                                        }
                                        it.action[ComponentAction.SHIFT_LEFT_CLICK]?.let {
                                            "shift_left" to it
                                        }
                                        it.action[ComponentAction.SHIFT_RIGHT_CLICK]?.let {
                                            "shift_right" to it
                                        }
                                    }
                                }
                            }.yml
                        )
                    }
                    "context" to list
                }
            }
        }.saveToFile(file)
    }

    final fun lock() {
        hologram.lock()
    }

    final fun unlock() {
        hologram.unlock()
    }

}