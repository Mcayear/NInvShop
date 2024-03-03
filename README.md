# NInvShop - 多功能箱子商店插件

## 依赖

| 插件              | 用途                     | 必需 |
|-----------------|------------------------|----|
| FakeInventories | 用于箱子 UI 的展示            | ✔  |
| MagicItem       | 用于获取 `mi@` 物品需求        | ✗  |
| PlayerPoints    | 用于支持 `point@` 点券需求     | ✗  |
| RcRPG           | 用于支持 `rcrpg@` 装备需求     | ✗  |
| RcTaskBook      | 用于支持 `taskbook@` 任务书需求 | ✗  |

## 配置

### config.yml

```yml
language: zh_CN # 无实际用途
```

### mcrmbConfig.yml

```yml
website: "https://mcrmb.com" # 请勿修改
sid: "" # 在 MCRMB 的服务器列表可见 sid，为纯数字
key: "" # 在 MCRMB 的服务器列表可见 key，由小写字母与数字组合的字符串
```

### ShopPages/example.yml

```yml
row: '' # 用于 /shop 命令显示列表时的文本说明
icon: '' # 用于 /shop 命令显示列表的显示图标
onlyConsole: false # 仅允许控制台使用命令打开
data: # 箱子物品的实际数据
- showitem: rcrpg@ornament 万妖幡
  execcmd: 
  - shop open 礼包_逍遥@@player
  onlycmd: true
  direct: true
- showitem: item@minecraft:air 1
  showNeed: ""
  execcmd: []
  onlycmd: true
- showitem: item@minecraft:beacon 1
  need: 
  - rmb@100
  iteminfo:
    name: "§r§e点击购买"
    lore: |-
      §r
      §f使用 100 点券购买
      §f
      §7请确保背包至少有 §l4 §r§7格空隙
  execcmd: 
  - sblock say "§l§e%player% §a已购买超值礼包!"
  - rpg ornament give %player% 宝玉如意1
  - mi give %player% 金元宝 2
  - mi give %player% 天山雪莲 20
  - mi give %player% 辟谷丹 10
  onlycmd: true
  direct: true
```

## 箱子物品数据篇

### showitem

用于箱子的显示物品，数据格式为字符串，它按照下表的格式进行解析

| 插件                       | 介绍                           | 参数1                                                | 参数2                                         | 参数3                   |
|--------------------------|------------------------------|----------------------------------------------------|---------------------------------------------|-----------------------|
| item@minecraft:stone 1 0 | `item@` 代表原版物品               | `minecraft:stone` 为物品的命名空间                         | `1` 代表数量                                    | `0` 代表 aux，旧版称之为 meta |
| mi@1 铜钱                  | `mi@` 代表魔法物品                 | `1` 代表数量                                           | `铜钱` 为 MagicItem 物品的 yml 文件名，也就是 **铜钱.yml** | -                     |
| rcrpg@ornament 万妖幡 1     | `rcrpg@` 代表 RcRPG 物品         | `ornament` 代表饰品类型，更多可参[RcRPG的物品类型](#RcRPG的物品类型)    | `万妖幡` 为 RcRPG 物品的 yml 文件名，也就是 **万妖幡.yml**   | `1` 代表数量              |
| taskbook@主线1-1           | `taskbook@` 代表 RcTaskBook 物品 | `主线1-1` 为 RcTaskBook 物品的 yml 文件名，也就是 **主线1-1.yml** | -                                           | -                     |
| point@20                 | `point@` 代表 PlayerPoints 点券  | `20` 代表点券数量                                        | -                                           | -                     |
| rmb@20                   | `rmb@` 代表 MCRMB 点数           | `20` 代表数量                                          | -                                           | -                     |

### need

用于箱子的显示物品，数据格式为字符串列表。

与 [showitem](#showitem) 一致

### showNeed

展示需求，数据类型字符串。

用于显示兑换此物品索要的需求。

### execcmd

当成功时执行的命令，数据格式为字符串列表

#### 身份标识
字符 `@@` 为切割表示，若后方跟随 `player` 代表以玩家身份执行命令，若跟随 `console` 或未曾使用 `@@` 进行标识则代表控制台身份执行。

#### 可用变量

变量以 `%` 符号包围，`%player%` 代表玩家名字，`%total%` 代表购买的总数量

#### 示例

举例1：
```yml
# 以玩家身份执行命令 `/shop open 礼包_逍遥`
- shop open 礼包_逍遥@@player
```

举例2：
```yml
# 以控制台身份执行命令 `/mi give <玩家名> 辟谷丹 10`
- mi give %player% 辟谷丹 10
```

### onlycmd

仅执行命令，数据类型为布尔值 (默认值false)

若为 `false` 给予玩家 `showitem` 展示的物品，并执行 `execcmd` 的命令.

若为 `true` 则不给予 `showitem` 物品，仅执行 `execcmd` 的命令.

### direct

不弹出表单直接以数量 1 购买，数据类型为布尔值 (默认值false)

若为 `false` 无任何变化。

若为 `true` 直接购买 1 份，不会弹出确认表单。

## 补充说明

### RcRPG的物品类型

| 类型       | 说明 |
|----------|----|
| armour   | 盔甲 |
| weapon   | 武器 |
| stone    | 宝石 |
| ornament | 饰品 |