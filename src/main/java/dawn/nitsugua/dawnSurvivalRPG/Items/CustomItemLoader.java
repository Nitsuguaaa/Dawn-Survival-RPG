package dawn.nitsugua.dawnSurvivalRPG.Items;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.jetbrains.annotations.NotNull;

public class CustomItemLoader {
    private final DawnSurvivalRPG plugin;

    public CustomItemLoader(@NotNull DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    //Do I just need separation of methods (static) or I want to use it elsewhere? (instance)

    public static void reloadCustomItem() {
        //run something
        //has to check if the item it scanned already exists then just update it
    }
}
