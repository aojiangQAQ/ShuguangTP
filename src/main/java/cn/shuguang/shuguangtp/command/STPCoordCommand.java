package cn.shuguang.shuguangtp.command;

import cn.shuguang.shuguangtp.ShuguangTP;
import cn.shuguang.shuguangtp.economy.EconomyManager;
import cn.shuguang.shuguangtp.manager.DelayedTeleportManager;
import cn.shuguang.shuguangtp.util.CostCalculator;
import cn.shuguang.shuguangtp.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * /stp <x> <y> <z> [world]
 * 传送到指定坐标
 */
public class STPCoordCommand implements CommandExecutor, TabCompleter {

    private final ShuguangTP plugin;
    private final DelayedTeleportManager delayManager;

    public STPCoordCommand(ShuguangTP plugin) {
        this.plugin = plugin;
        this.delayManager = new DelayedTeleportManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&c该命令只能由玩家执行。"));
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("shuguangtp.use")) {
            MessageUtil.send(player, "no-permission");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage(MessageUtil.color("&e用法: /stp <x> <y> <z> [世界名]"));
            return true;
        }

        double x, y, z;
        try {
            x = Double.parseDouble(args[0]);
            y = Double.parseDouble(args[1]);
            z = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            MessageUtil.send(player, "coord-invalid");
            return true;
        }

        // 解析世界（默认当前世界）
        World world = player.getWorld();
        if (args.length >= 4) {
            world = Bukkit.getWorld(args[3]);
            if (world == null) {
                MessageUtil.send(player, "world-not-found",
                        MessageUtil.of("world", args[3]));
                return true;
            }
        }

        Location dest = new Location(world, x, y, z,
                player.getLocation().getYaw(), player.getLocation().getPitch());
        Location from = player.getLocation();

        FileConfiguration cfg = plugin.getConfig();
        double perBlock   = cfg.getDouble("cost.per-block", 0.05);
        double crossMulti = cfg.getDouble("cost.cross-dimension-multiplier", 3.0);
        double minimum    = cfg.getDouble("cost.minimum", 1.0);
        double maximum    = cfg.getDouble("cost.maximum", 500.0);
        double extra      = cfg.getDouble("cost.extra-coord", 0.0);

        double cost = CostCalculator.calculate(from, dest, perBlock, crossMulti, minimum, maximum, extra);
        double distance = CostCalculator.distance(from, dest);
        boolean cross = CostCalculator.isCrossDimension(from, dest);

        String dimTip = cross
                ? MessageUtil.get("dimension-tip-cross",
                    MessageUtil.of("multiplier", String.valueOf(crossMulti)))
                : MessageUtil.get("dimension-tip-same");

        // 免费权限
        boolean free = player.hasPermission("shuguangtp.free") || player.hasPermission("shuguangtp.admin");

        // 费用预告
        MessageUtil.send(player, "cost-info", MessageUtil.of(
                "cost", plugin.getEconomyManager().format(cost),
                "distance", String.format("%.1f", distance),
                "dimension_tip", dimTip));

        if (!free) {
            EconomyManager eco = plugin.getEconomyManager();
            if (!eco.has(player, cost)) {
                MessageUtil.send(player, "not-enough-money", MessageUtil.of(
                        "cost", eco.format(cost),
                        "balance", eco.format(eco.getBalance(player))));
                return true;
            }
            // 扣费
            eco.withdraw(player, cost);
            MessageUtil.send(player, "charge-success", MessageUtil.of(
                    "cost", eco.format(cost),
                    "balance", eco.format(eco.getBalance(player))));
        } else {
            MessageUtil.send(player, "free-teleport");
        }

        // 延迟传送
        final double finalCost = free ? 0 : cost;
        delayManager.submit(player, dest, finalCost, () ->
                MessageUtil.send(player, "teleport-success"));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) result.add("~");
        if (args.length == 2) result.add("64");
        if (args.length == 3) result.add("~");
        if (args.length == 4) {
            for (World w : Bukkit.getWorlds()) result.add(w.getName());
        }
        return result;
    }
}
