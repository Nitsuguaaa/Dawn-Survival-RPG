package dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.destroystokyo.paper.entity.ai.MobGoals;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.MobManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.MobGoals.FleeFromPlayerGoal;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;

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

        mobManager.onMobDeath(e, e.getEntity(), player);

    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        /*if(!(e.getEntity() instanceof Zombie zombie)) return;

        Goal<Zombie> goal = new FleeFromPlayerGoal(zombie);

        plugin.getServer().getMobGoals().removeAllGoals(zombie);
        plugin.getServer().getMobGoals().addGoal(zombie, 0, goal);*/
        if(!(e.getEntity() instanceof Creature)) return;

        mobManager.stripGenericMobGoal(e.getEntity());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for(Entity entity : e.getChunk().getEntities()) {
            if(!(entity instanceof Creature)) return;
            mobManager.stripGenericMobGoal((LivingEntity) entity);
        }
    }
}
