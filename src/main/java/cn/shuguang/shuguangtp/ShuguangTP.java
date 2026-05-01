package cn.shuguang.shuguangtp;

import cn.shuguang.shuguangtp.command.*;
import cn.shuguang.shuguangtp.economy.EconomyManager;
import cn.shuguang.shuguangtp.home.HomeProvider;
import cn.shuguang.shuguangtp.home.HomeProviderFactory;
import cn.shuguang.shuguangtp.manager.RequestManager;
import cn.shuguang.shuguangtp.util.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ShuguangTP 主类
 * 曙光团队出品，作者：鳌江
 */
public class ShuguangTP extends JavaPlugin {

    private static ShuguangTP instance;
    private EconomyManager economyManager;
    private HomeProvider homeProvider;
    private RequestManager requestManager;

    @Override
    public void onEnable() {
        instance = this;

        // 保存默认配置
        saveDefaultConfig();

        // 初始化工具类
        MessageUtil.init(this);

        // 初始化经济
        economyManager = new EconomyManager(this);
        if (!economyManager.setup()) {
            getLogger().severe("未找到 Vault 经济插件！ShuguangTP 将禁用。");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 初始化 Home 提供者
        homeProvider = HomeProviderFactory.create(this);

        // 初始化请求管理器
        requestManager = new RequestManager(this);

        // 注册命令
        registerCommands();

        getLogger().info("========================================");
        getLogger().info("  ShuguangTP v" + getDescription().getVersion() + " 已启动");
        getLogger().info("  制作团队：曙光团队  |  制作人：鳌江");
        getLogger().info("  经济插件：" + economyManager.getEconomyName());
        getLogger().info("  Home 提供者：" + homeProvider.getProviderName());
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        if (requestManager != null) {
            requestManager.cancelAll();
        }
        getLogger().info("ShuguangTP 已卸载。");
    }

    private void registerCommands() {
        // /stp <x> <y> <z> [world]
        STPCoordCommand coordCmd = new STPCoordCommand(this);
        getCommand("stp").setExecutor(coordCmd);
        getCommand("stp").setTabCompleter(coordCmd);

        // /stpp <player>
        STPPlayerCommand playerCmd = new STPPlayerCommand(this);
        getCommand("stpp").setExecutor(playerCmd);
        getCommand("stpp").setTabCompleter(playerCmd);

        // /stph [home]
        STPHomeCommand homeCmd = new STPHomeCommand(this);
        getCommand("stph").setExecutor(homeCmd);
        getCommand("stph").setTabCompleter(homeCmd);

        // /stpaccept /stpdeny
        getCommand("stpaccept").setExecutor(new STPAcceptCommand(this));
        getCommand("stpdeny").setExecutor(new STPDenyCommand(this));

        // /stpreload
        getCommand("stpreload").setExecutor(new STPReloadCommand(this));
    }

    // ---- 重载配置 ----
    public void reload() {
        reloadConfig();
        MessageUtil.init(this);
        homeProvider = HomeProviderFactory.create(this);
    }

    // ---- Getters ----
    public static ShuguangTP getInstance() { return instance; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public HomeProvider getHomeProvider() { return homeProvider; }
    public RequestManager getRequestManager() { return requestManager; }
}
