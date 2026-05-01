# ShuguangTP — 曙光收费传送插件

> **制作团队**：曙光团队  
> **制作人**：鳌江  
> **版本**：1.0.0  
> **适配核心**：Paper / Arclight 1.16.5  
> **依赖**：Vault + 任意经济插件（如 EssentialsX Economy、CMI Economy）  
> **软依赖**：Essentials（用于读取 home 数据）

---

## 功能特性

| 功能 | 说明 |
|------|------|
| 坐标传送 | `/stp <x> <y> <z> [世界名]` |
| 玩家传送 | `/stpp <玩家名>` 发起请求，对方同意后执行 |
| Home 传送 | `/stph [home名]` 兼容 Essentials，默认 `home` |
| 距离计费 | 直线距离 × 每格单价 |
| 跨维度加倍 | 跨世界传送按配置倍率收费 |
| 余额检查 | 余额不足直接拒绝，不扣费 |
| 请求超时 | 可配置超时时间，过期自动取消 |
| 传送延迟 | 可配置延迟秒数，移动取消并退款 |
| 免费权限 | `shuguangtp.free` 权限节点可跳过扣费 |
| 热重载 | `/stpreload` 不重启服务器重载配置 |

---

## 安装步骤

1. 确保服务器已安装 **Vault** 及经济插件（如 EssentialsX）
2. 将编译好的 `ShuguangTP-1.0.0.jar` 放入 `plugins/` 目录
3. 启动/重启服务器，`plugins/ShuguangTP/config.yml` 自动生成
4. 根据需要修改费率、消息等配置，执行 `/stpreload` 生效

---

## 命令一览

| 命令 | 权限 | 说明 |
|------|------|------|
| `/stp <x> <y> <z> [世界]` | `shuguangtp.use` | 传送到指定坐标 |
| `/stpp <玩家名>` | `shuguangtp.use` | 向玩家发起传送请求 |
| `/stph [home名]` | `shuguangtp.use` | 传送到 home |
| `/stpaccept` (别名 `/stpa`) | `shuguangtp.use` | 同意传送请求 |
| `/stpdeny` (别名 `/stpd`) | `shuguangtp.use` | 拒绝传送请求 |
| `/stpreload` | `shuguangtp.admin` | 重载配置 |

---

## 权限节点

| 节点 | 默认 | 说明 |
|------|------|------|
| `shuguangtp.use` | 所有玩家 | 使用传送命令 |
| `shuguangtp.admin` | OP | 重载命令 + 免费传送 |
| `shuguangtp.free` | 无 | 免费传送（不扣费） |

---

## 计费公式

```
费用 = max(min(距离 × 每格单价 + 额外附加, 最高上限), 最低费用)
                          ↑ 若跨维度，整体 × cross-dimension-multiplier
```

**示例**（默认配置）：  
- 主世界传送 500 格：`500 × 0.05 = 25 金币`  
- 主世界 → 地狱 300 格：`300 × 0.05 × 3 = 45 金币`

---

## 配置文件关键项

```yaml
cost:
  per-block: 0.05          # 每格单价
  cross-dimension-multiplier: 3.0  # 跨维度倍率
  minimum: 1.0             # 最低收费
  maximum: 500.0           # 最高上限（0=不限）

teleport:
  delay: 3                 # 传送延迟（秒）
  cancel-on-move: true     # 移动取消并退款

home:
  provider: essentials     # essentials / builtin
```

---

## 编译方法

需要 JDK 11+ 和 Maven 3.8+：

```bash
cd ShuguangTP
mvn clean package -DskipTests
```

生成文件：`target/ShuguangTP-1.0.0.jar`

---

## Arclight 兼容说明

- 本插件纯 Bukkit API，**不使用 NMS**，天然兼容 Arclight（Forge + Paper 混合核心）
- 测试版本：Arclight 1.16.5-xxx
- 若使用 CMI 作为 home 提供者，可将 `home.provider` 改为 `builtin`，并通过 CMI 内置命令管理 home，本插件读取 homes.yml

---

## 更新日志

### 1.0.0
- 初始发布
- 支持坐标/玩家/home 收费传送
- 玩家 TP 请求与同意/拒绝机制
- 跨维度费用倍率
- 传送延迟与移动检测退款
- Vault 经济集成
- Essentials home 兼容

---

*© 2026 曙光团队 · 制作人：鳌江*
