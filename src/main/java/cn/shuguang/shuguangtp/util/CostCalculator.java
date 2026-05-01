package cn.shuguang.shuguangtp.util;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * 距离与费用计算工具
 */
public class CostCalculator {

    /**
     * 计算两点之间的直线距离（跨维度时使用各自坐标直接计算，不换算比例）
     */
    public static double distance(Location from, Location to) {
        double dx = from.getX() - to.getX();
        double dy = from.getY() - to.getY();
        double dz = from.getZ() - to.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * 判断两个位置是否跨维度
     */
    public static boolean isCrossDimension(Location from, Location to) {
        if (from.getWorld() == null || to.getWorld() == null) return false;
        return !from.getWorld().getUID().equals(to.getWorld().getUID());
    }

    /**
     * 计算费用
     *
     * @param from          出发位置
     * @param to            目的地位置
     * @param perBlock      每格费用
     * @param crossMulti    跨维度倍率
     * @param minimum       最低费用
     * @param maximum       最高费用（0=不限）
     * @param extra         额外附加费
     * @return 最终费用
     */
    public static double calculate(Location from, Location to,
                                    double perBlock, double crossMulti,
                                    double minimum, double maximum, double extra) {
        double dist = distance(from, to);
        double cost = dist * perBlock + extra;

        if (isCrossDimension(from, to)) {
            cost *= crossMulti;
        }

        cost = Math.max(cost, minimum);
        if (maximum > 0) {
            cost = Math.min(cost, maximum);
        }

        // 保留两位小数
        return Math.round(cost * 100.0) / 100.0;
    }

    /**
     * 获取维度显示名称
     */
    public static String getDimensionName(World world) {
        if (world == null) return "未知维度";
        switch (world.getEnvironment()) {
            case NETHER:  return "地狱";
            case THE_END: return "末地";
            default:      return "主世界";
        }
    }
}
