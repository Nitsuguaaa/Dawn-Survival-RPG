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

    public CustomWeapon(@NotNull String customMaterial, @NotNull String name, @NotNull Material parentMaterial, @Nullable List<Component> lore, @Nullable String customModelData) {
        super(customMaterial, name, parentMaterial, lore, customModelData);
    }

}
