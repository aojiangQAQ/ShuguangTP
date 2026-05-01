package cn.shuguang.shuguangtp.economy;

import cn.shuguang.shuguangtp.ShuguangTP;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Vault 经济管理器
 */
public class EconomyManager {

    private final ShuguangTP plugin;
    private Economy economy;

    public EconomyManager(ShuguangTP plugin) {
        this.plugin = plugin;
    }

    /** 初始化 Vault，返回是否成功 */
    public boolean setup() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp =
                plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    /** 检查玩家余额是否足够 */
    public boolean has(Player player, double amount) {
        return economy.has(player, amount);
    }

    /** 扣款，返回是否成功 */
    public boolean withdraw(Player player, double amount) {
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    /** 获取玩家余额 */
    public double getBalance(Player player) {
        return economy.getBalance(player);
    }

    /** 获取经济插件名称 */
    public String getEconomyName() {
        return economy != null ? economy.getName() : "None";
    }

    /** 退款（移动取消传送时调用） */
    public boolean refund(Player player, double amount) {
        if (amount <= 0) return true;
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    /** 格式化金额 */
    public String format(double amount) {
        return economy.format(amount);
    }
}
