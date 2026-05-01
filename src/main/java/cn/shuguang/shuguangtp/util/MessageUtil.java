package cn.shuguang.shuguangtp.util;

import cn.shuguang.shuguangtp.ShuguangTP;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息工具类，从 config.yml 读取消息并替换变量
 */
public class MessageUtil {

    private static ShuguangTP plugin;
    private static String prefix;

    public static void init(ShuguangTP p) {
        plugin = p;
        prefix = color(p.getConfig().getString("messages.prefix", "&8[&b曙光传送&8] "));
    }

    /** 发送带前缀的消息 */
    public static void send(CommandSender sender, String key, Map<String, String> placeholders) {
        String raw = plugin.getConfig().getString("messages." + key, "&cMissing message: " + key);
        String msg = replace(raw, placeholders);
        sender.sendMessage(prefix + color(msg));
    }

    public static void send(CommandSender sender, String key) {
        send(sender, key, new HashMap<>());
    }

    /** 获取原始消息（不加前缀，已上色） */
    public static String get(String key, Map<String, String> placeholders) {
        String raw = plugin.getConfig().getString("messages." + key, "&cMissing: " + key);
        return color(replace(raw, placeholders));
    }

    public static String get(String key) {
        return get(key, new HashMap<>());
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private static String replace(String template, Map<String, String> vars) {
        for (Map.Entry<String, String> e : vars.entrySet()) {
            template = template.replace("{" + e.getKey() + "}", e.getValue());
        }
        return template;
    }

    /** 快捷 map 构建 */
    public static Map<String, String> of(String... pairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return map;
    }
}
