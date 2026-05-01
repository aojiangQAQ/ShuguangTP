package cn.shuguang.shuguangtp.command;

import cn.shuguang.shuguangtp.ShuguangTP;
import cn.shuguang.shuguangtp.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * /stpreload
 * 重载插件配置
 */
public class STPReloadCommand implements CommandExecutor {

    private final ShuguangTP plugin;

    public STPReloadCommand(ShuguangTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shuguangtp.admin")) {
            MessageUtil.send(sender, "no-permission");
            return true;
        }

        plugin.reload();
        MessageUtil.send(sender, "reloaded");
        return true;
    }
}
