package me.neon.holo.nms.packet

import me.neon.holo.nms.Packet
import org.bukkit.entity.Player


class PacketArmorStandMeta(
    val entityId: Int,
    val isInvisible: Boolean,
    val isGlowing: Boolean,
    val isSmall: Boolean,
    val hasArms: Boolean,
    val noBasePlate: Boolean,
    val isMarker: Boolean
) {

    fun send(target: Player) {
        send(listOf(target))
    }

    fun send(target: List<Player>) {
        val data = Packet.nmsSundryHandler.adaptArmorStandMeta(this)
        Packet.nmsEntityOperatorHandler.updateEntityMetadata(target, entityId, *data)
    }
}