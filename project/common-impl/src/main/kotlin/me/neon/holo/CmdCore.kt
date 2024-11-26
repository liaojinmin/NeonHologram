package me.neon.holo

import me.neon.holo.hologram.Hologram
import me.neon.holo.migrator.TrHDMigrator
import me.neon.holo.hologram.HologramManagerImpl
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.common.platform.function.submitAsync
import taboolib.expansion.createHelper
import taboolib.module.chat.Components
import taboolib.module.lang.sendLang
import taboolib.platform.type.BukkitPlayer
import taboolib.platform.util.nextChat

@CommandHeader(name = "neonhologram", aliases = ["hologram","neonhd", "hd"], permissionDefault = PermissionDefault.TRUE)
object CmdCore {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "neonhologram.command.create", optional = true)
    val create = subCommand {
        dynamic("id") {
            dynamic("save") {
                suggest { listOf("true", "false") }
                execute<ProxyPlayer> { sender, context, argument ->
                    if (HologramManagerImpl.findHologram(context["id"]) == null) {
                        HologramManagerImpl.createHologram(
                            context["id"],
                            sender.location.clone().add(0.0, 2.5, 0.0),
                            save = argument.toBoolean()
                        )
                        sender.sendLang("全息-创建成功", context["id"])
                    } else {
                        sender.sendLang("全息-创建失败-重复", context["id"])
                    }
                }
            }
            execute<ProxyPlayer> { sender, _, argument ->
                if (HologramManagerImpl.findHologram(argument) == null) {
                    HologramManagerImpl.createHologram(argument, sender.location, save = true)?.let {
                        sender.sendLang("全息-创建成功", argument)
                        sender.createOperate(it)
                    }
                } else {
                    sender.sendLang("全息-创建失败-重复", argument)
                }
            }
        }
    }

    @CommandBody(permission = "neonhologram.command.delete", optional = true)
    val delete = subCommand {
        dynamic("id") {
            suggest { HologramManagerImpl.getHologramKeyList() }
            execute<ProxyPlayer> { sender, _, argument ->
                if (HologramManagerImpl.deleteHologram(argument)) {
                    sender.sendLang("全息-删除成功", argument)
                }
            }
        }
    }

    @CommandBody(permission = "neonhologram.command.reload", optional = true)
    val reload = subCommand {
        execute<ProxyCommandSender> { _, _, _ ->
            HologramManagerImpl.loadAllHologram()
        }
    }

    @CommandBody(permission = "neonhologram.command.teleport", optional = true)
    val teleport = subCommand {
        dynamic("id") {
            suggestUncheck { HologramManagerImpl.getHologramKeyList() }
            execute<ProxyPlayer> { sender, _, argument ->
                HologramManagerImpl.findHologram(argument)?.let {
                    sender.teleport(it.option.location)
                    sender.sendLang("全息-传送成功", argument)
                } ?: sender.sendLang("全息-查找-不存在", argument)
            }
        }
    }

    @CommandBody(permission = "neonhologram.command.moveHere", optional = true)
    val moveHere = subCommand {
        dynamic("id") {
            suggestUncheck { HologramManagerImpl.getHologramKeyList() }
            execute<ProxyPlayer> { sender, _, argument ->
                HologramManagerImpl.findHologram(argument)?.let {
                    it.move(sender.location)
                    sender.sendLang("全息-移动成功", argument, "${sender.location.world};${sender.location.x};${sender.location.y-1};${sender.location.z}")
                } ?: sender.sendLang("全息-查找-不存在", argument)
            }
        }
    }

    @CommandBody(permission = "neonhologram.command.list", optional = true)
    val list = subCommand {
        execute<ProxyPlayer> { sender, _, _ ->
            val comp = Components.empty().append("").newLine()
            HologramManagerImpl.getHologramList().forEach {
                comp.append("  §e全息单位 §8> §e${it.uniqueId}")
                    .append("§8[§e操作§8]").clickRunCommand("/hd operate ${it.uniqueId}")
                    .newLine()
            }
            comp.sendTo(sender)
        }
    }

    @CommandBody(permission = "neonhologram.command.operate", optional = true)
    val operate = subCommand {
        dynamic("id") {
            suggest { HologramManagerImpl.getHologramKeyList() }
            execute<ProxyPlayer> { sender, _, arg ->
                HologramManagerImpl.findHologram(arg)?.let {
                    sender.createOperate(it)
                } ?: sender.sendLang("全息-查找-不存在", arg)
            }
        }
    }

    @CommandBody(permission = "neonhologram.command.addline", optional = true)
    val addline = subCommand {
        dynamic("id") {
            suggestUncheck { HologramManagerImpl.getHologramKeyList() }
            dynamic("text") {
                execute<ProxyPlayer> { sender, context, _ ->
                    HologramManagerImpl.findHologram(context["id"])?.let {
                        it.append(context["text"])
                        sender.sendLang(
                            "全息-添加新行-成功",
                            it.uniqueId,
                            context["text"]
                        )
                        sender.createOperate(it)
                    } ?: sender.sendLang("全息-查找-不存在", context["id"])
                }
            }
            execute<ProxyPlayer> { sender, context, _ ->
                HologramManagerImpl.findHologram(context["id"])?.let {
                    sender.sendLang("全息-添加新行-捕获")
                    (sender as BukkitPlayer).player.nextChat { text ->
                        it.append(text)
                        sender.sendLang(
                            "全息-添加新行-成功",
                            it.uniqueId,
                            text
                        )
                        sender.createOperate(it)
                    }
                } ?: sender.sendLang("全息-查找-不存在", context["id"])
            }
        }
    }

    @CommandBody(permission = "neonhologram.command.delline", optional = true)
    val delline = subCommand {
        dynamic("id") {
            suggestUncheck { HologramManagerImpl.getHologramKeyList() }
            dynamic("line") {
                suggestUncheck { listOf("0") }
                execute<ProxyPlayer> { sender, context, _ ->
                    HologramManagerImpl.findHologram(context["id"])?.let {
                        it.subtract(context["line"].toInt())
                        sender.sendLang(
                            "全息-删除一行-成功",
                            it.uniqueId,
                            context["line"]
                        )
                        sender.createOperate(it)
                    } ?: sender.sendLang("全息-查找-不存在", context["id"])
                }
            }
            execute<ProxyPlayer> { sender, context, _ ->
                HologramManagerImpl.findHologram(context["id"])?.let {
                    it.subtract(0)
                    sender.sendLang(
                        "全息-删除一行-成功",
                        it.uniqueId,
                        0
                    )
                    sender.createOperate(it)
                } ?: sender.sendLang("全息-查找-不存在", context["id"])
            }
        }
    }

    @CommandBody(permission = "neonhologram.command.migrator", optional = true)
    val trHDMigrator = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendMessage("正在运行迁移器...")
            submitAsync { TrHDMigrator().start() }
        }
    }


    private fun ProxyPlayer.createOperate(it: Hologram) {
        val comp = Components.empty().append("").newLine()
        comp.append("  §6全息单位 §8> §e${it.uniqueId}")
            .newLine()
        comp.append("  §7├ §6路径 §8> §7${it.loaderPath}")
            .newLine()
        comp.append("  §7├ §6操作 §8> ")
            .newLine()
        comp.append("  §7│     §8[§a传送过来§8] ").clickRunCommand("/hd moveHere ${it.uniqueId}")
            .append("§8[§a传送过去§8] ").clickRunCommand("/hd teleport ${it.uniqueId}")
            .newLine()
        comp.append("  §7│     §8[§a添加新行§8] ").clickRunCommand("/hd addline ${it.uniqueId}")
            .append("§8[§a移除一行§8] ").clickRunCommand("/hd delline ${it.uniqueId}")
            .newLine()
        comp.append("  §7├ §6位置 §8> §e${it.formLoc(true)}")
            .newLine()
        if (it.hologramContext.size == 1) {
            comp.append("  §7└ §6行数 §8> §e${it.hologramContext[0]?.originContext?.size ?: 0}")
                .newLine()
        } else {
            comp.append("  §7└ §6首页行数 §8> §e${it.hologramContext[0]?.originContext?.size ?: 0}")
                .newLine()
        }
        if (it.hologramContext.size >= 2) {
            comp.append("  §c§n当前多页全息，建议前往配置文件修改.")
        }
        comp.append("  §8§n如果编辑器无法满足你的需求，请前往配置文件修改。")
            .newLine()
        comp.sendTo(this)
    }

    private fun Hologram.formLoc(sub: Boolean = false): String {
        val location = option.location
        if (sub) {
            val world = location.world
            val x = location.x.toString()
            val y = location.y.toString()
            val z = location.z.toString()
            return "$world;${x.subSequence(0, if (x.length > 5) 5 else x.length)};${y.subSequence(0, if (y.length > 5) 5 else y.length)};${z.subSequence(0, if (z.length > 5) 5 else z.length)}"
        }
        return "${location.world};${location.x};${location.y};${location.z}"
    }


}