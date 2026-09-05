package dawn.nitsugua.dawnSurvivalRPG.GUI.GUIRegistries;

import dawn.nitsugua.dawnSurvivalRPG.GUI.Utils.ExchangeRecipe;
import org.bukkit.Material;

import java.util.List;

public class CompactorRegistry {
    public static final List<ExchangeRecipe> RECIPES = List.of(
      new ExchangeRecipe(
              "test",
              List.of("this", "is", "a", "test"),
              Material.DIAMOND,
              "DIAMOND_BLOCK",
              1,
              (player) -> {
                  player.sendMessage("This is a test");
              }
      )
    );
}
