package me.neon.holo.nms.data




// 9
typealias NMS9PacketDataSerializer = net.minecraft.server.v1_9_R2.PacketDataSerializer
typealias NMS9PacketPlayOutEntityTeleport = net.minecraft.server.v1_9_R2.PacketPlayOutEntityTeleport
typealias NMS9DataWatcherItem<T> = net.minecraft.server.v1_9_R2.DataWatcher.Item<T>
typealias NMS9DataWatcherObject<T> = net.minecraft.server.v1_9_R2.DataWatcherObject<T>
typealias NMS9DataWatcherRegistry = net.minecraft.server.v1_9_R2.DataWatcherRegistry
typealias CraftItemStack9 = org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack
typealias NMS9PacketPlayOutSpawnEntity = net.minecraft.server.v1_9_R2.PacketPlayOutSpawnEntity

// 1.11
// meta
typealias NMS11DataWatcherItem<T> = net.minecraft.server.v1_11_R1.DataWatcher.Item<T>
typealias NMS11DataWatcherObject<T> = net.minecraft.server.v1_11_R1.DataWatcherObject<T>
typealias NMS11DataWatcherRegistry = net.minecraft.server.v1_11_R1.DataWatcherRegistry
typealias NMS11PacketPlayOutSpawnEntityLiving = net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityLiving
typealias NMS11PacketDataSerializer = net.minecraft.server.v1_11_R1.PacketDataSerializer
typealias NMS11DataWatcher = net.minecraft.server.v1_11_R1.DataWatcher

// 1.12

typealias NMS12DataWatcherItem<T> = net.minecraft.server.v1_12_R1.DataWatcher.Item<T>

typealias NMS12DataWatcherObject<T> = net.minecraft.server.v1_12_R1.DataWatcherObject<T>

typealias NMS12DataWatcherRegistry = net.minecraft.server.v1_12_R1.DataWatcherRegistry

typealias CraftItemStack12 = org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack

// 1.13
// meta
typealias NMS13DataWatcherItem<T> = net.minecraft.server.v1_13_R2.DataWatcher.Item<T>
typealias NMS13DataWatcherObject<T> = net.minecraft.server.v1_13_R2.DataWatcherObject<T>
typealias NMS13DataWatcherRegistry = net.minecraft.server.v1_13_R2.DataWatcherRegistry
typealias NMS13IRegistry<T> = net.minecraft.server.v1_13_R2.IRegistry<T>
typealias NMS13EntityTypes<T> = net.minecraft.server.v1_13_R2.EntityTypes<T>
typealias CraftItemStack13 = org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack
typealias NMS13EnumItemSlot = net.minecraft.server.v1_13_R2.EnumItemSlot
typealias NMS13PacketPlayOutEntityEquipment = net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment


// 1.16
// type
typealias NMS16IRegistry<T> = net.minecraft.server.v1_16_R1.IRegistry<T>
typealias NMS16EntityTypes<T> = net.minecraft.server.v1_16_R1.EntityTypes<T>
typealias NMS16Player = org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
// meta
typealias NMS16DataWatcherRegistry = net.minecraft.server.v1_16_R1.DataWatcherRegistry
typealias NMS16DataWatcherItem<T> = net.minecraft.server.v1_16_R1.DataWatcher.Item<T>
typealias NMS16DataWatcherObject<T> = net.minecraft.server.v1_16_R1.DataWatcherObject<T>
// Mount
typealias NMS16PacketPlayOutMount = net.minecraft.server.v1_16_R1.PacketPlayOutMount
// DataSerializer
typealias NMS16PacketDataSerializer = net.minecraft.server.v1_16_R1.PacketDataSerializer
// ItemStack
typealias CraftItemStack16 = org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack
// SpawnEntity
typealias NMS16PacketPlayOutSpawnEntity = net.minecraft.server.v1_16_R1.PacketPlayOutSpawnEntity
typealias NMS16PacketPlayOutSpawnEntityLiving = net.minecraft.server.v1_16_R1.PacketPlayOutSpawnEntityLiving
typealias NMS16PacketPlayOutSpawnEntityPlayer = net.minecraft.server.v1_16_R1.PacketPlayOutNamedEntitySpawn
// EnumItemSlot
typealias NMS16PacketPlayOutEntityEquipment = net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment
typealias NMS16PacketPlayOutEntityMetadata = net.minecraft.server.v1_16_R1.PacketPlayOutEntityMetadata

typealias NMS16IChatBaseComponent = net.minecraft.server.v1_16_R1.IChatBaseComponent

// 1.19
typealias CraftItemStack19 = org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
typealias CraftChatMessage19 = org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage
// EnumItemSlot
typealias NMSEnumItemSlot = net.minecraft.world.entity.EnumItemSlot
typealias NMSPacketPlayOutEntityEquipment = net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment
typealias NMSPacketPlayOutEntityTeleport = net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport
typealias NMSPacketPlayOutAttachEntity = net.minecraft.network.protocol.game.PacketPlayOutAttachEntity

// 从 1.19+ 移除 1.19+ 没有 SpawnEntityLiving
typealias NMSPacketPlayOutSpawnEntityLiving = net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving

typealias NMSDataWatcherObject<T> = net.minecraft.network.syncher.DataWatcherObject<T>
typealias NMSDataWatcher = net.minecraft.network.syncher.DataWatcher
typealias NMSDataWatcherItem<T> = net.minecraft.network.syncher.DataWatcher.Item<T>
typealias NMSDataWatcherRegistry = net.minecraft.network.syncher.DataWatcherRegistry
typealias NMSIChatBaseComponent = net.minecraft.network.chat.IChatBaseComponent


typealias NMSPacketPlayOutSpawnEntity = net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity

typealias NMSPacketPlayOutSpawnEntityPlayer = net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn

typealias NMSPacketPlayOutEntityDestroy = net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy

typealias NMSPacketPlayOutEntityLook = net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutEntityLook

typealias NMSPacketPlayOutEntityHeadRotation = net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation

typealias NMSPacketPlayOutMount = net.minecraft.network.protocol.game.PacketPlayOutMount

typealias NMSPacketPlayOutEntityMetadata = net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata

typealias NMSPacketDataSerializer = net.minecraft.network.PacketDataSerializer

typealias NMSIRegistry<T> = net.minecraft.core.IRegistry<T>

typealias NMSEntityTypes<T> = net.minecraft.world.entity.EntityTypes<T>





