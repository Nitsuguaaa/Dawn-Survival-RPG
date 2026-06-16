package dawn.nitsugua.dawnSurvivalRPG.Core.Registry;

import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class DawnRegistry {
    private static final DawnRegistry INSTANCE = new DawnRegistry();
    private static final HashMap<String, CustomItem> customItems = new HashMap<>(); //Custom Items

    public DawnRegistry() {}
    public static DawnRegistry getInstance() {return INSTANCE;};

    public boolean registerItem(CustomItem customItem) {
        //register it here
        customItems.put(customItem.getCustomMaterial(), customItem);
        return true;
    }

    @Nullable
    public CustomItem getCustomItem(String customMaterial) {
        return customItems.get(customMaterial);
    }



}
