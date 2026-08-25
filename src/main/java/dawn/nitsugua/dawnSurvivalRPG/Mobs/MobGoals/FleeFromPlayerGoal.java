package dawn.nitsugua.dawnSurvivalRPG.Mobs.MobGoals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

public class FleeFromPlayerGoal implements Goal<Zombie> {

    private static final DawnSurvivalRPG plugin = DawnSurvivalRPG.getInstance();

    // This is the key for the goal. It is used to identify the goal and is
    // used to determine if two goals are the same.
    public static final GoalKey<Zombie> KEY = GoalKey.of(
            // The entity class this goal is targeting.
            Zombie.class,
            // The key used for identification. Should use your plugin's namespace.
            new NamespacedKey(plugin, "flee_from_player")
    );

    private final Zombie zombie;
    private final double fleeDistance = 8.0;
    private final double runSpeed = 0.3;

    public FleeFromPlayerGoal(Zombie zombie) {
        this.zombie = zombie;
    }

    @Override
    public boolean shouldActivate() {
        List<Entity> nearbyPlayers = zombie.getNearbyEntities(fleeDistance, fleeDistance, fleeDistance)
                .stream()
                .filter(e -> e instanceof Player)
                .toList();

        return !nearbyPlayers.isEmpty();
    }

    @Override
    public boolean shouldStayActive() {
        return shouldActivate();
    }

    @Override
    public void tick() {

        if(zombie.getTicksLived() % 20 != 0) return;

        // Find the nearest player
        Player nearest = zombie.getNearbyEntities(fleeDistance, fleeDistance, fleeDistance)
                .stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                // PRO TIP: Use distanceSquared instead of distance!
                // It avoids the heavy Math.sqrt() calculation every single tick.
                .min((p1, p2) -> Double.compare(
                        p1.getLocation().distanceSquared(zombie.getLocation()),
                        p2.getLocation().distanceSquared(zombie.getLocation())
                ))
                .orElse(null);

        if (nearest == null) return;

        // 1. Calculate the vector pointing AWAY from the player
        Vector awayDirection = zombie.getLocation().toVector()
                .subtract(nearest.getLocation().toVector())
                .normalize();

        // 2. Pick a target location a few blocks away in that direction
        Location targetLocation = zombie.getLocation().add(awayDirection.multiply(3.0));

        // 3. Tell the Pathfinder to WALK there!
        // Speed for pathfinder is usually around 0.25 (slow walk) to 1.0 (sprint).
        // A normal zombie walks at about 0.23, so 0.4 makes it run in panic!
        zombie.getPathfinder().moveTo(targetLocation, 2);
    }

    @Override
    public GoalKey<Zombie> getKey() {
        return KEY;
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE);
    }
}
