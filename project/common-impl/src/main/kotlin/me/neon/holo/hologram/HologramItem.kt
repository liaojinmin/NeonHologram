package me.neon.holo.hologram

import me.neon.holo.nms.PacketHandler
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.util.Location
import taboolib.library.xseries.parseToItemStack
import java.util.*


/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2024/11/27 15:23
 */
class HologramItem(
    override var displayName: String = "",
    override var highSet: Double = 0.0,
    override var wideSet: Double = 0.0,
    override var update: Int = -1
): Component() {

    init {
        highSet += 0.25
    }

    override fun spawn(view: Player, location: Location): Component {
        val loc = location.add(0.0, 2.0, 0.0)
        val tID = ComponentHandler.nextIndex()
        val players = listOf(view)
        PacketHandler.sendSpawnArmorStand(players, tID, UUID.randomUUID(), loc)
        PacketHandler.sendArmorStandMeta(players, tID,
            isInvisible = true,
            isGlowing = false,
            isSmall = true,
            hasArms = false,
            noBasePlate = true,
            isMarker = true
        )
        PacketHandler.sendSpawnItemEntity(players, entityId, uuid, loc, displayName.parseToItemStack())

        submit(delay = 5L) {
            PacketHandler.nmsEntityOperatorHandler.sendMount(view, tID, IntArray(1) { entityId })
            submit(delay = 20 * 5) {
                PacketHandler.sendEntityDestroy(players, tID)

            }
        }
        return this
    }

    override fun destroy(view: Player) {
        if (viewPlayer.remove(view.name)) {
            PacketHandler.sendEntityDestroy(listOf(view), entityId)
        }
    }

    override fun tick(view: List<Player>, location: Location?) {
        if (have()) {
            PacketHandler.sendItemEntityMeta(view, entityId, false, displayName.parseToItemStack())
        }
    }

}