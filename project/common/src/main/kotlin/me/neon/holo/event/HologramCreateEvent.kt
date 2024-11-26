package me.neon.holo.event

import taboolib.common.util.Location
import taboolib.platform.type.BukkitProxyEvent

/**
 * NeonHologram
 * me.neon.holo.event
 *
 * @author 老廖
 * @since 2024/11/26 23:38
 */
class HologramCreateEvent(
    var id: String,
    var location: Location,
    var text: List<String>,
    val lineSpacing: Double,
    var visibleByDistance: Double,
    var save: Boolean
): BukkitProxyEvent()