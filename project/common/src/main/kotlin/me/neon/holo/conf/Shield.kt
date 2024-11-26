package me.neon.holo.conf

import com.google.gson.GsonBuilder
import taboolib.common.platform.function.submitAsync
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * GeekHologram
 * me.geek.holo.bukkit.player
 *
 * @author 老廖
 * @since 2023/9/24 22:35
 */
class Shield(
    val local: MutableSet<String> = mutableSetOf(),
    private val cloudUrl: List<String>
) {
    private val gsonBuilder: GsonBuilder = GsonBuilder()

    fun initCloud() {
        submitAsync {
            var connection: HttpURLConnection? = null
            try {
                cloudUrl.forEach {
                    connection = URL(it).openConnection() as HttpURLConnection
                    connection?.let { conn ->
                        conn.connectTimeout = 5000
                        BufferedReader(InputStreamReader(conn.inputStream)).use { bf ->
                            val data = gsonBuilder.create().fromJson(bf.readText(), WebShield::class.java)
                            data.words.forEach { c ->
                                local.add(c)
                            }
                        }
                        conn.disconnect()
                    }
                }
            } catch (ignored: Throwable) {
            } finally {
                connection?.disconnect()
            }
        }
    }


    data class WebShield(
        val words: List<String> = mutableListOf()
    )
}