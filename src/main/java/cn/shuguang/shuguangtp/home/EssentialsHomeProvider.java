package cn.shuguang.shuguangtp.home;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于 Essentials 的 Home 提供者
 */
public class EssentialsHomeProvider implements HomeProvider {

    private final Essentials ess;

    public EssentialsHomeProvider(Essentials ess) {
        this.ess = ess;
    }

    @Override
    public Location getHome(Player player, String homeName) {
        try {
            User user = ess.getUser(player);
            if (user == null) return null;
            return user.getHome(homeName);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getHomeNames(Player player) {
        try {
            User user = ess.getUser(player);
            if (user == null) return new ArrayList<>();
            return new ArrayList<>(user.getHomes());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public String getProviderName() {
        return "Essentials";
    }
}
