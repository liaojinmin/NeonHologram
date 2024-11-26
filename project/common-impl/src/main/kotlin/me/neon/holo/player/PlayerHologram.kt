package me.neon.holo.player

import com.google.gson.annotations.Expose
import me.neon.holo.conf.Option
import me.neon.holo.hologram.Component
import me.neon.holo.hologram.ComponentHandler
import me.neon.holo.hologram.ComponentHandler.Companion.register
import me.neon.holo.hologram.ComponentHandler.Companion.unregister
import me.neon.holo.hologram.ComponentHandlerImpl
import taboolib.module.chat.colored
import java.util.UUID

/**
 * NeonHologram
 * me.neon.holo.player
 *
 * @author 老廖
 * @since 2023/9/22 23:33
 */
@Deprecated(message = "玩家类型全息咱不维护，可能弃用")
class PlayerHologram(
    val owner: UUID,
    val uniqueId: String,
    val option: Option,
    val context: MutableList<String> = mutableListOf()
) {

    @Expose
    internal var hologram: ComponentHandler? = ComponentHandlerImpl(option)
        private set

    fun spawn(): PlayerHologram {
        if (hologram == null) {
            hologram = ComponentHandlerImpl(option)
        }
        hologram?.let {
            context.forEach { text ->
                it.publicComponent.add(Component.of(text))
            }
            it.register()
        }
        return this
    }

    fun delete(): PlayerHologram {
        this.hologram?.unregister()
        return this
    }

    fun append(name: String): PlayerHologram {
        hologram?.let {
            it.lock()
            context.add(name.colored())
            if (it.active) {
                it.publicDestroyAll()
                context.forEach { text ->
                    it.publicComponent.add(Component.of(text))
                }
            }
            it.unlock()
        }
        return this
    }

    fun subtract(line: Int): PlayerHologram {
        if (line >= context.size) return this
        hologram?.let {
            it.lock()
            context.removeAt(line)
            if (it.active) {
                it.publicDestroyAll()
                context.forEach { text ->
                    it.publicComponent.add(Component.of(text))
                }
            }
            it.unlock()
        }
        return this
    }

    fun reverse() {
        this.context.reverse()
    }

}