package dawn.nitsugua.dawnSurvivalRPG.GUI.GUIRegistries;

import dawn.nitsugua.dawnSurvivalRPG.GUI.Utils.ExchangeRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class CompactorRegistry {
    public static final List<ExchangeRecipe> RECIPES = List.of(
      new ExchangeRecipe(
              "test",
              List.of("<dark_gray>Requires:", "<white>x128 Coal"),
              "ENCHANTED_COAL",
              "COAL",
              1,
              (player) -> {
                  compactItem(player, 128, "COAL", "ENCHANTED_COAL");
              }
      )
    );

    private static void compactItem(Player player, int cost, String input, String result) {

    }
}
