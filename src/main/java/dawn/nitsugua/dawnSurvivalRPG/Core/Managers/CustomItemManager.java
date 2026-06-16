package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class CustomItemManager {
    //Changed to Manager which is a Registry and a Manager merged into one.
    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    public CustomItemManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }
}
