package dawn.nitsugua.dawnSurvivalRPG.Items;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class CustomItemManager {
    //Changed to Manager which is a Registry and a Manager merged into one.
    private final DawnSurvivalRPG plugin;

    public CustomItemManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    //This contained the runtime storage of all custom items.
    //When a customitem is needed, CustomItemFactory will retrieve data here and fabricate a world item of that instance
    public static HashMap<String, CustomItem> customItems = new HashMap<>();

    public void registerItem(CustomItem customItem) {
        //register it here
        customItems.put(customItem.customMaterial, customItem);
        plugin.getLog().info(TextUtil.format("<green>Successfully registered custom item: <white>" + customItem.getItemName() + " <green> With custom material name: <white>" + customItem.getCustomMaterial()));
        plugin.getLog().info("getting custom item: " + customItems.get(customItem.customMaterial).getItemName());
    }

    @Nullable
    public CustomItem getCustomItem(String customMaterial) {
        plugin.getLog().info("Available keys: " + customItems.keySet());
        //plugin.getLog().info(TextUtil.format("[CustomItemManager]<green>Getting item: " + customItems.get("END_SWORD").getCustomMaterial()));
        if(customItems.get(customMaterial) == null) {
            return null;
        }
        return customItems.get(customMaterial);
    }
}
