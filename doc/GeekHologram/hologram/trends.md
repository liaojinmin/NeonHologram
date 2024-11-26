---
sidebar_position: 2
---
# 动态面板


## 动态的全息面板

:::info

**在这里你会认识到两个新配置选项**
> {item: 物品特征} # 这个可将一行全息解析为物品

> {update: 20} # 这会告诉插件，这一行全息需要每 20Tick 更新一次

> {highSet: 0.15} # 这会告诉插件，这一行全息需要往上偏移 0.15 格

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
    - "&f这是 &bGeekHologram &f全息面板插件 {highSet: 0.15}"
    # 这是一个自定义物品展示行 
    - "{item: material:PAPER,model:0}" 
    - ""
    # 这是一行动态更新的文字
    - "&f时间: &e%server_time_HH:mm:ss% {update:20}"
    - "&f名称: &6%player_name%"
```




 
