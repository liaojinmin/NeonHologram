package me.neon.holo.nms.data

import java.util.*

/**
 * Adyeshach
 * ink.ptms.adyeshach.api.dataserializer.DataSerializer
 *
 * @author 坏黑
 * @since 2022/12/12 23:00
 */
interface DataSerializer {

    fun writeByte(byte: Byte): DataSerializer

    fun writeBytes(bytes: ByteArray): DataSerializer

    fun writeShort(short: Short): DataSerializer

    fun writeInt(int: Int): DataSerializer

    fun writeLong(long: Long): DataSerializer

    fun writeFloat(float: Float): DataSerializer

    fun writeDouble(double: Double): DataSerializer

    fun writeBoolean(boolean: Boolean): DataSerializer

    fun writeMetadata(meta: List<Any>): DataSerializer

    fun writeUUID(uuid: UUID): DataSerializer {
        writeLong(uuid.mostSignificantBits)
        writeLong(uuid.leastSignificantBits)
        return this
    }

    fun writeVarIntArray(intArray: IntArray): DataSerializer {
        writeVarInt(intArray.size)
        intArray.forEach { writeVarInt(it) }
        return this
    }

    fun writeVarInt(int: Int): DataSerializer {
        var i = int
        while (i and -128 != 0) {
            writeByte((i and 127 or 128).toByte())
            i = i ushr 7
        }
        writeByte(i.toByte())
        return this
    }

    fun writeUtf(string: String, length: Int = 32767): DataSerializer {
        if (string.length > length) {
            error("String too big (was ${string.length} bytes encoded, max 32767)")
        } else {
            val arr = string.encodeToByteArray()
            val maxEncodedUtfLength = length * 3
            if (arr.size > maxEncodedUtfLength) {
                error("String too big (was ${arr.size} bytes encoded, max $maxEncodedUtfLength)")
            } else {
                writeVarInt(arr.size)
                writeBytes(arr)
            }
        }
        return this
    }


    fun toNMS(): Any
}

fun createDataSerializer(builder: DataSerializer.() -> Unit): DataSerializer {
    return DataSerializerFactory.instance.newSerializer().also(builder)
}