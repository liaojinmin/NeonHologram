package me.neon.holo.hologram


import me.neon.holo.conf.Option
import me.neon.holo.event.HologramInteractEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.service.PlatformExecutor
import taboolib.common.util.random
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketReceiveEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * NeonHologram
 * me.neon.holo.hologram
 *
 * @author 老廖
 * @since 2024/11/26 21:11
 */
interface ComponentHandler {
    /**
     * 唯一ID标识
     */
    val uuid: UUID

    /**
     * 设置
     */
    val option: Option

    /**
     * 运行时的载体组件
     */
    val publicComponent: MutableList<Component>

    /**
     * 私有会话载体
     */
    val privateComponent: MutableMap<String, List<Component>>

    /**
     * 载体是否处于锁定状态
     */
    val synLock: AtomicBoolean

    /**
     * 是否处于活跃状态
     */
    var active: Boolean

    /**
     * 解析共有载体-生成
     */
    fun publicSpawn(player: Player)

    /**
     * 解析私有载体-生成
     */
    fun privateSpawn(player: Player, list: List<Component>?)

    /**
     * 解析共有载体-摧毁
     */
    fun publicDestroy(player: Player)

    /**
     * 解析私有载体-摧毁
     */
    fun privateDestroy(player: Player)

    /**
     * 摧毁公共容器，并删除可视玩家
     */
    fun publicDestroyAll()

    fun privateDestroyAll()

    fun destroyPublicView(player: Player)

    fun destroyPrivateView(player: Player)

    fun addTaskFunc(func: () -> Unit)

    fun tick()

    fun visibleByDistance(player: Player): Boolean

    fun lock() = synLock.set(true)

    fun isLock() = synLock.get()

    fun unlock() = synLock.set(false)

    companion object {

        /**
         * 活跃中的所有载体
         */
        private val carrierCache: ConcurrentHashMap<UUID, ComponentHandler> = ConcurrentHashMap()

        /**
         * 载体ID映射表
         */
        private val componentCache: MutableMap<Int, Pair<Component, Hologram?>> = mutableMapOf()

        /**
         * 可视 Task
         */
        private var viewPlayerTask: PlatformExecutor.PlatformTask? = null

        private var viewPlayerStart: Boolean = true

        /**
         * max 2147483647
         */
        private var index = 12999599 + random(0, 702)

        fun nextIndex(): Int {
            return index++
        }

        fun ComponentHandler.register(hologram: Hologram? = null) {
            if (carrierCache.containsKey(this.uuid)) return
            this.active = true
            carrierCache[this.uuid] = this

            // 缓存每个载体客户端ID
            this.publicComponent.forEach {
                componentCache[it.entityId] = it to hologram
            }
        }

        fun ComponentHandler.unregister() {
            this.active = false
            // 清理客户端 ID
            this.publicComponent.forEach {
                componentCache.remove(it.entityId)
            }
            // 清理私有会话 ID
            this.privateComponent.forEach { (_, value) ->
                value.forEach {
                    componentCache.remove(it.entityId)
                }
            }
            // 删除载体刷新缓存
            carrierCache.remove(this.uuid)
            this.publicDestroyAll()
            this.privateDestroyAll()
        }

        fun findComponent(int: Int): Component? {
            return componentCache[int]?.first
        }

        fun addComponent(component: Component, hologram: Hologram?) {
            componentCache[component.entityId] = component to hologram
        }

        fun delComponent(component: Component) {
            componentCache.remove(component.entityId)
        }

        fun findPair(int: Int): Pair<Component, Hologram?>? {
            return componentCache[int]
        }

        @Awake(LifeCycle.DISABLE)
        private fun stop() {
            viewPlayerStart = false
            viewPlayerTask?.cancel()
        }

        @Awake(LifeCycle.ACTIVE)
        private fun updateTask() {
            viewPlayerTask?.cancel()
            viewPlayerTask = submitAsync {
                while (viewPlayerStart) {
                    try {
                        Thread.sleep(50)
                        val a = carrierCache.entries.iterator()
                        while (a.hasNext()) {
                            val armor = a.next().value
                            val players = Bukkit.getOnlinePlayers().toList()
                            for (player in players) {
                                if (player.isOnline()) {
                                    if (!armor.isLock()) {
                                        if (armor.visibleByDistance(player)) {
                                            armor.publicSpawn(player)
                                            armor.privateSpawn(player, null)
                                        } else {
                                            armor.publicDestroy(player)
                                            armor.privateDestroy(player)
                                        }
                                        armor.tick()
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        break
                    }
                }
            }
        }

        @SubscribeEvent
        private fun onReceive(e: PacketReceiveEvent) {
            if (e.packet.name == "PacketPlayInUseEntity") {
                val (component, hologram) = findPair(e.packet.read<Int>("a")!!) ?: return
                if (component.isView(e.player)) {
                    val event = if (MinecraftVersion.isUniversal) {
                        val action = e.packet.source.getProperty<Any>("b", remap = false)!!
                        val name = action.javaClass.name
                        when {
                            // 左键
                            name.endsWith("PacketPlayInUseEntity\$1") -> {
                                HologramInteractEvent(e.player, hologram, component,
                                    if (e.player.isSneaking) ComponentAction.SHIFT_LEFT_CLICK else ComponentAction.LEFT_CLICK,
                                    true
                                )
                            }
                            // 右键
                            name.endsWith("PacketPlayInUseEntity\$e") -> {
                                HologramInteractEvent(e.player, hologram, component,
                                    if (e.player.isSneaking) ComponentAction.SHIFT_RIGHT_CLICK else ComponentAction.RIGHT_CLICK,
                                    action.getProperty<Any>("a", remap = false).toString() == "MAIN_HAND"
                                )
                            }
                            else -> {
                                HologramInteractEvent(e.player, hologram, component, ComponentAction.ALL, false)
                            }
                        }
                    } else {
                        // 低版本 EnumEntityUseAction 为枚举类型
                        // 通过字符串判断点击方式
                        when (e.packet.source.getProperty<Any>("action")!!.toString()) {
                            "ATTACK" -> {
                                HologramInteractEvent(e.player, hologram, component,
                                    if (e.player.isSneaking) ComponentAction.SHIFT_LEFT_CLICK else ComponentAction.LEFT_CLICK,
                                    true
                                )
                            }
                            "INTERACT_AT" -> {
                                HologramInteractEvent(e.player, hologram, component,
                                    if (e.player.isSneaking) ComponentAction.SHIFT_RIGHT_CLICK else ComponentAction.RIGHT_CLICK,
                                    e.packet.read<Any>("d").toString() == "MAIN_HAND"
                                )
                            }
                            else -> {
                                HologramInteractEvent(e.player, hologram, component, ComponentAction.ALL, false)
                            }
                        }
                    }
                    submit {
                        if (event.callEvent()) {
                            //println("click ho ${hologram?.uniqueId}")
                            event.hologram?.onClick(event.player, event.action, event.isMainHand)
                            event.component.click(event.player, event.action, event.isMainHand)
                        }
                    }
                }
            }
        }
    }
}