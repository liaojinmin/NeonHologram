package me.neon.holo.utils

import java.io.File

/**
 * 作者: 老廖
 * 时间: 2022/12/12
 *
 **/
@Suppress("UNCHECKED_CAST")
fun Any?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return when (this) {
        is String -> mutableListOf(this)
        is List<*> -> this as List<String>
        else -> listOf(toString())
    }
}

fun forFile(file: File, endWith: String): List<File> {
    return mutableListOf<File>().run {
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                addAll(forFile(it, endWith))
            }
        } else if (file.exists() && file.absolutePath.endsWith(endWith)) {
            add(file)
        }
        this
    }
}