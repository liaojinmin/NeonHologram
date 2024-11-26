---
sidebar_position: 3
---

# 自动翻页滚动全息图

```yaml
hologram:
  uniqueId: "自动滚动全息面板"
  option:
    location: world;5.0;80.0;5.0
    visibleByDistance: 32
    lineSpacing: 0.25
    # 将全息设置为滚动模式
    isScroll: true
  context:
    # 这里代表第一页
    - text:
        - "&6这是一个滚动面板 &7by &bGeekHologram {highSet: 0.25}"
        - " "
        - "&7我是第一页的内容"
        - "&7服务器更新日志，现在有滚动广告全息图了!!!"
        - " "
      # 这一页的点击动作，每一个页面均可定义
      action:
        left:
          - "tell color '&7你点击了第一页的全息'"

    # 这里代表第二页
    - text:
        - "&6这是一个滚动面板 &7by &bGeekHologram"
        - " "
        - "&7我是第二页的内容"
        - "&6服务器广告: 点券充值充一送一"
        - " "
      action:
        left:
          - "tell color '&7你点击了第二页的广告'"
```