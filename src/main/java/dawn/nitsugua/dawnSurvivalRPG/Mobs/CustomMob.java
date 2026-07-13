package dawn.nitsugua.dawnSurvivalRPG.Mobs;

import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.MobGroups;
import org.bukkit.NamespacedKey;

import java.util.List;

public class CustomMob {
    private NamespacedKey parentMob;
    private String mobName;
    private MobGroups mobGroup;

    private List<CustomItem> customDrops;

    public CustomMob(NamespacedKey parentMob, String mobName, MobGroups mobGroup) {
        this.parentMob = parentMob;
        this.mobName = mobName;
        this.mobGroup = mobGroup;
    }

    public NamespacedKey getParentMob() {
        return parentMob;
    }
    public String getMobName() {
        return mobName;
    }
    public MobGroups getMobGroup() {
        return mobGroup;
    }

    public List<CustomItem> getCustomDrops() {return customDrops;}
    public void setCustomDrops(List<CustomItem> customDrops) {this.customDrops = customDrops;}
}
