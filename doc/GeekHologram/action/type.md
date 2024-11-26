---
sidebar_position: 1
---

# 支持的动作类型
| **动作**      | **触发**     |
|-------------|------------|
| **all** | 不分种类，点击就触发 |
| **left**    | 左键点击 触发    |
| **right** | 右键点击 触发    |
| **shift_left**    | 下蹲 + 左键 触发 |
| **shift_right**    | 下蹲 + 右键 触发 |


> 支持 使用 list 格式书写
> 如果到这里你还不知道这个动作该写在哪里
> 那就请参考 **多页动态面板配置中的写法**
```yaml
  action:
    all:
      - 'tell color "&7你点击了全息图"'
    left: |-
      tell color '&7你左键点击了全息图'
    right: |-
      tell color '&7你右键点击了全息图'
    shift_left: 'null'
    shift_right: 'null'
```

:::warning
在配置动作时，你必须对 Yaml 语法有一定了解，避免应语法错误导致的各种问题.
:::




