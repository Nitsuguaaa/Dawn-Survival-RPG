package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;

public class ManaManager {
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    private final DawnSurvivalRPG plugin;

    public ManaManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void onManaItemUse(CustomItem customItem) {
        int manaCost = customItem.getManaCost();
    }
}
