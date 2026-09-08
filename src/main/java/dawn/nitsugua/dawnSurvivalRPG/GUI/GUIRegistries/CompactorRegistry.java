package dawn.nitsugua.dawnSurvivalRPG.GUI.GUIRegistries;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.GUI.Utils.ExchangeRecipe;
import dawn.nitsugua.dawnSurvivalRPG.Utils.InventoryUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CompactorRegistry {

    public static final List<ExchangeRecipe> RECIPES = List.of(
      new ExchangeRecipe(
              "Enchanted Coal",
              List.of("<dark_gray>Requires:", "<white>x128 Coal"),
              "ENCHANTED_COAL",
              "COAL",
              128,
              "ENCHANTED_COAL",
              1
//              (player) -> {
//                  compactItem(player, 128, "COAL", "ENCHANTED_COAL");
//              }
      )
    );
}
