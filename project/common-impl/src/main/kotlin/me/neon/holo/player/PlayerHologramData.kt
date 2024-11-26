package me.neon.holo.player

import java.io.File
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.UUID

/**
 * NeonHologram
 * me.neon.holo.player
 *
 * @author 老廖
 * @since 2023/9/24 23:02
 */
@Deprecated(message = "玩家类型全息咱不维护，可能弃用")
data class PlayerHologramData(
    val uuid: UUID,
    /**
     * 全息数据
     */
    val data: MutableList<PlayerHologram> = mutableListOf(),
    /**
     * 玩家当前创建数量
     */
    var amount: Int = 0
) {
    fun save() {
        val file = File(PlayerHologramManager.defPlayerHologramFile, "$uuid.json")
        if (!file.exists()) {
            file.createNewFile()
        }
        OutputStreamWriter(
            Files.newOutputStream(file.toPath()),
            StandardCharsets.UTF_8
        ).use {
            it.write(PlayerHologramManager.gsonBuilder.create().toJson(this))
        }
    }
}