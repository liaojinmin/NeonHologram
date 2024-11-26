package me.neon.holo.hologram

import me.neon.holo.conf.Option
import me.neon.holo.event.HologramCreateEvent
import me.neon.holo.event.HologramDeleteEvent
import me.neon.holo.event.HologramInteractEvent
import me.neon.holo.event.HologramReloadEvent
import me.neon.holo.hologram.ComponentHandler.Companion.findPair
import me.neon.holo.utils.forFile
import me.neon.holo.utils.toStringList
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.event.SubscribeEvent

import taboolib.common.platform.function.*
import taboolib.common.util.Location
import taboolib.common5.FileWatcher
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.chat.colored
import taboolib.module.configuration.ConfigFile
import taboolib.module.configuration.ConfigSection
import taboolib.module.configuration.SecuredFile
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketReceiveEvent
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2023/6/28 21:42
 */
object HologramManagerImpl: HologramManager {

    @Awake(LifeCycle.INIT)
    private fun init() {
        PlatformFactory.registerAPI<HologramManager>(this)
    }

    /**
     * 全息缓存
     */
    private val hologramCache: MutableSet<Hologram> = mutableSetOf()

    private val itemParse: Regex by lazy { Regex("\\{item?[:=] ?(.*?)}") }

    private val highSetParse: Regex by lazy { Regex("\\{highSet?[:=] ?([0-9.]+)}") }

    private val wideSetParse: Regex by lazy { Regex("\\{wideSet?[:=] ?([0-9.]+)}") }

    private val updateParse: Regex by lazy { Regex("\\{(update|refresh)?[:=] ?([0-9.]+)}") }

    private val defHologramFile by lazy {
        val dir = File(getDataFolder(), "hologram")
        if (!dir.exists()) {
            arrayOf(
                "hologram/def.yml",
                "hologram/multipage.yml",
                "hologram/ScrollHologram.yml"
            ).forEach { releaseResourceFile(it, true) }
        }
        dir
    }

    override val defLocation by lazy {
        Location("world", 0.0, 78.0, 0.0)
    }

    override val defContext by lazy {
        listOf(
            "Hello &eThis is &b$pluginId",
            "&7time: &e%server_time%",
            "&7name: &6%player_name%"
        )
    }

    override val defLineSpacing: Double = 0.25

    override val defVisibleByDistance: Double = 32.0

    override fun findHologram(entityId: Int): Hologram? {
        val iterator = hologramCache.iterator()
        while (iterator.hasNext()) {
            val h = iterator.next()
            if (h.claimComponent(entityId)) {
                return h
            }
        }
        return null
    }

    override fun findHologram(uniqueId: String): Hologram? {
        return hologramCache.find { it.uniqueId == uniqueId }
    }

    override fun deleteHologram(id: String, save: Boolean): Boolean {
        findHologram(id)?.let {
            val event = HologramDeleteEvent(it, save)
            event.callEvent()
            if (!event.isCancelled) {
                if (hologramCache.remove(it)) {
                    it.delete()
                    // 是否删除目录
                    if (event.save) {
                        File(it.loaderPath).delete()
                    }
                    return true
                }
            }
        }
        return false
    }

    override fun createHologram(
        id: String,
        location: Location,
        text: List<String>,
        lineSpacing: Double,
        visibleByDistance: Double,
        save: Boolean
    ): Hologram? {
        if (findHologram(id) != null) {
            return findHologram(id)!!
        }
        val event = HologramCreateEvent(id, location, text, lineSpacing, visibleByDistance, save)
        event.callEvent()
        if (!event.isCancelled) {
            val path = File(defHologramFile, "${event.id}.yml")
            val option = Option(event.location, event.visibleByDistance, event.lineSpacing, "")
            val hologram = NormalHologram(event.id, option, path.path)
            if (hologram.hologramContext.isEmpty()) {
                hologram.hologramContext[0] = HologramContext(mutableListOf(), emptyMap())
            }
            event.text.forEach {
                hologram.append(it)
            }

            // 添加缓存
            hologramCache.add(hologram)

            // 开始展示
            hologram.spawn()

            if (event.save) {
                // 保存
                hologram.save(path)
                // 添加自动重载
                val var1 = SecuredFile().apply { file = path }
                var1.onReload {
                    loadFile(var1, true)
                }
            }
            return hologram
        }
        return null
    }

    override fun parseComponent(string: String): Component {
        var text = string
        val item = itemParse.find(text)?.groupValues?.get(1)?.also {
            text = text.replace(itemParse, "")
        }
        val high = highSetParse.find(text)?.groupValues?.get(1)?.also {
            text = text.replace(highSetParse, "")
        }?.toDoubleOrNull() ?: 0.0

        val wide = wideSetParse.find(text)?.groupValues?.get(1)?.also {
            text = text.replace(wideSetParse, "")
        }?.toDoubleOrNull() ?: 0.0
        val update = updateParse.find(text)?.groupValues?.get(2)?.also {
            text = text.replace(updateParse, "")
        }?.toIntOrNull() ?: 0
        return if (item != null) {
            // item = 暂未解析的物品特征
            HologramItem(item, high, wide, update)
        } else {
            HologramText(text, high, wide, update)
        }
    }

