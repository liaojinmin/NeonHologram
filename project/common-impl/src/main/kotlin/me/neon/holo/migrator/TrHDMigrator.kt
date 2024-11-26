package me.neon.holo.migrator


import me.neon.holo.hologram.HologramManagerImpl
import me.neon.holo.utils.forFile
import org.bukkit.Bukkit
import org.bukkit.Location
import taboolib.common.platform.function.info
import taboolib.module.configuration.SecuredFile
import taboolib.platform.util.toProxyLocation
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * @作者: 老廖
 * @时间: 2023/6/30 12:49
 * @包: me.geek.holo.module.hologram
 */
class TrHDMigrator {
    private val target: File by lazy {
        File("${System.getProperty("user.dir")}${File.separator}plugins${File.separator}TrHologram", "holograms")
    }
    private val list = mutableListOf<File>()
    fun start() {
        measureTimeMillis {

            list.also {
                it.addAll(forFile(target, ".yml"))
            }
            list.forEach { file ->
                SecuredFile.loadConfiguration(file).also {
                    val id = file.nameWithoutExtension
                    val location = it.getString("Location")?.parseLocation() ?: throw Exception("No valid location")
                    val lineSpacing = it.getDouble("Options.Line-Spacing", 0.25)
                    val viewDistance = it.getDouble("Options.View-Distance", 32.0)
                    val contents = it.getStringList("Contents")
                    HologramManagerImpl.createHologram(
                        id,
                        location.subtract(0.0, 2.5, 0.0).toProxyLocation(),
                        contents,
                        lineSpacing,
                        viewDistance,
                        true
                    )
                }
            }
        }.also {
            info("迁移 (${list.size})个 TrHologram 全息图... (耗时 $it ms)")
        }
    }

    private fun String.parseLocation(): Location {
        val (world, loc) = split("~", limit = 2)
        val (x, y, z) = loc.split(",", limit = 3).map { it.toDouble() }
        return Location(Bukkit.getWorld(world), x, y, z)
    }

}