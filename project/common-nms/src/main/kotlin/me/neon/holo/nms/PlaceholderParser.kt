package me.neon.holo.nms

import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.type.BukkitPlayer

/**
 * GeekHologram
 * me.geek.holo.nms
 *
 * @author 老廖
 * @since 2023/9/23 17:01
 */
object PlaceholderParser {

    fun parsePlaceholderAPI(text: String, player: Player): String {

        return text.replacePlaceholder(player)
    }

}