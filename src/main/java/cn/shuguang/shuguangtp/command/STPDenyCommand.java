package cn.shuguang.shuguangtp.command;

import cn.shuguang.shuguangtp.ShuguangTP;
import cn.shuguang.shuguangtp.manager.RequestManager;
import cn.shuguang.shuguangtp.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /stpdeny
 * 拒绝传送请求
 */
public class STPDenyCommand implements CommandExecutor {

    private final ShuguangTP plugin;

    public STPDenyCommand(ShuguangTP plugin) {
        this.plugin = plugin;
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
        plugin.getRequestManager().removeRequest(target);

        if (requester.isOnline()) {
            MessageUtil.send(requester, "request-denied",
                    MessageUtil.of("player", target.getName()));
        }

        return true;
    }
}
