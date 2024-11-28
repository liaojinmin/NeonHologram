package me.neon.holo

import me.neon.holo.conf.Group
import me.neon.holo.conf.Shield
import me.neon.holo.hologram.HologramManager
import me.neon.holo.utils.toStringList
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile


/**
 * NeonHologram
 * me.neon.holo.api
 *
 * @author 老廖
 * @since 2023/9/22 18:49
 */
object HologramAPI {

    val hologramManager: HologramManager by lazy {
        PlatformFactory.getAPI()
    }

    @Config(value = "settings.yml", autoReload = true)
    lateinit var settings: ConfigFile
        private set

    val groupConfig: MutableList<Group> = mutableListOf()

    var shieldConfig: Shield = Shield()
        private set

    @Awake(LifeCycle.LOAD)
    private fun load() {
        console().sendMessage("")
        console().sendMessage("正在加载 §3§l$pluginId §f...  §8" + Bukkit.getVersion())
        console().sendMessage("")
        settings.onReload { loadSettings() }
        loadSettings()
    }

    @Awake(LifeCycle.DISABLE)
    private fun disable() {
        hologramManager.unloadAllHologram()
    }

    @Awake(LifeCycle.ENABLE)
    private fun enable() {
        console().sendMessage("")
        console().sendMessage("       §a${pluginId}  §bv$pluginVersion §7by §awww.geekcraft.ink")
        console().sendMessage("       §8适用于Bukkit: §71.12x-1.20.x §8当前: §7 ${Bukkit.getServer().version}")
        console().sendMessage("")
        // 加载服务器全息图
        hologramManager.loadAllHologram()
        // 加载玩家全息
        //hologramManager.loadPlayerHologram()
    }

    @Awake(LifeCycle.ACTIVE)
    private fun active() {
        //  VersionUpdate().checkUp()
    }

    private fun loadSettings() {
        groupConfig.clear()
        val loc = settings.getStringList("playerShield.local")
        val cloud = settings.getStringList("playerShield.cloud")
        shieldConfig = Shield(cloudUrl =  cloud).also {
            it.local.addAll(loc)
            it.initCloud()
        }
        settings.getList("playerHoloSetting")?.forEach {
            val map = it as Map<*, *>
            val priority = map["priority"].toString().toInt()
            val permission = map["permission"].toString()
            val costCondition = Group.CostCondition().apply {
                val c = map["costCondition"] as Map<*, *>
                condition = c["condition"].toString()
                allow = c["allow"].toString()
                deny = c["deny"].toString()
            }
            val visibleByDistance = map["visibleByDistance"].toString().toDouble()
            val haveAmount = map["haveAmount"].toString().toInt()
            val haveLineAmount = map["haveLineAmount"].toString().toInt()
            val haveCharLength = map["haveCharLength"].toString().toInt()
            val haveWorldName = map["haveWorldName"].toStringList()
            val variable = map["variable"].toStringList()
            groupConfig.add(
                Group(
                    priority, permission, costCondition, visibleByDistance,
                    haveAmount, haveLineAmount, haveCharLength,
                    haveWorldName, variable
                )
            )
        }
        groupConfig.sortBy { it.priority }
    }



}