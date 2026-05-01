package cn.shuguang.shuguangtp.manager;

import cn.shuguang.shuguangtp.ShuguangTP;
import cn.shuguang.shuguangtp.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 延迟传送管理器
 * 扣费成功后等待指定秒数再执行传送，期间若玩家移动则取消
 */
public class DelayedTeleportManager {

    private final ShuguangTP plugin;

    /** key = 玩家UUID，value = 正在等待传送的任务 */
    private final Map<UUID, BukkitTask> pendingTeleports = new HashMap<>();

    /** 玩家传送前的位置快照（用于检测移动） */
    private final Map<UUID, Location> startLocations = new HashMap<>();

    public DelayedTeleportManager(ShuguangTP plugin) {
        this.plugin = plugin;
    }

    /**
     * 提交一个延迟传送任务
     *
     * @param player   传送者
     * @param dest     目的地
     * @param cost     已预先扣除的费用（若取消则退款）
     * @param onDone   传送完成回调（实际执行传送后调用）
     */
    public void submit(Player player, Location dest, double cost, Runnable onDone) {
        UUID uid = player.getUniqueId();

        // 取消已有的等待传送
        cancel(uid, false);

        int delay = plugin.getConfig().getInt("teleport.delay", 3);
        boolean cancelOnMove = plugin.getConfig().getBoolean("teleport.cancel-on-move", true);

        // 延迟为 0 时直接传送
        if (delay <= 0) {
            player.teleport(dest);
            if (onDone != null) onDone.run();
            return;
        }

        // 记录起始位置
        startLocations.put(uid, player.getLocation().clone());

        // 提示
        MessageUtil.send(player, "teleport-start",
                MessageUtil.of("delay", String.valueOf(delay)));

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                pendingTeleports.remove(uid);
                Location start = startLocations.remove(uid);

                if (!player.isOnline()) return;

                // 检测移动
                if (cancelOnMove && start != null && hasMoved(start, player.getLocation())) {
                    // 退款
                    plugin.getEconomyManager().refund(player, cost);
                    MessageUtil.send(player, "teleport-cancelled");
                    return;
                }

                player.teleport(dest);
                if (onDone != null) onDone.run();
            }
        }.runTaskLater(plugin, delay * 20L);

        pendingTeleports.put(uid, task);
    }

    private boolean hasMoved(Location a, Location b) {
        if (!a.getWorld().equals(b.getWorld())) return true;
        return a.getBlockX() != b.getBlockX()
                || a.getBlockY() != b.getBlockY()
                || a.getBlockZ() != b.getBlockZ();
    }

    private void cancel(UUID uid, boolean refund) {
        BukkitTask task = pendingTeleports.remove(uid);
        if (task != null) task.cancel();
        startLocations.remove(uid);
    }

    public boolean isPending(Player player) {
        return pendingTeleports.containsKey(player.getUniqueId());
    }
}
