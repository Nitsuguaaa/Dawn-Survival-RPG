package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class VaultAndSpawnerManager {
    private final DawnSurvivalRPG plugin;

    public VaultAndSpawnerManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void manageVaultLoot() {

    }

    public void manageTrialSpawnerLoot() {

    }

    public void manageSpawnerSpawnedMob(Entity e) {
        if(e instanceof Player) return;
        if(!(e instanceof Creature creature)) return;

        AttributeInstance health = creature.getAttribute(Attribute.MAX_HEALTH);
        if(health != null) {
            double newHealth = health.getBaseValue() * 1.5;
            health.setBaseValue(newHealth);
            creature.setHealth(newHealth);
            //plugin.getLog().info("Set new health: " + newHealth + ", from: " + health.getBaseValue());
        }

        AttributeInstance damage = creature.getAttribute(Attribute.ATTACK_DAMAGE);
        if(damage != null) {
            double newDamage = damage.getBaseValue() * 1.5;
            damage.setBaseValue(newDamage);
            //plugin.getLog().info("Set new damage: " + newDamage + ", from: " + damage.getBaseValue());
        }
    }


}
