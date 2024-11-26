package me.neon.holo.event

import me.neon.holo.hologram.Component
import me.neon.holo.hologram.ComponentAction
import me.neon.holo.hologram.Hologram
import org.bukkit.entity.Player
import taboolib.common.util.Location
import taboolib.platform.type.BukkitProxyEvent

/**
 * NeonHologram
 * me.neon.holo.event
 *
 * @author 老廖
 * @since 2024/11/26 23:38
 */
class HologramInteractEvent(
    val player: Player,
    val hologram: Hologram?,
    val component: Component,
    val action: ComponentAction,
    val isMainHand: Boolean
): BukkitProxyEvent()