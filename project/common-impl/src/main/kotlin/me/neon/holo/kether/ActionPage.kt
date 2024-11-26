package me.neon.holo.kether


import me.neon.holo.hologram.ComponentAction.Companion.hologram
import me.neon.holo.hologram.MutableHologram
import taboolib.module.kether.*
import taboolib.platform.type.BukkitPlayer

/**
 * @作者: 老廖
 * @时间: 2023/7/25 15:21
 * @包: me.geek.holo.kether.impl
 */
object ActionPage {

    @KetherParser(["page"], namespace = "NeonHologram", shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("last") {
                actionNow {
                    (hologram() as? MutableHologram)?.lastPage((player() as BukkitPlayer).player)
                }
            }
            case("next") {
                actionNow {
                    (hologram() as? MutableHologram)?.nextPage((player() as BukkitPlayer).player)
                }
            }
        }
    }
}
