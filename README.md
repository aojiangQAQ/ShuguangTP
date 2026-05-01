# ShuguangTP — 曙光收费传送插件

![Minecraft](https://img.shields.io/badge/Minecraft-1.16.5-green)
![Paper/Arclight](https://img.shields.io/badge/Paper%2FArclight-Compatible-blue)
![License](https://img.shields.io/badge/license-MIT-blue)

> **ShuguangTP**（曙光传送）是一个为 Minecraft 服务器设计的收费传送插件。支持坐标传送、玩家互传、Home 传送，按距离智能计费，完美集成 Vault 经济系统，兼容 Arclight 混合核心。

---

## ✨ 功能亮点

- **多种传送方式**  
  坐标传送 `/stp`、玩家请求传送 `/stpp`、Essentials Home 兼容传送 `/stph`

- **智能距离计费**  
  按直线距离 × 每格单价计费，跨维度按配置倍率加倍收费

- **传送请求机制**  
  玩家互传需对方同意，支持超时自动取消

- **传送延迟保护**  
  可配置延迟秒数，移动即取消并自动退款

- **余额安全检查**  
  余额不足直接拒绝，不扣费，不产生异常

- **免费权限节点**  
  拥有 `shuguangtp.free` 权限的玩家可跳过扣费

- **热重载支持**  
  `/stpreload` 不重启服务器即可生效配置修改

- **Arclight 完美兼容**  
  纯 Bukkit API 实现，不依赖 NMS，在 Forge + Paper 混合核心下稳定运行

---

## 📂 目录结构

```
ShuguangTP/
├── pom.xml
├── README.md
├── LICENSE
├── .gitignore
└── src/
    └── main/
        ├── java/
        │   └── com/shuguangteam/shuguangtp/...
        └── resources/
            ├── plugin.yml
            └── config.yml
```

---

## 🛠 本地构建

确保使用 **JDK 11+** 和 **Maven 3.8+**：

```bash
# 克隆仓库
git clone https://github.com/aojiangQAQ/ShuguangTP.git
cd ShuguangTP

# Maven 打包（跳过测试）
mvn clean package -DskipTests

# 生成 target/ShuguangTP-1.0.0.jar
```

---

## 🚀 安装与配置

### 前置依赖

| 前置插件 | 说明 |
|--------|------|
| **Vault** | 必须 |
| **EssentialsX** / CMI / 其他经济插件 | 提供实际的经济系统 |
| **Essentials**（可选） | 提供 home 数据读取 |

### 安装步骤

1. 确保服务器已安装 **Vault** 及经济插件（如 EssentialsX）
2. 将 `ShuguangTP-1.0.0.jar` 复制到服务器 `plugins/` 目录
3. 启动或重启服务器，`plugins/ShuguangTP/config.yml` 自动生成
4. 根据需要修改费率、消息等配置，执行 `/stpreload` 生效

---

## 📝 指令 & 权限

| 命令 | 权限 | 说明 |
|------|------|------|
| `/stp <x> <y> <z> [世界]` | `shuguangtp.use` | 传送到指定坐标 |
| `/stpp <玩家名>` | `shuguangtp.use` | 向玩家发起传送请求 |
| `/stph [home名]` | `shuguangtp.use` | 传送到 Essentials Home |
| `/stpaccept`（别名 `/stpa`） | `shuguangtp.use` | 同意传送请求 |
| `/stpdeny`（别名 `/stpd`） | `shuguangtp.use` | 拒绝传送请求 |
| `/stpreload` | `shuguangtp.admin` | 重载配置 |

### 权限节点

| 节点 | 默认 | 说明 |
|------|------|------|
| `shuguangtp.use` | 所有玩家 | 使用传送命令 |
| `shuguangtp.admin` | OP | 重载命令 + 免费传送 |
| `shuguangtp.free` | 无 | 免费传送（不扣费） |

---

## 💰 计费公式

```
费用 = max(min(距离 × 每格单价 + 额外附加, 最高上限), 最低费用)
                          ↑ 若跨维度，整体 × cross-dimension-multiplier
```

**示例**（默认配置）：
- 主世界传送 500 格：`500 × 0.05 = 25 金币`
- 主世界 → 地狱 300 格：`300 × 0.05 × 3 = 45 金币`

---

## 🔧 配置说明

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

## 🔧 开发环境

- **Java:** 11+
- **Build:** Maven 3.8+
- **API:** Spigot / Paper API 1.16.5
- **测试服务端:** Paper 1.16.5 / Arclight 1.16.5

---

## 🤝 贡献

欢迎 Issue / PR！

1. Fork 本仓库
2. 创建新分支: `git checkout -b feature/awesome`
3. 提交更改: `git commit -m "Add awesome feature"`
4. 推送分支: `git push origin feature/awesome`
5. 发起 Pull Request

---

## ⚖️ License

ShuguangTP 使用 **MIT License**，详见 [LICENSE](LICENSE)。

---

> **制作团队**：曙光团队  
> **制作人**：鳌江  
> **适用服务端**：Paper / Arclight 1.16.5
