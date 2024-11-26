package me.neon.holo.hologram

import taboolib.common.util.Location

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2024/11/26 22:16
 */
interface HologramManager {

    val defLocation: Location

    val defContext: List<String>

    val defLineSpacing: Double

    val defVisibleByDistance: Double

    fun parseComponent(string: String): Component

    fun findHologram(entityId: Int): Hologram?

    fun findHologram(uniqueId: String): Hologram?

    fun createHologram(
        id: String,
        location: Location,
        text: List<String> = defContext,
        lineSpacing: Double = defLineSpacing,
        visibleByDistance: Double = defVisibleByDistance,
        save: Boolean = false
    ): Hologram?

    fun deleteHologram(uniqueId: String, save: Boolean = true): Boolean

    fun unloadAllHologram()

    fun loadAllHologram()

}