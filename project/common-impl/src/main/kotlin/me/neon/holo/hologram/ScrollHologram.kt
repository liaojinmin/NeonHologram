package me.neon.holo.hologram

import me.neon.holo.conf.Option
import me.neon.holo.hologram.ComponentHandler.Companion.register
import me.neon.holo.hologram.ComponentHandler.Companion.unregister
import org.bukkit.entity.Player
import taboolib.module.chat.colored
import java.io.File

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2023/11/23 9:51
 */
class ScrollHologram(
    override val uniqueId: String,
    option: Option,
    override val loaderPath: String,
): Hologram(option) {

    override var owner: String = "server"

    override val hologram: ComponentHandler = ComponentHandlerImpl(option)

    private var taskTimer = 60

    private var page = 0

    override fun onClick(player: Player, action: ComponentAction, isMainHand: Boolean) {
        if (hologramContext.isEmpty() || !isMainHand) return
        hologramContext[page]?.evalAction(player, this, action)
    }

    override fun spawn(): Hologram {
        hologram.addTaskFunc {
            if (taskTimer == 0) {
                taskTimer = 60
                if (page == hologramContext.size) {
                    page = 0
                } else {
                    lock()
                    hologramContext[page]?.let {
                        hologram.publicDestroyAll()
                        it.carrierContext.forEach { text ->
                            this.hologram.publicComponent.add(text)
                        }
                        it.registerAll(this)
                    }
                    page++
                    unlock()
                }
            } else taskTimer--
        }

        hologramContext[0]?.let {
            it.carrierContext.forEach { text ->
                this.hologram.publicComponent.add(text)
            }
            this.hologram.register(this)
        } ?: error("全息页面 0 不存在，请检查配置文件...")
        return this;
    }

    override fun delete(): Boolean {
        this.hologram.lock()
        this.hologram.unregister()

        this.hologramContext.values.forEach {
            it.unRegisterAll()
        }

        this.hologram.unlock()
        return true;
    }

    override fun append(name: String, page: Int): Hologram {
        if (this.hologramContext.isEmpty()) {
            this.hologramContext[0] = HologramContext(mutableListOf(name), emptyMap())
        }
        hologram.lock()
        val holo = Component.of(name)
        if (page == 0) {
            hologram.publicDestroyAll()
            hologram.privateDestroyAll()

            this.hologramContext[0]?.let {

                it.originContext.add(name.colored())
                it.carrierContext.add(holo)

                hologram.publicComponent.add(holo)
            }
        } else {
            this.hologramContext[0]?.let {

                it.originContext.add(name.colored())
                it.carrierContext.add(holo)

            }
        }
        save(File(loaderPath))
        hologram.unlock()
        return this
    }



    override fun subtract(line: Int, page: Int): Hologram {
        if (this.hologramContext.isEmpty()) {
            return this
        }

        hologram.lock()
        if (page == 0) {
            if (hologram.publicComponent.size > line) {
                hologram.publicComponent.removeAt(line)?.destroyAll()
                hologramContext[0]?.let {
                    if (it.originContext.size > line) {
                        it.originContext.removeAt(line)
                    }
                    if (it.carrierContext.size > line) {
                        it.carrierContext.removeAt(line).destroyAll()
                    }
                }
            }
        } else {
            hologramContext[page]?.let {
                if (it.originContext.size > line) {
                    it.originContext.removeAt(line)
                }
                if (it.carrierContext.size > line) {
                    it.carrierContext.removeAt(line).destroyAll()
                }
            }
        }
        save(File(loaderPath))
        hologram.unlock()
        return this
    }

}