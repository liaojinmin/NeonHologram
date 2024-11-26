package me.neon.holo.hologram

import me.neon.holo.conf.Option
import org.bukkit.entity.Player
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.platform.type.BukkitPlayer

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * NeonHologram
 * me.neon.holo.api
 *
 * @author 老廖
 * @since  2023/6/28 20:36
 */
class ComponentHandlerImpl(
    override val option: Option
): ComponentHandler {

    override val uuid: UUID = UUID.randomUUID()

    override val publicComponent: CopyOnWriteArrayList<Component> = CopyOnWriteArrayList()

    override val privateComponent: ConcurrentHashMap<String, List<Component>> = ConcurrentHashMap()

    override val synLock: AtomicBoolean = AtomicBoolean(false)

    override var active: Boolean = false

    private val disPrPlayer: MutableSet<String> = mutableSetOf()

    private val disPublicPlayer: MutableSet<String> = mutableSetOf()

    private val task: MutableSet<() -> Unit> = mutableSetOf()

    override fun publicSpawn(player: Player) {
        if (isLock()) return

        if (privateComponent.containsKey(player.name)) return

        if (disPublicPlayer.add(player.name)) {
          //  info("发送公共生成 parsePublicSpawn by player ${player.name}")
            var lines = 0.0
            val iterator = publicComponent.listIterator(publicComponent.size)
         //   info("大小: ${publicCarrier.size}")
            while (iterator.hasPrevious()) {
                val data = iterator.previous()
              //  info("文本: ${data.displayName}")
                if (data.viewPlayer.add(player.name)) {
                    lines += (option.lineSpacing + data.highSet)
                    data.spawn(player, option.location.clone().add(data.wideSet, lines, data.wideSet))
                }
            }
        }
    }

    override fun privateSpawn(player: Player, list: List<Component>?) {
        if (isLock()) return

        if (disPrPlayer.add(player.name)) {
            val carrier: List<Component>? = if (list.isNullOrEmpty()) {
                privateComponent[player.name]
            } else {
                privateComponent[player.name] = list
                list
            }
            carrier?.let { out ->
           //     info("发送私有生成 parsePrivateSpawn by player ${player.name}")
                var line = 0.0
                val iterator = out.listIterator(out.size)
                while (iterator.hasPrevious()) {
                    val data = iterator.previous()
                    if (data.viewPlayer.add(player.name)) {
                        line += option.lineSpacing + data.highSet
                        data.spawn(player, option.location.clone().add(data.wideSet, line, data.wideSet))
                    }
                }
            }
        }

    }

    override fun publicDestroy(player: Player) {
        if (disPublicPlayer.remove(player.name)) {
          //  info("摧毁公共视图 by player ${player.name}")
            publicComponent.forEach { it.destroy(player) }
        }
    }

    override fun privateDestroy(player: Player) {
        if (disPrPlayer.remove(player.name)) {
            privateComponent.remove(player.name)?.forEach { it.destroy(player) }
        }
    }


    override fun destroyPublicView(player: Player) {
        if (disPublicPlayer.remove(player.name)) {
            publicComponent.forEach { it.removeView(player) }
        }
    }

    override fun destroyPrivateView(player: Player) {
        if (disPrPlayer.remove(player.name)) {
            privateComponent.remove(player.name)?.forEach { it.removeView(player) }
        }
    }

    override fun publicDestroyAll() {
        publicComponent.forEach { it.destroyAll() }
        publicComponent.clear()
        disPublicPlayer.clear()
    }

    override fun privateDestroyAll() {
        privateComponent.values.forEach { it.forEach { carrier -> carrier.destroyAll() } }
        privateComponent.clear()
        disPrPlayer.clear()
    }

    override fun addTaskFunc(func: () -> Unit) {
        task.add(func)
    }

    override fun tick() {
        privateComponent.values.forEach { it.forEach { carrier -> carrier.tick() } }
        publicComponent.forEach { it.tick() }
        task.forEach { it.invoke() }
    }

    override fun visibleByDistance(player: Player): Boolean {

        if (option.location.world == null) return false

        if (player.world.name != option.location.world) return false


        if (option.distance(player.location) > option.visibleByDistance) return false


        if (option.visibleCondition.isEmpty() || option.visibleCondition == "true") {
            return true
        }

        val option = ScriptOptions(sender = BukkitPlayer(player), namespace = listOf("kether, NeonHologram"))
        return KetherShell.eval(this.option.visibleCondition, option).get() as? Boolean ?: false
    }
}