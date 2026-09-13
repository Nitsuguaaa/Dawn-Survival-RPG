package dawn.nitsugua.dawnSurvivalRPG.Listeners.EntityListeners;

import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.MobManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.CustomMob;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.MobGoals.FleeFromPlayerGoal;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class MobEvents implements Listener {
    private final DawnSurvivalRPG plugin;
    private final MobManager mobManager;
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    public MobEvents(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.mobManager = plugin.getMobManager();
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        if(e.getEntity() instanceof Player) return;
        if(!(e.getEntity() instanceof Creature)) return;
        if(!(e.getDamageSource().getCausingEntity() instanceof Player player)) return;

        mobManager.onMobDeath(e, e.getEntity(), player);

    }

        @EventHandler
        public void onMobSpawn(CreatureSpawnEvent e) {
            /*if(!(e.getEntity() instanceof Zombie zombie)) return;

            Goal<Zombie> goal = new FleeFromPlayerGoal(zombie);

            plugin.getServer().getMobGoals().removeAllGoals(zombie);
            plugin.getServer().getMobGoals().addGoal(zombie, 0, goal);*/
            if(!(e.getEntity() instanceof Creature creature)) return;

            mobManager.stripGenericMobGoal(e.getEntity());
            if(e.getEntity().getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER || e.getEntity().getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER) {
                PersistentDataContainer pdc = e.getEntity().getPersistentDataContainer();
                pdc.set(DawnKeys.SPAWNER_MOB, PersistentDataType.BOOLEAN, true);
                plugin.getVaultAndSpawnerManager().manageSpawnerSpawnedMob(creature);
                //plugin.getLog().info("Gave spawner mob tag");
            }
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
                    if(entity.getPersistentDataContainer().has(DawnKeys.SPAWNER_MOB)) {
                        plugin.getVaultAndSpawnerManager().manageSpawnerSpawnedMob(creature);
                    }
                }
            }
        }

    //Damage translator for ranged mobs
    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Mob shooter)) return;
        if (!(event.getEntity() instanceof Player)) return;
        //plugin.getLog().info("Shooting");

        // 3. Check if it's a Dawn custom mob or a spawner spawned mob
        if (!shooter.getPersistentDataContainer().has(DawnKeys.DAWN_MOB, PersistentDataType.BOOLEAN) && !shooter.getPersistentDataContainer().has(DawnKeys.SPAWNER_MOB, PersistentDataType.BOOLEAN)) return;

        AttributeInstance attackDamage = shooter.getAttribute(Attribute.ATTACK_DAMAGE);
        if(attackDamage != null) {
            event.setDamage(attackDamage.getValue());
        }
    }
