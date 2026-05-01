package cn.shuguang.shuguangtp.command;

import cn.shuguang.shuguangtp.ShuguangTP;
import cn.shuguang.shuguangtp.economy.EconomyManager;
import cn.shuguang.shuguangtp.manager.DelayedTeleportManager;
import cn.shuguang.shuguangtp.manager.RequestManager;
import cn.shuguang.shuguangtp.util.CostCalculator;
import cn.shuguang.shuguangtp.util.MessageUtil;
import org.bukkit.Bukkit;
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
 * /stpp <玩家名>
 * 向目标玩家发起 TP 请求，目标同意后传送
 */
public class STPPlayerCommand implements CommandExecutor, TabCompleter {

    private final ShuguangTP plugin;

    public STPPlayerCommand(ShuguangTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&c该命令只能由玩家执行。"));
            return true;
        }
        Player requester = (Player) sender;

        if (!requester.hasPermission("shuguangtp.use")) {
            MessageUtil.send(requester, "no-permission");
            return true;
        }

        if (args.length < 1) {
            requester.sendMessage(MessageUtil.color("&e用法: /stpp <玩家名>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            MessageUtil.send(requester, "player-not-found",
                    MessageUtil.of("player", args[0]));
            return true;
        }

        if (target.equals(requester)) {
            requester.sendMessage(MessageUtil.color("&c不能向自己发送传送请求。"));
            return true;
        }

        // 预计费用（以请求者当前位置 → 目标当前位置）
        FileConfiguration cfg = plugin.getConfig();
        double perBlock   = cfg.getDouble("cost.per-block", 0.05);
        double crossMulti = cfg.getDouble("cost.cross-dimension-multiplier", 3.0);
        double minimum    = cfg.getDouble("cost.minimum", 1.0);
        double maximum    = cfg.getDouble("cost.maximum", 500.0);
        double extra      = cfg.getDouble("cost.extra-player", 0.0);

        Location from = requester.getLocation();
        Location to   = target.getLocation();
        double cost = CostCalculator.calculate(from, to, perBlock, crossMulti, minimum, maximum, extra);
        double dist = CostCalculator.distance(from, to);

        boolean free = requester.hasPermission("shuguangtp.free") || requester.hasPermission("shuguangtp.admin");

        // 余额预检（免费不检查）
        if (!free && !plugin.getEconomyManager().has(requester, cost)) {
            MessageUtil.send(requester, "not-enough-money", MessageUtil.of(
                    "cost", plugin.getEconomyManager().format(cost),
                    "balance", plugin.getEconomyManager().format(plugin.getEconomyManager().getBalance(requester))));
            return true;
        }

        int timeout = cfg.getInt("timeout.request-expire", 30);

        // 创建请求（费用记录在请求对象里，同意时再扣）
        plugin.getRequestManager().createRequest(requester, target, free ? 0 : cost);

        // 告知发起者
        MessageUtil.send(requester, "request-sent", MessageUtil.of(
                "player", target.getName(),
                "timeout", String.valueOf(timeout)));

        // 告知目标
        MessageUtil.send(target, "request-received", MessageUtil.of(
                "player", requester.getName(),
                "cost", plugin.getEconomyManager().format(cost)));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String alias, String[] args) {
        List<String> names = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(sender) && p.getName().toLowerCase().startsWith(prefix)) {
                    names.add(p.getName());
                }
            }
        }
        return names;
    }
}
