package cn.shuguang.shuguangtp.home;

import cn.shuguang.shuguangtp.ShuguangTP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 内置 Home 提供者，将 home 存储在 homes.yml 中
 * 命令：/sethome [name]  /delhome [name]（由本插件扩展提供，此处仅负责读取）
 */
public class BuiltinHomeProvider implements HomeProvider {

    private final ShuguangTP plugin;
    private final File homeFile;
    private FileConfiguration homeConfig;

    public BuiltinHomeProvider(ShuguangTP plugin) {
        this.plugin = plugin;
        this.homeFile = new File(plugin.getDataFolder(), "homes.yml");
        load();
    }

    private void load() {
        if (!homeFile.exists()) {
            try { homeFile.createNewFile(); } catch (IOException ignored) {}
        }
        homeConfig = YamlConfiguration.loadConfiguration(homeFile);
    }

    public void save() {
        try { homeConfig.save(homeFile); } catch (IOException e) {
            plugin.getLogger().warning("homes.yml 保存失败: " + e.getMessage());
        }
    }

    /** 设置 home（供 /sethome 命令调用） */
    public void setHome(Player player, String name, Location loc) {
        String path = player.getUniqueId() + "." + name;
        homeConfig.set(path + ".world",  loc.getWorld().getName());
        homeConfig.set(path + ".x", loc.getX());
        homeConfig.set(path + ".y", loc.getY());
        homeConfig.set(path + ".z", loc.getZ());
        homeConfig.set(path + ".yaw",   loc.getYaw());
        homeConfig.set(path + ".pitch", loc.getPitch());
        save();
    }

    /** 删除 home */
    public void deleteHome(Player player, String name) {
        homeConfig.set(player.getUniqueId() + "." + name, null);
        save();
    }

    @Override
    public Location getHome(Player player, String homeName) {
        String path = player.getUniqueId() + "." + homeName;
        if (!homeConfig.contains(path)) return null;
        String worldName = homeConfig.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        double x = homeConfig.getDouble(path + ".x");
        double y = homeConfig.getDouble(path + ".y");
        double z = homeConfig.getDouble(path + ".z");
        float yaw   = (float) homeConfig.getDouble(path + ".yaw");
        float pitch = (float) homeConfig.getDouble(path + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public List<String> getHomeNames(Player player) {
        String uid = player.getUniqueId().toString();
        if (!homeConfig.contains(uid)) return new ArrayList<>();
        return new ArrayList<>(homeConfig.getConfigurationSection(uid).getKeys(false));
    }

    @Override
    public String getProviderName() { return "内置(builtin)"; }
}