//    @EventHandler
//    public void onHit(PlayerInteractEvent e) {
//        Player player = e.getPlayer();
//        if(!player.isOp()) return;
//        ItemStack item = player.getInventory().getItemInMainHand();
//        // 1. Only trigger on Right-Clicking a block (prevents double-firing)
//        if (item.getType() == Material.AIR || !item.hasItemMeta()) return;
//        // 2. Extract the name from the Component
//        Component nameComp = item.getItemMeta().displayName();
//        if (nameComp == null) return;
//        String itemName = PlainTextComponentSerializer.plainText().serialize(nameComp);
//        if (!itemName.equals("spawner-tester")) return;
//
//        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
//
//
//            // 3. Get the block and check if it's a spawner
//            Block block = e.getClickedBlock();
//            if(block == null) return;
//
//            cachedLocation = block.getLocation().add(0.5,1,0.5);
//            player.sendMessage(TextUtil.format("<green>Location cached: <white>" +
//                    cachedLocation.getWorld().getName() + " " +
//                    cachedLocation.getBlockX() + ", " +
//                    cachedLocation.getBlockY() + ", " +
//                    cachedLocation.getBlockZ()));
//
//           if(block.getType() == Material.AIR) return;
//            Location spawnLocation = block.getLocation().add(0.5, 1., 0.5);
//
//           Zombie zombie = block.getWorld().spawn(spawnLocation, Zombie.class);
//            zombie.setBaby();
//
//            zombie.getPersistentDataContainer().set(DawnKeys.DAWN_MOB, PersistentDataType.BOOLEAN, true);
//        } else if(e.getAction() == Action.LEFT_CLICK_AIR) {
//            if (cachedLocation == null) {
//                player.sendMessage(TextUtil.format("<red>No location cached! Right-click a block first."));
//                return;
//            }
//
//            //spawn mob here
//            Mob mob = mobManager.spawnCustomMob(cachedLocation, "GRAVEYARD_ZOMBIE");
//            MobGoals mobGoal = plugin.getServer().getMobGoals();
//
//            if(mob instanceof Animals animal) {
//                mobGoal.removeGoal(animal, VanillaGoal.TEMPT);
//                //plugin.getLog().info(TextUtil.format("<green>is an animal"));
//            }
//
//        }
//    }

    @EventHandler
    public void onBreed(EntityBreedEvent e) {
        if(e.getEntity() instanceof Villager) {
            e.setCancelled(true);
            return;
        }
        if(e.getEntity().getPersistentDataContainer().has(DawnKeys.DAWN_MOB)) e.setCancelled(true);
    }

//    @EventHandler
//    public void onDebugHit(EntityDamageByEntityEvent e) {
//        if (!(e.getDamager() instanceof Player player)) return;
//        if(!player.isOp()) return;
//        if (!(e.getEntity() instanceof Mob mob)) return;
//
//        // 1. Check if they are holding the "spawner-tester" stick
//        ItemStack hand = player.getInventory().getItemInMainHand();
//        if (hand.getType() == Material.AIR || !hand.hasItemMeta()) return;
//
//        Component nameComp = hand.getItemMeta().displayName();
//        if (nameComp == null) return;
//        String itemName = PlainTextComponentSerializer.plainText().serialize(nameComp);
//        if (!itemName.equals("mob-tester")) return;
//
//        // 2. Cancel the damage so you don't accidentally kill the mob while debugging!
//        e.setCancelled(true);
//
//        // 3. Print the debug info
//        printMobDebugInfo(player, mob);
//    }
//
//    private void printMobDebugInfo(Player player, Mob mob) {
//        player.sendMessage("<gold><b>--- " + mob.getType().name() + " DEBUG ---");
//
//        // --- ATTRIBUTES ---
//        player.sendMessage("<yellow>Attributes:");
//        // Loop through all possible attributes in the game
//        for (Attribute attribute : Registry.ATTRIBUTE) {
//            AttributeInstance instance = mob.getAttribute(attribute);
//            // If the mob actually has this attribute, print it!
//            if (instance != null) {
//                player.sendMessage("<gray> • " + attribute.getKey().getKey() + ": <white>" + instance.getBaseValue());
//            }
//        }
//
//        // --- PDC TAGS ---
//        player.sendMessage("<yellow>PDC Tags:");
//        PersistentDataContainer pdc = mob.getPersistentDataContainer();
//        if (pdc.getKeys().isEmpty()) {
//            player.sendMessage("<gray> • No tags found.");
//        } else {
//            for (NamespacedKey key : pdc.getKeys()) {
//                String value = extractPdcValue(pdc, key);
//                player.sendMessage("<gray> • " + key.asString() + ": <white>" + value);
//            }
//        }
//    }