    override fun unloadAllHologram() {
        hologramCache.forEach {
            it.lock()
            it.hologramContext.values.forEach { context ->
                context.carrierContext.forEach(Component::destroyAll)
            }
        }
        hologramCache.clear()
    }

    override fun loadAllHologram() {
        hologramCache.removeIf {
            it.delete()
        }
        val list = mutableListOf<File>()
        measureTimeMillis {
            list.also {
                it.addAll(forFile(defHologramFile, ".yml"))
            }
            list.forEach { file ->
                SecuredFile.loadConfiguration(file).also { se ->
                    // 加载
                    loadFile(se)
                    // 添加自动重载事件
                    FileWatcher.INSTANCE.addSimpleListener(file) { _ ->
                        se.loadFromFile(file)
                    }
                    // 重载
                    se.onReload { loadFile(se, true) }
                }
            }
            hologramCache.forEach { it.spawn() }
        }.also { info("加载 ${hologramCache.size}个 全息配置... (耗时 $it ms)") }
    }

    fun getHologramKeyList() = hologramCache.map { it.uniqueId }

    fun getHologramList() = hologramCache.toList()

    private fun loadFile(var1: ConfigFile, isReload: Boolean = false) {
        val id = var1.getString("hologram.uniqueId") ?: error("uniqueId 失效 ${var1.file}")
        val location = try {
            val local = var1.getString("hologram.option.location")?.split(";") ?: error("目录: ${var1.file} 获取 location 失败...")
            Location(
                local[0],
                local[1].toDouble(),
                local[2].toDouble(),
                local[3].toDouble()
            )
        } catch (ex: Exception) {
            warning("加载 ${var1.file} 时发生错误，坐标参数异常...")
            defLocation
        }
        val visibleByDistance = var1.getDouble("hologram.option.visibleByDistance")
        val lineSpacing = var1.getDouble("hologram.option.lineSpacing")
        val visibleCondition = var1.getString("hologram.option.visibleCondition") ?: ""
        val isScroll = var1.getBoolean("hologram.option.isScroll", false)

        val option = Option(location, visibleByDistance, lineSpacing, visibleCondition, isScroll)

        fun parseAction(source: MutableMap<ComponentAction, String>, data: Map<*, *>) {
            data.forEach { (key, value) ->
                source[ComponentAction.of(key.toString())] = value.toStringList().joinToString("\n").colored()
            }
        }

        val context = var1["hologram.context"]
        var hologram: Hologram? = null
        if (context is List<*>) {
            if (context.isEmpty()) return

            // 构建动作
            val click = mutableMapOf<ComponentAction, String>()
            if (var1["hologram.action"] != null) {
                (var1["hologram.action"] as ConfigSection).getValues(true).forEach { (key, value) ->
                    click[ComponentAction.of(key)] = value.toStringList().joinToString("\n").colored()
                }
            }

            // 动作与文本分离写法
            if (context[0] is String) {

                val holo = NormalHologram(id, option, var1.file!!.path)
                holo.hologramContext[0] = HologramContext((context as List<String>).colored().toMutableList(), click)
                hologram = holo

            } else {
                val holo = if (option.isScroll) {
                    ScrollHologram(id, option, var1.file!!.path)
                } else MutableHologram(id, option, var1.file!!.path)

                // 加载文本
                for ((index, any) in context.withIndex()) {
                    if (any is List<*>) {
                        if (any.isNotEmpty()) {
                            if (any[0] is String) {
                                holo.hologramContext[index] = HologramContext((any as List<String>).colored().toMutableList(), click)
                            }
                        }
                    } else {
                        val data = any as Map<String, Any>
                        val clicks = mutableMapOf<ComponentAction, String>()
                        val obj = data["action"]
                        if (obj != null) {
                            if (obj is Map<*, *>) {
                                parseAction(clicks, data["action"] as Map<*, *>)
                            }
                        }
                        holo.hologramContext[index] = HologramContext((data["text"] as List<String>).colored().toMutableList(), clicks)
                    }
                }

                hologram = holo
            }
        }

        hologram?.let {
            HologramReloadEvent(it).callEvent()
            if (isReload) {
                // 2023-9-26 0:07 发现直接的删除摧毁有概率未销毁，所以改用上锁机制
                val iterator = hologramCache.iterator()
                while (iterator.hasNext()) {
                    val data = iterator.next()
                    // 如果ID不同尝试匹配目录，防止因为改名导致的无法匹配
                    if (data.uniqueId == id || data.loaderPath == var1.file!!.path) {
                        // 上锁,防止摧毁时被生成
                        data.lock()
                        data.delete()
                        iterator.remove()
                        break
                    }
                }
                info("已自动重新加载 $id 全息图的更改...")
                it.spawn()
            }
            hologramCache.add(it)
        }
    }


}