package dawn.nitsugua.dawnSurvivalRPG.Items.Weapons;

import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomWeapon extends CustomItem {
    //For custom weapons...and bows?

    public CustomWeapon(@NotNull String customMaterial, @NotNull String name, @NotNull Material parentMaterial, @Nullable String customModelData) {
        super(customMaterial, name, parentMaterial, customModelData);
    }

}
