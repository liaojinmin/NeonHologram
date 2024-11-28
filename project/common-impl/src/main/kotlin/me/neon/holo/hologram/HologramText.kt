package me.neon.holo.hologram

import kotlinx.coroutines.yield
import me.neon.holo.nms.PacketHandler
import me.neon.holo.nms.PlaceholderParser
import me.neon.holo.nms.agent.EntityType
import org.bukkit.entity.Player

import taboolib.common.util.Location

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2023/6/28 21:01
*/
class HologramText(
    override var displayName: String,
    override var highSet: Double = 0.0,
    override var wideSet: Double = 0.0,
    override var update: Int = -1
): Component() {

    override fun spawn(view: Player, location: Location): Component {
        PacketHandler.sendSpawnArmorStand(listOf(view), entityId, uuid, location)
        val contains = displayName.contains("{notInvisible}")

        PacketHandler.sendArmorStandMeta(listOf(view), entityId,
            isInvisible = !contains,
            isGlowing = false,
            isSmall = false,
            hasArms = false,
            noBasePlate = true,
            isMarker = false
        )
        if (contains) {
            PacketHandler.sendEntityName(
                listOf(view),
                this.entityId,
                PlaceholderParser.parsePlaceholderAPI(this.displayName.replace("{notInvisible}", ""), view),
                true
            )
        } else {
            PacketHandler.sendEntityName(
                listOf(view),
                this.entityId,
                PlaceholderParser.parsePlaceholderAPI(this.displayName, view),
                true
            )
        }
        return this
    }

    override fun tick(view: List<Player>, location: Location?) {
        if (have()) {
            view.forEach {
                PacketHandler.sendEntityName(
                    listOf(it),
                    this.entityId,
                    PlaceholderParser.parsePlaceholderAPI(this.displayName, it),
                    true
                )
            }
        }
    }

    override fun click(player: Player, action: ComponentAction, isMainHand: Boolean) {

    }


    override fun destroy(view: Player) {
        if (viewPlayer.remove(view.name)) {
            PacketHandler.sendEntityDestroy(listOf(view), entityId)
        }
    }


}