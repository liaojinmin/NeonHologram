---
sidebar_position: 2
---

# 玩家组设置

> 这个配置玩家告诉你如何为玩家分配功能组，通过权限决定玩家如何使用全息

```yaml title="settings.yml"
# 玩家全息 屏蔽词
playerShield:
  # 本地词库
  local:
    - "fuck"
  # 云词库
  cloud:
    - "https://raw.githubusercontent.com/Yurinann/Filter-Thesaurus-Cloud/main/database.json"

# 玩家全息 权限组
playerHoloSetting:
    # 优先级
  - priority: 1
    # 需要的权限
    permission: hologram.player.default

    # 创建花费条件 -> 比如玩家创建全息图，则判断这个条件是否达成，
    # 这里案例消耗 金币 >= 1000
    # 这里均使用 Kether 脚本
    costCondition:
      condition: 'check papi %vault_eco_balance% >= 1000'
      # 执行指令扣除金币，替换为你的经济插件指令
      allow: 'command papi "money take %player_name% 1000" as console'
      deny: 'tell "你没有足够的金币创建全息图"'

    # 最大视距
    visibleByDistance: 16
    # 允许创建的全息数量
    haveAmount: 10
    # 单个全息允许的最大行数
    haveLineAmount: 2
    # 单行文字长度 表示一行全息最大有几个字符
    haveCharLength: 32
    # 允许创建全息的世界
    haveWorldName:
      - "world"
    # 允许使用的 PlaceholderAPI 变量列表
    variable:
      - "%player_name%"

  - priority: 2
    permission: hologram.player.vip
    costCondition:
      condition: 'check papi %vault_eco_balance% >= 1000'
      allow: 'command papi "money take %player_name% 1000" as console'
      deny: 'tell "你没有足够的金币创建全息图"'
    visibleByDistance: 18
    haveAmount: 15
    haveLineAmount: 4
    haveCharLength: 32
    haveWorldName:
      - "world"
    variable:
      - "%player_name%"

  - priority: 3
    permission: hologram.player.svip
    costCondition:
      condition: 'check papi %vault_eco_balance% >= 1000'
      allow: 'command papi "money take %player_name% 1000" as console'
      deny: 'tell "你没有足够的金币创建全息图"'
    visibleByDistance: 18
    haveAmount: 20
    haveLineAmount: 5
    haveCharLength: 32
    haveWorldName:
      - "world"
    variable:
      - "%player_name%"
```