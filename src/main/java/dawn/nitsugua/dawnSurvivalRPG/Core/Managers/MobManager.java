package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemFactory;
import dawn.nitsugua.dawnSurvivalRPG.Loots.LootTable;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.CustomMob;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.CustomMobLoader;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.w3c.dom.Attr;

import java.util.Collection;
import java.util.stream.Collectors;

public class MobManager {
    private final DawnSurvivalRPG plugin;
    private final LootManager lootManager;
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    public MobManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.lootManager = plugin.getLootManager();
    }

    public void onMobDeath(EntityDeathEvent event, Entity entity, Player damager) {
        event.setDroppedExp(0);
        event.getDrops().clear();
        PersistentDataContainer pdc = entity.getPersistentDataContainer();


        //if custom mob
        if(pdc.has(DawnKeys.DAWN_MOB, PersistentDataType.BOOLEAN)) {
            String mobName = pdc.get(DawnKeys.DAWN_MOB_NAME, PersistentDataType.STRING);
            if(mobName == null) return;
            plugin.getLog().info("DAWN MOB FOUND: " + mobName);

            LootTable table = dawnRegistry.getLootTable(mobName);
            if(table != null) {
                event.getDrops().addAll(lootManager.rollLoot(table));
                event.setDroppedExp(table.rollExp());
                plugin.getLog().info("ROLLING LOOT");
            } else {
                plugin.getLog().warn("LOOT NOT FOUND: " + mobName);
            }
            return;
        }

        //if vanilla mob inside protected area
        if(dawnRegistry.isProtected(entity.getWorld().getName(), entity.getChunk().getX(), entity.getChunk().getZ())) {
            String vanillaName = entity.getType().toString();
            LootTable table = dawnRegistry.getLootTable(vanillaName);
            if(table != null) {
                event.getDrops().addAll(lootManager.rollLoot(table));
                event.setDroppedExp(table.rollExp());
            }
        }
    }

    public void stripGenericMobGoal(LivingEntity entity) {
        if(entity instanceof EnderDragon || entity instanceof Wither || entity instanceof Warden) return;

        if(entity instanceof Creature creature) {

            plugin.getServer().getMobGoals().removeGoal(creature, VanillaGoal.RANDOM_LOOK_AROUND);
            plugin.getServer().getMobGoals().removeGoal(creature, VanillaGoal.RANDOM_STROLL);
            plugin.getServer().getMobGoals().removeGoal(creature, VanillaGoal.WATER_AVOIDING_RANDOM_STROLL);
            plugin.getServer().getMobGoals().removeGoal(creature, VanillaGoal.MOVE_THROUGH_VILLAGE);
            plugin.getServer().getMobGoals().removeGoal(creature, VanillaGoal.MOVE_BACK_TO_VILLAGE);

            /*if(creature instanceof Zombie zombie) {
                zombie.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(3);
            }*/

            // 2. Grab all remaining goals (Returns a Collection<Goal<Mob>>)
//            Collection<Goal<Mob>> remainingGoals = plugin.getServer().getMobGoals().getAllGoals(creature);
//            String goalNames = remainingGoals.stream()
//                    // 1. goal.getKey() -> returns GoalKey
//                    // 2. .getNamespacedKey() -> returns NamespacedKey
//                    // 3. .getKey() -> returns the String name (e.g. "random_stroll")
//                    .map(goal -> goal.getKey().getNamespacedKey().asString())
//                    .collect(Collectors.joining(", "));

            // 4. Send it to the player!
//            Player player = plugin.getServer().getPlayer("Nitsuguaaa");
//            if (player != null) {
//                player.sendMessage(MessagesUtil.sendInfo(
//                        MessagesUtil.commonMessages.SUCCESS,
//                        "Remaining goals for " + creature.getType() + ": [" + goalNames + "]"
//                ));
//            } else {
//                plugin.getLogger().info("Remaining goals for " + creature.getType() + ": [" + goalNames + "]");
//            }
        }
    }

    public Mob spawnCustomMob(Location location, String mobName) {
        CustomMob blueprint = dawnRegistry.getCustomMob(mobName.toUpperCase());
        if(blueprint == null) {
            plugin.getLog().info(TextUtil.sendLogInfo("MobManager", TextUtil.LogInfo.ERROR, "Attempted to spawn unknown mob: <white>" + mobName));
            return null;
        }

        Mob entity = (Mob) location.getWorld().spawnEntity(location, blueprint.getParentType());

        entity.customName(blueprint.getDisplayName());
        entity.setCustomNameVisible(true);

        entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(blueprint.getMaxHealth());
        entity.setHealth(blueprint.getMaxHealth()); //pretty sure setting max health only sets but not heal so I added this

        if(blueprint.isPassive()) {
            entity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(blueprint.getAttackDamage());
        }

        entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(blueprint.getMovementSpeed());
        //entity.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(blueprint.getAttackDamage());
        entity.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(blueprint.getFollowRange());



        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(DawnKeys.DAWN_MOB, PersistentDataType.BOOLEAN, true);
        pdc.set(DawnKeys.DAWN_MOB_NAME, PersistentDataType.STRING, blueprint.getMobName());

        stripGenericMobGoal(entity);

        return entity;
    }

    public void reload() {
        dawnRegistry.clearCustomMobs();
        CustomMobLoader.initialize(plugin);
    }
}
