package me.neon.holo.utils

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import taboolib.module.chat.colored
import taboolib.platform.util.buildItem

/**
 * NeonHologram
 * me.neon.holo.utils
 *
 * @author 老廖
 * @since 2024/11/26 21:47
 */
fun parseItemStackToString(itemStacks: ItemStack): String {
    val builder = StringBuilder()
    builder.append("material:").append(itemStacks.type).append(",")
    builder.append("amount:").append(itemStacks.amount)
    itemStacks.itemMeta?.let { meta ->
        if (meta.hasDisplayName()) {
            builder.append(",").append("name:").append(meta.displayName)
        }
        meta.lore?.let { l1 ->
            val b = StringBuilder()
            l1.forEach { l2 ->
                b.append(l2).append("\\n")
            }
            builder.append(",").append("lore:").append(b)
        }

        if ((meta as Damageable).hasDamage()) {
            builder.append(",").append("data:").append((meta as Damageable).damage)
        }

        try {
            if (meta.hasCustomModelData()) {
                builder.append(",").append("model:").append(meta.customModelData)
            }
        } catch (ignored: NoSuchMethodException) {
        }
    }
    return builder.toString()


}

fun parseItemStack(input: String): ItemStack {
    val parts = input.split(",").associate {
        val (key, value) = it.split(":")
        key.trim() to value.trim()
    }
    val materialString = parts["material"]?.uppercase() ?: error("无法解析物品 -> $input")
    return buildItem(Material.valueOf(materialString)) {

        amount = parts["amount"]?.toIntOrNull() ?: 1

        parts["name"]?.let {
            name = it.colored()
        }

        parts["lore"]?.let {
            lore.addAll(it.split("\\n").colored())
        }

        parts["data"]?.let {
            this.damage = it.toIntOrNull() ?: 0
        }

        parts["model"]?.let {
            this.customModelData = it.toIntOrNull() ?: 0
        }
    }
}