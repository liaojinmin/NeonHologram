---
sidebar_position: 1
---
# 普通面板


## 普通的全息面板

> 这里教你如何配置一个全息图，且没有花里胡哨的功能

> 在 hologram 文件夹下新建一个 exp.yml

> 并填入下方配置, 完成后在游戏输入 /hd reload 即可生效
```yaml title="普通"
hologram:
  # 这个全息面板唯一识别 ID
  uniqueId: test
  # 全息面板的基本设置
  option:
    # 全息面板所在世界、位置坐标
    location: world;1.0;70.0;1.0
    # 全息视距，玩家超过这个视距将看不见这个全息图
    visibleByDistance: 32
    # 这个全息图默认的行距
    lineSpacing: 0.25
  context:
    - "&f这是 &bGeekHologram &f全息面板插件"
    - "&f你的名称: &6%player_name%"
```




 
