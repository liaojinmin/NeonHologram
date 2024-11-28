package me.neon.holo.player

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import me.neon.holo.HologramAPI
import me.neon.holo.conf.Option
import me.neon.holo.utils.forFile
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submitAsync
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.sendLang
import taboolib.platform.util.toProxyLocation
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

/**
 * GeekHologram
 * me.geek.holo.bukkit
 *
 * @author 老廖
 * @since 2023/9/23 14:02
 */
@Deprecated(message = "玩家类型全息咱不维护，可能弃用")
object PlayerHologramManager {

    internal val regex: Regex = Regex("^[\u4E00-\u9FA5A-Za-z0-9_]+\$")

    internal val gsonBuilder: GsonBuilder = GsonBuilder()
        .setPrettyPrinting()
        .setExclusionStrategies(object : ExclusionStrategy {
        override fun shouldSkipField(f: FieldAttributes): Boolean {
            return f.getAnnotation(Expose::class.java) != null
        }

        override fun shouldSkipClass(clazz: Class<*>): Boolean {
            return clazz.getAnnotation(Expose::class.java) != null
        }
    })

    internal val defPlayerHologramFile by lazy {
        val file = File(BukkitPlugin.getInstance().dataFolder, "player")
        if (!file.exists()) {
            file.mkdir()
        }
        file
    }

    internal val playerHologramCache: ConcurrentHashMap<UUID, PlayerHologramData> = ConcurrentHashMap()

    fun find(uuid: UUID, uid: String): PlayerHologram? {
        return playerHologramCache[uuid]?.data?.find { it.uniqueId == uid }
    }

    fun remove(uuid: UUID, uid: String): Boolean {
        playerHologramCache[uuid]?.let {
            val iterator = it.data.listIterator()
            while (iterator.hasNext()) {
                val data = iterator.next()
                if (data.uniqueId == uid) {
                    data.delete()
                    iterator.remove()
                    submitAsync {
                        it.amount--
                        it.save()
                    }
                    return true
                }
            }
        }
        return false
    }

    fun create(sender: Player, uid: String, context: List<String>, location: org.bukkit.Location?): PlayerHologram? {
        HologramAPI.groupConfig.find { sender.hasPermission(it.permission) }?.let {
            val playerHologramData = playerHologramCache.computeIfAbsent(sender.uniqueId) { _ ->
                PlayerHologramData(sender.uniqueId)
            }
            // 检查 - 黑名单世界 haveWorldName
            if (!it.haveWorldName.contains(sender.world.name)) {
                sender.sendLang("玩家-创建失败-不允许的世界")
                return null
            }

            // 检查 - 可创建数量 haveAmount
            if (playerHologramData.amount >= it.haveAmount) {
                sender.sendLang("玩家-创建失败-超过数量限制")
                return null
            }

            // 检查 - ID 是否重复
            if (playerHologramData.data.find { d -> d.uniqueId == "#_$uid" } != null) {
                sender.sendLang("玩家-创建失败-重复的ID")
                return null
            }

            // 检查 - 字符长度
            if (uid.length > 6) {
                sender.sendLang("玩家-创建失败-文本过长", "全息ID")
                return null
            }

            // 检查 - ID 特殊字符
            if (!regex.matches(uid)) {
                sender.sendLang("玩家-创建失败-存在屏蔽词汇", "ID存在特殊字符")
                return null
            }

            // 检查 - ID 屏蔽词
            HologramAPI.shieldConfig.local.find { value -> uid.contains(value) }?.let { pass ->
                // 检查 - 存在屏蔽词
                sender.sendLang("玩家-创建失败-存在屏蔽词汇", pass)
                return null
            }

            // 检查 - 字符长度 haveCharLength
            for (text in context) {

                if (text.length > it.haveCharLength) {
                    // 检查 - 字符长度 超出
                    sender.sendLang("玩家-创建失败-文本过长", "全息文本")
                    return null
                }

                // 检查 - 文本 特殊字符
                if (!regex.matches(text)) {
                    sender.sendLang("玩家-创建失败-存在屏蔽词汇", "文本存在特殊字符")
                    return null
                }

                // 检查 - 文本 屏蔽词
                HologramAPI.shieldConfig.local.find { value -> text.contains(value) }?.let { pass ->
                    // 检查 - 存在屏蔽词
                    sender.sendLang("玩家-创建失败-存在屏蔽词汇", pass)
                    return null
                }
            }
            // 检查 - 创建花费 costCondition
            if (it.costCondition.eval(sender)) {
                // 替换不在列表的变量
                // pass TODO(暂时过滤的%%，不考虑替换)

                val option = Option(location?.toProxyLocation() ?: sender.location.toProxyLocation(), it.visibleByDistance)
                val hologram = PlayerHologram(sender.uniqueId, "#_$uid", option)
                // 添加默认文本，不可移除的文本
                hologram.context.add("§8${hologram.uniqueId} by ${sender.name}")
                context.forEach { text -> hologram.context.add(text) }
                playerHologramData.data.add(hologram)
                playerHologramData.amount++
                submitAsync {
                    playerHologramData.save()
                }
                hologram.spawn()
                sender.sendLang("玩家-创建成功")
                return hologram
            }
        }
        return null
    }

    @SubscribeEvent
    private fun save(event: PlayerQuitEvent) {
        submitAsync {
            playerHologramCache[event.player.uniqueId]?.save()
        }
    }

    @Awake(LifeCycle.ENABLE)
    private fun loadPlayerHologram() {
        measureTimeMillis {
            forFile(defPlayerHologramFile, ".json").forEach { file ->
                InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8).use { sr ->
                    BufferedReader(sr).use {  bf ->
                        val hologram = gsonBuilder.create().fromJson(bf, PlayerHologramData::class.java)
                        playerHologramCache.putIfAbsent(hologram.uuid, hologram)
                    }
                }
            }
            playerHologramCache.entries.forEach {
                it.value.data.forEach { holo ->
                    holo.context.forEach { t ->
                   //     info("加载时: $t")
                    }
                    holo.spawn()
                }
            }
        }.also { info("加载 ${playerHologramCache.size}个 玩家全息... (耗时 $it ms)") }
    }






}