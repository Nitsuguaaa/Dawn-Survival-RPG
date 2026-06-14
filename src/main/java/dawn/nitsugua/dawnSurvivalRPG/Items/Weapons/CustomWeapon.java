package dawn.nitsugua.dawnSurvivalRPG.Items.Weapons;

import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomWeapon extends CustomItem{
    //For custom weapons...and bows?
    private double weaponDamage;
    private int weaponDurability;

    public CustomWeapon(@NotNull String customMaterial, @NotNull String rarity, @NotNull String name, @NotNull Material parentMaterial, @NotNull String category, @NotNull String subCategory, double weaponDamage, int weaponDurability, @Nullable List<Component> lore, @Nullable String customModelData) {
        super(customMaterial, rarity, name, parentMaterial, category, subCategory, lore, customModelData);

        this.weaponDamage = weaponDamage;
        this.weaponDurability = weaponDurability;
    }

    public double getWeaponDamage() {
        return weaponDamage;
    }
    public CustomWeapon setWeaponDamage(double weaponDamage) {
        this.weaponDamage = weaponDamage;
        return this;
    }

    public int getWeaponDurability() {
        return weaponDurability;
    }
    public CustomWeapon setWeaponDurability(int weaponDurability) {
        this.weaponDurability = weaponDurability;
        return this;
    }

}
