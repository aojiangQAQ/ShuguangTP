package cn.shuguang.shuguangtp.home;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Home 提供者接口，适配不同的 home 插件
 */
public interface HomeProvider {

    /**
     * 获取玩家指定 home 的位置，不存在返回 null
     */
    Location getHome(Player player, String homeName);

    /**
     * 获取玩家所有 home 名称列表（用于 Tab 补全）
     */
    List<String> getHomeNames(Player player);

    /**
     * 提供者名称，用于日志显示
     */
    String getProviderName();
}
