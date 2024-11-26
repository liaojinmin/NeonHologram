package me.neon.holo.nms.packet

import me.neon.holo.nms.Packet
import org.bukkit.entity.Player



class PacketEntityName(
    val entityId: Int,
    val isCustomNameVisible: Boolean,
    val name: String)
{
    fun send(target: Player) {
        send(listOf(target))
    }
    fun send(target: List<Player>) {
        val data = Packet.nmsSundryHandler.adaptEntityName(this)
        Packet.nmsEntityOperatorHandler.updateEntityMetadata(target, entityId, *data)
    }
}
