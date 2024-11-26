---
sidebar_position: 2
---

# 开发者API

> 提供基本的操作方法

```kotlin
package me.geek.holo.api

object HologramAPI {

    /**
     * 通过唯一ID 获取 Hologram 实例
     * @param uniqueId 唯一ID
     * @return Hologram 可能 null
     */
    fun findHologram(uniqueId: String): Hologram? {}

    /**
     * 获取玩家全息图
     * @param player 玩家对象
     * @param uniqueId 唯一ID
     * @return Hologram 可能 null
     */
    fun findHologramByPlayer(player: OfflinePlayer, uniqueId: String): PlayerHologram? {}

    /**
     * 创建全息
     * @param uniqueId 唯一ID
     * @param location 坐标
     * @param context 全息内容
     * @param save 是否保存到文件夹
     * @return 如果 uniqueId 重复则 null 否则返回全息对象
     */
    fun createHologram(uniqueId: String, location: Location, context: List<String>, save: Boolean): Hologram {}

    /**
     * 创建玩家全息
     * @param player 玩家对象
     * @param uniqueId 唯一ID
     * @param location 坐标
     * @param context 全息内容
     * @return 如果文本内容检查失败则 null 否则返回玩家全息对象
     */
    fun createHologramByPlayer(player: Player, uniqueId: String, location: Location, context: List<String>): PlayerHologram? {}

    /**
     * 删除、摧毁一个玩家全息图
     * 这个删除是永久性的
     * @param player 玩家
     * @param uniqueId 全息唯一ID
     * @return 如果删除成功 true
     */
    fun removeHologramByPlayer(player: OfflinePlayer, uniqueId: String): Boolean {}
    
    /**
     * 删除、摧毁一个全息图
     * @param uniqueId 全息唯一ID
     * @param save 是否将文件也删除
     * @return 如果删除成功
     */
    fun removeHologram(uniqueId: String, save: Boolean): Boolean {}

}
```
