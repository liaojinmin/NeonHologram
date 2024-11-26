---
sidebar_position: 3
---
# 翻页动态面板


## 翻页动态的全息面板

:::info

**在这里你会认识到两个新配置选项**
> {item: 物品特征} # 这个可将一行全息解析为物品

> {update: 20} # 这会告诉插件，这一行全息需要每 20Tick 更新一次

> {highSet: 0.15} # 这会告诉插件，这一行全息需要往上偏移 0.15 格

> {now_page} # 这在解析多页面板时，会被替换为当前页面

> {max_page} # 这在解析多页面板时，会被替换为全息的最大页面数

**Kether 新动作**
> page last # 上一页动作

> page next # 下一页动作

:::

> 这里教你如何配置一个动态全息图
> 在 hologram 文件夹下新建一个 exp2.yml
> 并填入下方配置, 完成后在游戏输入 /hd reload 即可生效
```yaml title="普通"
hologram:
  # 这个全息面板唯一识别 ID
  uniqueId: test
  # 全息面板的基本设置
  option:
    # 全息面板所在世界、位置坐标
    location: world;5.0;70.0;5.0
    # 全息视距，玩家超过这个视距将看不见这个全息图
    visibleByDistance: 32
    # 这个全息图默认的行距
    lineSpacing: 0.25
  context:
    - - '&6这是一个多页全息面板 &7by &bGeekHologram {highSet: 0.25}'
      - '&7我是第一页的内容 {highSet: 0.25}'
      - §7(§f左键§8=§e上一页§7) &f{now_page}&7/&a{max_page} (§f右键§8=§e下一页§7)
    - - '&6这是一个多页全息面板 &7by &bGeekHologram'
      - ''
      - '&7我是第二页的内容'
      - ''
      - §7(§f左键§8=§e上一页§7) &f{now_page}&7/&a{max_page} (§f右键§8=§e下一页§7)
  action:
    left: |-
      page last
      tell color '&7你点击的全息图的上跳页面动作'
    right: |-
      page next
      tell color '&7你点击的全息图的下跳页面动作'
```




 
