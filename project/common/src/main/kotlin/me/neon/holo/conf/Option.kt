package me.neon.holo.conf

import taboolib.common.util.Location
import taboolib.common.util.Vector
import kotlin.math.sqrt

/**
 * GeekHologram
 * me.geek.holo.hologram
 *
 * @author 老廖
 * @since 2023/9/23 12:50
 */
data class Option(
    val location: Location = Location("world", 0.0, 0.0, 0.0),
    val visibleByDistance: Double = 32.0,
    val lineSpacing: Double = 0.25,
    val visibleCondition: String = "",
    val isScroll: Boolean = false
) {

    fun distance(bukkitLocation: org.bukkit.Location): Double {
        return sqrt(this.distanceSquared(bukkitLocation))
    }

    private fun distanceSquared(bukkitLocation: org.bukkit.Location): Double {
        if (bukkitLocation.world != null && location.world != null) {
            require(bukkitLocation.world.name == location.world) { "Cannot measure distance between " + location.world + " and " + bukkitLocation.world.name }
            return Vector.square(location.x - bukkitLocation.x) + Vector.square(location.y - bukkitLocation.y) + Vector.square(
                location.z - bukkitLocation.z
            )
        } else {
            throw IllegalArgumentException("Cannot measure distance to a null world")
        }
    }


}