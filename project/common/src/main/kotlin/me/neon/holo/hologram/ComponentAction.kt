package me.neon.holo.hologram

import taboolib.common.platform.ProxyPlayer
import taboolib.library.kether.LocalizedException
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2024/11/26 21:56
 */
enum class ComponentAction {

    LEFT_CLICK,

    RIGHT_CLICK,

    SHIFT_LEFT_CLICK,

    SHIFT_RIGHT_CLICK,

    ALL;

    companion object {

        fun of(value: String): ComponentAction {
            return when (value) {
                "left" -> LEFT_CLICK
                "right" -> RIGHT_CLICK
                "shift_left" -> SHIFT_LEFT_CLICK
                "shift_right" -> SHIFT_RIGHT_CLICK
                else -> ALL
            }
        }

        fun eval(player: ProxyPlayer, script: String, vararg vara: Pair<String, Any?>): CompletableFuture<Any?> {
            return try {
                KetherShell.eval(script, namespace =  listOf("NeonHologram", "kether")) {
                    sender = player
                    rootFrame().variables().also { vars ->
                        vara.forEach {
                            vars.set(it.first, it.second)
                        }
                    }
                }
            } catch (e: LocalizedException) {
                e.printStackTrace()
                CompletableFuture.completedFuture(false)
            }
        }

        fun ScriptFrame.hologram(): Hologram {
            return variables().get<Any?>("@Hologram").orElse(null) as? Hologram ?: error("Kether 不存在全息回调...")
        }

    }
}



