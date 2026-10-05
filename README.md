# ShuguangTP

Minecraft 距离收费传送插件。作者：**aojiangQAQ（鳌江）**，曙光团队。

当前版本为 `1.0.0`，使用 Spigot `1.16.5` API 编译，Java 字节码目标为 `11`。经济接入使用 Vault，Home 可读取 Essentials 数据或内置 YAML 文件。

## 功能

- 指定坐标传送、玩家传送请求与 Home 传送。
- 根据三维距离、跨世界倍率和传送类型附加费计费。
- 玩家请求支持同意、拒绝与自动超时；对方同意时重新计算非免费请求的费用。
- 支持延迟传送；启用移动取消时，在延迟结束时检查玩家是否离开原方块，取消并退款。
- 管理员或拥有免费权限的玩家可免除费用。
- 支持配置重载、消息颜色码与变量替换。

## 构建

需要 JDK 11+ 和 Maven。项目依赖由 Maven 仓库提供。

```powershell
git clone https://github.com/aojiangQAQ/ShuguangTP.git
cd ShuguangTP
mvn clean package
```

产物：`target/ShuguangTP-1.0.0.jar`。

源码位于 `src/main/java/cn/shuguang/shuguangtp/`，插件描述符和默认配置位于 `src/main/resources/`。

## 安装

1. 准备 Minecraft 1.16.5 Bukkit/Spigot API 服务端，使用 Java 11 或更高版本。
2. 安装 Vault 和一个注册 Vault Economy 服务的经济插件；需要 Essentials Home 时安装 EssentialsX。
3. 将 JAR 放入服务器 `plugins/` 目录并重启。
4. 编辑 `plugins/ShuguangTP/config.yml`，执行 `/stpreload`。

Vault 和经济服务为必需依赖。CMI 可作为 Vault 经济来源，但项目没有 CMI Home 适配器。

## 命令与权限

| 命令 | 别名 | 权限 | 说明 |
| --- | --- | --- | --- |
| `/stp <x> <y> <z> [世界]` | `/stpcoord` | `shuguangtp.use` | 指定世界的坐标传送，默认当前世界 |
| `/stpp <在线玩家>` | `/stpplayer` | `shuguangtp.use` | 向玩家发送传送请求 |
| `/stph [home名]` | `/stphome` | `shuguangtp.use` | 传送至 Home，默认名称 `home` |
| `/stpaccept` | `/stpa` | 无额外检查 | 同意收到的请求 |
| `/stpdeny` | `/stpd` | 无额外检查 | 拒绝收到的请求 |
| `/stpreload` | 无 | `shuguangtp.admin` | 重载配置并重建 Home 提供者 |

坐标参数只接受数字，不解析原版命令的 `~` 相对坐标。

| 权限 | 默认 | 说明 |
| --- | --- | --- |
| `shuguangtp.use` | 所有玩家 | 使用坐标、玩家请求与 Home 传送命令 |
| `shuguangtp.admin` | OP | 重载配置、免费传送 |
| `shuguangtp.free` | 不授予 | 免费传送 |

## 计费

1. 三维直线距离乘以每格单价，再加该传送类型的附加费。
2. 跨世界时将结果乘以 `cross-dimension-multiplier`。
3. 先应用 `minimum` 下限，再应用大于零的 `maximum` 上限。
4. 四舍五入至两位小数。

跨世界距离直接使用两处坐标计算，不换算地狱与主世界的坐标比例。默认单价下，同世界 500 格为 25 金币；跨世界坐标距离 300 格为 45 金币。

## 配置

完整消息与选项见 [config.yml](src/main/resources/config.yml)。

```yaml
cost:
  per-block: 0.05
  cross-dimension-multiplier: 3.0
  minimum: 1.0
  maximum: 500.0
  extra-coord: 0.0
  extra-home: 0.0
  extra-player: 0.0

timeout:
  request-expire: 30

teleport:
  delay: 3
  cancel-on-move: true

home:
  provider: essentials
```

`maximum: 0` 表示不限制最高费用，`delay: 0` 表示立即传送。坐标和 Home 传送在等待前扣费；玩家互传在对方同意后扣费。

## Home 数据

- `essentials`：读取 Essentials 已有的 Home；未找到 Essentials 时回退到 `builtin`。
- `builtin`：读取 `plugins/ShuguangTP/homes.yml`。一级键为玩家 UUID，二级键为 Home 名，位置包含 `world`、`x`、`y`、`z`、`yaw`、`pitch`。
- 本插件没有 `/sethome`、`/delhome` 命令；内置模式的数据需由文件或调用对应提供者方法维护。

运行时 Home 数据含玩家标识与坐标，不属于源码；发布仓库时不要上传服务器数据目录。

## 致谢与许可

感谢落尽红樱君不见委托定制开发。

采用 [MIT License](LICENSE)。问题反馈与修改建议通过本仓库的 Issues 或 Pull Requests 提交。
