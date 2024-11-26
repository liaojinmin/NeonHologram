package me.neon.holo.hologram

import me.neon.holo.nms.Packet
import me.neon.holo.nms.agent.EntityType
import me.neon.holo.nms.packet.PacketArmorStandMeta
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.util.Location
import taboolib.library.xseries.parseToItemStack
import java.util.*


/**
 * @作者: 老廖
 * @时间: 2023/7/26 13:17
 * @包: me.geek.holo.hologram
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
        Packet.nmsEntitySpawnHandler.spawnEntityLiving(view, EntityType.ARMOR_STAND, tID, UUID.randomUUID(), loc)

        PacketArmorStandMeta(tID,
            isInvisible = true,
            isGlowing = false,
            isSmall = true,
            hasArms = false,
            noBasePlate = true,
            isMarker = true).send(view)

        Packet.nmsEntitySpawnHandler.spawnEntity(view, EntityType.DROPPED_ITEM, this.entityId, this.uuid, loc)

        val data = Packet.nmsSundryHandler.adaptItemStackMeta(false, displayName.parseToItemStack())
        Packet.nmsEntityOperatorHandler.updateEntityMetadata(view, entityId, *data)

        submit(delay = 5L) {
            Packet.nmsEntityOperatorHandler.sendMount(view, tID, IntArray(1) { entityId })
            submit(delay = 20 * 5) {
                Packet.nmsEntityOperatorHandler.destroyEntity(view, tID)
            }
        }

        return this
    }

    override fun destroy(view: Player) {
        if (viewPlayer.remove(view.name)) {
            Packet.nmsEntityOperatorHandler.destroyEntity(view, entityId)
        }
    }

    override fun tick(view: List<Player>, location: Location?) {
        if (have()) {
            val data = Packet.nmsSundryHandler.adaptItemStackMeta(false, displayName.parseToItemStack())
            Packet.nmsEntityOperatorHandler.updateEntityMetadata(view, entityId, *data)
        }
    }

}