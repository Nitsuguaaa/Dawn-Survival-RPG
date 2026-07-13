package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MobManager {
    private final DawnSurvivalRPG plugin;

    public MobManager(DawnSurvivalRPG plugin) {this.plugin = plugin;}

    public void onMobDeath(Entity entity, Player damager) {
        if(entity instanceof Chicken) {
            damager.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "You Killed a CHIMKEN!"));
        }
    }
}
