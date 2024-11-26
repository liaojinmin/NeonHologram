package me.neon.holo.player

import me.neon.holo.HologramAPI
import org.bukkit.entity.Player

import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.Components

import taboolib.platform.util.nextChat
import taboolib.platform.util.sendLang

/**
 * NeonHologram
 * me.neon.holo.api.player
 *
 * @author 老廖
 * @since 2023/9/23 0:42
 */
@Deprecated(message = "玩家类型全息咱不维护，可能弃用")
@CommandHeader(name = "NeonHologram:Player", aliases = ["PHD","phd"], permissionDefault = PermissionDefault.TRUE)
object PlayerCommand {

    @CommandBody(permission = "hologram.player.command.create", optional = true)
    val create = subCommand {
        dynamic("id") {
            suggestUncheck { listOf("为你的全息取名") }

            dynamic("line") {
                suggestUncheck { listOf("第一行内容") }

                execute<Player> { sender, context, argument ->
                    val id = context["id"]
                    val text = context["line"]
                    val hologram = PlayerHologramManager.create(sender, id, listOf(text), null)
                    if (hologram != null) {
                        sender.createOperate(hologram)
                    }
                }
            }
        }
    }
    @CommandBody(permission = "hologram.player.command.edit", optional = true)
    val edit = subCommand {
        dynamic("id") {
            suggestion<Player> { sender, _ ->
                PlayerHologramManager.playerHologramCache[sender.uniqueId]?.data?.map { it.uniqueId }
                    ?: listOf("你没有可以的全息")
            }
            execute<Player> { sender, _, arg ->
                PlayerHologramManager.playerHologramCache[sender.uniqueId]?.let { data ->
                    data.data.find { it.uniqueId == arg }?.let { playerHologram ->
                        sender.createOperate(playerHologram)
                    }  ?: sender.sendLang("全息-查找-不存在", arg)
                }
            }
        }
    }
    @CommandBody(permission = "hologram.player.command.del", optional = true)
    val del = subCommand {
        dynamic("id") {
            suggestion<Player> { sender, _ ->
                PlayerHologramManager.playerHologramCache[sender.uniqueId]?.data?.map { it.uniqueId }
                    ?: listOf("你没有可以的全息")
            }
            execute<Player> { sender, context, _ ->
                val id = context["id"]
                PlayerHologramManager.playerHologramCache[sender.uniqueId]?.let { data ->
                    data.data.find { it.uniqueId == id }?.let { playerHologram ->
                        val size = playerHologram.context.size
                        if (size >= 2) {
                            playerHologram.subtract(size-1)
                            sender.sendLang(
                                "全息-删除一行-成功",
                                id,
                                size-1
                            )
                        } else {
                            sender.sendLang("玩家-删除行失败-不可删除")
                        }
                    } ?: sender.sendLang("玩家-行操作-不存在")
                }
            }
        }
    }
    @CommandBody(permission = "hologram.player.command.remove", optional = true)
    val remove = subCommand {
        dynamic("id") {
            suggestion<Player> { sender, _ ->
                PlayerHologramManager.playerHologramCache[sender.uniqueId]?.data?.map { it.uniqueId }
                    ?: listOf("你没有可以的全息")
            }
            execute<Player> { sender, context, _ ->
                val id = context["id"]
                PlayerHologramManager.playerHologramCache[sender.uniqueId]?.let { data ->
                    data.data.find { it.uniqueId == id }?.let { playerHologram ->
                        data.data.removeIf { it.uniqueId == playerHologram.uniqueId }
                        playerHologram.delete()
                        data.amount--
                        sender.sendLang("全息-删除成功", id)
                    } ?: sender.sendLang("玩家-行操作-不存在")
                }
            }
        }
    }
    @CommandBody(permission = "hologram.player.command.add", optional = true)
    val add = subCommand {
        dynamic("id") {
            suggestion<Player> { sender, _ ->
                PlayerHologramManager.playerHologramCache[sender.uniqueId]?.data?.map { it.uniqueId } ?: listOf("你没有可以的全息")
            }
            execute<Player> { sender, context, _ ->
                val id = context["id"]
                HologramAPI.groupConfig.find { sender.hasPermission(it.permission) }?.let { config ->
                    PlayerHologramManager.playerHologramCache[sender.uniqueId]?.let { data ->

                        data.data.find { it.uniqueId == id }?.let { playerHologram ->
                            if (playerHologram.context.size - 1 <= config.haveLineAmount) {
                                sender.sendLang("全息-添加新行-捕获")
                                sender.nextChat { text ->
                                    // 检查 - 字符长度 haveCharLength
                                    if (text.length > config.haveCharLength) {
                                        // 检查 - 字符长度 超出
                                        sender.sendLang("玩家-创建失败-文本过长", "全息文本")
                                    } else

                                    // 检查 - 文本 特殊字符
                                    if (!PlayerHologramManager.regex.matches(text)) {
                                        sender.sendLang("玩家-创建失败-存在屏蔽词汇", "文本存在特殊字符")
                                    } else {

                                        // 检查 - 文本 屏蔽词
                                        val pass = HologramAPI.shieldConfig.local.find { value -> text.contains(value) }

                                        if (pass != null) {
                                            // 检查 - 存在屏蔽词
                                            sender.sendLang("玩家-创建失败-存在屏蔽词汇", pass)
                                        } else {
                                            // 成功
                                            playerHologram.append(text)
                                            sender.sendLang("全息-添加新行-成功", id, text)
                                        }
                                    }
                                }
                            } else {
                                sender.sendLang("玩家-添加行失败-超出限制")
                            }
                        } ?: sender.sendLang("玩家-行操作-不存在")
                    }
                }
            }
        }
    }

    private fun Player.createOperate(hologram: PlayerHologram) {
        val comp = Components.empty().append("").newLine()
        comp.append("  §6全息单位 §8> §e${hologram.uniqueId}")
            .newLine()
        comp.append("  §7├ §6操作 §8> ")
            .newLine()
        comp.append("  §7│     §8[§a添加新行§8] ").clickRunCommand("/phd add ${hologram.uniqueId}")
            .append("§8[§a移除一行§8] ").clickRunCommand("/phd del ${hologram.uniqueId}")
            .newLine()
        comp.append("  §7│     §8[§e删除全息§8] ").clickRunCommand("/hd remove ${hologram.uniqueId}")
            .newLine()
        comp.append("  §7└ §6行数 §8> §e${hologram.context.size}")
            .newLine()
        comp.sendTo(adaptPlayer(this))
    }

}