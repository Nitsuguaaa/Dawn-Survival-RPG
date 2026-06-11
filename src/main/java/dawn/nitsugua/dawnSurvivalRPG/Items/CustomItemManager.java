package dawn.nitsugua.dawnSurvivalRPG.Items;

import java.util.HashMap;

public class CustomItemManager {
    //Changed to Manager which is a Registry and a Manager merged into one.

    //This contained the runtime storage of all custom items.
    //When a customitem is needed, CustomItemFactory will retrieve data here and fabricate a world item of that instance
    private HashMap<String, CustomItem> customItems = new HashMap<>();

    public void newItem(CustomItem customItem) {
        //register it here
        customItems.put(customItem.name, customItem);
    }
}
