package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemFactory;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobManager {
    private final DawnSurvivalRPG plugin;
    private final CustomItemFactory itemFactory;

    public MobManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.itemFactory = new CustomItemFactory(plugin);
    }

    public void onMobDeath(EntityDeathEvent e, Entity entity, Player damager) {
        if(entity instanceof Enderman) {
            e.getDrops().clear();
            e.getDrops().add(itemFactory.createCustomItem("CONCENTRATED_EYE", 10));
        }
    }
}
