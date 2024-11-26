package me.neon.holo.hologram

import me.neon.holo.nms.Packet
import me.neon.holo.nms.PlaceholderParser
import me.neon.holo.nms.agent.EntityType
import me.neon.holo.nms.packet.PacketArmorStandMeta
import me.neon.holo.nms.packet.PacketEntityName
import org.bukkit.entity.Player

import taboolib.common.platform.ProxyPlayer
import taboolib.common.util.Location

/**
 * @作者: 老廖
 * @时间: 2023/6/28 21:01
 * @包: me.geek.holo.module.hologram
 */
class HologramText(
    override var displayName: String,
    override var highSet: Double = 0.0,
    override var wideSet: Double = 0.0,
    override var update: Int = -1
): Component() {

    override fun spawn(view: Player, location: Location): Component {
        Packet.nmsEntitySpawnHandler.spawnEntityLiving(view, EntityType.ARMOR_STAND, this.entityId, this.uuid, location)
        val contains = displayName.contains("{notInvisible}")
        PacketArmorStandMeta(this.entityId,
            isInvisible = !contains,
            isGlowing = false,
            isSmall = false,
            hasArms = false,
            noBasePlate = true,
            isMarker = false).send(view)
        if (contains) {
            PacketEntityName(this.entityId,
                true,
                PlaceholderParser.parsePlaceholderAPI(this.displayName.replace("{notInvisible}", ""), view)).send(view)
        } else {
            PacketEntityName(
                this.entityId,
                true,
                PlaceholderParser.parsePlaceholderAPI(this.displayName, view)
            ).send(view)
        }
        return this
    }

    override fun tick(view: List<Player>, location: Location?) {
        if (have()) {
            view.forEach {
                PacketEntityName(this.entityId, true,
                    PlaceholderParser.parsePlaceholderAPI(this.displayName, it)).send(it)
            }
        }
    }

    override fun click(player: Player, action: ComponentAction, isMainHand: Boolean) {

    }


    override fun destroy(view: Player) {
        if (viewPlayer.remove(view.name)) {
            Packet.nmsEntityOperatorHandler.destroyEntity(view, entityId)
        }
    }


}