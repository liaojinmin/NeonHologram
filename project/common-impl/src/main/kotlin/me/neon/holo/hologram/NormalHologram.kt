package me.neon.holo.hologram

import me.neon.holo.HologramAPI
import me.neon.holo.conf.Option
import me.neon.holo.hologram.ComponentHandler.Companion.register
import me.neon.holo.hologram.ComponentHandler.Companion.unregister
import org.bukkit.entity.Player
import taboolib.module.chat.colored

/**
 * GeekHologram
 * me.geek.holo.hologram
 *
 * @author 老廖
 * @since 2023/9/23 14:37
 */
class NormalHologram(
    override val uniqueId: String,
    option: Option,
    override val loaderPath: String,
): Hologram(option) {

    override var owner: String = "server"

    override val hologram: ComponentHandler = ComponentHandlerImpl(option)

    override fun spawn(): Hologram {
        hologramContext[0]?.let {
            it.carrierContext.forEach { comp ->
                hologram.publicComponent.add(comp)
            }
        }
        this.hologram.register(this)
        return this
    }

    override fun delete(): Boolean {
        this.hologram.unregister()
        return true
    }

    override fun append(name: String, page: Int): Hologram {
        hologram.lock()
        hologramContext[0]?.let {
            it.originContext.add(name.colored())
            it.carrierContext.add(HologramAPI.hologramManager.parseComponent(name))

            if (hologram.active) {
                hologram.publicDestroyAll()
                it.carrierContext.forEach { comp ->
                    hologram.publicComponent.add(comp)
                }
            }
        }
        hologram.unlock()
        return this
    }

    override fun subtract(line: Int, page: Int): Hologram {
        if (page != 0 && page >= hologramContext.size) return this
        lock()
        hologramContext[0]?.let {
            if (it.originContext.size > line) {
                it.originContext.removeAt(line)
            }
            if (it.carrierContext.size > line) {
                it.carrierContext.removeAt(line).destroyAll()
            }

            if (hologram.active) {
                hologram.publicDestroyAll()
                it.carrierContext.forEach { comp ->
                    hologram.publicComponent.add(comp)
                }
            }
        }
        unlock()
        return this
    }

}