package me.neon.holo.event

import me.neon.holo.hologram.Hologram
import taboolib.platform.type.BukkitProxyEvent

/**
 * NeonHologram
 * me.neon.holo.event
 *
 * @author 老廖
 * @since 2024/11/26 23:38
 */
class HologramDeleteEvent(val hologram: Hologram, var save: Boolean): BukkitProxyEvent()