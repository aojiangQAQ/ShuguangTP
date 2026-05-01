package cn.shuguang.shuguangtp.home;

import cn.shuguang.shuguangtp.ShuguangTP;
import com.earth2me.essentials.Essentials;
import org.bukkit.plugin.Plugin;

/**
 * Home 提供者工厂，根据配置选择实现
 */
public class HomeProviderFactory {

    public static HomeProvider create(ShuguangTP plugin) {
        String provider = plugin.getConfig().getString("home.provider", "essentials").toLowerCase();

        switch (provider) {
            case "essentials": {
                Plugin ess = plugin.getServer().getPluginManager().getPlugin("Essentials");
                if (ess instanceof Essentials) {
                    plugin.getLogger().info("Home 提供者：Essentials");
                    return new EssentialsHomeProvider((Essentials) ess);
                }
                plugin.getLogger().warning("未找到 Essentials，回退到内置 Home 提供者。");
                return new BuiltinHomeProvider(plugin);
            }
            case "builtin":
            default: {
                plugin.getLogger().info("Home 提供者：内置（builtin）");
                return new BuiltinHomeProvider(plugin);
            }
        }
    }
}
