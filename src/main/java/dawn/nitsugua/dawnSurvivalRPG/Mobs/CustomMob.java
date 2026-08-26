package dawn.nitsugua.dawnSurvivalRPG.Mobs;

import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.MobGroups;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;

public class CustomMob {

    // Identity (Usually these don't change on reload, but we'll make them mutable just in case)
    private String mobName;
    private EntityType parentType;
    private MobGroups mobGroup;
    private Component displayName;

    // Core Stats (These WILL change when you tweak the YAML and reload!)
    private double maxHealth;
    private double attackDamage;
    private double movementSpeed;
    private double followRange;
    private double attackSpeed;

    public CustomMob(String mobName, EntityType parentType, MobGroups mobGroup,
                     Component displayName, double maxHealth, double attackDamage,
                     double movementSpeed, double followRange, double attackSpeed) {
        this.mobName = mobName;
        this.parentType = parentType;
        this.mobGroup = mobGroup;
        this.displayName = displayName;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.movementSpeed = movementSpeed;
        this.followRange = followRange;
        this.attackSpeed = attackSpeed;
    }

    // Getters
    public String getMobName() { return mobName; }
    public EntityType getParentType() { return parentType; }
    public MobGroups getMobGroup() { return mobGroup; }
    public Component getDisplayName() { return displayName; }

    public double getMaxHealth() { return maxHealth; }
    public double getAttackDamage() { return attackDamage; }
    public double getMovementSpeed() { return movementSpeed; }
    public double getFollowRange() { return followRange; }
    public double getAttackSpeed() { return attackSpeed; }

    // Setters for Hot-Reloading!
    public void setDisplayName(Component displayName) { this.displayName = displayName; }
    public void setMaxHealth(double maxHealth) { this.maxHealth = maxHealth; }
    public void setAttackDamage(double attackDamage) { this.attackDamage = attackDamage; }
    public void setMovementSpeed(double movementSpeed) { this.movementSpeed = movementSpeed; }
    public void setFollowRange(double followRange) { this.followRange = followRange; }
    public void setAttackSpeed(double attackSpeed) { this.attackSpeed = attackSpeed; }
}