//    // Helper method to safely extract PDC values without knowing the type
//    private String extractPdcValue(PersistentDataContainer pdc, NamespacedKey key) {
//        if (pdc.has(key, PersistentDataType.STRING)) return pdc.get(key, PersistentDataType.STRING);
//        if (pdc.has(key, PersistentDataType.INTEGER)) return String.valueOf(pdc.get(key, PersistentDataType.INTEGER));
//        if (pdc.has(key, PersistentDataType.DOUBLE)) return String.valueOf(pdc.get(key, PersistentDataType.DOUBLE));
//        if (pdc.has(key, PersistentDataType.BOOLEAN)) return String.valueOf(pdc.get(key, PersistentDataType.BOOLEAN));
//        if (pdc.has(key, PersistentDataType.LONG)) return String.valueOf(pdc.get(key, PersistentDataType.LONG));
//        return "[Complex Type]";
//    }

    @EventHandler
    public void onBurn(EntityCombustEvent e) {
        if(e instanceof EntityCombustByBlockEvent || e instanceof EntityCombustByEntityEvent) return;
        Entity entity = e.getEntity();
        if(!entity.getPersistentDataContainer().has(DawnKeys.DAWN_MOB)) return;

        e.setCancelled(true);
    }

    private final Set<CreatureSpawnEvent.SpawnReason> ALLOWED_REASONS = Set.of(
            CreatureSpawnEvent.SpawnReason.SPAWNER,
            CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER,
            CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM,
            CreatureSpawnEvent.SpawnReason.BUILD_COPPERGOLEM,
            CreatureSpawnEvent.SpawnReason.COMMAND,
            CreatureSpawnEvent.SpawnReason.CUSTOM,
            CreatureSpawnEvent.SpawnReason.SPAWNER_EGG,
            CreatureSpawnEvent.SpawnReason.OMINOUS_ITEM_SPAWNER,
            CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN,
            CreatureSpawnEvent.SpawnReason.SPELL,
            CreatureSpawnEvent.SpawnReason.ENCHANTMENT
    );

    private final Set<String> ALLOWED_WORLDS = Set.of(
            "world",
            "world_nether",
            "world_the_end",
            "dungeon-1",
            "arkos"
    );

    private static final Set<EntityType> BANNED_CHUNK_SPAWNS = EnumSet.of(
            // Villages
            EntityType.VILLAGER, EntityType.IRON_GOLEM, EntityType.CAT,
            EntityType.HORSE, EntityType.PIG, EntityType.COW, EntityType.SHEEP, EntityType.CHICKEN,
            // End Cities
            EntityType.SHULKER,
            // Mansions & Outposts
            EntityType.EVOKER, EntityType.VINDICATOR, EntityType.ALLAY, EntityType.PILLAGER,
            // Ocean Monuments
            EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.DROWNED,
            // Nether Bastions
            EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.HOGLIN,
            // Witch Huts
            EntityType.WITCH
    );

    @EventHandler
    public void banVillagersOnChunks(ChunkLoadEvent e) {
        if(!ALLOWED_WORLDS.contains(e.getWorld().getName())) return;
        if(!e.isNewChunk()) return;

        Bukkit.getScheduler().runTask(plugin, () -> {
           for(Entity entity : e.getChunk().getEntities()) {
               if(BANNED_CHUNK_SPAWNS.contains(entity.getType())) {
                   entity.remove();
               }
           }
        });
    }

    @EventHandler
    public void stopNaturalSpawn(CreatureSpawnEvent e) {
        if(e.getEntity() instanceof EnderDragon || e.getEntity() instanceof Warden || e.getEntity() instanceof Wither) return;

        if(!ALLOWED_WORLDS.contains(e.getEntity().getWorld().getName())) return;

        CreatureSpawnEvent.SpawnReason reason = e.getSpawnReason();

        if(e.getEntity() instanceof Villager) {
            e.setCancelled(true);
            return;
        }

        if(ALLOWED_REASONS.contains(reason)) {
            //plugin.getLog().info("Allowed spawn for: " + e.getEntity().getName() + ". Spawn Reason: " + e.getEntity().getEntitySpawnReason().name());
        } else {
            e.setCancelled(true);
            //plugin.getLog().info("Banned spawn for: " + e.getEntity().getName() + ". Spawn Reason: " + e.getEntity().getEntitySpawnReason().name());
        }
    }
}
