package dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.MobManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobEvents implements Listener {
    private final DawnSurvivalRPG plugin;
    private final MobManager mobManager;

    public MobEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.mobManager = new MobManager(plugin);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        if(e.getEntity() instanceof Player) return;
        if(!(e.getDamageSource().getCausingEntity() instanceof Player player)) return;

        mobManager.onMobDeath(e.getEntity(), player);

    }
}
