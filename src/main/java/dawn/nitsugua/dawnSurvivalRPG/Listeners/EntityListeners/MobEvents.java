package dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.destroystokyo.paper.entity.ai.MobGoals;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.MobManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.MobGoals.FleeFromPlayerGoal;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class MobEvents implements Listener {
    private final DawnSurvivalRPG plugin;
    private final MobManager mobManager;

    private Location cachedLocation = null;

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

//    @EventHandler
//    public void onChunkLoad(ChunkLoadEvent e) {
//        for(Entity entity : e.getChunk().getEntities()) {
//            if(!(entity instanceof Creature)) return;
//            mobManager.stripGenericMobGoal((LivingEntity) entity);
//        }
//    }

    @EventHandler
    public void onEntityLoad(EntitiesLoadEvent e) {
        for(Entity entity : e.getEntities()) {
            if(entity instanceof Creature creature) {
                mobManager.stripGenericMobGoal(creature);
            }
        }
    }

    @EventHandler
    public void onHit(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        // 1. Only trigger on Right-Clicking a block (prevents double-firing)
        if (item.getType() == Material.AIR || !item.hasItemMeta()) return;
        // 2. Extract the name from the Component
        Component nameComp = item.getItemMeta().displayName();
        if (nameComp == null) return;
        String itemName = PlainTextComponentSerializer.plainText().serialize(nameComp);
        if (!itemName.equals("spawner-tester")) return;

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {


            // 3. Get the block and check if it's a spawner
            Block block = e.getClickedBlock();
            if(block == null) return;

            cachedLocation = block.getLocation().add(0.5,1,0.5);
            player.sendMessage(TextUtil.format("<green>Location cached: <white>" +
                    cachedLocation.getWorld().getName() + " " +
                    cachedLocation.getBlockX() + ", " +
                    cachedLocation.getBlockY() + ", " +
                    cachedLocation.getBlockZ()));

//            if(block.getType() == Material.AIR) return;
//            Location spawnLocation = block.getLocation().add(0.5, 1., 0.5);
//
//            Zombie zombie = block.getWorld().spawn(spawnLocation, Zombie.class);
//            zombie.setBaby();
//
//            zombie.getPersistentDataContainer().set(DawnKeys.DAWN_MOB, PersistentDataType.BOOLEAN, true);
        } else if(e.getAction() == Action.LEFT_CLICK_AIR) {
            if (cachedLocation == null) {
                player.sendMessage("<red>No location cached! Right-click a block first.");
                return;
            }

            Zombie zombie = cachedLocation.getWorld().spawn(cachedLocation, Zombie.class);
            zombie.setPersistent(true);
            zombie.setRemoveWhenFarAway(false);
            zombie.setBaby();
            zombie.customName(TextUtil.format("<green><b>Gary"));
            zombie.setCustomNameVisible(true);
            zombie.getPersistentDataContainer().set(
                    DawnKeys.DAWN_MOB, PersistentDataType.BOOLEAN, true);

            player.sendMessage("<green>Zombie spawned at cached location!");
            player.sendMessage("<gray>Chunk loaded: <white>" +
                    cachedLocation.getChunk().isLoaded());
        }
    }

    @EventHandler
    public void onBurn(EntityCombustEvent e) {
        if(e instanceof EntityCombustByBlockEvent || e instanceof EntityCombustByEntityEvent) return;
        Entity entity = e.getEntity();
        if(!entity.getPersistentDataContainer().has(DawnKeys.DAWN_MOB)) return;

        e.setCancelled(true);
    }
}
