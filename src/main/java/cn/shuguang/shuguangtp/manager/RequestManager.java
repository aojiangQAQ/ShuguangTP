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
 * 玩家 TP 请求管理器
 * 负责维护 "谁向谁发起请求" 以及自动超时取消
 */
public class RequestManager {

    private final ShuguangTP plugin;

    /** 待处理请求：key = 目标玩家UUID，value = 请求对象 */
    private final Map<UUID, TeleportRequest> pendingRequests = new HashMap<>();

    /** 超时任务 */
    private final Map<UUID, BukkitTask> timeoutTasks = new HashMap<>();

    public RequestManager(ShuguangTP plugin) {
        this.plugin = plugin;
    }

    /**
     * 发起一个传送到玩家的请求
     *
     * @param requester  发起者
     * @param target     目标玩家（接受请求的人）
     * @param cost       预计费用
     */
    public void createRequest(Player requester, Player target, double cost) {
        UUID targetId = target.getUniqueId();

        // 取消旧请求（如有）
        cancelRequest(targetId);

        TeleportRequest req = new TeleportRequest(requester, target, cost);
        pendingRequests.put(targetId, req);

        int timeout = plugin.getConfig().getInt("timeout.request-expire", 30);

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (pendingRequests.containsKey(targetId)) {
                    pendingRequests.remove(targetId);
                    timeoutTasks.remove(targetId);
                    MessageUtil.send(requester, "request-expired");
                    if (target.isOnline()) {
                        MessageUtil.send(target, "request-expired");
                    }
                }
            }
        }.runTaskLater(plugin, timeout * 20L);

        timeoutTasks.put(targetId, task);
    }

    /** 获取目标玩家收到的请求，不存在返回 null */
    public TeleportRequest getRequest(Player target) {
        return pendingRequests.get(target.getUniqueId());
    }

    /** 移除请求（同意/拒绝后调用） */
    public void removeRequest(Player target) {
        UUID id = target.getUniqueId();
        pendingRequests.remove(id);
        BukkitTask task = timeoutTasks.remove(id);
        if (task != null) task.cancel();
    }

    /** 取消指定目标的请求（内部用） */
    private void cancelRequest(UUID targetId) {
        pendingRequests.remove(targetId);
        BukkitTask task = timeoutTasks.remove(targetId);
        if (task != null) task.cancel();
    }

    /** 插件卸载时清理所有任务 */
    public void cancelAll() {
        timeoutTasks.values().forEach(BukkitTask::cancel);
        timeoutTasks.clear();
        pendingRequests.clear();
    }

    // ---- 内部数据类 ----
    public static class TeleportRequest {
        public final Player requester;
        public final Player target;
        public final double cost;
        public final long createdAt;

        public TeleportRequest(Player requester, Player target, double cost) {
            this.requester = requester;
            this.target = target;
            this.cost = cost;
            this.createdAt = System.currentTimeMillis();
        }
    }
}
