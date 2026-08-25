package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.MobGoals;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItemFactory;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Collection;
import java.util.stream.Collectors;

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

    public void stripGenericMobGoal(LivingEntity entity) {

        if(entity instanceof Creature creature) {

            plugin.getServer().getMobGoals().removeGoal(creature, VanillaGoal.RANDOM_LOOK_AROUND);
            plugin.getServer().getMobGoals().removeGoal(creature, VanillaGoal.RANDOM_STROLL);
            plugin.getServer().getMobGoals().removeGoal(creature, VanillaGoal.WATER_AVOIDING_RANDOM_STROLL);
            plugin.getServer().getMobGoals().removeGoal(creature, VanillaGoal.MOVE_THROUGH_VILLAGE);
            plugin.getServer().getMobGoals().removeGoal(creature, VanillaGoal.MOVE_BACK_TO_VILLAGE);

            if(creature instanceof Zombie zombie) {
                zombie.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(3);
            }

            // 2. Grab all remaining goals (Returns a Collection<Goal<Mob>>)
            Collection<Goal<Mob>> remainingGoals = plugin.getServer().getMobGoals().getAllGoals(creature);
            String goalNames = remainingGoals.stream()
                    // 1. goal.getKey() -> returns GoalKey
                    // 2. .getNamespacedKey() -> returns NamespacedKey
                    // 3. .getKey() -> returns the String name (e.g. "random_stroll")
                    .map(goal -> goal.getKey().getNamespacedKey().asString())
                    .collect(Collectors.joining(", "));

            // 4. Send it to the player!
            Player player = plugin.getServer().getPlayer("Nitsuguaaa");
            if (player != null) {
                player.sendMessage(MessagesUtil.sendInfo(
                        MessagesUtil.commonMessages.SUCCESS,
                        "Remaining goals for " + creature.getType() + ": [" + goalNames + "]"
                ));
            } else {
                plugin.getLogger().info("Remaining goals for " + creature.getType() + ": [" + goalNames + "]");
            }
        }


    }
}
