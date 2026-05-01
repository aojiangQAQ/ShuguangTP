package cn.shuguang.shuguangtp.command;

import cn.shuguang.shuguangtp.ShuguangTP;
import cn.shuguang.shuguangtp.economy.EconomyManager;
import cn.shuguang.shuguangtp.manager.DelayedTeleportManager;
import cn.shuguang.shuguangtp.manager.RequestManager;
import cn.shuguang.shuguangtp.util.CostCalculator;
import cn.shuguang.shuguangtp.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * /stpaccept
 * 同意传送请求，此时扣费并执行传送
 */
public class STPAcceptCommand implements CommandExecutor {

    private final ShuguangTP plugin;
    private final DelayedTeleportManager delayManager;

    public STPAcceptCommand(ShuguangTP plugin) {
        this.plugin = plugin;
        this.delayManager = new DelayedTeleportManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color("&c该命令只能由玩家执行。"));
            return true;
        }
        Player target = (Player) sender;

        RequestManager.TeleportRequest req = plugin.getRequestManager().getRequest(target);
        if (req == null) {
            MessageUtil.send(target, "request-none");
            return true;
        }

        Player requester = req.requester;
        double cost = req.cost;

        // 移除请求（先移除，防止重复触发）
        plugin.getRequestManager().removeRequest(target);

        if (!requester.isOnline()) {
            target.sendMessage(MessageUtil.color("&c请求发起者已下线，传送取消。"));
            return true;
        }

        // 同意时重新计算费用（位置可能已改变）
        boolean free = (cost == 0);

        if (!free) {
            // 以目标当前位置重新计算
            FileConfiguration cfg = plugin.getConfig();
            double perBlock   = cfg.getDouble("cost.per-block", 0.05);
            double crossMulti = cfg.getDouble("cost.cross-dimension-multiplier", 3.0);
            double minimum    = cfg.getDouble("cost.minimum", 1.0);
            double maximum    = cfg.getDouble("cost.maximum", 500.0);
            double extra      = cfg.getDouble("cost.extra-player", 0.0);

            cost = CostCalculator.calculate(
                    requester.getLocation(), target.getLocation(),
                    perBlock, crossMulti, minimum, maximum, extra);

            EconomyManager eco = plugin.getEconomyManager();
            if (!eco.has(requester, cost)) {
                MessageUtil.send(requester, "not-enough-money", MessageUtil.of(
                        "cost", eco.format(cost),
                        "balance", eco.format(eco.getBalance(requester))));
                target.sendMessage(MessageUtil.color("&c对方余额不足，传送取消。"));
                return true;
            }
            eco.withdraw(requester, cost);
            MessageUtil.send(requester, "charge-success", MessageUtil.of(
                    "cost", eco.format(cost),
                    "balance", eco.format(eco.getBalance(requester))));
        }

        // 告知双方
        MessageUtil.send(requester, "request-accepted",
                MessageUtil.of("player", target.getName()));

        // 目标地点 = target 当前位置
        Location dest = target.getLocation();
        final double finalCost = free ? 0 : cost;
        delayManager.submit(requester, dest, finalCost,
                () -> MessageUtil.send(requester, "teleport-success"));

        return true;
    }
}
