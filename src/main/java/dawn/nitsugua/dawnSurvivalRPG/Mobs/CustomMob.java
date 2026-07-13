package dawn.nitsugua.dawnSurvivalRPG.Mobs;

import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.MobGroups;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class CustomMob {
    private NamespacedKey parentMob;
    private String mobName;
    private MobGroups mobGroup;

    private List<ItemStack> customDrops;

    public CustomMob(NamespacedKey parentMob, String mobName, MobGroups mobGroup) {
        this.parentMob = parentMob;
        this.mobName = mobName;
        this.mobGroup = mobGroup;
    }


}
