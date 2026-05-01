package cn.shuguang.shuguangtp.command;

import cn.shuguang.shuguangtp.ShuguangTP;
import cn.shuguang.shuguangtp.economy.EconomyManager;
import cn.shuguang.shuguangtp.home.HomeProvider;
import cn.shuguang.shuguangtp.manager.DelayedTeleportManager;
import cn.shuguang.shuguangtp.util.CostCalculator;
import cn.shuguang.shuguangtp.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * /stph [home名称]
 * 传送到玩家 home，未指定名称时使用 "home"
 */
public class STPHomeCommand implements CommandExecutor, TabCompleter {

    private final ShuguangTP plugin;
    private final DelayedTeleportManager delayManager;

    public STPHomeCommand(ShuguangTP plugin) {
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

        String homeName = (args.length >= 1) ? args[0] : "home";

        HomeProvider homeProvider = plugin.getHomeProvider();
        Location dest = homeProvider.getHome(player, homeName);

        if (dest == null) {
            MessageUtil.send(player, "home-not-found",
                    MessageUtil.of("home", homeName));
            return true;
        }

        Location from = player.getLocation();

        FileConfiguration cfg = plugin.getConfig();
        double perBlock   = cfg.getDouble("cost.per-block", 0.05);
        double crossMulti = cfg.getDouble("cost.cross-dimension-multiplier", 3.0);
        double minimum    = cfg.getDouble("cost.minimum", 1.0);
        double maximum    = cfg.getDouble("cost.maximum", 500.0);
        double extra      = cfg.getDouble("cost.extra-home", 0.0);

        double cost = CostCalculator.calculate(from, dest, perBlock, crossMulti, minimum, maximum, extra);
        double distance = CostCalculator.distance(from, dest);
        boolean cross = CostCalculator.isCrossDimension(from, dest);

        String dimTip = cross
                ? MessageUtil.get("dimension-tip-cross",
                    MessageUtil.of("multiplier", String.valueOf(crossMulti)))
                : MessageUtil.get("dimension-tip-same");

        // 免费权限
        boolean free = player.hasPermission("shuguangtp.free") || player.hasPermission("shuguangtp.admin");

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
            eco.withdraw(player, cost);
            MessageUtil.send(player, "charge-success", MessageUtil.of(
                    "cost", eco.format(cost),
                    "balance", eco.format(eco.getBalance(player))));
        } else {
            MessageUtil.send(player, "free-teleport");
        }

        final double finalCost = free ? 0 : cost;
        delayManager.submit(player, dest, finalCost,
                () -> MessageUtil.send(player, "teleport-success"));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String alias, String[] args) {
        List<String> names = new ArrayList<>();
        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            String prefix = args[0].toLowerCase();
            for (String h : plugin.getHomeProvider().getHomeNames(player)) {
                if (h.toLowerCase().startsWith(prefix)) names.add(h);
            }
        }
        return names;
    }
}
