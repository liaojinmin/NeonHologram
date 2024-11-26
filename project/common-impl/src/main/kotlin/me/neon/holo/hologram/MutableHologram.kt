package me.neon.holo.hologram

import me.neon.holo.conf.Option
import me.neon.holo.hologram.ComponentHandler.Companion.register
import me.neon.holo.hologram.ComponentHandler.Companion.unregister
import org.bukkit.entity.Player
import taboolib.module.chat.colored

import java.io.File

/**
 * @作者: 老廖
 * @时间: 2023/6/28 21:12
 * @包: me.geek.holo.module.hologram
 */
class MutableHologram(
    override val uniqueId: String,
    option: Option,
    override val loaderPath: String,
): Hologram(option) {

    override var owner: String = "server"

    override val hologram: ComponentHandler = ComponentHandlerImpl(option)

    /**
     * 当前页面
     */
    private val pagePlayer: MutableMap<String, Int> = mutableMapOf()

    override fun onClick(player: Player, action: ComponentAction, isMainHand: Boolean) {
        if (hologramContext.isEmpty() || !isMainHand) return
        val page = pagePlayer[player.name] ?: 0
        hologramContext[page]?.evalAction(player, this, action)
    }

    override fun append(name: String, page: Int): Hologram {

        if (this.hologramContext.isEmpty()) {
            this.hologramContext[0] = HologramContext(mutableListOf(name.colored()), emptyMap())
        }

        hologram.lock()
        val holo = HologramManagerImpl.parseComponent(name)
        if (page == 0) {
            hologram.lock()

            hologram.publicDestroyAll()
            hologram.privateDestroyAll()
            pagePlayer.clear()
            hologramContext[page]?.let {
                it.originContext.add(name.colored())
                it.carrierContext.add(holo)
                hologram.publicComponent.add(holo)
            }
        } else {
            hologramContext[page]?.let {
                it.originContext.add(name.colored())
                it.carrierContext.add(holo)
            }
        }

        unlock()
        save(File(loaderPath))
        return this
    }

    override fun subtract(line: Int, page: Int): Hologram {
        if (hologramContext.isEmpty()) {
            return this
        }
        lock()
        if (page == 0 && hologram.publicComponent.size > line) {
            hologram.publicComponent.removeAt(line)?.destroyAll()
        }

        hologramContext[page]?.let {
            if (it.originContext.size > line) {
                it.originContext.removeAt(line)
            }
            if (it.carrierContext.size > line) {
                it.carrierContext.removeAt(line).destroyAll()
            }
        }

        save(File(loaderPath))
        unlock()
        return this
    }




    /**
     * 对指定玩家切换上一页
     */
    fun lastPage(player: Player): MutableHologram {
        if (hologramContext.size < 2) return this

        val page = pagePlayer[player.name] ?: return this

        if (page != 0) {

            lock()

            // 摧毁旧的
            hologram.privateDestroy(player)

            pagePlayer[player.name] = page - 1

            hologramContext[page - 1]?.let {
                // 重新解析自有变量
                it.carrierContext.forEach { a ->
                    a.displayName = parsePlaceholder(a.displayName, player)
                }
                it.registerAll(this)

                // 生成 改用被动式
                hologram.privateComponent[player.name] = it.carrierContext
            }

            unlock()
        }
        return this
    }

    fun nextPage(player: Player): MutableHologram {
        // 如果只有一页则不可下跳
        if (hologramContext.size < 2) return this

        var page = pagePlayer.putIfAbsent(player.name, 0)

        lock()

        if (page == null) {
            page = 0
            hologram.publicDestroy(player)
        }
        if (page == 0) {
            hologram.publicDestroy(player)
        }

        if (page + 1 < hologramContext.size) {

            pagePlayer[player.name] = page + 1

            hologramContext[page + 1]?.let {

                // 重新解析自有变量
                it.carrierContext.forEach { a ->
                    a.displayName = parsePlaceholder(a.displayName, player)
                }
                it.registerAll(this)

                // 移除旧的，如果有
                hologram.privateDestroy(player)

                // 生成 改用被动式
                hologram.privateComponent[player.name] = it.carrierContext
            }
        }

        hologram.unlock()
        return this
    }

    override fun spawn(): Hologram {

        hologramContext[0]?.let {

            it.carrierContext.forEach { text ->
                if (text !is HologramItem) {
                    text.displayName = parsePlaceholder(text.displayName)
                }
                this.hologram.publicComponent.add(text)
            }
            this.hologram.register(this)
        } ?: error("全息页面 0 不存在，请检查配置文件...")
        return this
    }

    override fun delete(): Boolean {
        this.hologram.lock()
        pagePlayer.clear()
        this.hologram.unregister()
        this.hologramContext.values.forEach{
            it.unRegisterAll()
        }
        this.hologram.unlock()
        return true
    }

    private fun parsePlaceholder(test: String, player: Player? = null): String {
        var now = 1
        if (player != null) {
            now = (pagePlayer[player.name] ?: 1) + 1
        }
        return test.replace("{now_page}", now.toString())
            .replace("{max_page}", hologramContext.size.toString())
            .colored()
    }


}