package dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobEvents implements Listener {
    private final DawnSurvivalRPG plugin;

    public MobEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        if(e.getEntity() instanceof Player) return;

        e.getDrops().clear();


    }
}
